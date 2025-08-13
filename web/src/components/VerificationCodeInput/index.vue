<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue';
import { http, PUB_GM } from '@xcan-angus/infra';
import { Input } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

/**
 * Function type definition for account validation
 * Used to validate account before sending verification code
 */
type ValidationFunction = (value?: any) => any;

/**
 * Component props interface
 * Defines the structure for verification code input configuration
 */
interface Props {
  value: string; // Verification code value
  account: string | number; // Account (mobile/email) to send code to
  sendType?: string; // Type of verification: 'mobile' or 'email'
  bizKey?: string; // Business key for verification code
  disabled?: boolean; // Whether the component is disabled
  validateAccount?: ValidationFunction; // Function to validate account before sending code
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  account: '',
  bizKey: 'SIGNUP',
  sendType: 'mobile',
  disabled: false,
  validateAccount: undefined
});

/**
 * Component emits definition
 * Defines the events that this component can emit
 */
const emit = defineEmits<{
  (e: 'update:value', value: string): void;
  (e: 'pressEnter'): void;
}>();

// Reactive state variables
const inputValue = ref<string>('');
const error = ref(false);
const sent = ref(false);
const second = ref(60);
const errorMessage = ref(t('components.verificationCodeInput.messages.errorVerifyCode'));

// Timer reference for countdown functionality
let timer: NodeJS.Timeout | undefined;

/**
 * Watch for changes in input value and emit updates
 * Keeps parent component in sync with input changes
 */
watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
});

/**
 * Handle Enter key press event
 * Emits pressEnter event to parent component
 */
const pressEnter = (): void => {
  emit('pressEnter');
};

/**
 * Send verification code to the specified account
 * Handles both mobile SMS and email verification code sending
 */
const getCode = async (): Promise<void> => {
  if (props.disabled) {
    return;
  }

  // Validate account if validation function is provided
  if (typeof props.validateAccount === 'function') {
    const isValid = props.validateAccount();
    if (!isValid) {
      return;
    }
  }

  // Prevent resending if code was already sent and countdown is active
  if (sent.value) {
    return;
  }

  // Prepare API parameters based on send type
  let params: Record<string, any> = {};
  let action = '';

  if (props.sendType === 'mobile') {
    // Mobile SMS verification code
    params = {
      bizKey: props.bizKey,
      country: 'CN',
      mobile: props.account
    };
    action = `${PUB_GM}/auth/user/signsms/send`;
  } else {
    // Email verification code
    params = {
      bizKey: props.bizKey,
      toAddress: [props.account]
    };
    action = `${PUB_GM}/auth/user/signemail/send`;
  }

  // Clear previous errors and send request
  error.value = false;

  try {
    await http.post(action, params);
    // Start countdown timer after successful request
    bootstrap();
  } catch (e: any) {
    // Handle request errors
    error.value = true;
    errorMessage.value = e.message || t('components.verificationCodeInput.messages.networkError');
    resetSecond();
  }
};

/**
 * Reset countdown timer and sent state
 * Clears interval and resets countdown to initial value
 */
const resetSecond = (): void => {
  sent.value = false;
  second.value = 60;
  if (timer) {
    clearInterval(timer);
    timer = undefined;
  }
};

/**
 * Start countdown timer for verification code resend
 * Prevents users from resending codes too frequently
 */
const bootstrap = (): void => {
  sent.value = true;
  timer = setInterval(() => {
    if (second.value <= 0) {
      resetSecond();
      return;
    }
    second.value--;
  }, 1000);
};

/**
 * Validate verification code input
 * Ensures code is exactly 6 characters long
 * @returns true if validation passes, false otherwise
 */
const validateData = (): boolean => {
  const value = inputValue.value;
  if (value?.length === 6) {
    error.value = false;
    return true;
  }

  // Invalid verification code length
  error.value = true;
  errorMessage.value = t('components.verificationCodeInput.messages.errorVerifyCode');
  return false;
};

/**
 * Cleanup timer on component unmount
 * Prevents memory leaks from active intervals
 */
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});

// Expose validation method for parent components
defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="6"
      :disabled="disabled"
      dataType="number"
      size="large"
      :placeholder="$t('components.verificationCodeInput.placeholder.enterCode')"
      @pressEnter="pressEnter">
      <template #prefix>
        <img src="./assets/email.png" alt="Verification code icon" />
      </template>
      <template #suffix>
        <template v-if="sent">
          <div class="text-4 select-none text-gray-tip cursor-pointer">
            {{ $t('components.verificationCodeInput.messages.resend',{second}) }}
          </div>
        </template>
        <template v-else>
          <div
            :class="[disabled?'text-gray-tip':'text-blue-tab-active']"
            class="text-4 select-none cursor-pointer"
            @click="getCode">
            {{ $t('components.verificationCodeInput.messages.getCode') }}
          </div>
        </template>
      </template>
    </Input>
    <div class="error-message">{{ errorMessage }}</div>
  </div>
</template>
