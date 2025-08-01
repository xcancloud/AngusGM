<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { enumLoader } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { DateType, PieSetting } from './PropsType';

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
// ---------------蓝---------绿-----------黄------------红---------------浅蓝-------------紫----------橘黄---------灰色-------------粉色-------------浅蓝1
const COLOR = ['45,142,255', '82,196,26', '255,165,43', '245,34,45', '103,215,255', '201,119,255', '255,102,0', '217, 217, 217', '251, 129, 255', '171, 211, 255'];

/**
 * 饼图统计图配置说明：
 * key: 对应数据库表的列,请求接口的groupByColumns参数,返回的统计数据和顺序有关,可参照后台接口返回（开启loadCountGroup()可查看具体返回结果）
 * value: 每一列统计的类型名称,前端配置，对应统计图的title
 * type: 分两种 一种是统计结果有多种类型,对应具体的enum接口;另一种只有0 和 1（0和1前端配置）
 * color:rgba格式，配置rgb即可,颜色数组的长度对应type的长度，缺少会报错
 * 顺序决定统计的结果的顺序
 */

// 用户统计配置
const userGroup = ref<PieSetting[]>([
  {
    key: 'source',
    value: t('source'),
    type: [],
    color: [COLOR[0], COLOR[1], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[1]]
  },
  {
    key: 'sys_admin',
    value: t('oprole'),
    type: [{ value: 0, message: t('generalUsers') }, { value: 1, message: t('administrators') }],
    color: [COLOR[0], COLOR[6]]
  },
  {
    key: 'enabled',
    value: t('validStatus'),
    type: [{ value: 0, message: t('disable') }, { value: 1, message: t('enable') }],
    color: [COLOR[3], COLOR[1], COLOR[5], COLOR[6]]
  },
  {
    key: 'locked',
    value: t('lockStatus'),
    type: [{ value: 0, message: t('unlocked') }, { value: 1, message: t('lockout') }],
    color: [COLOR[1], COLOR[3]]
  },
  { key: 'gender', value: t('gender'), type: [], color: [COLOR[5], COLOR[6], COLOR[3]] }
]);

// 组统计配置
const groupByGroup = ref<PieSetting[]>([
  {
    key: 'enabled',
    value: t('status'),
    type: [{ value: 0, message: t('disable') }, { value: 1, message: t('enable') }],
    color: [COLOR[3], COLOR[1]]
  },
  { key: 'source', value: t('source'), type: [], color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3]] }
]);

// 部门统计配置
const deptByGroup = ref<PieSetting[]>([
  {
    key: 'level',
    value: t('level'),
    type: [
      { value: 1, message: t('grade1') },
      { value: 2, message: t('grade2') },
      { value: 3, message: t('grade3') },
      { value: 4, message: t('grade4') },
      { value: 5, message: t('grade5') },
      { value: 6, message: t('grade6') },
      { value: 7, message: t('grade7') },
      { value: 8, message: t('grade8') },
      { value: 9, message: t('grade9') },
      { value: 10, message: t('grade10') }
    ],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[4], COLOR[5], COLOR[6], COLOR[3]]
  }
]);

// 租户标签统计配置
const orgTagGroup = ref<PieSetting[]>([
  { key: 'target_type', value: t('related'), type: [], color: [COLOR[1], COLOR[4], COLOR[5]] }
]);

