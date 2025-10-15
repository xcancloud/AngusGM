import { EnumMessage } from '@xcan-angus/infra';
import { GroupBy, AggregateFunction, DateRangeType, ChartType } from './enums';

// Statistical query parameters type
export interface SummaryQueryParams {
  name: string; // Resource name
  groupBy?: GroupBy; // Grouping method
  groupByColumns?: string[]; // Grouping columns
  dateRangeType?: DateRangeType; // Date range type
  aggregates?: Array<{
    column: string;
    function: AggregateFunction;
  }>;
  filters?: Array<{
    key: string;
    op: string;
    value: string;
  }>;
  closeMultiTenantCtrl?: boolean; // Whether to disable multi-tenant control
  projectId?: number; // Project ID
}

// Statistical return data type
export interface SummaryData {
  [key: string]: any;
}

// Line chart data type
export interface LineChartData {
  title: string;
  unit: DateRangeType;
  total?: number;
  xData: string[];
  yData: (number | null)[];
  color?: string | string[]; // Line chart color configuration
}

// Pie chart data type
export interface PieChartData {
  key: string;
  title: string;
  total: number;
  color: string[];
  legend: { value: string | number; message: string }[];
  data: { name: string; value: number; codes?: number }[];
  legendPosition: 'bottom'|'right',
  size: 'normal'|'small',
  legendGroupSize: number
}

// Chart configuration type
export interface ChartConfig {
  type: ChartType;
  title: string;
  field: string | string[]; // Statistical field, supports single or multiple fields
  enumKey: EnumMessage<string>[] | EnumMessage<string>[][];
  // Pie chart specific configuration
  pieConfig?: {
    color: string[];
    legend: { value: string | number; message: string }[];
  };
  // Line chart specific configuration
  lineConfig?: {
    unit: string;
    color?: string | string[]; // Line chart color configuration
  };
  // Aggregation configuration
  aggregates?: Array<{
    column: string;
    function: AggregateFunction;
  }>;
}
