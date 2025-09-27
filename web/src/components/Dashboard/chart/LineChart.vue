<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import * as echarts from 'echarts/core';
import { NoData } from '@xcan-angus/vue-ui';
import { GridComponent, TitleComponent, TooltipComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { computed } from '@vue/reactivity';
import { LineChartData } from '../types';

const { t } = useI18n();

interface Props {
  chartData: LineChartData;
}
const props = defineProps<Props>();

echarts.use([
  TitleComponent,
  GridComponent,
  TooltipComponent,
  LineChart,
  CanvasRenderer
]);

const chartsRef = ref();
let myChart: echarts.ECharts;

const initCharts = () => {
  if (!chartsRef.value || !props.chartData) {
    return;
  }
  myChart = echarts.init(chartsRef.value);
  myChart.setOption(chartsOption.value);
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

const noData = computed(() => {
  return !props.chartData || !props.chartData.yData || props.chartData.yData.every(element => element === null);
});

const chartsOption = computed(() => {
  // Ensure chartData exists
  if (!props.chartData) {
    return {};
  }

  const total = props.chartData.yData && props.chartData.yData.length
    ? props.chartData.yData.reduce((sum, value) => (sum || 0) + (value || 0), 0)
    : 0;

  const timeUnitMessageKey = 'time.' + props.chartData.unit.toLowerCase();

  return {
    grid: {
      top: 26,
      right: 15,
      bottom: 45,
      left: 40
    },
    title: {
      text: `${props.chartData.title} (${t('chart.timeUnit')}: ${t(timeUnitMessageKey)} , ${t('chart.total')}: ${total})`,
      bottom: 0,
      left: 'center',
      textStyle: {
        fontSize: 12,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: [
      {
        type: 'category',
        data: props.chartData.xData,
        axisTick: {
          show: false
        },
        axisLine: {
          show: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        max: noData.value ? 100 : 'dataMax',
        axisLine: {
          show: true
        },
        splitLine: {
          show: false
        }
      }
    ],
    series: [
      {
        type: 'line',
        barWidth: 15,
        label: {
          show: props.chartData.yData.length === 1 && props.chartData.yData[0] === 0 ? false : props.chartData.yData.length > 0,
          position: 'top'
        },
        itemStyle: {
          color: props.chartData.color
            ? (typeof props.chartData.color === 'string'
                ? props.chartData.color
                : props.chartData.color[0] || '#2D8EFF')
            : new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 1, color: 'rgba(45, 142, 255, 0.2)' },
              { offset: 0, color: 'rgba(45, 142, 255, 1)' }
            ])
        },
        areaStyle: {},
        smooth: true,
        data: props.chartData.yData
      }
    ]
  };
});

// Watch data changes and update chart
watch(() => props.chartData, () => {
  if (myChart) {
    myChart.setOption(chartsOption.value, true);
  }
}, { deep: true });

onBeforeMount(() => {
  window.removeEventListener('resize', () => {
    myChart?.resize();
  });
});

onMounted(() => {
  initCharts();
});
</script>

<template>
  <div class="relative" style="height: 200px;">
    <div ref="chartsRef" class="w-full h-full flex-shrink-0"></div>
    <template v-if="props.chartData && noData">
      <NoData class="h-30 absolute w-30 my-no-data" />
    </template>
  </div>
</template>

<style scoped>
.my-no-data {
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
