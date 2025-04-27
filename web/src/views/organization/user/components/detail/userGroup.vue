<script setup lang='ts'>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Hints, Table, Input, Icon, AsyncComponent, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, utils } from '@xcan-angus/tools';

import { UserGroup, SearchParams } from './PropsType';
import { user } from '@/api';

const GroupModal = defineAsyncComponent(() => import('@/components/GroupModal/index.vue'));

interface Props {
  userId: string;
  hasAuth: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  userId: undefined,
  hasAuth: false
});

const { t } = useI18n();

const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const count = ref(0);
const isContUpdate = ref(true);
const dataList = ref<UserGroup[]>([]);
const loadUserGroup = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserGroup(props.userId, params.value);
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

const groupVisible = ref(false);
const addGroup = () => {
  groupVisible.value = true;
};

const updateLoading = ref(false);
const isRefresh = ref(false);
const disabled = ref(false); // 刷新按钮禁用旋转
const groupSave = async (_groupIds: string[], _groups: { id: string, name: string }[], deleteGroupIds: string[]) => {
  if (deleteGroupIds.length) {
    await delUserGroup(deleteGroupIds, 'Modal');
  }

  if (_groupIds.length) {
    await addUserGroup(_groupIds);
  }

  groupVisible.value = false;
  updateLoading.value = false;

  if (isRefresh.value) {
    disabled.value = true;
    await loadUserGroup();
    disabled.value = false;
    isRefresh.value = false;
  }
};

const addUserGroup = async (_groupIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserGroup(props.userId, _groupIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

const delUserGroup = async (_groupIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserGroup(props.userId, _groupIds);
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
    await loadUserGroup();
    disabled.value = false;
  }
};

const handleCancel = async (id) => {
  delUserGroup([id], 'Table');
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'groupName', op: 'MATCH_END', value }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  isContUpdate.value = false;
  await loadUserGroup();
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

const handleChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadUserGroup();
  disabled.value = false;
};

onMounted(() => {
  loadUserGroup();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'groupId',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name'),
    dataIndex: 'groupName',
    width: '15%'
  },
  {
    title: t('code'),
    dataIndex: 'groupCode',
    width: '15%'
  },
  {
    title: t('description'),
    dataIndex: 'groupRemark'
  },
  {
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '13%'
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: '6%',
    align: 'center'
  }
];
const hintTip = t(`每个用户最多允许关联200个组，当前用户已关联${count.value}个组。`);
</script>
<template>
  <div>
    <Hints :text="hintTip" class="mb-1" />
    <div class="flex items-center justify-between mb-2">
      <Input
        placeholder="查询组名称"
        class="w-60"
        size="small"
        allowClear
        @change="handleSearch">
        <template #suffix>
          <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
        </template>
      </Input>
      <div class="flex space-x-2 items-center">
        <ButtonAuth
          code="UserGroupAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="hasAuth || total>=200"
          @click="addGroup" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserGroup" />
      </div>
    </div>
    <Table
      size="small"
      rowKey="id"
      :loading="loading"
      :dataSource="dataList"
      :columns="columns"
      :pagination="pagination"
      @change="handleChange">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'groupName'">
          <div class="flex items-center">
            <Icon icon="icon-zu1" class="text-4 mr-2" />
            <div class="w-full truncate" :title="text">{{ text }}</div>
          </div>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserGroupUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.groupId)" />
        </template>
      </template>
    </Table>
  </div>
  <AsyncComponent :visible="groupVisible">
    <GroupModal
      v-if="groupVisible"
      v-model:visible="groupVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="groupSave" />
  </AsyncComponent>
</template>
