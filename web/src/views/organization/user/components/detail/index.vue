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

const Lock = defineAsyncComponent(() => import('@/components/Lock/index.vue'));
const UserDetail = defineAsyncComponent(() => import('./userDetail.vue'));
const AuthorizationPolicy = defineAsyncComponent(() => import('./authPolicy.vue'));
const UserDept = defineAsyncComponent(() => import('./userDept.vue'));
const UserGroup = defineAsyncComponent(() => import('./userGroup.vue'));
const UserTag = defineAsyncComponent(() => import('./userTag.vue'));
const UpdatePassword = defineAsyncComponent(() => import('@/views/organization/user/components/password/index.vue'));

const { t } = useI18n();
const route = useRoute();
const userId = route.params.id as string;
const activeKey = ref<string>('1');

const userDetail = ref<Detail>();
const loading = ref(false);
const firstLoad = ref(true);
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

// 禁用启用用户弹框
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

const visible = ref(false);
const lockingUser = (locked: boolean) => {
  if (locked) {
    visible.value = true;
  } else {
    unlock();
  }
};
const closeLockModal = () => {
  visible.value = false;
};
const saveLock = () => {
  visible.value = false;
  loadUserDetail();
};

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

const state = reactive<{
  updatePasswdVisible: boolean,
  lockModalVisible: boolean,
}>({
  updatePasswdVisible: false,
  lockModalVisible: false
});
const openUpdatePasswdModal = () => {
  state.updatePasswdVisible = true;
};
const closeUpdatePasswdModal = () => {
  state.updatePasswdVisible = false;
};

// 设置或取消用户系统管理员弹框
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

const getOperationPermissions = computed(() => {
  return !appContext.isSysAdmin() && userDetail.value?.sysAdmin;
});

onMounted(() => {
  loadUserDetail();
});

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
    <PureCard class="p-3.5 h-45 px-10 py-8 flex justify-between itmes-start mb-2">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :avatar="{ size: 120, shape: 'square' }"
        :paragraph="{ rows: 4 }">
        <div v-if="userDetail" class="flex flex-1 space-x-10">
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
          <div class="ml-10 flex flex-col justify-between flex-1">
            <div class="text-4.5 leading-3.5 text-theme-title mb-3.5 font-bold">{{ userDetail.fullName }}</div>
            <Grid :columns="userGridColumns" :dataSource="userDetail" />
          </div>
        </div>
      </Skeleton>
      <div v-if="!firstLoad && userDetail" class="flex space-x-3.5">
        <ButtonAuth
          code="UserModify"
          :href="`/organization/user/edit/${userDetail.id}?source=detail`"
          :disabled="getOperationPermissions" />
        <ButtonAuth
          code="UserEnable"
          :disabled="getOperationPermissions"
          :showTextIndex="userDetail.enabled?1:0"
          @click="updateStatusConfirm" />
        <ButtonAuth
          code="UserDelete"
          :disabled="getOperationPermissions"
          @click="delUserConfirm" />
        <ButtonAuth
          code="LockingUser"
          :disabled="getOperationPermissions"
          :showTextIndex="userDetail.locked?1:0"
          @click="lockingUser(!userDetail.locked)" />
        <ButtonAuth
          code="ResetPassword"
          :disabled="getOperationPermissions"
          @click="openUpdatePasswdModal" />
        <!-- TODO  英文按钮文案不生效     -->
        <ButtonAuth
          code="SetIdentity"
          :disabled="getOperationPermissions && appContext.isSysAdmin()"
          :showTextIndex="userDetail.sysAdmin?1:0"
          @click="setAdminConfirm" />
      </div>
    </PureCard>
    <PureCard class="flex-1 px-3.5 pb-3.5 pt-0 relative">
      <Tabs
        v-model:activeKey="activeKey"
        style="flex: 0 0 auto;"
        size="small">
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
        <Tabs.TabPane key="2" :tab="t('user.tab.associatedDepartment')">
          <UserDept :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="3" :tab="t('user.tab.associatedGroup')">
          <UserGroup :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="4" :tab="t('user.tab.associatedTag')">
          <UserTag :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="5" :tab="t('user.tab.authPolicy')">
          <AuthorizationPolicy :userId="userId" :hasAuth="getOperationPermissions" />
        </Tabs.TabPane>
      </Tabs>
    </PureCard>
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
    <AsyncComponent :visible="state.updatePasswdVisible">
      <UpdatePassword
        v-if="state.updatePasswdVisible"
        :visible="state.updatePasswdVisible"
        :userId="userDetail?.id"
        @cancel="closeUpdatePasswdModal" />
    </AsyncComponent>
  </div>
</template>
