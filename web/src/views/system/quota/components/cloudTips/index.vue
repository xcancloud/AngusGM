<script setup lang='ts'>
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan/design';
import { site } from '@xcan/utils';

const { t } = useI18n();

const href = ref();
onMounted(async () => {
  const host = await site.getUrl('www');
  href.value = host + '/workorders?s=quota';
});
</script>
<template>
  <div class="text-3 leading-4 mb-1 flex items-center text-theme-sub-content">
    <Icon class="text-blue-tips text-3.5 mr-1" icon="icon-tishi1" />
    <span>{{ t('systemResources.t0') }}</span>
    <a
      class="text-theme-special text-theme-text-hover"
      :href="href"
      target="_blank">{{ t('systemResources.t01') }}</a>
    {{ t('systemResources.t1') }}
  </div>
</template>
