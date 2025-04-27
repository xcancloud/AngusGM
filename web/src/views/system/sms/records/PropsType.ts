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

export type Protocol = 'SMTP' | 'IMAP' | 'POP3'

export type SmsRecord = {
  id: string;
  templateCode: string;
  language: {
    value: string;
    message: string;
  },
  bizKey: string;
  outId: string;
  thirdOutputParam: any;
  inputParam: any;
  verificationCode: boolean;
  batch: boolean;
  sendTenantId: string;
  sendUserId: string;
  urgent: boolean;
  sendStatus: boolean;
  actualSendDate: boolean;
  expectedSendDate: boolean;
}
