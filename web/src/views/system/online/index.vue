<script setup lang="ts">
import { computed, h, onMounted, reactive } from 'vue';
import { Badge, Spin } from 'ant-design-vue';
import { Icon, IconRefresh, Image, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, app } from '@xcan-angus/infra';
import { LoadingOutlined } from '@ant-design/icons-vue';

import { useI18n } from 'vue-i18n';
import { online } from '@/api';
import {
  OnlineUser,
  OnlineState,
  PaginationConfig,
  TableChangeParams
} from './types';
import {
  createOnlineUserColumns,
  createOnlineUserSearchOptions,
  createPaginationConfig,
  processOnlineUserList,
  createLogoutParams,
  getOnlineStatusColor,
  getOnlineStatusText,
  updatePaginationParams,
  updateSortingParams,
  resetPagination
} from './utils';

const { t } = useI18n();

// Reactive state management
const state = reactive<OnlineState>({
  params: {
    pageNo: 1,
    pageSize: 10,
    filters: [],
    orderBy: 'id',
    orderSort: PageQuery.OrderSort.Desc,
    fullTextSearch: true
  },
  total: 0,
  onlineList: [],
  loading: false,
  disabled: false
});

/**
 * Load online user list from API
 */
const getList = async (): Promise<void> => {
  if (state.loading) {
    return;
  }

  state.loading = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await online.getOnlineUserList(state.params);
    if (error) {
      return;
    }

    state.onlineList = processOnlineUserList(data?.list);
    state.total = +data.total;
  } finally {
    state.loading = false;
  }
};

/**
 * Handle user logout
 */
const handleLogOut = async (item: OnlineUser): Promise<void> => {
  item.loading = true;
  try {
    const logoutParams = createLogoutParams(item.userId);
    await online.offlineUser(logoutParams);
  } finally {
    item.loading = false;
  }

  state.disabled = true;
  await getList();
  state.disabled = false;
};

/**
 * Handle table pagination, filtering, and sorting changes
 */
const tableChange = async (_pagination: any, _filters: any, sorter: TableChangeParams['sorter']): Promise<void> => {
  updatePaginationParams(state.params, _pagination);
  updateSortingParams(state.params, sorter);

  state.disabled = true;
  await getList();
  state.disabled = false;
};

/**
 * Handle search criteria changes
 */
const searchChange = async (data: SearchCriteria[]): Promise<void> => {
  resetPagination(state.params);
  state.params.filters = data;
  state.disabled = true;
  await getList();
  state.disabled = false;
};

// Computed pagination configuration
const pagination = computed<PaginationConfig>(() => {
  return createPaginationConfig(state.params.pageNo, state.params.pageSize, state.total);
});

// Create table columns using utility function
const columns = computed(() => createOnlineUserColumns(t));

// Create search options using utility function
const searchOptions = computed(() => createOnlineUserSearchOptions(t));

// Loading indicator for logout operation
const indicator = h(LoadingOutlined, {
  style: {
    fontSize: '24px'
  },
  spin: true
});

// Lifecycle hook - initialize component on mount
onMounted(() => {
  getList();
});
</script>

<template>
  <PureCard class="min-h-full p-3.5">
    <!-- Search and filter controls -->
    <div class="flex items-center justify-between mb-2">
      <!-- Search panel for filtering users -->
      <SearchPanel
        class="flex-1"
        :options="searchOptions"
        @change="searchChange" />

      <!-- Refresh button -->
      <IconRefresh
        :loading="state.loading"
        :disabled="state.disabled"
        @click="getList" />
    </div>

    <!-- Online users table -->
    <Table
      :columns="columns"
      :dataSource="state.onlineList"
      :pagination="pagination"
      :loading="state.loading"
      rowKey="id"
      :noDataSize="'small'"
      :noDataText="t('common.messages.noData')"
      @change="tableChange">
      <!-- Custom cell rendering -->
      <template #bodyCell="{ column, text, record }">
        <!-- User name column with avatar -->
        <template v-if="column.dataIndex === 'fullName'">
          <div class="inline-flex items-center truncate">
            <Image
              type="avatar"
              class="w-6 rounded-full mr-1"
              :src="record.avatar" />
            <span class="flex-1 truncate" :title="record.fullName">{{ record.fullName }}</span>
          </div>
        </template>

        <!-- Online status column with colored badge -->
        <template v-if="column.dataIndex === 'online'">
          <Badge
            :color="getOnlineStatusColor(text)"
            :text="getOnlineStatusText(text, t)" />
        </template>

        <!-- Logout action column -->
        <template v-if="column.dataIndex === 'option' && app.show('SignOut')">
          <template v-if="record.loading">
            <Spin :indicator="indicator" />
          </template>
          <template v-else>
            <Icon
              v-if="record.online && app.has('SignOut')"
              icon="icon-xuanzezhanghao1"
              class="cursor-pointer text-theme-special text-theme-text-hover"
              @click="handleLogOut(record)" />
            <Icon
              v-else
              icon="icon-xuanzezhanghao1"
              class="cursor-pointer text-theme-sub-content" />
          </template>
        </template>
      </template>
    </Table>
  </PureCard>
</template>
