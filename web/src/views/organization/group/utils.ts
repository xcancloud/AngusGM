import { PageQuery, SearchCriteria, utils } from '@xcan-angus/infra';
import { modal, notification } from '@xcan-angus/vue-ui';
import { group, auth } from '@/api';
import type { Ref } from 'vue';

/**
 * Add new group
 */
export const addGroup = async (
  formState: any,
  t: (key: string) => string
) => {
  const [error] = await group.addGroup([formState]);
  if (error) {
    return false;
  }
  notification.success(t('common.messages.addSuccess'));
  return true;
};

/**
 * Edit existing group
 */
export const editGroup = async (
  groupId: string,
  formState: any,
  oldFormState: any,
  t: (key: string) => string
) => {
  const isEqual = utils.deepCompare(oldFormState, formState);
  if (isEqual) {
    return false;
  }

  const [error] = await group.replaceGroup([{ id: groupId, ...formState }]);
  if (error) {
    return false;
  }
  notification.success(t('common.messages.editSuccess'));
  return true;
};

/**
 * Update group status with confirmation dialog
 */
export const updateStatusConfirm = (
  id: string,
  name: string,
  enabled: boolean,
  t: (key: string, params?: any) => string,
  params?: Ref<PageQuery>,
  total?: Ref<number>,
  loadGroupList?: () => Promise<void>,
  disabled?: Ref<boolean>,
  loadGroupDetail?: () => Promise<void>
) => {
  modal.confirm({
    centered: true,
    title: enabled ? t('common.actions.disable') : t('common.actions.enable'),
    content: enabled ? t('common.messages.confirmDisable', { name: name }) : t('common.messages.confirmEnable', { name: name }),
    async onOk () {
      await updateStatus(id, enabled, t, params, total, loadGroupList, disabled, loadGroupDetail);
    }
  });
};

/**
 * Update group status
 */
export const updateStatus = async (
  id: string,
  enabled: boolean,
  t: (key: string) => string,
  params?: Ref<PageQuery>,
  total?: Ref<number>,
  loadGroupList?: () => Promise<void>,
  disabled?: Ref<boolean>,
  loadGroupDetail?: () => Promise<void>
) => {
  const statusParams = [{ id: id, enabled: !enabled }];
  const [error] = await group.toggleGroupEnabled(statusParams);
  if (error) {
    return;
  }
  notification.success(enabled ? t('common.messages.disableSuccess') : t('common.messages.enableSuccess'));

  if (params && total && loadGroupList && disabled) {
    params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
    disabled.value = true;
    await loadGroupList();
    disabled.value = false;
  }

  if (loadGroupDetail) {
    await loadGroupDetail();
  }

  return true;
};

/**
 * Delete group with confirmation dialog
 */
export const delGroupConfirm = (
  id: string,
  name: string,
  t: (key: string, params?: any) => string,
  params: Ref<PageQuery>,
  total: Ref<number>,
  loadGroupList: () => Promise<void>,
  disabled: Ref<boolean>
) => {
  modal.confirm({
    centered: true,
    title: t('common.actions.delete'),
    content: t('common.messages.confirmDelete', { name: name }),
    async onOk () {
      await delGroup(id, t, params, total, loadGroupList, disabled);
    }
  });
};

/**
 * Delete group
 */
export const delGroup = async (
  id: string,
  t: (key: string) => string,
  params: Ref<PageQuery>,
  total: Ref<number>,
  loadGroupList: () => Promise<void>,
  disabled: Ref<boolean>
) => {
  const [error] = await group.deleteGroup([id]);
  if (error) {
    return;
  }
  notification.success(t('common.messages.deleteSuccess'));

  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

/**
 * Load group list with pagination and filters
 */
export const loadGroupList = async (
  params: PageQuery,
  loading: Ref<boolean>,
  groupList: Ref<any[]>,
  total: Ref<number>
): Promise<void> => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await group.getGroupList(params);
  loading.value = false;
  if (error) {
    return;
  }
  groupList.value = data.list;
  total.value = +data.total;
};

/**
 * Handle search criteria changes
 */
export const searchChange = async (
  data: SearchCriteria[],
  params: Ref<PageQuery>,
  loadGroupList: () => Promise<void>,
  disabled: Ref<boolean>
) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

/**
 * Handle table pagination and sorting changes
 */
export const tableChange = async (
  _pagination: any,
  _filters: any,
  sorter: any,
  params: Ref<PageQuery>,
  loadGroupList: () => Promise<void>,
  disabled: Ref<boolean>
) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

/**
 * Add users to group
 */
