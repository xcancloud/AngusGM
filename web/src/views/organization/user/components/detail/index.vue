<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton, Tabs } from 'ant-design-vue';
import { AsyncComponent, ButtonAuth, Grid, Image, modal, notification, PureCard } from '@xcan-angus/vue-ui';
import { Detail } from './PropsType';
import router from '@/router';
import { appContext, GM } from '@xcan-angus/infra';
import { user } from '@/api';

/**
 * Async component definitions for lazy loading
 * These components are loaded only when needed to improve performance
 */
const Lock = defineAsyncComponent(() => import('@/components/Lock/index.vue'));
const UserDetail = defineAsyncComponent(() => import('./userDetail.vue'));
const UserPolicy = defineAsyncComponent(() => import('./userPolicy.vue'));
const UserDept = defineAsyncComponent(() => import('./userDept.vue'));
const UserGroup = defineAsyncComponent(() => import('./userGroup.vue'));
const UserTag = defineAsyncComponent(() => import('./userTag.vue'));
const UpdatePassword = defineAsyncComponent(() => import('@/views/organization/user/components/password/index.vue'));

// Internationalization and routing setup
const { t } = useI18n();
const route = useRoute();
const userId = route.params.id as string;
const activeKey = ref<string>('1'); // Currently active tab

/**
 * Reactive state management for user detail data
 */
const userDetail = ref<Detail>();
const loading = ref(false); // Loading state for API calls
const firstLoad = ref(true); // First load state for skeleton display

/**
 * Load user detail information from API
 * Handles loading state and error handling
 */
const loadUserDetail = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await user.getUserDetail(userId);
  loading.value = false;
  firstLoad.value = false;
  if (error) {
    return;
  }

  userDetail.value = data;
};

/**
 * Show confirmation modal for enabling/disabling user
 * Provides user confirmation before changing user status
 */
const updateStatusConfirm = () => {
  modal.confirm({
    centered: true,
    title: userDetail.value?.enabled
      ? t('common.actions.disable')
      : t('common.actions.enable'),
    content: userDetail.value?.enabled
      ? t('common.messages.confirmDisable', { name: userDetail.value?.fullName })
      : t('common.messages.confirmEnable', { name: userDetail.value?.fullName }),
    async onOk () {
      await updateStatus();
    }
  });
};

/**
 * Update user enabled/disabled status
 * Calls API to toggle user status and refreshes data
 */
const updateStatus = async () => {
  if (!userDetail.value) {
    return;
  }
  const params = [{ id: userDetail.value.id, enabled: !userDetail.value.enabled }];
  const [error] = await user.toggleUserEnabled(params);
  if (error) {
    return;
  }
  notification.success(userDetail.value.enabled
    ? t('common.messages.disableSuccess')
    : t('common.messages.enableSuccess'));
  loadUserDetail();
};

/**
 * Modal state management for user lock operations
 */
const visible = ref(false); // Lock modal visibility

/**
 * Handle user lock/unlock operations
 * @param locked - Whether to lock or unlock the user
 */
const lockingUser = (locked: boolean) => {
  if (locked) {
    visible.value = true; // Show lock modal
  } else {
    unlock(); // Direct unlock
  }
};

/**
 * Close lock modal
 */
const closeLockModal = () => {
  visible.value = false;
};

/**
 * Save lock changes and refresh user data
 */
const saveLock = () => {
  visible.value = false;
  loadUserDetail();
};

/**
 * Unlock user with confirmation
 * Shows confirmation dialog before unlocking
 */
const unlock = () => {
  modal.confirm({
    centered: true,
    title: t('user.actions.unlockUser'),
    content: t('common.messages.unlockTip', { name: userDetail.value?.fullName }),
    async onOk () {
      const [error] = await user.toggleUserLocked({ id: userDetail.value?.id, locked: false });
      if (error) {
        return;
      }
      await loadUserDetail();
      notification.success(t('common.messages.unlockSuccess'));
    }
  });
};

/**
 * Password reset modal state management
 */
const state = reactive<{
  updatePasswdVisible: boolean, // Password reset modal visibility
  lockModalVisible: boolean, // Lock modal visibility
}>({
  updatePasswdVisible: false,
  lockModalVisible: false
});

/**
 * Open password reset modal
 */
const openUpdatePasswdModal = () => {
  state.updatePasswdVisible = true;
};

/**
 * Close password reset modal
 */
const closeUpdatePasswdModal = () => {
  state.updatePasswdVisible = false;
};

/**
 * Show confirmation modal for setting/canceling system administrator
 * Provides user confirmation before changing system admin status
 */
const setAdminConfirm = () => {
  if (!userDetail.value) {
    return;
  }
  modal.confirm({
    centered: true,
    title: userDetail.value.sysAdmin
      ? t('user.actions.cancelAdmin')
      : t('user.actions.setAdmin'),
    content: userDetail.value.sysAdmin
      ? t('common.messages.cancelAdminTip', { name: userDetail.value.fullName })
      : t('common.messages.setAdminTip', { name: userDetail.value.fullName }),
    async onOk () {
      await updateSysAdmin();
    }
  });
};

/**
 * Update user's system administrator status
 * Calls API to toggle system admin flag and refreshes data
 */
const updateSysAdmin = async () => {
  if (!userDetail.value) {
    return;
  }
  const params = { id: userDetail.value.id, sysAdmin: !userDetail.value.sysAdmin };
  const [error] = await user.updateUserSysAdmin(params);
  if (error) {
    return;
  }
  notification.success(t('common.messages.editSuccess'));
  await loadUserDetail();
};

/**
 * Show confirmation modal for user deletion
 * Provides user confirmation before deleting user account
 */
