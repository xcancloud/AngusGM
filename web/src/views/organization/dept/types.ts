import { PageQuery, SearchCriteria } from '@xcan-angus/infra';

/**
 * <p>
 * Record type for general data records
 * </p>
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
 * <p>
 * Department data type interface
 * </p>
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
 * <p>
 * Tree record type for department hierarchy
 * </p>
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
 * <p>
 * User record type for department users
 * </p>
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

/**
 * <p>
 * Department state interface
 * </p>
 * Manages the reactive state of the department component
 */
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

/**
 * <p>
 * Department info interface
 * </p>
 * Contains detailed information about a department
 */
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
 * <p>
 * Current action node interface
 * </p>
 * Represents the currently selected department node for operations
 */
export interface CurrentActionNode {
  id: string;
  name: string;
  pid: string;
}

/**
 * <p>
 * Policy pagination interface
 * </p>
 * Manages pagination for policy data
 */
export interface PolicyPagination {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * <p>
 * Search parameters for departments
 * </p>
 */
export interface DeptSearchParams {
  tagId?: string;
  fullTextSearch: boolean;
}

/**
 * <p>
 * Tree parameters for department tree loading
 * </p>
 */
export interface TreeParams {
  pid: number;
  pageSize: number;
  tagId?: string;
  orderBy: string;
  orderSort: string;
}

/**
 * <p>
 * Tree field names configuration
 * </p>
 */
export interface TreeFieldNames {
  title: string;
  key: string;
  children: string;
}

/**
 * <p>
 * Table column configuration
 * </p>
 */
export interface TableColumn {
  title: string;
  key: string;
  dataIndex: string;
  width: string;
  align?: 'left' | 'center' | 'right';
  ellipsis?: boolean;
  customCell?: () => any;
}

/**
 * <p>
 * API response structure for department operations
 * </p>
 */
export interface DeptApiResponse<T = any> {
  data: T;
  success: boolean;
  message?: string;
}

/**
 * <p>
 * Navigation chain response for department search
 * </p>
 */
export interface NavigationResponse {
  parentChain: DataType[];
  current: DataType;
}

/**
 * <p>
 * Department count response
 * </p>
 */
export interface DeptCountResponse {
  subDeptNum: number;
  sunUserNum: number;
}

/**
 * <p>
 * Generic API function type
 * </p>
 */
export type ApiFunction<TParams = any, TResponse = any> = (
  ...params: TParams[]
) => Promise<[any, TResponse] | [null, TResponse]>;

/**
 * <p>
 * Event handler types
 * </p>
 */
export interface EventHandlers {
  onSelect: (selectedKeys: string[], selected: boolean, info: TreeRecordType) => Promise<void>;
  onTableChange: (pagination: any) => Promise<void>;
  onSearch: (value: any) => Promise<void>;
}

/**
 * <p>
 * Modal visibility state
 * </p>
 */
export interface ModalState {
  addModalVisible: boolean;
  editModalVisible: boolean;
  editTagVisible: boolean;
  userVisible: boolean;
  moveVisible: boolean;
  policyVisible: boolean;
}

/**
 * <p>
 * Loading state interface
 * </p>
 */
export interface LoadingState {
  userLoading: boolean;
  treeLoading: boolean;
  refreshDisabled: boolean;
  userLoadDisabled: boolean;
  policyLoadDisabled: boolean;
  policyUpdateLoading: boolean;
  userUpdateLoading: boolean;
  policyLoading: boolean;
}