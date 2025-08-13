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

export interface NoticeFormType extends NoticeDataType {
  editionType?: any;
  appId?: string;
}

export interface PaginationType {
  pageSize: number;
  current: number;
  total: number;
}

export interface SearchParamsType {
  [key: string]: any;
}

export interface QueryParamsType {
  pageSize: number;
  pageNo: number;
  filters: SearchParamsType[];
  fullTextSearch: boolean;
}

export interface EnumsListType {
  noticeScopeList: Array<any>;
  SentTypeList: Array<any>;
}

export interface FormRulesType {
  [key: string]: any[];
}

export interface SearchOptionType {
  type: string;
  allowClear: boolean;
  valueKey: string;
  placeholder: string;
  enumKey?: any;
}

export interface TableColumnType {
  title: string;
  key: string;
  dataIndex: string;
  ellipsis?: boolean;
  width?: string;
  align?: string;
  customCell?: () => { style: string };
}

export interface DetailColumnType {
  dataIndex: string;
  label: string;
}

export interface DetailColumnsType {
  [key: number]: DetailColumnType[];
}

export interface AppOptionType {
  appCode: string;
  appName: string;
  appId: string;
  editionType: {
    value: any;
    message: string;
  };
}
