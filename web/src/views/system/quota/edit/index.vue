<script setup lang="ts">
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { Icon, IconRequired, Input, Modal } from '@xcan-angus/vue-ui';

import { setting } from '@/api';
import { EditQuotaProps } from '../types';
import { isValidQuotaValue } from '../utils';

const { t } = useI18n();

// Component props with defaults
type Props = EditQuotaProps

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  default0: 0,
  max: 0,
  min: 0,
  name: undefined
});

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'ok'): void;
}>();

// Reactive state for new quota value
const newQuota = ref<number>(Number(props.default0));

/**
 * Handle OK button click - save quota changes
 */
const handleOk = async (): Promise<void> => {
  if (!newQuota.value) {
    return;
  }

  // Validate quota value
  if (!isValidQuotaValue(newQuota.value, props.min, props.max)) {
    return;
  }

  const [error] = await setting.updateTenantQuota(props.name, newQuota.value);
  if (error) {
    return;
  }

  emit('update:visible', false);
  emit('ok');
};

/**
 * Handle cancel button click
 */
const handleCancel = (): void => {
  emit('update:visible', false);
};

// Computed properties for validation and display
const hasValidationError = computed<boolean>(() => {
  return !newQuota.value || !isValidQuotaValue(newQuota.value, props.min, props.max);
});
</script>

<template>
  <Modal
    :title="t('quota.messages.modifyQuota')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    width="540px"
    @cancel="handleCancel"
    @ok="handleOk">
    <!-- Quota range information -->
    <div class="text-3 leading-3 text-theme-sub-content mb-4 flex items-center">
      <Icon class="text-blue-tips text-3.5 mr-1" icon="icon-tishi1" />
      {{ t('quota.messages.allowMinQuota') }}<em class="not-italic mr-1">:</em>
      <span class="text-theme-content">{{ props.min }}</span>
      <Icon class="text-blue-tips text-3.5 ml-5 mr-1" icon="icon-tishi1" />
      {{ t('quota.messages.allowMaxQuota') }}<em class="not-italic mr-1">:</em>
      <span class="text-theme-content">{{ props.max }}</span>
    </div>

    <!-- Quota input field -->
    <div class="flex items-center space-x-2 whitespace-nowrap text-3 leading-3">
      <IconRequired class="mr-1" />
      <span>{{ t('quota.messages.currentQuota') }}</span>
      <Input
        v-model:value="newQuota"
        dataType="number"
        :class="{'border-err': hasValidationError}"
        :placeholder="t('quota.placeholder.quotaRange', { min: props.min, max: props.max })"
        :min="props.min"
        :max="props.max" />
    </div>
  </Modal>
</template>

<style scoped>
.border-err {
  border-color: #ff4d4f;
}
</style>
