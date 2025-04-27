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
