export type Protocol = 'SMTP' | 'IMAP' | 'POP3'

export type MailboxService = {
  id: string;
  name: string;
  protocol: Protocol;
  remark: string;
  enabled: boolean;
  host: string;
  port: string | number;
  startTlsEnabled: boolean;
  sslEnabled: boolean;
  authEnabled: boolean;
  authAccount: {
    account: string;
    password: string;
  },
  subjectPrefix: string;
}

export type FormState = {
  id?: string;
  name: string;
  protocol: Protocol;
  remark: string;
  enabled: boolean;
  host: string;
  port: string | number;
  startTlsEnabled: boolean;
  sslEnabled: boolean;
  authEnabled: boolean;
  authAccount: {
    account: string;
    password: string;
  },
  subjectPrefix: string;
}
