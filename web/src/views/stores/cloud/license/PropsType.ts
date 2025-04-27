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

type EnumType = {
  value: string,
  message: string
}

export type Licensed = {
  id: string;
  applyId: string;
  goodsName: string;
  goodsId: string;
  expired: false,
  revoke: false,
  createdBy: string;
  createdByName: string;
  createdDate: string;
  licenseNo: string;
  mainLicenseNo: string;
  provider: string;
  issuer: string;
  holderId: string;
  holder: string;
  goodsType: EnumType;
  editionType: EnumType;
  goodsVersion: string;
  orderNo: string;
  subject: string;
  info: string;
  signature: string;
  productSignatureArtifact: string;
  productSignature: string;
  issuedDate: string;
  expiredDate: string;
  validDays: string;
  consumerType: EnumType;
  consumerAmount: string;
  testConcurrency: string;
  testNodeNumber: string;
  testTaskNumber: string;
  extraQuotas: Record<string, string>;
  servers: {
    ipAddress: string;
    macAddress: string;
    cpuSerialNumber: string;
    mainBoardSerial: string;
    machineCode: string;
  }[]
}
