<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Modal, notification } from '@xcan-angus/vue-ui';
import { Button, Checkbox } from 'ant-design-vue';
import { login } from '@/api';
import DOMPurify from 'dompurify';

import PasswordInput from '@/components/PasswordInput/index.vue';
import PasswordConfirmInput from '@/components/PasswordConfirmInput/index.vue';
import InvitationCodeInput from '@/components/InvitationCodeInput/index.vue';
import VerificationCodeInput from '@/components/VerificationCodeInput/index.vue';
import EmailInput from '@/components/EmailInput/index.vue';
import MobileInput from '@/components/MobileInput/index.vue';

import { privacyPolicy, termsAndConditions } from './config';

/**
 * Component props interface
 * Defines the registration type (mobile or email)
 */
interface Props {
  type: 'mobile' | 'email';
}

const { t } = useI18n();
const router = useRouter();
const props = withDefaults(defineProps<Props>(), {
  type: 'mobile'
});

// Form state management
const error = ref(false);
const errorMessage = ref<string>(); // Registration error message
const loading = ref(false); // Registration request loading state
const checked = ref(false); // Terms and conditions acceptance status

/**
 * Email registration form data
 * Contains all fields needed for email-based registration
 */
const emailForm = reactive({
  account: undefined as string | undefined,
  invitationCode: undefined as string | undefined,
  password: undefined as string | undefined,
  confirmPassword: undefined as string | undefined,
  signupType: 'EMAIL',
  socialCode: undefined as string | undefined,
  verificationCode: undefined as string | undefined
});

/**
 * Mobile registration form data
 * Contains all fields needed for mobile-based registration
 */
const mobileForm = reactive({
  account: undefined as string | undefined,
  country: 'CN',
  invitationCode: undefined as string | undefined,
  itc: '86',
  password: undefined as string | undefined,
  confirmPassword: undefined as string | undefined,
  signupType: 'MOBILE',
  socialCode: undefined as string | undefined,
  verificationCode: undefined as string | undefined
});

// Terms and privacy policy state
const terms = ref();
const privacy = ref();
const termsContent = ref<string>('');
const visible = ref(false);
const modelTitle = ref('');

// Component references for validation
const mobileRef = ref();
const emailRef = ref();
const mobileVerificationRef = ref();
const mobilePassRef = ref();
const mobileConfirmpassRef = ref();
const emailConfirmpassRef = ref();
const emailPassRef = ref();
const emailVeriRef = ref();

// Validation state
const validateData = ref(false);

/**
 * Check if current registration type is mobile
 */
const isMobile = computed(() => {
  return props.type === 'mobile';
});

/**
 * Watch for type changes and reset error state
 */
watch(() => props.type, () => {
  error.value = false;
});

// Terms and conditions loading (commented out for now)
// const loadTerms = () => {
//   http.get(`${PUB_ESS}/content/setting/termsAndConditions`)
//     .then(([error, resp]) => {
//       if (error) {
//         return;
//       }
//       terms.value = resp.data;
//     });
//   http.get(`${PUB_ESS}/content/setting/privacyPolicy`)
//     .then(([error, resp]) => {
//       if (error) {
//         return;
//       }
//       privacy.value = resp?.data || '';
//     });
// };

/**
 * Open modal for terms or privacy policy
 * Sets appropriate title and content based on key
 */
const openModal = (key: string) => {
  if (key === 'termsVisible') {
    modelTitle.value = t('xcan_cloud') + t('terms_cloud');
    termsContent.value = termsAndConditions;
  } else {
    modelTitle.value = t('xcan_cloud') + t('privacy_cloud');
    termsContent.value = privacyPolicy;
  }
  visible.value = !visible.value;
};

/**
 * Close modal dialog
 */
const closeModel = () => {
  visible.value = !visible.value;
};

/**
 * Validate mobile account input
 */
const validateAccount = () => {
  if (mobileRef.value?.validateData) {
    return mobileRef.value.validateData();
  }
  return false;
};

/**
 * Validate email account input
 */
const validateEmail = () => {
  if (emailRef.value?.validateData) {
    return emailRef.value.validateData();
  }
  return false;
};

/**
 * Validate mobile form inputs
 * @returns {boolean} true if validation fails
 */
const validateMobileForm = () => {
  let validationErrors = 0; // Count validation failures
  
  if (!mobileRef.value?.validateData()) {
    validationErrors++;
  }

  if (!mobileVerificationRef.value?.validateData()) {
    validationErrors++;
  }

  if (!mobilePassRef.value?.validateData()) {
    validationErrors++;
  }

  if (!mobileConfirmpassRef.value?.validateData()) {
    validationErrors++;
  }

  return validationErrors > 0;
};

/**
 * Validate email form inputs
 * @returns {boolean} true if validation fails
 */
const validateEmailForm = () => {
  let validationErrors = 0; // Count validation failures
  
  if (!emailRef.value?.validateData()) {
    validationErrors++;
  }

  if (!emailVeriRef.value?.validateData()) {
    validationErrors++;
  }

  if (!emailPassRef.value?.validateData()) {
    validationErrors++;
  }

  if (!emailConfirmpassRef.value?.validateData()) {
    validationErrors++;
  }

  return validationErrors > 0;
};

/**
 * Main registration function
 * Validates form, submits registration request, and handles response
 */