// 公告统计配置
const noticeGroup = ref<PieSetting[]>([
  {
    key: 'scope',
    value: t('发送范围'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'send_type',
    value: t('发送类型'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  }
]);

// 消息统计配置
const messageGroup = ref<PieSetting[]>([
  {
    key: 'receive_type',
    value: t('status'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'status',
    value: t('status'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  }
]);

// 服务统计配置
const serviceGroup = ref<PieSetting[]>([
  {
    key: 'source',
    value: t('source'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  // { key: 'api_doc_type', value: t('ApiDoc类型'), type: [], color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]] },
  {
    key: 'enabled',
    value: t('status'),
    type: [{ value: 0, message: t('disable') }, { value: 1, message: t('enable') }],
    color: [COLOR[3], COLOR[1], COLOR[5], COLOR[6]]
  }
]);

// 接口统计配置
const apiGroup = ref<PieSetting[]>([
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

// http状态码范围
const statusCode = [
  {
    value: 100, message: '1xx' // 信息性状态码
  },
  {
    value: 200, message: '2xx' // 成功
  },
  {
    value: 300, message: '3xx' // 重定向
  },
  {
    value: 400, message: '4xx' // 客户端错误
  },
  {
    value: 500, message: '5xx' // 服务器错误
  }
];
// 日志-请求日志配置
const requestLogsGroup = ref<PieSetting[]>([
  {
    key: 'api_type',
    value: t('接口类型'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  {
    key: 'method',
    value: t('请求方式'),
    type: [],
    color: [COLOR[0], COLOR[2], COLOR[4], COLOR[5], COLOR[6], COLOR[3], COLOR[1]]
  },
  { key: 'status', value: t('状态码'), type: statusCode, color: [COLOR[4], COLOR[1], COLOR[0], COLOR[3], COLOR[2]] },
  {
    key: 'success',
    value: t('是否成功'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[1], COLOR[3]]
  }
]);

// 日志-操作日志配置
const operationLogsGroup = ref<PieSetting[]>([
  // {
  //   key: 'success',
  //   value: t('是否成功'),
  //   type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
  //   color: [COLOR[1], COLOR[3]]
  // }
]);

// 系统事件-事件模板统计
const eventGroup = ref<PieSetting[]>([
  {
    key: 'type',
    value: t('事件类型'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[1], COLOR[3]]
  },
  {
    key: 'push_status',
    value: t('推送状态'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[1], COLOR[3]]
  }
]);

// 系统-短信发送记录
const smsRecordGroup = ref<PieSetting[]>([
  { key: 'send_status', value: t('发送状态'), type: [], color: [] },
  {
    key: 'urgent',
    value: t('是否加急'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'verification_code',
    value: t('是否验证码'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'batch',
    value: t('批量发送'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  }
]);

// 系统-邮件发送记录
const emailRecordGroup = ref<PieSetting[]>([
  { key: 'send_status', value: t('发送状态'), type: [], color: [] },
  {
    key: 'urgent',
    value: t('是否加急'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'verification_code',
    value: t('是否验证码'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  },
  {
    key: 'batch',
    value: t('批量发送'),
    type: [{ value: 1, message: t('yes') }, { value: 0, message: t('no') }],
    color: [COLOR[3], COLOR[1]]
  }
]);

const hasPieChart = ref(false);
// 获取饼图统计相关枚举
const loadEnums = async () => {
  switch (props.resource) {
    case 'User':
      await loadUserSource();
      await loadUserGender();
      hasPieChart.value = true;
      break;
    case 'Dept':
      hasPieChart.value = true;
      break;
    case 'Group':
      await loadGroupSource();
      hasPieChart.value = true;
      break;
    case 'OrgTagTarget':
      await loadOrgTargetType();
      hasPieChart.value = true;
      break;
    case 'Notice':
      await loadNoticeScope();
      await getNoticeSentType();
      hasPieChart.value = true;
      break;
    case 'Message':
      await loadMessageReceiveType();
      await loadMessageStatus();
      hasPieChart.value = true;
      break;
    case 'Service':
      await loadServiceSource();
      // await loadApiDocType();
      hasPieChart.value = true;
      break;
    case 'Api':
      await loadApiHttpMethod();
      await loadApiType();
      hasPieChart.value = true;
      break;
    case 'ApiLogs':
      await loadApiType();
      await loadApiHttpMethod();
      hasPieChart.value = true;
      break;
    case 'OperationLog':
      hasPieChart.value = true;
      break;
    case 'Event':
      await loadEventType();
      await loadEventPushStatus();
      hasPieChart.value = true;
      break;
    case 'Sms':
      await loadSendStatusType();
      hasPieChart.value = true;
      break;
    case 'Email':
      await loadSendStatusType();
      hasPieChart.value = true;
      break;
  }
};

// 用户来源
const loadUserSource = async () => {
  const [error, data] = await enumLoader.load('UserSource');
  if (error) {
    return;
  }
  userGroup.value[0].type = data;
  // userGroup.value[0].color = data.map(item => getUserPieChartColor(item.value));
};

// // 配置固定颜色
// const getUserPieChartColor = (value:string) => {
//   switch (value) {
//     case 'PLATFORM_SIGNUP': // 平台注册
//       return COLOR[0];
//     case 'INVITATION_CODE_SIGNUP': // 邀请码注册
//       return COLOR[1];
//     case 'BACKGROUND_SIGNUP': // 后台注册
//       return COLOR[2];
//     case 'BACKGROUND_ADDED': // 后台添加
//       return COLOR[3];
//     case 'THIRD_PARTY_LOGIN': // 三方登录
//       return COLOR[4];
//     case 'LDAP_SYNCHRONIZE': // Ldap同步
//       return COLOR[5];
//   }
// };
// 用户性别
const loadUserGender = async () => {
  const [error, data] = await enumLoader.load('Gender');
  if (error) {
    return;
  }
  userGroup.value[4].type = data;
  userGroup.value[4].color = data.map(item => getUserGenderColor(item.value));
};

// 配置固定颜色
const getUserGenderColor = (value: string) => {
  switch (value) {
    case 'MALE': // 男
      return COLOR[0];
    case 'FEMALE': // 女
      return COLOR[2];
    case 'UNKNOWN': // 未知
      return COLOR[1];
  }
};

// 组来源
const loadGroupSource = async () => {
  const [error, data] = await enumLoader.load('GroupSource');
  if (error) {
    return;
  }
  groupByGroup.value[1].type = data;
};

// 租户下标签类型
const loadOrgTargetType = async () => {
  const [error, data] = await enumLoader.load('OrgTargetType');
  if (error) {
    return;
  }
  orgTagGroup.value[0].type = data;
};

// 公告发送范围
const loadNoticeScope = async () => {
  const [error, data] = await enumLoader.load('NoticeScope');
  if (error) {
    return;
  }
  noticeGroup.value[0].type = data;
  noticeGroup.value[0].color = data.map(item => getNoticeScopeColor(item.value));
};
const getNoticeScopeColor = (value: string) => {
  switch (value) {
    case 'GLOBAL': // 全局
      return COLOR[0];
    case 'APP': // 应用
      return COLOR[1];
  }
};
//  公告发送类型
const getNoticeSentType = async () => {
  const [error, data] = await enumLoader.load('SentType');
  if (error) {
    return;
  }
  noticeGroup.value[1].type = data;
  noticeGroup.value[1].color = data.map(item => getNoticeSentTypeColor(item.value));
};

const getNoticeSentTypeColor = (value: string) => {
  switch (value) {
    case 'SEND_NOW': // 立即发送
      return COLOR[2];
    case 'TIMING_SEND': // 定时发送
      return COLOR[4];
  }
};

// 消息类型
const loadMessageReceiveType = async () => {
  const [error, data] = await enumLoader.load('MessageReceiveType');
  if (error) {
    return;
  }
  messageGroup.value[0].type = data;
  messageGroup.value[0].color = data.map(item => getMessageReceiveTypeColor(item.value));
};

const getMessageReceiveTypeColor = (value: string) => {
  switch (value) {
    case 'SITE': // 站内
      return COLOR[4];
    case 'EMAIL': // 邮件
      return COLOR[1];
  }
};

// 消息状态
const loadMessageStatus = async () => {
  const [error, data] = await enumLoader.load('MessageStatus');
  if (error) {
    return;
  }
  messageGroup.value[1].type = data;
  messageGroup.value[1].color = data.map(item => getMessageStatusColor(item.value));
};

const getMessageStatusColor = (value: string) => {
  switch (value) {
    case 'PENDING': // 待发送
      return COLOR[2];
    case 'FAILURE': // 失败
      return COLOR[3];
    case 'SENT': // 已发送
      return COLOR[1];
  }
};

// 服务来源
const loadServiceSource = async () => {
  const [error, data] = await enumLoader.load('ServiceSource');
  if (error) {
    return;
  }
  serviceGroup.value[0].type = data;
  serviceGroup.value[0].color = data.map(item => getServiceSourceColor(item.value));
};

const getServiceSourceColor = (value: string) => {
  switch (value) {
    case 'BACK_ADD': // 后台添加
      return COLOR[2];
    case 'EUREKA': // EUREKA
      return COLOR[3];
    case 'NACOS': // NACOS
      return COLOR[1];
    case 'CONSUL': // CONSUL
      return COLOR[4];
  }
};

// 接口方法
const loadApiHttpMethod = async () => {
  const [error, data] = await enumLoader.load('HttpMethod');
  if (error) {
    return;
  }

  if (props.resource === 'ApiLogs') {
    requestLogsGroup.value[1].type = data;
    requestLogsGroup.value[1].color = data.map(item => getApiHttpMethodColor(item.value));
    return;
  }

  apiGroup.value[0].type = data;
  apiGroup.value[0].color = data.map(item => getApiHttpMethodColor(item.value));
};

const getApiHttpMethodColor = (value: string) => {
  switch (value) {
    case 'GET': // GET
      return COLOR[0];
    case 'HEAD': // HEAD
      return COLOR[1];
    case 'POST': // POST
      return COLOR[2];
    case 'PUT': // PUT
      return COLOR[5];
    case 'PATCH': // PATCH
      return COLOR[4];
    case 'DELETE': // DELETE
      return COLOR[3];
    case 'TRACE': // TRACE
      return COLOR[6];
    case 'OPTIONS': // OPTIONS
      return COLOR[7];
  }
};
// 接口类型
const loadApiType = async () => {
  const [error, data] = await enumLoader.load('ApiType');
  if (error) {
    return;
  }

  if (props.resource === 'ApiLogs') {
    requestLogsGroup.value[0].type = data;
    requestLogsGroup.value[0].color = data.map(item => getApiTypeColor(item.value));
    return;
  }

  apiGroup.value[1].type = data;
  apiGroup.value[1].color = data.map(item => getApiTypeColor(item.value));
};

const getApiTypeColor = (value: string) => {
  switch (value) {
    case 'API':
      return COLOR[0];
    case 'OPEN_API':
      return COLOR[1];
    case 'OPEN_API_2P':
      return COLOR[2];
    case 'DOOR_API':
      return COLOR[4];
    case 'PUB_API':
      return COLOR[5];
    case 'VIEW':
      return COLOR[6];
    case 'PUB_VIEW':
      return COLOR[7];
  }
};

// 事件类型
const loadEventType = async () => {
  const [error, data] = await enumLoader.load('EventType');
  if (error) {
    return;
  }
  eventGroup.value[0].type = data;
  eventGroup.value[0].color = data.map(item => getEventTypeColor(item.value));
};

const getEventTypeColor = (value: string) => {
  switch (value) {
    case 'BUSINESS': // 业务
      return COLOR[0];
    case 'SECURITY': // 安全
      return COLOR[1];
    case 'QUOTA': // 配额
      return COLOR[2];
    case 'SYSTEM': // 系统
      return COLOR[3];
    case 'OPERATION': // 操作
      return COLOR[4];
    case 'PROTOCOL': // 协议
      return COLOR[5];
    case 'API': // 接口
      return COLOR[6];
    case 'NOTICE': // 通知
      return COLOR[8];
    case 'OTHER': // 其他
      return COLOR[9];
  }
};

// 事件推送状态
const loadEventPushStatus = async () => {
  const [error, data] = await enumLoader.load('EventPushStatus');
  if (error) {
    return;
  }
  eventGroup.value[1].type = data;
  eventGroup.value[1].color = data.map(item => getEventPushStatusColor(item.value));
};

const getEventPushStatusColor = (value: string) => {
  switch (value) {
    case 'PENDING': // 未推送
      return COLOR[7];
    case 'PUSHING': // 推送中
      return COLOR[0];
    case 'PUSH_SUCCESS': // 推送成功
      return COLOR[1];
    case 'PUSH_FAIL': // 推送失败
      return COLOR[3];
    case 'IGNORED':
      return COLOR[2];
  }
};

// 邮件和短信发送状态
const loadSendStatusType = async () => {
  const [error, data] = await enumLoader.load('ProcessStatus');
  if (error) {
    return;
  }

  if (props.resource === 'Email') {
    emailRecordGroup.value[0].type = data;
    emailRecordGroup.value[0].color = data.map(item => getSendStatusColor(item.value));
    return;
  }
  smsRecordGroup.value[0].type = data;
  smsRecordGroup.value[0].color = data.map(item => getSendStatusColor(item.value));
};

const getSendStatusColor = (value: string) => {
  switch (value) {
    case 'SUCCESS': // 成功
      return COLOR[1];
    case 'PENDING': // 待处理
      return COLOR[2];
    case 'FAILURE': // 失败
      return COLOR[3];
  }
};

const pieParmas = computed(() => {
  switch (props.resource) {
    case 'User': // 用户
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

onMounted(() => {
  loadEnums();
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
