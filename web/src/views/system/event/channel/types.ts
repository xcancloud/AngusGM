/**
 * Channel type enumeration for different notification channels
 */
export type ChannelType = 'WEBHOOK' | 'EMAIL' | 'DINGTALK' | 'WECHAT';

/**
 * Enum item interface for channel type selection
 */
export interface EnumItem {
  /** Enum value */
  value: string;
  /** Display message */
  message: string;
}

/**
 * Tab configuration interface for channel type tabs
 */
export interface TabConfig {
  /** Tab key */
  key: string;
  /** Tab display name */
  tab: string;
}

/**
 * Component state interface for main channel component
 */
export interface ChannelState {
  /** Active channel type key */
  activeKey: string;
  /** List of enum items for tabs */
  pKeyEnumList: TabConfig[];
}

/**
 * Component props interface for channel address component
 */
export interface ChannelAddressProps {
  /** Maximum number of channels allowed */
  max: number;
  /** Type of channel */
  channelType: ChannelType;
  /** Placeholder text for input fields */
  placeholder: string;
}

/**
 * Component props interface for channel type components
 */
export interface ChannelTypeProps {
  /** Maximum number of channels allowed */
  max: number;
}

/**
 * Channel data interface for individual channel configuration
 */
export interface ChannelData {
  /** Unique identifier for the channel */
  id: string;
  /** Channel name */
  name: string;
  /** Channel address (string for webhook, array for email) */
  address: string | string[];
  /** Channel type */
  channelType: ChannelType;
  /** Whether the channel is in edit mode */
  isEdit?: boolean;
  /** Original name before editing */
  oldName?: string;
  /** Original address before editing */
  oldAddress?: string | string[] | undefined;
}

/**
 * Channel form data interface for API requests
 */
export interface ChannelFormData {
  /** Channel name */
  name: string;
  /** Channel type */
  channelType: ChannelType;
  /** Channel address */
  address: string;
}

/**
 * Channel update data interface for API requests
 */
export interface ChannelUpdateData {
  /** Channel ID */
  id: string;
  /** Channel name */
  name: string;
  /** Channel address */
  address: string;
}

/**
 * Channel test configuration interface for API requests
 */
export interface ChannelTestConfig {
  /** Channel name */
  name: string;
  /** Channel address */
  address: string;
  /** Channel type */
  channelType: ChannelType;
}

/**
 * Validation rule interface for form validation
 */
export interface ValidationRule {
  /** Whether the field is required */
  required: boolean;
  /** Custom validator function */
  validator: (rule: any, value: any) => Promise<void>;
}

/**
 * Form reference interface for form validation
 */
export interface FormRef {
  /** Validate form function */
  validate: () => Promise<void>;
  /** Clear validation function */
  clearValidate: () => void;
}

/**
 * Channel configuration interface for different channel types
 */
export interface ChannelConfig {
  /** Channel type */
  type: ChannelType;
  /** Maximum allowed channels */
  maxChannels: number;
  /** Placeholder text */
  placeholder: string;
  /** Title text */
  title: string;
  /** Validation rules */
  validationRules: ValidationRule[];
}

/**
 * Channel type configuration mapping
 */
export interface ChannelTypeConfigs {
  [key: string]: ChannelConfig;
}

/**
 * Form item interface for channel configuration
 */
export interface FormItem {
  /** Form item label */
  label: string;
  /** Form item name */
  name: string;
  /** Form item rules */
  rules: ValidationRule[];
  /** Form item colon */
  colon: boolean;
  /** Form item class */
  class: string;
}
