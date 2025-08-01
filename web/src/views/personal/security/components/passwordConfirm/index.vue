<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import {appContext, cookieUtils, type TokenInfo} from '@xcan-angus/infra';
import {Hints, Input, Modal} from '@xcan-angus/vue-ui';
import {login} from '@/api';

interface Props {
  value: string,
  visible: boolean,
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  visible: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:value', val: string): void,
  (e: 'update:visible', val: boolean): void,
  (e: 'ok'): void,
}>();

const inputValue = ref<string>();
const passIsError = ref(false);

const ok = async () => {
  if (!inputValue.value) {
    passIsError.value = true;
    return;
  }

  if (!appContext.getUser()?.id) {
    return;
  }

  // 验证密码是否正确
  const clientId = appContext.getEnv().oauthClientId || '';
  const clientSecret = appContext.getEnv().oauthClientSecret || '';
  const params = {
    account: appContext.getUser()?.mobile,
    password: inputValue.value,
    scope: 'user_trust',
    signinType: 'ACCOUNT_PASSWORD',
    userId: appContext.getUser()?.id,
    clientId,
    clientSecret
  };
  const [error, res] = await login.signin(params, {
    headers: {
      clientId,
      clientSecret
    }
  });

  if (error) {
    passIsError.value = true;
    return;
  }

  const tokenInfo: TokenInfo = {
    request_auth_time: new Date().toISOString(),
    ...res?.data
  };
  cookieUtils.setTokenInfo(tokenInfo);

  cancel();
  emit('ok');
};

const cancel = () => {
  inputValue.value = undefined;
  passIsError.value = false;
  emit('update:visible', false);
};

const inputChange = (event: { target: { value: string; }; }) => {
  const value = event.target.value;
  emit('update:value', value);
};

onMounted(() => {
  watch(() => props.value, (newValue) => {
    inputValue.value = newValue;
  }, { immediate: true });
});
</script>
<template>
  <Modal
    :closable="false"
    :title="$t('personalCenter.verifyPass')"
    :visible="visible"
    :okText="$t('personalCenter.nextStep')"
    @ok="ok"
    @cancel="cancel">
    <Hints text="你正在进行敏感操作，继续操作前请验证您的身份。" />
    <div class="flex items-center flex-nowrap whitespace-nowrap mt-6">
      <span class="mr-2 text-theme-title">登录密码</span>
      <Input
        :value="inputValue"
        :allowClear="false"
        :error="passIsError"
        placeholder="输入登录密码进行验证"
        type="password"
        @change="inputChange" />
    </div>
  </Modal>
</template>
