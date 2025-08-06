<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Button } from 'ant-design-vue';
import { SupportedLanguage } from '@xcan-angus/infra';

import { useI18n } from 'vue-i18n';
import { tenant } from '@/api';

const { t } = useI18n();
const id = ref('');
const oldLanguage = ref();
const language = ref('zh_CN');
const oldTimeZone = ref('Asia/Shanghai');
const timeZone = ref();
const loading = ref(false);

// 获取国际化配置
const getOptions = async function () {
  loading.value = true;
  const [error, {
    data = {
      id: '',
      localeData: { defaultLanguage: { value: 'zh_CN', message: '简体中文' }, defaultTimeZone: 'Asia/Shanghai' }
    }
  }] = await tenant.getLocale();
  loading.value = false;
  if (error) {
    return;
  }

  id.value = data?.id;
  oldLanguage.value = data.localeData?.defaultLanguage?.value;
  language.value = data.localeData?.defaultLanguage?.value;
  oldTimeZone.value = data.localeData?.defaultTimeZone;
  timeZone.value = data.localeData?.defaultTimeZone;
};

// 保存配置
const saveConfig = async function () {
  if (oldLanguage.value === language.value && oldTimeZone.value === timeZone.value) {
    return;
  }
  loading.value = true;
  const params = {
    id: id.value,
    localeData: { defaultTimeZone: timeZone.value, defaultLanguage: language.value }
  };
  await tenant.updateLocale(params);
  loading.value = false;
  oldLanguage.value = language.value;
  oldTimeZone.value = timeZone.value;
};

onMounted(() => {
  getOptions();
});
</script>
<template>
  <Card bodyClass="flex flex-col items-center py-20" class="flex-1">
    <div class="flex">
      <div>
        <div class="text-3 leading-3 text-theme-title h-8 leading-8">{{ t('systemIdn.default-1') }}</div>
        <div class="text-3 leading-3 text-theme-title h-8 leading-8 mt-6">{{ t('systemIdn.default-2') }}</div>
      </div>
      <div class="flex flex-col ml-2.5">
        <SelectEnum
          v-model:value="language"
          class="w-112.5"
          :enumKey="SupportedLanguage" />
        <SelectIntl
          v-model:value="timeZone"
          style="width: 28.125rem;"
          class="mt-6" />
      </div>
    </div>
    <Button
      size="small"
      type="primary"
      class="mt-10"
      :loading="loading"
      @click="saveConfig">
      {{ t('systemIdn.default-3') }}
    </Button>
  </Card>
</template>
