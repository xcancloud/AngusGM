<script setup lang="ts">
import { inject, computed } from 'vue';
import { Skeleton, Progress } from 'ant-design-vue';

const diskData: any = inject('diskData');
const loading = inject('loading');

const getColor = computed(() => {
  if (diskData.percentage < 75) {
    return 'rgba(82,196,26,100%)';
  } else if (diskData.percentage < 89) {
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
          :percent="diskData.percentage">
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
      :paragraph="{rows:2,width:['120%','120%']}">
      <div class="text-theme-content font-semibold text-3.5 mt-2">
        Disk <span class="text-theme-content font-normal mb-1">({{ diskData.used }}/{{ diskData.totalNuclear }}G)</span>
      </div>
    </Skeleton>
  </div>
</template>
