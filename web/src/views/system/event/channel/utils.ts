import type {
  ChannelType, EnumItem, TabConfig, ChannelData, ChannelFormData, ChannelUpdateData,
  ChannelTestConfig, ValidationRule, ChannelConfig, ChannelTypeConfigs
} from './types';
import { regexpUtils } from '@xcan-angus/infra';

/**
 * Convert enum items to tab configurations
 * @param enumItems - Array of enum items
 * @returns Array of tab configurations
 */
export const convertEnumToTabs = (enumItems: EnumItem[]): TabConfig[] => {
  if (!Array.isArray(enumItems)) return [];

  return enumItems.map(item => ({
    key: item.value,
    tab: item.message
  }));
};

/**
 * Create channel type configurations for different channel types
 * @returns Channel type configuration mapping
 */
export const createChannelTypeConfigs = (): ChannelTypeConfigs => ({
  WEBHOOK: {
    type: 'WEBHOOK',
    maxChannels: 5,
    placeholder: 'event.channel.placeholder.inputWebhookAddress',
    title: 'event.channel.titles.webhookTitle',
    validationRules: []
  },
  EMAIL: {
    type: 'EMAIL',
    maxChannels: 20,
    placeholder: 'event.channel.placeholder.inputEmailAddress',
    title: 'event.channel.titles.emailTitle',
    validationRules: []
  },
  DINGTALK: {
    type: 'DINGTALK',
    maxChannels: 10,
    placeholder: 'event.channel.placeholder.inputDingTalkKeyword',
    title: 'event.channel.titles.dingTalkTitle',
    validationRules: []
  },
  WECHAT: {
    type: 'WECHAT',
    maxChannels: 10,
    placeholder: 'event.channel.placeholder.inputWeChatAddress',
    title: 'event.channel.titles.weChatTitle',
    validationRules: []
  }
});

/**
 * Get channel configuration by type
 * @param channelType - Channel type to get configuration for
 * @returns Channel configuration object
 */
export const getChannelConfig = (channelType: ChannelType): ChannelConfig => {
  const configs = createChannelTypeConfigs();
  return configs[channelType] || configs.WEBHOOK;
};

/**
 * Create initial channel data for new channels
 * @param channelType - Type of channel to create
 * @returns Initial channel data object
 */
export const createInitialChannelData = (channelType: ChannelType): ChannelData => ({
  id: '',
  address: channelType === 'EMAIL' ? [] : '',
  name: '',
  channelType,
  isEdit: true
});

/**
 * Process channel data from API response
 * @param data - Raw channel data from API
 * @param channelType - Type of channel
 * @returns Processed channel data array
 */
export const processChannelData = (data: any[], channelType: ChannelType): ChannelData[] => {
  if (!Array.isArray(data)) return [];

  return data.map(item => ({
    ...item,
    isEdit: false,
    address: channelType === 'EMAIL' ? item.address.split(',') : item.address
  }));
};

/**
 * Add new channel data to existing list
 * @param dataSource - Existing channel data array
 * @param channelType - Type of channel
 * @param maxChannels - Maximum allowed channels
 */
export const addNewChannelData = (
  dataSource: ChannelData[],
  channelType: ChannelType,
  maxChannels: number
): void => {
  if (dataSource.length < maxChannels) {
    dataSource.push(createInitialChannelData(channelType));
  }
};

/**
 * Validate email format for multiple email addresses
 * @param value - Email addresses string (comma-separated)
 * @param t - i18n translation function
 * @returns Promise that resolves if valid, rejects with error message if invalid
 */
export const validateEmail = (value: string, t: (key: string) => string): Promise<void> => {
  if (!value.trim()) {
    return Promise.reject(new Error(t('event.channel.messages.addressRequired')));
  }

  if (value) {
    const values = value.split(',');
    if (values.every(email => regexpUtils.isEmail(email))) {
      return Promise.resolve();
    } else {
      return Promise.reject(new Error(t('event.channel.messages.emailFormatError')));
    }
  }

  return Promise.reject(new Error(t('event.channel.messages.emailFormatError')));
};

