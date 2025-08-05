import { Gender, UserSource, EnumMessage } from '@xcan-angus/infra';

/**
 * 用户信息类型定义
 */
export interface User {
  id: string;
  username: string;
  fullName: string;
  firstName: string;
  lastName: string;
  itc: string;
  country: string;
  mobile: string;
  email: string;
  landline: string;
  avatar: string;
  title: string;
  gender: EnumMessage<Gender>;
  address: {
    provinceCode: string;
    provinceName: string;
    cityCode: string;
    cityName: string;
    street: string;
  };
  sysAdmin: boolean;
  deptHead: boolean;
  enabled: boolean;
  source: EnumMessage<UserSource>;
  locked: boolean;
  lockStartDate: string;
  lockEndDate: string;
  online: boolean;
  onlineDate: string;
  offlineDate: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
}

/**
 * 表单状态类型定义
 */
export interface FormState {
  address: string;
  avatar: string;
  country: string;
  email: string;
  firstName: string;
  fullName: string;
  gender: Gender;
  itc: string;
  landline: string;
  lastName: string;
  mobile: string | null;
  password: string;
  sysAdmin: boolean;
  title: string;
  username: string;
  confirmPassword?: string;
}

/**
 * 部门信息类型定义
 */
export interface Dept {
  id: string;
  code: string;
  name: string;
  pid: string;
  parentName: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
  hasSubDept: boolean;
}

/**
 * 用户状态管理类型定义
 */
export interface UserState {
  updatePasswdVisible: boolean;
  lockModalVisible: boolean;
  currentUserId: string | undefined;
}

/**
 * 搜索选项类型定义
 */
export type SearchOptionType = 'input' | 'select-enum' | 'date-range' | 'select' | 'date' | 'select-app' | 'select-dept' | 'select-group' | 'select-intl' | 'select-itc' | 'select-user' | 'select-service' | 'select-tag' | 'select-tenant' | 'tree-select';

/**
 * 搜索操作符类型定义
 */
export type SearchOperator = 'EQUAL' | 'GREATER_THAN' | 'GREATER_THAN_EQUAL' | 'IN' | 'LESS_THAN' | 'LESS_THAN_EQUAL' | 'MATCH' | 'MATCH_END' | 'NOT_EQUAL' | 'NOT_IN';

/**
 * 搜索选项配置类型定义
 */
export interface SearchOption {
  placeholder?: string;
  valueKey: string;
  type: SearchOptionType;
  op?: SearchOperator;
  allowClear?: boolean;
  enumKey?: string;
  action?: string;
  fieldNames?: { label: string; value: string };
  showSearch?: boolean;
  lazy?: boolean;
}
