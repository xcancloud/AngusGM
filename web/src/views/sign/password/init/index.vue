<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { notification } from '@xcan-angus/vue-ui';

import { redirectTo } from '@/utils/url';
import { auth } from '@/api';
import PasswordInput from '@/components/PasswordInput/index.vue';
import PasswordConfirmInput from '@/components/PasswordConfirmInput/index.vue';

const { t } = useI18n();
const router = useRouter();

// Form references for validation
const passwordRef = ref();
const confirmPasswordRef = ref();

// State management
const error = ref(false);
const errorMessage = ref<string>();
const loading = ref(false);
const password = ref<string>();
const confirmPassword = ref<string>();

/**
 * Initialize component and check if user needs password setup
 * Redirects to signin if password already exists
 */
const init = () => {
  if (sessionStorage.getItem('hasPassword') !== 'false') {
    router.push('/signin');
  }
};

/**
 * Validate form inputs
 * @returns {boolean} true if validation fails, false if passes
 */
const validate = () => {
  let validationErrors = 0;
  if (!passwordRef.value?.validateData()) {
    validationErrors++;
  }
  if (!confirmPasswordRef.value?.validateData()) {
    validationErrors++;
  }
  return validationErrors > 0;
};

/**
 * Handle password initialization submission
 * Updates user's initial password and redirects on success
 */
const confirm = async () => {
  if (validate()) {
    return;
  }

  try {
    error.value = false;
    loading.value = true;

    const [err] = await auth.updateUserInitPassword({ newPassword: password.value });
    if (err) {
      error.value = true;
      errorMessage.value = err.message;
      return;
    }

    // Clear session storage and show success message
    sessionStorage.removeItem('hasPassword');
    notification.success(t('sign.messages.setPasswordSuccess'));
    await redirectTo();
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  init();
});
</script>

<template>
  <!-- Password initialization description -->
  <div class="flex flex-no-wrap items-start mb-8 text-gray-content">
    <img class="relative top-0.25 w-4 h-4 flex-shrink-0 flex-grow-0 mr-2" src="./assets/warning.png">
    <span>{{ $t('sign.messages.initPassDesc') }}</span>
  </div>

  <!-- Password input field -->
  <PasswordInput
    ref="passwordRef"
    v-model:value="password"
    :placeholder="$t('sign.placeholder.enterPass')"
    class="mb-7.5 absolute-fixed" />

  <!-- Password confirmation field -->
  <PasswordConfirmInput
    ref="confirmPasswordRef"
    v-model:value="confirmPassword"
    :password="password"
    :placeholder="$t('sign.placeholder.confirmPass')"
    class="mb-7.5 absolute-fixed" />

  <!-- Submit button with error handling -->
  <div :class="{ 'error': error }" class="absolute-fixed relative">
    <Button
      :loading="loading"
      class="rounded-full w-full"
      type="primary"
      size="large"
      @click="confirm">
      {{ $t('sign.actions.ok') }}
    </Button>
    <div class="error-message">{{ errorMessage }}</div>
  </div>
</template>
