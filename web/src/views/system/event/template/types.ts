import { PageQuery } from '@xcan-angus/infra';

/**
 * Save setting interface for event configuration
 */
export interface SaveSetting {
  /** Policy key with value and message */
  pkey: { value: string; message: string };
  /** List of receiver IDs */
  receiveIds: string[];
}

/**
 * Event configuration list interface for individual event template
 */
export interface EventConfigList {
  /** Unique identifier */
  id: string;
  /** Event key */
  eKey: string;
  /** Business key */
  bizKey: string;
  /** Big business key */
  bigBizKey: string;
  /** Event code */
  eventCode: string;
  /** Event name */
  eventName: string;
  /** Event type */
  eventType: undefined | string;
  /** Allowed channel types */
  allowedChannelTypes: string[] | { value: string; message: string }[];
  /** Push settings */
  pushSetting: SaveSetting[];
  /** Target type */
  targetType?: string;
  /** App code */
  appCode?: string;
}

/**
 * Search option interface for search panel
 */
export interface SearchOption {
  /** Placeholder text */
  placeholder: string;
  /** Value key for the search field */
  valueKey: string;
  /** Input type */
  type: 'input' | 'select-enum' | 'select';
  /** Operation type */
  op?: 'EQUAL';
  /** Whether to allow clearing */
  allowClear?: boolean;
  /** Whether to show search */
  showSearch?: boolean;
  /** Enum key for select-enum type */
  enumKey?: any;
  /** Options for select type */
  options?: any[];
  /** Field names for select type */
  fieldNames?: {
    value: string;
    label: string;
  };
}

/**
 * Table column interface for event template table
 */
export interface TableColumn {
  /** Column title */
  title: string;
  /** Data index */
  dataIndex: string;
  /** Column key */
  key: string;
  /** Column width */
  width?: string | number;
  /** Custom render function */
  customRender?: ({ text }: { text: any }) => string;
}

/**
 * App item interface
 */
export interface AppItem {
  /** App code */
  appCode: string;
  /** App name */
  appName: string;
  /** App display name */
  appShowName: string;
}

/**
 * Component state interface for main event template component
 */
export interface EventTemplateState {
  /** Whether modal is visible */
  visible: boolean;
  /** Selected item for configuration */
  selectedItem: EventConfigList;
  /** List of event configurations */
  eventConfigList: EventConfigList[];
  /** Pagination parameters */
  params: PageQuery;
  /** Total count */
  total: number;
  /** Loading state */
  loading: boolean;
  /** Disabled state */
  disabled: boolean;
  /** Target type enums */
  targetTypeEnums: any[];
  /** App list */
  appList: AppItem[];
  /** Whether app list is loaded */
  appLoaded: boolean;
}

/**
 * Component props interface for receive config component
 */
export interface ReceiveConfigProps {
  /** Whether modal is visible */
  visible: boolean;
  /** Event template ID */
  id: string;
}

/**
 * Channel option interface for receive config
 */
export interface ChannelOption {
  /** Channel address */
  address: string;
  /** Channel ID */
  id: string;
  /** Channel name */
  name: string;
  /** Policy key */
  pkey: { value: string; message: string };
  /** Channel secret */
  secret: string;
}

/**
 * Channel values interface for different channel types
 */
export interface ChannelValues {
  /** Webhook channels */
  WEBHOOK: string[];
  /** Email channels */
  EMAIL: string[];
  /** DingTalk channels */
  DINGTALK: string[];
  /** WeChat channels */
  WECHAT: string[];
}

/**
 * Component state interface for receive config component
 */
export interface ReceiveConfigState {
  /** Selected channel types */
  selectedType: string[];
  /** Event types */
  eventTypes: any[];
  /** Channel values for each type */
  channelValues: ChannelValues;
  /** Webhook options */
  webOptions: ChannelOption[];
  /** Email options */
  emailOptions: ChannelOption[];
  /** DingTalk options */
  dingtalkOptions: ChannelOption[];
  /** WeChat options */
  wechatOptions: ChannelOption[];
}
