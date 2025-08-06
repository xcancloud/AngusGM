<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { PureCard, SelectEnum } from '@xcan-angus/vue-ui';
import { SupportedLanguage } from '@xcan-angus/infra';

import { setting } from '@/api';

const { t } = useI18n();
const language = ref();
const theme = ref<string>();
const loading = ref(true);
const initLanguage = ref();

const loadData = async () => {
  loading.value = true;
  const [error, { data }] = await setting.getUserPreference();
  loading.value = false;
  if (error) {
    return;
  }

  language.value = data.language?.value;
  initLanguage.value = language.value;
  theme.value = data.themeCode;
};

const updateLanguage = async (value) => {
  language.value = value;
  if (initLanguage.value === value) {
    return;
  }
  const params = { language: value, themeCode: theme.value };
  const [error] = await setting.updateUserPreference(params);
  if (error) {
    return;
  }

  if (BroadcastChannel) {
    const searchParams = new URLSearchParams();
    searchParams.append('key', 'language');
    searchParams.append('value', value);
    const message = searchParams.toString();
    const channel = new BroadcastChannel('xcan_preference_channel');
    channel.postMessage(message);
  }
};

onMounted(() => {
  loadData();
});
</script>

<template>
  <PureCard :loading="loading" class="px-6 py-11 w-11/12 2xl:px-6 mx-auto flex-1 flex items-start justify-center">
    <div class="flex flex-col justify-center items-start">
      <div class="flex flex-nowrap mt-8 text-3">
        <div class="flex items-center whitespace-nowrap mr-5 text-theme-title">
          {{ t('personalCenter.lang') }}
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
