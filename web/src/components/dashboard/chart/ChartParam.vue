<script setup lang="ts">
import { ref } from 'vue';
import { RadioButton, RadioGroup } from 'ant-design-vue';
import { DatePicker } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { DateRangeType } from '@/components/dashboard/enums';

interface Props {
  datePicker: string[] | undefined;
  resource:string;
  dateType: DateRangeType;
}
const props = withDefaults(defineProps<Props>(), {
  datePicker: undefined,
  resource: '',
  dateType: DateRangeType.MONTH
});

const emit = defineEmits<{(e: 'selectDate', type:DateRangeType): void,
  (e: 'dateChange', type:string[] | undefined): void}>();

const { t } = useI18n();

const customDateChange = (value:string[] | undefined) => {
  dataType.value = '' as DateRangeType; // Clear the selected date type on the interface
  emit('dateChange', value);
};

const dateTypeChange = (e) => {
  dataType.value = e.target.value;
  // Clear the selected time in DatePicker
  emit('dateChange', undefined);
  emit('selectDate', e.target.value);
};

const dataType = ref(props.dateType);

</script>
<template>
  <div class="flex items-center text-3 leading-3 ml-5 space-x-5">
    <RadioGroup
      v-model:value="dataType"
      buttonStyle="solid"
      size="small"
      class="whitespace-nowrap"
      @change="dateTypeChange">
      <RadioButton :value="DateRangeType.DAY">{{ t('chart.today') }}</RadioButton>
      <RadioButton :value="DateRangeType.WEEK">{{ t('chart.last7Days') }}</RadioButton>
      <RadioButton :value="DateRangeType.MONTH">{{ t('chart.lastMonth') }}</RadioButton>
      <RadioButton :value="DateRangeType.YEAR">{{ t('chart.lastYear') }}</RadioButton>
    </RadioGroup>
    <DatePicker
      :value="props.datePicker as [string, string] | undefined"
      type="date-range"
      size="small"
      class="w-52"
      @change="customDateChange" />
  </div>
</template>
<style scoped>
:deep(.ant-radio-group-small .ant-radio-button-wrapper span) {
  font-size: 12px;
}
</style>
