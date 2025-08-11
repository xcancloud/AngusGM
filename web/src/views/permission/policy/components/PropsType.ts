/**
 * Form data structure for permission policy
 */
export interface FormType {
  id: string,
  appId: string | undefined,
  appName: string,
  name: string,
  description: string,
  funcIds: string[],
  code?: string,
  default0: boolean
}

/**
 * Form data structure for authorization modal
 * @interface FormType
 * @property {string} id - Policy identifier
 * @property {string} name - Policy name
 * @property {string} appId - Application identifier
 * @property {'USER' | 'DEPT' | 'GROUP'} targetType - Type of target to authorize
 * @property {string[]} targetId - Array of target identifiers to authorize
 */
export interface AuthFormType {
  id: string,
  name: string,
  appId: string,
  targetType: 'USER' | 'DEPT' | 'GROUP',
  targetId: string[]
}

/**
 * API resource structure
 */
export interface ApisType {
  id: string,
  code: string,
  name: string,
  resourceName: string
}

/**
 * Tree node data structure for menu and function permissions
 */
export interface DataRecordType {
  id: string | number | undefined,
  showName: string,
  pid: string | number | undefined,
  type: { value: 'BUTTON' | 'MENU' | 'PANEL' | undefined, message: string | undefined },
  apis: ApisType[],
  auth?: boolean,
  children: DataRecordType[],
  disabled?: boolean,
  disableCheckbox?: boolean
}

/**
 * Policy record data structure
 */
export interface PolicyRecordType {
  id: string | undefined,
  name: string | undefined,
  appId: string | number | undefined,
  appName: string | undefined,
  createdByName: string | undefined,
  createdDate: string | undefined,
  type: { value: 'PRE_DEFINED' | 'USER_DEFINED', message: string },
  enabled: boolean,
}

/**
 * Policy detail information structure
 */
export type PolicyDetailType = {
  id: string | undefined,
  name: string | undefined,
  code: string | undefined,
  appId: string | undefined,
  appName: string | undefined,
  createdByName: string | undefined,
  createdDate: string | undefined,
  type: { value: 'OPEN_GRANT' | 'PRE_DEFINED' | 'USER_DEFINED' | undefined, message: string | undefined },
  enabled: boolean,
  description: string | undefined
}

/**
 * Policy default record structure
 */
export interface PolicyDefaultRecordType {
  id: string,
  name: string | undefined,
  code: string | undefined,
  policyId?: string,
  currentDefault: boolean,
  type: { value: 'PRE_DEFINED' | 'USER_DEFINED', message: string }
}

/**
 * Data record structure for the table
 */
export interface PolicyDataRecordType {
  appId: number | undefined,
  appName: string | undefined,
  policyId?: string,
  defaultPolicies: PolicyDefaultRecordType[]
}

/**
 * Data structure for target items (users, departments, or groups)
 */
export interface AuthObjectDataType {
  id: string,
  policyId: string,
  appId?: string,
  userId?: string,
  avatar?: string,
  createdByName?: string,
  deptId?: string,
  groupId?: string,
  fullName?: string,
  name: string
}
