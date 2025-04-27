<script setup lang="ts">
import { ref, defineAsyncComponent, Ref, inject, onMounted } from 'vue';
import { Card, AsyncComponent, notification } from '@xcan/design';
import { Switch } from 'ant-design-vue';


import {tenant} from '@/api';

const CancellationModal = defineAsyncComponent(() => import('./cancelConform.vue'));
const tenantInfo: Ref = inject('tenantInfo', ref());

const checked = ref(false);
const tenantStatus = ref<{ value: string, message: string }>({ value: '', message: '' });
const getTenantDetail = async () => {
  const [error, { data }] = await tenant.getTenantDetail(tenantInfo.value.tenantId);
  if (error) {
    return;
  }
  tenantStatus.value = data?.status;
  checked.value = data?.status?.value === 'CANCELLING';
};

const switchChange = async (value: boolean) => {
  // 撤销注销
  if (!value) {
    const [error] = await tenant.revokeSignCancel();
    if (error) {
      return;
    }
    getTenantDetail();
    checked.value = false;
    notification.success('取消注销成功');
    return;
  }

  checked.value = true;
  visible.value = true;
};

const handleUpdate = (value: boolean) => {
  if (value) {
    getTenantDetail();
    return;
  }
  checked.value = false;
};

onMounted(() => {
  getTenantDetail();
});
const visible = ref(false);
</script>
<template>
  <Card bodyClass="px-8 py-5 text-3 leading-3 text-theme-content">
    <template #title>
      <div class="flex items-center">
        <span>注销账号</span>
        <Switch
          v-model:checked="checked"
          size="small"
          class="ml-6 mt-0.5 mr-2"
          @change="switchChange" />
        <span
          v-if="tenantStatus.value === 'CANCELLING'"
          class="text-3 leading-3 mt-1">{{ tenantStatus?.message }}</span>
      </div>
    </template>
    确认账号不在使用，可以进行注销。系统管理员注销确认后，账户会继续保留24小时后被清除，保留期间您可以撤销注销账户。注销后账号及账号下所有的用户将无法登录，服务及数据将会被删除且无法恢复。
    <AsyncComponent :visible="visible">
      <CancellationModal
        v-if="visible"
        v-model:visible="visible"
        @update="handleUpdate" />
    </AsyncComponent>
  </Card>
</template>
