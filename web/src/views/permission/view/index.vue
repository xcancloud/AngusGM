<script setup lang='ts'>
import { reactive, computed, defineAsyncComponent, ref, watch, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { Tabs, TabPane } from 'ant-design-vue';
import { modal, notification, PureCard, Table, Select, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';

import { auth } from '@/api';
import { GM } from '@xcan-angus/tools';

const TargetPanel = defineAsyncComponent(() => import('./components/targetPanel.vue'));
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

interface PolicyRecordType {
  id: string,
  policyId: string,
  name: string,
  fullName: string,
  createdDate: string,
  description: string,
}

const _columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '16%'
  },
  {
    title: 'permissionsCheck.columns.name',
    dataIndex: 'name',
    width: '14%'
  },
  {
    title: '编码',
    dataIndex: 'code'
  },
  {
    title: '授权来源',
    dataIndex: 'orgType',
    width: '13%'
  },
  {
    title: 'permissionsCheck.columns.fullName',
    dataIndex: 'authByName',
    width: '13%'
  },
  {
    title: '授权时间',
    dataIndex: 'authDate',
    width: '15%'
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];

const { t } = useI18n();
const tenantInfo = inject('tenantInfo', ref());

const state = reactive<{
  tab: 'USER' | 'DEPT' | 'GROUP',
  targetId: string | undefined,
  loading: boolean,
  dataSource: PolicyRecordType[]
}>({
  tab: 'USER',
  targetId: undefined, // 当前选中的授权目标ID
  loading: false, // 策略表格数据加载状态
  dataSource: [] // 策略表格数据
});

const userRef = ref();
const deptRef = ref();
const groupRef = ref();

const addPolicyConfig = {
  USER: auth.addUserPolicy,
  DEPT: auth.addPolicyByDept,
  GROUP: auth.addGroupPolicy
};

const appId = ref();

// 分页参数
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10
});

const load = async () => {
  if (!state.targetId || !appId.value) {
    return;
  }

  const params: {
    pageNo: number;
    pageSize: number;
    appId: string;
  } = {
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    appId: appId.value
  };

  let res: [Error | null, any];
  state.loading = true;
  if (state.tab === 'USER') {
    res = await auth.getUserPolicy(state.targetId, params);
  } else if (state.tab === 'DEPT') {
    res = await auth.getDeptPolicy(state.targetId, params);
  } else {
    res = await auth.getGroupPolicy(state.targetId, params);
  }

  const [error, { data }] = res;
  state.loading = false;
  if (error) {
    return;
  }

  state.dataSource = data.list;
  pagination.total = +data.total;
};

const listChange = (page: { current: number, pageSize: number }) => {
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  load();
};

watch(() => state.tab, () => {
  state.targetId = undefined;
  state.dataSource = [];
  if (state.tab === 'USER') {
    setTimeout(() => {
      userRef.value && userRef.value.activeFirstItem();
    });
  }
  if (state.tab === 'DEPT') {
    setTimeout(() => {
      deptRef.value && deptRef.value.activeFirstItem();
    });
  }
  if (state.tab === 'GROUP') {
    setTimeout(() => {
      groupRef.value && groupRef.value.activeFirstItem();
    });
  }
});

// 授权目标发生变化
const targetChange = (targetId: string, type: string) => {
  if (type === state.tab) {
    state.targetId = targetId;
    load();
  }
};

// 删除策略下的对象
const delTargetPolicy = (item: PolicyRecordType) => {
  const typeName = state.tab === 'USER' ? t('permissionsCheck.user') : state.tab === 'GROUP' ? t('permissionsCheck.dept') : t('permissionsCheck.group');
  modal.confirm({
    centered: true,
    title: t('confirmTitle'),
    content: t('permissionsCheck.delConfirm', { name: typeName }),
    async onOk () {
      let res: [Error | null, any];
      if (state.tab === 'USER') {
        res = await auth.deleteUserPolicy(state.targetId as string, [item.id]);
      } else if (state.tab === 'DEPT') {
        res = await auth.deletePolicyByDept(state.targetId as string, [item.id]);
      } else {
        res = await auth.deleteGroupPolicy(state.targetId as string, [item.id]);
      }
      const [error] = res;
      if (error) {
        return;
      }

      notification.success(t('permissionsCheck.delSuccess'));
      if (pagination.current > 1 && state.dataSource.length === 1) {
        pagination.current = pagination.current - 1;
      }
      load();
    }
  });
};

const changeAppId = (value) => {
  appId.value = value;
  state.targetId = undefined;
  state.dataSource = [];
};

const columnUser = _columns.map(item => {
  return {
    ...item,
    title: t(item.title)
  };
});

const addVisible = ref(false);
const getType = computed(() => {
  return state.tab === 'USER' ? 'User' : state.tab === 'DEPT' ? 'Dept' : 'Group';
});

const openAddPolicy = () => {
  addVisible.value = true;
};

const addPolicy = async (policyIds: string[]) => {
  const [error] = await addPolicyConfig[state.tab](state.targetId as string, policyIds);
  if (error) {
    return;
  }
  addVisible.value = false;
  load();
};

