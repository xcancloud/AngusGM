/**
 * SMS template configuration interface
 */
export interface Template {
  id: string; // Unique template identifier
  code: string; // Template code for identification
  name: string; // Template display name
  thirdCode: string; // Third-party service template code
  channelId: string; // Associated SMS channel ID
  signature: string; // SMS signature text
  content: string; // SMS template content
  subject: string; // SMS subject (if applicable)
  language: string; // Template language code
  verificationCode: boolean; // Whether template contains verification code
  verificationCodeValidSecond: string; // Verification code validity period in seconds
  showEdit: boolean; // Edit mode display state
  editValues: EditableTemplateValues; // Editable template values
}

/**
 * Editable template values interface
 */
export interface EditableTemplateValues {
  name: string; // Editable name
  thirdCode: string; // Editable third-party code
  language: string; // Editable language
  signature: string; // Editable signature
  content: string; // Editable content
}

/**
 * Generic options interface for select components
 */
export interface Options {
  label: string; // Display label
  value: string; // Option value
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  title: string;
  dataIndex: string;
  key: string;
  width?: string;
  align?: 'left' | 'center' | 'right';
}

/**
 * Query parameters interface for SMS templates
 */
export interface TemplateQueryParams {
  channelId: string | undefined
  language?: string;
  pageNo: number;
  pageSize: number;
}

/**
 * Pagination configuration interface
 */
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * Component state interface for main template component
 */
export interface TemplateState {
  dataSource: Template[];
  options: Options[];
}

/**
 * Validation result interface
 */
export interface ValidationResult {
  isValid: boolean;
  errors: string[];
}
