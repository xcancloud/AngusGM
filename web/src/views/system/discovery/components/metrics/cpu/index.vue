<script setup lang="ts">
import { inject, computed } from 'vue';
// import { useI18n } from 'vue-i18n';
import { Skeleton, Progress } from 'ant-design-vue';

const cpuData: any = inject('cpuData');
const loading = inject('loading');

// const { t } = useI18n();

const getColor = computed(() => {
  if (cpuData.percentage < 75) {
    return 'rgba(82,196,26,100%)';
  } else if (cpuData.percentage < 89) {
    return 'rgba(255,165,43,100%)';
  } else {
    return 'rgba(245,34,45,100%)';
  }
});
</script>
<template>
  <div class="flex flex-col items-center justify-center">
    <Skeleton
      active
      :avatar="{ size: 110, shape: 'circle' }"
      :loading="loading"
      :title="false"
      :paragraph="false">
      <div class="relative p-0.75">
        <Progress
          :strokeWidth="10"
          :strokeColor="getColor"
          type="circle"
          width="90px"
          :percent="cpuData.percentage">
          <template #format="percent">
            <span style="font-size: 18px;" :style="{'color':getColor}">{{ percent }}%</span>
          </template>
        </Progress>
      </div>
    </Skeleton>
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{ rows: 2, width: ['120%', '120%'] }">
      <div class="text-theme-content font-semibold text-3.5 mt-2">
        CPU <span class="text-theme-content font-normal mb-1">({{ cpuData.used }}/{{ cpuData.totalNuclear }}核)</span>
      </div>
    </Skeleton>
  </div>
</template>
