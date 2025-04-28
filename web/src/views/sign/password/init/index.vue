<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';
import { notification } from '@xcan-angus/vue-ui';

import { redirectTo } from '@/utils/url';
import { auth } from '@/api';
import PasswordInput from '@/components/PasswordInput/index.vue';
import PasswordConfirmInput from '@/components/PasswordConfirmInput/index.vue';

const router = useRouter();

const passwordRef = ref();
const confirmPasswordRef = ref();
const error = ref(false);
const errorMessage = ref<string>();
const loading = ref(false);
const password = ref<string>();
const confirmPassword = ref<string>();

const init = () => {
  if (sessionStorage.getItem('hasPassword') !== 'false') {
    router.push('/signin');
  }
};

const validate = () => {
  let flag = 0;
  if (!passwordRef.value?.validateData()) {
    flag++;
  }

  if (!confirmPasswordRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const confirm = async () => {
  if (validate()) {
    return;
  }

  error.value = false;
  loading.value = true;
  const [err] = await auth.updateUserInitPassword({ newPassword: password.value });
  loading.value = false;
  if (err) {
    error.value = true;
    errorMessage.value = err.message;
    return;
  }
  sessionStorage.removeItem('hasPassword');
  notification.success('设置密码成功');
  await redirectTo();
};

onMounted(() => {
  init();
});

</script>

<template>
  <div class="flex flex-no-wrap items-start mb-8 text-gray-content">
    <img class="relative top-0.25 w-4 h-4 flex-shrink-0 flex-grow-0 mr-2" src="./assets/warning.png">
    <span>{{ $t('init-pass-desc') }}</span>
  </div>
  <PasswordInput
    ref="passwordRef"
    v-model:value="password"
    :placeholder="$t('enter-pass')"
    class="mb-7.5 absolute-fixed" />
  <PasswordConfirmInput
    ref="confirmPasswordRef"
    v-model:value="confirmPassword"
    :password="password"
    :placeholder="$t('confirm-pass')"
    class="mb-7.5 absolute-fixed" />
  <div :class="{ 'error': error }" class="absolute-fixed relative">
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
</template>
