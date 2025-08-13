import type { Dayjs } from 'dayjs';
import { NoticeScope, SentType } from '@/enums/enums';

/**
 * Core notice data structure
 * Represents a notice/notification with all its properties
 */
export interface NoticeDataType {
  content: string; // Notice content text
  scope: NoticeScope; // Notice scope (global or app-specific)
  appCode?: string | undefined; // Application code for app-scoped notices
  appName?: string | undefined; // Application name for app-scoped notices
  sendType: SentType; // Send type (immediate or scheduled)
  sendTimingDate?: Dayjs | string | undefined; // Scheduled send date/time
  expirationDate: string | undefined; // Notice expiration date
  id?: string; // Unique notice identifier

  [key: string]: unknown; // Allow additional dynamic properties
}

/**
 * Extended notice form type
 * Used for creating and editing notices with additional form-specific properties
 */
export interface NoticeFormType extends NoticeDataType {
  editionType?: any; // Application edition type
  appId?: string; // Application ID for app-scoped notices
}

/**
 * Pagination configuration
 * Controls how notice lists are displayed and navigated
 */
export interface PaginationType {
  pageSize: number; // Number of items per page
  current: number; // Current page number
  total: number; // Total number of items
}

/**
 * Search parameters type
 * Flexible structure for various search and filter criteria
 */
export interface SearchParamsType {
  [key: string]: any; // Dynamic search parameters
}

/**
 * Query parameters for API calls
 * Structured parameters sent to notice list API
 */
export interface QueryParamsType {
  pageSize: number; // Items per page
  pageNo: number; // Page number
  filters: SearchParamsType[]; // Search and filter criteria
  fullTextSearch: boolean; // Enable full-text search
}

/**
 * Enum lists for form options
 * Contains available options for notice scope and send type
 */
export interface EnumsListType {
  noticeScopeList: Array<any>; // Available notice scopes
  SentTypeList: Array<any>; // Available send types
}

/**
 * Form validation rules
 * Defines validation requirements for form fields
 */
export interface FormRulesType {
  [key: string]: any[]; // Validation rules for each field
}

/**
 * Search option configuration
 * Defines search panel options and their properties
 */
export interface SearchOptionType {
  type: string; // Search option type (input, select, etc.)
  allowClear: boolean; // Allow clearing the input
  valueKey: string; // Key for the search value
  placeholder: string; // Placeholder text
  enumKey?: any; // Enum values for select options
}

/**
 * Table column configuration
 * Defines how table columns are displayed and formatted
 */
export interface TableColumnType {
  title: string; // Column header text
  key: string; // Unique column identifier
  dataIndex: string; // Data field to display
  ellipsis?: boolean; // Enable text truncation
  width?: string; // Column width
  align?: string; // Text alignment
  customCell?: () => { style: string }; // Custom cell styling
}

/**
 * Detail view column configuration
 * Defines columns for notice detail display
 */
export interface DetailColumnType {
  dataIndex: string; // Data field to display
  label: string; // Column label text
}

/**
 * Detail columns layout
 * Groups detail columns into logical sections
 */
export interface DetailColumnsType {
  [key: number]: DetailColumnType[]; // Column groups by section
}

/**
 * Application option structure
 * Represents an application that can be selected for app-scoped notices
 */
export interface AppOptionType {
  appCode: string; // Application code
  appName: string; // Application name
  appId: string; // Application identifier
  editionType: {
    value: any; // Edition type value
    message: string; // Edition type display text
  };
}
