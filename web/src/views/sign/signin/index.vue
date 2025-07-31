<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext, EditionType } from '@xcan-angus/infra';

import Tab from '@/components/SignTab/index.vue';
import Form from './form.vue';

const { t } = useI18n();
const otherAccount = ref();
const activeValue = ref('account');

const editionType = ref<string>();
onMounted(async () => {
  editionType.value = appContext.getEditionType();
});

const tabList = computed(() => {
  if (editionType.value === EditionType.CLOUD_SERVICE) {
    return [{ label: t('signin-account'), value: 'account' }, { label: t('signin-sms'), value: 'mobile' }];
  }

  return [{ label: t('signin-account'), value: 'account' }];
});
</script>
<template>
  <template v-if="!otherAccount">
    <Tab v-model:activeKey="activeValue" :tabList="tabList" />
    <Form :type="activeValue" />
  </template>
</template>
<style scoped>
.divider {
  display: flex;
  align-items: center;
  margin-top: 30px;
  padding: 0 16px;
  font-size: 16px;
  line-height: 20px;
  white-space: nowrap;
}

.divider-line {
  display: inline-block;
  flex: 1 1 auto;
  height: 1px;
  background-color: #f0f0f0;
}
</style>
