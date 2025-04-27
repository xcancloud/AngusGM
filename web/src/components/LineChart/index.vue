<script setup lang="ts">
import {ref, onMounted, watch, onBeforeMount, Ref, inject} from 'vue';
import * as echarts from 'echarts/core';
import {NoData} from '@xcan/design';

import {
  TitleComponent,
  TooltipComponent,
  GridComponent
} from 'echarts/components';

import {BarChart, LineChart} from 'echarts/charts';
import {CanvasRenderer} from 'echarts/renderers';
import {computed} from '@vue/reactivity';
import DarkTheme from '../Statistics/echartsDark.json';
import GrayTheme from '../Statistics/echartsGray.json';

interface Props {
  title: string
  unit: string
  xData: string[]
  yData: (number | null)[];
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  unit: '',
  xData: () => [],
  yData: () => []
});

echarts.use([
  TitleComponent,
  GridComponent,
  TooltipComponent,
  BarChart,
  CanvasRenderer,
  LineChart
]);

const tenantInfo: Ref = inject('tenantInfo', ref());
const chartsRef = ref();
let myChart: echarts.ECharts;

const initCharts = () => {
  if (!chartsRef.value) {
    return;
  }
  echarts.registerTheme(tenantInfo.value.preference.themeCode, tenantInfo.value.preference.themeCode === 'dark' ? DarkTheme : GrayTheme);
  myChart = echarts.init(chartsRef.value, tenantInfo.value.preference.themeCode, {renderer: 'canvas'});
  myChart.setOption(chartsOption);
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

const noData = computed(() => {
  return props.yData.length === 1 && props.yData[0] === 0;
});

const chartsOption = {
  grid: {
    top: 32,
    right: 15,
    bottom: 45,
    left: 40
  },
  title: {
    text: `${props.title} ( ${props.unit} , 总数: ${props.yData.length ? props.yData.reduce((n, m) => Number(n) + Number(m)) : ''})`,
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
    formatter: function (params) {
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
      // axisLabel: {
      //   fontSize: 12
      // }
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
          {offset: 1, color: 'rgba(45, 142, 255, 0.2)'},
          {offset: 0, color: 'rgba(45, 142, 255, 1)'}
        ]),
        borderRadius: [3, 3, 0, 0]
      },
      data: props.yData,
      areaStyle: {}
    }
  ]
};

// Enable data zoom when user click bar.

watch(() => props.xData, () => {
  chartsOption.title.text = `${props.title} ( ${props.unit} , 总数: ${props.yData.length ? props.yData.reduce((n, m) => Number(n) + Number(m)) : ''})`;
  chartsOption.xAxis[0].data = props.xData;
  chartsOption.yAxis[0].max = props.yData.length === 1 && props.yData[0] === 0 ? 100 : 'dataMax';
  chartsOption.series[0].data = props.yData;
  chartsOption.series[0].label.show = props.yData.length === 1 && props.yData[0] === 0 ? false : props.yData.length;
  myChart?.setOption(chartsOption, true);
}, {deep: true});

onBeforeMount(() => {
  window.removeEventListener('resize', () => {
    myChart.resize();
  });
});

onMounted(() => {
  initCharts();
});

</script>
<template>
  <div class="relative" style="height: 200px;">
    <div ref="chartsRef" class="w-full h-full flex-shrink-0">
    </div>
    <template v-if="noData">
      <NoData class="!h-30 absolute w-30  my-no-data"/>
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
