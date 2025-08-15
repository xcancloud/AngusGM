<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Icon, Input } from '@xcan-angus/vue-ui';

import { sms } from '@/api';
import { Aisle, SendState, SmsSendProps, SmsSendState } from '../types';
import { getSendStateIcon, getSendStateIconColor, createSmsTestParams } from '../utils';

// Component props interface
type Props = SmsSendProps

const props = defineProps<Props>();

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'closeSmsMessage', value: boolean, aisle: Aisle): void
}>();

const { t } = useI18n();

// Reactive state for SMS sending
const state = reactive<SmsSendState>({
  sendState: '',
  sendLoading: false,
  phoneNumber: {
    areaCode: 'CN',
    number: ''
  }
});

/**
 * Handle send button click
 */
const handleOk = () => {
  sendTest();
};

/**
 * Handle cancel button click
 */
const handleCancel = () => {
  state.sendState = '';
  emit('closeSmsMessage', false, props.aisle);
};

/**
 * Send test SMS via API
 */
const sendTest = async (): Promise<void> => {
  const params = createSmsTestParams(props.aisle.id, state.phoneNumber.number);

  state.sendLoading = true;
  state.sendState = SendState.SENDING;

  try {
    const [error] = await sms.loadSendTest(params);
    if (error) {
      state.sendState = SendState.SENDFAILED;
      return;
    }
    state.sendState = SendState.SENDSUCCESS;
  } finally {
    state.sendLoading = false;
  }
};

/**
 * Get icon name based on send state
 */
const iconName = computed(() => {
  return getSendStateIcon(state.sendState);
});

/**
 * Get icon color class based on send state
 */
const iconColor = computed(() => {
  return getSendStateIconColor(state.sendState);
});
</script>

<template>
  <div class="flex items-center space-x-10 mt-8">
    <!-- Phone number input section -->
    <div class="flex h-16">
      <div class="h-8 leading-8">
        {{ t('sms.messages.phoneNumber') }}
        <Colon />
      </div>
      <div class="items-center space-x-2 mb-8 flex-1">
        <!-- Phone number input field -->
        <Input
          v-model:value="state.phoneNumber.number"
          class="flex flex-1"
          :trimAll="true"
          :placeholder="t('sms.placeholder.mobilePhonePlaceholder')"
          dataType="number" />

        <!-- Status indicator with icon -->
        <div :class="iconColor" class="-ml-2 flex items-center">
          <Icon :icon="iconName" class="mr-2" />
          {{ t(state.sendState) }}
        </div>
      </div>
    </div>

    <!-- Action buttons -->
    <div class="h-16 space-x-6 text-right">
      <!-- Send button -->
      <Button
        type="primary"
        size="small"
        :loading="state.sendLoading"
        :disabled="!state.phoneNumber.number"
        @click="handleOk">
        {{ t('sms.buttons.send') }}
      </Button>

      <!-- Cancel button -->
      <Button
        size="small"
        @click="handleCancel">
        {{ t('sms.buttons.cancel') }}
      </Button>
    </div>
  </div>
</template>
