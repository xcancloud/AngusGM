<script setup lang='ts'>
import { defineAsyncComponent, nextTick, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Pagination, Tag, Tooltip } from 'ant-design-vue';
import { AsyncComponent, ButtonAuth, IconRefresh, Input, modal, notification, PureCard, Spin } from '@xcan-angus/vue-ui';
import { PageQuery, app } from '@xcan-angus/infra';

import { OrgTag, TagAddParams, TagUpdateParams, TagDeleteParams } from '../types';
import { createSearchHandler } from '../utils';

import { orgTag } from '@/api';

// Lazy load add modal component
const AddModal = defineAsyncComponent(() => import('@/views/organization/tag/add/index.vue'));

// Component event emissions
const emit = defineEmits<{
  (e: 'update:tag', tag: OrgTag): void,
  (e: 'update:tenantName', name: string): void,
  (e: 'update'): void
}>();

const { t } = useI18n();

// Reactive state management
const loading = ref(false);
const disabled = ref(false);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 20, filters: [], fullTextSearch: true });
const total = ref(0);
const tagList = ref<OrgTag[]>([]);
const checkedTag = ref<OrgTag | undefined>(undefined);

// Initialize component data
const init = () => {
  loadTagList();
};

// Load tag list from API
const loadTagList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data }] = await orgTag.getTagList(params.value);
  loading.value = false;

  if (error) {
    return;
  }

  // Map API response to local state with edit flags
  tagList.value = data?.list?.map(item => ({ ...item, isEdit: false }));
  total.value = +data?.total;

  // Auto-select first tag if none selected
  if (tagList.value.length && !checkedTag.value) {
    checkedTag.value = tagList.value[0];
    emit('update:tag', tagList.value[0]);
  }
};

// Show delete confirmation dialog
const deleteConfirm = (id: string, name: string): void => {
  modal.confirm({
    centered: true,
    title: t('common.actions.delete'),
    content: t('common.messages.confirmDelete', { name: name }),
    async onOk () {
      await deleteTag(id);
    }
  });
};

