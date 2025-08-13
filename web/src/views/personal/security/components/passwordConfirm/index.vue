<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext, cookieUtils, type TokenInfo } from '@xcan-angus/infra';
import { Hints, Input, Modal } from '@xcan-angus/vue-ui';
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

const { t } = useI18n();
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

const inputChange = (event: any) => {
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
    :title="$t('securities.messages.verifyPass')"
    :visible="visible"
    :okText="$t('securities.messages.nextStep')"
    @ok="ok"
    @cancel="cancel">
    <Hints :text="t('securities.messages.modalDescription', { typeIfy: t('securities.columns.loginPassword') })" />
    <div class="flex items-center flex-nowrap whitespace-nowrap mt-6">
      <span class="mr-2 text-theme-title">{{ t('securities.columns.loginPassword') }}</span>
      <Input
        :value="inputValue"
        :allowClear="false"
        :error="passIsError"
        :placeholder="t('securities.placeholder.passwordPlaceholder')"
        type="password"
        @change="inputChange" />
    </div>
  </Modal>
</template>
