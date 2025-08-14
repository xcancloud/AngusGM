<script setup lang='ts'>
import { computed, defineAsyncComponent, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  AsyncComponent, ButtonAuth, Card, Icon, IconCount, IconRefresh, Input, SelectEnum, Table
} from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';
import { OrgTargetType } from '@/enums/enums';

import { Target, TargetSearchParams, UserData, GroupData } from '../types';
import {
  createTargetSearchHandler, getTargetTypeText, getColumnTitles, getPlaceholderText,
  getCancelBtnDisabled, getCancelText, calculateCurrentPage, createTableChangeHandler,
  columns as columns_
} from '../utils';
import { orgTag } from '@/api';

// Lazy load modal components
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const DeptModal = defineAsyncComponent(() => import('@/components/DeptModal/index.vue'));
const GroupModal = defineAsyncComponent(() => import('@/components/GroupModal/index.vue'));

// Component props interface
interface Props {
  tagId: string;
  visible: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  tagId: undefined,
  visible: true
});

// Component event emissions
const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const { t } = useI18n();

// Reactive state management
const loading = ref(false);
const disabled = ref(false);
const params = ref<TargetSearchParams>({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  tagId: '',
  targetType: OrgTargetType.USER
});
const total = ref(0);
const targetList = ref<Target[]>([]);
const targetType = ref<OrgTargetType>(OrgTargetType.USER);

// Computed pagination object for table
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

// Load tag targets from API
const loadTagTargetList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }

  const listParams = {
    ...params.value,
    tagId: props.tagId,
    targetType: targetType.value
  };

  loading.value = true;
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  loading.value = false;

  if (error) {
    return;
  }

  targetList.value = data?.list || [];
  total.value = +data?.total;
};

