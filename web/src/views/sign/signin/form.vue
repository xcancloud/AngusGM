<script lang="ts" setup>
import { onMounted, reactive, ref, watch } from 'vue';
import { cookie, site } from '@xcan-angus/tools';
import { redirectTo } from '@/utils/url';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';
import { login } from '@/api';

import AccountInput from '@/components/AccountInput/index.vue';
import MobileInput from '@/components/MobileInput/index.vue';
import VerificationCodeInput from '@/components/VerificationCodeInput/index.vue';
import PasswordPureInput from '@/components/PasswordBaseInput/index.vue';
import AccountSelect from '@/components/AccountSelect/index.vue';

interface Props {
  type: string;
}

const router = useRouter();

const mobile = ref();
const account = ref();
const accountPassRef = ref();
const mobilePassRef = ref();
const mobileSelectRef = ref();
const accountSelectRef = ref();
const props = withDefaults(defineProps<Props>(), {
  type: 'mobile'
});

const isMobile = ref(false);
const error = ref(false);
const errorMessage = ref(); // 注册的错误提示信息
const loading = ref(false); // 正在登录
const accountForm = reactive({
  account: undefined,
  password: undefined,
  scope: 'user_trust',
  signinType: 'ACCOUNT_PASSWORD',
  userId: undefined, // 要登录的用户
  hasPassword: false// 是否已经设置密码
});

const mobileForm = reactive({
  account: undefined,
  password: undefined,
  verificationCode: undefined,
  scope: 'user_trust',
  signinType: 'SMS_CODE',
  userId: undefined, // 要登录的用户
  hasPassword: false// 是否已经设置密码
});

const accountList = ref([]);

watch(() => props.type, newValue => {
  error.value = false;
  isMobile.value = newValue === 'mobile';
  accountForm.account = undefined;
  accountForm.password = undefined;
  accountForm.scope = 'user_trust';
  accountForm.signinType = 'ACCOUNT_PASSWORD';
  accountForm.userId = undefined; // 要登录的用户
  accountForm.hasPassword = false; // 是否已经设置密码
  mobileForm.account = undefined;
  mobileForm.password = undefined;
  mobileForm.scope = 'user_trust';
  mobileForm.signinType = 'SMS_CODE';
  mobileForm.userId = undefined; // 要登录的用户
  mobileForm.hasPassword = false; // 是否已经设置密码
  accountList.value = [];
});

const accountChange = (id) => {
  const accountInfo = accountList.value.find(item => item.userId === id);
  if (accountInfo) {
    const { userId, linkSecret, hasPassword } = accountInfo;
    if (isMobile.value) {
      mobileForm.userId = userId;
      mobileForm.password = linkSecret;
      mobileForm.hasPassword = hasPassword;
    } else {
      accountForm.userId = userId;
    }
  }

  // 选择用户后自动登录
  toSignin();
};

const toSignin = async () => {
  const { account, password, scope, signinType, userId, hasPassword } = isMobile.value ? mobileForm : accountForm;
  const params = { account, password, scope, signinType, userId };
  const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID;
  const clientSecret = import.meta.env.VITE_OAUTH_CLIENT_SECRET;
  debugger;
  const [err, res] = await login.signin({ ...params, clientSecret, clientId });

  if (err) {
    loading.value = false;
    error.value = true;
    errorMessage.value = err.message;
    accountList.value = [];
    return;
  }
  // eslint-disable-next-line camelcase
  const { access_token, refresh_token } = res.data;
  const private0 = await site.isPrivate();
  let queryParams: { accessToken: string; refreshToken: string; clientId: string };
  if (!private0) {
    cookie.set('access_token', access_token);
    cookie.set('refresh_token', refresh_token);
    cookie.set('clientId', clientId);
  } else {
    queryParams = { accessToken: access_token, refreshToken: refresh_token, clientId };
  }

  sessionStorage.removeItem('hasPassword');
  if (isMobile.value && !hasPassword) {
    sessionStorage.setItem('hasPassword', 'false');
    router.push('/password/init' + location.search);
    return;
  }

  await redirectTo(queryParams);
};

const validateAccount = () => {
  if (mobile.value) {
    return mobile.value?.validateData();
  }
  return false;
};

