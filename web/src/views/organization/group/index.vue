<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  AsyncComponent, ButtonAuth, Icon, IconCount, IconRefresh, PureCard, SearchPanel, Table
} from '@xcan-angus/vue-ui';
import { app, GM, PageQuery, SearchCriteria, enumUtils } from '@xcan-angus/infra';

// Import local types and utility functions
import { ListGroup } from './types';
import {
  addGroupUser as addGroupUserUtil, createGroupColumns, createSearchOptions,
  delGroupConfirm as delGroupConfirmUtil, delGroupUser as delGroupUserUtil,
  loadGroupList as loadGroupListUtil, searchChange as searchChangeUtil,
  tableChange as tableChangeUtil, updateStatusConfirm as updateStatusConfirmUtil
} from './utils';
import { ChartType, DateRangeType } from '@/components/dashboard/enums';
import {  GroupSource } from '@/enums/enums';

// Define async components for lazy loading
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/dashboard/Dashboard.vue'));

// Initialize internationalization
const { t } = useI18n();

// Reactive state variables
const loading = ref(false); // Loading state for API calls
const showCount = ref(true); // Toggle for statistics display
const disabled = ref(false); // Disable state for operations
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true }); // Pagination and search parameters
const total = ref(0); // Total number of groups
const groupList = ref<ListGroup[]>([]); // List of groups to display

// Computed pagination object for table
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

// Initialize component data
const init = () => {
  loadGroupList();
};

// Load group list from API
const loadGroupList = async (): Promise<void> => {
  await loadGroupListUtil(params.value, loading, groupList, total);
};

// Confirm and update group status (enable/disable)
const updateStatusConfirm = (id: string, name: string, enabled: boolean) => {
  updateStatusConfirmUtil(id, name, enabled, t, params, total, loadGroupList, disabled);
};

// Handle search criteria changes
const searchChange = async (data: SearchCriteria[]) => {
  await searchChangeUtil(data, params, loadGroupList, disabled);
};

// Handle table pagination, filtering, and sorting changes
const tableChange = async (_pagination, _filters, sorter) => {
  await tableChangeUtil(_pagination, _filters, sorter, params, loadGroupList, disabled);
};

// Confirm and delete group
const delGroupConfirm = (id: string, name: string) => {
  delGroupConfirmUtil(id, name, t, params, total, loadGroupList, disabled);
};

// User management modal state
const userVisible = ref(false); // Control user modal visibility
const selectedGroup = ref<ListGroup>(); // Currently selected group for user operations
const updateLoading = ref(false); // Loading state for user operations

// Open user association modal for a specific group
const openConcatUser = async (record: ListGroup): Promise<void> => {
  selectedGroup.value = record;
  userVisible.value = true;
};

// Save user associations (add/remove users from group)
const saveUser = async (_userIds: string[], _users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  let reload = false;
  // Remove users from group
  if (deleteUserIds.length) {
    await delGroupUser(deleteUserIds);
    reload = true;
  }
  // Add users to group
  if (_userIds.length) {
    await addGroupUser(_userIds);
    reload = true;
  }

  userVisible.value = false;
  // Reload group list if changes were made
  if (reload) {
    disabled.value = true;
    await loadGroupList();
    disabled.value = false;
  }
};

// Add users to the selected group
const addGroupUser = async (_userIds: string[]) => {
  if (!selectedGroup.value) {
    return;
  }
  await addGroupUserUtil(selectedGroup.value.id, _userIds, updateLoading);
};

// Remove users from the selected group
const delGroupUser = async (_userIds: string[]) => {
  if (!selectedGroup.value) {
    return;
  }
  await delGroupUserUtil(selectedGroup.value.id, _userIds, updateLoading);
};

// Search options for the search panel
const searchOptions = ref(createSearchOptions(t, GM));

// Computed columns with internationalized titles
const columns = computed(() => {
  return _columns.map((item) => ({ ...item, title: t(item.title) }));
});

// Handle refresh button click
const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadGroupList();
};

// Create group table columns
const _columns = createGroupColumns(t);


const dashboardConfig = {
  charts: [
      {
        type: ChartType.LINE,
        title: t('statistics.metrics.newGroups'),
        field: 'created_date'
      },
      {
        type: ChartType.PIE,
        title: [t('common.status.validStatus'), t('common.labels.source')],
        field: ['enabled', 'source'],
        enumKey: [
          [{ value: 0, message: t('common.status.disabled') }, { value: 1, message: t('common.status.enabled') }],

          enumUtils.enumToMessages(GroupSource),

        ],
        legendPosition: ['right', 'right']
      }
    ],
    layout: {
      cols: 2,
      gap: 16
    }
}

