/**
 * Operation log record interface for tracking user operations
 */
export interface OperationLogRecord {
  /** Unique identifier for the operation log record */
  id: string;
  /** Operator's full name */
  fullName: string;
  /** Operator's avatar URL */
  avatar?: string;
  /** Description of the operation performed */
  description: string;
  /** Resource information with message */
  resource: {
    message: string;
  };
  /** Name of the resource being operated on */
  resourceName: string;
  /** ID of the resource being operated on */
  resourceId: string;
  /** Operation date/time */
  optDate: string;
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  key: string;
  title: string;
  dataIndex: string;
  width?: string;
  hide?: boolean;
  sorter?: boolean;
  customRender?: (params: { text: any }) => string;
  groupName?: string;
  customCell?: () => { style: string };
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
 * Component state interface for main operation log component
 */
export interface OperationLogState {
  params: {
    pageNo: number;
    pageSize: number;
    filters: any[];
    fullTextSearch: boolean;
    orderBy?: string;
    orderSort?: any;
  };
  total: number;
  tableList: OperationLogRecord[];
  loading: boolean;
  showCount: boolean;
  clearBeforeDay?: string;
}

/**
 * Search criteria interface
 */
export interface SearchCriteria {
  filters: {
    key: string;
    op: string;
    value: string | string[];
  }[];
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
    orderSort: any;
  };
}