// Handle table pagination and sorting changes
const tableChange = createTableChangeHandler(async (pagination: { current: number, pageSize: number }) => {
  params.value.pageNo = pagination.current;
  params.value.pageSize = pagination.pageSize;
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

// State for update operations
const updateLoading = ref(false);
const isRefresh = ref(false);

// User association modal state and handlers
const userVisible = ref(false);
const relevancyUser = () => {
  userVisible.value = true;
};

// Save user tag associations
const saveUserTags = async (_userIds: string[], users: UserData[], deleteUserIds: string[]) => {
  // Delete existing associations
  if (deleteUserIds.length) {
    await delTagTarget(deleteUserIds);
  }

  // Add new associations
  if (users.length) {
    const targetList = users.map(item => ({
      targetId: item.id,
      targetType: targetType.value
    }));
    await addTagTarget(targetList);
  }

  updateLoading.value = false;
  userVisible.value = false;

  // Refresh data if needed
  if (isRefresh.value) {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

// Department association modal state and handlers
const deptVisible = ref(false);
const relevancyDept = () => {
  deptVisible.value = true;
};

// Save department tag associations
const saveDeptTags = async (addIds: string[], delIds: string[]) => {
  // Delete existing associations
  if (delIds.length) {
    await delTagTarget(delIds, 'Modal');
  }

  // Add new associations
  if (addIds.length) {
    await addTagDepts(addIds);
  }

  deptVisible.value = false;
  updateLoading.value = false;

  // Refresh data if needed
  if (isRefresh.value) {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

// Add department associations
const addTagDepts = async (_addIds: string[]) => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  await orgTag.addTagTarget(
    props.tagId,
    _addIds.map(item => ({ targetId: item, targetType: 'DEPT' }))
  );
  loading.value = false;
  isRefresh.value = true;
};

// Group association modal state and handlers
const groupVisible = ref(false);
const relevancyGroup = () => {
  groupVisible.value = true;
};

// Save group tag associations
const addTagGroups = async (_groupIds: string[], groups: GroupData[], deleteGroupIds: string[]) => {
  // Delete existing associations
  if (deleteGroupIds.length) {
    await delTagTarget(deleteGroupIds, 'Modal');
  }

  // Add new associations
  if (groups.length) {
    const targetList = groups.map(item => ({
      targetId: item.id,
      targetName: item.name,
      targetType: targetType.value
    }));
    await addTagTarget(targetList);
  }

  groupVisible.value = false;
  updateLoading.value = false;

  // Refresh data if needed
  if (isRefresh.value) {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

// Add tag targets (generic function)
const addTagTarget = async (targetList: { targetId: string, targetType: OrgTargetType }[]) => {
  updateLoading.value = true;
  const [error] = await orgTag.addTagTarget(props.tagId, targetList);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

// Delete tag targets
const delTagTarget = async (targetIds: string[], type?: 'Table' | 'Modal') => {
  updateLoading.value = true;
  const [error] = await orgTag.deleteTagTarget(props.tagId, targetIds);

  if (type === 'Table') {
    updateLoading.value = false;
  }

  if (error) {
    return;
  }

  if (type === 'Modal') {
    isRefresh.value = true;
  }

  // Recalculate current page after deletion
  params.value.pageNo = calculateCurrentPage(params.value.pageNo, params.value.pageSize, total.value);

  if (type === 'Table') {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
  }
};

// Watch for target type changes
watch(() => targetType.value, async () => {
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

// Watch for tag ID changes
watch(() => props.tagId, async (newValue) => {
  if (!newValue) {
    return;
  }
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

// Handle search with filters
const searchUserName = createTargetSearchHandler(async (filters: any[], pageNo: number) => {
  params.value.pageNo = pageNo;
  params.value.filters = filters;
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

// Toggle visibility
const toggle = () => {
  emit('update:visible', !props.visible);
};

// Get target type display text
const getTargetType = (value: OrgTargetType) => getTargetTypeText(value, t);

// Initialize table columns
const columns = ref(columns_(t));

// Update column titles based on target type
const targetTypeChange = () => {
  const titles = getColumnTitles(targetType.value, t);
  columns.value[0].title = titles.id;
  columns.value[1].title = titles.name;
};

// Computed placeholder text for search input
const placeholder = computed(() => getPlaceholderText(targetType.value, t));

// Get cancel button configuration
const cancelBtnDisabled = getCancelBtnDisabled(app);
const cancelText = getCancelText(app);
</script>

<template>
  <Card class="flex-1">
    <template #title>
      <span class="text-3">{{ t('tag.assocOrg') }}</span>
    </template>

    <!-- Search and action toolbar -->
    <div class="flex justify-between mb-2">
      <div class="flex items-center space-x-2">
        <!-- Target type selector -->
        <SelectEnum
          v-model:value="targetType"
          internal
          class="w-50"
          size="small"
          :enumKey="OrgTargetType"
          @change="targetTypeChange" />
        <!-- Search input -->
        <Input
          :placeholder="placeholder"
          class="w-60"
          size="small"
          allowClear
          @change="searchUserName">
          <template #suffix>
            <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
          </template>
        </Input>
      </div>

      <!-- Action buttons -->
      <div class="space-x-2 flex items-center">
        <!-- User association button -->
        <template v-if="targetType === 'USER'">
          <ButtonAuth
            code="TagUserAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyUser" />
        </template>
        <!-- Department association button -->
        <template v-if="targetType === 'DEPT'">
          <ButtonAuth
            code="TagDeptAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyDept" />
        </template>
        <!-- Group association button -->
        <template v-if="targetType === 'GROUP'">
          <ButtonAuth
            code="TagGroupAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyGroup" />
        </template>

        <!-- Visibility toggle and refresh -->
        <IconCount :value="props.visible" @click="toggle" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadTagTargetList" />
      </div>
    </div>

    <!-- Data table -->
    <Table
      :dataSource="targetList"
      rowKey="id"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      size="small"
      :noDataSize="'small'"
      :noDataText="t('common.messages.noData')"
      @change="tableChange">
      <template #bodyCell="{ column, text, record }">
        <!-- Target type display -->
        <template v-if="column.dataIndex === 'targetType'">
          {{ getTargetType(text) }}
        </template>
        <!-- Action buttons -->
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="TagGroupAssociate"
            type="text"
            :disabled="cancelBtnDisabled[targetType]"
            :text="cancelText[targetType]"
            icon="icon-quxiao"
            @click="delTagTarget([record.targetId], 'Table')" />
        </template>
      </template>
    </Table>

    <!-- User association modal -->
    <AsyncComponent :visible="userVisible">
      <UserModal
        v-if="userVisible"
        v-model:visible="userVisible"
        :tagId="props.tagId"
        :updateLoading="updateLoading"
        type="Tag"
        @change="saveUserTags" />
    </AsyncComponent>

    <!-- Department association modal -->
    <AsyncComponent :visible="deptVisible">
      <DeptModal
        v-if="deptVisible"
        v-model:visible="deptVisible"
        type="Tag"
        :updateLoading="updateLoading"
        :tagId="props.tagId"
        @change="saveDeptTags" />
    </AsyncComponent>

    <!-- Group association modal -->
    <AsyncComponent :visible="groupVisible">
      <GroupModal
        v-if="groupVisible"
        v-model:visible="groupVisible"
        :tagId="props.tagId"
        :updateLoading="updateLoading"
        type="Tag"
        @change="addTagGroups" />
    </AsyncComponent>
  </Card>
</template>

<style scoped>
/* Loading animation keyframes */
@keyframes circle {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* Loading animation class */
.circle-move {
  animation-name: circle;
  animation-duration: 1000ms;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  animation-direction: normal;
}
</style>
