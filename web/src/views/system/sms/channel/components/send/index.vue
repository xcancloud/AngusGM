<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Icon, Input } from '@xcan-angus/vue-ui';

import { sms } from '@/api';
import { Aisle, SendState } from './PropsType';

interface Props {
  aisle: any
}

const props = withDefaults(defineProps<Props>(), { aisle: {} as Aisle });
const emit = defineEmits<{(e: 'closeSmsMessage', value: boolean, aisle: Aisle): void }>();

const { t } = useI18n();
const sendState = ref<string>('');
const sendLoading = ref<boolean>(false);
const phoneNumber: { areaCode: string, number: string } = reactive({ areaCode: 'CN', number: '' });

const handleOk = () => {
  sendTest();
};

const handleCancel = () => {
  sendState.value = '';
  emit('closeSmsMessage', false, props.aisle);
};

const sendTest = async (): Promise<void> => {
  const params = { channelId: props.aisle.id, mobiles: [phoneNumber.number] };
  sendLoading.value = true;
  sendState.value = SendState.SENDING;
  const [error] = await sms.loadSendTest(params);
  sendLoading.value = false;
  if (error) {
    sendState.value = SendState.SENDFAILED;
    return;
  }
  sendState.value = SendState.SENDSUCCESS;
};

const iconName = computed(() => {
  switch (sendState.value) {
    case SendState.SENDSUCCESS:
      return 'icon-right';
    case SendState.SENDFAILED:
      return 'icon-cuowu';
  }
  return '';
});

const iconColor = computed(() => {
  switch (sendState.value) {
    case SendState.SENDSUCCESS:
      return 'text-success';
    case SendState.SENDFAILED:
      return 'text-danger';
  }
  return '';
});
</script>
<template>
  <div class="flex items-center space-x-10 mt-8">
    <div class="flex h-16">
      <div class="h-8 leading-8">
        {{ t('phoneNumber') }}
        <Colon />
      </div>
      <div class="items-center space-x-2 mb-8 flex-1">
        <Input
          v-model:value="phoneNumber.number"
          class="flex flex-1"
          :trimAll="true"
          :placeholder="t('mobilePhonePlaceholder')"
          dataType="number" />
        <div :class="iconColor" class="-ml-2 flex items-center">
          <Icon :icon="iconName" class="mr-2" />
          {{ t(sendState) }}
        </div>
      </div>
    </div>
    <div class="h-16 space-x-6 text-right">
      <Button
        type="primary"
        size="small"
        :loading="sendLoading"
        :disabled="!phoneNumber.number"
        @click="handleOk">
        {{ t('send') }}
      </Button>
      <Button size="small" @click="handleCancel">{{ t('cancel') }}</Button>
    </div>
  </div>
</template>
