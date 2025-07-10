<script setup lang="ts">
import { ref, watch } from 'vue';
import { http, PUB_GM } from '@xcan-angus/tools';
import { Input } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

type Func = (value?: any) => any

interface Props {
  value: string;
  account: string | number;
  sendType?: string;
  bizKey?: string;
  disabled?: boolean;
  validateAccount?: Func
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  account: '',
  bizKey: 'SIGNUP',
  sendType: 'mobile',
  disabled: false,
  validateAccount: undefined
});

const emit = defineEmits<{(e: 'update:value', value: string): void, (e: 'pressEnter'): void }>();

const inputValue = ref();
const error = ref(false);
const sent = ref(false);
const second = ref(60);
const errorMessage = ref(t('error-verify-code'));
let timer;

watch(() => inputValue.value, newValue => {
  emit('update:value', newValue);
});

const pressEnter = () => {
  emit('pressEnter');
};

const getCode = () => {
  if (props.disabled) {
    return;
  }

  if (typeof props.validateAccount === 'function') {
    const isValid = props.validateAccount();
    if (!isValid) {
      return;
    }
  }

  // 已经发送验证码，在规定时间内不允许重新发送
  if (sent.value) {
    return;
  }

  // 发送接口获取验证码，发送请求与倒计时为并行关系，即使接口请求失败也会启动倒计时。
  let params = {};
  let action = '';
  if (props.sendType === 'mobile') {
    params = {
      bizKey: props.bizKey,
      country: 'CN',
      mobile: props.account
    };
    action = `${PUB_GM}/auth/user/signsms/send`;
  } else {
    params = {
      bizKey: props.bizKey,
      toAddress: [props.account]
    };
    action = `${PUB_GM}/auth/user/signemail/send`;
  }

  error.value = false;
  http.post(action, params).catch(e => {
    error.value = true;
    errorMessage.value = e.message || t('network-error');
    resetSecond();
  });
  // 启动倒计时
  bootstrap();
};

const resetSecond = () => {
  sent.value = false;
  second.value = 60;
  clearInterval(timer);
};

const bootstrap = () => {
  sent.value = true;
  timer = setInterval(() => {
    if (second.value <= 0) {
      resetSecond();
      return;
    }
    second.value--;
  }, 1000);
};

const validateData = () => {
  const value = inputValue.value;
  if (value?.length === 6) {
    error.value = false;
    return true;
  }

  // 无效的验证码
  error.value = true;
  errorMessage.value = t('error-verify-code');
  return false;
};

defineExpose({ validateData });
</script>
<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="6"
      :disabled="disabled"
      dataType="number"
      size="large"
      :placeholder="$t('enter-code')"
      @pressEnter="pressEnter">
      <template #prefix>
        <img src="./assets/email.png" />
      </template>
      <template #suffix>
        <template v-if="sent">
          <div class="text-4 select-none text-gray-tip cursor-pointer">{{ $t('resend',{second}) }}</div>
        </template>
        <template v-else>
          <div
            :class="[disabled?'text-gray-tip':'text-blue-tab-active']"
            class="text-4 select-none cursor-pointer"
            @click="getCode">
            {{ $t('get-code') }}
          </div>
        </template>
      </template>
    </Input>
    <div class="error-message">{{ errorMessage }}</div>
  </div>
</template>
