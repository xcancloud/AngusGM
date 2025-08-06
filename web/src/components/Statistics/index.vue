<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { enumUtils, UserSource, Gender, HttpMethod, ApiType, EventType, ProcessStatus } from '@xcan-angus/infra';
import {
  GroupSource, OrgTargetType, NoticeScope, SentType, MessageReceiveType,
  MessageStatus, EventPushStatus, ServiceSource
} from '@/enums/enums';
import { useI18n } from 'vue-i18n';
import { DateType, PieSetting } from './PropsType';

// Async component for chart loading
const LoadChart = defineAsyncComponent(() => import('./LoadChart.vue'));

/**
 * Component props interface definition
 * Defines the structure for statistics component configuration
 */
interface Props {
  router: string; // API gateway URL
  resource: string; // Resource type for statistics
  barTitle: string; // Title for bar chart
  dateType: DateType; // Date range type
  visible?: boolean; // Visibility control for statistics panel
  userId?: string; // Optional user ID for filtering
}

const props = withDefaults(defineProps<Props>(), {
  resource: '',
  barTitle: '',
  dateType: 'MONTH',
  visible: true,
  userId: ''
});

const { t } = useI18n();

// Color palette configuration for charts
const COLOR = [
  '45,142,255', // Blue
  '82,196,26', // Green
  '255,165,43', // Yellow
  '245,34,45', // Red
  '103,215,255', // Light Blue
  '201,119,255', // Purple
  '255,102,0', // Orange
  '217, 217, 217', // Gray
  '251, 129, 255', // Pink
  '171, 211, 255' // Light Blue 1
] as const;

// User statistics configuration - defines pie chart settings for user data
const userGroup = ref<PieSetting[]>([
  {
    key: 'source', // TODO 删除来源饼图
    value: t('source'),
    type: [],
    color: [COLOR[0], COLOR[1], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[1]]
  },
  {
    key: 'sys_admin',
    value: t('user.profile.identity'),
    type: [{ value: 0, message: t('user.profile.generalUser') }, { value: 1, message: t('user.profile.systemAdmin') }],
    color: [COLOR[0], COLOR[6]]
  },
  {
    key: 'enabled',
    value: t('common.status.validStatus'),
    type: [{ value: 0, message: t('common.status.disabled') }, { value: 1, message: t('common.status.enabled') }],
    color: [COLOR[3], COLOR[1], COLOR[5], COLOR[6]]
  },
  {
    key: 'locked',
    value: t('common.status.lockStatus'),
    type: [{ value: 0, message: t('common.status.unlocked') }, { value: 1, message: t('common.status.locked') }],
    color: [COLOR[1], COLOR[3]]
  },
  {
    key: 'gender',
    value: t('user.profile.gender'),
    type: [],
    color: [COLOR[5], COLOR[6], COLOR[3]]
  }
]);

// Group statistics configuration
const groupByGroup = ref<PieSetting[]>([
  {
    key: 'enabled',
    value: t('common.status.validStatus'),
    type: [{ value: 0, message: t('common.status.disabled') }, { value: 1, message: t('common.status.enabled') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'source',
    value: t('common.labels.source'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3]]
  }
]);

// Department statistics configuration
const deptByGroup = ref<PieSetting[]>([
  {
    key: 'level',
    value: t('department.level'),
    type: [
      { value: 1, message: t('department.level1') },
      { value: 2, message: t('department.level2') },
      { value: 3, message: t('department.level3') },
      { value: 4, message: t('department.level4') },
      { value: 5, message: t('department.level5') },
      { value: 6, message: t('department.level6') },
      { value: 7, message: t('department.level7') },
      { value: 8, message: t('department.level8') },
      { value: 9, message: t('department.level9') },
      { value: 10, message: t('department.level10') }
    ],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[4], COLOR[5], COLOR[6], COLOR[3]]
  }
]);

// Organization tag statistics configuration
const orgTagGroup = ref<PieSetting[]>([
  {
    key: 'target_type',
    value: t('common.labels.association'),
    type: [],
    color: [COLOR[1], COLOR[4], COLOR[5]]
  }
]);

