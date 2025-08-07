<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  AsyncComponent, ButtonAuth, Hints, Icon, IconCount, IconRefresh,
  Image, modal, notification, PureCard, SearchPanel, Table
} from '@xcan-angus/vue-ui';
import { app, appContext, GM, utils, Enabled, UserSource, SearchCriteria, PageQuery } from '@xcan-angus/infra';

import { User, UserState, SearchOption } from './PropsType';
import { user } from '@/api';

// Async component definitions for lazy loading
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UpdatePasswd = defineAsyncComponent(() => import('@/views/organization/user/components/password/index.vue'));
const Lock = defineAsyncComponent(() => import('@/components/Lock/index.vue'));

// Reactive data and state management
const { t } = useI18n();
const showCount = ref(true); // Controls statistics panel visibility
const loading = ref(false); // Loading state for table operations
const disabled = ref(false); // Disabled state for buttons during operations
const total = ref(0); // Total number of users for pagination
const userList = ref<User[]>([]); // User list data

// Pagination and search parameters
const params = ref<PageQuery>({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  fullTextSearch: true
});

// Modal state management
const state = reactive<UserState>({
  updatePasswdVisible: false, // Password reset modal visibility
  lockModalVisible: false, // User lock modal visibility
  currentUserId: undefined // Currently selected user ID
});

// Computed pagination object for table
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

/**
 * Check operation permissions based on user's system admin status
 * @param sysAdmin - Whether the user is a system administrator
 * @returns Whether the operation is disabled
 */
const getOperationPermissions = (sysAdmin: boolean): boolean => {
  return !appContext.isSysAdmin() && sysAdmin;
};

/**
 * Load user list from API
 * Handles loading state and error notifications
 */
const loadUserList = async (): Promise<void> => {
  try {
    loading.value = true;
    const [error, { data = { list: [], total: 0 } }] = await user.getUserList(params.value);

    if (error) {
      notification.error('加载用户列表失败');
      return;
    }

    userList.value = data.list;
    total.value = +data.total;
  } catch (error) {
    notification.error('加载用户列表失败');
  } finally {
    loading.value = false;
  }
};

/**
 * Initialize component data
 * Sets disabled state during initialization
 */
