import { modal, notification } from '@xcan-angus/vue-ui';
import { user } from '@/api';
import { User, Detail } from './types';
import { PageQuery, SearchCriteria, Enabled, UserSource, utils } from '@xcan-angus/infra';
import { OrgTargetType } from '@/enums/enums';

/**
 * Load user list from API with error handling
 */
export const loadUserList = async (params: PageQuery): Promise<{ list: User[]; total: number }> => {
  try {
    const [error, { data = { list: [], total: 0 } }] = await user.getUserList(params);
    if (error) {
      notification.error('common.messages.queryListFailed');
      return { list: [], total: 0 };
    }
    return { list: data.list, total: +data.total };
  } catch (error) {
    notification.error('common.messages.networkError');
    return { list: [], total: 0 };
  }
};

/**
 * Update user's system administrator status
 */
export const updateSysAdmin = async (
  id: string,
  sysAdmin: boolean,
  t: (key: string) => string
): Promise<boolean> => {
  try {
    const [error] = await user.updateUserSysAdmin({ id, sysAdmin: !sysAdmin });
    if (error) {
      notification.error(t('common.messages.editFailed'));
      return false;
    }
    notification.success(t('common.messages.editSuccess'));
    return true;
  } catch (error) {
    notification.error(t('common.messages.editFailed'));
    return false;
  }
};

/**
 * Delete user from system
 */
export const deleteUser = async (
  id: string,
  t: (key: string) => string
): Promise<boolean> => {
  try {
    const [error] = await user.deleteUser([id]);
    if (error) {
      notification.error(t('user.messages.deleteFailed'));
      return false;
    }
    notification.success(t('user.messages.deleteSuccess'));
    return true;
  } catch (error) {
    notification.error(t('user.messages.deleteFailed'));
    return false;
  }
};

/**
 * Toggle user enabled/disabled status
 */
export const toggleUserStatus = async (
  id: string,
  enabled: boolean,
  t: (key: string) => string
): Promise<boolean> => {
  try {
    const [error] = await user.toggleUserEnabled([{ id, enabled: !enabled }]);
    if (error) {
      notification.error(t('common.messages.editFailed'));
      return false;
    }
    notification.success(t('common.messages.editSuccess'));
    return true;
  } catch (error) {
    notification.error(t('common.messages.editFailed'));
    return false;
  }
};

/**
 * Unlock user account
 */
export const unlockUser = async (
  id: string,
  t: (key: string) => string
): Promise<boolean> => {
  try {
    const [error] = await user.toggleUserLocked({ id, locked: false });
    if (error) {
      notification.error(t('common.messages.unlockFailed'));
      return false;
    }
    notification.success(t('common.messages.unlockSuccess'));
    return true;
  } catch (error) {
    notification.error(t('common.messages.unlockFailed'));
    return false;
  }
};

/**
 * Show confirmation modal for setting/canceling system administrator
 */
export const showAdminConfirm = (
  name: string,
  sysAdmin: boolean,
  t: (key: string, params?: { name: string }) => string,
  onConfirm: () => Promise<void>
): void => {
  modal.confirm({
    centered: true,
    title: sysAdmin ? t('user.actions.cancelAdmin') : t('user.actions.setAdmin'),
    content: sysAdmin
      ? t('common.messages.cancelAdminTip', { name })
      : t('common.messages.setAdminTip', { name }),
    async onOk () {
      await onConfirm();
    }
  });
};

/**
 * Show confirmation modal for user deletion
 */
export const showDeleteConfirm = (
  name: string,
  t: (key: string, params?: { name: string }) => string,
  onConfirm: () => Promise<void>
): void => {
  modal.confirm({
    centered: true,
    title: t('user.actions.deleteUser'),
    content: t('common.messages.confirmDelete', { name }),
    async onOk () {
      await onConfirm();
    }
  });
};

/**
 * Show confirmation modal for enabling/disabling user
 */
