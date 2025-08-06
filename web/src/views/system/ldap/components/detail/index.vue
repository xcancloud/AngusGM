<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, notification } from '@xcan-angus/vue-ui';
import { TabPane, Tabs } from 'ant-design-vue';

// 基本信息组件
import ServerInfo from '@/views/system/ldap/components/serverInfo/index.vue';
// LDAP 基本模式设置组件
import LdapConfig from '@/views/system/ldap/components/ldapConfig/index.vue';
// 用户模式设置组件
import UserConfig from '@/views/system/ldap/components/userConfig/index.vue';
// 组模式设置组件
import GroupConfig from '@/views/system/ldap/components/groupConfig/index.vue';
// 成员模式设置组件
import MemberConfig from '@/views/system/ldap/components/memberConfig/index.vue';
// 按钮组件
import SubmitButton from '@/views/system/ldap/components/submitButton/index.vue';

import { useI18n } from 'vue-i18n';
import { userDirectory } from '@/api';

const { t } = useI18n();

const router = useRouter();

const route = useRoute();
const query = route.query;

const activeKey = ref(0);

const groupMemberMode = ref();
const groupMode = ref();
const mode = ref();
const server = ref();
const userMode = ref();

const childrenData: any = reactive({
  server: null,
  groupSchema: null,
  membershipSchema: null,
  schema: null,
  userSchema: null
});

const errorStop = ref(true);

const saveLoading = ref(false);
const testLoading = ref(false);

const submitType = ref('save');

// 编辑回显的内容
const editShow: any = ref({});

// 验证页面状态
const isAdd = query.f === 'add';
const isTest = query.f === 'test';

// 表单验证次数
const rulesCount = ref(5);

// 按钮 ref
const submitRef = ref();

const loadInfo = async () => {
// 回显
  if (!isAdd) {
    const [error, res] = await userDirectory.getDirectoryDetail(query.q as string);
    if (error) {
      return;
    }

    editShow.value = {
      ...res.data,
      server: {
        ...res.data.server,
        directoryType: res.data.server.directoryType.value
      }, // passwordEncoderType
      userSchema: {
        ...res.data.userSchema,
        passwordEncoderType: res.data.userSchema?.passwordEncoderType?.value
      }
    };
    // 测试自动点击测试按钮
    if (isTest) {
      nextTick(() => {
        submitRef.value.testClick();
      });
    }
  }
};

// 公共提交
const publicConfig = function () {
  server.value && server.value.childRules();
  mode.value && mode.value.childRules();
  userMode.value && userMode.value.childRules();
  groupMode.value && groupMode.value.childRules();
  groupMemberMode.value && groupMemberMode.value.childRules();
  // 终止器重置
  errorStop.value = true;
};

// 测试配置事件
const testConfig = function () {
  submitType.value = 'test';
  publicConfig();
  rulesCount.value = 5;
};

// 保存配置
const saveConfig = function () {
  submitType.value = 'save';
  publicConfig();
  rulesCount.value = 5;
};

// 验证完的回调（emit）
const childRules = function (type: string, label: string, index: number, form: any) {
  rulesCount.value--;
  if (type === 'error' && errorStop.value) {
    // 终止后边的执行
    errorStop.value = false;
    // 定位表单报错的页面
    activeKey.value = index;
    // 数据清除
    childrenData[label] = null;
    return;
  }
  if (type === 'success') {
    // 表单内容赋值
    childrenData[label] = form;
  }
};

// 保存提交
const saveSubmit = async function (val: { [x: string]: null; }) {
  saveLoading.value = true;
  const submitApi = isAdd ? userDirectory.addDirectory : userDirectory.updateDirectory;
  if (!isAdd) {
    childrenData.id = query.q;
  }
  const [error] = await submitApi({ ...childrenData, sequence: editShow.value.sequence });
  saveLoading.value = false;
  Object.keys(val).forEach(key => {
    val[key] = null;
  });
  if (error) {
    return;
  }
  notification.success(`ldap${isAdd ? t('systemLdap.detail-1') : t('systemLdap.detail-2')}${t('systemLdap.detail-3')}`);
  router.push(decodeURIComponent(query.r as string));
};

// tab 选择事件
function tabChange (index: number) {
  activeKey.value = index;
}

// 监听内容集合, 都有内容说明表单验证都通过, 则触发提交
watch(() => rulesCount.value, (val) => {
  if (val === 0) {
    const isSubmit = Object.keys(childrenData).every(key => childrenData[key] !== null);
    if (isSubmit) {
      // 不传不需要的组key groupSchema,membershipSchema 为非必传，如果传入则又必填字段校验
      Object.keys(childrenData).forEach(key => {
        if (['groupSchema', 'membershipSchema'].includes(key)) {
          const isNull = Object.keys(childrenData[key]).every(ckey => childrenData[key][ckey] === '' || ckey === 'ignoreSameNameGroup');
          if (isNull) {
            delete childrenData[key];
          }
        }
      });
      // 根据点击的按钮决定提交的方法
      saveSubmit(childrenData);
    }
  }
}, { deep: true });

onMounted(() => {
  loadInfo();
});
</script>
<template>
  <Card bodyClass="p-0" class="min-h-full">
    <Tabs
      v-model:activeKey="activeKey"
      :centered="true"
      size="small"
      @change="tabChange">
      <TabPane :key="0" :tab="t('systemLdap.detail-5')" />
      <TabPane :key="1" :tab="t('systemLdap.detail-6')" />
      <TabPane :key="2" :tab="t('systemLdap.detail-7')" />
      <TabPane :key="3" :tab="t('systemLdap.detail-8')" />
      <TabPane :key="4" :tab="t('systemLdap.detail-9')" />
    </Tabs>
    <div class="flex flex-col items-center w-full text-3 leading-3">
      <ServerInfo
        v-show="activeKey===0"
        ref="server"
        keys="server"
        :query="editShow.server"
        :index="0"
        class="mt-7"
        @rules="childRules" />
      <LdapConfig
        v-show="activeKey===1"
        ref="mode"
        keys="schema"
        :query="editShow.schema"
        :index="1"
        class="mt-7"
        @rules="childRules" />
      <UserConfig
        v-show="activeKey===2"
        ref="userMode"
        keys="userSchema"
        class="mt-7"
        :query="editShow.userSchema"
        :index="2"
        @rules="childRules" />
      <GroupConfig
        v-show="activeKey===3"
        ref="groupMode"
        keys="groupSchema"
        class="mt-7"
        :query="editShow.groupSchema"
        :index="3"
        @rules="childRules" />
      <MemberConfig
        v-show="activeKey===4"
        ref="groupMemberMode"
        keys="membershipSchema"
        class="mt-7"
        :query="editShow.membershipSchema"
        :index="4"
        @rules="childRules" />
      <submit-button
        ref="submitRef"
        :saveLoading="saveLoading"
        :testLoading="testLoading"
        @testConfig="testConfig"
        @saveConfig="saveConfig" />
    </div>
  </Card>
</template>

<style scoped>
:deep(.ant-tabs-nav-wrap) {
  @apply px-50 text-3 leading-3 ;

  height: 45px;
}

:deep(.ant-tabs-nav) {
  height: 46px;
}

:deep(.ant-tabs-nav-list) {
  @apply flex justify-between flex-1;
}

:deep(.ant-tabs-top > .ant-tabs-nav) {
  margin: 0;
}

</style>
