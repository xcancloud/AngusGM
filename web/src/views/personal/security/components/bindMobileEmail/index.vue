<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext, regexpUtils } from '@xcan-angus/infra';
import { Icon, Input, Modal } from '@xcan-angus/vue-ui';

import VerificationCode from '@/views/personal/security/components/verificationCode/index.vue';
import { user } from '@/api';

interface Props {
  valueKey: 'email' | 'mobile',
  visible: boolean,
  linkSecret: string,
  isModify: boolean
}

interface EmailParams {
  bizKey: string,
  email: string,
  linkSecret?: string,
  verificationCode: string
}

const props = withDefaults(defineProps<Props>(), {
  valueKey: 'mobile',
  visible: false,
  linkSecret: '',
  isModify: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void,
  (e: 'cancel'): void,
  (e: 'ok', text: any, valueKey: 'email' | 'mobile'): void,
}>();

const { t } = useI18n();
const itc = ref('86');
const country = ref('CN');
const code = ref('');
const email = ref('');
const mobile = ref('');
const loading = ref(false);
const canSend = ref(false);// 是否能发送请求获取验证码

const changeHandle = (value: string, valueKey: 'email' | 'code' | 'mobile') => {
  switch (valueKey) {
    case 'email':
      email.value = value as string;
      break;
    case 'mobile':
      mobile.value = value as string;
      break;
    case 'code':
      code.value = value as string;
      break;
  }
};

const reset = () => {
  itc.value = '86';
  country.value = 'CN';
  code.value = '';
  email.value = '';
  mobile.value = '';
  loading.value = false;
  codeError.value = false;
  mobileError.value = false;
  emailError.value = false;
  canSend.value = false;
};

const cancel = (): void => {
  emit('cancel');
  emit('update:visible', false);
  reset();
};

const ok = (): void => {
  if (loading.value) {
    return;
  }
  switch (props.valueKey) {
    case 'email':
      emailOk();
      break;
    case 'mobile':
      mobileOk();
      break;
  }
};

const codeError = ref(false);
const emailError = ref(false);
const validateEmail = () => {
  const isValid = regexpUtils.isEmail(email.value);
  emailError.value = !isValid;
  canSend.value = isValid;
  return isValid;
};
const emailOk = async () => {
  if (!validateEmail()) {
    return;
  }
  if (!code.value) {
    codeError.value = true;
    return;
  }
  loading.value = true;
  const params: EmailParams = {
    bizKey: 'BIND_EMAIL',
    email: email.value,
    verificationCode: code.value,
    linkSecret: props.linkSecret
  };
  if (props.isModify) {
    params.bizKey = 'MODIFY_EMAIL';
  }
  loading.value = true;
  const [error] = await user.updateCurrentEmail(params);
  loading.value = false;
  if (error) {
    return;
  }
  const info = { email: email.value };
  updateUser(info);
  emit('ok', info, props.valueKey);
  cancel();
};

const mobileError = ref(false);
const validatePhone = () => {
  const isValid = regexpUtils.isMobileNumber(country.value, mobile.value);
  mobileError.value = !isValid;
  canSend.value = isValid;
  return isValid;
};
const mobileOk = async () => {
  if (!validatePhone()) {
    return;
  }
  if (!code.value) {
    codeError.value = true;
    return;
  }
  const params = {
    bizKey: 'BIND_MOBILE',
    itc: itc.value,
    country: country.value,
    linkSecret: props.linkSecret,
    mobile: mobile.value,
    verificationCode: code.value
  };
  if (props.isModify) {
    params.bizKey = 'MODIFY_MOBILE';
  }
  loading.value = true;
  const [error] = await user.updateCurrentMobile(params);
  loading.value = false;
  if (error) {
    return;
  }
  const info = {
    itc: itc.value,
    country: country.value,
    mobile: mobile.value
  };
  updateUser(info);
  emit('ok', info, props.valueKey);
  cancel();
};

const updateUser = (info: Record<string, string>) => {
  appContext.setUser({ ...appContext.getUser(), ...info });
};

const typeIfy = computed(() => {
  return props.valueKey === 'mobile' ? t('personalCenter.mobile') : t('personalCenter.email');
});

const title = computed(() => {
  return `${t('personalCenter.bind')}${typeIfy.value}`;
});

const mediumIfy = computed(() => {
  return props.valueKey === 'mobile' ? mobile.value : email.value;
});
</script>
<template>
  <Modal
    :closable="false"
    :title="title"
    :visible="visible"
    :confirmLoading="loading"
    @cancel="cancel"
    @ok="ok">
    <div class="flex flex-nowrap text-3 leading-5 text-theme-content">
      <template v-if="valueKey === 'mobile'">
        <Icon class="flex-shrink-0 text-4 transform-gpu translate-y-0.5 mr-2 text-green-wechat" icon="icon-right" />
        {{ t('personalCenter.security.mobileBindDescription') }}
      </template>
      <template v-else>
        <Icon class="flex-shrink-0 text-4 transform-gpu translate-y-0.5 mr-2 text-green-wechat" icon="icon-right" />
        {{ t('personalCenter.security.emailBindDescription') }}
      </template>
    </div>
    <div class="flex flex-nowrap mt-8">
      <div class="flex flex-col items-end flex-grow-0 whitespace-nowrap text-theme-title mr-3">
        <span class="block h-8 leading-8">{{ typeIfy }}</span>
        <span class="block h-8 leading-8 mt-6">{{ t('personalCenter.verificationCode') }}</span>
      </div>
      <div class="flex flex-wrap">
        <template v-if="valueKey === 'mobile'">
          <div
            :class="{'mobile-error':mobileError}"
            class="flex items-center w-full rounded border border-solid border-gray-slider overflow-hidden">
            <div class="flex items-center px-3">
              +{{ itc }}
            </div>
            <div class="h-4 w-0.25 bg-gray-slider"></div>
            <Input
              dataType="number"
              :allowClear="false"
              :maxlength="11"
              :bordered="false"
              class="flex-free-full"
              :placeholder="t('personalCenter.security.typeMobile')"
              @blur="validatePhone"
              @change="changeHandle($event.target.value, 'mobile')" />
          </div>
        </template>
        <template v-else>
          <Input
            :error="emailError"
            :allowClear="false"
            :maxlength="150"
            :placeholder="t('personalCenter.security.typeEmail')"
            class="relative h-8"
            @blur="validateEmail"
            @change="changeHandle($event.target.value, 'email')" />
        </template>
        <VerificationCode
          :canSend="canSend"
          :error="codeError"
          :mediumIfy="mediumIfy"
          :sendType="valueKey"
          :country="country"
          :isModify="isModify"
          class="flex-grow mt-6"
          @change="changeHandle($event, 'code')" />
      </div>
    </div>
  </Modal>
</template>
<style scoped>
.mobile-error {
  @apply border-danger;

  background-color: #f5222d0f;
}

.ant-input-affix-wrapper {
  background-color: transparent;
}
</style>
