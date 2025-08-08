<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue';
import { BarData, DateType, PieData, PieSetting } from './PropsType';
import dayjs from 'dayjs';
import { http } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

// Async component definitions for chart components
const BarChartParam = defineAsyncComponent(() => import('./BarChartParam.vue'));
const PieChart = defineAsyncComponent(() => import('../PieChart/index.vue'));
const LineChart = defineAsyncComponent(() => import('@/components/LineChart/index.vue'));

/**
 * Component props interface definition
 * Defines the structure for chart loading component configuration
 */
interface Props {
  router: string; // API gateway URL
  resource: string; // Resource type for statistics
  pieSetting: PieSetting[]; // Pie chart configuration parameters
  barTitle: string; // Title for bar chart
  dateType: DateType; // Date range type
  hasPieChart: boolean; // Whether to show pie charts
  visible?: boolean; // Visibility control for statistics panel
  userId?: string; // Optional user ID for filtering
}

const props = withDefaults(defineProps<Props>(), {
  resource: '',
  pieSetting: () => [],
  barTitle: '',
  dateType: 'MONTH',
  hasPieChart: false,
  visible: true,
  userId: ''
});

const { t } = useI18n();

// Reactive data for chart state management
const datePicker = ref<[string, string] | undefined>(undefined); // Date range picker value
const dateFilters = ref<Array<{ key: string; op: string; value: string }>>([]); // Date filter criteria
const dateRangeType = ref<[string, string]>(['DAY', t('common.time.day')]); // Current date range type
const pieLoading = ref(true); // Loading state for pie charts
const pieChartData = ref<PieData[]>([]); // Pie chart data array
const barChartData = ref<BarData>({} as BarData); // Bar chart data object
const barLoading = ref(true); // Loading state for bar charts

/**
 * Handle date range changes from date picker
 * Processes date filters and updates chart data accordingly
 * @param value - Date range value from picker
 */
