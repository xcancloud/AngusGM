import type { Dayjs } from 'dayjs';

export interface FormDataType {
  content: string;
  scope: string;
  appCode?: string | undefined;
  appName?: string | undefined;
  sendType: string;
  sendTimingDate?: Dayjs | string | undefined;

  [key: string]: unknown;

  expirationDate: string | undefined;
  id?: string,
}
