export interface PieSetting {
  key: string;
  value: string;
  type: { value: string | number, message: string }[];
  color: string [];
}

export interface PieData {
  key: string;
  title: string;
  total: number;
  color: string [];
  legend: { value: string, message: string }[];
  data: { name: string, value: number, codes?: number }[];
}

export interface BarData {
  title: string;
  unit: string;
  total?: number;
  xData: string[];
  yData: (number | null)[];
}

export type DateType = 'DAY' | 'WEEK' | 'MONTH' | 'YEAR';
