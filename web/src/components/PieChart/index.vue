<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch, nextTick, computed } from 'vue';
import * as echarts from 'echarts/core';
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components';
import { PieChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

/**
 * Component props interface definition
 * Defines the structure for pie chart configuration
 */
interface Props {
  source: string; // Data source identifier
  title: string; // Chart title
  type: string; // Chart type identifier
  total: number; // Total count for the dataset
  color: string[]; // Color palette for chart segments
  legend: Array<{ value: string | number; message: string }>; // Legend configuration
  dataSource: Array<{ name: string; value: number; codes?: number }>; // Chart data points
}

const props = withDefaults(defineProps<Props>(), {
  source: '',
  title: '',
  type: '',
  total: 0,
  color: () => [],
  legend: () => [],
  dataSource: () => []
});

// Register ECharts components
echarts.use([
  GridComponent,
  TooltipComponent,
  TitleComponent,
  LegendComponent,
  PieChart,
  CanvasRenderer,
  LabelLayout
]);

// Chart reference and instance
const chartsRef = ref<HTMLElement>();
let myChart: echarts.ECharts | null = null;

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
    console.error('Failed to initialize pie chart:', error);
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
 * Computed legend configuration based on data source length
 * Provides adaptive legend layout for different data sizes
 */
const legend = computed(() => {
  const dataSource = props.dataSource;
  
  if (dataSource.length > 8 && dataSource.length < 12) {
    // Multi-column layout for medium data sets
    return [0, 1, 2].map(idx => ({
      orient: 'vertical',
      top: 124,
      left: `${idx * 30 + 5}%`,
      itemHeight: 8,
      itemWidth: 12,
      textStyle: {
        width: 60,
        fontsize: 10,
        overflow: 'truncate'
      },
      tooltip: {
        show: true
      },
      itemGap: 6,
      data: dataSource.filter(item => item.name).slice(idx * 4, idx * 4 + 4)
    }));
  } else if (dataSource.length > 2) {
    // Two-column layout for larger data sets
    return [
      {
        orient: 'vertical',
        top: 124,
        right: '50%',
        itemHeight: 8,
        itemWidth: 12,
        textStyle: {
          width: 60,
          fontsize: 10,
          overflow: 'truncate'
        },
        tooltip: {
          show: true
        },
        itemGap: 6,
        data: dataSource.filter(item => item.name).slice(0, Math.ceil(dataSource.length / 2))
      },
      {
        orient: 'vertical',
        left: '50%',
        top: 124,
        itemHeight: 8,
        itemWidth: 12,
        textStyle: {
          width: 60,
          fontsize: 10,
          overflow: 'truncate'
        },
        tooltip: {
          show: true
        },
        itemGap: 6,
        data: dataSource.filter(item => item.name).slice(Math.ceil(dataSource.length / 2), dataSource.length)
      }
    ];
  } else {
    // Single horizontal layout for small data sets
    return {
      orient: 'horizontal',
      top: 124,
      left: 'center',
      itemHeight: 8,
      itemWidth: 12,
      textStyle: {
        width: 50,
        fontsize: 10,
        overflow: 'truncate'
      },
      tooltip: {
        show: true
      },
      itemGap: 6,
      data: dataSource.filter(item => item.name)
    };
  }
});

/**
 * Computed chart configuration
 * Provides reactive chart options based on props
 */
const chartsOption = computed(() => ({
  grid: {
    top: 32,
    right: 0,
    bottom: 45,
    left: 0
  },
  title: {
    text: props.title,
    left: 'center',
    top: 50,
    textStyle: {
      fontSize: 12,
      fontWeight: 'normal'
    },
    subtext: props.total,
    subtextStyle: {
      fontSize: 12,
      fontWeight: 'normal'
    },
    itemGap: 2
  },
  tooltip: {
    trigger: 'item',
    formatter: function (params: any) {
      let res = `<div style='margin-bottom:5px;width:100%;min-width:100px;font-size:12px;color:#8C8C8C;color:var(--content-text-title);'>
                    ${props.title}</div>`;
      
      // Special handling for API logs status
      if (props.source === 'ApiLogs' && props.type === 'status') {
        if (params.data.codes?.length) {
          const _codes = params.data.codes;
          for (let i = 0; i < _codes?.length; i++) {
            res += `<div style="font-size: 14px;line-height: 16px;display:flex;justify-content: space-between;align-items: center;">
                         <div style="display:flex;align-items: center;">
                        <div style="width:6px;height:6px;border-radius:12px;background-color:${params.color.colorStops[1].color};margin-right:4px;"></div>
                        <span style="font-size:12px;color:var(--content-text-content)">${_codes[i]?.name}</span>
                        </div>
                        <span style="font-size:12px;margin-left:8px;color:var(--content-text-content)">${_codes[i]?.value}</span>
                        </div>`;
          }
        }
        return res;
      } else {
        // Standard tooltip format
        res += `<div style="font-size: 14px;line-height: 16px;display:flex;justify-content: space-between;align-items: center;">
                         <div style="display:flex;align-items: center;">
                        <div style="width:6px;height:6px;border-radius:12px;background-color:${params.color.colorStops[1].color};margin-right:4px;"></div>
                        <span style="font-size:12px;color:var(--content-text-content)">${params.name}</span>
                        </div>
                        <span style="font-size:12px;margin-left:8px;color:var(--content-text-content)">${params.value}</span>
                        </div>`;
        return res;
      }
    }
  },
  legend: legend.value,
  series: [
    {
      id: props.type,
      name: props.title,
      type: 'pie',
      radius: [41, 56],
      center: ['50%', 65],
      label: {
        show: true,
        position: 'inside',
        formatter: (params: any) => {
          // Hide label for zero values
          if (params.value === 0) {
            return '';
          } else {
            return params.value;
          }
        },
        fontSize: 12,
        lineHeight: 14,
        color: '#525A65'
      },
      labelLine: {
        normal: {
          show: false
        }
      },
      itemStyle: {
        normal: {
          color: (list: any) => {
            // Create gradient colors for each segment
            const colorList = props.color.map(item => ({
              colorStart: `rgba(${item},0.5)`,
              colorEnd: `rgba(${item},1)`
            }));
            return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              {
                offset: 0,
                color: colorList[list.dataIndex]?.colorStart
              },
              {
                offset: 1,
                color: colorList[list.dataIndex]?.colorEnd
              }
            ]);
          }
        }
      },
      data: props.dataSource,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      emptyCircleStyle: {
        color: '#f5f5f5'
      }
    }
  ]
}));

// Watch for data source changes and update chart
watch(() => [props.dataSource, props.color, props.title], () => {
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
  <div 
    ref="chartsRef" 
    class="w-full h-full" 
    style="min-height: 200px; min-width: 200px;">
  </div>
</template>

<style scoped>
/* Ensure minimum dimensions for chart container */
.w-full.h-full {
  min-height: 200px;
  min-width: 200px;
}
</style>
