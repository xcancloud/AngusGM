<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Input, Modal, notification } from '@xcan-angus/vue-ui';
import { email } from '@/api';

/**
 * Component props interface for email server testing
 */
interface Props {
  /** Whether the modal is visible */
  visible: boolean;
  /** Server identifier to test */
  id: string;
  /** Server address for display purposes */
  address: string;
}

const props = withDefaults(defineProps<Props>(), {});
const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const { t } = useI18n();

// Reactive state management
const loading = ref(false);
const emailAddress = ref('');
const inputRule = ref(false);

// Configuration constants
const MODAL_WIDTH = '540px';
const MAX_EMAIL_LENGTH = 100;

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
  if (!emailAddress.value.trim()) {
    return;
  }

  loading.value = true;
  try {
    const [error, data] = await email.testServerConfig({
      serverId: props.id,
      toAddress: [emailAddress.value.trim()]
    });

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
    loading.value = false;
  }
};

/**
 * Handle email address input change and validation
 */
const handleChange = (event: Event): void => {
  const target = event.target as HTMLInputElement;
  const value = target.value;
  inputRule.value = !value.trim();
};

/**
 * Reset component state when modal closes
 */
const handleModalClose = (): void => {
  emailAddress.value = '';
  inputRule.value = false;
};
</script>

<template>
  <Modal
    :width="MODAL_WIDTH"
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
          v-model:value="emailAddress"
          :placeholder="t('email.placeholder.receiveEmailAddress')"
          :maxlength="MAX_EMAIL_LENGTH"
          @change="handleChange" />

        <!-- Input validation message -->
        <div class="h-4 text-3 leading-3 text-rule mt-0.5">
          <template v-if="inputRule">
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
        :loading="loading"
        type="primary"
        size="small"
        @click="handleOk">
        {{ t('email.messages.sure') }}
      </Button>
    </template>
  </Modal>
</template>
