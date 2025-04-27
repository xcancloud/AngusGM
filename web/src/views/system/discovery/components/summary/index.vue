<script setup lang="ts">
import { inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { PureCard, Icon } from '@xcan/design';
import { Skeleton } from 'ant-design-vue';

interface Props {
  registryStatus: string,
  registeredRuntime: string,
  minNum: number,
  lastNum: number
}

withDefaults(defineProps<Props>(), {
  registryStatus: '',
  registeredRuntime: '',
  minNum: 0,
  lastNum: 0
});

const { t } = useI18n();

const getStateColor = (state: string) => {
  switch (state) {
    case 'UP':
      return 'text-success';
    case 'DOWM':
      return 'text-danger';
    case 'UNKOWN':
      return 'text-warn';
    default:
      return '';
  }
};

const loading = inject('loading');

</script>
<template>
  <div class="flex justify-between box-border space-x-2">
    <PureCard class="registry-head p-3.5">
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{rows:2,width:['100']}">
        <div class="h-15 flex justify-between flex-col box-border">
          <p class="text-3.5 leading-3.5 text-theme-sub-content font-normal">{{ t('registryStatus') }}</p>
          <p class="text-6 leading-6 font-semibold" :class="getStateColor(registryStatus)">{{ registryStatus }}</p>
        </div>
        <div class="w-15 h-15 p-3.25 rounded-lg" style="background-color: rgba(74, 217, 145, 10%);">
          <Icon icon="icon-zhuangtai" class="min-w-full min-h-full" />
        </div>
      </Skeleton>
    </PureCard>
    <PureCard class="registry-head p-3.5">
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{rows:2,width:['100']}">
        <div class="h-15 flex justify-between flex-col box-border">
          <p class="text-3.5 leading-3.5 text-theme-sub-content font-normal">{{ t('registeredRuntime') }}</p>
          <p class="text-6 leading-6 font-semibold text-theme-content">{{ registeredRuntime }}</p>
        </div>
        <div class="w-15 h-15 p-3.25 rounded-lg" style="background-color: rgba(255, 144, 102, 10%);">
          <Icon icon="icon-shijian" class="min-w-full min-h-full" />
        </div>
      </Skeleton>
    </PureCard>
    <PureCard class="registry-head p-3.5">
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{rows:2,width:['100']}">
        <div class="h-15 flex justify-between flex-col box-border">
          <p class="text-3.5 leading-3.5 text-theme-sub-content font-normal">{{ t('minNum') }}</p>
          <p class="text-6 leading-6 font-semibold text-theme-content">{{ minNum }}</p>
        </div>
        <div class="w-15 h-15 p-3.25 rounded-lg" style="background-color: rgba(61, 206, 254, 10%);">
          <Icon icon="icon-xuyueshulan" class="min-w-full min-h-full" />
        </div>
      </Skeleton>
    </PureCard>
    <PureCard class="registry-head p-3.5">
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{rows:2,width:['100']}">
        <div class="h-15 flex justify-between flex-col box-border">
          <p class="text-3.5 leading-3.5 text-theme-sub-content font-normal">{{ t('lastNum') }}</p>
          <p class="text-6 leading-6 font-semibold text-theme-content">{{ lastNum }}</p>
        </div>
        <div class="w-15 h-15 p-3.25 rounded-lg" style="background-color: rgba(254, 197, 61, 15%);">
          <Icon icon="icon-xuyueshuhuang" class="min-w-full min-h-full" />
        </div>
      </Skeleton>
    </PureCard>
  </div>
</template>
<style scoped>
.registry-head {
  @apply w-1/4  h-30 p-7.5 flex justify-between;
}
</style>
