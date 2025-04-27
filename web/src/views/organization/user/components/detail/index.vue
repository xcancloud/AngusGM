<script setup lang='ts'>
import { computed, reactive, inject, onMounted, ref, defineAsyncComponent, Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton, Tabs } from 'ant-design-vue';
import { ButtonAuth, modal, Grid, PureCard, Image, notification, AsyncComponent } from '@xcan-angus/vue-ui';
import { Detail } from './PropsType';
import router from '@/router';
import { GM } from '@xcan-angus/tools';
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
const tenantInfo: Ref = inject('tenantInfo', ref());
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
    title: userDetail.value?.enabled ? t('禁用') : t('启用'),
    content: userDetail.value?.enabled ? `确定禁用【${userDetail.value?.fullName}】吗？` : `确定启用【${userDetail.value?.fullName}】吗？`,
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
  notification.success(userDetail.value.enabled ? '禁用成功' : '启用成功');
  loadUserDetail();
};

const visible = ref(false);
const lockingUser = (lockede: boolean) => {
  if (lockede) {
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
    title: t('unlockUsers'),
    content: t('userTip7', { name: userDetail.value?.fullName }),
    async onOk () {
      const [error] = await user.toggleUserLocked({ id: userDetail.value?.id, locked: false });
      if (error) {
        return;
      }
      loadUserDetail();
      notification.success(t('解锁成功'));
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
    title: userDetail.value.sysAdmin ? t('cancelAdministrator') : t('setupAdministrator'),
    content: userDetail.value.sysAdmin ? t('userTip2', { name: userDetail.value.fullName }) : t('userTip3', { name: userDetail.value.fullName }),
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
  notification.success('修改成功');
  loadUserDetail();
};

const delUserConfirm = () => {
  if (!userDetail.value) {
    return;
  }
  modal.confirm({
    centered: true,
    title: t('delUser'),
    content: t('userTip4', { name: userDetail.value.fullName }),
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
  notification.success('删除成功');
  router.push('/organization/user');
};

const getOperatPermissions = computed(() => {
  return !tenantInfo.value.sysAdmin && userDetail.value?.sysAdmin;
});

onMounted(() => {
  loadUserDetail();
});

const userGridColumns = [
  [
    {
      label: t('userName'),
      dataIndex: 'username'
    },
    {
      label: t('加入时间'),
      dataIndex: 'createdDate'
    },
    {
      label: t('recentlyOnline'),
      dataIndex: 'onlineDate'
    }
  ]
];
</script>
<template>
  <div class="flex flex-col min-h-full">
    <PureCard class="p-3.5 h-48 px-10 py-8 flex justify-between itmes-start mb-2">
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
              :text="`${userDetail.online ? t('onLine') : t('offLine')}`"
              class="text-center" />
          </div>
          <div class="ml-10 flex flex-col justify-between flex-1">
            <div class="text-3.5 leading-3.5 text-theme-title font-medium">{{ userDetail.fullName }}</div>
            <Grid :columns="userGridColumns" :dataSource="userDetail" />
          </div>
        </div>
      </Skeleton>
      <div v-if="!firstLoad && userDetail" class="flex space-x-3.5">
        <ButtonAuth
          code="UserModify"
          :href="`/organization/user/edit/${userDetail.id}?source=detail`"
          :disabled="getOperatPermissions" />
        <ButtonAuth
          code="UserEnable"
          :disabled="getOperatPermissions"
          :showTextIndex="userDetail.enabled?1:0"
          @click="updateStatusConfirm" />
        <ButtonAuth
          code="UserDelete"
          :disabled="getOperatPermissions"
          @click="delUserConfirm" />
        <ButtonAuth
          code="LockingUser"
          :disabled="getOperatPermissions"
          :showTextIndex="userDetail.locked?1:0"
          @click="lockingUser(!userDetail.locked)" />
        <ButtonAuth
          code="ResetPassword"
          :disabled="getOperatPermissions"
          @click="openUpdatePasswdModal" />
        <ButtonAuth
          code="SetIdentity"
          :disabled="getOperatPermissions && tenantInfo.sysAdmin"
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
          :tab="t('basicInformation')"
          class="p-3.5 pt-0">
          <Skeleton
            active
            :loading="firstLoad"
            :title="false"
            :paragraph="{ rows: 7 }">
            <UserDetail :dataSource="userDetail" />
          </Skeleton>
        </Tabs.TabPane>
        <Tabs.TabPane key="2" :tab="t('授权策略')">
          <AuthorizationPolicy :userId="userId" :hasAuth="getOperatPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="3" :tab="t('关联部门')">
          <UserDept :userId="userId" :hasAuth="getOperatPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="4" :tab="t('关联组')">
          <UserGroup :userId="userId" :hasAuth="getOperatPermissions" />
        </Tabs.TabPane>
        <Tabs.TabPane key="5" :tab="t('关联标签')">
          <UserTag :userId="userId" :hasAuth="getOperatPermissions" />
        </Tabs.TabPane>
      </Tabs>
    </PureCard>
    <AsyncComponent :visible="visible">
      <Lock
        v-if="visible"
        :id="userDetail?.id"
        :visible="visible"
        :title="t('lockout')+t('user')"
        :tip="t('userTip8')"
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
