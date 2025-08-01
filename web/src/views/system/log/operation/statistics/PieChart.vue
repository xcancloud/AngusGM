<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch } from 'vue';
import * as echarts from 'echarts/core';
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components';

import { PieChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

interface Props {
  source: string;
  title: string;
  type: string;
  total: number;
  color: string[];
  legend: { value: string, message: string }[];
  dataSource: { name: string, value: number, codes?: number }[];
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

echarts.use([
  GridComponent,
  TooltipComponent,
  TitleComponent,
  LegendComponent,
  PieChart,
  CanvasRenderer,
  LabelLayout
]);

const chartsRef = ref();
let myChart: echarts.ECharts;

const initCharts = () => {
  if (!chartsRef.value) {
    return;
  }
  myChart = echarts.init(chartsRef.value);
  myChart.setOption(chartsOption, true, false);
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

const legend = [0, 1, 2, 3, 4, 5, 6, 7].map(idx => {
  return {
    orient: 'vertical',
    top: 10,
    left: `${idx * 100 + 150}`,
    itemHeight: 8,
    itemWidth: 12,
    textStyle: {
      width: 60,
      fontsize: 10,
      // color: '#8C8C8C',
      overflow: 'truncate'
    },
    tooltip: {
      show: true
    },
    itemGap: 6,
    data: props.dataSource.filter(item => item.name).slice(idx * 10, idx * 10 + 10)
  };
});

// 通用饼图配置
const chartsOption = {
  grid: {
    top: 32,
    right: 0,
    bottom: 45,
    left: 0
  },
  title: {
    text: props.title,
    left: 50,
    top: 'center',
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
    formatter: function (params) {
      let res = `<div style='margin-bottom:5px;width:100%;min-width:100px;font-size:12px;color:#8C8C8C;color:var(--content-text-title);'>
                    ${props.title}</div>`;
      res += `<div style="font-size: 14px;line-height: 16px;display:flex;justify-content: space-between;align-items: center;">
                         <div style="display:flex;align-items: center;">
                        <div style="width:6px;height:6px;border-radius:12px;background-color:${params.color};margin-right:4px;"></div>
                        <span style="font-size:12px;color:var(--content-text-content)">${params.name}</span>
                        </div>
                        <span style="font-size:12px;margin-left:8px;color:var(--content-text-content)">${params.value}</span>
                        </div>`;
      return res;
    }
  },
  legend: legend,
  series: [
    {
      id: props.type,
      name: props.title,
      type: 'pie',
      radius: [41, 56],
      center: [65, '50%'],
      label: {
        show: true,
        position: 'inside',
        formatter: (params) => {
          if (params.value === 0) {
            return ''; // 返回空字符串表示不显示label
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
          color: (list) => {
            // 注意 ！！！！！ 这里的数组一定要和实际的类目长度相等或大于，不然会缺少颜色报错
            const colorList = props.color.map(item => ({
              colorStart: item, // 0-1设置渐变色
              colorEnd: item
            }));
            return new echarts.graphic.LinearGradient(1, 0, 0, 0, [{ // 左、下、右、上
              offset: 0,
              color: colorList[list.dataIndex]?.colorStart
            }, {
              offset: 1,
              color: colorList[list.dataIndex]?.colorEnd
            }]);
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
};

watch(() => props.dataSource, (newValue) => {
  chartsOption.series[0].data = [];
  myChart?.setOption(chartsOption, true, false);
  chartsOption.title.text = props.title;
  chartsOption.title.subtext = props.total;
  chartsOption.series[0].data = newValue;
  myChart?.setOption(chartsOption, true, false);
}, { deep: true });

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
  <div
    ref="chartsRef"
    class="h-full flex-1"
    style="min-width: 625px;">
  </div>
</template>
