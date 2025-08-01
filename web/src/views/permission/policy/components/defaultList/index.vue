<script setup lang='ts'>
import { onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, modal, notification, Select, Table } from '@xcan-angus/vue-ui';
import { Radio, RadioGroup } from 'ant-design-vue';
import { app } from '@xcan-angus/infra';

import { auth } from '@/api';

const { t } = useI18n();

const columns = [
  {
    title: t('permissionsStrategy.default.columns.appName'),
    dataIndex: 'appName',
    width: 274
  },
  {
    title: t('permissionsStrategy.default.columns.polices'),
    dataIndex: 'polices'
  }
];

interface PolicyDefaultRecordType {
  id: string,
  name: string | undefined,
  code: string | undefined,
  policyId?: string,
  currentDefault: boolean,
  type: { value: 'PRE_DEFINED' | 'USER_DEFINED', message: string }
}

interface DataRecordType {
  appId: number | undefined,
  appName: string | undefined,
  policyId?: string,
  defaultPolicies: PolicyDefaultRecordType[]
}

const state = reactive<{
  dataSource: DataRecordType[]
}>({
  dataSource: []
});

const load = async () => {
  const [error, res] = await auth.getTenantDefaultPolicy();
  if (error) {
    return;
  }

  const handlerData = res.data || [];
  handlerData.forEach((item: DataRecordType) => {
    const checkedItem = item.defaultPolicies.find(v => v.currentDefault);
    if (checkedItem) {
      item.policyId = checkedItem.id;
    } else {
      item.policyId = undefined;
    }
  });
  state.dataSource = handlerData;
};

const updateSave = async (appId: string, policyId: string, policyName: string) => {
  if (!policyId) {
    modal.confirm({
      title: t('confirmTitle'),
      content: `禁用当前“应用默认授权”会导致系统中用户授权"${policyName}“策略对应权限失效，新用户将被禁止访问应用，是否继续确认？`,
      async onOk () {
        const [error] = await auth.deleteAppDefaultPolicy(appId);
        if (error) {
          return;
        }
        notification.success(t('permissionsStrategy.default.updateSuccess'));
        load();
      }
    });
  } else {
    modal.confirm({
      title: t('confirmTitle'),
      content: `修改后会授权“${policyName}”策略给所有用户，是否继续确认？`,
      async onOk () {
        const [error] = await auth.patchAppDefaultPolicy(appId, policyId);
        if (error) {
          return;
        }
        notification.success(t('permissionsStrategy.default.updateSuccess'));
        load();
      }
    });
  }
};

const radioChange = (e: { target: { value: string } }, appId: string, options) => {
  const { value } = e.target;
  const policyName = value ? options.find(policy => policy.id === value).name : options.find(policy => policy.currentDefault).name;
  updateSave(appId, value, policyName);
};

const selectChange = (value: string, appId: string, options) => {
  const policyName = value ? options.find(policy => policy.id === value).name : options.find(policy => policy.currentDefault).name;
  updateSave(appId, value, policyName);
};

const getPolices = (record) => {
  return record.defaultPolicies.filter((v: PolicyDefaultRecordType) => v.type.value === 'USER_DEFINED');
};

const getUserDefinePolicyId = (record) => {
  return getPolices(record).find(policy => policy.id === record.policyId) ? record.policyId : undefined;
};

onMounted(() => {
  load();
});

defineExpose({
  load
});
</script>

<template>
  <Card>
    <template #title>
      <div class="flex items-center">
        <span class="text-theme-title">{{ t('permissionsStrategy.default.title') }}</span>
        <!-- <span class="text-3 leading-3 text-theme-sub-content ml-3">{{ t('permissionsStrategy.default.tip') }}</span> -->
      </div>
    </template>
    <Table
      :dataSource="state.dataSource"
      :rowKey="(record:any) => record.appId"
      :columns="columns"
      :pagination="false"
      size="small">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'appName'">{{ record.appName }}</template>
        <template v-if="column.dataIndex === 'polices'">
          <RadioGroup
            :value="record.policyId"
            :disabled="!app.has('DefaultAppPolicySetting')"
            size="small"
            @change="radioChange($event, record.appId, record.defaultPolicies)">
            <Radio
              :value="undefined"
              size="small">
              禁止访问
            </Radio>
            <Radio
              v-for="(item, index) in record.defaultPolicies.filter((v: PolicyDefaultRecordType)=> v.type.value === 'PRE_DEFINED')"
              :key="index"
              :value="item.id"
              size="small">
              {{ item.name }}
            </Radio>
          </RadioGroup>
          <Radio
            class="ml-8"
            :disabled="!app.has('DefaultAppPolicySetting')"
            :checked="!!getUserDefinePolicyId(record)">
            其他
          </Radio>
          <Select
            :options="getPolices(record)"
            :value="getUserDefinePolicyId(record)"
            :fieldNames="{label:'name',value:'id'}"
            :disabled="!app.has('DefaultAppPolicySetting')"
            :placeholder="t('permissionsStrategy.default.placeholder')"
            class="w-70"
            size="small"
            @change="selectChange($event, record.appId, record.defaultPolicies)" />
        </template>
      </template>
    </Table>
  </Card>
</template>
