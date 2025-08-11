<script setup lang="ts">
import { ref } from 'vue';
import { Button } from 'ant-design-vue';
import { Colon, Hints, Input, Modal, notification } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { tenant } from '@/api';

const { t } = useI18n();

interface Props {
  visible: boolean
}

withDefaults(defineProps<Props>(), { visible: false });

const emit = defineEmits<{(e: 'update:visible', value: boolean): void, (e: 'update', value: boolean): void }>();

// Form state variables
const code = ref<string>('');
const loading = ref(false);
const count = ref(0);

/**
 * Send verification code SMS for account cancellation
 * Starts countdown timer after successful sending
 */
const sendVerificationCode = async () => {
  loading.value = true;
  const [error] = await tenant.sendSignCancelSms();
  loading.value = false;
  if (error) {
    return;
  }

  // Start countdown timer
  count.value = 60;
  const timeout = setInterval(() => {
    count.value--;
    if (count.value === 0) {
      clearInterval(timeout);
    }
  }, 1000);
};

// Input validation state
const inputError = ref(false);

/**
 * Handle form submission
 * Validates verification code input before proceeding
 */
const handleOk = async () => {
  if (!code.value) {
    inputError.value = true;
    return;
  }
  verificationCodeConfirm();
};

/**
 * Confirm account cancellation with verification code
 * Emits success event and closes modal on completion
 */
const verificationCodeConfirm = async () => {
  loading.value = true;
  const [error] = await tenant.confirmSignCancelSms(code.value);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success(t('security.messages.cancellationSuccess'));
  emit('update', true);
  emit('update:visible', false);
};

/**
 * Handle modal cancellation
 * Emits cancel event and closes modal
 */
const handleCancel = () => {
  emit('update', false);
  emit('update:visible', false);
};
</script>
<template>
  <Modal
    :visible="visible"
    centered
    closable
    :title="t('security.titles.cancellationConfirmation')"
    @cancel="handleCancel">
    <template #default>
      <Hints :text="t('security.messages.cancellationModalDesc')" class="mb-4" />
      <div class="flex">
        <div class="mr-1 pt-1.25">
          <div>
            {{ t('security.buttons.sendVerificationCode') }}
            <Colon />
          </div>
          <div class="mt-8">
            {{ t('security.columns.verificationCode') }}
            <Colon />
          </div>
        </div>
        <div class="flex flex-col flex-1 space-y-5">
          <Button
            size="small"
            :disabled="!appContext.isSysAdmin() || count !== 0"
            :loading="loading"
            @click="sendVerificationCode">
            {{ t('security.messages.sendVerificationCodeDesc') }} {{ count !== 0 ? t('security.messages.verificationCodeCountdown', { count }) : '' }}
          </Button>
          <Input
            v-model:value="code"
            size="small"
            dataType="number"
            :error="inputError"
            :maxlength="6" />
          <div style="margin-top: -5px;" class="h-4">
            <template v-if="inputError">{{ t('security.validation.verificationCodeRequired') }}</template>
          </div>
        </div>
      </div>
    </template>
    <template #footer>
      <div>
        <Button size="small" @click="handleCancel">{{ t('security.buttons.cancel') }}</Button>
        <Button
          size="small"
          type="primary"
          :disabled="!appContext.isSysAdmin()"
          @click="handleOk">
          {{ t('security.buttons.confirm') }}
        </Button>
      </div>
    </template>
  </Modal>
</template>
