<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { AsyncComponent } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';

const CloudTips = defineAsyncComponent(() => import('@/views/system/quota/components/cloudTips/index.vue'));
const Table = defineAsyncComponent(() => import('@/views/system/quota/components/list/index.vue'));

const editionType = ref<string>();

onMounted(async () => {
  editionType.value = appContext.getEditionType();
});

const isShow = computed(() => {
  return editionType.value === 'CLOUD_SERVICE';
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <AsyncComponent :visible="isShow">
      <CloudTips v-if="isShow" />
    </AsyncComponent>
    <Table />
  </div>
</template>