export const showStatusConfirm = (
  name: string,
  enabled: boolean,
  t: (key: string, params?: { name: string }) => string,
  onConfirm: () => Promise<void>
): void => {
  modal.confirm({
    centered: true,
    title: enabled ? t('common.status.disabled') : t('common.status.enabled'),
    content: enabled
      ? t('common.messages.confirmDisable', { name })
      : t('common.messages.confirmEnable', { name }),
    async onOk () {
      await onConfirm();
    }
  });
};

/**
 * Show confirmation modal for unlocking user
 */
export const showUnlockConfirm = (
  name: string,
  t: (key: string, params?: { name: string }) => string,
  onConfirm: () => Promise<void>
): void => {
  modal.confirm({
    centered: true,
    title: t('user.actions.unlockUser'),
    content: t('common.messages.unlockTip', { name }),
    async onOk () {
      await onConfirm();
    }
  });
};

/**
 * Calculate current page after deletion to handle edge cases
 */
export const calculateCurrentPage = (currentPage: number, pageSize: number, total: number): number => {
  return utils.getCurrentPage(currentPage, pageSize, total);
};

/**
 * Check operation permissions based on user's system admin status
 */
export const checkOperationPermissions = (sysAdmin: boolean, isCurrentUserSysAdmin: boolean): boolean => {
  return sysAdmin && !isCurrentUserSysAdmin;
};

/**
 * Handle search criteria changes
 */
export const handleSearchChange = (currentParams: PageQuery, newFilters: SearchCriteria[]): PageQuery => {
  return {
    ...currentParams,
    pageNo: 1, // Reset to first page when search criteria changes
    filters: newFilters
  };
};

/**
 * Handle table pagination, sorting, and filtering changes
 */
export const handleTableChange = (
  currentParams: PageQuery,
  pagination: { current: number; pageSize: number },
  sorter: { orderBy?: string; orderSort?: PageQuery.OrderSort }
): PageQuery => {
  return {
    ...currentParams,
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    orderBy: sorter.orderBy,
    orderSort: sorter.orderSort
  };
};

/**
 * Load user detail information from API
 */
export const loadUserDetail = async (userId: string): Promise<Detail | null> => {
  try {
    const [error, { data }] = await user.getUserDetail(userId);
    if (error) {
      return null;
    }
    return data;
  } catch (error) {
    notification.error('common.messages.queryFailed');
    return null;
  }
};

/**
 * Update user enabled/disabled status
 */
export const updateUserStatus = async (
  userDetail: Detail,
  t: (key: string) => string
): Promise<boolean> => {
  try {
    const params = [{ id: userDetail.id, enabled: !userDetail.enabled }];
    const [error] = await user.toggleUserEnabled(params);
    if (error) {
      return false;
    }

    notification.success(userDetail.enabled
      ? t('common.messages.disableSuccess')
      : t('common.messages.enableSuccess'));
    return true;
  } catch (error) {
    notification.error('common.messages.editFailed');
    return false;
  }
};

// ==================== Additional Detail Utils ====================

/**
 * Show confirmation modal for user deletion
 */
export const showDeleteUserConfirm = (
  userName: string,
  t: (key: string, params?: { name: string }) => string,
  onConfirm: () => Promise<void>
): void => {
  modal.confirm({
    centered: true,
    title: t('user.actions.deleteUser'),
    content: t('common.messages.confirmDelete', { name: userName }),
    async onOk () {
      await onConfirm();
    }
  });
};

/**
 * Check if current user can modify system administrator accounts
 */
export const canModifyUser = (userDetail: Detail | undefined, isSysAdmin: boolean): boolean => {
  return !isSysAdmin && userDetail?.sysAdmin !== true;
};

/**
 * Handle user lock operations
 */
export const handleUserLock = (
  locked: boolean,
  visible: { value: boolean },
  onUnlock: () => void
): void => {
  if (locked) {
    visible.value = true; // Show lock modal
  } else {
    onUnlock(); // Direct unlock
  }
};

/**
 * Close lock modal and refresh user data
 */
export const closeLockModal = (
  visible: { value: boolean },
  onRefresh: () => void
): void => {
  visible.value = false;
  onRefresh();
};

/**
 * Open password reset modal
 */
