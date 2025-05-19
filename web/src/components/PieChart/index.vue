<script setup lang="ts">
import { ref, onMounted, watch, onBeforeMount, inject, Ref } from 'vue';
import * as echarts from 'echarts/core';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components';

import { PieChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import DarkTheme from '../Statistics/echartsDark.json';
import GrayTheme from '../Statistics/echartsGray.json';

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

const tenantInfo: Ref = inject('tenantInfo', ref());
const chartsRef = ref();
let myChart: echarts.ECharts;

const initCharts = () => {
  if (!chartsRef.value) {
    return;
  }
  echarts.registerTheme(tenantInfo.value.preference.themeCode, tenantInfo.value.preference.themeCode === 'dark' ? DarkTheme : GrayTheme);
  myChart = echarts.init(chartsRef.value, tenantInfo.value.preference.themeCode, { renderer: 'canvas' });
  myChart.setOption(chartsOption, true, false);
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

const legend = props.dataSource.length > 8 && props.dataSource.length < 12
  ? [0, 1, 2].map(idx => {
      return {
        orient: 'vertical',
        top: 124,
        left: `${idx * 30 + 5}%`,
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
        data: props.dataSource.filter(item => item.name).slice(idx * 4, idx * 4 + 4)
      };
    }) : props.dataSource.length > 2
      ? [{
          orient: 'vertical',
          top: 124,
          right: '50%',
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
          data: props.dataSource.filter(item => item.name).slice(0, Math.ceil(props.dataSource?.length / 2))
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
            // color: '#8C8C8C',
            overflow: 'truncate'
          },
          tooltip: {
            show: true
          },
          itemGap: 6,
          data: props.dataSource.filter(item => item.name).slice(Math.ceil(props.dataSource?.length / 2), props.dataSource?.length)
        }]
      : {
          orient: 'horizontal',
          top: 124,
          left: 'center',
          itemHeight: 8,
          itemWidth: 12,
          textStyle: {
            width: 50,
            fontsize: 10,
            // color: '#8C8C8C',
            overflow: 'truncate'
          },
          tooltip: {
            show: true
          },
          itemGap: 6,
          data: props.dataSource.filter(item => item.name)
        };

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
    left: 'center',
    top: 50,
    textStyle: {
      fontSize: 12,
      fontWeight: 'normal'
      // color: '#8C8C8C'
    },
    subtext: props.total,
    subtextStyle: {
      fontSize: 12,
      fontWeight: 'normal'
      // color: '#8C8C8C'
    },
    itemGap: 2
  },
  tooltip: {
    trigger: 'item',
    formatter: function (params) {
      let res = `<div style='margin-bottom:5px;width:100%;min-width:100px;font-size:12px;color:#8C8C8C;color:var(--content-text-title);'>
                    ${props.title}</div>`;
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
  legend: legend,
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
              colorStart: `rgba(${item},0.5)`, // 0-1设置渐变色
              colorEnd: `rgba(${item},1)`
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
        color: tenantInfo.value.preference.themeCode === 'dark' ? '#666874' : '#f5f5f5'
      }
    }
  ]
};

watch(() => props.dataSource, (newValue) => {
  chartsOption.series[0].data = [];
  myChart?.setOption(chartsOption, true, false);
  chartsOption.title.text = props.title;
  chartsOption.title.subtext = props.total;
  chartsOption.legend = newValue.length > 2
    ? [{
        orient: 'vertical',
        top: 124,
        right: '50%',
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
        data: newValue.filter(item => item.name).slice(0, Math.ceil(newValue?.length / 2))
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
          // color: '#8C8C8C',
          overflow: 'truncate'
        },
        tooltip: {
          show: true
        },
        itemGap: 6,
        data: newValue.filter(item => item.name).slice(Math.ceil(newValue?.length / 2), newValue?.length)
      }]
    : {
        orient: 'horizontal',
        top: 124,
        left: 'center',
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
        data: newValue.filter(item => item.name)
      };
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
  <!--  {{legend}}-->
  <div
    ref="chartsRef"
    class="flex-1 h-full"
    style="width: 20%;max-width: 200px;"></div>
</template>
