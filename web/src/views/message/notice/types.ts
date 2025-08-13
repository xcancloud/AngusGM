import type { Dayjs } from 'dayjs';
import { NoticeScope, SentType } from '@/enums/enums';

export interface NoticeDataType {
  content: string;
  scope: NoticeScope;
  appCode?: string | undefined;
  appName?: string | undefined;
  sendType: SentType;
  sendTimingDate?: Dayjs | string | undefined;

  [key: string]: unknown;

  expirationDate: string | undefined;
  id?: string,
}
