import type { TableColumn, Template, EditableTemplateValues, PaginationConfig, ValidationResult } from './types';

/**
 * Process SMS templates and add edit state
 */
export const processSmsTemplates = (values: Template[]): Template[] => {
  if (!values.length) {
    return [];
  }

  return values.map((item) => ({
    ...item,
    showEdit: false,
    editValues: createDefaultEditValues(item)
  }));
};

/**
 * Create default edit values for a template
 */
export const createDefaultEditValues = (template: Template): EditableTemplateValues => ({
  name: template.name,
  thirdCode: template.thirdCode,
  language: template.language,
  signature: template.signature,
  content: template.content
});

/**
 * Enable edit mode for a template
 */
export const enableTemplateEdit = (record: Template): void => {
  record.showEdit = true;
  record.editValues = createDefaultEditValues(record);
};

/**
 * Cancel edit mode for a template
 */
export const cancelTemplateEdit = (record: Template): void => {
  record.showEdit = false;
};

/**
 * Validate template edit values
 */
export const validateTemplateEdit = (record: Template, t: (key: string) => string): ValidationResult => {
  const editValueKeys: (keyof EditableTemplateValues)[] = ['name', 'thirdCode', 'language', 'signature', 'content'];
  const errors: string[] = [];

  // Validate required fields
  editValueKeys.forEach(key => {
    if (!record.editValues[key]) {
      errors.push(`${getColumnTitle(key, t)}${t('sms.messages.isNull')}`);
    }
  });

  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * Get column title for a given key
 */
export const getColumnTitle = (key: keyof EditableTemplateValues, t: (key: string) => string): string => {
  const columnTitles: Record<keyof EditableTemplateValues, string> = {
    name: t('sms.columns.templateName'),
    thirdCode: t('sms.columns.thirdCode'),
    language: t('sms.columns.language'),
    signature: t('sms.columns.signature'),
    content: t('sms.columns.content')
  };

  return columnTitles[key] || key;
};

/**
 * Check if template values have changed
 */
export const hasTemplateChanges = (record: Template): boolean => {
  const editValueKeys: (keyof EditableTemplateValues)[] = ['name', 'thirdCode', 'language', 'signature', 'content'];

  return editValueKeys.some(key => record[key] !== record.editValues[key]);
};

/**
 * Update template with new values
 */
export const updateTemplateValues = (record: Template): void => {
  const { name, thirdCode, language, signature, content } = record.editValues;
  record.name = name;
  record.thirdCode = thirdCode;
  record.language = language;
  record.signature = signature;
  record.content = content;
  record.showEdit = false;
};

/**
 * Create pagination configuration object
 */
export const createPaginationConfig = (
  current: number,
  pageSize: number,
  total: number
): PaginationConfig => ({
  current,
  pageSize,
  total
});

/**
 * Create table columns configuration for SMS templates
 * Defines the structure and display properties for the SMS templates table
 */
export const createSmsTemplateColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('sms.columns.templateName'),
    dataIndex: 'name',
    key: 'name',
    width: '10%'
  },
  {
    title: t('sms.columns.code'),
    dataIndex: 'code',
    key: 'code',
    width: '15%'
  },
  {
    title: t('sms.columns.thirdCode'),
    dataIndex: 'thirdCode',
    key: 'thirdCode',
    width: '18%'
  },
  {
    title: t('sms.columns.language'),
    dataIndex: 'language',
    key: 'language',
    width: '6%'
  },
  {
    title: t('sms.columns.signature'),
    dataIndex: 'signature',
    key: 'signature',
    width: '8%'
  },
  {
    title: t('sms.columns.content'),
    dataIndex: 'content',
    key: 'content'
  },
  {
    title: t('sms.columns.operate'),
    key: 'operate',
    dataIndex: 'operate',
    width: '8%',
    align: 'center'
  }
];
