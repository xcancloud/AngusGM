<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  AsyncComponent, ButtonAuth, Hints, Icon, IconCount, IconRefresh, Image, PureCard, SearchPanel, Table
} from '@xcan-angus/vue-ui';
import { app, appContext, GM, SearchCriteria, PageQuery, Gender, enumUtils } from '@xcan-angus/infra';

import { User, UserState } from './types';
import {
  loadUserList, updateSysAdmin,
  deleteUser, toggleUserStatus, unlockUser, showAdminConfirm, showDeleteConfirm, showStatusConfirm,
  showUnlockConfirm, calculateCurrentPage, checkOperationPermissions, handleSearchChange,
  handleTableChange, createSearchOptions, createTableColumns
} from './utils';
import { ChartType, DateRangeType } from '@/components/Dashboard/enums';

/**
 * Async component definitions for lazy loading
 * These components are loaded only when needed to improve initial page load performance
 */
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const Lock = defineAsyncComponent(() => import('@/components/Lock/index.vue'));
const UpdatePasswd = defineAsyncComponent(() => import('@/views/organization/user/password/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/Dashboard/Dashboard.vue'));

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive state management for component UI and data
 */
const showCount = ref(true); // Controls statistics panel visibility
const loading = ref(false); // Loading state for table operations
const disabled = ref(false); // Disabled state for buttons during operations
const total = ref(0); // Total number of users for pagination
const userList = ref<User[]>([]); // User list data

/**
 * Pagination and search parameters
 * Manages current page, page size, filters, and search criteria
 */
const params = ref<PageQuery>({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  fullTextSearch: true
});

/**
 * Modal state management
 * Controls visibility and data for various modals in the component
 */
const state = reactive<UserState>({
  updatePasswdVisible: false, // Password reset modal visibility
  lockModalVisible: false, // User lock modal visibility
  currentUserId: undefined // Currently selected user ID for modal operations
});

/**
 * Computed pagination object for table component
 * Provides reactive pagination data to the table
 */
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

/**
 * Load user list from API with error handling
 * Handles loading state and displays error notifications on failure
 */
