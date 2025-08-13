<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { AsyncComponent, Card, notification } from '@xcan-angus/vue-ui';
import { Switch } from 'ant-design-vue';
import { appContext } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { tenant } from '@/api';

const { t } = useI18n();

// Async component for account cancellation confirmation modal
const CancellationModal = defineAsyncComponent(() => import('./cancelConform.vue'));

// Reactive state for switch control and tenant status
const checked = ref(false);
const tenantStatus = ref<{ value: string, message: string }>({ value: '', message: '' });

/**
 * Fetch tenant details and update component state
 * Sets the switch state based on tenant cancellation status
 */
const getTenantDetail = async () => {
  const [error, { data }] = await tenant.getTenantDetail(appContext.getUser()?.tenantId ?? 0);
  if (error) {
    return;
  }
  tenantStatus.value = data?.status;
  checked.value = data?.status?.value === 'CANCELLING';
};

/**
 * Handle switch change events for account cancellation
 * @param value - Boolean value indicating cancellation state
 */
const switchChange = async (value: boolean) => {
  // Revoke cancellation request
  if (!value) {
    const [error] = await tenant.revokeSignCancel();
    if (error) {
      return;
    }
    await getTenantDetail();
    checked.value = false;
    notification.success(t('security.messages.cancellationRevoked'));
    return;
  }

  checked.value = true;
  visible.value = true;
};

/**
 * Handle modal update events
 * @param value - Boolean value indicating if update was successful
 */
const handleUpdate = (value: boolean) => {
  if (value) {
    getTenantDetail();
    return;
  }
  checked.value = false;
};

// Initialize component data on mount
onMounted(() => {
  getTenantDetail();
});

// Modal visibility state
const visible = ref(false);
</script>
<template>
  <Card bodyClass="px-8 py-5 text-3 leading-3 text-theme-content">
    <template #title>
      <div class="flex items-center">
        <span>{{ t('security.titles.accountCancellation') }}</span>
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
    {{ t('security.messages.accountCancellationDesc') }}
    <AsyncComponent :visible="visible">
      <CancellationModal
        v-if="visible"
        v-model:visible="visible"
        @update="handleUpdate" />
    </AsyncComponent>
  </Card>
</template>
