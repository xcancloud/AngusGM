<script setup lang="ts">
import { inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import { Skeleton } from 'ant-design-vue';

interface Props {
  values: string[]
}

withDefaults(defineProps<Props>(), {
  values: () => []
});

const { t } = useI18n();
const labels = ['environmentName', 'dataCenter', 'IP', 'isItDue'];
const loading = inject('loading');

</script>
<template>
  <Card
    :title="t('信息')"
    class="w-1/2"
    bodyClass="flex px-8 py-5">
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{rows:4,width:['90%','90%','90%','90%']}">
      <ol class="text-3 leading-3 space-y-5 w-1/2 font-normal">
        <li
          v-for="(item,index) in labels"
          :key="index"
          class=" text-theme-content ">
          {{ t(item) }}
        </li>
      </ol>
    </Skeleton>
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{rows:4,width:['90%','90%','90%','90%']}">
      <ol class="ml-6 text-3 leading-3 space-y-5 w-1/2">
        <li
          v-for="(item,index) in values"
          :key="index"
          class="text-theme-content font-normal">
          {{ item }}
        </li>
      </ol>
    </Skeleton>
  </Card>
</template>
