<script setup lang='ts'>
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';
import { AppOrServiceRoute, DomainManager } from '@xcan-angus/infra';

const { t } = useI18n();

const href = ref();
onMounted(async () => {
  const host = DomainManager.getInstance().getApiDomain(AppOrServiceRoute.www);
  href.value = host + '/workorders?s=quota';
});
</script>
<template>
  <div class="text-3 leading-4 mb-1 flex items-center text-theme-sub-content">
    <Icon class="text-blue-tips text-3.5 mr-1" icon="icon-tishi1" />
    <span>{{ t('quota.messages.quotaRangeTip', { min: '0', max: '100' }) }}</span>&nbsp;
    <a
      class="text-theme-special text-theme-text-hover"
      :href="href"
      target="_blank">{{ t('quota.messages.submitTicket') }}</a>
    &nbsp;{{ t('quota.messages.contactEngineer') }}
  </div>
</template>
