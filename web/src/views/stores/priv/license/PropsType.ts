type EnumType = {
  value: string,
  message: string
}

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

export type Licensed = {
  id: string;
  main: boolean;
  licenseNo: string;
  mainLicenseNo: string;
  provider: string;
  issuer: string;
  holderId: string;
  holder: string;
  productType: EnumType;
  productCode: string;
  productName: string;
  editionType: EnumType;
  version: string;
  orderNo: string;
  goodsId: string;
  subject: string;
  info: string;
  signature: string;
  issuedDate: string;
  beginDate: string;
  endDate: string;
  content: string;
}
