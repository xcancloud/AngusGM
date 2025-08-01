<script setup lang="ts">
import { ref } from 'vue';
import { DateType } from './PropsType';
import { RadioButton, RadioGroup } from 'ant-design-vue';
import { DatePicker } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

interface Props {
  datePicker: string[] | undefined;
  resource: string;
  dateType: string;
}

const props = withDefaults(defineProps<Props>(), {
  datePicker: undefined,
  resource: '',
  dateType: 'MONTH'
});

const emit = defineEmits<{
  (e: 'selectDate', type: DateType): void,
  (e: 'dateChange', type: string[] | undefined): void
}>();

const { t } = useI18n();
const dateChange = (value: string[] | undefined) => {
  if (value) {
    dataType.value = '';
    emit('dateChange', value);
    return;
  }

  if (['ApiLogs', 'OperationLog'].includes(props.resource)) {
    dataType.value = 'DAY';
    emit('selectDate', 'DAY');
    return;
  }

  dataType.value = 'YEAR';
  emit('selectDate', 'YEAR');
};

const radioGroupChange = (e) => {
  emit('selectDate', e.target.value);
};

// 默认展示的数据 近一年 近一月 近七天 今日
const dataType = ref(props.dateType);

</script>
<template>
  <div class="flex items-center space-x-5 text-3 leading-3 ml-5">
    <RadioGroup
      v-model:value="dataType"
      buttonStyle="solid"
      size="small"
      class="whitespace-nowrap"
      @change="radioGroupChange">
      <RadioButton value="DAY">{{ t('today') }}</RadioButton>
      <RadioButton value="WEEK">{{ t('nearly7Days') }}</RadioButton>
      <RadioButton value="MONTH">{{ t('近1月') }}</RadioButton>
      <template v-if="!['ApiLogs', 'OperationLog'].includes(props.resource)">
        <RadioButton value="YEAR">{{ t('nearlyaYear') }}</RadioButton>
      </template>
    </RadioGroup>
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
