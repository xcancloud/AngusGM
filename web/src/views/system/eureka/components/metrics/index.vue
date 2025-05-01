<script setup lang="ts">
import { defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';

const Cpu = defineAsyncComponent(() => import('@/views/system/eureka/components/metrics/cpu/index.vue'));
const Ram = defineAsyncComponent(() => import('@/views/system/eureka/components/metrics/memory/index.vue'));
const Disk = defineAsyncComponent(() => import('@/views/system/eureka/components/metrics/disk/index.vue'));

const { t } = useI18n();
</script>

<template>
  <Card
    class="w-1/2"
    bodyClass="flex px-15 py-3.5 justify-between"
    :title="t('resourceOccupy')">
    <Cpu />
    <Ram />
    <Disk />
  </Card>
</template>