export const openPasswordResetModal = (state: { updatePasswdVisible: boolean }): void => {
  state.updatePasswdVisible = true;
};

/**
 * Close password reset modal
 */
export const closePasswordResetModal = (state: { updatePasswdVisible: boolean }): void => {
  state.updatePasswdVisible = false;
};

// ==================== Table Columns Configuration ====================

/**
 * Create user grid columns configuration
 */
export const createUserGridColumns = (t: (key: string) => string) => [
  [
    {
      label: t('user.columns.username'),
      dataIndex: 'username'
    },
    {
      label: t('user.columns.createdDate'),
      dataIndex: 'createdDate'
    },
    {
      label: t('user.columns.onlineDate'),
      dataIndex: 'onlineDate'
    }
  ]
];

/**
 * Create user tag table columns configuration
 */
export const createUserTagColumns = (t: (key: string) => string) => [
  {
    title: 'ID',
    dataIndex: 'tagId',
    key: 'tagId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('tag.columns.userTag.name'),
    dataIndex: 'tagName',
    key: 'tagName',
    width: '30%'
  },
  {
    title: t('tag.columns.userTag.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('tag.columns.userTag.creator'),
    dataIndex: 'creator',
    key: 'creator',
    width: '20%'
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    key: 'action',
    width: '10%',
    align: 'center' as const
  }
];

/**
 * Create user department table columns configuration
 */
export const createUserDeptColumns = (t: (key: string) => string) => [
  {
    title: 'ID',
    dataIndex: 'deptId',
    key: 'deptId',
    width: '15%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('department.columns.userDept.name'),
    dataIndex: 'deptName',
    key: 'deptName'
  },
  {
    title: t('department.columns.userDept.code'),
    dataIndex: 'deptCode',
    key: 'deptCode',
    width: '20%'
  },
  {
    title: t('department.columns.userDept.head'),
    dataIndex: 'deptHead',
    key: 'deptHead',
    customRender: ({ text }: { text: boolean }): string => text ? t('common.status.yes') : t('common.status.no'),
    width: '8%'
  },
  {
    title: t('department.columns.userDept.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('department.columns.userDept.creator'),
    dataIndex: 'creator',
    key: 'creator',
    width: '13%'
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    key: 'action',
    width: '6%',
    align: 'center' as const
  }
];

/**
 * Create user group table columns configuration
 */
export const createUserGroupColumns = (t: (key: string) => string) => [
  {
    title: 'ID',
    dataIndex: 'groupId',
    key: 'groupId',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('group.columns.assocGroup.name'),
    dataIndex: 'groupName',
    key: 'groupName',
    width: '15%'
  },
  {
    title: t('group.columns.assocGroup.code'),
    dataIndex: 'groupCode',
    key: 'groupCode',
    width: '15%'
  },
  {
    title: t('group.columns.assocGroup.remark'),
    dataIndex: 'groupRemark',
    key: 'groupRemark'
  },
  {
    title: t('group.columns.assocGroup.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('group.columns.assocGroup.creator'),
    dataIndex: 'creator',
    key: 'creator',
    width: '13%'
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    key: 'action',
    width: '6%',
    align: 'center' as const
  }
];

/**
 * Create search options configuration for SearchPanel component
 */
export const createSearchOptions = (
  t: (key: string, params?: { name: string }) => string,
  GM: string
) => [
  {
    placeholder: t('user.placeholder.userId'),
    valueKey: 'id',
    type: 'input' as const,
    op: 'EQUAL' as const,
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.search'),
    valueKey: 'fullName',
    type: 'input' as const,
    allowClear: true
  },
  {
    placeholder: t('common.status.validStatus'),
    valueKey: 'enabled',
    type: 'select-enum' as const,
    enumKey: Enabled,
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.source'),
    valueKey: 'source',
    type: 'select-enum' as const,
    enumKey: UserSource,
    allowClear: true
  },
  {
    valueKey: 'createdDate',
    type: 'date-range' as const,
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.tag'),
    valueKey: 'tagId',
    type: 'select' as const,
    action: `${GM}/org/tag`,
    fieldNames: { label: 'name', value: 'id' },
    showSearch: true,
    allowClear: true,
    lazy: false
  }
];

/**
 * Create table columns configuration
 */
export const createTableColumns = (t: (key: string) => string) => [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.name'),
    dataIndex: 'fullName',
    key: 'fullName',
    ellipsis: true,
    width: '16%'
  },
  {
    title: t('user.columns.username'),
    dataIndex: 'username',
    key: 'username',
    width: '12%'
  },
  {
    title: t('user.columns.status'),
    dataIndex: 'enabled',
    key: 'enabled',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.lockedStatus'),
    dataIndex: 'locked',
    key: 'locked',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.onlineStatus'),
    dataIndex: 'online',
    key: 'online',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.mobile'),
    dataIndex: 'mobile',
    key: 'mobile',
    groupName: 'contact',
    width: '10%',
    customRender: ({ text }: { text: string }): string => text || '--',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.landline'),
    dataIndex: 'landline',
    key: 'landline',
    groupName: 'contact',
    customRender: ({ text }: { text: string }): string => text || '--',
    hide: true,
    width: '10%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.email'),
    dataIndex: 'email',
    key: 'email',
    groupName: 'contact',
    customRender: ({ text }: { text: string }): string => text || '--',
    hide: true,
    width: '10%'
  },
  {
    title: t('user.columns.title'),
    dataIndex: 'title',
    key: 'title',
    groupName: 'other',
    customRender: ({ text }: { text: string }): string => text || '--',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.source'),
    dataIndex: 'source',
    key: 'source',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.gender'),
    dataIndex: 'gender',
    key: 'gender',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.identity'),
    dataIndex: 'sysAdmin',
    key: 'sysAdmin',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.deptHead'),
    dataIndex: 'deptHead',
    key: 'deptHead',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    sorter: true,
    groupName: 'date',
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.onlineDate'),
    dataIndex: 'onlineDate',
    key: 'onlineDate',
    groupName: 'date',
    hide: true,
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.creator'),
    dataIndex: 'creator',
    key: 'creator',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }: { text: string }): string => text || '--'
  },
  {
    title: t('user.columns.modifier'),
    dataIndex: 'modifier',
    key: 'modifier',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }: { text: string }): string => text || '--'
  },
  {
    title: t('user.columns.modifiedDate'),
    dataIndex: 'modifiedDate',
    key: 'modifiedDate',
    groupName: 'date',
    customRender: ({ text }: { text: string }): string => text || '--',
    hide: true,
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    key: 'action',
    width: 160,
    align: 'center' as const
  }
];