const getTenantTypeName = (record) => {
  const result: string[] = [];
  if (record?.currentDefault) {
    result.push('应用默认授权');
  }
  if (record?.openAuth) {
    result.push('开通授权');
  }
  return result.join(',');
};

const deptColumns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('permissionsCheck.columns.name'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    title: t('编码'),
    dataIndex: 'code'
  },
  {
    title: t('permissionsCheck.columns.fullName'),
    dataIndex: 'authByName',
    width: '15%'
  },
  {
    title: t('授权时间'),
    dataIndex: 'authDate',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('操作'),
    dataIndex: 'action',
    width: 82,
    align: 'center',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];
</script>
<template>
  <div class="flex h-full">
    <PureCard class="w-80 p-0 flex flex-col mr-2">
      <div class="m-2">
        <Select
          showSearch
          :action="`${GM}/appopen/list?tenantId=${tenantInfo.tenantId}&clientId=xcan_tp`"
          :placeholder="t('permissionsStrategy.add.appPlaceholder')"
          :lazy="false"
          :fieldNames="{label: 'appName', value: 'appId'}"
          defaultActiveFirstOption
          internal
          size="small"
          class="w-full"
          @change="changeAppId" />
      </div>
      <Tabs
        v-model:activeKey="state.tab"
        size="small"
        class="flex-1 flex flex-col">
        <TabPane
          key="USER"
          :tab="t('permissionsCheck.tab1')">
          <target-panel
            ref="userRef"
            v-model:selectedTargetId="state.targetId"
            :appId="appId"
            type="USER"
            @change="targetChange($event, 'USER')">
          </target-panel>
        </TabPane>
        <TabPane key="DEPT" :tab="t('permissionsCheck.tab2')">
          <target-panel
            ref="deptRef"
            v-model:selectedTargetId="state.targetId"
            :appId="appId"
            type="DEPT"
            @change="targetChange($event, 'DEPT')">
          </target-panel>
        </TabPane>
        <TabPane key="GROUP" :tab="t('permissionsCheck.tab3')">
          <target-panel
            ref="groupRef"
            v-model:selectedTargetId="state.targetId"
            :appId="appId"
            type="GROUP"
            @change="targetChange($event, 'GROUP')">
          </target-panel>
        </TabPane>
      </Tabs>
    </PureCard>
    <PureCard class="flex-1 overflow-auto p-3.5">
      <div class="mb-2 flex items-center justify-end">
        <ButtonAuth
          code="PoliciesAdd"
          type="primary"
          icon="icon-tianjia"
          :disabled="!state.targetId "
          @click="openAddPolicy" />
        <IconRefresh
          class="text-3.5 ml-2"
          :loading="state.loading"
          @click="load" />
      </div>
      <Table
        v-if="state.tab === 'USER'"
        :dataSource="state.dataSource"
        :rowKey="(record:any) => record.id"
        :loading="state.loading"
        :columns="columnUser"
        :pagination="pagination"
        size="small"
        @change="listChange">
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              class="text-theme-special"
              :to="`/permissions/policy/${record.id}`"
              :title="record.description">
              {{ record.name }}
            </RouterLink>
          </template>
          <template v-if="column.dataIndex === 'orgType'">
            <span
              v-if="['USER','DEPT','GROUP'].includes(text?.value)"
              :title="`${ text?.message }}(${ record.orgName }`">
              {{ text?.message }}({{ record.orgName }})
            </span>
            <span v-else :title="getTenantTypeName(record)">
              {{ getTenantTypeName(record) }}
            </span>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <ButtonAuth
              code="PoliciesCancel"
              type="text"
              icon="icon-quxiao"
              :disabled="!['USER','DEPT','GROUP'].includes(record.orgType.value)"
              @click="delTargetPolicy(record)" />
          </template>
        </template>
      </Table>
      <Table
        v-else
        storageKey="dept"
        :dataSource="state.dataSource"
        :rowKey="(record:any) => record.id"
        :loading="state.loading"
        :columns="deptColumns"
        :pagination="pagination"
        size="small"
        @change="listChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              class="text-theme-special"
              :to="`/permissions/policy/${record.id}`"
              :title="record.description">
              {{ record.name }}
            </RouterLink>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <ButtonAuth
              code="PoliciesCancel"
              type="text"
              icon="icon-quxiao"
              @click="delTargetPolicy(record)" />
          </template>
        </template>
      </Table>
      <PolicyModal
        v-model:visible="addVisible"
        :appId="appId"
        :type="getType"
        :userId="state.targetId"
        :deptId="state.targetId"
        :groupId="state.targetId"
        @change="addPolicy" />
    </PureCard>
  </div>
</template>
<style scoped>
:deep(.ant-tabs-nav-wrap) {
  @apply px-10 text-3 leading-3 ;

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

:deep(.ant-tabs-top .ant-tabs-content) {
  @apply h-full;
}

:deep(.ant-tabs-top .ant-tabs-content .ant-tabs-tabpane) {
  @apply flex flex-col;
}

</style>
