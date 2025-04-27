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

export type ListGroup = {
  id: string;
  name: string;
  code: string;
  userNum: string;
  enabled: boolean;
  remark: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
}

export type Detail = ListGroup & { tags: null | { id: string, name: string }[] }

export const _columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: 'name',
    dataIndex: 'name',
    width: '13%'
  },
  {
    title: 'code',
    dataIndex: 'code'
  },
  {
    title: 'status',
    dataIndex: 'enabled',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: '来源',
    dataIndex: 'source',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: 'userNumber',
    dataIndex: 'userNum',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: 'createdBy',
    dataIndex: 'createdByName',
    width: '10%'
  },
  {
    title: 'createdDate',
    sorter: true,
    dataIndex: 'createdDate',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: 'operation',
    dataIndex: 'action',
    width: 130,
    align: 'center'
  }
];

export type GroupRecordType = {
  id: string,
  name: string,
  tenantId: string,
  tenantName: string,
  member: number,
  enabled: boolean
};

export type FormState = {
  code: string;
  name: string;
  remark: string;
  tagIds: string[]
};

export type ConcatUserRecordType = {
  avatar: string,
  createdDate: string,
  fullName: string,
  groupId: string,
  id: string,
  userId: string,
  mobile: string
}
