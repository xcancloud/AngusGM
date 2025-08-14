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
  createdByName?: string;
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
  createdByName: string;
  createdDate: string;
  tags: { id: string; name: string }[];
  level: string;
  name: string;
  code: string;
  id: string;
  lastModifiedDate: string;
  lastModifiedByName: string;
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
 * Department creation parameters
 */
export interface CreateDeptParams {
  code: string;
  name: string;
  pid: string;
  tagIds: string[];
}

/**
 * Department update parameters
 */
export interface UpdateDeptParams {
  id: string;
  name?: string;
  pid?: string;
}

/**
 * Department count information
 */
export interface DeptCountInfo {
  subDeptNum: number;
  sunUserNum: number;
}

/**
 * Department navigation response
 */
export interface DeptNavigationResponse {
  parentChain: any[];
  current: {
    id: string;
    pid: string;
    name: string;
  };
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
 * Tree field names configuration
 */
export interface TreeFieldNames {
  title: string;
  key: string;
  children: string;
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
 * Component props for department operations
 */
export interface DeptModalProps {
  visible: boolean;
  id?: string;
  name?: string;
  pid?: string;
  pname?: string;
  moveId?: string;
  defaultPid?: string;
  deptId?: string;
  updateLoading?: boolean;
  type?: string;
}

/**
 * Emit events interface
 */
export interface DeptEmits {
  close: () => void;
  save: (value: any) => void;
  ok?: (value: string) => void;
  'update:visible'?: (value: boolean) => void;
  change?: (addIds: string[], users: any[], deleteIds: string[]) => void;
  add?: (node: any) => void;
  editName?: (node: any) => void;
  delete?: (node: any) => void;
  editTag?: (node: any) => void;
  move?: (node: any) => void;
}

/**
 * Department tag interface
 */
export interface DeptTag {
  id: string;
  name: string;
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
 * Component props interface for info component
 */
export interface InfoProps {
  node: NodeLike;
  deptInfo: DeptInfo;
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
