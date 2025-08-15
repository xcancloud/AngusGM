/**
 * Email record status enumeration
 */
export type EmailSendStatus = 'SUCCESS' | 'PENDING' | 'FAILURE';
/**
 * Email type enumeration
 */
export type EmailType = 'VERIFICATION' | 'NOTIFICATION' | 'MARKETING' | 'SYSTEM';

/**
 * Language configuration interface
 */
export interface LanguageConfig {
  /** Language value */
  value: string;
  /** Language display message */
  message: string;
}

/**
 * Email record interface representing a single email record
 */
export interface EmailRecord {
  /** Unique identifier for the email record */
  id: string;
  /** Template code used for email generation */
  templateCode: string;
  /** Language configuration for internationalization */
  language: LanguageConfig;
  /** Business key for tracking purposes */
  bizKey: string;
  /** External identifier */
  outId: string;
  /** Sender user identifier */
  sendId: string;
  /** Type of email (verification, notification, etc.) */
  emailType: EmailType;
  /** Email subject line */
  subject: string;
  /** Sender email address */
  fromAddr: string;
  /** Whether this email contains verification code */
  verificationCode: boolean;
  /** Validity period for verification code in seconds */
  verificationCodeValidSecond: string;
  /** Array of recipient email addresses */
  toAddress: string[];
  /** Carbon copy email addresses */
  ccAddress: string;
  /** Whether email content is HTML format */
  html: boolean;
  /** Current send status */
  sendStatus: EmailSendStatus;
  /** Reason for failure if send failed */
  failureReason: string;
  /** Email content body */
  content: string;
  /** Template parameters for dynamic content */
  templateParams: Record<string, any>;
  /** File attachments */
  attachments: string;
  /** Expected send date/time */
  expectedSendDate: string;
  /** Actual send date/time */
  actualSendDate: string;
  /** Whether email is marked as urgent */
  urgent: boolean;
  /** Tenant identifier for multi-tenancy */
  sendTenantId: string;
  /** Whether email is part of a batch send */
  batch: boolean;
}

/**
 * Table column interface for email records table
 */
export interface TableColumn {
  /** Column title */
  title: string;
  /** Data index */
  dataIndex: string;
  /** Column key */
  key: string;
  /** Column width */
  width?: string;
  /** Whether column is sortable */
  sorter?: boolean;
  /** Custom render function */
  customRender?: ({ text }: { text: any }) => string;
  /** Custom cell renderer */
  customCell?: () => { style: string };
}

/**
 * Grid column interface for detail view
 */
export interface GridColumn {
  /** Column label */
  label: string;
  /** Data index */
  dataIndex: string;
  /** Custom render function */
  customRender?: ({ text }: { text: any }) => string;
}

/**
 * Search option interface for search panel
 */
export interface SearchOption {
  /** Value key for the search field */
  valueKey: string;
  /** Input type */
  type: 'select-user' | 'select-enum' | 'input' | 'select' | 'date-range';
  /** Whether to allow clearing */
  allowClear?: boolean;
  /** Placeholder text */
  placeholder: string | string[];
  /** Enum key for select-enum type */
  enumKey?: any;
  /** Options for select type */
  options?: Array<{ value: any; label: string }>;
  /** Axios configuration for select-user type */
  axiosConfig?: { headers: Record<string, string> };
}

/**
 * Pagination parameters interface
 */
export interface PaginationParams {
  /** Current page number */
  pageNo: number;
  /** Page size */
  pageSize: number;
  /** Search filters */
  filters: any[];
  /** Order by field */
  orderBy?: string;
  /** Order sort direction */
  orderSort?: string;
}

/**
 * Pagination object interface for table component
 */
export interface PaginationObject {
  /** Current page */
  current: number;
  /** Page size */
  pageSize: number;
  /** Total count */
  total: number;
}

/**
 * Component state interface for main email records component
 */
export interface EmailRecordsState {
  /** List of email records */
  emailList: EmailRecord[];
  /** Whether to show count */
  showCount: boolean;
  /** Loading state */
  loading: boolean;
  /** Total count */
  total: number;
  /** Pagination parameters */
  params: PaginationParams;
}

/**
 * Component state interface for detail component
 */
export interface DetailState {
  /** Whether this is the first load */
  firstLoad: boolean;
  /** Email record information */
  emailRecordInfo: EmailRecord | undefined;
}

/**
 * Status color mapping interface
 */
export interface StatusColorMapping {
  /** Success status color */
  SUCCESS: string;
  /** Pending status color */
  PENDING: string;
  /** Failure status color */
  FAILURE: string;
}

/**
 * Search change parameters interface
 */
export interface SearchChangeParams {
  /** Search key */
  key: string;
  /** Search value */
  value: string;
  /** Search operation */
  op: any;
}
