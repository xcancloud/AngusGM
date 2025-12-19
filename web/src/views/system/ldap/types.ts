/**
 * Type definitions for LDAP module
 * Contains interfaces and types used across LDAP components
 */

/**
 * LDAP directory type enumeration
 */
export type DirectoryType = 'OpenLDAP' | 'ActiveDirectory' | 'Other';

/**
 * LDAP connection status
 */
export type ConnectionStatus = 'enabled' | 'disabled';

/**
 * Test operation result status
 */
export type TestResultStatus = 'success' | 'failure' | 'pending' | null;

/**
 * Form validation result type
 */
export type ValidationResult = 'success' | 'error';

/**
 * Component props interface for LDAP configuration components
 */
export interface LdapComponentProps {
  /** Component index in parent */
  index: number;
  /** Unique key identifier */
  keys: string;
  /** Query parameters for data population */
  query?: any;
}

/**
 * Server information configuration interface
 */
export interface ServerConfig {
  /** Directory type */
  directoryType: DirectoryType;
  /** Server host address */
  host: string;
  /** Server name/identifier */
  name: string;
  /** Connection password */
  password: string;
  /** Connection port */
  port: string | number;
  /** SSL connection flag */
  ssl: boolean;
  /** Connection username */
  username: string;
}

/**
 * LDAP base configuration interface
 */
export interface LdapBaseConfig {
  /** Base Distinguished Name for LDAP search */
  baseDn: string;
  /** Additional user DN for extended user search */
  additionalUserDn: string;
  /** Additional group DN for extended group search */
  additionalGroupDn: string;
}

/**
 * User schema configuration interface
 */
export interface UserSchemaConfig {
  /** LDAP attribute for user's first name */
  firstNameAttribute: string;
  /** LDAP attribute for user's last name */
  lastNameAttribute: string;
  /** LDAP attribute for user's display name */
  displayNameAttribute: string;
  /** LDAP attribute for user's email */
  emailAttribute: string;
  /** LDAP attribute for user's mobile number */
  mobileAttribute: string;
  /** LDAP attribute for user's unique ID */
  userIdAttribute: string;
  /** LDAP object class for user entries */
  objectClass: string;
  /** LDAP filter for user search */
  objectFilter: string;
  /** LDAP attribute for user's password */
  passwordAttribute: string;
  /** LDAP attribute for user's username */
  usernameAttribute: string;
  /** Flag to ignore duplicate identity users */
  ignoreSameIdentityUser: boolean;
}

/**
 * Group schema configuration interface
 */
export interface GroupSchemaConfig {
  /** LDAP attribute for group description */
  descriptionAttribute: string;
  /** LDAP filter for group search */
  objectFilter: string;
  /** LDAP attribute for group name */
  nameAttribute: string;
  /** LDAP object class for group entries */
  objectClass: string;
  /** Flag to ignore duplicate name groups */
  ignoreSameNameGroup: boolean;
}

/**
 * Membership configuration interface
 */
export interface MembershipConfig {
  /** LDAP attribute for group membership (user -> group) */
  groupMemberAttribute: string;
  /** LDAP attribute for member groups (group -> user) */
  memberGroupAttribute: string;
}

/**
 * Complete LDAP directory configuration interface
 */
export interface LdapDirectoryConfig {
  /** Unique identifier */
  id?: string;
  /** Directory name */
  name: string;
  /** Whether directory is enabled */
  enabled: boolean;
  /** Sequence number for ordering */
  sequence?: number;
  /** Server configuration */
  server: ServerConfig;
  /** LDAP base configuration */
  schema: LdapBaseConfig;
  /** User schema configuration */
  userSchema: UserSchemaConfig;
  /** Group schema configuration */
  groupSchema?: GroupSchemaConfig;
  /** Membership configuration */
  membershipSchema?: MembershipConfig;
  /** Created by user name */
  creator?: string;
  /** Creation date */
  createdDate?: string;
}

/**
 * Table column interface for LDAP directory list
 */
export interface TableColumn {
  /** Column title */
  title: string;
  /** Data index */
  dataIndex: string;
  /** Column key */
  key?: string;
  /** Column width */
  width?: string;
  /** Custom cell renderer */
  customCell?: () => { style: string };
}

/**
 * Form layout configuration interface
 */
export interface FormLayoutConfig {
  /** Label column span */
  span: number;
}

/**
 * Form validation rule interface
 */
export interface ValidationRule {
  /** Whether field is required */
  required: boolean;
  /** Error message */
  message: string;
}

/**
 * Form validation rules interface
 */
export interface FormValidationRules {
  [key: string]: ValidationRule[];
}

/**
 * Test result data interface
 */