const delUserConfirm = () => {
  if (!userDetail.value) {
    return;
  }
  modal.confirm({
    centered: true,
    title: t('user.actions.deleteUser'),
    content: t('common.messages.confirmDelete', { name: userDetail.value.fullName }),
    async onOk () {
      await delUser();
    }
  });
};

/**
 * Delete user from system
 * Calls API to delete user and navigates back to user list
 */
const delUser = async () => {
  if (!userDetail.value) {
    return;
  }
  const [error] = await user.deleteUser([userDetail.value.id]);
  if (error) {
    return;
  }
  notification.success('common.messages.deleteSuccess');
  router.push('/organization/user');
};

/**
 * Computed property for operation permissions
 * Checks if current user can modify system administrator accounts
 */
const getOperationPermissions = computed(() => {
  return !appContext.isSysAdmin() && userDetail.value?.sysAdmin;
});

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial user detail data
 */
onMounted(() => {
  loadUserDetail();
});

/**
 * Grid columns configuration for user basic information display
 * Defines the layout and fields for user info grid
 */
const userGridColumns = [
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
</script>
<template>
  <div class="flex flex-col min-h-full">
    <!-- User header card with avatar and action buttons -->
    <PureCard class="p-3.5 h-45 px-10 py-8 flex justify-between itmes-start mb-2">
      <!-- Loading skeleton for user information -->
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :avatar="{ size: 120, shape: 'square' }"
        :paragraph="{ rows: 4 }">
        <!-- User information display -->
        <div v-if="userDetail" class="flex flex-1 space-x-10">
          <!-- User avatar and online status -->
          <div class="flex flex-col justify-between space-y-5 items-center">
            <Image
              :src="userDetail.avatar"
              type="avatar"
              class="w-20 h-20 rounded-full" />
            <Badge
              :status="`${userDetail.online ? 'success' : 'default'}`"
              :text="`${userDetail.online ? t('common.status.online') : t('common.status.offline')}`"
              class="text-center" />
          </div>

          <!-- User basic information grid -->
          <div class="ml-10 flex flex-col justify-between flex-1">
            <div class="text-4.5 leading-3.5 text-theme-title mb-3.5 font-bold">{{ userDetail.fullName }}</div>
            <Grid :columns="userGridColumns" :dataSource="userDetail" />
          </div>
        </div>
      </Skeleton>

      <!-- Action buttons for user operations -->
      <div v-if="!firstLoad && userDetail" class="flex space-x-3.5">
        <!-- Edit user button -->
        <ButtonAuth
          code="UserModify"
          :href="`/organization/user/edit/${userDetail.id}?source=detail`"
          :disabled="getOperationPermissions" />

        <!-- Enable/Disable user button -->
        <ButtonAuth
          code="UserEnable"
          :disabled="getOperationPermissions"
          :showTextIndex="userDetail.enabled?1:0"
          @click="updateStatusConfirm" />

        <!-- Delete user button -->
        <ButtonAuth
          code="UserDelete"
          :disabled="getOperationPermissions"
          @click="delUserConfirm" />

        <!-- Lock/Unlock user button -->
        <ButtonAuth
          code="LockingUser"
          :disabled="getOperationPermissions"
          :showTextIndex="userDetail.locked?1:0"
          @click="lockingUser(!userDetail.locked)" />

        <!-- Reset password button -->
        <ButtonAuth
          code="ResetPassword"
          :disabled="getOperationPermissions"
          @click="openUpdatePasswdModal" />

        <!-- Set/Cancel system admin button -->
        <!-- TODO: English button text not working -->
        <ButtonAuth
          code="SetIdentity"
          :disabled="getOperationPermissions && appContext.isSysAdmin()"
          :showTextIndex="userDetail.sysAdmin?1:0"
          @click="setAdminConfirm" />
      </div>
    </PureCard>

    <!-- Tabbed content area -->
    <PureCard class="flex-1 px-3.5 pb-3.5 pt-0 relative">
      <Tabs
        v-model:activeKey="activeKey"
        style="flex: 0 0 auto;"
        size="small">
        <!-- Basic information tab -->
        <Tabs.TabPane
          key="1"
          :tab="t('user.profile.basicInfo')"
          class="p-3.5 pt-0">
          <Skeleton
            active
            :loading="firstLoad"
            :title="false"
            :paragraph="{ rows: 7 }">
            <UserDetail :dataSource="userDetail" />
          </Skeleton>
        </Tabs.TabPane>

        <!-- Associated departments tab -->
        <Tabs.TabPane key="2" :tab="t('user.tab.associatedDepartment')">
          <UserDept :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>

        <!-- Associated groups tab -->
        <Tabs.TabPane key="3" :tab="t('user.tab.associatedGroup')">
          <UserGroup :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>

        <!-- Associated tags tab -->
        <Tabs.TabPane key="4" :tab="t('user.tab.associatedTag')">
          <UserTag :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>

        <!-- Authorization policy tab -->
        <Tabs.TabPane key="5" :tab="t('user.tab.authPolicy')">
          <UserPolicy :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>
      </Tabs>
    </PureCard>

    <!-- User lock modal -->
    <AsyncComponent :visible="visible">
      <Lock
        v-if="visible"
        :id="userDetail?.id"
        :visible="visible"
        :title="t('user.actions.lockUser')"
        :tip="t('common.messages.lockTip')"
        width="550px"
        :action="`${GM}/user/locked`"
        @cancel="closeLockModal"
        @save="saveLock()" />
    </AsyncComponent>

    <!-- Password reset modal -->
    <AsyncComponent :visible="state.updatePasswdVisible">
      <UpdatePassword
        v-if="state.updatePasswdVisible"
        :visible="state.updatePasswdVisible"
        :userId="userDetail?.id"
        @cancel="closeUpdatePasswdModal" />
    </AsyncComponent>
  </div>
</template>
