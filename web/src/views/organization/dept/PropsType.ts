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
