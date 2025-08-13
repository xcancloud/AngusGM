<script setup lang='ts'>
import { computed, defineAsyncComponent, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { TabPane, Tabs } from 'ant-design-vue';
import { ButtonAuth, IconRefresh, modal, notification, PureCard, Select, Table } from '@xcan-angus/vue-ui';
import { appContext, GM } from '@xcan-angus/infra';

import { auth } from '@/api';

/**
 * Async component imports for better performance
 * Lazy loading components to improve initial page load time
 */
const TargetPanel = defineAsyncComponent(() => import('./authObjects.vue'));
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

/**
 * Policy Record Type Definition
 *
 * Defines the structure for policy records displayed in the data table,
 * including identification, naming, and metadata information
 */
interface PolicyRecordType {
  id: string,
  policyId: string,
  name: string,
  fullName: string,
  createdDate: string,
  description: string,
}

const { t } = useI18n();

/**
 * Get Tenant Type Name for Display
 *
 * Constructs a human-readable string representing the tenant authorization type
 * by checking currentDefault and openAuth flags on the record
 */
const getTenantAuthTypeName = (record: any) => {
  const result: string[] = [];
  if (record?.currentDefault) {
    result.push(t('permission.view.tenant.defaultAuth'));
  }
  if (record?.openAuth) {
    result.push(t('permission.view.tenant.openAuth'));
  }
  return result.join(',');
};

/**
 * Component State Management
 *
 * Centralized reactive state for managing component behavior, data loading,
 * and user interactions across different entity types
 */
const state = reactive<{
  tab: 'USER' | 'DEPT' | 'GROUP',
  targetId: string | undefined,
  loading: boolean,
  dataSource: PolicyRecordType[]
}>({
  tab: 'USER', // Current active tab for entity type
  targetId: undefined, // Currently selected authorization target ID
  loading: false, // Loading state for policy table data
  dataSource: [] // Policy table data source
});

/**
 * Component references for target panels
 * Used to access methods on child components for programmatic control
 */
const userRef = ref();
const deptRef = ref();
const groupRef = ref();

/**
 * Policy Addition Configuration Mapping
 *
 * Maps entity types to their corresponding API functions for adding policies,
 * enabling dynamic policy assignment based on selected entity type
 */
const addPolicyConfig = {
  USER: auth.addUserPolicy,
  DEPT: auth.addPolicyByDept,
  GROUP: auth.addGroupPolicy
};

/**
 * Application ID for current permission context
 * Used to filter policies and targets by specific application
 */
const appId = ref();

/**
 * Pagination Configuration
 *
 * Manages table pagination state including total count, current page,
 * and page size for efficient data loading and display
 */
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10
});

/**
 * Load Policy Data
 *
 * Fetches policy data from the backend based on current entity type,
 * target ID, and pagination parameters. Handles different API calls
 * for users, departments, and groups.
 */
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

  try {
    // Route API call based on entity type
    if (state.tab === 'USER') {
      res = await auth.getUserPolicy(state.targetId, params);
    } else if (state.tab === 'DEPT') {
      res = await auth.getDeptPolicy(state.targetId, params);
    } else {
      res = await auth.getGroupPolicy(state.targetId, params);
    }

    const [error, { data }] = res;
    if (error) {
      return;
    }

    state.dataSource = data.list;
    pagination.total = +data.total;
  } finally {
    state.loading = false;
  }
};

/**
 * Handle Pagination Changes
 *
 * Updates pagination state and reloads data when user changes
 * page number or page size
 */
const listChange = (page: { current: number, pageSize: number }) => {
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  load();
};

/**
 * Watch Tab Changes
 *
 * Monitors tab switching to reset target selection and data,
 * then activates the first item in the new target panel
 */