// Notice statistics configuration
const noticeGroup = ref<PieSetting[]>([
  {
    key: 'scope',
    value: t('statistics.metrics.sendScope'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'send_type',
    value: t('statistics.metrics.sendType'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  }
]);

// Message statistics configuration
const messageGroup = ref<PieSetting[]>([
  {
    key: 'receive_type',
    value: t('statistics.metrics.receiveType'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'status',
    value: t('statistics.metrics.sendStatus'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  }
]);

// Service statistics configuration
const serviceGroup = ref<PieSetting[]>([
  {
    key: 'source',
    value: t('source'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'enabled',
    value: t('status'),
    type: [{ value: 0, message: t('disable') }, { value: 1, message: t('enable') }],
    color: [COLOR[3], COLOR[1], COLOR[5], COLOR[6]]
  }
]);

// API statistics configuration
const apiGroup = ref<PieSetting[]>([ // TODO 
  {
    key: 'method',
    value: t('方法'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'type',
    value: t('type'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'enabled',
    value: t('status'),
    type: [{ value: 0, message: t('disable') }, { value: 1, message: t('enable') }],
    color: [COLOR[3], COLOR[1], COLOR[5], COLOR[6]]
  }
]);

// HTTP status code ranges for API logs
const statusCode = [
  { value: 100, message: '1xx' }, // Informational status codes
  { value: 200, message: '2xx' }, // Success
  { value: 300, message: '3xx' }, // Redirection
  { value: 400, message: '4xx' }, // Client errors
  { value: 500, message: '5xx' } // Server errors
];

// Request logs statistics configuration // TODO 未展示默认数量0
const requestLogsGroup = ref<PieSetting[]>([
  {
    key: 'api_type', // TODO 删除类型饼图
    value: t('statistics.metrics.apiType'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'method',
    value: t('statistics.metrics.requestMethod'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'status',
    value: t('statistics.metrics.httpStatus'),
    type: statusCode,
    color: [COLOR[4], COLOR[1], COLOR[0], COLOR[3], COLOR[2]]
  },
  {
    key: 'success',
    value: t('statistics.status.name'),
    type: [{ value: 1, message: t('statistics.status.success') }, { value: 0, message: t('statistics.status.failed') }],
    color: [COLOR[1], COLOR[3]]
  }
]);

// Operation logs statistics configuration
const operationLogsGroup = ref<PieSetting[]>([]);

// System events statistics configuration
const eventGroup = ref<PieSetting[]>([
  {
    key: 'type',
    value: t('statistics.metrics.eventType'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[1], COLOR[3]]
  },
  {
    key: 'push_status',
    value: t('statistics.metrics.eventPushStatus'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[1], COLOR[3]]
  }
]);

// SMS records statistics configuration
const smsRecordGroup = ref<PieSetting[]>([
  {
    key: 'send_status',
    value: t('statistics.metrics.sendStatus'),
    type: [],
    color: []
  },
  {
    key: 'urgent',
    value: t('statistics.metrics.isUrgent'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'verification_code',
    value: t('statistics.metrics.isVCode'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'batch',
    value: t('statistics.metrics.isBatchSend'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  }
]);

// Email records statistics configuration
const emailRecordGroup = ref<PieSetting[]>([
  {
    key: 'send_status',
    value: t('statistics.metrics.sendStatus'),
    type: [],
    color: []
  },
  {
    key: 'urgent',
    value: t('statistics.metrics.isUrgent'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'verification_code',
    value: t('statistics.metrics.isVCode'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'batch',
    value: t('statistics.metrics.isBatchSend'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  }
]);

// Flag to control pie chart visibility
const hasPieChart = ref(false);

/**
 * Load statistics-related enums based on resource type
 * Initializes pie chart configurations for different resource types
 */
const loadEnums = async (): Promise<void> => {
  try {
    switch (props.resource) {
      case 'User':
        loadUserSource();
        loadUserGender();
        hasPieChart.value = true;
        break;
      case 'Dept':
        hasPieChart.value = true;
        break;
      case 'Group':
        loadGroupSource();
        hasPieChart.value = true;
        break;
      case 'OrgTagTarget':
        loadOrgTargetType();
        hasPieChart.value = true;
        break;
      case 'Notice':
        loadNoticeScope();
        getNoticeSentType();
        hasPieChart.value = true;
        break;
      case 'Message':
        loadMessageReceiveType();
        loadMessageStatus();
        hasPieChart.value = true;
        break;
      case 'Service':
        loadServiceSource();
        hasPieChart.value = true;
        break;
      case 'Api':
        loadApiHttpMethod();
        loadApiType();
        hasPieChart.value = true;
        break;
      case 'ApiLogs':
        loadApiType();
        loadApiHttpMethod();
        hasPieChart.value = true;
        break;
      case 'OperationLog':
        hasPieChart.value = true;
        break;
      case 'Event':
        loadEventType();
        loadEventPushStatus();
        hasPieChart.value = true;
        break;
      case 'Sms':
        loadSendStatusType();
        hasPieChart.value = true;
        break;
      case 'Email':
        loadSendStatusType();
        hasPieChart.value = true;
        break;
    }
  } catch (error) {
    console.error('Failed to load enum data:', error);
  }
};

/**
 * Load user source enum data
 * Maps enum values to user group configuration
 */
const loadUserSource = (): void => {
  try {
    userGroup.value[0].type = enumUtils.enumToMessages(UserSource);
  } catch (error) {
    console.error('Failed to load user source:', error);
  }
};

/**
 * Load user gender enum data
 * Maps enum values and assigns colors based on gender
 */
const loadUserGender = (): void => {
  try {
    const data = enumUtils.enumToMessages(Gender);
    userGroup.value[4].type = data;
    userGroup.value[4].color = data.map(item => getUserGenderColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load user gender:', error);
  }
};

/**
 * Get color for user gender
 * @param value - Gender enum value
 * @returns Color string for the gender
 */
const getUserGenderColor = (value: string): string => {
  switch (value) {
    case Gender.MALE:
      return COLOR[0];
    case Gender.FEMALE:
      return COLOR[2];
    case Gender.UNKNOWN:
      return COLOR[1];
    default:
      return COLOR[1];
  }
};

/**
 * Load group source enum data
 */
const loadGroupSource = (): void => {
  try {
    groupByGroup.value[1].type = enumUtils.enumToMessages(GroupSource); // TODO 英文不生效
  } catch (error) {
    console.error('Failed to load group source:', error);
  }
};

/**
 * Load organization target type enum data
 */
const loadOrgTargetType = (): void => {
  try {
    orgTagGroup.value[0].type = enumUtils.enumToMessages(OrgTargetType); // TODO 英文不生效
    console.log(orgTagGroup.value);
  } catch (error) {
    console.error('Failed to load organization target type:', error);
  }
};

/**
 * Load notice scope enum data
 * Maps enum values and assigns colors based on scope
 */
const loadNoticeScope = (): void => {
  try {
    const data = enumUtils.enumToMessages(NoticeScope);
    noticeGroup.value[0].type = data;
    noticeGroup.value[0].color = data.map(item => getNoticeScopeColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load notice scope:', error);
  }
};

/**
 * Get color for notice scope
 * @param value - Scope enum value
 * @returns Color string for the scope
 */
const getNoticeScopeColor = (value: string): string => {
  switch (value) {
    case NoticeScope.GLOBAL:
      return COLOR[0];
    case NoticeScope.APP:
      return COLOR[1];
    default:
      return COLOR[0];
  }
};

/**
 * Load notice sent type enum data
 */
const getNoticeSentType = (): void => {
  try {
    const data = enumUtils.enumToMessages(SentType);
    noticeGroup.value[1].type = data;
    noticeGroup.value[1].color = data.map(item => getNoticeSentTypeColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load notice sent type:', error);
  }
};

/**
 * Get color for notice sent type
 * @param value - Sent type enum value
 * @returns Color string for the sent type
 */
const getNoticeSentTypeColor = (value: string): string => {
  switch (value) {
    case SentType.SEND_NOW:
      return COLOR[2];
    case SentType.TIMING_SEND:
      return COLOR[4];
    default:
      return COLOR[2];
  }
};

/**
 * Load message receive type enum data
 */
const loadMessageReceiveType = (): void => {
  try {
    const data = enumUtils.enumToMessages(MessageReceiveType);
    messageGroup.value[0].type = data;
    messageGroup.value[0].color = data.map(item => getMessageReceiveTypeColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load message receive type:', error);
  }
};

/**
 * Get color for message receive type
 * @param value - Receive type enum value
 * @returns Color string for the receive type
 */
const getMessageReceiveTypeColor = (value: string): string => {
  switch (value) {
    case MessageReceiveType.SITE:
      return COLOR[4];
    case MessageReceiveType.EMAIL:
      return COLOR[1];
    default:
      return COLOR[4];
  }
};

/**
 * Load message status enum data
 */
const loadMessageStatus = (): void => {
  try {
    const data = enumUtils.enumToMessages(MessageStatus);
    messageGroup.value[1].type = data;
    messageGroup.value[1].color = data.map(item => getMessageStatusColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load message status:', error);
  }
};

/**
 * Get color for message status
 * @param value - Status enum value
 * @returns Color string for the status
 */
const getMessageStatusColor = (value: string): string => {
  switch (value) {
    case MessageStatus.PENDING:
      return COLOR[2];
    case MessageStatus.FAILURE:
      return COLOR[3];
    case MessageStatus.SENT:
      return COLOR[1];
    default:
      return COLOR[2];
  }
};

/**
 * Load service source enum data
 */
const loadServiceSource = (): void => {
  try {
    const data = enumUtils.enumToMessages(ServiceSource);
    serviceGroup.value[0].type = data;
    serviceGroup.value[0].color = data.map(item => getServiceSourceColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load service source:', error);
  }
};

/**
 * Get color for service source
 * @param value - Source enum value
 * @returns Color string for the source
 */
const getServiceSourceColor = (value: string): string => {
  switch (value) {
    case ServiceSource.BACK_ADD:
      return COLOR[2];
    case ServiceSource.EUREKA:
      return COLOR[3];
    default:
      return COLOR[2];
  }
};

/**
 * Load API HTTP method enum data
 * Handles both API and API logs configurations
 */
const loadApiHttpMethod = (): void => {
  try {
    const data = enumUtils.enumToMessages(HttpMethod);
    if (props.resource === 'ApiLogs') {
      requestLogsGroup.value[1].type = data;
      requestLogsGroup.value[1].color = data.map(item => getApiHttpMethodColor(item.value) || '');
      return;
    }
    apiGroup.value[0].type = data;
    apiGroup.value[0].color = data.map(item => getApiHttpMethodColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load API HTTP method:', error);
  }
};

/**
 * Get color for API HTTP method
 * @param value - HTTP method enum value
 * @returns Color string for the method
 */
const getApiHttpMethodColor = (value: string): string => {
  switch (value) {
    case HttpMethod.GET:
      return COLOR[0];
    case HttpMethod.HEAD:
      return COLOR[1];
    case HttpMethod.POST:
      return COLOR[2];
    case HttpMethod.PUT:
      return COLOR[5];
    case HttpMethod.PATCH:
      return COLOR[4];
    case HttpMethod.DELETE:
      return COLOR[3];
    case HttpMethod.TRACE:
      return COLOR[6];
    case HttpMethod.OPTIONS:
      return COLOR[7];
    default:
      return COLOR[0];
  }
};

/**
 * Load API type enum data
 * Handles both API and API logs configurations
 */
const loadApiType = (): void => {
  try {
    const data = enumUtils.enumToMessages(ApiType);

    if (props.resource === 'ApiLogs') {
      requestLogsGroup.value[0].type = data;
      requestLogsGroup.value[0].color = data.map(item => getApiTypeColor(item.value) || '');
      return;
    }
    apiGroup.value[1].type = data;
    apiGroup.value[1].color = data.map(item => getApiTypeColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load API type:', error);
  }
};

/**
 * Get color for API type
 * @param value - API type enum value
 * @returns Color string for the type
 */
const getApiTypeColor = (value: string): string => {
  switch (value) {
    case ApiType.API:
      return COLOR[0];
    case ApiType.OPEN_API:
      return COLOR[1];
    case ApiType.OPEN_API_2P:
      return COLOR[2];
    case ApiType.DOOR_API:
      return COLOR[4];
    case ApiType.PUB_API:
      return COLOR[5];
    case ApiType.VIEW:
      return COLOR[6];
    case ApiType.PUB_VIEW:
      return COLOR[7];
    default:
      return COLOR[0];
  }
};

/**
 * Load event type enum data
 */
const loadEventType = (): void => {
  try {
    const data = enumUtils.enumToMessages(EventType);
    eventGroup.value[0].type = data;
    eventGroup.value[0].color = data.map(item => getEventTypeColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load event type:', error);
  }
};

/**
 * Get color for event type
 * @param value - Event type enum value
 * @returns Color string for the type
 */
const getEventTypeColor = (value: string): string => {
  switch (value) {
    case EventType.BUSINESS:
      return COLOR[0];
    case EventType.SECURITY:
      return COLOR[1];
    case EventType.QUOTA:
      return COLOR[2];
    case EventType.SYSTEM:
      return COLOR[3];
    case EventType.OPERATION:
      return COLOR[4];
    case EventType.PROTOCOL:
      return COLOR[5];
    case EventType.API:
      return COLOR[6];
    case EventType.NOTICE:
      return COLOR[8];
    case EventType.OTHER:
      return COLOR[9];
    default:
      return COLOR[0];
  }
};

/**
 * Load event push status enum data
 */
const loadEventPushStatus = (): void => {
  try {
    const data = enumUtils.enumToMessages(EventPushStatus);
    eventGroup.value[1].type = data;
    eventGroup.value[1].color = data.map(item => getEventPushStatusColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load event push status:', error);
  }
};

/**
 * Get color for event push status
 * @param value - Push status enum value
 * @returns Color string for the status
 */
const getEventPushStatusColor = (value: string): string => {
  switch (value) {
    case EventPushStatus.PENDING:
      return COLOR[7];
    case EventPushStatus.PUSHING:
      return COLOR[0];
    case EventPushStatus.PUSH_SUCCESS:
      return COLOR[1];
    case EventPushStatus.PUSH_FAIL:
      return COLOR[3];
    case EventPushStatus.IGNORED:
      return COLOR[2];
    default:
      return COLOR[7];
  }
};

/**
 * Load send status enum data for email and SMS
 */
const loadSendStatusType = (): void => {
  try {
    const data = enumUtils.enumToMessages(ProcessStatus);
    if (props.resource === 'Email') {
      emailRecordGroup.value[0].type = data;
      emailRecordGroup.value[0].color = data.map(item => getSendStatusColor(item.value) || '');
      return;
    }
    smsRecordGroup.value[0].type = data;
    smsRecordGroup.value[0].color = data.map(item => getSendStatusColor(item.value) || '');
  } catch (error) {
    console.error('Failed to load send status:', error);
  }
};

/**
 * Get color for send status
 * @param value - Send status enum value
 * @returns Color string for the status
 */
const getSendStatusColor = (value: string): string => {
  switch (value) {
    case ProcessStatus.SUCCESS:
      return COLOR[1];
    case ProcessStatus.PENDING:
      return COLOR[2];
    case ProcessStatus.FAILURE:
      return COLOR[3];
    default:
      return COLOR[1];
  }
};

/**
 * Computed pie chart parameters based on resource type
 * Returns appropriate configuration for different resource types
 */
const pieSetting = computed(() => {
  switch (props.resource) {
    case 'User': // User
      return userGroup.value;
    case 'Group':
      return groupByGroup.value;
    case 'OrgTagTarget':
      return orgTagGroup.value;
    case 'Dept':
      return deptByGroup.value;
    case 'Notice':
      return noticeGroup.value;
    case 'Message':
      return messageGroup.value;
    case 'Service':
      return serviceGroup.value;
    case 'Api':
      return apiGroup.value;
    case 'ApiLogs':
      return requestLogsGroup.value;
    case 'OperationLog':
      return operationLogsGroup.value;
    case 'Event':
      return eventGroup.value;
    case 'Sms':
      return smsRecordGroup.value;
    case 'Email':
      return emailRecordGroup.value;
    default:
      return [];
  }
});

// Lifecycle hook - load enums on component mount
onMounted(() => {
  loadEnums();
});
</script>

<template>
  <LoadChart
    :router="router"
    :resource="resource"
    :pieSetting="pieSetting"
    :barTitle="barTitle"
    :dateType="dateType"
    :hasPieChart="hasPieChart"
    :visible="visible"
    :userId="userId" />
</template>
