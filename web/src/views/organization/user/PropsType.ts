export type FilterOp =
  'EQUAL'
  | 'NOT_EQUAL'
  | 'GREATER_THAN'
  | 'GREATER_THAN_EQUAL'
  | 'LESS_THAN'
  | 'LESS_THAN_EQUAL'
  | 'CONTAIN'
  | 'NOT_CONTAIN'
  | 'MATCH_END'
  | 'MATCH'
  | 'IN'
  | 'NOT_IN'
export type Filters = { key: string, value: string, op: FilterOp }[]
export type SearchParams = {
  pageNo?: number;
  pageSize?: number;
  filters?: Filters;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
}

export type Gender = 'FEMALE' | 'MALE' | 'UNKNOWN'
export type Source =
  'PLATFORM_SIGNUP'
  | 'INVITATION_CODE_SIGNUP'
  | 'BACKGROUND_SIGNUP'
  | 'BACKGROUND_ADDED'
  | 'BACKGROUND_INVITATION'
  | 'THIRD_PARTY_LOGIN'
  | 'LDAP_SYNCHRONIZE'

export type User = {
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
  gender: {
    value: Gender;
    message: string;
  },
  address: {
    provinceCode: string;
    provinceName: string;
    cityCode: string;
    cityName: string;
    street: string;
  },
  sysAdmin: boolean;
  deptHead: boolean;
  enabled: boolean;
  source: {
    value: Source;
    message: string;
  },
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

export type FormState = {
  address: string;
  avatar: string;
  country: string;
  email: string;
  firstName: string;
  fullName: string;
  gender: Gender
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

export type Dept = {
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

export type TreeData = Dept & {
  level: number,
  mainDept: boolean,
  pageNo: number,
  total: number,
  children: TreeData[]
}
