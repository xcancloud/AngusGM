<script setup lang='ts'>
import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Input, Modal, notification } from '@xcan-angus/vue-ui';
import { email } from '@/api';

import { TestProps, TestState } from '../types';
import { getModalConfig, createTestServerConfigRequest, isValidEmailAddress } from '../utils';

// Component props with proper typing
const props = withDefaults(defineProps<TestProps>(), {});

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
}>();

const { t } = useI18n();

// Component state management
const state = reactive<TestState>({
  loading: false,
  emailAddress: '',
  inputRule: false
});

// Configuration constants
const modalConfig = getModalConfig();

/**
 * Handle modal cancel action
 */
const handleCancel = (): void => {
  emit('update:visible', false);
};

/**
 * Handle email server test submission
 */
const handleOk = async (): Promise<void> => {
  // Validate email address input
  if (!isValidEmailAddress(state.emailAddress)) {
    return;
  }

  try {
    state.loading = true;
    const params = createTestServerConfigRequest(props.id, state.emailAddress);

    const [error, data] = await email.testServerConfig(params);

    if (error) {
      console.error('Failed to test email server configuration:', error);
      return;
    }

    // Show success notification
    notification.success(data?.msg || t('email.messages.testSuccess'));
    emit('update:visible', false);
  } catch (err) {
    console.error('Unexpected error testing email server:', err);
    notification.error(t('email.messages.testError'));
  } finally {
    state.loading = false;
  }
};

/**
 * Handle email address input change and validation
 */
const handleChange = (event: Event): void => {
  const target = event.target as HTMLInputElement;
  const value = target.value;
  state.inputRule = !isValidEmailAddress(value);
};

/**
 * Reset component state when modal closes
 */
const handleModalClose = (): void => {
  state.emailAddress = '';
  state.inputRule = false;
};
</script>

<template>
  <Modal
    :width="modalConfig.width"
    :title="t('email.messages.testEmail')"
    :centered="true"
    :maskClosable="false"
    :keyboard="false"
    :visible="props.visible"
    destroyOnClose
    @cancel="handleModalClose">
    <template #default>
      <!-- Email address input section -->
      <div class="mb-4">
        <span class="text-3 leading-3 text-theme-content">
          {{ t('email.messages.emailAddress') }}<Colon />
        </span>
        <Input
          v-model:value="state.emailAddress"
          :placeholder="t('email.placeholder.receiveEmailAddress')"
          :maxlength="modalConfig.maxEmailLength"
          @change="handleChange" />

        <!-- Input validation message -->
        <div class="h-4 text-3 leading-3 text-rule mt-0.5">
          <template v-if="state.inputRule">
            {{ t('email.placeholder.inputReceiveEmailAddress') }}
          </template>
        </div>
      </div>
    </template>

    <template #footer>
      <!-- Cancel button -->
      <Button
        size="small"
        @click="handleCancel">
        {{ t('email.messages.cancel') }}
      </Button>

      <!-- Test button -->
      <Button
        :loading="state.loading"
        type="primary"
        size="small"
        @click="handleOk">
        {{ t('email.messages.sure') }}
      </Button>
    </template>
  </Modal>
</template>
