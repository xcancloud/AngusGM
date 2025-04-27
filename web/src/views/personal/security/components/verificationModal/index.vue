<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { http } from '@xcan/utils';
import { Modal, Hints } from '@xcan/design';
import { GM } from '@xcan/sdk';

import { UserInfoParams } from '@/views/personal/security/components/modifyMobileEmail/index.vue';
import VerificationCode from '@/views/personal/security/components/verificationCode/index.vue';

interface Props {
  isModify: boolean,
  visible: boolean,
  tenantInfo: UserInfoParams,
  valueKey?: 'email' | 'mobile',
}

interface Params {
  country?: string,
  email?: string,
  mobile?: string,
  verificationCode: string,
  bizKey?: 'BIND_EMAIL' | 'MODIFY_EMAIL' | 'BIND_MOBILE' | 'MODIFY_MOBILE'
}

const props = withDefaults(defineProps<Props>(), {
  isModify: false,
  valueKey: 'mobile',
  visible: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'cancel'): void,
  (e: 'ok', linkSecret: string): void,
}>();

const { t } = useI18n();

const code = ref();// 验证码
const changeHandle = (_code: string) => {
  code.value = _code;
};

const loading = ref(false);
const codeError = ref(false);
const ok = async (): Promise<void> => {
  if (!code.value) {
    codeError.value = true;
    return;
  }
  if (loading.value) {
    return;
  }

  const params: Params = {
    verificationCode: code.value
  };
  let action = '';
  switch (props.valueKey) {
    case 'email':
      action = `${GM}/user/current/email/check/`;
      params.email = mediumIfy.value;
      params.bizKey = 'MODIFY_EMAIL';
      break;
    case 'mobile':
      action = `${GM}/user/current/sms/check/`;
      params.country = props.tenantInfo.country;
      params.mobile = mediumIfy.value;
      params.bizKey = 'MODIFY_MOBILE';
      break;
  }

  loading.value = true;
  const [error, res] = await http.get(action, params);
  loading.value = false;
  if (error) {
    codeError.value = true;
    return;
  }
  emit('ok', res.data.linkSecret);
  reset();
};

const cancel = (): void => {
  emit('cancel');
  reset();
};

const reset = () => {
  code.value = undefined;
  codeError.value = false;
};

const typeIfy = computed(() => {
  return props.valueKey === 'mobile' ? t('personalCenter.mobile') : t('personalCenter.email');
});

const title = computed(() => {
  return `${typeIfy.value}${t('personalCenter.validation')}`;
});

const description = computed(() => {
  return t('personalCenter.security.modalDescription', { typeIfy: typeIfy.value });
});

const prevDesc = computed(() => {
  return `${props.valueKey === 'mobile' ? t('personalCenter.sms') : t('personalCenter.email')}${t('personalCenter.validation')}，${t('personalCenter.receive')}${typeIfy.value}：`;
});

const mediumIfy = computed((): string => {
  if (!props.tenantInfo) {
    return '';
  }

  const { mobile, email } = props.tenantInfo;
  switch (props.valueKey) {
    case 'mobile':
      return mobile || '';
    case 'email':
      return email || '';
    default:
      return '';
  }
});

</script>
<template>
  <Modal
    :closable="false"
    :title="title"
    :visible="visible"
    :confirmLoading="loading"
    :okText="t('personalCenter.nextStep')"
    @ok="ok"
    @cancel="cancel">
    <Hints :text="description" />
    <div class="mt-8 text-theme-content">
      {{ prevDesc }}
      <span
        class="content-primary-text">{{ props.valueKey === 'mobile' ? ('+' + tenantInfo.itc + ' ' + mediumIfy) : mediumIfy }}</span>
    </div>
    <div class="flex items-center mt-5.5">
      <span class="flex items-center flex-grow-0 whitespace-nowrap text-theme-title mr-3">{{ t('personalCenter.verificationCode') }}</span>
      <VerificationCode
        class="flex-grow"
        :error="codeError"
        :country="tenantInfo.country"
        :mediumIfy="mediumIfy"
        :sendType="valueKey"
        :isModify="isModify"
        @change="changeHandle" />
    </div>
  </Modal>
</template>