/**
 * Validate webhook URL format
 * @param value - URL string to validate
 * @param t - i18n translation function
 * @returns Promise that resolves if valid, rejects with error message if invalid
 */
export const validateAddress = (value: string, t: (key: string) => string): Promise<void> => {
  if (!value.trim()) {
    return Promise.reject(new Error(t('event.channel.messages.addressRequired')));
  }

  if (value && regexpUtils.isUrl(value)) {
    return Promise.resolve();
  }

  return Promise.reject(new Error(t('event.channel.messages.webhookFormatError')));
};

/**
 * Create validation rules for address field
 * @param channelType - Type of channel
 * @param t - i18n translation function
 * @returns Array of validation rules
 */
export const createAddressValidationRules = (
  channelType: ChannelType,
  t: (key: string) => string
): ValidationRule[] => [
  {
    required: true,
    validator: channelType === 'EMAIL'
      ? (_rule: any, value: any) => validateEmail(value, t)
      : (_rule: any, value: any) => validateAddress(value, t)
  }
];

/**
 * Convert address to string format for API requests
 * @param address - Address value (string or array)
 * @returns String representation of address
 */
export const convertAddressToString = (address: string | string[]): string => {
  if (typeof address === 'string') {
    return address;
  }
  return Array.isArray(address) ? address.join(',') : '';
};

/**
 * Create channel form data for API requests
 * @param name - Channel name
 * @param channelType - Channel type
 * @param address - Channel address
 * @returns Channel form data object
 */
export const createChannelFormData = (
  name: string,
  channelType: ChannelType,
  address: string | string[]
): ChannelFormData => ({
  name,
  channelType,
  address: convertAddressToString(address)
});

/**
 * Create channel update data for API requests
 * @param id - Channel ID
 * @param name - Channel name
 * @param address - Channel address
 * @returns Channel update data object
 */
export const createChannelUpdateData = (
  id: string,
  name: string,
  address: string | string[]
): ChannelUpdateData => ({
  id,
  name,
  address: convertAddressToString(address)
});

/**
 * Create channel test configuration for API requests
 * @param name - Channel name
 * @param address - Channel address
 * @param channelType - Channel type
 * @returns Channel test configuration object
 */
export const createChannelTestConfig = (
  name: string,
  address: string | string[],
  channelType: ChannelType
): ChannelTestConfig => ({
  name,
  address: convertAddressToString(address),
  channelType
});

/**
 * Check if channel data is valid for saving
 * @param item - Channel data item to validate
 * @returns True if valid, false otherwise
 */
export const isValidChannelData = (item: ChannelData): boolean => {
  return !!(item.name && item.name.trim() && item.address);
};

/**
 * Check if channel is in edit mode
 * @param item - Channel data item to check
 * @returns True if in edit mode, false otherwise
 */
export const isChannelInEditMode = (item: ChannelData): boolean => {
  return item.isEdit === true;
};

/**
 * Check if channel is new (no ID)
 * @param item - Channel data item to check
 * @returns True if new, false otherwise
 */
export const isNewChannel = (item: ChannelData): boolean => {
  return !item.id || item.id.trim() === '';
};

/**
 * Check if channel can be edited
 * @param item - Channel data item to check
 * @returns True if can be edited, false otherwise
 */
export const canEditChannel = (item: ChannelData): boolean => {
  return !isNewChannel(item) && !isChannelInEditMode(item);
};

/**
 * Check if channel can be deleted
 * @param item - Channel data item to check
 * @returns True if can be deleted, false otherwise
 */
export const canDeleteChannel = (item: ChannelData): boolean => {
  return !isNewChannel(item);
};

/**
 * Check if channel can be tested
 * @param item - Channel data item to check
 * @returns True if can be tested, false otherwise
 */
export const canTestChannel = (item: ChannelData): boolean => {
  return isValidChannelData(item);
};
