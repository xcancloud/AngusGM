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
  filters: Filters;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
}

export type EmailRecord = {
  id: string;
  templateCode: string;
  language: {
    value: string;
    message: string;
  },
  bizKey: string;
  outId: string;
  sendId: string;
  emailType: string;
  subject: string;
  fromAddr: string;
  verificationCode: boolean;
  verificationCodeValidSecond: string;
  toAddress: string[],
  ccAddress: string;
  html: boolean;
  sendStatus: string;
  failureReason: string;
  content: string;
  templateParams: any,
  attachments: string;
  expectedSendDate: string;
  actualSendDate: string;
  urgent: boolean;
  sendTenantId: string;
  batch: boolean;
}