const loadUserListData = async (): Promise<void> => {
  try {
    loading.value = true;
    const { list, total: totalCount } = await loadUserList(params.value);
    userList.value = list;
    total.value = totalCount;
  } catch (error) {
    console.error('Failed to load user list:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Initialize component data
 * Sets disabled state during initialization to prevent user interaction
 */
const init = async (): Promise<void> => {
  disabled.value = true;
  try {
    await loadUserListData();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle search criteria changes from SearchPanel component
 * Resets pagination to first page and reloads data with new filters
 */
const searchChange = async (data: SearchCriteria[]): Promise<void> => {
  params.value = handleSearchChange(params.value, data);
  disabled.value = true;
  try {
    await loadUserListData();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle table pagination, sorting, and filtering changes
 * Updates parameters and reloads data based on table interactions
 */
const tableChange = async (_pagination: any, _filters: any, sorter: any): Promise<void> => {
  params.value = handleTableChange(params.value, _pagination, sorter);
  disabled.value = true;
  try {
    await loadUserListData();
  } finally {
    disabled.value = false;
  }
};

/**
 * Show confirmation modal for setting/canceling system administrator
 * Provides user confirmation before changing system admin status
 */
const setAdminConfirm = (id: string, name: string, sysAdmin: boolean): void => {
  showAdminConfirm(name, sysAdmin, t, async () => {
    const success = await updateSysAdmin(id, sysAdmin, t);
    if (success) {
      await refreshUserList();
    }
  });
};

/**
 * Show confirmation modal for user deletion
 * Provides user confirmation before deleting a user account
 */
const delUserConfirm = (id: string, name: string): void => {
  showDeleteConfirm(name, t, async () => {
    const success = await deleteUser(id, t);
    if (success) {
      // Recalculate current page after deletion to handle edge cases
      params.value.pageNo = calculateCurrentPage(
        params.value.pageNo as number,
        params.value.pageSize as number,
        total.value
      );
      await refreshUserList();
    }
  });
};

/**
 * Show confirmation modal for enabling/disabling user
 * Provides user confirmation before changing user enabled status
 */
const updateStatusConfirm = (id: string, name: string, enabled: boolean): void => {
  showStatusConfirm(name, enabled, t, async () => {
    const success = await toggleUserStatus(id, enabled, t);
    if (success) {
      await refreshUserList();
    }
  });
};

/**
 * Open user lock modal
 * Sets the current user ID and shows the lock modal
 */
const openLockedModal = (id: string): void => {
  state.currentUserId = id;
  state.lockModalVisible = true;
};

/**
 * Close user lock modal
 * Hides the lock modal and resets state
 */
const closeLockModal = (): void => {
  state.lockModalVisible = false;
};

/**
 * Save lock changes and refresh user list
 * Called when lock modal is successfully saved
 */
const saveLock = async (): Promise<void> => {
  state.lockModalVisible = false;
  await refreshUserList();
};

/**
 * Unlock user with confirmation
 * Shows confirmation dialog before unlocking a user
 */
const unlock = (id: string, name: string): void => {
  showUnlockConfirm(name, t, async () => {
    const success = await unlockUser(id, t);
    if (success) {
      await refreshUserList();
    }
  });
};

/**
 * Open password reset modal
 * Sets the current user ID and shows the password reset modal
 */
const openUpdatePasswdModal = (id: string): void => {
  state.currentUserId = id;
  state.updatePasswdVisible = true;
};

/**
 * Close password reset modal
 * Hides the password reset modal and resets state
 */
const closeUpdatePasswdModal = (): void => {
  state.updatePasswdVisible = false;
};

/**
 * Refresh user list with current parameters
 * Handles loading state during refresh operation
 */
const refreshUserList = async (): Promise<void> => {
  disabled.value = true;
  try {
    await loadUserListData();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle refresh button click
 * Prevents multiple simultaneous requests by checking loading state
 */
const handleRefresh = (): void => {
  if (loading.value) {
    return;
  }
  loadUserListData();
};

/**
 * Search options configuration for SearchPanel component
 * Defines available search fields and their properties
 */
const searchOptions = computed(() => createSearchOptions(t, GM));

/**
 * Table columns configuration with custom cell renderers
 * Defines the structure and behavior of each table column
 */
const columns = computed(() => createTableColumns(t));

const dashboardConfig = {
  charts: [
    {
      type: ChartType.LINE,
      title: t('statistics.metrics.newUsers'),
      field: 'created_date'
    },
    {
      type: ChartType.PIE,
      title: [t('common.status.validStatus'), t('common.status.lockStatus'), t('user.profile.gender'), t('user.profile.identity')],
      field: ['enabled', 'locked', 'gender', 'sys_admin'],
      enumKey: [
        [{ value: 0, message: t('common.status.disabled') }, { value: 1, message: t('common.status.enabled') }],
        [{ value: 0, message: t('common.status.unlocked') }, { value: 1, message: t('common.status.locked') }],
        enumUtils.enumToMessages(Gender),
        [{ value: 0, message: t('user.profile.generalUser') }, { value: 1, message: t('user.profile.systemAdmin') }]
      ],
      legendPosition: ['bottom', 'bottom', 'bottom', 'bottom']
    }
  ],
  layout: {
    cols: 2,
    gap: 16
  }
};

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial user data when component is mounted
 */
onMounted(() => {
  init();
});
</script>
<template>
  <PureCard class="w-full min-h-full p-3.5">
    <!-- Statistics panel showing user metrics -->
    <Dashboard
      v-show="showCount"
      class="py-3"
      :config="dashboardConfig"
      :apiRouter="GM"
      resource="User"
      :dateType="DateRangeType.YEAR"
      :showChartParam="true" />

    <!-- User management hints and tips -->
    <Hints :text="t('user.tip')" class="mb-1" />

    <!-- Search and action toolbar -->
    <div class="flex items-start my-2 justify-between">
      <!-- Search panel for filtering users -->
      <SearchPanel
        class="flex-1 mr-2"
        :options="searchOptions"
        @change="searchChange" />

      <!-- Action buttons: Add user, toggle statistics, refresh -->
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

    <!-- User data table -->
    <Table
      :dataSource="userList"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      rowKey="id"
      size="small"
      @change="tableChange">
      <!-- Custom cell renderers for table columns -->
      <template #bodyCell="{ column,text, record }">
        <!-- User name with avatar and link to detail page -->
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

        <!-- User enabled/disabled status badge -->
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

        <!-- System admin status display -->
        <template v-if="column.dataIndex === 'sysAdmin'">
          {{ record.sysAdmin ? t('user.profile.systemAdmin') : t('user.profile.generalUser') }}
        </template>

        <!-- Department head status display -->
        <template v-if="column.dataIndex === 'deptHead'">
          {{ record.deptHead ? t('common.status.yes') : t('common.status.no') }}
        </template>

        <!-- User locked status badge -->
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

        <!-- User online status badge -->
        <template v-if="column.dataIndex === 'online'">
          <Badge
            v-if="record.online"
            status="success"
            :text="t('common.status.online')" />
          <Badge
            v-else
            status="default"
            :text="t('common.status.offline')" />
        </template>

        <!-- User source display -->
        <template v-if="column.dataIndex === 'source'">
          {{ record.source?.message }}
        </template>

        <!-- User gender display -->
        <template v-if="column.dataIndex === 'gender'">
          {{ record.gender?.message }}
        </template>

        <!-- Action buttons for each user row -->
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <!-- Edit user button -->
            <ButtonAuth
              code="UserModify"
              type="text"
              :href="`/organization/user/edit/${record.id}?source=home`"
              :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin())"
              icon="icon-shuxie" />

            <!-- Reset password button -->
            <ButtonAuth
              code="ResetPassword"
              type="text"
              :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin())"
              icon="icon-zhongzhimima"
              @click="openUpdatePasswdModal(record.id)" />

            <!-- Dropdown menu for additional actions -->
            <Dropdown
              placement="bottomRight"
              overlayClassName="ant-dropdown-sm">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <!-- Enable/Disable user menu item -->
                  <MenuItem
                    v-if="app.show('UserEnable')"
                    :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin()) || !app.has('UserEnable')"
                    @click="updateStatusConfirm(record.id,record.fullName,record.enabled)">
                    <template #icon>
                      <Icon :icon="record.enabled?'icon-jinyong':'icon-qiyong'" />
                    </template>
                    {{ record.enabled ? t('common.status.disabled') : t('common.status.enabled') }}
                  </MenuItem>

                  <!-- Delete user menu item -->
                  <MenuItem
                    v-if="app.show('UserDelete')"
                    :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin()) || !app.has('UserDelete')"
                    @click="delUserConfirm(record.id,record.fullName)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ t('common.actions.delete') }}
                  </MenuItem>

                  <!-- Lock user menu item -->
                  <MenuItem
                    v-if="!record.locked && app.show('LockingUser')"
                    :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin()) || !app.has('LockingUser')"
                    @click="openLockedModal(record.id)">
                    <template #icon>
                      <Icon icon="icon-lock" />
                    </template>
                    {{ t('user.actions.lockUser') }}
                  </MenuItem>

                  <!-- Unlock user menu item -->
                  <MenuItem
                    v-if="record.locked && app.show('LockingUser')"
                    :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin()) || !app.has('LockingUser')"
                    @click="unlock(record.id, record.fullName)">
                    <template #icon>
                      <Icon icon="icon-jiesuo" />
                    </template>
                    {{ t('user.actions.unlockUser') }}
                  </MenuItem>

                  <!-- Set/Cancel system admin menu item -->
                  <MenuItem
                    v-if="appContext.isSysAdmin() && app.show('SetIdentity')"
                    :disabled="checkOperationPermissions(record.sysAdmin, appContext.isSysAdmin()) || !app.has('SetIdentity')"
                    @click="setAdminConfirm(record.id, record.fullName, record.sysAdmin)">
                    <template #icon>
                      <Icon icon="icon-shezhi" />
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

  <!-- Password reset modal -->
  <AsyncComponent :visible="state.updatePasswdVisible">
    <UpdatePasswd
      v-if="state.updatePasswdVisible"
      :visible="state.updatePasswdVisible"
      :userId="state.currentUserId"
      @cancel="closeUpdatePasswdModal" />
  </AsyncComponent>

  <!-- User lock modal -->
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
