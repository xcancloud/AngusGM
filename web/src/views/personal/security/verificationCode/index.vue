<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Input } from '@xcan-angus/vue-ui';

import { user } from '@/api';

// Component props interface for verification code functionality
interface Props {
  mediumIfy: string; // Email or mobile number to send code to
  isModify: boolean; // Whether this is for modification or binding
  sendType: 'email' | 'mobile'; // Type of verification (email or SMS)
  canSend?: boolean; // Whether code can be sent (for rate limiting)
  error?: boolean; // Error state for input field
  country?: string; // Country code for mobile verification
}

const props = withDefaults(defineProps<Props>(), {
  mediumIfy: undefined,
  isModify: false,
  canSend: true,
  error: false,
  country: undefined
});

// Component event emissions
const emit = defineEmits<{
  (e: 'change', value: string): void; // Emit verification code input changes
}>();

// Internationalization setup
const { t } = useI18n();

// Reactive state for verification code functionality
const hadSent = ref(false); // Whether verification code has been sent
const countDown = ref(60); // Countdown timer for resend functionality
let loading = false; // Loading state for send operation

/**
 * Handle verification code input changes
 * Emits the input value to parent component
 */
const changeHandle = (event: any) => {
  const value = event.target.value;
  emit('change', value);
};

/**
 * Send verification code to user's email or mobile
 * Handles different business keys and parameters for email vs mobile
 */
const toSend = async () => {
  // Validate prerequisites before sending
  if (!props.canSend || loading || !props.mediumIfy) {
    return;
  }

  loading = true;

  // Prepare parameters based on verification type
  const params = {} as Record<'bizKey' | 'mobile' | 'toAddress' | 'country', string | string[]>;

  switch (props.sendType) {
    case 'email':
      params.bizKey = props.isModify ? 'MODIFY_EMAIL' : 'BIND_EMAIL';
      params.toAddress = [props.mediumIfy];
      break;
    case 'mobile':
      params.bizKey = props.isModify ? 'MODIFY_MOBILE' : 'BIND_MOBILE';
      // Ensure country is defined for mobile verification
      if (props.country) {
        params.country = props.country;
      }
      params.mobile = props.mediumIfy;
      break;
  }

  // Start countdown and send verification code
  hadSent.value = true;
  enableCountdown();

  // Send code based on type
  if (props.sendType === 'email') {
    await user.senEmailCode(params);
  }
  if (props.sendType === 'mobile') {
    await user.sendSmsCode(params);
  }

  loading = false;
};

// Timer variables for debouncing and countdown functionality
let timeoutTimer: ReturnType<typeof setTimeout>;
const send = () => {
  clearTimeout(timeoutTimer);
  timeoutTimer = setTimeout(() => {
    toSend();
  }, 16.66);
};

let timer: ReturnType<typeof setInterval>;
/**
 * Enable countdown timer for resend functionality
 * Prevents spam clicking of send button
 */
const enableCountdown = () => {
  timer = setInterval(() => {
    if (countDown.value <= 0) {
      clearInterval(timer);
      countDown.value = 60;
      hadSent.value = false;
      return;
    }

    countDown.value--;
  }, 1000);
};

</script>

<template>
  <!-- Verification Code Input Container -->
  <div class="select-none relative flex flex-nowrap items-center">
    <!-- Verification Code Input Field -->
    <Input
      dataType="number"
      trimAll
      class="h-8 pr-24"
      :error="error"
      :placeholder="t('securities.placeholder.codePlaceholder')"
      :allowClear="false"
      :maxlength="6"
      @change="changeHandle" />

    <!-- Countdown Display (when code has been sent) -->
    <span
      v-if="hadSent"
      class="absolute z-2 right-3 block text-gray-placeholder cursor-pointer whitespace-nowrap h-8 leading-8">
      {{ t('securities.messages.resend', { countDown }) }}
    </span>

    <!-- Send Code Button (when code hasn't been sent) -->
    <span
      v-else
      class="absolute z-2 right-3 block content-primary-text cursor-pointer whitespace-nowrap h-8 leading-8"
      @click="send">
      {{ t('securities.messages.sendCode') }}
    </span>
  </div>
</template>
