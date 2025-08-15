/**
 * Utility functions for SMS channel module
 * Contains helper functions extracted from channel components
 */

import type { ValidationRules, ChannelUpdateParams, ChannelToggleParams, SmsTestParams } from './types';
import { SendState } from './types';

/**
 * Get logo border styling based on logo availability
 * @param value - Logo URL or empty string
 * @returns CSS class for border styling
 */
export const getLogoBorder = (value: string): string => {
  return value ? '' : 'border border-gray-border border-dashed';
};

/**
 * Get logo source, fallback to placeholder if not available
 * @param value - Logo URL
 * @param placeholderLogo - Placeholder logo URL
 * @returns Logo URL or placeholder image
 */
export const getLogo = (value: string, placeholderLogo: string): string => {
  return value || placeholderLogo;
};

/**
 * Get channel status text
 * @param value - Enabled state
 * @param t - i18n translation function
 * @returns Localized status text
 */
export const getChannelStatus = (value: boolean, t: (key: string) => string): string => {
  return value ? t('sms.status.enabled') : t('sms.status.disabled');
};

/**
 * Get icon name based on send state
 * @param sendState - Current send state
 * @returns Icon name for display
 */
export const getSendStateIcon = (sendState: string): string => {
  switch (sendState) {
    case SendState.SENDSUCCESS:
      return 'icon-right';
    case SendState.SENDFAILED:
      return 'icon-cuowu';
    default:
      return '';
  }
};

/**
 * Get icon color class based on send state
 * @param sendState - Current send state
 * @returns CSS class for icon color
 */
export const getSendStateIconColor = (sendState: string): string => {
  switch (sendState) {
    case SendState.SENDSUCCESS:
      return 'text-success';
    case SendState.SENDFAILED:
      return 'text-danger';
    default:
      return '';
  }
};

/**
 * Create form validation rules
 * @param t - i18n translation function
 * @returns Validation rules object
 */
export const createValidationRules = (t: (key: string) => string): ValidationRules => ({
  accessKeyId: [
    {
      required: true,
      message: t('sms.validation.accessKeyIdRequired'),
      trigger: 'change'
    }
  ],
  accessKeySecret: [
    {
      required: true,
      message: t('sms.validation.accessKeySecretRequired'),
      trigger: 'change'
    }
  ],
  endpoint: [
    {
      required: true,
      message: t('sms.validation.endpointRequired'),
      trigger: 'change'
    }
  ]
});

/**
 * Set channel state with default values
 * @param data - Raw channel data from API
 * @returns Processed channel data with default values
 */
export const processChannelData = (data: any[]): any[] => {
  return data.map(item => ({
    ...item,
    accessKeyId: item.accessKeyId || '',
    accessKeySecret: item.accessKeySecret || '',
    endpoint: item.endpoint || '',
    loading: false,
    display: false,
    visible: false
  }));
};

/**
 * Create channel configuration update parameters
 * @param formData - Form data object
 * @param channelId - Channel ID to update
 * @returns Update parameters object
 */
export const createChannelUpdateParams = (
  formData: { accessKeyId: string; accessKeySecret: string; endpoint: string; thirdChannelNo: string },
  channelId: string
): ChannelUpdateParams => ({
  accessKeyId: formData.accessKeyId?.trim() || '',
  accessKeySecret: formData.accessKeySecret?.trim() || '',
  endpoint: formData.endpoint?.trim() || '',
  thirdChannelNo: formData.thirdChannelNo?.trim() || '',
  id: channelId
});

/**
 * Create channel toggle parameters
 * @param channelId - Channel ID to toggle
 * @param currentEnabled - Current enabled state
 * @returns Toggle parameters object
 */
export const createChannelToggleParams = (channelId: string, currentEnabled: boolean): ChannelToggleParams => ({
  id: channelId,
  enabled: !currentEnabled
});

/**
 * Create SMS test parameters
 * @param channelId - Channel ID to test
 * @param phoneNumber - Phone number to send test SMS to
 * @returns SMS test parameters object
 */
export const createSmsTestParams = (channelId: string, phoneNumber: string): SmsTestParams => ({
  channelId,
  mobiles: [phoneNumber]
});

/**
 * Validate phone number format
 * @param phoneNumber - Phone number to validate
 * @returns True if valid, false otherwise
 */
export const isValidPhoneNumber = (phoneNumber: string): boolean => {
  if (!phoneNumber) return false;

  // Basic phone number validation - can be enhanced based on requirements
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(phoneNumber);
};

/**
 * Format phone number for display
 * @param phoneNumber - Phone number to format
 * @param areaCode - Area code
 * @returns Formatted phone number string
 */
export const formatPhoneNumber = (phoneNumber: string, areaCode = 'CN'): string => {
  if (!phoneNumber) return '';

  return `${areaCode} ${phoneNumber}`;
};

/**
 * Get popup container for tooltips
 * @param triggerNode - DOM element that triggered the tooltip
 * @returns Parent node or document body as fallback
 */
export const getPopupContainer = (triggerNode: HTMLElement): HTMLElement => {
  return (triggerNode?.parentNode as HTMLElement) || document.body;
};

/**
 * Check if channel configuration is valid
 * @param formData - Form data to validate
 * @returns True if valid, false otherwise
 */
export const isChannelConfigValid = (formData: {
  accessKeyId: string;
  accessKeySecret: string;
  endpoint: string;
}): boolean => {
  return !!(formData.accessKeyId?.trim() &&
           formData.accessKeySecret?.trim() &&
           formData.endpoint?.trim());
};

/**
 * Reset channel form data to initial state
 * @param formData - Form data object to reset
 */
export const resetChannelForm = (formData: {
  accessKeyId: string;
  accessKeySecret: string;
  endpoint: string;
  thirdChannelNo: string;
}): void => {
  formData.accessKeyId = '';
  formData.accessKeySecret = '';
  formData.endpoint = '';
  formData.thirdChannelNo = '';
};

/**
 * Close all channel configuration forms
 * @param channels - Array of channel items
 */
export const closeAllChannelConfigs = (channels: any[]): void => {
  channels.forEach(item => {
    item.display = false;
  });
};

/**
 * Populate form with channel values
 * @param formData - Form data object to populate
 * @param channel - Channel item with values
 */
export const populateChannelForm = (
  formData: {
    accessKeyId: string;
    accessKeySecret: string;
    endpoint: string;
    thirdChannelNo: string;
  },
  channel: any
): void => {
  formData.accessKeyId = channel.accessKeyId;
  formData.accessKeySecret = channel.accessKeySecret;
  formData.endpoint = channel.endpoint;
  formData.thirdChannelNo = channel.thirdChannelNo || '';
};
