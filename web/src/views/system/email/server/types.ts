/**
 * Email protocol types supported by the system
 */
export type Protocol = 'SMTP' | 'IMAP' | 'POP3';

/**
 * Authentication account configuration
 */
export interface AuthAccount {
  /** Email account username */
  account: string;
  /** Email account password */
  password: string;
}

/**
 * Mailbox service configuration interface
 */
export interface MailboxService {
  /** Unique identifier for the mailbox service */
  id: string;
  /** Display name for the mailbox service */
  name: string;
  /** Email protocol type */
  protocol: Protocol;
  /** Additional remarks or description */
  remark: string;
  /** Whether this service is enabled as default */
  enabled: boolean;
  /** SMTP/IMAP server hostname */
  host: string;
  /** Server port number */
  port: string | number;
  /** Whether STARTTLS is enabled */
  startTlsEnabled: boolean;
  /** Whether SSL/TLS encryption is enabled */
  sslEnabled: boolean;
  /** Whether authentication is required */
  authEnabled: boolean;
  /** Authentication credentials */
  authAccount: AuthAccount;
  /** Subject prefix for outgoing emails */
  subjectPrefix: string;
}

/**
 * Form state interface for editing mailbox services
 * Extends MailboxService with optional id for create/update operations
 */
export interface FormState extends Omit<MailboxService, 'id'> {
  /** Optional identifier for existing services */
  id?: string;
}

/**
 * Table column interface for mailbox services table
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
  /** Column alignment */
  align?: 'left' | 'center' | 'right';
  /** Custom cell renderer */
  customCell?: () => { style: string };
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
 * Component state interface for main email server component
 */
export interface EmailServerState {
  /** List of mailbox services */
  mailboxServiceList: MailboxService[];
  /** Loading state */
  loading: boolean;
  /** Total count */
  total: number;
  /** Pagination parameters */
  params: PaginationParams;
  /** Modal visibility */
  visible: boolean;
  /** Test server address */
  testAddress: string;
  /** Test server ID */
  testServerId: string;
}

/**
 * Component props interface for test component
 */
export interface TestProps {
  /** Whether the modal is visible */
  visible: boolean;
  /** Server identifier to test */
  id: string;
  /** Server address for display purposes */
  address: string;
}

/**
 * Component state interface for test component
 */
export interface TestState {
  /** Loading state */
  loading: boolean;
  /** Email address input */
  emailAddress: string;
  /** Input validation state */
  inputRule: boolean;
}

/**
 * Component state interface for edit component
 */
export interface EditState {
  /** Loading state */
  loading: boolean;
  /** Password visibility toggle */
  passType: boolean;
  /** Form state */
  formState: FormState;
  /** Original form state for comparison */
  oldFormState: FormState | undefined;
}

/**
 * Protocol option interface for select dropdown
 */
export interface ProtocolOption {
  /** Display label */
  label: string;
  /** Protocol value */
  value: Protocol;
}

/**
 * Form validation rules interface
 */
export interface FormRules {
  /** Name field validation */
  name: { required: boolean; message: string };
  /** Host field validation */
  host: { required: boolean; message: string };
  /** Port field validation */
  port: { required: boolean; message: string };
  /** Auth account validation */
  authAccount: { required: boolean; message: string };
  /** Auth password validation */
  authPassword: { required: boolean; message: string };
}

/**
 * Add server request interface
 */
export interface AddServerRequest {
  /** Display name for the mailbox service */
  name: string;
  /** Email protocol type */
  protocol: Protocol;
  /** Additional remarks or description */
  remark: string;
  /** Whether this service is enabled as default */
  enabled: boolean;
  /** SMTP/IMAP server hostname */
  host: string;
  /** Server port number */
  port: string | number;
  /** Whether STARTTLS is enabled */
  startTlsEnabled: boolean;
  /** Whether SSL/TLS encryption is enabled */
  sslEnabled: boolean;
  /** Whether authentication is required */
  authEnabled: boolean;
  /** Authentication credentials (optional) */
  authAccount?: AuthAccount;
  /** Subject prefix for outgoing emails */
  subjectPrefix: string;
}

/**
 * Replace server request interface
 */
export interface ReplaceServerRequest extends AddServerRequest {
  /** Server ID */
  id: string;
}

/**
 * Update server request interface
 */
export interface UpdateServerRequest {
  /** Server ID */
  id: string;
  /** Whether server is enabled */
  enabled: boolean;
}

/**
 * Delete server request interface
 */
export interface DeleteServerRequest {
  /** Array of server IDs to delete */
  ids: string[];
}

/**
 * Test server config request interface
 */
export interface TestServerConfigRequest {
  /** Server ID to test */
  serverId: string;
  /** Array of recipient email addresses */
  toAddress: string[];
}