export const addGroupUser = async (
  groupId: string,
  userIds: string[],
  updateLoading: Ref<boolean>
) => {
  updateLoading.value = true;
  await group.addGroupUser(groupId, userIds);
  updateLoading.value = false;
};

/**
 * Delete users from group
 */
export const delGroupUser = async (
  groupId: string,
  userIds: string[],
  updateLoading: Ref<boolean>
) => {
  updateLoading.value = true;
  await group.deleteGroupUser(groupId, userIds);
  updateLoading.value = false;
};

/**
 * Load group detail by ID
 */
export const loadGroupDetail = async (groupId: string): Promise<any> => {
  const [error, { data }] = await group.getGroupDetail(groupId);
  if (error) {
    return null;
  }
  return data;
};

/**
 * Load group tag list
 */
export const loadGroupTagList = async (groupId: string, params: any): Promise<number> => {
  const [error, { data }] = await group.getGroupTag(groupId, params);
  if (error) {
    return 0;
  }
  return +data?.total || 0;
};

/**
 * Get group policy list
 */
export const getGroupPolicy = async (
  groupId: string,
  params: PageQuery,
  loading: Ref<boolean>,
  disabled: Ref<boolean>,
  dataList: Ref<any[]>,
  total: Ref<number>
): Promise<void> => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  disabled.value = true;
  const [error, { data = { list: [], total: 0 } }] = await auth.getGroupPolicy(groupId, params);
  loading.value = false;
  disabled.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = data.total;
};

/**
 * Add policies to group
 */
export const addGroupPolicy = async (
  groupId: string,
  addIds: string[],
  updateLoading: Ref<boolean>
) => {
  updateLoading.value = true;
  const [error] = await auth.addGroupPolicy(groupId, addIds);
  updateLoading.value = false;
  if (error) {
    return false;
  }
  return true;
};

/**
 * Delete group policy
 */
export const deleteGroupPolicy = async (
  groupId: string,
  policyId: string,
  cancelLoading: Ref<boolean>
) => {
  if (cancelLoading.value) {
    return false;
  }
  cancelLoading.value = true;
  const [error] = await auth.deleteGroupPolicy(groupId, [policyId]);
  cancelLoading.value = false;
  if (error) {
    return false;
  }
  return true;
};

/**
 * Load group user list
 */
export const loadGroupUserList = async (
  groupId: string,
  params: PageQuery,
  loading: Ref<boolean>,
  userList: Ref<any[]>,
  total: Ref<number>,
  count: Ref<number>,
  isCountUpdate: Ref<boolean>
): Promise<void> => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await group.getGroupUser(groupId, params);
  loading.value = false;
  if (error) {
    return;
  }
  userList.value = data?.list || [];
  total.value = +data?.total;
  if (isCountUpdate.value) {
    count.value = +data.total;
  }
};

/**
 * Create search options for group list
 */
export const createSearchOptions = (t: (key: string) => string, Enabled: any, GM: string) => [
  {
    placeholder: t('group.placeholder.id'),
    valueKey: 'id',
    type: 'input' as const,
    op: 'EQUAL' as const,
    allowClear: true
  },
  {
    placeholder: t('group.placeholder.name'),
    valueKey: 'name',
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
    placeholder: t('tag.placeholder.name'),
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
 * Create table columns for group list
 */
export const createGroupColumns = (t: (key: string) => string) => [
  {
    key: 'id',
    title: 'ID',
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'name',
    title: t('common.columns.name'),
    dataIndex: 'name',
    width: '18%'
  },
  {
    key: 'code',
    title: t('common.columns.code'),
    dataIndex: 'code'
  },
  {
    key: 'enabled',
    title: t('common.status.validStatus'),
    dataIndex: 'enabled',
    width: '7%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'source',
    title: t('common.labels.source'),
    dataIndex: 'source',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'userNum',
    title: t('group.columns.userNum'),
    dataIndex: 'userNum',
    width: '7%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'createdByName',
    title: t('common.columns.createdByName'),
    dataIndex: 'createdByName',
    width: '10%'
  },
  {
    key: 'createdDate',
    title: t('common.columns.createdDate'),
    sorter: true,
    dataIndex: 'createdDate',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 130,
    align: 'center' as const
  }
];

/**
 * Create table columns for group user list
 */
export const createGroupUserColumns = (t: (key: string) => string) => [
  {
    key: 'userId',
    title: t('user.columns.assocUser.id'),
    dataIndex: 'userId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'fullName',
    title: t('user.columns.assocUser.name'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    key: 'createdByName',
    title: t('user.columns.assocUser.createdByName'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    key: 'createdDate',
    title: t('user.columns.assocUser.createdDate'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: '15%',
    align: 'center' as const
  }
];
