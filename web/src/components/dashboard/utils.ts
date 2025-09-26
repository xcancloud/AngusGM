import { http } from '@xcan-angus/infra';
import { SummaryQueryParams, SummaryData, LineChartData, PieChartData } from './types';
import { GroupBy, DateRangeType } from './enums';
import dayjs from 'dayjs';
const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

/**
 * Statistical data query utility method
 * @param apiRouter API route prefix
 * @param params Query parameters
 * @returns Promise<SummaryData>
 */
export const fetchSummaryData = async (
  apiRouter: string,
  params: SummaryQueryParams
): Promise<SummaryData> => {
  // Construct parameter object
  const requestParams: any = {
    name: params.name,
    groupBy: params.groupBy
  };

  // Add group by column parameters, support multiple fields
  if (params.groupByColumns && params.groupByColumns.length > 0) {
    params.groupByColumns.forEach((column, index) => {
      requestParams[`groupByColumns[${index}]`] = column;
    });
  }

  // Only add dateRangeType parameter when groupBy is DATE
  if (params.groupBy === GroupBy.DATE && params.dateRangeType) {
    requestParams.dateRangeType = params.dateRangeType;
  }

  // Add aggregation parameters
  if (params.aggregates) {
    params.aggregates.forEach((aggregate, index) => {
      requestParams[`aggregates[${index}].column`] = aggregate.column;
      requestParams[`aggregates[${index}].function`] = aggregate.function;
    });
  }

  // Add filter parameters
  if (params.filters) {
    params.filters.forEach((filter, index) => {
      requestParams[`filters[${index}].key`] = filter.key;
      requestParams[`filters[${index}].op`] = filter.op;
      requestParams[`filters[${index}].value`] = filter.value;
    });
  }

  // Add other optional parameters
  if (params.closeMultiTenantCtrl !== undefined) {
    requestParams.closeMultiTenantCtrl = params.closeMultiTenantCtrl;
  }
  if (params.projectId !== undefined) {
    requestParams.projectId = params.projectId;
  }

  const [error, { data }] = await http.get(
    `${apiRouter}/analysis/customization/summary`,
    requestParams
  );

  if (error) {
    throw new Error('Failed to fetch summary data');
  }

  return data;
};

/**
 * Convert statistical data to line chart data
 * @param data Statistical data
 * @param title Chart title
 * @param unit Unit
 * @param color Chart color configuration
 * @returns LineChartData
 */
export const convertToLineChartData = (
  data: Record<string, any>,
  title: string,
  unit: DateRangeType,
  color?: string | string[]
): LineChartData => {
  if (!data) {
    return {
      title,
      unit,
      xData: [],
      yData: [],
      color
    };
  }

  const keys = Object.keys(data);
  if (!keys.length) {
    return {
      title,
      unit,
      xData: [],
      yData: [],
      color
    };
  }

  return {
    title,
    unit,
    xData: keys,
    yData: Object.values(data).map(item => (item.COUNT_id ? +item.COUNT_id : 0)),
    color
  };
};

/**
 * Convert statistical data to pie chart data
 * @param group Group configuration
 * @param data Statistical data
 * @returns PieChartData[]
 */
export const convertToPieChartData = (
  group: Array<{
    key: string;
    value: string;
    type: { value: string | number; message: string }[];
    color: string[];
  }>,
  data: Record<string, any>
): PieChartData[] => {
  const dataSource: PieChartData[] = [];

  for (let i = 0; i < group.length; i++) {
    const column = group[i];
    const res = data[column.key];

    if (!res) {
      const _dataSource: PieChartData = {
        key: column.key,
        title: column.value,
        total: 0,
        color: column.color,
        legend: column.type,
        data: column.type.map(m => ({ name: m.message, value: 0 }))
      };
      dataSource.push(_dataSource);
      continue;
    }

    const arr = Object.entries(res);
    if (!arr.length) {
      const _dataSource: PieChartData = {
        key: column.key,
        title: column.value,
        total: 0,
        color: column.color,
        legend: column.type,
        data: column.type.map(m => ({ name: m.message, value: 0 }))
      };
      dataSource.push(_dataSource);
      continue;
    }

    // Determine whether each group contains an empty object.
    const _group = Object.keys(res);
    if (!_group.length) {
      continue;
    }

    // Enum type data processing
    if (['target_type'].includes(column.key)) {
      const _data: { name: string; value: number; codes?: number }[] = [];
      let _total = 0;

      for (let j = 0; j < column.type.length; j++) {
        const _key = column.type[j].value;
        if (res[_key]) {
          const _item: { name: string; value: number; codes?: number } = {
            name: column.type[j]?.message,
            value: +res[_key]?.COUNT_id
          };
          _data.push(_item);
          _total = +res[_key]?.TOTAL_COUNT_id;
        } else {
          _data.push({ name: column.type[j]?.message, value: 0 });
        }
      }

      const _dataSource: PieChartData = {
        key: column.key,
        title: column.value,
        total: _total,
        color: column.color,
        legend: column.type,
        data: _data.length ? _data : []
      };
      dataSource.push(_dataSource);
    }

    // Boolean type data processing
    if (['sys_admin_flag'].includes(column.key)) {
      const _data: { name: string; value: number }[] = [];

      for (let j = 0; j < column.type.length; j++) {
        const _key = column.type[j].value;
        if (res[_key]) {
          _data.push({ name: column.type[j]?.message, value: +res[_key]?.COUNT_id });
        } else {
          _data.push({ name: column.type[j]?.message, value: 0 });
        }
      }

      const _dataSource: PieChartData = {
        key: column.key,
        title: column.value,
        total: res[0]?.TOTAL_COUNT_id ? +res[0].TOTAL_COUNT_id : +res[1]?.TOTAL_COUNT_id,
        color: column.color,
        legend: column.type,
        data: _data || []
      };
      dataSource.push(_dataSource);
    }
  }

  return dataSource;
};

