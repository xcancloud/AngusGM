/**
 * SMS sending states enumeration
 */
export const enum SendState {
  SENDSUCCESS = 'sentSuccessfully', // SMS sent successfully
  SENDFAILED = 'sendFailed', // SMS sending failed
  SENDING = 'sending', // SMS is being sent
}

/**
 * SMS channel configuration interface
 */
export interface Aisle {
  accessKeyId: string, // Access key ID for SMS service
  accessKeySecret: string, // Access key secret for SMS service
  endpoint: string, // SMS service endpoint URL
  thirdChannelNo?: string, // Optional third-party channel number
  enabled: boolean, // Whether the channel is enabled
  id: string, // Unique channel identifier
  logo: string, // Channel logo URL
  name: string, // Channel display name
  loading: boolean, // Loading state for operations
  display: boolean, // Configuration form display state
  visible: boolean // SMS sending dialog visibility
}

/**
 * Component props interface for SMS sending component
 */
export interface SmsSendProps {
  aisle: Aisle;
}

/**
 * Phone number input data interface
 */
export interface PhoneNumberData {
  areaCode: string;
  number: string;
}

/**
 * SMS test parameters interface
 */
export interface SmsTestParams {
  channelId: string;
  mobiles: string[];
}

/**
 * Channel configuration update parameters interface
 */
export interface ChannelUpdateParams {
  accessKeyId: string;
  accessKeySecret: string;
  endpoint: string;
  thirdChannelNo: string;
  id: string;
}

/**
 * Channel state toggle parameters interface
 */
export interface ChannelToggleParams {
  id: string;
  enabled: boolean;
}

/**
 * Form validation rules interface
 */
export interface ValidationRules {
  accessKeyId: Array<{
    required: boolean;
    message: string;
    trigger: string;
  }>;
  accessKeySecret: Array<{
    required: boolean;
    message: string;
    trigger: string;
  }>;
  endpoint: Array<{
    required: boolean;
    message: string;
    trigger: string;
  }>;
}

/**
 * Component state interface for main channel component
 */
export interface ChannelState {
  aisles: Aisle[];
}

/**
 * Form data interface for channel configuration
 */
export interface ChannelFormData {
  accessKeyId: string;
  accessKeySecret: string;
  thirdChannelNo: string;
  endpoint: string;
  enabled: boolean;
  id: string;
  logo: string;
  name: string;
  loading: boolean;
  display: boolean;
  visible: boolean;
}

/**
 * SMS sending component state interface
 */
export interface SmsSendState {
  sendState: string;
  sendLoading: boolean;
  phoneNumber: PhoneNumberData;
}
