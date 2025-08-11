<script lang="ts" setup>
import { onMounted, reactive, ref, watch } from 'vue';
import { appContext, cookieUtils, type TokenInfo } from '@xcan-angus/infra';
import { redirectTo } from '@/utils/url';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';
import { login } from '@/api';

import AccountInput from '@/components/AccountInput/index.vue';
import MobileInput from '@/components/MobileInput/index.vue';
import VerificationCodeInput from '@/components/VerificationCodeInput/index.vue';
import PasswordPureInput from '@/components/PasswordBaseInput/index.vue';
import AccountSelect from '@/components/AccountSelect/index.vue';

/**
 * Component Props Interface
 * 
 * Defines the authentication type for the form,
 * supporting 'mobile' or 'account' authentication methods
 */
interface Props {
  type: 'mobile' | 'account';
}

const router = useRouter();

/**
 * Component References
 * 
 * References to child components for validation and data access
 */
const mobile = ref();
const account = ref();
const accountPassRef = ref();
const mobilePassRef = ref();
const mobileSelectRef = ref();
const accountSelectRef = ref();

const props = withDefaults(defineProps<Props>(), {
  type: 'account'
});

/**
 * Component State Management
 * 
 * Centralized reactive state for managing form behavior,
 * authentication flow, and user interactions
 */
const isMobile = ref(false);
const error = ref(false);
const errorMessage = ref<string>(); // Error message for authentication failures
const loading = ref(false); // Loading state during authentication process

/**
 * Account Authentication Form State
 * 
 * Manages account-based authentication data including
 * credentials, scope, and user identification
 */
const accountForm = reactive({
  account: undefined as string | undefined,
  password: undefined as string | undefined,
  scope: 'user_trust',
  signinType: 'ACCOUNT_PASSWORD',
  userId: '', // Target user ID for login
  hasPassword: false // Whether user has set a password
});

/**
 * Mobile Authentication Form State
 * 
 * Manages mobile-based authentication data including
 * verification codes and user identification
 */
const mobileForm = reactive({
  account: undefined as string | undefined,
  password: undefined as string | undefined,
  verificationCode: undefined as string | undefined,
  scope: 'user_trust',
  signinType: 'SMS_CODE',
  userId: '', // Target user ID for login
  hasPassword: false // Whether user has set a password
});

// List of available accounts for multi-account users
const accountList = ref<Array<{
  userId: string;
  linkSecret: string;
  hasPassword: boolean;
}>>([]);

/**
 * Watch Authentication Type Changes
 * 
 * Resets form state and clears data when switching between
 * mobile and account authentication methods
 */
watch(() => props.type, newValue => {
  error.value = false;
  isMobile.value = newValue === 'mobile';
  
  // Reset account form state
  accountForm.account = undefined;
  accountForm.password = undefined;
  accountForm.scope = 'user_trust';
  accountForm.signinType = 'ACCOUNT_PASSWORD';
  accountForm.userId = '';
  accountForm.hasPassword = false;
  
  // Reset mobile form state
  mobileForm.account = undefined;
  mobileForm.password = undefined;
  mobileForm.verificationCode = undefined;
  mobileForm.scope = 'user_trust';
  mobileForm.signinType = 'SMS_CODE';
  mobileForm.userId = '';
  mobileForm.hasPassword = false;
  
  accountList.value = [];
});

/**
 * Handle Account Selection Change
 * 
 * Processes user selection from multi-account list and
 * automatically initiates login process
 */
const accountChange = (id: string | undefined) => {
  if (!id) return;
  
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

  // Automatically login after user selection
  toSignin();
};

/**
 * Execute Sign In Process
 * 
 * Performs the actual authentication request with the backend,
 * handles token storage, and manages post-login flow
 */
const toSignin = async () => {
  const { account, password, scope, signinType, userId, hasPassword } = isMobile.value ? mobileForm : accountForm;
  const params = { account, password, scope, signinType, userId };
  const clientId = appContext.getContext().env.oauthClientId || '';
  const clientSecret = appContext.getContext().env.oauthClientSecret || '';
  
  try {
    const [err, res] = await login.signin({ ...params, clientSecret, clientId });

    if (err) {
      error.value = true;
      errorMessage.value = err.message;
      accountList.value = [];
      return;
    }
    
    // Extract authentication tokens from response
    // eslint-disable-next-line camelcase
    const { access_token, refresh_token, expires_in } = res.data;
    const isCloudServiceEdition = appContext.isCloudServiceEdition();
    let queryParams: { accessToken: string; refreshToken: string; clientId: string } | undefined;
    
    if (isCloudServiceEdition) {
      // Store token info in cookies for cloud service edition
      const tokenInfo: TokenInfo = {
        access_token,
        refresh_token,
        expires_in,
        request_auth_time: new Date().toISOString()
      };
      cookieUtils.setTokenInfo(tokenInfo);
    } else {
      // Prepare query parameters for non-cloud editions
      queryParams = { accessToken: access_token, refreshToken: refresh_token, clientId };
    }

    // Clear session storage and handle password initialization
    sessionStorage.removeItem('hasPassword');
    if (isMobile.value && !hasPassword) {
      sessionStorage.setItem('hasPassword', 'false');
      router.push('/password/init' + location.search);
      return;
    }

    // Redirect to appropriate destination
    await redirectTo(queryParams);
  } finally {
    loading.value = false;
  }
};

