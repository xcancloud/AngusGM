<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch, nextTick, computed } from 'vue';
import * as echarts from 'echarts/core';
import { NoData } from '@xcan-angus/vue-ui';
import { GridComponent, TitleComponent, TooltipComponent } from 'echarts/components';
import { BarChart, LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { useI18n } from 'vue-i18n';

/**
 * Component props interface definition
 * Defines the structure for line/bar chart configuration
 */
interface Props {
  title: string; // Chart title
  unit: string; // Data unit (e.g., "count", "percentage")
  xData: string[]; // X-axis labels (time periods)
  yData: (number | null)[]; // Y-axis values (counts)
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  unit: '',
  xData: () => [],
  yData: () => []
});

// Register ECharts components
echarts.use([
  TitleComponent,
  GridComponent,
  TooltipComponent,
  BarChart,
  CanvasRenderer,
  LineChart
]);

// Chart reference and instance
const chartsRef = ref<HTMLElement>();
let myChart: echarts.ECharts | null = null;

const { t } = useI18n();

/**
 * Check if DOM element is ready for chart initialization
 * @returns Whether the DOM element has valid dimensions
 */
const isDomReady = (): boolean => {
  if (!chartsRef.value) {
    return false;
  }

  const dom = chartsRef.value;
  return dom.clientWidth > 0 && dom.clientHeight > 0;
};

/**
 * Initialize chart with retry mechanism
 * Handles DOM dimension issues and ensures proper chart initialization
 */
const initCharts = async (): Promise<void> => {
  // Wait for DOM rendering to complete
  await nextTick();

  if (!isDomReady()) {
    // Retry initialization if DOM is not ready
    setTimeout(() => {
      initCharts();
    }, 100);
    return;
  }

  try {
    // Dispose existing chart instance
    if (myChart) {
      myChart.dispose();
    }

    if (chartsRef.value) {
      myChart = echarts.init(chartsRef.value);
      myChart.setOption(chartsOption.value, true, false);

      // Add window resize listener
      window.addEventListener('resize', handleResize);
    }
  } catch (error) {
    console.error('Failed to initialize line chart:', error);
  }
};

/**
 * Handle window resize events
 * Resizes chart when window dimensions change
 */
const handleResize = (): void => {
  if (myChart && isDomReady()) {
    myChart.resize();
  }
};

/**
 * Destroy chart instance and cleanup event listeners
 */
const destroyChart = (): void => {
  if (myChart) {
    window.removeEventListener('resize', handleResize);
    myChart.dispose();
    myChart = null;
  }
};

/**
 * Computed property to check if data is empty
 * Returns true when there's only one data point with value 0
 */
const noData = computed(() => {
  return props.yData.length === 1 && props.yData[0] === 0;
});

/**
 * Computed chart configuration
 * Provides reactive chart options based on props
 */
const totalMessage = t('statistics.total');
const chartsOption = computed(() => ({
  grid: {
    top: 32,
    right: 15,
    bottom: 45,
    left: 40
  },
  title: {
    text: `${props.title} ( ${props.unit} , ${totalMessage}: ${props.yData.length ? props.yData.reduce((n, m) => Number(n) + Number(m)) : ''})`,
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
    },
    formatter: function (params: any) {
      let res = `<div style='margin-bottom:5px;width:100%;font-size: 12px;min-width:100px;color:#8C8C8C;color:var(--content-text-title);'>${params.name}</div>`;
      res += `<div style="font-size: 14px;line-height: 16px;display:flex;justify-content: space-between;align-items: center;">
      <div style="display:flex;align-items: center;">
      <div style="width:6px;height:6px;border-radius:12px;background-color:rgba(45, 142, 255, 1);margin-right:4px;"></div>
      <span style="font-size:12px;color:var(--content-text-content);">${props.title}</span>
      </div>
      <span style="font-size:12px;margin-left:8px;color:var(--content-text-content);">${params.value}</span>
      </div>`;
      return res;
    }
  },
  xAxis: [
    {
      type: 'category',
      data: props.xData,
      axisTick: {
        show: false
      },
      axisLine: {
        show: true,
        lineStyle: {
          // color: '#8C8C8C'
        }
      }
    }
  ],
  yAxis: [
    {
      type: 'value',
      max: props.yData.length === 1 && props.yData[0] === 0 ? 100 : 'dataMax',
      axisLine: {
        show: true,
        lineStyle: {
          // color: '#8C8C8C'
        }
      },
      splitLine: {
        show: false
      }
    }
  ],
  max: 50,
  series: [
    {
      type: 'line',
      label: {
        show: props.yData.length === 1 && props.yData[0] === 0 ? false : props.yData.length,
        position: 'top'
      },
      barWidth: 15,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 1, color: 'rgba(45, 142, 255, 0.2)' },
          { offset: 0, color: 'rgba(45, 142, 255, 1)' }
        ]),
        borderRadius: [3, 3, 0, 0]
      },
      data: props.yData,
      areaStyle: {}
    }
  ]
}));

// Watch for data changes and update chart
watch(() => [props.xData, props.yData, props.title, props.unit], () => {
  if (myChart && isDomReady()) {
    myChart.setOption(chartsOption.value, true, false);
  }
}, { deep: true });

// Lifecycle hooks
onMounted(() => {
  initCharts();
});

onBeforeMount(() => {
  destroyChart();
});
</script>

<template>
  <div class="relative" style="height: 200px;">
    <div
      ref="chartsRef"
      class="w-full h-full flex-shrink-0"
      style="min-height: 200px; min-width: 200px;">
    </div>
    <template v-if="noData">
      <NoData class="!h-30 absolute w-30 my-no-data" />
    </template>
  </div>
</template>

<style scoped>
.my-no-data {
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* Ensure minimum dimensions for chart container */
.w-full.h-full {
  min-height: 200px;
  min-width: 200px;
}
</style>