const dateChange = (value: [string, string] | undefined): void => {
  let unit: [string, string];
  let filters: Array<{ key: string; op: string; value: string }> = [];

  if (!value?.length) {
    // Handle empty date picker - set default date ranges
    if (!['Api', 'Service', 'Course', 'Doc', 'Blog'].includes(props.resource)) {
      filters = [
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
      unit = ['MONTH', t('common.time.month')];
    } else if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
      // Logs don't have year data, default to today when cleared
      filters = [
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
      unit = ['HOUR', t('common.time.hour')];
    } else {
      filters = [
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
      unit = ['DAY', t('common.time.day')];
    }
  } else {
    // Process selected date range
    const [startDate, endDate] = value;
    filters = [
      {
        key: 'created_date',
        op: 'GREATER_THAN_EQUAL',
        value: `"${startDate}"`
      },
      {
        key: 'created_date',
        op: 'LESS_THAN_EQUAL',
        value: `"${endDate}"`
      }
    ];

    // Determine unit based on date difference
    const diffDays = dayjs(endDate).diff(dayjs(startDate), 'day');
    if (diffDays <= 1) {
      unit = ['HOUR', t('common.time.hour')];
    } else if (diffDays <= 7) {
      unit = ['DAY', t('common.time.day')];
    } else if (diffDays <= 31) {
      unit = ['WEEK', t('common.time.week')];
    } else if (diffDays <= 365) {
      unit = ['MONTH', t('common.time.month')];
    } else {
      unit = ['YEAR', t('common.time.year')];
    }
  }

  dateFilters.value = filters;
  dateRangeType.value = unit;
  datePicker.value = value;

  // Load chart data with new filters
  loadCount();
  loadDateCount();
};

/**
 * Handle date type selection from radio buttons
 * @param value - Selected date type
 */
const selectDate = (value: DateType): void => {
  datePicker.value = undefined;
  let filters: Array<{ key: string; op: string; value: string }> = [];
  let unit: [string, string] = ['DAY', t('common.time.day')];

  switch (value) {
    case 'DAY':
      filters = [
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
      unit = ['HOUR', t('common.time.hour')];
      break;
    case 'WEEK':
      filters = [
        {
          key: 'created_date',
          op: 'GREATER_THAN_EQUAL',
          value: `"${dayjs().startOf('date').subtract(1, 'week').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
        },
        {
          key: 'created_date',
          op: 'LESS_THAN_EQUAL',
          value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"`
        }
      ];
      unit = ['DAY', t('common.time.day')];
      break;
    case 'MONTH':
      filters = [
        {
          key: 'created_date',
          op: 'GREATER_THAN_EQUAL',
          value: `"${dayjs().startOf('date').subtract(1, 'month').add(1, 'day').format('YYYY-MM-DD HH:mm:ss')}"`
        },
        {
          key: 'created_date',
          op: 'LESS_THAN_EQUAL',
          value: `"${dayjs().format('YYYY-MM-DD HH:mm:ss')}"`
        }
      ];
      unit = ['DAY', t('common.time.day')];
      break;
    case 'YEAR':
      filters = [
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
      unit = ['MONTH', t('common.time.month')];
      break;
  }

  dateFilters.value = filters;
  dateRangeType.value = unit;
  loadCount();
  loadDateCount();
};

/**
 * Load pie chart count data from API
 * Fetches statistics data and processes it for pie charts
 */
const loadCount = async (): Promise<void> => {
  if (!props.visible) {
    return;
  }

  try {
    pieLoading.value = true;

    const groupByColumns = props.pieSetting.map(item => item.key);
    const params: Record<string, any> = {
      'aggregates[0].column': 'id',
      'aggregates[0].function': 'COUNT',
      groupBy: 'STATUS',
      name: props.resource,
      groupByColumns,
      filters: dateFilters.value,
      dateRangeType: dateRangeType.value[0]
    };

    // Adjust filters based on resource type
    if (props.resource === 'ApiLogs') {
      params.filters.forEach((item: any) => {
        item.key = 'request_date';
      });
    }

    if (props.resource === 'OperationLog') {
      params.filters.forEach((item: any) => {
        item.key = 'opt_date';
      });
    }

    if (['Sms', 'Email'].includes(props.resource)) {
      params.filters.forEach((item: any) => {
        item.key = 'actual_send_date';
      });
    }

    const [error, { data }] = await http.get(`${props.router}/analysis/customization/summary`, params);
    pieLoading.value = false;

    if (error) {
      return;
    }

    if (props.resource === 'ApiLogs' && data?.status) {
      data.status = getHandleHttpStatusCode(data.status);
    }

    pieChartData.value = getCountData(props.pieSetting, data);
  } catch (error) {
    console.error('Failed to load pie chart data:', error);
    pieLoading.value = false;
  }
};

/**
 * Process count data for pie charts
 * Transforms raw API data into pie chart format
 * @param group - Pie chart configuration group
 * @param data - Raw API response data
 * @returns Processed pie chart data array
 */
const getCountData = (group: PieSetting[], data: Record<string, any>): PieData[] => {
  const dataSource: PieData[] = [];

  for (let i = 0; i < group.length; i++) {
    const column = group[i];
    const res = data[column.key];

    // Process enum type data
    if (['gender', 'type', 'status', 'real_name_status', 'target_type', 'receive_type', 'scope', 'send_type', 'api_type', 'method', 'send_status', 'push_status'].includes(column.key)) {
      setEnumDatasource(column, res, dataSource);
    }

    // Process boolean type data
    if (['sys_admin', 'locked', 'hot', 'top', 'recommend', 'comment', 'level', 'success', 'urgent', 'verification_code', 'batch'].includes(column.key)) {
      setBooleanDatasource(column, res, dataSource);
    }

    // Special handling for enabled field
    if (['enabled'].includes(column.key)) {
      if (['Tenant'].includes(props.resource)) {
        setEnumDatasource(column, res, dataSource);
      }

      if (['User', 'Group', 'Service', 'Api'].includes(props.resource)) {
        setBooleanDatasource(column, res, dataSource);
      }
    }
  }

  return dataSource;
};

/**
 * Process boolean type data for pie charts
 * @param column - Column configuration
 * @param res - API response data
 * @param dataSource - Target data source array
 */
const setBooleanDatasource = (column: PieSetting, res: Record<string, any>, dataSource: PieData[]): void => {
  const _data: Array<{ name: string; value: number }> = [];

  for (let j = 0; j < column.type.length; j++) {
    const _key = column.type[j].value;
    if (Object.keys(res).length) {
      _data.push({
        name: column.type[j]?.message,
        value: +res[_key]?.COUNT_id || 0
      });
    } else {
      _data.push({
        name: column.type[j]?.message,
        value: 0
      });
    }
  }

  const _dataSource: PieData = {
    key: column.key,
    title: column.value,
    total: res?.[0]?.TOTAL_COUNT_id ? (+res?.[0]?.TOTAL_COUNT_id || 0) : (+res?.[1]?.TOTAL_COUNT_id || 0),
    color: column.color,
    legend: column.type,
    data: _data || []
  };

  dataSource.push(_dataSource);
};

/**
 * Process enum type data for pie charts
 * @param column - Column configuration
 * @param res - API response data
 * @param dataSource - Target data source array
 */
const setEnumDatasource = (column: PieSetting, res: Record<string, any>, dataSource: PieData[]): void => {
  const _data: Array<{ name: string; value: number; codes?: number }> = [];
  let _total = 0;

  for (let j = 0; j < column.type.length; j++) {
    const _key = column.type[j].value;
    if (res?.[_key]) {
      const _item: { name: string; value: number; codes?: number } = {
        name: column.type[j]?.message,
        value: +res[_key]?.COUNT_id || 0
      };

      if (props.resource === 'ApiLogs') {
        _item.codes = res[_key]?.codes || 0;
      }

      _data.push(_item);
      _total = +res[_key]?.TOTAL_COUNT_id || 0;
    } else {
      const _item: { name: string; value: number; codes?: number } = {
        name: column.type[j]?.message,
        value: 0
      };

      if (props.resource === 'ApiLogs') {
        _item.codes = 0;
      }

      _data.push(_item);
    }
  }

  const _dataSource: PieData = {
    key: column.key,
    title: column.value,
    total: _total,
    color: column.color,
    legend: column.type,
    data: _data.length ? _data : []
  };

  dataSource.push(_dataSource);
};

/**
 * Process HTTP status codes for API logs
 * Groups status codes into ranges for better visualization
 * @param status - Raw status code data
 * @returns Processed status code data
 */
const getHandleHttpStatusCode = (status: Record<string, any>): Record<string, any> => {
  const code100: Array<{ name: string; value: string }> = [];
  const code200: Array<{ name: string; value: string }> = [];
  const code300: Array<{ name: string; value: string }> = [];
  const code400: Array<{ name: string; value: string }> = [];
  const code500: Array<{ name: string; value: string }> = [];

  const codes = Object.keys(status);
  if (!codes.length) {
    return {};
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

/**
 * Calculate total count for HTTP status code groups
 * @param codes - Array of status codes with values
 * @returns Total count as string or number
 */
const getHttpCodeNum = (codes: Array<{ name: string; value: string }>): string | number => {
  if (!codes.length) {
    return '';
  }
  return codes.map(item => item.value).reduce((n, m) => `${Number(n) + Number(m)}`);
};

/**
 * Get default date range type based on resource
 * @returns Default date range type and unit
 */
const getDefaultDateRangeType = (): [string, string] => {
  if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
    return ['HOUR', t('common.time.hour')];
  }
  if (['Dept', 'User', 'Group', 'OrgTagTarget'].includes(props.resource)) {
    return ['MONTH', t('common.time.month')];
  }
  return ['DAY', t('common.time.day')];
};

/**
 * Get default date filters based on resource
 * @returns Default date filter criteria
 */
const getDefaultDateFilters = (): Array<{ key: string; op: string; value: string }> => {
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

// Initialize default values
dateRangeType.value = getDefaultDateRangeType();
dateFilters.value = getDefaultDateFilters();

/**
 * Load bar chart date count data from API
 * Fetches time-series data for bar/line charts
 */
const loadDateCount = async (): Promise<void> => {
  if (!props.visible) {
    return;
  }

  try {
    barLoading.value = true;

    const params: Record<string, any> = {
      groupByColumns: 'created_date',
      groupBy: 'DATE',
      name: props.resource === 'OrgTagTarget' ? 'OrgTag' : props.resource,
      dateRangeType: dateRangeType.value[0],
      filters: dateFilters.value
    };

    // Adjust parameters based on resource type
    if (props.resource === 'ApiLogs') {
      params.groupByColumns = 'request_date';
      params.filters.forEach((item: any) => {
        item.key = 'request_date';
      });
    }

    if (props.resource === 'OperationLog') {
      params.groupByColumns = 'opt_date';
      params.filters.forEach((item: any) => {
        item.key = 'opt_date';
      });
    }

    if (['Sms', 'Email'].includes(props.resource)) {
      params.groupByColumns = 'actual_send_date';
      params.filters.forEach((item: any) => {
        item.key = 'actual_send_date';
      });
    }

    const [error, { data }] = await http.get(`${props.router}/analysis/customization/summary`, params);
    barLoading.value = false;

    if (error) {
      return;
    }

    setBarCharCount(data);
  } catch (error) {
    console.error('Failed to load bar chart data:', error);
    barLoading.value = false;
  }
};

/**
 * Set default bar chart data
 */
const setBarChartDefault = (): void => {
  barChartData.value.title = `${props.barTitle}`;
  barChartData.value.unit = `${t('statistics.timeUnit')}: ${dateRangeType.value[1]}`;
  barChartData.value.xData = [];
  barChartData.value.yData = [];
};

/**
 * Set bar chart data from API response
 * @param data - API response data
 */
const setBarCharCount = (data: Record<string, any>): void => {
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

// Computed properties for chart layout
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
    case 3:
      return 'width:45%';
    case 2:
      return 'margin-left: 5.75rem; width:25%';
    case 1:
      return 'margin-left: 5.75rem; width:15%';
    default:
      return 'width:100%';
  }
});

// Dynamic height calculation for chart container
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

/**
 * Calculate step height for chart container
 * @param number - Number of data items
 * @param step - Step increment
 * @returns Calculated height
 */
function calcStep (number: number, step: number): number {
  if (number <= 8) {
    return 240;
  }
  return Math.ceil((number - 8) / 2) * step + 240;
}

// Lifecycle hooks
onMounted(() => {
  loadDateCount();
});

// Watch for pie chart visibility changes
watch(() => props.hasPieChart, newValue => {
  if (newValue) {
    loadCount();
  }
}, {
  immediate: true
});
</script>

<template>
  <div
    v-if="props.visible"
    class="statistics-container overflow-hidden relative"
    :style="{ height: height + 'px' }">
    <BarChartParam
      :datePicker="datePicker"
      :resource="props.resource"
      :dateType="props.dateType"
      class="search-bar"
      @selectDate="selectDate"
      @dateChange="dateChange" />
    <div
      v-if="!pieLoading && height > 0"
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
.statistics-container {
  transition: all 300ms linear 0ms;
}
</style>
