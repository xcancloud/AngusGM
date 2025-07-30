<script setup lang="ts">
import { ref, defineAsyncComponent, onMounted, watch, computed } from 'vue';
import { PieSetting, PieData, BarData, DateType } from './PropsType';
import dayjs from 'dayjs';
import { http } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

const BarChartParam = defineAsyncComponent(() => import('./BarChartParam.vue'));
const PieChart = defineAsyncComponent(() => import('../PieChart/index.vue'));
const LineChart = defineAsyncComponent(() => import('@/components/LineChart/index.vue'));

interface Props {
  geteway: string,
  resource: string,
  pieParmas: PieSetting[];
  barTitle: string;
  dateType: DateType;
  hasPieChart: boolean;
  visible?: boolean;
  userId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  resource: '',
  pieParmas: () => [],
  barTitle: '',
  dateType: 'MONTH',
  hasPieChart: false,
  visible: true,
  userId: ''
});

const { t } = useI18n();

const datePicker = ref([]);
const dateChange = (value) => {
  let unit: string[] = [];
  let filters: { key: string, op: string, value: string }[] = [];
  if (!value?.length) {
    // 清空时间框  接口 服务 视频 博客 文档展示近一年
    if (!['Api', 'Service', 'Course', 'Doc', 'Blog'].includes(props.resource)) {
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').subtract(1, 'year').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"` }];
      unit = ['MONTH', t('month')];
      return;
    }
    // 清空时间框 日志没有年 清空时间框默认展示今天
    if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      {
        key: 'created_date',
        op: 'LESS_THAN_EQUAL',
        value: `"${dayjs().endOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      }];
      unit = ['HOUR', t('hour')];
      return;
    }

    // 其他清空 默认展示近一月
    filters = [{
      key: 'created_date',
      op: 'GREATER_THAN_EQUAL',
      value: `"${dayjs().startOf('date').subtract(1, 'week').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
    },
    { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"` }];
    unit = ['DAY', t('day')];
  }

  filters = [{ key: 'created_date', op: 'GREATER_THAN_EQUAL', value: `"${value[0]}"` },
    { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${value[1]}"` }];
  const startDate = dayjs(value[0]);
  const endDate = dayjs(value[1]);
  const often = endDate.diff(startDate, 'day');
  if (often <= 1) {
    unit = ['HOUR', t('hour')];
  } else if (often > 1 && often <= 30) {
    unit = ['DAY', t('day')];
  } else if (often > 30 && often <= 120) {
    unit = ['WEEK', t('week')];
  } else if (often > 120 && often <= 730) {
    unit = ['MONTH', t('month')];
  } else {
    unit = ['YEAR', t('year')];
  }

  dateFilters.value = filters;
  dateRangeType.value = unit;
  loadCount();
  loadDateCount();
};

const selectDate = (value) => {
  datePicker.value = [];
  let filters: { key: string, op: string, value: string }[] = [];
  let unit: string[] = [];
  switch (value) {
    case 'DAY':
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      {
        key: 'created_date',
        op: 'LESS_THAN_EQUAL',
        value: `"${dayjs().endOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      }];
      unit = ['HOUR', t('hour')];
      break;
    case 'WEEK':
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').subtract(1, 'week').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"` }];
      unit = ['DAY', t('day')];
      break;
    case 'MONTH':
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').subtract(1, 'month').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"` }];
      unit = ['DAY', t('day')];
      break;
    case 'YEAR':
      filters = [{
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').subtract(1, 'year').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      { key: 'created_date', op: 'LESS_THAN_EQUAL', value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"` }];
      unit = ['MONTH', t('month')];
      break;
  }

  dateFilters.value = filters;
  dateRangeType.value = unit;
  loadCount();
  loadDateCount();
};

/**
 * 用户饼图统计参数
 * resource:对应数据库真实,有多少种name表示当前服务下分多少种统计（eg:User、Dept、Group、Tenant ）
 * groupByColumns: groupByColumns对应数据库表的字段,目前分为时间范围类型和其他类型，
 * aggregates[0].column: 通常统计传id即可
 * aggregates[0].function：统计类型 默认COUNT，其他参数参考/uc/api/v1/analysis/customization/summary接口
 * groupBy：分组类型，即按什么分组 具体参数参考/uc/api/v1/analysis/customization/summary接口
 * 备注：以上参数只支持结果里返回的，具体返回结构咨询后端
 */
// 饼图公共参数
const publicParams = {
  'aggregates[0].column': 'id',
  'aggregates[0].function': 'COUNT',
  groupBy: 'STATUS',
  name: props.resource
};
const pieloading = ref(true); // 饼图统计是否加载完成
const pieChartData = ref<PieData []>([]);
const loadCount = async () => {
  const groupByColumns = props.pieParmas.map(item => item.key);
  groupByColumns?.length ? publicParams.groupBy = 'STATUS' : delete publicParams.groupBy;
  const params = {
    ...publicParams,
    groupByColumns,
    filters: dateFilters.value,
    dateRangeType: dateRangeType.value[0]
  };

  if (props.resource === 'ApiLogs') {
    params.filters.forEach(item => {
      item.key = 'request_date';
    });
  }

  if (props.resource === 'OperationLog') {
    params.filters.forEach(item => {
      item.key = 'opt_date';
    });
  }

  if (['Sms', 'Email'].includes(props.resource)) {
    params.filters.forEach(item => {
      item.key = 'actual_send_date';
    });
  }

  const [error, { data }] = await http.get(`${props.geteway}/analysis/customization/summary`, params);
  pieloading.value = false;
  if (error) {
    return;
  }

  if (props.resource === 'ApiLogs' && data?.status) {
    data.status = getHandleHttpStatusCode(data.status);
  }

  pieChartData.value = getCountData(props.pieParmas, data);
};

const getCountData = (group, data) => {
  const dataSource: PieData[] = [];
  for (let i = 0; i < group.length; i++) {
    const cloum = group[i];
    const res = data[cloum.key];

    // 所有来源只有枚举类型数据
    if (['source', 'gender', 'type', 'status', 'real_name_status', 'target_type', 'receive_type', 'scope', 'send_type', 'api_type', 'method', 'send_status', 'push_status'].includes(cloum.key)) {
      setEnumDatasource(cloum, res, dataSource);
    }

    // 所有来源只有boolean 类型数据
    if (['sys_admin', 'locked', 'hot', 'top', 'recommend', 'comment', 'level', 'success', 'urgent', 'verification_code', 'batch'].includes(cloum.key)) {
      setBooleanDatasource(cloum, res, dataSource);
    }

    // enabled目前有三种情况
    if (['enabled'].includes(cloum.key)) {
      if (['Tenant'].includes(props.resource)) {
        setEnumDatasource(cloum, res, dataSource);
      }

      if (['User', 'Group', 'Service', 'Api'].includes(props.resource)) {
        setBooleanDatasource(cloum, res, dataSource);
      }
    }
  }
  return dataSource;
};

// 设置布尔型数据
const setBooleanDatasource = (cloum, res, dataSource) => {
  const _data: { name: string, value: number | null }[] = [];
  for (let j = 0; j < cloum.type.length; j++) {
    const _key = cloum.type[j].value;
    if (Object.keys(res).length) {
      _data.push({ name: cloum.type[j]?.message, value: +res[_key]?.COUNT_id || 0 });
    } else {
      _data.push({ name: cloum.type[j]?.message, value: 0 });
    }
  }
  const _dataSource = {
    key: cloum.key,
    title: cloum.value,
    total: res?.[0]?.TOTAL_COUNT_id ? (+res?.[0]?.TOTAL_COUNT_id || 0) : (+res?.[1]?.TOTAL_COUNT_id || 0),
    color: cloum.color,
    legend: cloum.type,
    data: _data || []
  };
  dataSource.push(_dataSource);
};
// 设置枚举型数据
const setEnumDatasource = (cloum, res, dataSource) => {
  const _data: { name: string, value: number | null, codes?: number | null }[] = [];
  let _total = 0;

  for (let j = 0; j < cloum.type.length; j++) {
    const _key = cloum.type[j].value;
    if (res?.[_key]) {
      const _item: { name: string, value: number | null, codes?: number | null } = {
        name: cloum.type[j]?.message,
        value: +res[_key]?.COUNT_id || 0
      };
      if (props.resource === 'ApiLogs') {
        _item.codes = res[_key]?.codes || 0;
      }
      _data.push(_item);
      _total = +res[_key]?.TOTAL_COUNT_id || 0;
    } else {
      const _item: { name: string, value: number | null, codes?: number | null } = {
        name: cloum.type[j]?.message,
        value: 0
      };
      if (props.resource === 'ApiLogs') {
        _item.codes = 0;
      }
      _data.push(_item);
    }
  }
  const _dataSource = {
    key: cloum.key,
    title: cloum.value,
    total: _total,
    color: cloum.color,
    legend: cloum.type,
    data: _data.length ? _data : []
  };
  dataSource.push(_dataSource);
};

// 处理HTTP状态码
const getHandleHttpStatusCode = (status) => {
  const code100: { name: string, value: string }[] = [];
  const code200: { name: string, value: string }[] = [];
  const code300: { name: string, value: string }[] = [];
  const code400: { name: string, value: string }[] = [];
  const code500: { name: string, value: string }[] = [];
  const codes = Object.keys(status);
  if (!codes.length) {
    return;
  }
  let _total = 0;
  for (let i = 0; i < codes.length; i++) {
    const _code = codes[i];
    _total = status[_code]?.TOTAL_COUNT_id;
    if (+_code <= 199) {
      code100.push({ name: codes[i], value: status[_code]?.COUNT_id });
    } else if (+_code <= 299) {
      code200.push({ name: codes[i], value: status[_code]?.COUNT_id });
    } else if (+_code <= 399) {
      code300.push({ name: codes[i], value: status[_code]?.COUNT_id });
    } else if (+_code <= 499) {
      code400.push({ name: codes[i], value: status[_code]?.COUNT_id });
    } else {
      code500.push({ name: codes[i], value: status[_code]?.COUNT_id });
    }
  }
  return {
    100: { codes: code100, TOTAL_COUNT_id: _total, COUNT_id: getHttpCodeNum(code100) },
    200: { codes: code200, TOTAL_COUNT_id: _total, COUNT_id: getHttpCodeNum(code200) },
    300: { codes: code300, TOTAL_COUNT_id: _total, COUNT_id: getHttpCodeNum(code300) },
    400: { codes: code400, TOTAL_COUNT_id: _total, COUNT_id: getHttpCodeNum(code400) },
    500: { codes: code500, TOTAL_COUNT_id: _total, COUNT_id: getHttpCodeNum(code500) }
  };
};

const getHttpCodeNum = (codes: { name: string, value: string }[]): string | number => {
  if (!codes.length) {
    return '';
  }
  return codes.map(item => item.value).reduce((n, m) => `${Number(n) + Number(m)}`);
};

// 柱状图数据
const barChartData = ref<BarData>({} as BarData);
const getDefaultDateRangeType = () => {
  if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
    return ['HOUR', t('hour')];
  }
  if (['Dept', 'User', 'Group', 'OrgTagTarget'].includes(props.resource)) {
    return ['MONTH', t('month')];
  }

  return ['DAY', t('day')];
};
// 默认查询单位
const dateRangeType = ref(getDefaultDateRangeType());

const getDefaultDateFilters = () => {
  if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
    return [
      {
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      },

      {
        key: 'created_date',
        op: 'LESS_THAN_EQUAL',
        value: `"${dayjs().endOf('date').format('YYYY-MM-DD HH:mm:ss')}"`
      }
    ];
  }

  if (['Dept', 'User', 'Group', 'OrgTagTarget'].includes(props.resource)) {
    return [
      {
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${dayjs().startOf('date').subtract(1, 'year').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
      },
      {
        key: 'created_date',
        op: 'LESS_THAN_EQUAL',
        value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"`
      }
    ];
  }

  return [
    {
      key: 'created_date',
      op: 'GREATER_THAN_EQUAL',
      value: `"${dayjs().subtract(1, 'month').format('YYYY-MM-DD HH:mm:ss')}"`
    },
    {
      key: 'created_date',
      op: 'LESS_THAN_EQUAL',
      value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"`
    }
  ];
};

// 默认查询时间范围单位
const dateFilters = ref(getDefaultDateFilters());
const barLoading = ref(true); // 柱图统计是否加载完成
// 请求柱状图数据
const loadDateCount = async () => {
  const params = {
    groupByColumns: 'created_date',
    groupBy: 'DATE',
    name: props.resource === 'OrgTagTarget' ? 'OrgTag' : props.resource,
    dateRangeType: dateRangeType.value[0],
    filters: dateFilters.value
  };

  if (props.resource === 'ApiLogs') {
    params.groupByColumns = 'request_date';
    params.filters.forEach(item => {
      item.key = 'request_date';
    });
  }

  if (props.resource === 'OperationLog') {
    params.groupByColumns = 'opt_date';
    params.filters.forEach(item => {
      item.key = 'opt_date';
    });
  }

  if (['Sms', 'Email'].includes(props.resource)) {
    params.groupByColumns = 'actual_send_date';
    params.filters.forEach(item => {
      item.key = 'actual_send_date';
    });
  }

  const [error, { data }] = await http.get(`${props.geteway}/analysis/customization/summary`, params);
  barLoading.value = false;
  if (error) {
    return;
  }
  setBarCharCount(data);
};

// 设置柱状图默认数据
const setBarChartDefault = () => {
  barChartData.value.title = `${t('new')}${props.barTitle}`;
  barChartData.value.unit = `${t('时间单位')}: ${dateRangeType.value[1]}`;
  barChartData.value.xData = [];
  barChartData.value.yData = [];
};

// 设置柱状图数据
const setBarCharCount = (data: Record<string, any>) => {
  if (!data) {
    setBarChartDefault();
    barChartData.value.yData = [0];
    return;
  }
  const keys = Object.keys(data);
  if (!keys.length) {
    setBarChartDefault();
    barChartData.value.yData = [0];
    return;
  }
  setBarChartDefault();

  barChartData.value.xData = dateRangeType.value[0] !== 'HOUR' ? keys : keys.map(item => dayjs(item).format('HH'));
  barChartData.value.yData = Object.values(data).map(item => item.COUNT_id ? +item.COUNT_id : null);
};
// 不要删除
// 该接口查看可统计的类型和字段
// const loadCountGroup = async () => {
//   const [error, { data }] = await http.get(`/${props.geteway}/api/v1/analysis/customization/summary/definition`);
//   if (error) {
//     return;
//   }
// };

onMounted(() => {
  // loadCountGroup();
  loadDateCount();
});

watch(() => props.hasPieChart, newValue => {
  if (newValue) {
    loadCount();
  }
}, {
  immediate: true
});

const getBarChartWidth = computed(() => {
  switch (pieChartData.value.length) {
    case 5:
      return 'width:40%';
    case 4:
      return 'width:50%';
    default:
      return 'width:50%';
  }
});

const getPieChartWidth = computed(() => {
  switch (pieChartData.value.length) {
    case 5:
      return 'width:60%';
    case 4:
      return 'width:50%';
    default:
      return 'width:50%';
  }
});

let height = 0;
watch(() => pieChartData.value, newValue => {
  if (!props.visible) {
    return;
  }
  if (!newValue.length) {
    height = 240;
  }

  let maxlength = 8;
  for (let i = 0; i < pieChartData.value.length; i++) {
    if (pieChartData.value[i].data?.length > maxlength) {
      maxlength = pieChartData.value[i].data?.length;
    }
  }
  height = calcStep(maxlength, 12);
});

function calcStep (number, step) {
  if (number <= 8) {
    return 240;
  }
  return Math.ceil((number - 8) / 2) * step + 240;
}
</script>
<template>
  <div
    class="statistics-container overflow-hidden relative"
    :class="{'show-statistics':props.visible}"
    :style="{height:+'px'}">
    <BarChartParam
      :datePicker="datePicker"
      :resource="props.resource"
      :dateType="props.dateType"
      class="search-bar"
      @selectDate="selectDate"
      @dateChange="dateChange" />
    <div
      v-if="!pieloading && height>0"
      class="flex"
      style="height: calc(100% - 28px);">
      <LineChart
        :title="barChartData?.title"
        :unit="barChartData?.unit"
        :xData="barChartData?.xData"
        :yData="barChartData?.yData"
        :style="getBarChartWidth" />
      <div class="flex ml-2" :style="getPieChartWidth">
        <PieChart
          v-for="item in pieChartData"
          :key="item.key"
          :type="item.key"
          :title="item.title"
          :color="item.color"
          :total="item.total"
          :legend="item.legend"
          :dataSource="item.data"
          :source="props.resource" />
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<style scoped>
.statistics-container.show-statistics {
  height: 250px;
  opacity: 1;
}

.statistics-container {
  height: 0;
  transition: all 300ms linear 0ms;
  opacity: 0;
}
</style>