// Component lifecycle - initialize on mount
onMounted(() => {
  init();
});
</script>
<template>
  <!-- Main container with statistics and group management -->
  <PureCard class="p-3.5 min-h-full">
    <!-- Statistics component showing new groups metrics -->
    <!-- <Statistics
      :visible="showCount"
      :barTitle="t('statistics.metrics.newGroups')"
      dateType="YEAR"
      resource="Group"
      :router="GM"
      class="mb-3" /> -->
      <Dashboard
        v-show="showCount"
        class="py-3"
        :config="dashboardConfig"
        :apiRouter="GM"
        resource="Group"
        :dateType="DateRangeType.YEAR"
        :showChartParam="true"
        pieItemClass="!w-120" />

    <!-- Search and action toolbar -->
    <div class="flex items-center justify-between mb-3">
      <!-- Search panel for filtering groups -->
      <SearchPanel :options="searchOptions" @change="searchChange" />

      <!-- Action buttons: Add group, toggle statistics, refresh -->
      <div class="flex items-center space-x-2">
        <ButtonAuth
          code="GroupAdd"
          type="primary"
          href="/organization/group/add?source=home"
          icon="icon-tianjia" />
        <IconCount v-model:value="showCount" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="handleRefresh" />
      </div>
    </div>

    <!-- Main data table for groups -->
    <Table
      :dataSource="groupList"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      rowKey="id"
      size="small"
      :noDataSize="'small'"
      :noDataText="t('common.messages.noData')"
      @change="tableChange">
      <!-- Custom cell rendering for different columns -->
      <template #bodyCell="{ column,text, record }">
        <!-- Group name column with link to detail page -->
        <template v-if="column.dataIndex === 'name'">
          <RouterLink
            v-if="app.has('GroupDetail')"
            :to="`/organization/group/${record.id}`"
            class="text-theme-special text-theme-text-hover"
            :title="record.name">
            {{ text }}
          </RouterLink>
        </template>

        <!-- Enabled status column with badge indicator -->
        <template v-if="column.dataIndex === 'enabled'">
          <Badge
            v-if="record.enabled"
            status="success"
            :text="t('common.actions.enable')" />
          <Badge
            v-else
            status="error"
            :text="t('common.actions.disable')" />
        </template>

        <!-- Created by name column with fallback -->
        <template v-if="column.dataIndex === 'createdByName'">
          {{ text || '--' }}
        </template>

        <!-- Source column showing message -->
        <template v-if="column.dataIndex === 'source'">
          {{ text?.message }}
        </template>

        <!-- Action column with operation buttons -->
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <!-- Edit group button -->
            <ButtonAuth
              code="GroupModify"
              type="text"
              :href="`/organization/group/edit/${record.id}?source=home`"
              icon="icon-shuxie" />

            <!-- Enable/disable group button -->
            <ButtonAuth
              code="GroupEnable"
              type="text"
              :icon="record.enabled?'icon-jinyong1':'icon-qiyong'"
              :showTextIndex="record.enabled?1:0"
              @click="updateStatusConfirm(record.id,record.name,record.enabled)" />

            <!-- More actions dropdown menu -->
            <Dropdown overlayClassName="ant-dropdown-sm" placement="bottomRight">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <!-- Delete group menu item -->
                  <MenuItem
                    v-if="app.show('GroupDelete')"
                    :disabled="!app.has('GroupDelete')"
                    @click="delGroupConfirm(record.id,record.name)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('GroupDelete') }}
                  </MenuItem>

                  <!-- Associate users menu item -->
                  <MenuItem
                    v-if="app.show('GroupUserAssociate')"
                    :disabled="!record.enabled || !app.has('GroupUserAssociate')"
                    @click="openConcatUser(record)">
                    <template #icon>
                      <Icon icon="icon-zhucezhongxin" />
                    </template>
                    {{ app.getName('GroupUserAssociate') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </div>
        </template>
      </template>
    </Table>
  </PureCard>

  <!-- Async user management modal -->
  <AsyncComponent :visible="userVisible">
    <UserModal
      v-if="userVisible"
      v-model:visible="userVisible"
      :tenantId="selectedGroup?.tenantId"
      :groupId="selectedGroup?.id"
      :updateLoading="updateLoading"
      type="Group"
      @change="saveUser" />
  </AsyncComponent>
</template>
