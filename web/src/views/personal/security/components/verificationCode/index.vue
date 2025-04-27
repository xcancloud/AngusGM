<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Input } from '@xcan-angus/vue-ui';

import { user } from '@/api';

interface Props {
  mediumIfy: string,
  isModify: boolean,
  sendType: 'email' | 'mobile',
  canSend?: boolean, // 是否能发送请求获取验证码
  error?: boolean,
  country?: string,
}

const props = withDefaults(defineProps<Props>(), {
  mediumIfy: undefined,
  isModify: false,
  canSend: true,
  error: false,
  country: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'change', value: string): void
}>();

const { t } = useI18n();
const hadSent = ref(false);
const countDown = ref(60);
let loading = false;

const changeHandle = (event: any) => {
  const value = event.target.value;
  emit('change', value);
};

const toSend = async () => {
  if (!props.canSend || loading || !props.mediumIfy) {
    return;
  }

  loading = true;
  const params = {} as Record<'bizKey' | 'mobile' | 'toAddress' | 'country', string | string[]>;
  switch (props.sendType) {
    case 'email':
      params.bizKey = props.isModify ? 'MODIFY_EMAIL' : 'BIND_EMAIL';
      params.toAddress = [props.mediumIfy];
      break;
    case 'mobile':
      params.bizKey = props.isModify ? 'MODIFY_MOBILE' : 'BIND_MOBILE';
      params.country = props.country;
      params.mobile = props.mediumIfy;
      break;
  }

  // 开始倒计时
  hadSent.value = true;
  enableCountdown();
  if (props.sendType === 'email') {
    await user.senEmailCode(params);
  }
  if (props.sendType === 'mobile') {
    await user.sendSmsCode(params);
  }
  loading = false;
};

let timeoutTimer: NodeJS.Timer;
const send = () => {
  clearTimeout(timeoutTimer);
  timeoutTimer = setTimeout(() => {
    toSend();
  }, 16.66);
};

let timer: NodeJS.Timer;
const enableCountdown = () => {
  timer = setInterval(() => {
    if (countDown.value <= 0) {
      clearInterval(timer);
      countDown.value = 60;
      hadSent.value = false;

      return;
    }

    countDown.value--;
  }, 1000);
};

</script>

<template>
  <div class="select-none relative flex flex-nowrap items-center">
    <Input
      dataType="number"
      trimAll
      class="h-8 pr-24"
      :error="error"
      :placeholder="t('personalCenter.security.codePlaceholder')"
      :allowClear="false"
      :maxlength="6"
      @change="changeHandle" />
    <span
      v-if="hadSent"
      class="absolute z-2 right-3 block text-gray-placeholder cursor-pointer whitespace-nowrap h-8 leading-8">
      {{ t('personalCenter.security.resend', { countDown }) }}</span>
    <span
      v-else
      class="absolute z-2 right-3 block content-primary-text cursor-pointer whitespace-nowrap h-8 leading-8"
      @click="send">{{ t('personalCenter.security.sendCode') }}</span>
  </div>
</template>
