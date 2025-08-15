/**
 * Online user interface for tracking user online status
 */
export interface OnlineUser {
  /** Unique identifier for the online user record */
  id: bigint;
  /** User ID */
  userId: string;
  /** User's full name */
  fullName: string;
  /** Device identifier */
  deviceId: string;
  /** User agent string */
  userAgent: string;
  /** Remote IP address */
  remoteAddress: string;
  /** Whether user is currently online */
  online: boolean;
  /** Online date/time */
  onlineDate: string;
  /** Offline date/time */
  offlineDate: string;
  /** Loading state for operations */
  loading: boolean;
  /** User avatar URL */
  avatar?: string;
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  title: string;
  dataIndex: string;
  key: string;
  width?: string;
  hide?: boolean;
  sorter?: boolean | {
    compare: (a: any, b: any) => number;
  };
  customRender?: (params: { text: any }) => string;
  groupName?: string;
  align?: 'left' | 'center' | 'right';
}

/**
 * Search option configuration interface
 */
export interface SearchOption {
  placeholder: string | string[];
  valueKey: string;
  type: 'input' | 'date-range';
  op?: 'EQUAL' | 'GREATER_THAN' | 'GREATER_THAN_EQUAL' | 'IN' | 'LESS_THAN' | 'LESS_THAN_EQUAL' | 'MATCH' | 'MATCH_END' | 'NOT_EQUAL' | 'NOT_IN';
  allowClear: boolean;
}

/**
 * Pagination configuration interface
 */
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * Component state interface for main online component
 */
export interface OnlineState {
  params: {
    pageNo: number;
    pageSize: number;
    filters: any[];
    orderBy: string;
    orderSort: any;
    fullTextSearch: boolean;
  };
  total: number;
  onlineList: OnlineUser[];
  loading: boolean;
  disabled: boolean;
}

/**
 * Logout parameters interface
 */
export interface LogoutParams {
  receiveObjectIds: string[];
  receiveObjectType: string;
  broadcast: boolean;
}

/**
 * Table change parameters interface
 */
export interface TableChangeParams {
  pagination: {
    current: number;
    pageSize: number;
  };
  filters: any;
  sorter: {
    orderBy: string;
    orderSort: string;
  };
}