/**
 * Validate Account Input
 * 
 * Delegates validation to the appropriate input component
 * based on current authentication type
 */
const validateAccount = () => {
  if (mobile.value?.validateData) {
    return mobile.value.validateData();
  }
  return false;
};

/**
 * Validate Account Form
 * 
 * Performs comprehensive validation of account form fields
 * and returns validation status
 */
const validateAccountForm = () => {
  let validationErrors = 0;
  
  if (!account.value?.validateData()) {
    validationErrors++;
  }

  if (!accountPassRef.value?.validateData()) {
    validationErrors++;
  }

  return validationErrors > 0;
};

/**
 * Validate Mobile Form
 * 
 * Performs comprehensive validation of mobile form fields
 * and returns validation status
 */
const validateMobileForm = () => {
  let validationErrors = 0;
  
  if (!mobile.value?.validateData()) {
    validationErrors++;
  }

  if (!mobilePassRef.value?.validateData()) {
    validationErrors++;
  }

  return validationErrors > 0;
};

/**
 * Get Account Information
 * 
 * Retrieves user account information from backend based on
 * current authentication type and form data
 */
const getAccount = () => {
  if (isMobile.value) {
    const params = { 
      bizKey: 'SIGNIN', 
      mobile: mobileForm.account, 
      verificationCode: mobileForm.verificationCode 
    };
    return login.checkUserMobileCode(params);
  }
  
  const params = { 
    account: accountForm.account, 
    password: accountForm.password 
  };
  return login.getUserAcount(params);
};

/**
 * Main Sign In Function
 * 
 * Orchestrates the complete sign-in process including
 * validation, account retrieval, and authentication
 */
const signin = async () => {
  if (accountList.value.length > 0) {
    // Handle multi-account scenario
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
  
  // Handle single account scenario
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
  
  try {
    // Retrieve account information from backend
    const [err, res] = await getAccount();
    if (err) {
      error.value = true;
      errorMessage.value = err.message;
      accountList.value = [];
      return;
    }

    const data = res.data || [];
    accountList.value = data;

         if (!data.length) {
       // No accounts found, proceed with direct login
       if (isMobile.value) {
         mobileForm.userId = '';
         mobileForm.password = mobileForm.verificationCode;
         mobileForm.hasPassword = true;
       } else {
         accountForm.userId = '';
       }
       toSignin();
       return;
     }
    
    if (data.length === 1) {
      // Single account found, auto-login
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
  } finally {
    loading.value = false;
  }
};

/**
 * Handle Enter Key Press
 * 
 * Triggers sign-in process when user presses Enter key
 * in form fields, providing keyboard navigation support
 */
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

/**
 * Watch Account Input Changes
 * 
 * Clears account list and resets loading state when
 * user modifies account input fields
 */
watch([() => accountForm.account, () => mobileForm.account], () => {
  accountList.value = [];
  loading.value = false;
});

// Edition type for conditional UI rendering
const editionType = ref<string>();

/**
 * Component Mounted Lifecycle
 * 
 * Initializes component state and retrieves edition type
 * for conditional feature rendering
 */
onMounted(async () => {
  editionType.value = appContext.getEditionType();
});
</script>

<template>
  <div class="text-3.5">
    <!-- Account-based Authentication Form -->
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
      <!-- Multi-account Selection for Account Users -->
      <template v-if="accountList.length > 1">
        <AccountSelect
          ref="accountSelectRef"
          key="email-account"
          v-model:value="accountForm.userId"
          class="mb-7.5 absolute-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    
    <!-- Mobile-based Authentication Form -->
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
      <!-- Multi-account Selection for Mobile Users -->
      <template v-if="accountList.length > 1">
        <AccountSelect
          ref="mobileSelectRef"
          key="mobile-account"
          v-model:value="mobileForm.userId"
          class="mb-7.5 absolute-fixed"
          :dataSource="accountList"
          @change="accountChange" />
      </template>
    </template>
    
    <!-- Sign In Button and Error Display -->
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
    
    <!-- Cloud Service Edition Links -->
    <div
      v-if="editionType === 'CLOUD_SERVICE'"
      class="select-none whitespace-nowrap px-4 flex justify-between text-3.5 leading-4">
      <RouterLink
        class="text-blue-tab-active"
        to="/signup">
        {{ $t('free-registration') }}
      </RouterLink>
      <RouterLink to="/password/reset">{{ $t('forgot-password') }}</RouterLink>
    </div>
    
    <!-- Standard Edition Links -->
    <div v-else class="select-none whitespace-nowrap px-4 flex-end text-3.5 leading-4">
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
