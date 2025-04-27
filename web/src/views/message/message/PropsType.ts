export interface TableColumnType {
  sorter?: boolean,
  title: string,
  dataIndex: string,
  key: string,
  slots?: { customRender: string }
}