watch(() => state.tab, () => {
  state.targetId = undefined;
  state.dataSource = [];

  // Activate first item in new target panel after tab change
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

/**
 * Handle Target Selection Change
 *
 * Processes target selection changes from child components
 * and triggers data loading when the target matches current tab
 */
const targetChange = (targetId: string, type: string) => {
  if (type === state.tab) {
    state.targetId = targetId;
    load();
  }
};

/**
 * Delete Target Policy
 *
 * Removes a policy from the selected target after user confirmation.
 * Handles different entity types and updates the UI accordingly.
 */
const deleteTargetPolicy = (item: PolicyRecordType) => {
  const typeName = state.tab === 'USER'
    ? t('permission.check.user')
    : state.tab === 'GROUP'
      ? t('permission.check.dept')
      : t('permission.check.group');

  modal.confirm({
    centered: true,
    title: t('common.messages.confirmTitle'),
    content: t('permission.check.deleteConfirm', { name: typeName }),
    async onOk () {
      let res: [Error | null, any];

      try {
        // Call appropriate delete API based on entity type
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

        notification.success(t('permission.check.deleteSuccess'));

        // Adjust pagination if current page becomes empty
        if (pagination.current > 1 && state.dataSource.length === 1) {
          pagination.current = pagination.current - 1;
        }

        load();
      } catch (error) {
        console.error('Failed to delete policy:', error);
      }
    }
  });
};

/**
 * Handle Application ID Change
 *
 * Resets target selection and data when switching applications,
 * ensuring clean state for new application context
 */
const changeAppId = (value: any) => {
  appId.value = value;
  state.targetId = undefined;
  state.dataSource = [];
};

/**
 * Modal visibility state for adding policies
 * Controls when the policy addition modal is displayed
 */
const addVisible = ref();

/**
 * Compute Entity Type for Policy Modal
 *
 * Generates the appropriate entity type string for the policy modal
 * based on the currently selected tab
 */
const getType = computed(() => {
  return state.tab === 'USER'
    ? 'User'
    : state.tab === 'DEPT'
      ? 'Dept'
      : 'Group';
});

/**
 * Open Add Policy Modal
 *
 * Shows the modal for adding new policies to the selected target
 */
const openAddPolicy = () => {
  addVisible.value = true;
};

/**
 * Add Policy to Target
 *
 * Calls the appropriate API to add selected policies to the target
 * and refreshes the data after successful operation
 */
const addPolicy = async (policyIds: string[]) => {
  try {
    const [error] = await addPolicyConfig[state.tab](state.targetId as string, policyIds);
    if (error) {
      return;
    }

    addVisible.value = false;
    load();
  } catch (error) {
    console.error('Failed to add policy:', error);
  }
};

/**
 * Column Configuration for User Policy Table
 *
 * Defines the structure and display properties for the main policy table,
 * including column widths, alignment, and internationalized titles
 */
const userPolicyColumns = [
  {
    key: 'id',
    title: t('permission.columns.assocPolicies.id'),
    dataIndex: 'id',
    width: '13%'
  },
  {
    key: 'name',
    title: t('permission.columns.assocPolicies.name'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'code',
    title: t('permission.columns.assocPolicies.code'),
    dataIndex: 'code'
  },
  {
    key: 'orgType',
    title: t('permission.columns.assocPolicies.source'),
    dataIndex: 'orgType',
    width: '20%'
  },
  {
    key: 'authByName',
    title: t('permission.columns.assocPolicies.authByName'),
    dataIndex: 'authByName',
    width: '12%'
  },
  {
    key: 'authDate',
    title: t('permission.columns.assocPolicies.authDate'),
    dataIndex: 'authDate',
    width: '12%'
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center' as const
  }
];

/**
 * Column Configuration for Department/Group Policy Table
 *
 * Defines the structure and display properties for department and group tables,
 * with optimized column widths and custom cell styling
 */
const deptOrGroupPolicyColumns = [
  {
    key: 'id',
    title: t('permission.columns.assocPolicies.id'),
    dataIndex: 'id',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'name',
    title: t('permission.columns.assocPolicies.name'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'code',
    title: t('permission.columns.assocPolicies.code'),
    dataIndex: 'code'
  },
  {
    key: 'authByName',
    title: t('permission.columns.assocPolicies.authByName'),
    dataIndex: 'authByName',
    width: '15%'
  },
  {
    key: 'authDate',
    title: t('permission.columns.assocPolicies.authDate'),
    dataIndex: 'authDate',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center' as const,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];
</script>

<template>
  <div class="flex h-full">
    <!-- Left Sidebar - Application Selection and Target Panels -->
    <PureCard class="w-80 p-0 flex flex-col mr-2">
      <!-- Application Selection -->
      <div class="m-2">
        <Select
          showSearch
          :action="`${GM}/appopen/list?tenantId=${appContext.getUser()?.tenantId}&clientId=xcan_tp`"
          :placeholder="t('permission.view.appPlaceholder')"
          :lazy="false"
          :fieldNames="{label: 'appName', value: 'appId'}"
          defaultActiveFirstOption
          internal
          size="small"
          class="w-full"
          @change="changeAppId" />
      </div>

      <!-- Entity Type Tabs -->
      <Tabs
        v-model:activeKey="state.tab"
        size="small"
        class="flex-1 flex flex-col">
        <TabPane
          key="USER"
          :tab="t('permission.check.user')">
          <target-panel
            ref="userRef"
            v-model:selectedTargetId="state.targetId"
            :appId="appId"
            type="USER"
            @change="targetChange($event, 'USER')">
          </target-panel>
        </TabPane>
        <TabPane key="DEPT" :tab="t('permission.check.dept')">
          <target-panel
            ref="deptRef"
            v-model:selectedTargetId="state.targetId"
            :appId="appId"
            type="DEPT"
            @change="targetChange($event, 'DEPT')">
          </target-panel>
        </TabPane>
        <TabPane key="GROUP" :tab="t('permission.check.group')">
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

    <!-- Right Content Area - Policy Table and Actions -->
    <PureCard class="flex-1 overflow-auto p-3.5">
      <!-- Action Bar -->
      <div class="mb-2 flex items-center justify-end">
        <ButtonAuth
          code="PoliciesAdd"
          type="primary"
          icon="icon-tianjia"
          :disabled="!state.targetId"
          @click="openAddPolicy" />
        <IconRefresh
          class="text-3.5 ml-2"
          :loading="state.loading"
          @click="load" />
      </div>

      <!-- User Policy Table -->
      <Table
        v-if="state.tab === 'USER'"
        :dataSource="state.dataSource"
        rowKey="id"
        :loading="state.loading"
        :columns="userPolicyColumns"
        :pagination="pagination"
        size="small"
        noDataSize="small"
        noDataText="No data"
        @change="listChange">
        <template #bodyCell="{ column, record, text }">
          <!-- Policy Name with Link -->
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              class="text-theme-special"
              :to="`/permissions/policy/${record.id}`"
              :title="record.description">
              {{ record.name }}
            </RouterLink>
          </template>

          <!-- Organization Type Display -->
          <template v-if="column.dataIndex === 'orgType'">
            <span
              v-if="['USER','DEPT','GROUP'].includes(text?.value)"
              :title="`${text?.message}(${record.orgName})`">
              {{ text?.message }}({{ record.orgName }})
            </span>
            <span v-else :title="getTenantAuthTypeName(record)">
              {{ getTenantAuthTypeName(record) }}
            </span>
          </template>

          <!-- Action Buttons -->
          <template v-if="column.dataIndex === 'action'">
            <ButtonAuth
              code="PoliciesCancel"
              type="text"
              icon="icon-quxiao"
              :disabled="!['USER','DEPT','GROUP'].includes(record.orgType.value)"
              @click="deleteTargetPolicy(record)" />
          </template>
        </template>
      </Table>

      <!-- Department/Group Policy Table -->
      <Table
        v-else
        storageKey="dept"
        :dataSource="state.dataSource"
        rowKey="id"
        :loading="state.loading"
        :columns="deptOrGroupPolicyColumns"
        :pagination="pagination"
        size="small"
        noDataSize="small"
        noDataText="No data"
        @change="listChange">
        <template #bodyCell="{ column, record }">
          <!-- Policy Name with Link -->
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              class="text-theme-special"
              :to="`/permissions/policy/${record.id}`"
              :title="record.description">
              {{ record.name }}
            </RouterLink>
          </template>

          <!-- Action Buttons -->
          <template v-if="column.dataIndex === 'action'">
            <ButtonAuth
              code="PoliciesCancel"
              type="text"
              icon="icon-quxiao"
              @click="deleteTargetPolicy(record)" />
          </template>
        </template>
      </Table>

      <!-- Add Policy Modal -->
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
/**
 * Custom styling for tabs component
 * Ensures proper layout and spacing for the tab navigation
 */
:deep(.ant-tabs-nav-wrap) {
  @apply px-10 text-3 leading-3;
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