const signup = async () => {
  let isInvalid = false;
  if (isMobile.value) {
    isInvalid = validateMobileForm();
  } else {
    isInvalid = validateEmailForm();
  }

  // Validation failed or terms not accepted
  if (isInvalid || !checked.value) {
    validateData.value = true;
    return;
  }
  
  loading.value = true;
  error.value = false;
  
  try {
    const params = isMobile.value ? mobileForm : emailForm;
    const [err] = await login.signup(params);
    
    if (err) {
      error.value = true;
      errorMessage.value = err.message;
      return;
    }
    
    notification.success('注册成功');
    router.push('/signin');
  } finally {
    loading.value = false;
    validateData.value = false;
  }
};

// Load terms on mount (commented out for now)
// onMounted(() => {
//   loadTerms();
// });
</script>

<template>
  <div>
    <!-- Mobile registration form -->
    <template v-if="isMobile">
      <MobileInput
        ref="mobileRef"
        v-model:value="mobileForm.account"
        class="input-container block-fixed" />
      <VerificationCodeInput
        key="mobile-veri"
        ref="mobileVerificationRef"
        v-model:value="mobileForm.verificationCode"
        :validateAccount="validateAccount"
        :account="mobileForm.account"
        class="input-container block-fixed" />
      <PasswordInput
        key="mobile-pass"
        ref="mobilePassRef"
        v-model:value="mobileForm.password"
        :showTip="true"
        :placeholder="$t('enter-pass')"
        class="input-container block-fixed" />
      <PasswordConfirmInput
        key="mobile-confirmpass"
        ref="mobileConfirmpassRef"
        v-model:value="mobileForm.confirmPassword"
        :password="mobileForm.password"
        :placeholder="$t('confirm-pass')"
        class="input-container block-fixed" />
      <InvitationCodeInput
        key="mobile-invite"
        v-model:value="mobileForm.invitationCode"
        class="input-container block-fixed" />
    </template>
    
    <!-- Email registration form -->
    <template v-else>
      <EmailInput
        ref="emailRef"
        v-model:value="emailForm.account"
        class="input-container block-fixed" />
      <VerificationCodeInput
        key="email-veri"
        ref="emailVeriRef"
        v-model:value="emailForm.verificationCode"
        sendType="email"
        :validateAccount="validateEmail"
        :account="emailForm.account"
        class="input-container block-fixed" />
      <PasswordInput
        key="email-pass"
        ref="emailPassRef"
        v-model:value="emailForm.password"
        :showTip="true"
        :placeholder="$t('enter-pass')"
        class="input-container block-fixed" />
      <PasswordConfirmInput
        key="email-confirmpass"
        ref="emailConfirmpassRef"
        v-model:value="emailForm.confirmPassword"
        :password="emailForm.password"
        :placeholder="$t('confirm-pass')"
        class="input-container block-fixed" />
      <InvitationCodeInput
        key="email-invite"
        v-model:value="emailForm.invitationCode"
        class="input-container block-fixed" />
    </template>
    
    <!-- Submit button and error display -->
    <div :class="{'error': error}" class="error-container block-fixed">
      <Button
        :loading="loading"
        class="form-btn"
        type="primary"
        size="large"
        @click="signup">
        {{ t('register') }}
      </Button>
      <div class="error-message">{{ errorMessage }}</div>
    </div>
    
    <!-- Sign in link -->
    <div class="link-container">
      <RouterLink class="link" to="/signin">{{ t('account-signin') }}</RouterLink>
    </div>
    
    <!-- Terms and privacy policy links -->
    <div class="text-group">
      <em class="require">*</em>
      <span>{{ t('xcan_cloud') }}</span>
      <span class="link" @click="openModal('termsVisible')">{{ t('terms_cloud') }}</span>
      <span class="link" @click="openModal('prvacyVisible')">{{ t('privacy_cloud') }}</span>
    </div>
    
    <!-- Terms acceptance checkbox -->
    <div class="flex items-center">
      <Checkbox v-model:checked="checked" class="checkbox-container">
        {{ t('clause') }}
      </Checkbox>
      <span class="error-message !px-1" :class="{'!block': !checked && validateData}">请勾选阅读并同意</span>
    </div>
    
    <!-- Terms and privacy policy modal -->
    <Modal
      key="termsVisible"
      v-model:visible="visible"
      :title="modelTitle"
      @ok="closeModel">
      <div class="modal-content" v-html="DOMPurify.sanitize(termsContent)"></div>
    </Modal>
  </div>
</template>

<style scoped>
.require {
  position: relative;
  top: 2px;
  margin-right: 2px;
  color: #f5222d;
  font-size: 14px;
  font-style: normal;
  line-height: 18px;
}

.input-container {
  margin-bottom: 20px;
}

.error-container {
  position: relative;
  margin-bottom: 16px;
}

.form-btn {
  width: 100%;
  height: 44px;
  border-radius: 24px;
}

.link-container {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
  line-height: 20px;
  user-select: none;
}

.link-container .link {
  color: rgba(24, 144, 255, 100%);
  white-space: nowrap;
}

.text-group {
  font-size: 14px;
}

.text-group .link {
  color: rgba(24, 144, 255, 100%);
  cursor: pointer;
}

.checkbox-container {
  color: rgba(68, 76, 90, 100%);
  user-select: none;
}

.modal-content {
  max-height: 600px;
  overflow: auto;
}
</style>
