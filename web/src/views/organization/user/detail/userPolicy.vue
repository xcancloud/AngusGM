<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Popover } from 'ant-design-vue';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Input, Table } from '@xcan-angus/vue-ui';
import { PageQuery, duration, utils, SearchCriteria } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';
import { OrgTargetType } from '@/enums/enums';

import { auth } from '@/api';
import { createAuthPolicyColumns } from '@/views/organization/user/utils';

/**
 * Async component for policy modal
 * Loaded only when needed to improve performance
 */
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

/**
 * Modal state management for olicy operations
 */
const policyVisible = ref(false); // Policy modal visibility
const updateLoading = ref(false); // Loading state for policy update operations

/**
 * Component props interface
 * Defines the properties passed to the authorization policy component
 */
interface Props {
  userId: string; // User ID for policy management
  hasAuth: boolean; // Whether user has permission to modify policies
}

const props = withDefaults(defineProps<Props>(), {
  userId: '',
  hasAuth: false
});

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive state management for component
 */
const loading = ref(false); // Loading state for API calls
const disabled = ref(false); // Disabled state for buttons during operations
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] }); // Search and pagination parameters
const total = ref(0); // Total number of policies for pagination
const dataList = ref([]); // Policy list data

/**
 * Load user policies from API
 * Handles loading state and error handling
 */
const getUserPolicy = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await auth.getUserPolicy(props.userId, params.value);
  loading.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = +data.total;
};

/**
 * Open policy modal for adding new policies
 */
const addPolicy = () => {
  policyVisible.value = true;
};

/**
 * Handle policy save from modal
 * Processes selected policy IDs and calls add function
 */
const policySave = (addIds: string[]) => {
  if (!addIds.length) {
    return;
  }
  addUserPolicy(addIds);
};

/**
 * Add policies to user
 * Calls API to associate policies with user
 */
const addUserPolicy = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await auth.addUserPolicy(props.userId, _addIds);
  updateLoading.value = false;
  policyVisible.value = false;
  if (error) {
    return;
  }
  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

/**
 * Cancel/Remove policy from user
 * Calls API to remove policy association
 */
const handleCancel = async (id) => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error] = await auth.deleteUserPolicy(props.userId, [id]);
  loading.value = false;

  if (error) {
    return;
  }
  // Recalculate current page after deletion
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

/**
 * Handle search input with debouncing
 * Filters policies by name with end matching
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'name', op: SearchCriteria.OpEnum.MatchEnd, value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
});

/**
 * Handle table pagination, sorting, and filtering changes
 * Updates parameters and reloads data based on table interactions
 */
const handleChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

/**
 * Get authorization type display text
 * Combines default and open authorization types
 */
const getAuthorizationType = (record) => {
  const resultArr: string[] = [];
  if (record.currentDefault) {
    resultArr.push(t('permission.defaultAuth'));
  }
  if (record.openAuth) {
    resultArr.push(t('permission.openAuth'));
  }
  return resultArr.join(',');
};

/**
 * Computed pagination object for table component
 * Provides reactive pagination data to the table
 */
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

/**
 * Table columns configuration
 * Defines the structure and behavior of each table column
 */
const columns = createAuthPolicyColumns(t, OrgTargetType.USER);

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial policy data
 */
onMounted(() => {
  getUserPolicy();
});
</script>
<template>
  <div>
    <!-- Authorization policy hints -->
    <Hints :text="t('permission.columns.assocPolicies.authTip')" class="mb-1" />

    <!-- Search and action toolbar -->
    <div class="flex items-center justify-between mb-2">
      <!-- Policy name search input -->
      <Input
        :placeholder="t('permission.placeholder.policyName')"
        class="w-60"
        allowClear
        @change="handleSearch">
        <template #suffix>
          <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
        </template>
      </Input>

      <!-- Action buttons -->
      <div class="flex items-center">
        <!-- Add policy button -->
        <ButtonAuth
          code="AuthorizeUser"
          type="primary"
          icon="icon-tianjia"
          class="mr-2"
          :disabled="props.hasAuth"
          @click="addPolicy" />

        <!-- Refresh button -->
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="getUserPolicy" />
      </div>
    </div>

    <!-- Policy data table -->
    <Table
      size="small"
      :loading="loading"
      :dataSource="dataList"
      :rowKey="(record: any) => record.id"
      :columns="columns"
      :pagination="pagination"
      @change="handleChange">
      <!-- Custom cell renderers for table columns -->
      <template #bodyCell="{ column,text,record }">
        <!-- Policy name with description popover -->
        <template v-if="column.dataIndex === 'name'">
          <div class="w-full truncate cursor-pointer">
            <Popover
              placement="bottomLeft"
              :overlayStyle="{maxWidth:'600px',fontSize:'12px'}">
              <template #title>
                {{ text }}
              </template>
              <template #content>
                {{ record.description }}
              </template>
              {{ text }}
            </Popover>
          </div>
        </template>

        <!-- Organization type display -->
        <template v-if="column.dataIndex === 'orgType'">
          <template v-if="['USER','DEPT','GROUP'].includes(text?.value)">
            {{ text?.message }}({{ record.orgName }})
          </template>
          <template v-else>
            {{ getAuthorizationType(record) }}
          </template>
        </template>

        <!-- Action buttons for each policy row -->
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserAuthorizeCancel"
            type="text"
            :disabled="record.orgType?.value !== 'USER'"
            icon="icon-quxiao"
            @click="handleCancel(record.id)" />
        </template>
      </template>
    </Table>
  </div>

  <!-- Policy modal for adding new policies -->
  <AsyncComponent :visible="policyVisible">
    <PolicyModal
      v-if="policyVisible"
      v-model:visible="policyVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="policySave" />
  </AsyncComponent>
</template>
