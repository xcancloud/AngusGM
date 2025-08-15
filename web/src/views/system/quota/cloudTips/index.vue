<script setup lang='ts'>
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';
import { AppOrServiceRoute, DomainManager } from '@xcan-angus/infra';
import { CloudTipsProps } from '../types';
import { getApiDomain } from '../utils';

const { t } = useI18n();

// Component props with defaults
type Props = CloudTipsProps

const props = withDefaults(defineProps<Props>(), {
  min: '0',
  max: '100'
});

// Reactive state for API domain
const href = ref<string>('');

/**
 * Initialize component data
 */
const init = (): void => {
  href.value = getApiDomain(DomainManager, AppOrServiceRoute);
};

// Lifecycle hook - initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <div class="text-3 leading-4 mb-1 flex items-center text-theme-sub-content">
    <!-- Information icon -->
    <Icon class="text-blue-tips text-3.5 mr-1" icon="icon-tishi1" />

    <!-- Quota range tip message -->
    <span>{{ t('quota.messages.quotaRangeTip', { min: props.min, max: props.max }) }}</span>&nbsp;

    <!-- Submit ticket link -->
    <a
      class="text-theme-special text-theme-text-hover"
      :href="href"
      target="_blank">{{ t('quota.messages.submitTicket') }}</a>

    <!-- Contact engineer message -->
    &nbsp;{{ t('quota.messages.contactEngineer') }}
  </div>
</template>
