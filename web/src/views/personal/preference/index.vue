<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { PureCard, SelectEnum } from '@xcan-angus/vue-ui';
import { SupportedLanguage } from '@xcan-angus/infra';

import { setting } from '@/api';

const { t } = useI18n();

// User preference state variables
const language = ref<string>(); // Current selected language
const theme = ref<string>(); // Current selected theme
const loading = ref(true); // Loading state for API calls
const initLanguage = ref<string>(); // Initial language value for comparison

// Load user preference data from API
const loadData = async () => {
  loading.value = true;
  const [error, { data }] = await setting.getUserPreference();
  loading.value = false;

  if (error) {
    return;
  }

  // Set language and theme from API response
  language.value = data.language?.value;
  initLanguage.value = language.value; // Store initial value for comparison
  theme.value = data.themeCode;
};

// Update user language preference
const updateLanguage = async (value: string) => {
  language.value = value;

  // Skip API call if language hasn't changed
  if (initLanguage.value === value) {
    return;
  }

  // Prepare parameters for API update
  const params = { language: value, themeCode: theme.value };
  const [error] = await setting.updateUserPreference(params);

  if (error) {
    return;
  }

  // Broadcast language change to other tabs/windows if supported
  if (BroadcastChannel) {
    const searchParams = new URLSearchParams();
    searchParams.append('key', 'language');
    searchParams.append('value', value);
    const message = searchParams.toString();
    const channel = new BroadcastChannel('xcan_preference_channel');
    channel.postMessage(message);
  }
};

// Initialize component on mount
onMounted(() => {
  loadData();
});
</script>

<template>
  <PureCard
    :loading="loading"
    class="px-6 py-11 w-11/12 2xl:px-6 mx-auto flex-1 flex items-start justify-center">
    <div class="flex flex-col justify-center items-start">
      <!-- Language selection section -->
      <div class="flex flex-nowrap mt-8 text-3">
        <div class="flex items-center whitespace-nowrap mr-5 text-theme-title">
          {{ t('common.labels.language') }}
        </div>
        <SelectEnum
          :value="language"
          class="w-50"
          :enumKey="SupportedLanguage"
          @change="updateLanguage" />
      </div>
    </div>
  </PureCard>
</template>

<style scoped>
/* Radio button styling adjustments */
.ant-radio-wrapper {
  @apply flex items-center mr-3;
  margin-top: 11px;
}

.ant-radio-wrapper:last-child {
  @apply mr-0;
}

:deep(.ant-radio) {
  @apply top-0;
}
</style>
