import { EnumMessage, Gender, UserSource, PasswordStrength } from '@xcan-angus/infra';

export type SearchParams = { // TODO 替换引用
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
  gender: EnumMessage<Gender>,
  contactAddress: string;
  sysAdmin: boolean;
  deptHead: boolean;
  enabled: boolean;
  source: EnumMessage<UserSource>,
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
  passwordStrength: EnumMessage<PasswordStrength>,
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
