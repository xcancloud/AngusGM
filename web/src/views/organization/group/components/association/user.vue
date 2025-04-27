<script setup lang='ts'>
import { ref, computed, defineAsyncComponent, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Hints, Table, AsyncComponent, Icon, Input, Image, IconRefresh, ButtonAuth } from '@xcan/design';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan/configs';

import { utils } from '@xcan-angus/tools';
import { User, SearchParams } from './PropsType';

import {group} from '@/api';

const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));

interface Props {
  groupId: string;
  enabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  groupId: undefined,
  enabled: false
});

const { t } = useI18n();

const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const count = ref(0);
// 条件查询不更新count
const isContUpdate = ref(true);
const userList = ref<User[]>([]);

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
  if (isContUpdate.value) {
    count.value = +data.total;
  }
};

// 关联用户
const userVisible = ref(false);
const relevancyUser = () => {
  userVisible.value = true;
};

const updateLoading = ref(false);
const isRefresh = ref(false);
const disabled = ref(false);
const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  // 如果有删除的用户
  if (deleteUserIds.length) {
    await delGroupUser(deleteUserIds, 'Modal');
  }

  // 如果有新增的用户
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

// 添加组关联用户
const addGroupUser = async (_userIds: string[]) => {
  updateLoading.value = true;
  const [error] = await group.addGroupUser(props.groupId, _userIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

// 添删除组关联用户
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

  // 要求表格操作不影响刷新图标
  if (type === 'Table') {
    disabled.value = true;
    await loadGroupUserList();
    disabled.value = false;
  }
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'fullName', op: 'MATCH_END', value: value }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  isContUpdate.value = false;
  await loadGroupUserList();
  isContUpdate.value = true;
  disabled.value = false;
});

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadGroupUserList();
  disabled.value = false;
};

onMounted(() => {
  loadGroupUserList();
});
const columns = [
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('userName1'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];
</script>
<template>
  <Hints class="mb-1" :text="t('groupTip1')+t(`每个组最多允许关联200个用户，当前组已关联${count}个用户。`)" />
  <div class="flex items-center justify-between mb-2">
    <Input
      :placeholder="t('groupPlaceholder2')"
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
