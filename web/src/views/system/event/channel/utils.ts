import type {
  ChannelType, EnumItem, TabConfig, ChannelData, ChannelFormData, ChannelUpdateData,
  ChannelTestConfig, ValidationRule, ChannelConfig, ChannelTypeConfigs
} from './types';
import { regexpUtils } from '@xcan-angus/infra';

/**
 * Convert enum items to tab configurations
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
 */
export const getChannelConfig = (channelType: ChannelType): ChannelConfig => {
  const configs = createChannelTypeConfigs();
  return configs[channelType] || configs.WEBHOOK;
};

/**
 * Create initial channel data for new channels
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
 */
export const convertAddressToString = (address: string | string[]): string => {
  if (typeof address === 'string') {
    return address;
  }
  return Array.isArray(address) ? address.join(',') : '';
};

/**
 * Create channel form data for API requests
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
 */
export const isValidChannelData = (item: ChannelData): boolean => {
  return !!(item.name && item.name.trim() && item.address);
};

/**
 * Check if channel is in edit mode
 */
export const isChannelInEditMode = (item: ChannelData): boolean => {
  return item.isEdit === true;
};

/**
 * Check if channel is new (no ID)
 */
export const isNewChannel = (item: ChannelData): boolean => {
  return !item.id || item.id.trim() === '';
};

/**
 * Check if channel can be edited
 */
export const canEditChannel = (item: ChannelData): boolean => {
  return !isNewChannel(item) && !isChannelInEditMode(item);
};

/**
 * Check if channel can be deleted
 */
export const canDeleteChannel = (item: ChannelData): boolean => {
  return !isNewChannel(item);
};

/**
 * Check if channel can be tested
 */
export const canTestChannel = (item: ChannelData): boolean => {
  return isValidChannelData(item);
};
