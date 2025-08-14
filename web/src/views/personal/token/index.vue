<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Card, DatePicker, Hints, Icon, Input } from '@xcan-angus/vue-ui';

import TokenTable from './Table.vue';
import { createToken, getTokenQuota, validateTokenForm, resetForm } from './utils';
import type { CreateTokenParams } from './types';

const { t } = useI18n();

// Form data for creating new token
const prevName = ref(''); // Store previous name for validation
const name = ref(''); // Token name input
const password = ref(''); // User password for verification
const expireDate = ref(''); // Token expiration date
const token = ref(''); // Generated token value
const total = ref(0); // Current total number of tokens
const confirmLoading = ref(false); // Loading state for form submission

// Update total count from child component
const tableChange = (_total: number) => {
  total.value = _total;
};

// Reset form fields to initial state
const reset = (): void => {
  resetForm({ name, password, expireDate });
};

// Handle form submission to create new token
const ok = async () => {
  try {
    const params: CreateTokenParams = {
      name: name.value.trim(),
      expireDate: expireDate.value,
      password: password.value
    };

    confirmLoading.value = true;
    const tokenValue = await createToken(params);

    if (tokenValue) {
      // Store generated token and update UI
      token.value = tokenValue;
      prevName.value = name.value;
      reset();
      console.log('Token created successfully');
    }
  } catch (err) {
    console.error('Unexpected error creating token:', err);
  } finally {
    confirmLoading.value = false;
  }
};

// Compute if form submission should be disabled
const disabled = computed(() => {
  return !validateTokenForm(name.value, password.value, prevName.value, total.value, tokenQuota.value);
});

// Token quota limit from system settings
const tokenQuota = ref(0);

// Fetch token quota from API
const fetchTokenQuota = async () => {
  tokenQuota.value = await getTokenQuota();
};

onMounted(() => {
  fetchTokenQuota();
});
</script>

<template>
  <div class="w-11/12 mx-auto">
    <!-- Page header -->
    <div class="items-center pb-2 border-b border-theme-divider">
      <span class="text-3.5 text-theme-title">{{ t('token.accessToken') }}</span>
    </div>

    <!-- Information hint about token quota -->
    <Hints :text="t('token.tokenDescription', { n: tokenQuota })" class="pt-2 font-medium" />

    <!-- Token creation form -->
    <Card class="mt-2" bodyClass="px-8">
      <template #title>{{ t('token.addToken') }}</template>
      <div class="flex mt-3">
        <!-- Left column: Token name and expiration date -->
        <div class="flex-free-half">
          <div class="w-112.5">
            <span class="flex items-center text-3-multi text-theme-title">
              <Icon class="mr-1 text-danger text-3.5" icon="icon-xinghao" />
              {{ t('token.tokenName') }}
            </span>
            <Input
              v-model:value="name"
              class="mt-2.5"
              type="input"
              :placeholder="t('token.placeholder.tokenNameLength')"
              :maxlength="100" />
          </div>
          <div class="w-112.5 mt-9">
            <span class="flex items-center text-3-multi text-theme-title">{{ t('token.expireTimeDescription') }}</span>
            <DatePicker
              v-model:value="expireDate"
              className="w-full"
              class="w-full mt-2.5"
              showTime
              :placeholder="t('token.expireTime')" />
          </div>
        </div>

        <!-- Right column: Password input -->
        <div class="flex-free-half">
          <!-- Hidden input to prevent browser autofill on date picker -->
          <input class="h-0 block" />
          <div class="w-112.5">
            <span class="flex items-center text-3-multi text-theme-title">
              <Icon class="mr-1 text-danger text-3.5" icon="icon-xinghao" />
              <span>{{ t('token.loginPassword') }}</span>
            </span>
            <Input
              v-model:value="password"
              class="mt-2.5"
              type="password"
              dataType="free"
              :allowClear="false"
              :placeholder="t('token.typeSignPassword')"
              :trimAll="true"
              :maxlength="50" />
          </div>
        </div>
      </div>

      <!-- Form submission button -->
      <div class="flex my-10">
        <Button
          size="small"
          type="primary"
          :disabled="disabled"
          :loading="confirmLoading"
          @click="ok">
          <Icon class="mr-1 text-white text-3.5" icon="icon-tianjia" />
          {{ t('token.createToken') }}
        </Button>
      </div>
    </Card>

    <!-- Token list table -->
    <TokenTable
      :notify="token"
      :tokenQuota="tokenQuota"
      @change="tableChange" />
  </div>
</template>

<style scoped>
/* Style disabled button text color */
.ant-btn[disabled] > * {
  @apply text-gray-placeholder;
}

/* Utility class for positioning */
.position-left-20 {
  left: calc(100% + 20px);
}
</style>