export interface TestResultData {
  /** Connection test success flag */
  connectSuccess: TestResultStatus;
  /** User sync test success flag */
  userSuccess: TestResultStatus;
  /** Group sync test success flag */
  groupSuccess: TestResultStatus;
  /** Membership sync test success flag */
  membershipSuccess: TestResultStatus;
  /** Error message if test failed */
  errorMessage?: string;
  /** Total user count */
  totalUserNum?: number;
  /** New user count */
  addUserNum?: number;
  /** Updated user count */
  updateUserNum?: number;
  /** Deleted user count */
  deleteUserNum?: number;
  /** Ignored user count */
  ignoreUserNum?: number;
  /** Total group count */
  totalGroupNum?: number;
  /** New group count */
  addGroupNum?: number;
  /** Updated group count */
  updateGroupNum?: number;
  /** Deleted group count */
  deleteGroupNum?: number;
  /** Ignored group count */
  ignoreGroupNum?: number;
  /** New membership count */
  addMembershipNum?: number;
  /** Deleted membership count */
  deleteMembershipNum?: number;
}

/**
 * Component state interface for main LDAP component
 */
export interface LdapMainState {
  /** LDAP directory list data */
  dataSource: LdapDirectoryConfig[];
  /** Loading state for list */
  listSpin: boolean;
  /** Test modal visibility */
  testVisible: boolean;
  /** Test loading state */
  testLoading: boolean;
  /** Sync loading states array */
  syncLoading: boolean[];
  /** Test record data */
  testRecord: any;
  /** Test result display data */
  showData: TestResultData;
  /** Test result messages */
  connectMsg: string;
  userMsg: string;
  groupMsg: string;
  memberMsg: string;
}

/**
 * Component state interface for detail component
 */
export interface LdapDetailState {
  /** Active tab key */
  activeKey: number;
  /** Component references */
  groupMemberMode: any;
  groupMode: any;
  mode: any;
  server: any;
  userMode: any;
  /** Children component data */
  childrenData: {
    server: any;
    groupSchema: any;
    membershipSchema: any;
    schema: any;
    userSchema: any;
  };
  /** Error handling flag */
  errorStop: boolean;
  /** Save loading state */
  saveLoading: boolean;
  /** Test loading state */
  testLoading: boolean;
  /** Submit operation type */
  submitType: string;
  /** Edit data */
  editShow: any;
  /** Validation rules count */
  rulesCount: number;
  /** Submit button reference */
  submitRef: any;
}

/**
 * Component state interface for form components
 */
export interface FormComponentState {
  /** Form reference */
  formRef: any;
  /** Form data */
  form: any;
  /** Form validation rules */
  rules: FormValidationRules;
}

/**
 * API response interface for LDAP directories
 */
export interface LdapDirectoriesResponse {
  /** Response data */
  data: LdapDirectoryConfig[];
}

/**
 * API response interface for LDAP directory detail
 */
export interface LdapDirectoryDetailResponse {
  /** Response data */
  data: LdapDirectoryConfig;
}

/**
 * API response interface for LDAP test result */
export interface LdapTestResponse {
  /** Response data */
  data: TestResultData;
}

/**
 * Form submission data interface
 */
export interface FormSubmissionData {
  [key: string]: any;
}

/**
 * Validation callback parameters interface
 */
export interface ValidationCallbackParams {
  /** Validation result type */
  type: ValidationResult;
  /** Component label */
  label: string;
  /** Component index */
  index: number;
  /** Form data */
  form: any;
}

/**
 * Tab change parameters interface
 */
export interface TabChangeParams {
  /** Tab index */
  index: number;
}

/**
 * Directory operation parameters interface
 */
export interface DirectoryOperationParams {
  /** Directory ID */
  id: string;
  /** Additional parameters */
  [key: string]: any;
}

/**
 * Sequence update parameters interface
 */
export interface SequenceUpdateParams {
  /** Directory ID */
  id: string;
  /** New sequence number */
  sequence: number;
}

/**
 * Delete confirmation parameters interface
 */
export interface DeleteConfirmationParams {
  /** Directory record */
  record: LdapDirectoryConfig;
  /** Whether to delete sync data */
  deleteSync: boolean;
}

/**
 * Test modal props interface
 */
export interface TestModalProps {
  /** Modal visibility */
  visible: boolean;
  /** Directory ID to test */
  id: string;
}

/**
 * Submit button props interface
 */
export interface SubmitButtonProps {
  /** Test loading state */
  testLoading: boolean;
  /** Save loading state */
  saveLoading: boolean;
}

/**
 * Alert type mapping interface
 */
export interface AlertTypeMapping {
  /** Success status */
  success: string;
  /** Error status */
  error: string;
  /** Info status */
  info: string;
}

/**
 * Form field configuration interface
 */
export interface FormFieldConfig {
  /** Field label */
  label: string;
  /** Field name */
  name: string;
  /** Field type */
  type?: string;
  /** Field placeholder */
  placeholder?: string;
  /** Maximum length */
  maxlength?: number;
  /** Whether field is required */
  required?: boolean;
  /** Field validation rules */
  rules?: ValidationRule[];
  /** Custom render function */
  customRender?: (params: any) => string;
}

/**
 * Component configuration interface
 */
export interface ComponentConfig {
  /** Component key */
  key: string;
  /** Component index */
  index: number;
  /** Component query data */
  query?: any;
  /** Component class */
  class?: string;
}
