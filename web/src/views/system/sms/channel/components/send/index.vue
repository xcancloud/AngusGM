<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Icon, Input } from '@xcan-angus/vue-ui';

import { sms } from '@/api';
import { Aisle, SendState } from '../../PropsType';

// Component props interface
interface Props {
  aisle: Aisle
}

const props = defineProps<Props>();

// Component emits
const emit = defineEmits<{
  (e: 'closeSmsMessage', value: boolean, aisle: Aisle): void
}>();

const { t } = useI18n();

// Reactive state for SMS sending
const sendState = ref<string>('');
const sendLoading = ref<boolean>(false);

// Phone number input data
const phoneNumber = reactive({
  areaCode: 'CN',
  number: ''
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
  sendState.value = '';
  emit('closeSmsMessage', false, props.aisle);
};

/**
 * Send test SMS via API
 */
const sendTest = async (): Promise<void> => {
  const params = {
    channelId: props.aisle.id,
    mobiles: [phoneNumber.number]
  };

  sendLoading.value = true;
  sendState.value = SendState.SENDING;

  try {
    const [error] = await sms.loadSendTest(params);
    if (error) {
      sendState.value = SendState.SENDFAILED;
      return;
    }
    sendState.value = SendState.SENDSUCCESS;
  } finally {
    sendLoading.value = false;
  }
};

/**
 * Get icon name based on send state
 */
const iconName = computed(() => {
  switch (sendState.value) {
    case SendState.SENDSUCCESS:
      return 'icon-right';
    case SendState.SENDFAILED:
      return 'icon-cuowu';
    default:
      return '';
  }
});

/**
 * Get icon color class based on send state
 */
const iconColor = computed(() => {
  switch (sendState.value) {
    case SendState.SENDSUCCESS:
      return 'text-success';
    case SendState.SENDFAILED:
      return 'text-danger';
    default:
      return '';
  }
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
          v-model:value="phoneNumber.number"
          class="flex flex-1"
          :trimAll="true"
          :placeholder="t('sms.placeholder.mobilePhonePlaceholder')"
          dataType="number" />

        <!-- Status indicator with icon -->
        <div :class="iconColor" class="-ml-2 flex items-center">
          <Icon :icon="iconName" class="mr-2" />
          {{ t(sendState) }}
        </div>
      </div>
    </div>

    <!-- Action buttons -->
    <div class="h-16 space-x-6 text-right">
      <!-- Send button -->
      <Button
        type="primary"
        size="small"
        :loading="sendLoading"
        :disabled="!phoneNumber.number"
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
