export const _searchOptions = [
  {
    placeholder: 'accountsDept.placeholder.p1',
    valueKey: 'name',
    type: 'input'
  },
  {
    value: 'tagId',
    type: 'select-tag',
    allowClear: true,
    valueKey: 'tag',
    placeholder: '选择标签'
  }
];

export const _columns = [
  {
    title: 'accountsDept.columns.code',
    dataIndex: 'code'
  },
  {
    title: 'accountsDept.columns.name',
    dataIndex: 'name'
  },
  {
    title: 'accountsDept.columns.tenantName',
    dataIndex: 'tenantName'
  },
  {
    title: 'accountsDept.columns.createdDate',
    dataIndex: 'createdDate'
  },
  {
    title: 'accountsDept.columns.action',
    dataIndex: 'action',
    align: 'center'
  }
];

export interface DataType {
  id: string | undefined,
  name: string | undefined,
  pid: string | undefined,
  code: string | undefined,
  createdDate: string | undefined,
  tenantId: string | undefined,
  tenantName: string | undefined,
  tags: RecordType[]
}

export interface TreeRecordType {
  id: string | undefined,
  name: string | undefined,
  pid: string | undefined,
  children?: TreeRecordType[],
  tags?: { id: string, name: string }[],
  icon?: string
}

export interface UserRecordType {
  id: string,
  fullName: string | undefined,
  deptId: string,
  userId: string,
  deptName: string | undefined,
  createdDate: string | undefined,
  avatar: string | undefined,
  mobile: string | undefined,
  deptHead: boolean,
  mainDept: boolean
}
