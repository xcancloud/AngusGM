import { useI18n } from 'vue-i18n';
import { MessageReceiveType, MessageStatus } from '@/enums/enums';
import { ReceiveObjectType } from '@xcan-angus/infra';
import dayjs from 'dayjs';

/**
 * Status color mapping for message status badges
 * Maps status values to appropriate badge colors for visual feedback
 */
export const STATUS_COLOR_MAP: Record<string, string> = {
  PENDING: 'warning',
  SENT: 'success',
  FAILURE: 'error'
};

/**
 * Get status color for a given status key
 * Returns the appropriate color for status badge display
 * @param key - Status key to get color for
 * @returns Color string for the status badge
 */
export const getStatusColor = (key: string): string => {
  return STATUS_COLOR_MAP[key] || 'default';
};

/**
 * Get status text for a given status key
 * Returns the appropriate color for status badge display
 * @param key - Status key to get text for
 * @returns Color string for the status badge
 */
export const getStatusText = (key: string): string => {
  return getStatusColor(key);
};

/**
 * Generate search panel configuration options
 * Creates search field configurations with proper internationalization
 * @returns Array of search option configurations
 */
export const createSearchOptions = () => {
  const { t } = useI18n();

  return [
    {
      valueKey: 'title',
      allowClear: true,
      type: 'input' as const,
      placeholder: t('messages.placeholder.title')
    },
    {
      valueKey: 'receiveType',
      type: 'select-enum' as const,
      enumKey: MessageReceiveType,
      allowClear: true,
      placeholder: t('messages.placeholder.receiveType')
    },
    {
      valueKey: 'status',
      type: 'select-enum' as const,
      enumKey: MessageStatus,
      allowClear: true,
      placeholder: t('messages.placeholder.status')
    },
    {
      valueKey: 'receiveObjectType',
      type: 'select-enum' as const,
      enumKey: ReceiveObjectType,
      allowClear: true,
      placeholder: t('messages.placeholder.receiveObjectType')
    }
  ];
};

/**
 * Generate table column configuration
 * Creates table column definitions with proper internationalization
 * @returns Array of column configurations
 */
export const createTableColumns = () => {
  const { t } = useI18n();

  return [
    {
      title: t('messages.columns.title'),
      key: 'title',
      dataIndex: 'title',
      width: '17%'
    },
    {
      title: t('messages.columns.receiveType'),
      key: 'receiveType',
      dataIndex: 'receiveType',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('messages.columns.status'),
      key: 'status',
      dataIndex: 'status',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('messages.columns.failureReason'),
      key: 'failureReason',
      dataIndex: 'failureReason',
      width: '17%'
    },
    {
      title: t('messages.columns.createdByName'),
      key: 'createdByName',
      dataIndex: 'createdByName',
      width: '8%'
    },
    {
      title: t('messages.columns.receiveObjectType'),
      key: 'receiveObjectType',
      dataIndex: 'receiveObjectType',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('messages.columns.sentNum'),
      dataIndex: 'sentNum',
      key: 'sentNum',
      width: '8%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    },
    {
      title: t('messages.columns.readNum'),
      key: 'readNum',
      dataIndex: 'readNum',
      width: '8%'
    },
    {
      title: t('messages.columns.timingDate'),
      key: 'timingDate',
      dataIndex: 'timingDate',
      width: '10%',
      customCell: () => {
        return { style: 'white-space:nowrap;' };
      }
    }
  ];
};

/**
 * Generate grid column configuration for message detail
 * Creates grid layout configuration for displaying message information
 * @returns Array of grid column configurations
 */
export const createDetailGridColumns = () => {
  const { t } = useI18n();

  return [
    [
      {
        label: t('messages.columns.createdByName'),
        dataIndex: 'fullName'
      },
      {
        label: t('messages.columns.sendType'),
        dataIndex: 'sentType'
      },
      {
        label: t('messages.columns.receiveType'),
        dataIndex: 'receiveType'
      },
      {
        label: t('messages.columns.timingDate'),
        dataIndex: 'timingDate'
      }
    ],
    [
      {
        label: t('messages.columns.sentNum'),
        dataIndex: 'sentNum'
      },
      {
        label: t('messages.columns.readNum'),
        dataIndex: 'readNum'
      },
      {
        label: t('messages.columns.status'),
        dataIndex: 'status'
      }
    ]
  ];
};

/**
 * Check if recipient type should be excluded from selection
 * Filters out certain recipient types that are not selectable
 * @param item - Object containing value property
 * @returns Boolean indicating if the type should be excluded
 */
export const shouldExcludeRecipientType = ({ value }: { value: any }): boolean => {
  return [ReceiveObjectType.TO_POLICY, ReceiveObjectType.POLICY, ReceiveObjectType.ALL].includes(value);
};

/**
 * Format sent number for display
 * Handles special cases like negative values
 * @param value - The sent number value
 * @returns Formatted string for display
 */
export const formatSentNumber = (value: any): string => {
  return +value > -1 ? value : '--';
};

/**
 * Get recipient display name
 * Returns appropriate display name for recipient type
 * @param receiveTenantName - Tenant name if available
 * @returns Display name string
 */
export const getRecipientDisplayName = (receiveTenantName?: string): string => {
  return receiveTenantName || '';
};

/**
 * Generate range of numbers
 * Utility function for time picker options
 * @param start - Start number
 * @param end - End number
 * @returns Array of numbers from start to end
 */
export const generateRange = (start: number, end: number): number[] => {
  const result: number[] = [];
  for (let i = start; i < end; i++) {
    result.push(i);
  }
  return result;
};

/**
 * Check if date is in the past
 * Prevents scheduling messages in the past
 * @param current - Date to check
 * @returns Boolean indicating if date is in the past
 */
export const isPastDate = (current: any): boolean => {
  return current && current < dayjs();
};

/**
 * Get disabled time options for date picker
 * Prevents selecting past times for scheduled messages
 * @param currentTime - Current time plus one minute
 * @returns Object with disabled hours and minutes
 */
export const getDisabledTimeOptions = (currentTime: any) => {
  return {
    disabledHours: () => generateRange(0, currentTime.hour()),
    disabledMinutes: () => generateRange(0, currentTime.minute())
  };
};

/**
 * Build hierarchical tree structure from flat list
 * Converts flat department list to nested tree structure
 * @param treeData - Flat array of items with id and pid properties
 * @returns Nested tree structure
 */
export const buildTree = (treeData: any[]): any[] => {
  if (!treeData?.length) {
    return [];
  }

  const result: any[] = [];
  const itemMap: any = {};

  for (const item of treeData) {
    const id = item.id;
    const pid = item.pid;

    if (!itemMap[id]) {
      itemMap[id] = {
        children: []
      };
    }

    itemMap[id] = {
      ...item,
      children: itemMap[id].children
    };

    const treeItem = itemMap[id];

    if (pid === '-1') {
      result.push(treeItem);
    } else {
      if (!itemMap[pid]) {
        itemMap[pid] = {
          children: []
        };
      }
      itemMap[pid].children.push(treeItem);
    }
  }

  return result;
};
