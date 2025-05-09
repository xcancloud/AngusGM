<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { notification } from '@xcan-angus/vue-ui';
import { Button } from 'ant-design-vue';
import { login } from '@/api';

import PasswordInput from '@/components/PasswordInput/index.vue';
import PasswordConfirmInput from '@/components/PasswordConfirmInput/index.vue';
import VerificationCodeInput from '@/components/VerificationCodeInput/index.vue';
import EmailInput from '@/components/EmailInput/index.vue';
import MobileInput from '@/components/MobileInput/index.vue';
import AccountSelect from '@/components/AccountSelect/index.vue';

interface IFormParams {
  account: string;
  newPassword: string;
  linkSecret: string;
  confirmPassword: string;
  verificationCode: string;
  userId: string;
  accountDisabled: boolean;
  passwordDisabled: boolean;
}

interface Props {
  type: string;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'mobile'
});

const router = useRouter();

const loading = ref(false);
const error = ref(false);
const errorMessage = ref<string>();

const mobileRef = ref();
const mobileVeriRef = ref();
const mobilePassRef = ref();
const mobileConfirmRef = ref();
const mobileSelectRef = ref();
const emailRef = ref();
const emailVeriRef = ref();
const emailPassRef = ref();
const emailConfirmRef = ref();
const emailSelectRef = ref();

const accountList = ref<Record<string, any>[]>([]);
const emailForm = ref<IFormParams>({
  account: '',
  newPassword: '',
  linkSecret: '',
  confirmPassword: '',
  verificationCode: '',
  userId: '',
  accountDisabled: false,
  passwordDisabled: false
});

const mobileForm = ref<IFormParams>({
  account: '',
  newPassword: '',
  linkSecret: '',
  confirmPassword: '',
  verificationCode: '',
  userId: '',
  accountDisabled: false,
  passwordDisabled: false
});

const isMobile = computed(() => {
  return props.type === 'mobile';
});

watch(() => props.type, () => {
  error.value = false;
  emailForm.value = {
    account: '',
    newPassword: '',
    linkSecret: '',
    confirmPassword: '',
    verificationCode: '',
    userId: '',
    accountDisabled: false,
    passwordDisabled: false
  };

  mobileForm.value = {
    account: '',
    newPassword: '',
    linkSecret: '',
    confirmPassword: '',
    verificationCode: '',
    userId: '',
    accountDisabled: false,
    passwordDisabled: false
  };

  accountList.value = [];
});

const accountChange = (id: string) => {
  const accountInfo = accountList.value.find(item => item.userId === id);
  if (!accountInfo) {
    return;
  }

  const { userId, linkSecret } = accountInfo;
  if (isMobile.value) {
    mobileForm.value.userId = userId;
    mobileForm.value.linkSecret = linkSecret;
    return;
  }

  emailForm.value.userId = userId;
  emailForm.value.linkSecret = linkSecret;
};
const validateAccount = () => {
  if (mobileRef.value.validateData) {
    return mobileRef.value.validateData();
  }
};

const validateEmail = () => {
  if (emailRef.value.validateData) {
    return emailRef.value.validateData();
  }
};

