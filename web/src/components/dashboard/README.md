# 项目活动统计看板

## 概述

项目活动统计看板是一个可配置的图表展示组件，支持折线图和饼图，并提供可选的图表参数控制功能。

## 组件特性

- **多图表支持**：支持折线图和饼图
- **响应式布局**：可配置网格列数和间距
- **数据过滤**：支持日期范围和自定义过滤条件
- **可配置控制面板**：可选择是否显示图表参数控制组件

## 使用方法

### 基本用法

```vue
<Dashboard
  :config="dashboardConfig"
  :apiRouter="TESTER"
  resource="Activity"
  :barTitle="活动统计"
  :dateType="DateRangeType.MONTH"
  :showChartParam="false" />
```

### 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `config` | `object` | - | 图表配置对象（必需） |
| `apiRouter` | `string` | `TESTER` | API 路由 |
| `resource` | `string` | - | 资源名称（必需） |
| `barTitle` | `string` | - | 标题（必需） |
| `dateType` | `DateRangeType` | `DateRangeType.MONTH` | 默认日期类型 |
| `userId` | `string` | `''` | 用户ID |
| `searchParams` | `array` | `[]` | 搜索参数 |
| `showChartParam` | `boolean` | `false` | 是否显示图表参数控制组件 |

### 图表参数控制组件

`showChartParam` 属性控制是否显示图表参数控制组件（ChartParam），该组件提供：

- **日期类型选择**：今天、最近7天、最近一个月、最近一年
- **自定义日期范围**：日期范围选择器
- **实时数据刷新**：选择后自动刷新图表数据

#### 显示控制组件

```vue
<!-- 显示图表参数控制组件 -->
<Dashboard
  :config="dashboardConfig"
  resource="Activity"
  :showChartParam="true" />
```

#### 隐藏控制组件

```vue
<!-- 隐藏图表参数控制组件（默认） -->
<Dashboard
  :config="dashboardConfig"
  resource="Activity"
  :showChartParam="false" />
```

### 图表配置

#### 折线图配置

```javascript
{
  type: ChartType.LINE,
  title: '活动趋势',
  field: 'opt_date'
}
```

#### 饼图配置

```javascript
{
  type: ChartType.PIE,
  title: '资源类型分布',
  field: 'target_type',
  enumKey: enumUtils.enumToMessages(CombinedTargetType),
  pieConfig: {
    color: ['#67D7FF', '#FFB925', '#F5222D'],
    legend: [
      { value: 'FUNC', message: '功能测试' },
      { value: 'API', message: '接口测试' }
    ]
  }
}
```

#### 布局配置

```javascript
{
  layout: {
    cols: 2,    // 网格列数
    gap: 16     // 网格间距
  }
}
```

## 事件

| 事件名 | 说明 | 参数 |
|--------|------|------|
| `selectDate` | 选择日期类型时触发 | `type: DateRangeType` |
| `dateChange` | 自定义日期范围改变时触发 | `dates: string[] \| undefined` |

## 样式定制

组件使用 CSS Grid 布局，支持自定义：

- 网格列数（`cols`）
- 网格间距（`gap`）
- 图表最小高度（默认 200px）

## 注意事项

1. **数据加载**：组件挂载时自动加载数据
2. **响应式更新**：监听相关属性变化自动刷新数据
3. **错误处理**：数据加载失败时在控制台输出错误信息
4. **性能优化**：使用异步组件和计算属性优化渲染性能