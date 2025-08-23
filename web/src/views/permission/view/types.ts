/**
 * Policy record data structure for display in tables
 */
export interface PolicyRecordType {
  id: string,
  policyId: string,
  name: string,
  fullName: string,
  createdDate: string,
  description: string,
  orgType?: { value: string, message: string },
  orgName?: string,
  authByName?: string,
  authDate?: string
}

/**
 * Target data structure for different entity types
 */
export interface TargetDataType {
  id: string,
  userId?: string,
  avatar?: string,
  createdByName?: string,
  userName?: string,
  deptName?: string,
  groupName?: string,
  deptId?: string,
  groupId?: string,
  fullName?: string,
  name?: string
}

/**
 * Component state interface for main view
 */
export interface ViewState {
  tab: 'USER' | 'DEPT' | 'GROUP',
  targetId: string | undefined,
  loading: boolean,
  dataSource: PolicyRecordType[]
}

/**
 * Component state interface for target management
 */
export interface TargetManagementState {
  loading: boolean,
  dataSource: TargetDataType[],
  searchValue: string | undefined,
  selectedTargetId: string | undefined
}

/**
 * Component state interface for add members modal
 */
export interface AddMembersState {
  selectedUserIds: any,
  userError: boolean,
  selectedPolicyIds: string[],
  policyError: boolean
}

/**
 * Pagination configuration interface
 */
export interface PaginationConfig {
  total: number,
  current: number,
  pageSize: number
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  key: string,
  title: string,
  dataIndex: string,
  width?: string | number,
  customCell?: () => any,
  align?: 'left' | 'center' | 'right'
}

/**
 * Policy addition configuration mapping
 */
export interface PolicyAdditionConfig {
  USER: any,
  DEPT: any,
  GROUP: any
}

/**
 * Authorization configuration mapping
 */
export interface AuthorizationConfig {
  USER: string,
  DEPT: string,
  GROUP: string
}

/**
 * Component props interface for target panel
 */
export interface TargetPanelProps {
  type: 'USER' | 'DEPT' | 'GROUP',
  appId: string | number,
  selectedTargetId?: string
}

/**
 * Component props interface for add members modal
 */
export interface AddMembersProps {
  visible: boolean,
  appId: string | number,
  type: 'USER' | 'DEPT' | 'GROUP'
}

/**
 * Entity type for policy modal
 */
export type EntityType = 'User' | 'Dept' | 'Group'

/**
 * API parameters for policy operations
 */
export interface PolicyApiParams {
  pageNo: number,
  pageSize: number,
  appId: string | number
}

/**
 * Add authorization parameters
 */
export interface AddAuthParams {
  orgIds: string[],
  policyIds: string[]
}
