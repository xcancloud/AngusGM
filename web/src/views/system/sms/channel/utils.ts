/**
 * Utility functions for SMS channel module
 * Contains helper functions extracted from channel components
 */

import type { ValidationRules, ChannelUpdateParams, ChannelToggleParams, SmsTestParams } from './types';
import { SendState } from './types';

/**
 * Get logo border styling based on logo availability
 */
export const getLogoBorder = (value: string): string => {
  return value ? '' : 'border border-gray-border border-dashed';
};

/**
 * Get logo source, fallback to placeholder if not available
 */
export const getLogo = (value: string, placeholderLogo: string): string => {
  return value || placeholderLogo;
};

/**
 * Get channel status text
 */
export const getChannelStatus = (value: boolean, t: (key: string) => string): string => {
  return value ? t('sms.status.enabled') : t('sms.status.disabled');
};

/**
 * Get icon name based on send state
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
 */
export const createChannelToggleParams = (channelId: string, currentEnabled: boolean): ChannelToggleParams => ({
  id: channelId,
  enabled: !currentEnabled
});

/**
 * Create SMS test parameters
 */
export const createSmsTestParams = (channelId: string, phoneNumber: string): SmsTestParams => ({
  channelId,
  mobiles: [phoneNumber]
});

/**
 * Validate phone number format
 */
export const isValidPhoneNumber = (phoneNumber: string): boolean => {
  if (!phoneNumber) return false;

  // Basic phone number validation - can be enhanced based on requirements
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(phoneNumber);
};

/**
 * Format phone number for display
 */
export const formatPhoneNumber = (phoneNumber: string, areaCode = 'CN'): string => {
  if (!phoneNumber) return '';

  return `${areaCode} ${phoneNumber}`;
};

/**
 * Get popup container for tooltips
 */
export const getPopupContainer = (triggerNode: HTMLElement): HTMLElement => {
  return (triggerNode?.parentNode as HTMLElement) || document.body;
};

/**
 * Check if channel configuration is valid
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
 */
export const closeAllChannelConfigs = (channels: any[]): void => {
  channels.forEach(item => {
    item.display = false;
  });
};

/**
 * Populate form with channel values
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
