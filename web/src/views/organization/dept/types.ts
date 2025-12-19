/**
 * Record type for general data records
 * Used for tags and other metadata
 */
export interface RecordType {
  /** Unique identifier */
  id: string;
  /** Record name */
  name: string;
  /** Additional properties */
  [key: string]: any;
}

/**
 * Department data type interface
 * Represents the structure of department information
 */
export interface DataType {
  /** Unique identifier for the department */
  id: string | undefined;
  /** Department name */
  name: string | undefined;
  /** Parent department ID */
  pid: string | undefined;
  /** Department code */
  code: string | undefined;
  /** Creation date */
  createdDate: string | undefined;
  /** Tenant ID */
  tenantId: string | undefined;
  /** Tenant name */
  tenantName: string | undefined;
  /** Associated tags */
  tags: RecordType[];
}

/**
 * Tree record type for department hierarchy
 * Represents department nodes in the tree structure
 */
export interface TreeRecordType {
  /** Unique identifier for the department */
  id: string | undefined;
  /** Department name */
  name: string | undefined;
  /** Parent department ID */
  pid: string | undefined;
  /** Child departments */
  children?: TreeRecordType[];
  /** Associated tags */
  tags?: { id: string; name: string }[];
  /** Icon for the department */
  icon?: string;
  /** Whether department has sub-departments */
  hasSubDept?: boolean;
}

/**
 * User record type for department users
 * Represents user information within a department
 */
export interface UserRecordType {
  /** Unique identifier for the user */
  id: string;
  /** Full name of the user */
  fullName: string | undefined;
  /** Department ID */
  deptId: string;
  /** User ID */
  userId: string;
  /** Department name */
  deptName: string | undefined;
  /** Creation date */
  createdDate: string | undefined;
  /** User avatar URL */
  avatar: string | undefined;
  /** Mobile number */
  mobile: string | undefined;
  /** Whether user is department head */
  deptHead: boolean;
  /** Whether this is the main department for the user */
  mainDept: boolean;
  /** Created by name */
  creator?: string;
}

// Department state interface
export interface DeptState {
  sortForm: Record<string, any>;
  searchForm: any[];
  loading: boolean;
  dataSource: DataType[];
  currentRecord: DataType | undefined;
  addDeptVisible: boolean;
  editDeptNameVisible: boolean;
  userLoading: boolean;
  userDataSource: UserRecordType[];
  currentSelectedNode: TreeRecordType;
  concatUserSource: UserRecordType[];
}

// Department info interface
export interface DeptInfo {
  creator: string;
  createdDate: string;
  tags: { id: string; name: string }[];
  level: string;
  name: string;
  code: string;
  id: string;
  modifiedDate: string;
  modifier: string;
}

/**
 * Form data interface for department operations
 */
export interface DeptFormData {
  pid: string | undefined;
  name: string | undefined;
  code: string | undefined;
  tags: string[];
}

/**
 * Policy pagination interface
 */
export interface PolicyPagination {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * Action node interface for context menu operations
 */
export interface ActionNode {
  id: string;
  name: string;
  pid: string;
}

/**
 * Tree parameters for loading
 */
export interface TreeParams {
  pid: number;
  pageSize: number;
  tagId?: string;
  orderBy: string;
  orderSort: string;
}

/**
 * Search parameters for department search
 */
export interface SearchDeptParams {
  tagId?: string;
  fullTextSearch: boolean;
}

/**
 * Table column configuration
 */
export interface TableColumn {
  title: string;
  key: string;
  dataIndex: string;
  width?: string;
  ellipsis?: boolean;
  align?: 'left' | 'center' | 'right';
  customCell?: () => any;
}

/**
 * Form validation rules for Ant Design Vue
 */
export interface FormRules {
  [key: string]: Array<{
    required?: boolean;
    message: string;
    trigger: string;
    min?: number;
    max?: number;
    type?: string;
    validator?: (rule: any, value: any) => boolean | string | Promise<void>;
  }>;
}

/**
 * Node-like interface for basic node operations
 */
export interface NodeLike {
  id?: string;
}

/**
 * Form type for department editing
 */
export interface FormType {
  name: string | undefined;
}

/**
 * Component props interface for edit component
 */
export interface EditProps {
  visible: boolean;
  id: string | undefined;
  name: string | undefined;
}

/**
 * Component emits interface for edit component
 */
export interface EditEmits {
  (e: 'close'): void;
  (e: 'save', value: string): void;
}

/**
 * Component props interface for add component
 */
export interface AddProps {
  visible: boolean;
  pid?: string | undefined;
  pname?: string;
}

/**
 * Component emits interface for add component
 */
export interface AddEmits {
  (e: 'close'): void;
  (e: 'save', value: any): void;
}

/**
 * Component props interface for move component
 */
export interface MoveProps {
  visible: boolean;
  moveId?: string;
  defaultPid?: string;
}

/**
 * Component emits interface for move component
 */
export interface MoveEmits {
  (e: 'ok', value: string): void;
  (e: 'update:visible', value: boolean): void;
}

/**
 * Component emits interface for info component
 */
export interface InfoEmits {
  (e: 'add', node: NodeLike): void;
  (e: 'editName', node: NodeLike): void;
  (e: 'delete', node: NodeLike): void;
  (e: 'editTag', node: NodeLike): void;
  (e: 'move', node: NodeLike): void;
}
