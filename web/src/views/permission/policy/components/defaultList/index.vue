<script setup lang='ts'>
import { onMounted, reactive, h } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, modal, notification, Select, Table } from '@xcan-angus/vue-ui';
import { Radio, RadioGroup } from 'ant-design-vue';
import { app } from '@xcan-angus/infra';

import { auth } from '@/api';
import { PolicyDefaultRecordType, PolicyDataRecordType } from '../PropsType';

const { t } = useI18n();

// Table column definitions
const columns = [
  {
    title: t('permission.policy.default.columns.appName'),
    dataIndex: 'appName',
    width: 274
  },
  {
    title: t('permission.policy.default.columns.polices'),
    dataIndex: 'polices'
  }
];

// Reactive state for the component
const state = reactive<{
  dataSource: PolicyDataRecordType[]
}>({
  dataSource: []
});

/**
 * Load default policy data for all applications
 */
const load = async () => {
  const [error, res] = await auth.getTenantDefaultPolicy();
  if (error) {
    return;
  }

  const handlerData = res.data || [];
  handlerData.forEach((item: PolicyDataRecordType) => {
    const checkedItem = item.defaultPolicies.find(v => v.currentDefault);
    if (checkedItem) {
      item.policyId = checkedItem.id;
    } else {
      item.policyId = undefined;
    }
  });
  state.dataSource = handlerData;
};

/**
 * Update and save default policy for an application
 * @param appId - Application ID
 * @param policyId - Policy ID to set as default
 * @param policyName - Policy name for display
 */
const updateSave = async (appId: string, policyId: string, policyName: string) => {
  if (!policyId) {
    // Disable current default policy
    modal.confirm({
      title: t('common.messages.confirmTitle'),
      content: t('permission.policy.default.disableTip', { policyName }),
      async onOk () {
        const [error] = await auth.deleteAppDefaultPolicy(appId);
        if (error) {
          return;
        }
        notification.success(t('common.messages.updateSuccess'));
        await load();
      }
    });
  } else {
    // Set new default policy
    modal.confirm({
      title: t('common.messages.confirmTitle'),
      content: t('permission.policy.default.authTip', { policyName }),
      async onOk () {
        const [error] = await auth.patchAppDefaultPolicy(appId, policyId);
        if (error) {
          return;
        }
        notification.success(t('common.messages.editSuccess'));
        await load();
      }
    });
  }
};

/**
 * Handle radio button change for predefined policies
 * @param e - Radio change event
 * @param appId - Application ID
 * @param options - Available policy options
 */
const radioChange = (e: { target: { value: string } }, appId: string, options: PolicyDefaultRecordType[]) => {
  const { value } = e.target;
  const policyName = value ? options.find(policy => policy.id === value)?.name : options.find(policy => policy.currentDefault)?.name;
  if (policyName) {
    updateSave(appId, value, policyName);
  }
};

/**
 * Handle select change for user-defined policies
 * @param value - Selected policy ID
 * @param appId - Application ID
 * @param options - Available policy options
 */
const selectChange = (value: string, appId: string, options: PolicyDefaultRecordType[]) => {
  const policyName = value ? options.find(policy => policy.id === value)?.name : options.find(policy => policy.currentDefault)?.name;
  if (policyName) {
    updateSave(appId, value, policyName);
  }
};

/**
 * Get user-defined policies from the record
 * @param record - Data record
 * @returns Filtered user-defined policies
 */
const getPolices = (record: PolicyDataRecordType): PolicyDefaultRecordType[] => {
  return record.defaultPolicies.filter((v: PolicyDefaultRecordType) => v.type.value === 'USER_DEFINED');
};

/**
 * Get the currently selected user-defined policy ID
 * @param record - Data record
 * @returns Selected policy ID or undefined
 */
const getUserDefinePolicyId = (record: PolicyDataRecordType): string | undefined => {
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
        <span class="text-theme-title">{{ t('permission.policy.default.title') }}</span>
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
              {{ t('permission.policy.default.forbidAccess') }}
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
            {{ t('permission.policy.default.other') }}
          </Radio>
          <Select
            :options="getPolices(record)"
            :value="getUserDefinePolicyId(record)"
            :fieldNames="{label:'name',value:'id'}"
            :disabled="!app.has('DefaultAppPolicySetting')"
            :placeholder="t('permission.policy.default.placeholder')"
            class="w-70"
            size="small"
            @change="selectChange($event, record.appId, record.defaultPolicies)" />
        </template>
      </template>
    </Table>
  </Card>
</template>
