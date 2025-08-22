<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Input, Table } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { PageQuery, SearchCriteria, duration, utils } from '@xcan-angus/infra';

import { user } from '@/api';

import { UserTag } from '../types';
import { createUserTagColumns } from '../utils';

/**
 * Async component for tag modal
 * Loaded only when needed to improve performance
 */
const TagModal = defineAsyncComponent(() => import('@/components/TagModal/index.vue'));

/**
 * Modal state management for tag operations
 */
const tagVisible = ref(false); // Tag modal visibility
const updateLoading = ref(false); // Loading state for tag update operations
const isRefresh = ref(false); // Refresh state flag for modal operations
const disabled = ref(false); // Disabled state for refresh button during operations

/**
 * Component props interface
 * Defines the properties passed to the user tag component
 */
interface Props {
  userId: string; // User ID for tag management
  hasAuth: boolean; // Whether user has permission to modify tags
}

const props = withDefaults(defineProps<Props>(), {
  userId: undefined,
  hasAuth: false
});

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive state management for component
 */
const loading = ref(false); // Loading state for API calls
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] }); // Search and pagination parameters
const total = ref(0); // Total number of tags for pagination
const count = ref(0); // Current tag count for quota display
const isContUpdate = ref(true); // Whether to update count continuously
const dataList = ref<UserTag[]>([]); // Tag list data

/**
 * Load user tags from API
 * Handles loading state and error handling
 */
const loadUserTag = async () => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserTag(props.userId, params.value);
  loading.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = +data.total;
  if (isContUpdate.value) {
    count.value = +data.total;
  }
};

/**
 * Open tag modal for adding new tags
 */
const addTag = () => {
  tagVisible.value = true;
};

/**
 * Handle tag save from modal
 * Processes selected tag IDs and handles add/delete operations
 */
const saveUserTags = async (_tagIds: string[], _tags: { id: string, name: string }[], deleteTagIds: string[]) => {
  if (deleteTagIds.length) {
    await delUserTag(deleteTagIds, 'Modal');
  }
  if (_tagIds.length) {
    await addUserTag(_tagIds);
  }
  tagVisible.value = false;
  updateLoading.value = false;

  if (isRefresh.value) {
    disabled.value = true;
    await loadUserTag();
    disabled.value = false;
    isRefresh.value = false;
  }
};

/**
 * Add tags to user
 * Calls API to associate tags with user
 */
const addUserTag = async (_tagIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserTag(props.userId, _tagIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

/**
 * Delete tags from user
 * Calls API to remove tag associations
 */
const delUserTag = async (_tagIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserTag(props.userId, _tagIds);
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
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  if (type === 'Table') {
    disabled.value = true;
    await loadUserTag();
    disabled.value = false;
  }
};

/**
 * Cancel/Remove tag from user
 * Calls delete function for table operations
 */
const handleCancel = async (id) => {
  await delUserTag([id], 'Table');
};

/**
 * Handle search input with debouncing
 * Filters tags by name with end matching
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'tagName', op: SearchCriteria.OpEnum.MatchEnd, value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  isContUpdate.value = false;
  await loadUserTag();
  disabled.value = false;
  isContUpdate.value = true;
});

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
 * Handle table pagination changes
 * Updates parameters and reloads data based on table interactions
 */
const handleChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadUserTag();
  disabled.value = false;
};

/**
 * Table columns configuration
 * Defines the structure and behavior of each table column
 */
const userTagColumns = createUserTagColumns(t);

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial tag data
 */
onMounted(() => {
  loadUserTag();
});
</script>
<template>
  <div>
    <!-- User tag quota hints -->
    <Hints :text="t('tag.userTagQuotaTip', {num: count})" class="mb-1" />

    <!-- Search and action toolbar -->
    <div class="flex items-center justify-between mb-2">
      <!-- Tag name search input -->
      <Input
        :placeholder="t('tag.placeholder.name')"
        class="w-60"
        size="small"
        allowClear
        @change="handleSearch">
        <template #suffix>
          <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
        </template>
      </Input>

      <!-- Action buttons -->
      <div class="flex space-x-2 items-center">
        <!-- Add tag button -->
        <ButtonAuth
          code="UserTagsAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="hasAuth || total>=10"
          @click="addTag" />

        <!-- Refresh button -->
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserTag" />
      </div>
    </div>

    <!-- Tag data table -->
    <Table
      size="small"
      rowKey="id"
      :loading="loading"
      :dataSource="dataList"
      :columns="userTagColumns"
      :pagination="pagination"
      @change="handleChange">
      <!-- Custom cell renderers for table columns -->
      <template #bodyCell="{ column, record }">
        <!-- Action buttons for each tag row -->
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserTagsUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.tagId)" />
        </template>
      </template>
    </Table>
  </div>

  <!-- Tag modal for adding new tags -->
  <AsyncComponent :visible="tagVisible">
    <TagModal
      v-if="tagVisible"
      v-model:visible="tagVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="saveUserTags" />
  </AsyncComponent>
</template>
