<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext, EditionType } from '@xcan-angus/infra';

import Tab from '@/components/SignTab/index.vue';
import Form from './form.vue';

const { t } = useI18n();

// Track other account state
const otherAccount = ref<boolean>();

// Active tab selection
const activeValue = ref<'account' | 'mobile'>('account');

// Store edition type for conditional rendering
const editionType = ref<string>();

/**
 * Initialize component and determine edition type
 * Sets up conditional tab rendering based on edition
 */
onMounted(async () => {
  editionType.value = appContext.getEditionType();
});

/**
 * Computed tab list based on edition type
 * Shows SMS verification only for cloud service editions
 */
const tabList = computed(() => {
  if (editionType.value === EditionType.CLOUD_SERVICE) {
    return [
      { label: t('sign.tabs.signinAccount'), value: 'account' },
      { label: t('sign.tabs.signinSms'), value: 'mobile' }
    ];
  }

  return [{ label: t('sign.tabs.signinAccount'), value: 'account' }];
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
