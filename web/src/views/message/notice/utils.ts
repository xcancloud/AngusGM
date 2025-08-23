import { reactive } from 'vue';
import { NoticeScope, SentType } from '@/enums/enums';

/**
 * Get query parameters for notice list
 */
export const getQueryParams = (pagination: any, searchParams: any[]) => {
  const { pageSize, current } = pagination;
  return {
    pageSize,
    pageNo: current,
    filters: searchParams,
    fullTextSearch: true
  };
};

/**
 * Reset form to default values
 */
export const resetForm = (form: any, formRef: any) => {
  formRef.value.resetFields();
  form.content = '';
  form.scope = NoticeScope.GLOBAL;
  form.sendType = SentType.SEND_NOW;
  form.sendTimingDate = undefined;
  form.expirationDate = undefined;
  form.appCode = undefined;
  form.appName = undefined;
};

/**
 * Validate send timing date
 */
export const validateSendTime = (sendTimingDate: any, expirationDate: any, t: any) => {
  if (!sendTimingDate) {
    return Promise.reject(new Error(t('notification.messages.selectApplication')));
  }
  if (new Date(sendTimingDate as string) < new Date()) {
    return Promise.reject(new Error(t('notification.messages.sendTimeMoreThanNow')));
  }
  if (new Date(sendTimingDate as string) > new Date(expirationDate || '')) {
    return Promise.reject(new Error(t('notification.messages.sendTimeMoreExpiredDate')));
  }
  return Promise.resolve();
};

/**
 * Validate expiration date
 */
export const validateExpirationDate = (expirationDate: any, t: any) => {
  if (!expirationDate) {
    return Promise.reject(new Error(t('notification.messages.expiredDateRequired')));
  }
  if (new Date(expirationDate as string) < new Date()) {
    return Promise.reject(new Error(t('notification.messages.expiredDateMoreThanNow')));
  }
  return Promise.resolve();
};

/**
 * Handle scope change in form
 */
export const handleScopeChange = (item: string, form: any) => {
  if (item === NoticeScope.GLOBAL) {
    form.appCode = undefined;
    form.appName = undefined;
    form.appId = undefined;
    form.editionType = undefined;
  }
};

/**
 * Handle send type change in form
 */
export const handleSendTypeChange = (item: string, form: any) => {
  if (item === SentType.SEND_NOW) {
    form.sendTimingDate = undefined;
  }
};

/**
 * Handle app selection change
 */
export const handleAppChange = (_value: any, options: any, form: any) => {
  form.appCode = options.appCode;
  form.appName = options.appName;
  form.appId = options.appId;
  form.editionType = options.editionType.value;
};

/**
 * Handle date change for send timing
 */
export const handleDateChange = (value: string, form: any): void => {
  form.sendTimingDate = value;
};

/**
 * Handle expiration date change
 */
export const handleExpirationDate = (value: string, form: any): void => {
  form.expirationDate = value;
};

/**
 * Get form rules for notice form
 */
export const getFormRules = (t: any) => {
  return reactive({
    content: [
      { required: true, message: t('notification.messages.noticeContentRequired'), trigger: 'change' }
    ],
    sendTimingDate: [
      { required: true, validator: (_rule: any, value: any) => validateSendTime(value, undefined, t), type: 'string' }
    ],
    expirationDate: [
      { required: true, validator: (_rule: any, value: any) => validateExpirationDate(value, t), type: 'string' }
    ],
    appId: [
      { required: true, message: t('notification.messages.selectApplication'), type: 'string' }
    ],
    scope: [
      { required: true }
    ]
  });
};

/**
 * Get search options for notice list
 */
export const getSearchOptions = (t: any) => {
  return [
    {
      placeholder: t('notification.placeholder.content'),
      type: 'input',
      allowClear: true,
      valueKey: 'content'
    },
    {
      placeholder: t('notification.placeholder.scope'),
      type: 'select-enum',
      allowClear: true,
      valueKey: 'scope',
      enumKey: NoticeScope
    },
    {
      placeholder: t('notification.placeholder.sendType'),
      type: 'select-enum',
      allowClear: true,
      valueKey: 'sendType',
      enumKey: SentType
    }
  ];
};

/**
 * Get table columns for notice list
 */
export const getTableColumns = (t: any) => {
  return [
    {
      title: t('notification.columns.content'),
      key: 'content',
      dataIndex: 'content',
      ellipsis: true
    },
    {
      title: t('notification.columns.scope'),
      key: 'scope',
      dataIndex: 'scope',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('notification.columns.sendType'),
      key: 'sendType',
      dataIndex: 'sendType',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('notification.columns.timingDate'),
      key: 'timingDate',
      dataIndex: 'timingDate',
      width: '12%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('common.columns.createdByName'),
      key: 'createdByName',
      dataIndex: 'createdByName',
      width: '14%'
    },
    {
      title: t('common.columns.createdDate'),
      key: 'createdDate',
      dataIndex: 'createdDate',
      width: '12%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('common.actions.operation'),
      key: 'action',
      dataIndex: 'action',
      width: '5%',
      align: 'center'
    }
  ];
};

/**
 * Get detail columns for notice detail view
 */
export const getDetailColumns = (t: any) => {
  return [
    [
      {
        dataIndex: 'id',
        label: 'ID'
      },
      {
        dataIndex: 'sendType',
        label: t('notification.columns.sendType')
      },
      {
        dataIndex: 'createdByName',
        label: t('common.columns.createdByName')
      },
      {
        dataIndex: 'createdDate',
        label: t('common.columns.createdDate')
      }
    ],
    [
      {
        dataIndex: 'timingDate',
        label: t('notification.columns.timingDate')
      },
      {
        dataIndex: 'expirationDate',
        label: t('notification.columns.expiredDate')
      },
      {
        dataIndex: 'scope',
        label: t('notification.columns.scope')
      }
    ]
  ];
};