/**
 * Generate default date filter conditions based on date range type
 * @param dateType Date type
 * @returns Default date filter conditions
 */
export const getDefaultDateFilters = (dateType: DateRangeType, dataKey = 'created_date') => {
  const now = dayjs();
  switch (dateType) {
    case DateRangeType.DAY:
      return [
        {
          key: dataKey,
          op: 'GREATER_THAN_EQUAL',
          value: `"${now.startOf('date').format(DATE_TIME_FORMAT)}"`
        },
        {
          key: dataKey,
          op: 'LESS_THAN_EQUAL',
          value: `"${now.endOf('date').format(DATE_TIME_FORMAT)}"`
        }
      ];
    case DateRangeType.WEEK:
      return [
        {
          key: dataKey,
          op: 'GREATER_THAN_EQUAL',
          value: `"${now.startOf('date').subtract(1, 'week').add(1, 'day').format(DATE_TIME_FORMAT)}"`
        },
        {
          key: dataKey,
          op: 'LESS_THAN_EQUAL',
          value: `"${now.format(DATE_TIME_FORMAT)}"`
        }
      ];
    case DateRangeType.MONTH:
      return [
        {
          key: dataKey,
          op: 'GREATER_THAN_EQUAL',
          value: `"${now.startOf('date').subtract(1, 'month').add(1, 'day').format(DATE_TIME_FORMAT)}"`
        },
        {
          key: dataKey,
          op: 'LESS_THAN_EQUAL',
          value: `"${now.format(DATE_TIME_FORMAT)}"`
        }
      ];
    case DateRangeType.YEAR:
      return [
        {
          key: dataKey,
          op: 'GREATER_THAN_EQUAL',
          value: `"${now.startOf('date').subtract(1, 'year').add(1, 'day').format(DATE_TIME_FORMAT)}"`
        },
        {
          key: dataKey,
          op: 'LESS_THAN_EQUAL',
          value: `"${now.format(DATE_TIME_FORMAT)}"`
        }
      ];
    default:
      return [
        {
          key: dataKey,
          op: 'GREATER_THAN_EQUAL',
          value: `"${now.subtract(1, 'month').format(DATE_TIME_FORMAT)}"`
        },
        {
          key: dataKey,
          op: 'LESS_THAN_EQUAL',
          value: `"${now.format(DATE_TIME_FORMAT)}"`
        }
      ];
  }
};

/**
 * Generate filter conditions based on custom date range
 * @param startDate Start date
 * @param endDate End date
 * @returns Date filter conditions and unit
 */
export const getDateFiltersAndUnit = (startDate: string, endDate: string, dateKey = 'created_date') => {
  const start = dayjs(startDate);
  const end = dayjs(endDate);
  const diffDays = end.diff(start, 'day');

  let unit: string[] = [];

  if (diffDays <= 1) {
    unit = ['HOUR', '小时'];
  } else if (diffDays > 1 && diffDays <= 30) {
    unit = ['DAY', '天'];
  } else if (diffDays > 30 && diffDays <= 120) {
    unit = ['WEEK', '周'];
  } else if (diffDays > 120 && diffDays <= 730) {
    unit = ['MONTH', '月'];
  } else {
    unit = ['YEAR', '年'];
  }

  return {
    filters: [
      { key: dateKey, op: 'GREATER_THAN_EQUAL', value: `"${startDate}"` },
      { key: dateKey, op: 'LESS_THAN_EQUAL', value: `"${endDate}"` }
    ],
    unit
  };
};

/**
 * Utility method for constructing filter conditions
 * @param filters Filter conditions array
 * @returns Constructed filter conditions
 */
export const buildFilters = (filters: Array<{key: string; op: string; value: string}> = []) => {
  return filters.map((filter, index) => ({
    [`filters[${index}].key`]: filter.key,
    [`filters[${index}].op`]: filter.op,
    [`filters[${index}].value`]: filter.value
  })).reduce((acc, curr) => ({ ...acc, ...curr }), {});
};
