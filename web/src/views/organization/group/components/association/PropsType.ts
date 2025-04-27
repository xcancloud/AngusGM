export type SearchParams = {
  pageNo: number;
  pageSize: number;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
  filters?: { key: string, op: string, value: any }[]
}

export type User = {
  avatar: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  fullName: string;
  groupCode: string;
  groupEnabled: boolean;
  groupId: string;
  groupName: string;
  id: string;
  mobile: string;
  tenantId: string;
  userId: string;
}
