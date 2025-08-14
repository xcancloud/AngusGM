<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton, Tabs } from 'ant-design-vue';
import { AsyncComponent, ButtonAuth, Grid, Image, PureCard } from '@xcan-angus/vue-ui';
import { Detail } from '../types';
import router from '@/router';
import { appContext, GM } from '@xcan-angus/infra';
import {
  showStatusConfirm, unlockUser, showAdminConfirm, updateSysAdmin, loadUserDetail,
  updateUserStatus, showDeleteUserConfirm, deleteUser, canModifyUser, createUserGridColumns,
  handleUserLock, closeLockModal, openPasswordResetModal, closePasswordResetModal
} from '../utils';

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
const UpdatePassword = defineAsyncComponent(() => import('@/views/organization/user/password/index.vue'));

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
const visible = ref(false); // Lock modal visibility

/**
 * Load user detail information from API
 * Handles loading state and error handling
 */
const loadUserDetailData = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const data = await loadUserDetail(userId);
  loading.value = false;
  firstLoad.value = false;
  if (data) {
    userDetail.value = data;
  }
};

/**
 * Show confirmation modal for enabling/disabling user
 * Provides user confirmation before changing user status
 */
const updateStatusConfirm = () => {
  if (!userDetail.value) return;
  showStatusConfirm(userDetail.value.fullName, userDetail.value.enabled, t, async () => {
    await updateStatus();
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
  const success = await updateUserStatus(userDetail.value, t);
  if (success) {
    await loadUserDetailData();
  }
};

/**
 * Handle user lock/unlock operations
 * @param locked - Whether to lock or unlock the user
 */
const lockingUser = (locked: boolean) => {
  handleUserLock(locked, visible, unlock);
};

/**
 * Close lock modal
 */
const closeLockModalLocal = () => {
  closeLockModal(visible, loadUserDetailData);
};

/**
 * Save lock changes and refresh user data
 */
const saveLock = () => {
  closeLockModal(visible, loadUserDetailData);
};

/**
 * Unlock user with confirmation
 * Shows confirmation dialog before unlocking
 */
const unlock = () => {
  if (!userDetail.value) return;
  unlockUser(userDetail.value.id, t).then(async (success) => {
    if (success) {
      await loadUserDetailData();
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
  openPasswordResetModal(state);
};

/**
 * Close password reset modal
 */
const closeUpdatePasswdModal = () => {
  closePasswordResetModal(state);
};

/**
 * Show confirmation modal for setting/canceling system administrator
 * Provides user confirmation before changing system admin status
 */
const setAdminConfirm = () => {
  if (!userDetail.value) {
    return;
  }
  showAdminConfirm(userDetail.value.fullName, userDetail.value.sysAdmin, t, async () => {
    await updateSysAdminData();
  });
};

/**
 * Update user's system administrator status
 * Calls API to toggle system admin flag and refreshes data
 */
const updateSysAdminData = async () => {
  if (!userDetail.value) {
    return;
  }
  const success = await updateSysAdmin(userDetail.value.id, userDetail.value.sysAdmin, t);
  if (success) {
    await loadUserDetailData();
  }
};

/**
 * Show confirmation modal for user deletion
 * Provides user confirmation before deleting user account
 */
const delUserConfirm = () => {
  if (!userDetail.value) {
    return;
  }
  showDeleteUserConfirm(userDetail.value.fullName, t, async () => {
    await delUser();
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
  const success = await deleteUser(userDetail.value.id, t);
  if (success) {
    await router.push('/organization/user');
  }
};

/**
 * Computed property for operation permissions
 * Checks if current user can modify system administrator accounts
 */
const getOperationPermissions = computed(() => {
  return canModifyUser(userDetail.value, appContext.isSysAdmin());
});

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial user detail data
 */
onMounted(() => {
  loadUserDetailData();
});

/**
 * Grid columns configuration for user basic information display
 * Defines the layout and fields for user info grid
 */
const userGridColumns = computed(() => createUserGridColumns(t));
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
        @cancel="closeLockModalLocal"
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
