<script setup lang="ts">
import { inject, ref, watch, onMounted } from 'vue';
import { cookie } from '@xcan-angus/tools';
import { Modal, Hints, Input } from '@xcan-angus/vue-ui';
import { login } from '@/api';

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

const tenantInfo = inject('tenantInfo', ref());

const inputValue = ref<string>();
const passIsError = ref(false);

const ok = async () => {
  if (!inputValue.value) {
    passIsError.value = true;
    return;
  }

  if (!tenantInfo?.value?.id) {
    return;
  }

  // 验证密码是否正确
  const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID || '';
  const clientSecret = import.meta.env.VITE_OAUTH_CLIENT_SECRET || '';
  const params = {
    account: tenantInfo.value.mobile,
    password: inputValue.value,
    scope: 'sign',
    signinType: 'ACCOUNT_PASSWORD',
    userId: tenantInfo.value.id,
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

  // 同步更新token
  cookie.set('access_token', res.data.access_token);
  cookie.set('refresh_token', res.data.refresh_token);
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
