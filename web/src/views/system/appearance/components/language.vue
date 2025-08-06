<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Skeleton } from 'ant-design-vue';
import { SelectEnum, SelectIntl, Card, notification } from '@xcan-angus/vue-ui';
import { SupportedLanguage } from '@xcan-angus/infra';

import { setting } from '@/api';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const oldLanguage = ref();
const language = ref<'zh_CN' | 'en'>('zh_CN');
const timeZone = ref('Asia/Shanghai');
const loading = ref(false);
const firstLoad = ref(true);

const loadSite = async function () {
  loading.value = true;
  const [error, { data }] = await setting.getTenantLocale();
  loading.value = false;
  firstLoad.value = false;
  if (error) {
    return;
  }
  language.value = data?.defaultLanguage?.value;
  oldLanguage.value = data?.defaultLanguage?.value;
  timeZone.value = data.defaultTimeZone;
};
const handleChange = async (value: 'zh_CN' | 'en') => {
  if (oldLanguage.value === value || loading.value) {
    return;
  }
  loading.value = true;
  const [error] = await setting.updateTenantLocale({ defaultLanguage: value });
  loading.value = false;
  if (error) {
    language.value = oldLanguage.value;
  }
  notification.success(t('common.messages.editSuccess'));
  // location.reload();
};

onMounted(() => {
  loadSite();
});
</script>
<template>
  <Card title="默认语言" bodyClass="py-8 px-15 text-3 leading-3">
    <Skeleton
      active
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 2,width:['100%','100%'] }">
      <div class="flex">
        <div>
          <div class="text-theme-content h-7 leading-7">{{ t('语言') }}&nbsp;</div>
          <div class="text-theme-content h-7 leading-7 mt-6">{{ t('时区') }}&nbsp;</div>
        </div>
        <div class="flex flex-col ml-2.5">
          <SelectEnum
            v-model:value="language"
            size="small"
            class="w-112.5"
            :enumKey="SupportedLanguage"
            @change="handleChange" />
          <SelectIntl
            v-model:value="timeZone"
            style="width: 28.125rem;"
            disabled
            size="small"
            class="mt-6" />
        </div>
      </div>
    </Skeleton>
  </Card>
</template>
