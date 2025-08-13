<script setup lang='ts'>
import { onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Pagination } from 'ant-design-vue';
import {
  ButtonAuth, Icon, Image, modal, NoData, notification,
  PureCard, SelectDept, SelectGroup, SelectUser
} from '@xcan-angus/vue-ui';
import { cookieUtils } from '@xcan-angus/infra';

import { auth } from '@/api';
import { AuthObjectDataType } from '../types';

/**
 * Props interface for TargetPanel component
 */
interface Props {
  policyId: string | undefined,
  type: 'USER' | 'DEPT' | 'GROUP',
  appId?: string | undefined
}

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), {
  appId: undefined
});

/**
 * Configuration for add button text based on target type
 * Maps target types to their corresponding authorization codes
 */
const addTextConfig = {
  USER: 'AuthUserAdd',
  DEPT: 'AuthDeptAdd',
  GROUP: 'AuthGroupAdd'
};

/**
 * Configuration for cancel button text based on target type
 * Maps target types to their corresponding cancellation codes
 */
const cancelText = {
  USER: 'AuthUserCancel',
  DEPT: 'AuthDeptCancel',
  GROUP: 'AuthGroupCancel'
};

/**
 * Human-readable type name for display purposes
 * Converts the type enum to localized display text
 */
const getTypeName = ():string => {
  return props.type === 'USER'
    ? t('permission.policy.detail.target.user')
    : props.type === 'GROUP'
      ? t('permission.policy.detail.target.group')
      : t('permission.policy.detail.target.dept');
};

/**
 * Component reactive state
 * Manages loading states, selected values, and data source
 */
const state = reactive<{
  loading: boolean,
  saving: boolean,
  selectedValue: string | undefined,
  dataSource: AuthObjectDataType[]
}>({
  loading: false, // Loading state for list queries
  saving: false, // Loading state for save operations
  selectedValue: undefined, // Currently selected value in selection box
  dataSource: [] // List data source
});

/**
 * Pagination configuration
 * Handles page navigation and display settings
 */
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 20,
  showLessItems: true,
  showTotal: (total: number) => {
    const pageNo = pagination.current;
    const totalPage = Math.ceil(total / pagination.pageSize);
    return t('common.table.pageShowTotal', { total, pageNo, totalPage });
  },
  showSizeChanger: false,
  size: 'small',
  pageSizeOptions: ['10', '20', '30', '40', '50']
});

/**
 * Handle target selection change
 * Updates the selected value when user makes a selection
 * @param {string | undefined} value - Selected target value
 */
const targetChange = (value: string | undefined) => {
  state.selectedValue = value;
};

/**
 * Load currently authorized targets for the policy
 * Fetches paginated data based on target type (users, departments, or groups)
 */
const load = async () => {
  if (!props.policyId) {
    return;
  }

  const params: {
    pageNo: number,
    pageSize: number
  } = {
    pageNo: pagination.current,
    pageSize: pagination.pageSize
  };
  state.loading = true;
  let res: [Error | null, any];
  if (props.type === 'USER') {
    res = await auth.getPolicyUsers(props.policyId, params);
  } else if (props.type === 'DEPT') {
    res = await auth.getPolicyDept(props.policyId, params);
  } else {
    res = await auth.getPolicyGroups(props.policyId, params);
  }

  const [error, { data }] = res;
  state.loading = false;
  if (error) {
    return;
  }

  state.dataSource = data.list;
  pagination.total = +data.total;
  if (props.type === 'USER') {
    state.dataSource.forEach(item => {
      item.avatar = item.avatar ? `${item.avatar}&access_token=${cookieUtils.get('access_token')}` : '';
    });
  }
};

/**
 * Reset pagination and reload data
 * Resets to first page and fetches fresh data
 */
const resetPageNoLoad = () => {
  pagination.current = 1;
  load();
};

/**
 * Save the selected target to the policy
 * Adds the selected user, department, or group to the current policy
 */
const addSave = async () => {
  if (!state.selectedValue) {
    notification.warning(t('permission.policy.detail.target.addWarn', { name: getTypeName() }));
    return;
  }

  if (!props.policyId) {
    return;
  }

  const ids = [state.selectedValue];
  let res: [Error | null, any];
  state.saving = true;
  if (props.type === 'USER') {
    res = await auth.addPolicyUser(props.policyId, ids);
  } else if (props.type === 'DEPT') {
    res = await auth.addPolicyDept(props.policyId, ids);
  } else {
    res = await auth.addPolicyGroup(props.policyId, ids);
  }

  const [error] = res;
  state.saving = false;
  if (error) {
    return;
  }

  notification.success(t('permission.policy.detail.target.addSuccess'));
  state.selectedValue = undefined;
  resetPageNoLoad();
};

