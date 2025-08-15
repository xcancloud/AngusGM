<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { AsyncComponent } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { EditionType } from './types';
import { isCloudService } from './utils';

// Lazy load components for better performance
const CloudTips = defineAsyncComponent(() => import('@/views/system/quota/cloudTips/index.vue'));
const Table = defineAsyncComponent(() => import('@/views/system/quota/list/index.vue'));

// Reactive state management
const editionType = ref<EditionType>();

/**
 * Initialize component data
 */
const init = async (): Promise<void> => {
  editionType.value = appContext.getEditionType() as EditionType;
};

// Computed property to determine if cloud tips should be shown
const isShow = computed<boolean>(() => {
  return isCloudService(editionType.value as EditionType);
});

// Lifecycle hook - initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <div class="flex flex-col min-h-full">
    <!-- Cloud service tips component (only shown for cloud service edition) -->
    <AsyncComponent :visible="isShow">
      <CloudTips v-if="isShow" />
    </AsyncComponent>

    <!-- Quota table component -->
    <Table />
  </div>
</template>