const validateMobileForm = () => {
  let flag = 0;
  if (!mobileRef.value?.validateData()) {
    flag++;
  }

  if (!mobileVeriRef.value?.validateData()) {
    flag++;
  }

  if (!mobilePassRef.value?.validateData()) {
    flag++;
  }

  if (!mobileConfirmRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const validateEmailForm = () => {
  let flag = 0;
  if (!emailRef.value?.validateData()) {
    flag++;
  }

  if (!emailVeriRef.value?.validateData()) {
    flag++;
  }

  if (!emailPassRef.value?.validateData()) {
    flag++;
  }

  if (!emailConfirmRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const getAccount = () => {
  if (isMobile.value) {
    const params = {
      bizKey: 'PASSWORD_FORGET',
      mobile: mobileForm.value.account,
      verificationCode: mobileForm.value.verificationCode
    };
    return login.getUserAcount(params);
  }

  const params = {
    bizKey: 'PASSWORD_FORGET',
    email: emailForm.value.account,
    verificationCode: emailForm.value.verificationCode
  };
  return login.checkUserEmaillCode(params);
};

const toUpdate = async () => {
  const { userId, newPassword, linkSecret } = isMobile.value ? mobileForm.value : emailForm.value;
  const [err] = await login.resetPassword({ id: userId, newPassword, linkSecret });
  loading.value = false;
  if (err) {
    error.value = true;
    errorMessage.value = err.message;
    return;
  }
  notification.success('重置密码成功');
  router.push('/signin');
};

const confirm = async () => {
  if (accountList.value.length > 1) {
    let isValid = false;
    if (isMobile.value) {
      isValid = mobileSelectRef.value?.validateData();
    } else {
      isValid = emailSelectRef.value?.validateData();
    }

    if (!isValid) {
      return;
    }

    toUpdate();
    return;
  }

  let isInvalid = false;
  if (isMobile.value) {
    isInvalid = validateMobileForm();
  } else {
    isInvalid = validateEmailForm();
  }

  if (isInvalid) {
    return;
  }

  loading.value = true;
  error.value = false;

  // 获取账户的用户信息
  const [e1, res] = await getAccount();
  if (e1) {
    loading.value = false;
    error.value = true;
    errorMessage.value = e1.message;
    accountList.value = [];
    return;
  }

  loading.value = false;
  const data = res.data || [];
  accountList.value = data;
  if (data.length === 1) {
    const { userId, linkSecret } = data[0];
    if (isMobile.value) {
      mobileForm.value.userId = userId;
      mobileForm.value.linkSecret = linkSecret;
      mobileForm.value.accountDisabled = true;
      mobileForm.value.passwordDisabled = true;
    } else {
      emailForm.value.userId = userId;
      emailForm.value.linkSecret = linkSecret;
      emailForm.value.accountDisabled = true;
      emailForm.value.passwordDisabled = true;
    }
    toUpdate();
  }
};
</script>

<template>
  <div>
    <template v-if="isMobile">
      <MobileInput
        ref="mobileRef"
        v-model:value="mobileForm.account"
        :disabled="!!mobileForm.userId"
        class="mb-5 block-fixed" />
      <VerificationCodeInput
        ref="mobileVeriRef"
        key="mobileVeriRef"
        v-model:value="mobileForm.verificationCode"
        bizKey="PASSWORD_FORGET"
        :disabled="!!mobileForm.userId"
        :validateAccount="validateAccount"
        :account="mobileForm.account"
        class="mb-5 block-fixed" />
      <PasswordInput
        key="mobilePassRef"
        ref="mobilePassRef"
        v-model:value="mobileForm.newPassword"
        tipClass="custom-tip"
        :showTip="true"
        :placeholder="$t('reset-password')"
        class="mb-5 block-fixed" />
      <PasswordConfirmInput
        key="mobileConfirmRef"
        ref="mobileConfirmRef"
        v-model:value="mobileForm.confirmPassword"
        :password="mobileForm.newPassword"
        :placeholder="$t('confirm-pass')"
        class="mb-5 block-fixed" />
      <template v-if="accountList.length>1">
        <AccountSelect
          ref="mobileSelectRef"
          key="mobile-account"
          v-model:value="mobileForm.userId"
          class="mb-5 block-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    <template v-else>
      <EmailInput
        ref="emailRef"
        v-model:value="emailForm.account"
        :disabled="!!emailForm.userId"
        class="mb-5 block-fixed" />
      <VerificationCodeInput
        ref="emailVeriRef"
        key="emailVeriRef"
        v-model:value="emailForm.verificationCode"
        sendType="email"
        bizKey="PASSWORD_FORGET"
        :disabled="!!emailForm.userId"
        :validateAccount="validateEmail"
        :account="emailForm.account"
        class="mb-5 block-fixed" />
      <PasswordInput
        key="emailPassRef"
        ref="emailPassRef"
        v-model:value="emailForm.newPassword"
        tipClass="custom-tip"
        :showTip="true"
        :placeholder="$t('reset-password')"
        class="mb-5 block-fixed" />
      <PasswordConfirmInput
        key="emailConfirmRef"
        ref="emailConfirmRef"
        v-model:value="emailForm.confirmPassword"
        :password="emailForm.newPassword"
        :placeholder="$t('confirm-pass')"
        class="mb-5 block-fixed" />
      <template v-if="accountList.length>1">
        <AccountSelect
          ref="emailSelectRef"
          key="email-account"
          v-model:value="emailForm.userId"
          class="mb-5 block-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    <div :class="{'error':error}" class="relative mb-4 block-fixed">
      <Button
        :loading="loading"
        class="rounded-full w-full"
        type="primary"
        size="large"
        @click="confirm">
        {{ $t('ok') }}
      </Button>
      <div class="error-message">{{ errorMessage }}</div>
    </div>
    <RouterLink
      class="select-none whitespace-nowrap flex justify-center items-center mt-6 text-4 leading-4 text-blue-tab-active"
      to="/signin">
      <img class="relative top-0.25 mr-1" src="./assets/left.png" />{{ $t('return-login') }}
    </RouterLink>
  </div>
</template>

<style scoped>
.custom-tip.tip-container {
  background-color: rgba(255, 255, 255, 100%);
  box-shadow: 0 3px 6px -4px #0000001f, 0 6px 16px #00000014, 0 9px 28px 8px #0000000d;
}
</style>