/**
 * Factory to create i18n-aware columns inside component setup.
 * Must be called within Vue component `setup` where `t` is available.
 */
export const createAuthPolicyColumns = (t: (key: string) => string, orgTargetType?: OrgTargetType) => {
  const columns = [
    {
      title: t('permission.columns.assocPolicies.id'),
      dataIndex: 'id',
      width: '15%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    },
    {
      title: t('permission.columns.assocPolicies.name'),
      dataIndex: 'name',
      width: '15%'
    },
    {
      title: t('permission.columns.assocPolicies.code'),
      dataIndex: 'code'
    }
  ] as const;

  // Conditionally include orgType column: exclude when target type is DEPT or GROUP
  const result: any[] = [...(columns as unknown as any[])];
  const shouldHideOrgType = orgTargetType === OrgTargetType.DEPT || orgTargetType === OrgTargetType.GROUP;
  if (!shouldHideOrgType) {
    result.push({
      title: t('permission.columns.assocPolicies.source'),
      dataIndex: 'orgType',
      width: '15%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    });
  }

  result.push(
    {
      title: t('permission.columns.assocPolicies.authByName'),
      dataIndex: 'authByName',
      width: '15%'
    },
    {
      title: t('permission.columns.assocPolicies.authDate'),
      dataIndex: 'authDate',
      width: '13%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    },
    {
      title: t('common.actions.operation'),
      dataIndex: 'action',
      width: '10%',
      align: 'center' as const
    }
  );

  return result;
};
