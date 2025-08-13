<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Image, Input, Table } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { PageQuery, duration, utils, SearchCriteria } from '@xcan-angus/infra';

import { User } from '../types';

import { group } from '@/api';

const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));

/**
 * Props from parent: current group id and enabled state for operations.
 */
interface Props {
  groupId: string;
  enabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  groupId: undefined,
  enabled: false
});

const { t } = useI18n();

/** Loading and query states */
const loading = ref(false);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const count = ref(0);
const isCountUpdate = ref(true);
const userList = ref<User[]>([]);

/**
 * Fetch group associated users list using current pagination/filters.
 */
const loadGroupUserList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await group.getGroupUser(props.groupId, params.value);
  loading.value = false;
  if (error) {
    return;
  }
  userList.value = data?.list || [];
  total.value = +data?.total;
  if (isCountUpdate.value) {
    count.value = +data.total;
  }
};

const userVisible = ref(false);
/** Open user association modal */
const relevancyUser = () => {
  userVisible.value = true;
};

const updateLoading = ref(false);
const isRefresh = ref(false);
const disabled = ref(false);

/**
 * Handle modal changes: delete first if needed, then add new users, and refresh when flagged.
 */
const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  if (deleteUserIds.length) {
    await delGroupUser(deleteUserIds, 'Modal');
  }

  if (_userIds.length) {
    await addGroupUser(_userIds);
  }

  userVisible.value = false;
  updateLoading.value = false;

  if (isRefresh.value) {
    disabled.value = true;
    await loadGroupUserList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

/** Add selected users to current group. */
const addGroupUser = async (_userIds: string[]) => {
  updateLoading.value = true;
  const [error] = await group.addGroupUser(props.groupId, _userIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

/** Delete selected users from current group. */
const delGroupUser = async (_userIds: string[], type?: 'Table' | 'Modal') => {
  if (loading.value) {
    return;
  }
  const [error] = await group.deleteGroupUser(props.groupId, _userIds);
  if (type === 'Table') {
    updateLoading.value = false;
  }
  if (error) {
    return;
  }

  if (type === 'Modal') {
    isRefresh.value = true;
  }

  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  if (type === 'Table') {
    disabled.value = true;
    await loadGroupUserList();
    disabled.value = false;
  }
};

/**
 * Debounced search by full name. Keeps count unchanged during condition queries.
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'fullName', op: SearchCriteria.OpEnum.MatchEnd, value: value }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  isCountUpdate.value = false;
  await loadGroupUserList();
  isCountUpdate.value = true;
  disabled.value = false;
});

/** Pagination object consumed by Table */
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

/** Table pagination change handler */
const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadGroupUserList();
  disabled.value = false;
};

/** Initial data load */
onMounted(() => {
  loadGroupUserList();
});
const columns = [
  {
    key: 'userId',
    title: t('user.columns.assocUser.id'),
    dataIndex: 'userId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'fullName',
    title: t('user.columns.assocUser.name'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    key: 'createdByName',
    title: t('user.columns.assocUser.createdByName'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    key: 'createdDate',
    title: t('user.columns.assocUser.createdDate'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: '15%',
    align: 'center' as const
  }
];
</script>
<template>
  <Hints class="mb-1" :text="t('group.disabledTip') + t('group.groupUserQuotaTip', { num: count })" />
  <div class="flex items-center justify-between mb-2">
    <Input
      :placeholder="t('user.placeholder.search')"
      class="w-60"
      allowClear
      @change="handleSearch">
      <template #suffix>
        <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
      </template>
    </Input>
    <div class="flex space-x-2 items-center">
      <ButtonAuth
        code="GroupUserAssociate"
        type="primary"
        icon="icon-tianjia"
        :disabled="!props.enabled || total>=200"
        @click="relevancyUser" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        @click="loadGroupUserList" />
    </div>
  </div>
  <Table
    :dataSource="userList"
    rowKey="id"
    :loading="loading"
    :columns="columns"
    :pagination="pagination"
    size="small"
    :noDataSize="'small'"
    :noDataText="t('common.messages.noData')"
    @change="tableChange">
    <template #bodyCell="{ column,text, record }">
      <template v-if="column.dataIndex === 'fullName'">
        <div class="flex items-center" :title="text">
          <Image
            :src="record.avatar"
            type="avatar"
            class="w-5 h-5 rounded-full mr-2" />
          {{ text }}
        </div>
      </template>
      <template v-if="column.dataIndex === 'action'">
        <ButtonAuth
          code="GroupUserUnassociate"
          type="text"
          icon="icon-quxiao"
          :disabled="!props.enabled"
          @click="delGroupUser([record.userId],'Table')" />
      </template>
    </template>
  </Table>
  <AsyncComponent :visible="userVisible">
    <UserModal
      v-if="userVisible"
      v-model:visible="userVisible"
      :groupId="props.groupId"
      :updateLoading="updateLoading"
      type="Group"
      @change="userSave" />
  </AsyncComponent>
</template>