// Delete tag from API
const deleteTag = async (id: string): Promise<void> => {
  const deleteParams: TagDeleteParams = {
    ids: [id]
  };
  const [error] = await orgTag.deleteTag(deleteParams);

  if (error) {
    return;
  }

  // Clear selection if deleted tag was selected
  if (checkedTag.value?.id === id) {
    checkedTag.value = undefined;
  }

  notification.success(t('common.messages.deleteSuccess'));
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

// Handle search with filters
const handleSearch = createSearchHandler(async (filters: any[]) => {
  params.value.filters = filters;
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
});

// Add tag modal state
const addVisible = ref(false);
const addTag = () => {
  addVisible.value = true;
};

// Add tag functionality
const addLoading = ref(false);
const addOK = async (name: string | undefined) => {
  if (!name || loading.value) {
    return;
  }

  const addParams: TagAddParams[] = [{ name }];
  addLoading.value = true;
  const [error] = await orgTag.addTag(addParams);
  addLoading.value = false;
  addVisible.value = false;

  if (error) {
    return;
  }

  notification.success(t('common.messages.addSuccess'));
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

// Refresh tag list
const handleRefresh = () => {
  loadTagList();
};

// Pagination configuration
const pageSizeOptions = ['10', '20', '30', '40', '50'];

// Handle pagination changes
const paginationChange = async (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

// Edit name functionality
const editNameLoading = ref(false);
const inputRef = ref();
let timer: ReturnType<typeof setTimeout>;

// Open edit mode for tag name
const openEditName = (tag: OrgTag) => {
  clearTimeout(timer);

  // Set edit mode for selected tag only
  for (let i = 0; i < tagList.value.length; i++) {
    if (tag.id !== tagList.value[i].id) {
      tagList.value[i].isEdit = false;
    } else {
      tagList.value[i].isEdit = true;
    }
  }

  // Focus input after DOM update
  nextTick(() => {
    inputRef.value[0]?.focus();
  });
};

// Save edited tag name
const editName = async (event: any, item: OrgTag) => {
  const value = event.target.value;
  const names = tagList.value.map(item => item.name);

  // Validate name uniqueness and non-empty
  if (names.includes(value) || !value) {
    item.isEdit = false;
    return;
  }

  editNameLoading.value = true;
  const updateParams: TagUpdateParams[] = [{ id: item.id, name: value }];
  const [error] = await orgTag.updateTag(updateParams);
  editNameLoading.value = false;
  item.isEdit = false;

  if (error) {
    return;
  }

  // Refresh tag data after update
  const [error1, { data }] = await orgTag.getDetail(item.id);
  if (error1) {
    return;
  }

  // Update local state
  const index = tagList.value.findIndex(f => f.id === item.id);
  if (index > -1) {
    tagList.value[index] = data;
  }

  // Update selected tag if it was the edited one
  if (checkedTag.value?.id === item.id) {
    emit('update:tag', data);
  }
};

// Select tag with debounced emit
const selectTag = (value: OrgTag): void => {
  clearTimeout(timer);
  timer = setTimeout(() => {
    emit('update:tag', value);
    checkedTag.value = value;
  }, 400);
};

// Lifecycle hooks
onMounted(() => {
  init();
});

// Expose methods for parent component
defineExpose({ openEditName });
</script>

<template>
  <div class="flex flex-col h-full">
    <PureCard class="pr-0 flex flex-col justify-between p-3.5 w-100">
      <!-- Search and action toolbar -->
      <div class="flex items-center mb-2 space-x-2 mr-3.5">
        <Input
          size="small"
          allowClear
          :placeholder="t('tag.placeholder.name')"
          @change="handleSearch" />
        <ButtonAuth
          code="TagAdd"
          type="primary"
          icon="icon-tianjia"
          @click="addTag" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="handleRefresh" />
      </div>

      <!-- Tag list with loading state -->
      <Spin :spinning="loading" class="flex-1 overflow-hidden hover:overflow-y-auto">
        <template
          v-for="item in tagList"
          :key="item.id">
          <!-- Edit mode: show input field -->
          <template v-if="item.isEdit">
            <Input
              ref="inputRef"
              :value="item.name"
              :maxlength="100"
              class="mr-3.5 mb-2"
              size="small"
              style="width: 370px;"
              :placeholder="t('tag.placeholder.addNameTip')"
              @blur="editName($event,item)"
              @pressEnter="editName($event, item)" />
          </template>
          <!-- Display mode: show tag -->
          <template v-else>
            <Tag
              :closable="app.has('TagDelete')"
              class="tag truncate cursor-pointer"
              :class="{
                'border-theme-divider-selected': item.id === checkedTag?.id,
                'selected': item.id === checkedTag?.id
              }"
              style="max-width: 372px;"
              @close.prevent="deleteConfirm(item.id,item.name)"
              @click="selectTag(item)"
              @dblclick="app.has('TagModify') && openEditName(item)">
              <!-- Show full name if short, truncated with tooltip if long -->
              <template v-if="item.name.length<=15">
                {{ item.name }}
              </template>
              <template v-else>
                <Tooltip
                  :title="item.name"
                  placement="bottomLeft">
                  {{ item.name.slice(0,15) }}...
                </Tooltip>
              </template>
            </Tag>
          </template>
        </template>
      </Spin>

      <!-- Pagination controls -->
      <Pagination
        :current="params.pageNo"
        :pageSize="params.pageSize"
        :pageSizeOptions="pageSizeOptions"
        :total="total"
        :showLessItems="true"
        :hideOnSinglePage="false"
        :showSizeChanger="false"
        size="small"
        class="text-right mr-4.5 mt-2"
        @change="paginationChange" />
    </PureCard>

    <!-- Add tag modal -->
    <AsyncComponent :visible="addVisible">
      <AddModal
        v-if="addVisible"
        v-model:visible="addVisible"
        :loading="addLoading"
        @ok="addOK" />
    </AsyncComponent>
  </div>
</template>

<style scoped>
/* Selected tag border styling */
.border-theme-divider-selected {
  border-color: var(--border-divider-selected);
}

/* Tag styling with light blue theme */
.tag {
  background: #e6f7ff; /* light blue background */
  border: 1px solid #91d5ff; /* blue border */
  color: #1890ff; /* primary blue text */
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-top: 5px;
}

/* Selected tag visual state */
.tag.selected {
  background: #bae7ff; /* deeper blue hint for selected state */
  border-color: #1890ff;
}

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
