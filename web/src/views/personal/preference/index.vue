<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { RadioGroup, Radio } from 'ant-design-vue';
import { SelectEnum, PureCard } from '@xcan/design';
import { lazyEnum } from '@xcan/enum';

import { setting } from '@/api';

interface Theme {
  message: string;
  value: string;
}

const { t } = useI18n();
const language = ref();
const theme = ref<string>();
const loading = ref(true);
const initLanguage = ref();
const themes = ref<Theme[]>([]);

const loadThemes = async () => {
  const [error, data] = await lazyEnum.load('ThemeCode');
  if (error) {
    return;
  }

  themes.value = data || [];
};

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

const confirm = async (item: Theme) => {
  theme.value = item.value;
  const params = { language: language.value, themeCode: item.value };
  const [error] = await setting.updateUserPreference(params);
  if (error) {
    return;
  }

  if (BroadcastChannel) {
    const searchParams = new URLSearchParams();
    searchParams.append('key', 'theme');
    searchParams.append('value', theme.value);
    const message = searchParams.toString();
    const channel = new BroadcastChannel('xcan_preference_channel');
    channel.postMessage(message);
  }
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
  loadThemes();
  loadData();
});
</script>

<template>
  <PureCard :loading="loading" class="px-6 py-11 w-11/12 2xl:px-6 mx-auto flex-1 flex items-start justify-center">
    <div class="flex flex-col justify-center items-start">
      <div class="flex flex-nowrap text-3">
        <div class="flex items-start whitespace-nowrap mt-3.5 mr-5 text-theme-title">
          {{ t('personalCenter.theme') }}
        </div>
        <RadioGroup v-model:value="theme" class="flex flex-wrap max-w-xl">
          <Radio
            v-for="item in themes"
            :key="item.value"
            :value="item.value"
            @click.prevent="confirm(item)">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </div>
      <div class="flex flex-nowrap mt-8 text-3">
        <div class="flex items-center whitespace-nowrap mr-5 text-theme-title">
          {{ t('personalCenter.lang') }}
        </div>
        <SelectEnum
          :value="language"
          class="w-50"
          enumKey="SupportedLanguage"
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
