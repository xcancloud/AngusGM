<script setup lang="ts">
import { ref } from 'vue';
import { DateType } from './PropsType';
import { RadioButton, RadioGroup } from 'ant-design-vue';
import { DatePicker } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

/**
 * Component props interface definition
 * Defines the structure for date parameter component configuration
 */
interface Props {
  datePicker: [string, string] | undefined; // Date range picker value
  resource: string; // Resource type for filtering
  dateType: DateType; // Default date type
}

const props = withDefaults(defineProps<Props>(), {
  datePicker: undefined,
  resource: '',
  dateType: 'MONTH'
});

/**
 * Component event definitions
 * Defines the events that can be emitted by this component
 */
const emit = defineEmits<{
  (e: 'selectDate', type: DateType): void; // Emitted when radio button selection changes
  (e: 'dateChange', type: [string, string] | undefined): void; // Emitted when date picker changes
}>();

const { t } = useI18n();

/**
 * Handle date picker changes
 * Processes date range selection and emits appropriate events
 * @param value - Selected date range from picker
 */
const dateChange = (value: [string, string] | undefined): void => {
  if (value) {
    // If date range is selected, set to month view and emit date change
    dataType.value = 'MONTH' as DateType;
    emit('dateChange', value);
    return;
  }

  // Handle empty date picker based on resource type
  if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
    dataType.value = 'DAY' as DateType;
    emit('selectDate', 'DAY');
    return;
  }

  dataType.value = 'YEAR' as DateType;
  emit('selectDate', 'YEAR');
};

/**
 * Handle radio button group changes
 * Emits date type selection event
 * @param e - Radio group change event
 */
const radioGroupChange = (e: { target: { value: DateType } }): void => {
  emit('selectDate', e.target.value);
};

// Current date type for radio button selection
const dataType = ref<DateType>(props.dateType);
</script>

<template>
  <div class="flex items-center space-x-5 text-3 leading-3 ml-5">
    <!-- Date type radio buttons -->
    <RadioGroup
      v-model:value="dataType"
      buttonStyle="solid"
      size="small"
      class="whitespace-nowrap"
      @change="radioGroupChange">
      <RadioButton value="DAY">{{ t('statistics.timeRanges.today') }}</RadioButton>
      <RadioButton value="WEEK">{{ t('statistics.timeRanges.last7Days') }}</RadioButton>
      <RadioButton value="MONTH">{{ t('statistics.timeRanges.last30Days') }}</RadioButton>
      <!-- Show year option only for non-log resources -->
      <template v-if="!['ApiLogs', 'OperationLog'].includes(props.resource)">
        <RadioButton value="YEAR">{{ t('statistics.timeRanges.lastYear') }}</RadioButton>
      </template>
    </RadioGroup>
    <!-- Date range picker -->
    <DatePicker
      :value="props.datePicker"
      type="date-range"
      size="small"
      class="w-52"
      @change="dateChange" />
  </div>
</template>

<style scoped>
:deep(.ant-radio-group-small .ant-radio-button-wrapper span) {
  font-size: 12px;
}
</style>
