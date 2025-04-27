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

export const _gidColumns = [
  [
    {
      label: 'ID',
      dataIndex: 'id',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      label: 'code',
      dataIndex: 'code'
    },
    {
      label: 'userNumber',
      dataIndex: 'userNum'
    },
    {
      label: '来源',
      dataIndex: 'source'
    },
    {
      label: 'status',
      dataIndex: 'enabled'
    },
    {
      label: 'description',
      dataIndex: 'remark'
    },
    {
      label: 'createdBy',
      dataIndex: 'createdByName'
    },
    {
      label: 'createdDate',
      dataIndex: 'createdDate'
    },
    {
      label: '最后修改人',
      dataIndex: 'lastModifiedByName'
    },
    {
      label: '最后修改时间',
      dataIndex: 'lastModifiedDate'
    },
    {
      label: 'label',
      dataIndex: 'tags'
    }
  ]
];
