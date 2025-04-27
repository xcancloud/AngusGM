export interface Option {
  value: string,
  label: string
}

export interface Column {
  dataIndex: string,
  localesCode: string
}

export interface GrantData {
  acls?: Array<string>,
  apiIds?: string[],
  resource: string
}

export interface Service {
  serviceCode: string,
  serviceName: string,
  title?: string,
  resources: Array<any>,
  spread?: boolean,
  list: Array<any>,
  open: boolean
}

export const _columns = [
  {
    title: '名称',
    dataIndex: 'name',
    localesCode: 'systemToken.col_name'
  },
  {
    title: '授权方式',
    dataIndex: 'authType',
    localesCode: '授权方式'
  },
  {
    title: '到期时间',
    dataIndex: 'expiredDate',
    localesCode: 'systemToken.col_expird_date'
  },
  {
    title: '令牌',
    dataIndex: 'token',
    localesCode: 'systemToken.table_token_name',
    width: '550px'
  },
  {
    title: '是否到期',
    dataIndex: 'expired',
    localesCode: '是否到期'
  },
  {
    title: '创建人',
    dataIndex: 'createdByName',
    localesCode: 'createdBy'
  },
  {
    title: '创建时间',
    dataIndex: 'createdDate',
    localesCode: 'createdDate'
  },
  {
    title: '操作',
    dataIndex: 'action',
    localesCode: 'systemToken.col_action',
    width: 140
  }
];
