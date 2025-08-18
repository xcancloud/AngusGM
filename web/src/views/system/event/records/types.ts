/**
 * Event record interface for individual event data
 */
export interface EventRecord {
  /** Event description */
  description: string;
  /** Event key */
  ekey: string;
  /** Error message */
  errMsg: string;
  /** Event code */
  eventCode: string;
  /** Event content */
  eventContent: string;
  /** Execution number */
  execNo: string;
  /** Unique identifier */
  id: string;
  /** Push status with value and message */
  pushStatus: { value: string; message: string };
  /** Tenant ID */
  tenantId: string;
  /** Tenant name */
  tenantName: string;
  /** Trigger time */
  triggerTime: string;
  /** Event type with value and message */
  type: { value: string; message: string };
  /** Event view URL */
  eventViewUrl?: string;
  /** Push message */
  pushMsg?: string;
  /** Full name of receiver */
  fullName?: string;
  /** User ID of receiver */
  userId?: string;
  /** Event name */
  name?: string;
  /** Event code */
  code?: string;
  /** Created date */
  createdDate?: string;
}

/**
 * Search option interface for search panel
 */
export interface SearchOption {
  /** Placeholder text */
  placeholder: string | string[];
  /** Value key for the search field */
  valueKey: string;
  /** Input type */
  type: 'input' | 'select-user' | 'select-enum' | 'date-range';
  /** Operation type */
  op?: 'MATCH';
  /** Whether to allow clearing */
  allowClear?: boolean;
  /** Whether to show search */
  showSearch?: boolean;
  /** Enum key for select-enum type */
  enumKey?: any;
}

/**
 * Table column interface for event records table
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
  /** Group name for grouping */
  groupName?: string;
  /** Whether to hide the column */
  hide?: boolean;
  /** Whether to show ellipsis */
  ellipsis?: boolean;
  /** Custom cell configuration */
  customCell?: () => { style: string };
  /** Sorter configuration */
  sorter?: {
    compare: (a: any, b: any) => number;
  };
  /** Column alignment */
  align?: 'center' | 'left' | 'right';
}

/**
 * Pagination parameters interface
 */
export interface PaginationParams {
  /** Current page number */
  pageNo: number;
  /** Page size */
  pageSize: number;
  /** Sort order */
  orderSort: string;
  /** Sort by field */
  sortBy: string;
  /** Whether to enable full text search */
  fullTextSearch: boolean;
  /** Search filters */
  filters: Record<string, string>[];
}

/**
 * Component state interface for main event records component
 */
export interface EventRecordsState {
  /** Whether to show count */
  showCount: boolean;
  /** List of event records */
  eventRecordList: EventRecord[];
  /** Pagination parameters */
  params: PaginationParams;
  /** Total count */
  total: number;
  /** Loading state */
  loading: boolean;
  /** Disabled state */
  disabled: boolean;
  /** Selected item for viewing */
  selectedItem: EventRecord;
  /** Whether modal is visible */
  visible: boolean;
}

/**
 * Check content configuration interface
 */
export interface CheckContentConfig {
  /** Whether dialog is visible */
  dialogVisible: boolean;
  /** Content to display */
  content: string;
}

/**
 * Status style mapping interface
 */
export interface StatusStyleMapping {
  [key: string]: string;
}

/**
 * Component props interface for receive config component
 */
export interface ReceiveConfigProps {
  /** Whether modal is visible */
  visible: boolean;
  /** Event code */
  eventCode: string;
  /** Event key */
  eKey: string;
}

/**
 * Component props interface for view component
 */
export interface ViewProps {
  /** Content value to display */
  value: string;
  /** Whether modal is visible */
  visible: boolean;
}

/**
 * Channel data interface for receive config
 */
export interface ChannelData {
  /** Channel type with value and message */
  channelType: { value: string; message: string };
  /** List of channels */
  channels: Array<{
    id: string;
    name: string;
    address: string;
  }>;
}

/**
 * Grid column interface for receive config
 */
export interface GridColumn {
  /** Data index */
  dataIndex: string;
  /** Column label */
  label: string;
}

/**
 * Component state interface for receive config component
 */
export interface ReceiveConfigState {
  /** Data source for grid */
  dataSource: Record<string, any>;
  /** Columns configuration */
  columns: GridColumn[][];
}
