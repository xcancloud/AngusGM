import { Gender, Source } from '../PropsType';

export type SearchParams = {
  pageNo: number;
  pageSize: number;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
  filters?: { key: string, op: string, value: any }[]
}

export type UserGroup = {
  id: string;
  groupId: string;
  groupName: string;
  groupCode: string;
  groupEnabled: true,
  userId: string;
  fullName: string;
  mobile: string;
  avatar: string;
  createdDate: string;
  createdBy: string;
  tenantId: string;
  groupRemark: string;
  createdByName: string;
}

export type UserDept = {
  id: string;
  deptId: string;
  deptCode: string;
  deptName: string;
  deptHead: boolean;
  mainDept: boolean;
  userId: string;
  fullName: string;
  mobile: string;
  avatar: string;
  hasSubDept: boolean;
  createdDate: string;
  createdBy: string;
  tenantId: string;
  createdByName: string;
}

export type UserTag = {
  tagId: string;
  tagName: string;
  targetId: string;
  targetType: string;
  targetName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  targetCreatedBy: string;
  targetCreatedByName: string;
  targetCreatedDate: string;
}

export interface Detail {
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
    value: Gender,
    message: string;
  },
  contactAddress: string;
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
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
  passwordStrength: {
    value: string;
    message: string;
  },
  passwordExpired: boolean;
  passwordExpiredDate: string;
  tenantRealNameStatus: string;
  online: boolean;
  onlineDate: string;
  offlineDate: string;
  tags: [
    {
      id: string;
      name: string;
    }
  ],
  depts: string;
  groups: [
    {
      id: string;
      name: string;
    }
  ]
}
