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

export type TargetType = 'USER' | 'DEPT' | 'GROUP'

export type OrgTag = {
  id: string;
  name: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
  isEdit: boolean;
}
export type Target = {
  id: string;
  targetType: TargetType,
  targetId: string;
  targetName: string;
  tagId: string;
  tagName: string;
  createdDate: string;
  createdBy: string;
  createdByName: string;
  targetCreatedDate: string;
  targetCreatedBy: string;
  targetCreatedByName: string;
}
