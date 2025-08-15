/**
 * Email protocol types
 */
export type Protocol = 'SMTP' | 'IMAP' | 'POP3'

/**
 * SMS record interface for tracking SMS sending history
 */
export interface SmsRecord {
  id: string; // Unique SMS record identifier
  templateCode: string; // Associated SMS template code
  language: { // Language information
    value: string; // Language code
    message: string; // Localized language name
  },
  bizKey: string; // Business key for tracking
  outId: string; // External system ID
  thirdOutputParam: any; // Third-party service output parameters
  inputParam: any; // Input parameters for SMS
  verificationCode: boolean; // Whether SMS contains verification code
  batch: boolean; // Whether SMS is part of batch sending
  sendTenantId: string; // Tenant ID of sender
  sendUserId: string; // User ID of sender
  urgent: boolean; // Whether SMS is marked as urgent
  sendStatus: boolean; // SMS sending status
  actualSendDate: boolean; // Actual sending date
  expectedSendDate: boolean; // Expected sending date
  failureReason?: string; // Reason for failure if any
  sendId?: string; // Alternative sender ID field
}

/**
 * SMS send status values
 */
export type SmsSendStatus = 'SUCCESS' | 'PENDING' | 'FAILURE';

/**
 * Table column configuration interface
 */
export interface TableColumn {
  title: string;
  dataIndex: string;
  key: string;
  width?: string;
  sorter?: boolean;
  customCell?: () => { style: string };
  customRender?: (params: { text: any }) => string;
}

/**
 * Grid column configuration interface
 */
export interface GridColumn {
  label: string;
  dataIndex: string;
  customRender?: (params: { text: any }) => string;
}

/**
 * Search option configuration interface
 */
export interface SearchOption {
  valueKey: string;
  type: 'select-user' | 'select-enum' | 'input' | 'select' | 'date-range';
  allowClear?: boolean;
  placeholder?: any;
  enumKey?: any;
  options?: Array<{ value: any; label: string }>;
  axiosConfig?: any;
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
 * Component state interface for main records component
 */
export interface RecordsState {
  smsList: SmsRecord[];
  showCount: boolean;
  loading: boolean;
  params: {
    pageNo: number;
    pageSize: number;
    filters: any[];
    orderBy?: string;
    orderSort?: string;
  };
  total: number;
}

/**
 * Component state interface for detail component
 */
export interface DetailState {
  firstLoad: boolean;
  recordInfo: SmsRecord | undefined;
}

/**
 * Search criteria interface
 */
export interface SearchCriteria {
  key: string;
  value: string;
  op: string;
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
    orderBy?: string;
    orderSort?: string;
  };
}
