<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { GM, http } from '@xcan-angus/infra';
import { Hints, Modal } from '@xcan-angus/vue-ui';

import { UserInfoParams } from '@/views/personal/security/modifyMobileEmail/index.vue';
import VerificationCode from '@/views/personal/security/verificationCode/index.vue';

// Component props interface for verification modal
interface Props {
  isModify: boolean; // Whether this is for modification or binding
  visible: boolean; // Modal visibility state
  userInfo: UserInfoParams; // User information for verification
  valueKey?: 'email' | 'mobile'; // Type of verification (email or mobile)
}

// API parameters interface for verification requests
interface Params {
  country?: string; // Country code for mobile verification
  email?: string; // Email address for email verification
  mobile?: string; // Mobile number for SMS verification
  verificationCode: string; // User input verification code
  bizKey?: 'BIND_EMAIL' | 'MODIFY_EMAIL' | 'BIND_MOBILE' | 'MODIFY_MOBILE'; // Business operation type
}

const props = withDefaults(defineProps<Props>(), {
  isModify: false,
  valueKey: 'mobile',
  visible: false
});

// Component event emissions
const emit = defineEmits<{
  (e: 'cancel'): void; // Cancel verification operation
  (e: 'ok', linkSecret: string): void; // Verification successful with link secret
}>();

// Internationalization setup
const { t } = useI18n();

// Reactive state for verification modal
const code = ref<string>(); // Verification code input value
const changeHandle = (_code: string) => {
  code.value = _code;
};

const loading = ref(false); // Loading state for verification request
const codeError = ref(false); // Error state for verification code
/**
 * Handle verification code submission
 * Validates code and sends verification request to server
 */
const ok = async (): Promise<void> => {
  // Validate verification code input
  if (!code.value) {
    codeError.value = true;
    return;
  }

  // Prevent multiple submissions
  if (loading.value) {
    return;
  }

  // Prepare verification parameters
  const params: Params = {
    verificationCode: code.value
  };

  let action = '';

  // Set API endpoint and parameters based on verification type
  switch (props.valueKey) {
    case 'email':
      action = `${GM}/user/current/email/check/`;
      params.email = mediumIfy.value;
      params.bizKey = 'MODIFY_EMAIL';
      break;
    case 'mobile':
      action = `${GM}/user/current/sms/check/`;
      params.country = props.userInfo.country;
      params.mobile = mediumIfy.value;
      params.bizKey = 'MODIFY_MOBILE';
      break;
  }

  // Send verification request
  loading.value = true;
  const [error, res] = await http.get(action, params);
  loading.value = false;

  if (error) {
    codeError.value = true;
    return;
  }

  // Emit success with link secret and reset form
  emit('ok', res.data.linkSecret);
  reset();
};

/**
 * Cancel verification operation
 * Emits cancel event and resets form state
 */
const cancel = (): void => {
  emit('cancel');
  reset();
};

/**
 * Reset form state to initial values
 * Clears verification code and error states
 */
const reset = () => {
  code.value = undefined;
  codeError.value = false;
};

// Computed properties for dynamic content and display

/**
 * Get the display name for the verification type (mobile or email)
 */
const typeIfy = computed(() => {
  return props.valueKey === 'mobile' ? t('securities.columns.mobile') : t('securities.columns.email');
});

/**
 * Generate modal title based on verification type
 */
const title = computed(() => {
  return `${typeIfy.value}${t('securities.messages.validation')}`;
});

/**
 * Generate modal description with verification type context
 */
const description = computed(() => {
  return t('securities.messages.modalDescription', { typeIfy: typeIfy.value });
});

/**
 * Generate description text showing where verification code will be sent
 */
const prevDesc = computed(() => {
  return `${props.valueKey === 'mobile' ? t('common.status.sms') : t('securities.columns.email')}${t('securities.messages.validation')}，${t('securities.messages.receive')}${typeIfy.value}：`;
});

/**
 * Get the current medium (mobile/email) value from user info
 * Returns empty string if user info is not available
 */
const mediumIfy = computed((): string => {
  if (!props.userInfo) {
    return '';
  }

  const { mobile, email } = props.userInfo;
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
  <!-- Verification Modal -->
  <Modal
    :closable="false"
    :title="title"
    :visible="visible"
    :confirmLoading="loading"
    :okText="t('securities.messages.nextStep')"
    @ok="ok"
    @cancel="cancel">
    <!-- Modal Description Hint -->
    <Hints :text="description" />

    <!-- Verification Target Information -->
    <div class="mt-8 text-theme-content">
      {{ prevDesc }}
      <span class="content-primary-text">
        {{ props.valueKey === 'mobile' ? ('+' + userInfo.itc + ' ' + mediumIfy) : mediumIfy }}
      </span>
    </div>

    <!-- Verification Code Input Section -->
    <div class="flex items-center mt-5.5">
      <span class="flex items-center flex-grow-0 whitespace-nowrap text-theme-title mr-3">
        {{ t('securities.columns.verificationCode') }}
      </span>

      <!-- Verification Code Component -->
      <VerificationCode
        class="flex-grow"
        :error="codeError"
        :country="userInfo.country"
        :mediumIfy="mediumIfy"
        :sendType="valueKey"
        :isModify="isModify"
        @change="changeHandle" />
    </div>
  </Modal>
</template>