const validateAccountForm = () => {
  let flag = 0;
  if (!account.value?.validateData()) {
    flag++;
  }

  if (!accountPassRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const validateMobileForm = () => {
  let flag = 0;
  if (!mobile.value?.validateData()) {
    flag++;
  }

  if (!mobilePassRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const getAccount = () => {
  if (isMobile.value) {
    const params = { bizKey: 'SIGNIN', mobile: mobileForm.account, verificationCode: mobileForm.verificationCode };
    return login.checkUserMobileCode(params);
  }
  const params = { account: accountForm.account, password: accountForm.password };
  return login.getUserAcount(params);
};

const signin = async () => {
  if (accountList.value.length) {
    let isValid = false;
    if (isMobile.value) {
      isValid = mobileSelectRef.value?.validateData();
    } else {
      isValid = accountSelectRef.value?.validateData();
    }

    if (!isValid) {
      return;
    }

    toSignin();
    return;
  }
  let isInvalid = false;
  if (isMobile.value) {
    isInvalid = validateMobileForm();
  } else {
    isInvalid = validateAccountForm();
  }

  if (isInvalid) {
    return;
  }

  loading.value = true;
  error.value = false;
  // 获取账户的用户信息
  const [err, res] = await getAccount();
  if (err) {
    loading.value = false;
    error.value = true;
    errorMessage.value = err.message;
    accountList.value = [];
    return;
  }

  const data = res.data || [];
  accountList.value = data;

  if (!data.length) {
    if (mobile.value) {
      mobileForm.userId = undefined;
      mobileForm.password = mobileForm.verificationCode;
      mobileForm.hasPassword = true;
    } else {
      accountForm.userId = undefined;
    }
    toSignin();
    return;
  }
  if (data.length === 1) {
    const { userId, linkSecret, hasPassword } = data[0];
    if (isMobile.value) {
      mobileForm.userId = userId;
      mobileForm.password = linkSecret;
      mobileForm.hasPassword = hasPassword;
    } else {
      accountForm.userId = userId;
    }
    toSignin();
  }
};

const pressEnter = () => {
  if (isMobile.value) {
    if (mobileForm.account && mobileForm.verificationCode) {
      signin();
    }
  }

  if (accountForm.account && accountForm.password) {
    signin();
  }
};

watch([() => accountForm.account, () => mobileForm.account], () => {
  accountList.value = [];
  loading.value = false;
});

const editionType = ref<string>();
onMounted(async () => {
  const envContent = await site.getEnvContent();
  editionType.value = envContent?.VITE_EDITION_TYPE;
});
</script>
<template>
  <div class="text-3.5">
    <template v-if="type === 'account'">
      <AccountInput
        ref="account"
        v-model:value="accountForm.account"
        class="mb-7.5 absolute-fixed"
        @pressEnter="pressEnter" />
      <PasswordPureInput
        ref="accountPassRef"
        v-model:value="accountForm.password"
        :placeholder="$t('enter-pass')"
        class="mb-7.5 absolute-fixed"
        @pressEnter="pressEnter" />
      <template v-if="accountList.length > 1">
        <AccountSelect
          ref="accountSelectRef"
          key="email-account"
          class="mb-7.5 absolute-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    <template v-else>
      <MobileInput
        ref="mobile"
        v-model:value="mobileForm.account"
        blurPure
        class="mb-7.5 absolute-fixed"
        @pressEnter="pressEnter" />
      <VerificationCodeInput
        ref="mobilePassRef"
        v-model:value="mobileForm.verificationCode"
        bizKey="SIGNIN"
        :validateAccount="validateAccount"
        :account="mobileForm.account"
        class="mb-7.5 absolute-fixed"
        @pressEnter="pressEnter" />
      <template v-if="accountList.length > 1">
        <AccountSelect
          ref="mobileSelectRef"
          key="mobile-account"
          class="mb-7.5 absolute-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    <div :class="{ 'error': error }" class="absolute-fixed relative mb-6">
      <Button
        :loading="loading"
        class="form-btn"
        type="primary"
        size="large"
        @click="signin">
        {{ $t('signin') }}
      </Button>
      <div class="error-message">{{ errorMessage }}</div>
    </div>
    <div
      v-if="editionType==='CLOUD_SERVICE'"
      class="select-none whitespace-nowrap px-4 flex justify-between text-3.5 leading-4">
      <RouterLink
        class="text-blue-tab-active"
        to="/signup">
        {{ $t('free-registration') }}
      </RouterLink>
      <RouterLink to="/password/reset">{{ $t('forgot-password') }}</RouterLink>
    </div>
    <div v-else class="select-none whitespace-nowrap px-4 flex justify-end text-3.5 leading-4">
      <RouterLink to="/password/reset">{{ $t('forgot-password') }}</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.form-btn {
  width: 100%;
  height: 44px;
  border-radius: 24px;
}
</style>
