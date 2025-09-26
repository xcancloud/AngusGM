<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch, computed, defineProps, nextTick } from 'vue';
import elementResizeDetector from 'element-resize-detector';
import { debounce } from 'throttle-debounce';
import * as echarts from 'echarts/core';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  LegendComponentOption
} from 'echarts/components';
import { PieChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import { duration } from '@xcan-angus/infra';
import { PieChartData } from '../types';

interface Props {
  chartData: PieChartData;
}
const props = defineProps<Props>();

echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  PieChart,
  CanvasRenderer,
  LabelLayout
]);

const erd = elementResizeDetector({ strategy: 'scroll' });

const chartsRef = ref();
let myChart: echarts.ECharts;

const initCharts = () => {
  if (!chartsRef.value || !props.chartData) {
    return;
  }
  // Ensure to dispose the previous instance before reinitializing
  if (myChart) {
    myChart.dispose();
  }
  myChart = echarts.init(chartsRef.value, undefined, { renderer: 'canvas' });
  myChart.setOption(chartsOption.value);
};

const chartsOption = computed(() => {
  // Ensure chartData and data exist
  if (!props.chartData || !props.chartData.data || props.chartData.data.length === 0) {
    return {
      title: {
        show: false
      },
      tooltip: {
        trigger: 'item',
        confine: false,
        textStyle: {
          fontSize: 12
        }
      },
      legend: [],
      series: []
    };
  }

  // Calculate legend groups, maximum 5 items per group
  const legendGroups: any[] = [];
  for (let i = 0; i < props.chartData.data.length; i += props.chartData.legendGroupSize) {
    legendGroups.push(props.chartData.data.slice(i, i + props.chartData.legendGroupSize));
  }

  const legendConfig: LegendComponentOption[] = legendGroups.map((group, index) => {
    const defaultConfig = {
      orient: 'vertical',
      left: 135 + index * 90, // 105px spacing between each group
      top: '25%',
      itemHeight: 12,
      itemWidth: 12,
      data: group.map(item => ({
        name: item.name,
        icon: 'circle'
      }))
    }
    if (props.chartData.legendPosition === 'bottom') {
      delete defaultConfig.top;
      defaultConfig.bottom = '0%';
      defaultConfig.left = index * 90;
      if (legendGroups.length < 2) {
        defaultConfig.orient = 'horizontal';
      }
    } else {
      if (props.chartData.legendGroupSize > 6) {
        defaultConfig.top = 0;
      }
    }
    
    return defaultConfig
  });

  debugger;
  return {
    color: props.chartData.color,
    title: {
      show: false,
      text: props.chartData.title,
      textStyle: { color: '#444C5A', fontSize: '12px', fontWeight: 'normal' },
      left: 34,
      bottom: 0
    },
    tooltip: {
      trigger: 'item',
      confine: false,
      textStyle: {
        fontSize: 12
      }
    },
    legend: legendConfig,
    series: [
      {
        center: props.chartData.size === 'small' ? [66, '30%'] : [66, '50%'],
        type: 'pie',
        selectedOffset: 5,
        left: 0,
        top: 0,
        radius: props.chartData.size === 'small' ? [30, 45] : [45, 60],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center',
          color: '#444C5A'
        },
        emphasis: {
          scale: true,
          scaleSize: 3,
          label: {
            show: false,
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: props.chartData.data
      }
    ]
  };
});

watch(() => props.chartData, () => {
  if (myChart && chartsRef.value) {
    myChart.setOption(chartsOption.value, true);
  } else {
    // If chart is not initialized yet, initialize it
    initCharts();
  }
}, { deep: true });

const resizeHandler = debounce(duration.resize, () => {
  myChart?.resize();
});

onMounted(() => {
  // Use nextTick to ensure DOM is fully rendered
  nextTick(() => {
    initCharts();
  });
  erd.listenTo(document.body, resizeHandler);
});

onBeforeUnmount(() => {
  erd.removeListener(document.body, resizeHandler);
});
</script>

<template>
  <div class="relative h-50">
    <div ref="chartsRef" class="h-full"></div>
    <div v-if="props.chartData" class="mark-container leading-5" :class="{'sizes-small': props.chartData.size === 'small'}">
      <div class="text-3 text-center text-theme-title font-medium">{{ props.chartData.title }}</div>
      <div class="mt-1 text-4.5 leading-4.5 text-theme-title text-center">{{ props.chartData.total }}</div>
    </div>
  </div>
</template>

<style scoped>
.mark-container {
  position: absolute;
  top: 45%;
  left: 66px;
  transform: translate(-50%, -20px);
}

.sizes-small {
  top: 30%;
}
</style>