const init = async (): Promise<void> => {
  disabled.value = true;
  try {
    await loadUserList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle search criteria changes
 * Resets pagination and reloads data with new filters
 * @param data - Search criteria array
 */
const searchChange = async (data: SearchCriteria[]): Promise<void> => {
  params.value.pageNo = 1; // Reset to first page
  params.value.filters = data;
  disabled.value = true;
  try {
    await loadUserList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle table pagination, sorting, and filtering changes
 * @param _pagination - Pagination object
 * @param _filters - Filter object
 * @param sorter - Sorting object
 */
const tableChange = async (_pagination: any, _filters: any, sorter: any): Promise<void> => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;

  disabled.value = true;
  try {
    await loadUserList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Show confirmation modal for setting/canceling system administrator
 * @param id - User ID
 * @param name - User name
 * @param sysAdmin - Current system admin status
 */
const setAdminConfirm = (id: string, name: string, sysAdmin: boolean): void => {
  modal.confirm({
    centered: true,
    title: sysAdmin ? t('cancelAdministrator') : t('setupAdministrator'),
    content: sysAdmin ? t('userTip2', { name }) : t('userTip3', { name }),
    async onOk () {
      await updateSysAdmin(id, sysAdmin);
    }
  });
};

/**
 * Update user's system administrator status
 * @param id - User ID
 * @param sysAdmin - Current system admin status
 */
const updateSysAdmin = async (id: string, sysAdmin: boolean): Promise<void> => {
  try {
    const [error] = await user.updateUserSysAdmin({ id, sysAdmin: !sysAdmin });

    if (error) {
      notification.error('修改失败');
      return;
    }

    notification.success(t('common.messages.editSuccess'));
    await refreshUserList();
  } catch (error) {
    notification.error('修改失败');
  }
};

/**
 * Show confirmation modal for user deletion
 * @param id - User ID
 * @param name - User name
 */
const delUserConfirm = (id: string, name: string): void => {
  modal.confirm({
    centered: true,
    title: t('delUser'),
    content: t('userTip4', { name }),
    async onOk () {
      await delUser(id);
    }
  });
};

/**
 * Delete user from system
 * Handles pagination recalculation after deletion
 * @param id - User ID
 */
const delUser = async (id: string): Promise<void> => {
  try {
    const [error] = await user.deleteUser([id]);

    if (error) {
      notification.error('删除失败');
      return;
    }

    notification.success('删除成功');
    // Recalculate current page after deletion
    params.value.pageNo = utils.getCurrentPage(
      params.value.pageNo as number,
      params.value.pageSize as number,
      total.value
    );
    await refreshUserList();
  } catch (error) {
    notification.error('删除失败');
  }
};

/**
 * Show confirmation modal for enabling/disabling user
 * @param id - User ID
 * @param name - User name
 * @param enabled - Current enabled status
 */
const updateStatusConfirm = (id: string, name: string, enabled: boolean): void => {
  modal.confirm({
    centered: true,
    title: enabled ? t('disable') : t('enable'),
    content: enabled ? `确定禁用【${name}】吗？` : `确定启用【${name}】吗？`,
    async onOk () {
      await updateStatus(id, enabled);
    }
  });
};

/**
 * Toggle user enabled/disabled status
 * @param id - User ID
 * @param enabled - Current enabled status
 */
const updateStatus = async (id: string, enabled: boolean): Promise<void> => {
  try {
    const [error] = await user.toggleUserEnabled([{ id, enabled: !enabled }]);

    if (error) {
      notification.error(enabled ? '禁用失败' : '启用失败');
      return;
    }

    notification.success(enabled ? '禁用成功' : '启用成功');
    await refreshUserList();
  } catch (error) {
    notification.error(enabled ? '禁用失败' : '启用失败');
  }
};

/**
 * Open user lock modal
 * @param id - User ID
 */
const openLockedModal = (id: string): void => {
  state.currentUserId = id;
  state.lockModalVisible = true;
};

/**
 * Close user lock modal
 */
const closeLockModal = (): void => {
  state.lockModalVisible = false;
};

/**
 * Save lock changes and refresh user list
 */
const saveLock = async (): Promise<void> => {
  state.lockModalVisible = false;
  await refreshUserList();
};

/**
 * Unlock user with confirmation
 * @param id - User ID
 * @param name - User name
 */
const unlock = (id: string, name: string): void => {
  modal.confirm({
    centered: true,
    title: t('unlockUsers'),
    content: t('userTip7', { name }),
    async onOk () {
      try {
        const [error] = await user.toggleUserLocked({ id, locked: false });

        if (error) {
          notification.error('解锁失败');
          return;
        }

        notification.success('解锁成功');
        await refreshUserList();
      } catch (error) {
        notification.error('解锁失败');
      }
    }
  });
};

/**
 * Open password reset modal
 * @param id - User ID
 */
const openUpdatePasswdModal = (id: string): void => {
  state.currentUserId = id;
  state.updatePasswdVisible = true;
};

/**
 * Close password reset modal
 */
const closeUpdatePasswdModal = (): void => {
  state.updatePasswdVisible = false;
};

/**
 * Refresh user list with current parameters
 * Handles loading state during refresh
 */
const refreshUserList = async (): Promise<void> => {
  disabled.value = true;
  try {
    await loadUserList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle refresh button click
 * Prevents multiple simultaneous requests
 */
const handleRefresh = (): void => {
  if (loading.value) {
    return;
  }
  loadUserList();
};

// Search options configuration for SearchPanel component
const searchOptions = ref<SearchOption[]>([
  {
    placeholder: t('user.placeholder.userId'),
    valueKey: 'id',
    type: 'input',
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.search'),
    valueKey: 'fullName',
    type: 'input',
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.enabled'),
    valueKey: 'enabled',
    type: 'select-enum',
    enumKey: Enabled,
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.source'),
    valueKey: 'source',
    type: 'select-enum',
    enumKey: UserSource,
    allowClear: true
  },
  {
    valueKey: 'createdDate', // TODO 时间提示
    type: 'date-range',
    allowClear: true
  },
  {
    placeholder: t('user.placeholder.tag'),
    valueKey: 'tagId',
    type: 'select',
    action: `${GM}/org/tag`,
    fieldNames: { label: 'name', value: 'id' },
    showSearch: true,
    allowClear: true,
    lazy: false
  }
]);

// Table columns configuration with custom cell renderers
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.name'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    title: t('user.columns.username'),
    dataIndex: 'username',
    width: '12%'
  },
  {
    title: t('user.columns.status'),
    dataIndex: 'enabled',
    width: '6%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.source'),
    dataIndex: 'source',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.lockedStatus'),
    dataIndex: 'locked',
    width: '6%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.onlineStatus'),
    dataIndex: 'online',
    width: '6%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.mobile'),
    dataIndex: 'mobile',
    groupName: 'contact',
    width: '8%',
    customRender: ({ text }): string => text || '--',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.landline'),
    dataIndex: 'landline',
    groupName: 'contact',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.email'),
    dataIndex: 'email',
    groupName: 'contact',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '15%'
  },
  {
    title: t('user.columns.gender'),
    dataIndex: 'gender',
    groupName: 'other',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.identity'),
    dataIndex: 'sysAdmin',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.deptHead'),
    dataIndex: 'deptHead',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.title'),
    dataIndex: 'title',
    groupName: 'other',
    hide: true,
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.createdDate'),
    sorter: true,
    dataIndex: 'createdDate',
    groupName: 'date',
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.onlineDate'),
    dataIndex: 'onlineDate',
    groupName: 'date',
    hide: true,
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.createdByName'),
    dataIndex: 'createdByName',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('user.columns.lastModifiedByName'),
    dataIndex: 'lastModifiedByName',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('user.columns.lastModifiedDate'),
    dataIndex: 'lastModifiedDate',
    groupName: 'date',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 160,
    align: 'center'
  }
];

// Lifecycle hook - initialize component on mount
onMounted(() => {
  init();
});
</script>
<template>
  <PureCard class="w-full min-h-full p-3.5">
    <Statistics
      :visible="showCount"
      :barTitle="t('statistics.metrics.newUsers')"
      resource="User"
      dateType="YEAR"
      :router="GM" />
    <Hints :text="t('user.tip')" class="mb-1" />
    <div class="flex items-start my-2 justify-between">
      <SearchPanel
        class="flex-1 mr-2"
        :options="searchOptions"
        @change="searchChange" />
      <div class="flex items-center space-x-2">
        <ButtonAuth
          code="UserAdd"
          type="primary"
          href="/organization/user/add?source=home"
          icon="icon-tianjia" />
        <IconCount v-model:value="showCount" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="handleRefresh" />
      </div>
    </div>
    <Table
      :dataSource="userList"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'fullName'">
          <div class="flex items-center">
            <Image
              :src="record.avatar"
              type="avatar"
              class="w-5 h-5 rounded-full inline-block mr-1"
              style="min-width: 20px;" />
            <template v-if="app.has('UserDetail')">
              <RouterLink
                :to="`/organization/user/${record.id}`"
                class="text-theme-special text-theme-text-hover cursor-pointer">
                {{ text }}
              </RouterLink>
            </template>
            <span
              v-else
              :title="text"
              class="cursor-pointer">{{ text }}</span>
          </div>
        </template>
        <template v-if="column.dataIndex === 'enabled'">
          <Badge
            v-if="record.enabled"
            status="success"
            :text="t('common.status.enabled')" />
          <Badge
            v-else
            status="error"
            :text="t('common.status.disabled')" />
        </template>
        <template v-if="column.dataIndex === 'sysAdmin'">
          {{ record.sysAdmin ? t('user.profile.systemAdmin') : t('user.profile.generalUser') }}
        </template>
        <template v-if="column.dataIndex === 'deptHead'">
          {{ record.deptHead ? t('common.status.yes') : t('common.status.no') }}
        </template>
        <template v-if="column.dataIndex === 'locked'">
          <Badge
            v-if="record.locked"
            status="error"
            :text="t('common.status.locked')" />
          <Badge
            v-else
            status="success"
            :text="t('common.status.unlocked')" />
        </template>
        <template v-if="column.dataIndex === 'online'">
          <Badge
            v-if="record.online"
            status="success"
            :text="t('common.status.online')" />
          <Badge
            v-else
            status="error"
            :text="t('common.status.offline')" />
        </template>
        <template v-if="column.dataIndex === 'source'">
          {{ record.source?.message }}
        </template>
        <template v-if="column.dataIndex === 'gender'">
          {{ record.gender?.message }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <ButtonAuth
              code="UserModify"
              type="text"
              :href="`/organization/user/edit/${record.id}?source=home`"
              :disabled="getOperationPermissions(record.sysAdmin)"
              icon="icon-shuxie" />
            <ButtonAuth
              code="ResetPassword"
              type="text"
              :disabled="getOperationPermissions(record.sysAdmin)"
              icon="icon-zhongzhimima"
              @click="openUpdatePasswdModal(record.id)" />
            <Dropdown
              placement="bottomRight"
              overlayClassName="ant-dropdown-sm">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <MenuItem
                    v-if="app.show('UserEnable')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('UserEnable')"
                    @click="updateStatusConfirm(record.id,record.fullName,record.enabled)">
                    <template #icon>
                      <Icon :icon="record.enabled?'icon-jinyong1':'icon-qiyong'" />
                    </template>
                    {{ record.enabled ? app.getName('UserEnable', 1) : app.getName('UserEnable',0) }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('UserDelete')"
                    :disabled="getOperationPermissions(record.sysAdmin) || !app.has('UserDelete')"
                    @click="delUserConfirm(record.id,record.fullName)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('UserDelete') }}
                  </MenuItem>
                  <MenuItem
                    v-if="!record.locked && app.show('LockingUser')"
                    :disabled="getOperationPermissions(record.sysAdmin) || !app.has('LockingUser')"
                    @click="openLockedModal(record.id)">
                    <template #icon>
                      <Icon icon="icon-lock" />
                    </template>
                    {{ app.getName('LockingUser', 0) }}
                  </MenuItem>
                  <MenuItem
                    v-if="record.locked && app.show('LockingUser')"
                    :disabled="getOperationPermissions(record.sysAdmin) || !app.has('LockingUser')"
                    @click="unlock(record.id, record.fullName)">
                    <template #icon>
                      <Icon icon="icon-kaibiaojiemi" />
                    </template>
                    {{ app.getName('LockingUser', 1) }}
                  </MenuItem>
                  <MenuItem
                    v-if="appContext.isSysAdmin() && app.show('SetIdentity')"
                    :disabled="getOperationPermissions(record.sysAdmin) || !app.has('SetIdentity')"
                    @click="setAdminConfirm(record.id, record.fullName, record.sysAdmin)">
                    <template #icon>
                      <Icon :icon="record.sysAdmin ? 'icon-yonghu' : 'icon-guanliyuan'" />
                    </template>
                    {{ record.sysAdmin ? t('user.actions.cancelAdmin') : t('user.actions.setAdmin') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </div>
        </template>
      </template>
    </Table>
  </PureCard>
  <AsyncComponent :visible="state.updatePasswdVisible">
    <UpdatePasswd
      v-if="state.updatePasswdVisible"
      :visible="state.updatePasswdVisible"
      :userId="state.currentUserId"
      @cancel="closeUpdatePasswdModal" />
  </AsyncComponent>
  <AsyncComponent :visible="state.lockModalVisible">
    <Lock
      v-if="state.lockModalVisible"
      :id="state.currentUserId"
      :visible="state.lockModalVisible"
      :title="t('user.lockTitle')"
      :tip="t('common.messages.lockTip')"
      width="550px"
      :action="`${GM}/user/locked`"
      @cancel="closeLockModal"
      @save="saveLock()" />
  </AsyncComponent>
</template>
