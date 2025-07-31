<script setup lang="ts">
import { ref, computed, defineAsyncComponent, onMounted } from 'vue';
import { enumLoader } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { PieSetting, DateType } from './PropsType';

const LoadChart = defineAsyncComponent(() => import('./LoadChart.vue'));

interface Props {
  geteway: string;
  resource: string;
  barTitle: string;
  dateType: DateType;
  visible?: boolean;
  userId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  resource: '',
  barTitle: '',
  dateType: 'MONTH',
  visible: true,
  userId: ''
});

const { t } = useI18n();

// 日志-操作日志配置
const operationLogsGroup = ref<PieSetting[]>([
  {
    key: 'resource',
    value: t('资源'),
    type: [],
    color: []
  }
]);

const hasPieChart = ref(true);
// 日志操作类型
const loadOperationLogType = async () => {
  const [error, data] = await enumLoader.load('OperationResourceType');
  if (error) {
    return;
  }
  operationLogsGroup.value[0].type = data;
  operationLogsGroup.value[0].color = data.map(item => getLogResourceTypeColor(item.value));
};

const getLogResourceTypeColor = (value: string) => {
  switch (value) {
    case 'TENANT':
      return 'rgba(166, 206, 255, 1)';
    case 'DEPT':
      return 'rgba(127, 145, 255, 1)';
    case 'GROUP':
      return 'rgba(217, 217, 217, 1)';
    case 'USER':
      return 'rgb(4,142,229)';
    case 'USER_DIRECTORY':
      return 'rgba(162, 222, 236, 1)';
    case 'USER_TOKEN':
      return 'rgba(45, 142, 255, 1)';
    case 'ORG_TAG':
      return 'rgba(118, 196, 125, 1)';
    case 'AUTH_CLIENT':
      return 'rgb(11,118,21)';
    case 'AUTH_USER':
      return 'rgba(255, 102, 0, 1)';
    case 'SERVICE':
      return 'rgba(255, 165, 43, 1)';
    case 'API':
      return 'rgba(178, 88, 131, 1)';
    case 'APP':
      return 'rgba(169, 104, 55, 0.7)';
    case 'APP_FUNC':
      return 'rgba(255, 185, 37, 1)';
    case 'WEB_TAG':
      return 'rgba(201, 119, 255, 1)';
    case 'POLICY':
      return 'rgba(165, 1135, 106, 1)';
    case 'POLICY_APP':
      return 'rgba(134, 97, 151, 1)';
    case 'POLICY_FUNC':
      return 'rgba(134, 97, 151, 1)';
    case 'POLICY_TENANT':
      return 'rgba(180, 128, 48, 1)';
    case 'POLICY_DEPT':
      return 'rgba(251, 129, 255, 1)';
    case 'POLICY_GROUP':
      return 'rgba(227, 220, 155, 1)';
    case 'POLICY_USER':
      return 'rgba(191, 199, 255, 1)';
    case 'EVENT':
      return 'rgba(103, 215, 255, 1)';
    case 'EVENT_CHANNEL':
      return 'rgb(58, 155, 190, 1)';
    case 'EVENT_TEMPLATE':
      return 'rgba(228, 112, 57, 1)';
    case 'EMAIL':
      return 'rgba(245, 34, 45, 1)';
    case 'EMAIL_SERVER':
      return 'rgba(127, 145, 255, 1)';
    case 'EMAIL_TEMPLATE':
      return 'rgba(57, 129, 184, 1)';
    case 'SMS':
      return 'rgba(166, 206, 255, 0.5)';
    case 'SMS_CHANNEL':
      return 'rgba(127, 145, 255, 0.5)';
    case 'SMS_TEMPLATE':
      return 'rgba(217, 217, 217, 0.5)';
    case 'TO_ROLE':
      return 'rgba(117, 118, 148, 0.5)';
    case 'TO_USER':
      return 'rgba(162, 222, 236, 0.5)';
    case 'MESSAGE':
      return 'rgba(45, 142, 255, 0.5)';
    case 'NOTICE':
      return 'rgba(118, 196, 125, 0.5)';
    case 'SETTING':
      return 'rgb(11,118,21, 0.5)';
    case 'SETTING_USER':
      return 'rgba(255, 102, 0, 0.5)';
    case 'SETTING_TENANT':
      return 'rgba(255, 165, 43, 0.5)';
    case 'SETTING_TENANT_QUOTA':
      return 'rgba(178, 88, 131, 0.5)';
    case 'SYSTEM_TOKEN':
      return 'rgba(255, 185, 37, 0.5)';
    case 'OTHER':
      return 'rgba(201, 119, 255, 0.5)';
  }
};

const pieParmas = computed(() => {
  switch (props.resource) {
    case 'OperationLog':
      return operationLogsGroup.value;
    default:
      return [];
  }
});

onMounted(() => {
  loadOperationLogType();
});

</script>
<template>
  <LoadChart
    :visible="props.visible"
    :geteway="props.geteway"
    :resource="props.resource"
    :barTitle="props.barTitle"
    :pieParmas="pieParmas"
    :hasPieChart="hasPieChart"
    :userId="props.userId"
    :dateType="props.dateType">
    <template #default>
      <slot></slot>
    </template>
  </LoadChart>
</template>