/**
 * Delete a target from the policy
 * Removes the specified user, department, or group from the current policy
 * @param {AuthObjectDataType} item - Target item to delete
 */
const delTarget = (item: AuthObjectDataType) => {
  modal.confirm({
    centered: true,
    title: t('common.messages.confirmTitle'),
    content: t('permission.policy.detail.target.deleteConfirm', { name: getTypeName() }),
    async onOk () {
      if (!props.policyId) {
        return;
      }

      let res: [Error | null, any];
      if (props.type === 'USER') {
        res = await auth.deletePolicyUsers(props.policyId, [item.id]);
      } else if (props.type === 'DEPT') {
        res = await auth.deletePolicyDept(props.policyId, [item.id]);
      } else {
        res = await auth.deletePolicyGroups(props.policyId, [item.id]);
      }

      const [error] = res;
      if (error) {
        return;
      }

      notification.success(t('permission.policy.detail.target.deleteSuccess'));
      resetPageNoLoad();
    }
  });
};

/**
 * Initialize component data on mount
 * Loads the initial list of authorized targets
 */
onMounted(() => {
  load();
});
</script>

<template>
  <!-- Target management panel for policy authorization -->
  <PureCard class="flex-1 flex flex-col p-3.5">
    <!-- Panel header with target type name -->
    <div class="mb-3 text-3 leading-3 text-theme-title font-medium">
      {{ t('permission.policy.detail.target.title', { name: getTypeName() }) }}
    </div>

    <!-- Target selection and add button section -->
    <div class="pb-3 flex justify-between border-b border-solid border-theme-divider ">
      <!-- User selection component -->
      <SelectUser
        v-if="type === 'USER'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permission.policy.detail.target.placeholder', { name: getTypeName() })"
        :internal="true"
        size="small"
        @change="targetChange" />

      <!-- Department selection component -->
      <SelectDept
        v-if="type === 'DEPT'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permission.policy.detail.target.placeholder', { name: getTypeName() })"
        :internal="true"
        size="small"
        @change="targetChange" />

      <!-- Group selection component -->
      <SelectGroup
        v-if="type === 'GROUP'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permission.policy.detail.target.placeholder', { name: getTypeName() })"
        :internal="true"
        size="small"
        @change="targetChange" />

      <!-- Add button with authorization check -->
      <ButtonAuth
        :code="addTextConfig[props.type]"
        size="small"
        type="primary"
        class="ml-3"
        icon="icon-tianjia"
        :loading="state.saving"
        @click="addSave" />
    </div>

    <!-- Target list display section -->
    <div class="flex-1 py-2 overflow-y-auto text-3 leading-7">
      <!-- Individual target items -->
      <div
        v-for="item in state.dataSource"
        :key="item.id"
        class="flex items-center px-3 rounded cursor-pointer hover:bg-blue-hover-light py-0.5">
        <!-- User avatar display -->
        <Image
          v-if="type === 'USER'"
          type="avatar"
          class="w-5 h-5 rounded-2xl"
          :src="item.avatar" />

        <!-- Department icon display -->
        <div v-if="type === 'DEPT'" class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline h-full text-theme-content text-4" icon="icon-bumen" />
        </div>

        <!-- Group icon display -->
        <div v-if="type === 'GROUP'" class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline h-full text-theme-content text-4" icon="icon-zu" />
        </div>

        <!-- Target name display -->
        <div class="w-70 flex-1 text-3 leading-3 text-theme-content px-2 truncate">
          {{ type === 'USER' ? item.fullName : item.name }}
        </div>

        <!-- Remove button for each target -->
        <div class="text-3 leading-3">
          <ButtonAuth
            :code="cancelText[props.type]"
            size="small"
            type="text"
            icon="icon-quxiao"
            @click="delTarget(item)" />
        </div>
      </div>

      <!-- No data display when list is empty -->
      <NoData
        v-if="state.dataSource.length === 0"
        class="h-full"
        :text="t('permission.policy.detail.target.noData', { name: getTypeName() })" />
    </div>

    <!-- Pagination component -->
    <Pagination
      v-if="pagination.total >= 10"
      v-model:current="pagination.current"
      v-model:pageSize="pagination.pageSize"
      class="w-full my-5 pr-5 h-6 text-right"
      :size="pagination.size"
      :showLessItems="pagination.showLessItems"
      :showSizeChanger="pagination.showSizeChanger"
      :total="pagination.total"
      :showTotal="pagination.showTotal"
      :pageSizeOptions="pagination.pageSizeOptions"
      @showSizeChange="load"
      @change="load">
    </Pagination>
  </PureCard>
</template>
