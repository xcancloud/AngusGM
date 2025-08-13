<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  AsyncComponent, ButtonAuth, Icon, IconCount, IconRefresh, modal, notification,
  PureCard, SearchPanel, Table
} from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, app, GM, utils, Enabled } from '@xcan-angus/infra';

import { ListGroup } from './types';
import { group } from '@/api';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));

const { t } = useI18n();
const loading = ref(false);
const showCount = ref(true);
const disabled = ref(false);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true });
const total = ref(0);
const groupList = ref<ListGroup[]>([]);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const init = () => {
  loadGroupList();
};

const loadGroupList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await group.getGroupList(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  groupList.value = data.list;
  total.value = +data.total;
};

const updateStatusConfirm = (id: string, name: string, enabled: boolean) => {
  modal.confirm({
    centered: true,
    title: enabled ? t('common.actions.disable') : t('common.actions.enable'),
    content: enabled ? t('common.messages.confirmDisable', { name: name }) : t('common.messages.confirmEnable', { name: name }),
    async onOk () {
      await updateStatus(id, enabled);
    }
  });
};

const updateStatus = async (id: string, enabled: boolean) => {
  const params = [{ id: id, enabled: !enabled }];
  const [error] = await group.toggleGroupEnabled(params);
  if (error) {
    return;
  }
  notification.success(enabled ? t('common.messages.disableSuccess') : t('common.messages.enableSuccess'));
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

const searchChange = async (data: SearchCriteria[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

const tableChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

const delGroupConfirm = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('common.actions.delete'),
    content: t('common.messages.confirmDelete', { name: name }),
    async onOk () {
      await delGroup(id);
    }
  });
};

const delGroup = async (id: string) => {
  const [error] = await group.deleteGroup([id]);
  if (error) {
    return;
  }
  notification.success(t('common.messages.deleteSuccess'));
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
  disabled.value = true;
  await loadGroupList();
  disabled.value = false;
};

const userVisible = ref(false);
const selectedGroup = ref<ListGroup>();
const updateLoading = ref(false);
const openConcatUser = async (record: ListGroup): Promise<void> => {
  selectedGroup.value = record;
  userVisible.value = true;
};

const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  let reload = false;
  // 如果有删除的用户
  if (deleteUserIds.length) {
    await delGroupUser(deleteUserIds);
    reload = true;
  }
  // 如果有新增的用户
  if (_userIds.length) {
    await addGroupUser(_userIds);
    reload = true;
  }

  userVisible.value = false;
  if (reload) {
    disabled.value = true;
    await loadGroupList();
    disabled.value = false;
  }
};

const addGroupUser = async (_userIds: string[]) => {
  if (!selectedGroup.value) {
    return;
  }
  updateLoading.value = true;
  await group.addGroupUser(selectedGroup.value.id, _userIds);
  updateLoading.value = false;
};

const delGroupUser = async (_userIds: string[]) => {
  if (!selectedGroup.value) {
    return;
  }
  updateLoading.value = true;
  await group.deleteGroupUser(selectedGroup.value.id, _userIds);
  updateLoading.value = false;
};

const searchOptions = ref([
  {
    placeholder: t('group.placeholder.id'),
    valueKey: 'id',
    type: 'input' as const,
    op: 'EQUAL' as const,
    allowClear: true
  },
  {
    placeholder: t('group.placeholder.name'),
    valueKey: 'name',
    type: 'input' as const,
    allowClear: true
  },
  {
    placeholder: t('common.status.validStatus'),
    valueKey: 'enabled',
    type: 'select-enum' as const,
    enumKey: Enabled,
    allowClear: true
  },
  {
    placeholder: t('tag.placeholder.name'),
    valueKey: 'tagId',
    type: 'select' as const,
    action: `${GM}/org/tag`,
    fieldNames: { label: 'name', value: 'id' },
    showSearch: true,
    allowClear: true,
    lazy: false
  }
]);

const columns = computed(() => {
  return _columns.map((item) => ({ ...item, title: t(item.title) }));
});

const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadGroupList();
};

const _columns = [
  {
    key: 'id',
    title: 'ID',
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'name',
    title: t('common.columns.name'),
    dataIndex: 'name',
    width: '18%'
  },
  {
    key: 'code',
    title: t('common.columns.code'),
    dataIndex: 'code'
  },
  {
    key: 'enabled',
    title: t('common.status.validStatus'),
    dataIndex: 'enabled',
    width: '7%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'source',
    title: t('common.labels.source'),
    dataIndex: 'source',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'userNum',
    title: t('group.columns.userNum'),
    dataIndex: 'userNum',
    width: '7%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'createdByName',
    title: t('common.columns.createdByName'),
    dataIndex: 'createdByName',
    width: '10%'
  },
  {
    key: 'createdDate',
    title: t('common.columns.createdDate'),
    sorter: true,
    dataIndex: 'createdDate',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 130,
    align: 'center' as const
  }
];

onMounted(() => {
  init();
});
</script>
<template>
  <PureCard class="p-3.5 min-h-full">
    <Statistics
      :visible="showCount"
      :barTitle="t('statistics.metrics.newGroups')"
      dateType="YEAR"
      resource="Group"
      :router="GM"
      class="mb-3" />
    <div class="flex items-center justify-between mb-3">
      <SearchPanel :options="searchOptions" @change="searchChange" />
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
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'name'">
          <RouterLink
            v-if="app.has('GroupDetail')"
            :to="`/organization/group/${record.id}`"
            class="text-theme-special text-theme-text-hover"
            :title="record.name">
            {{ text }}
          </RouterLink>
        </template>
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
        <template v-if="column.dataIndex === 'createdByName'">
          {{ text || '--' }}
        </template>
        <template v-if="column.dataIndex === 'source'">
          {{ text?.message }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <ButtonAuth
              code="GroupModify"
              type="text"
              :href="`/organization/group/edit/${record.id}?source=home`"
              icon="icon-shuxie" />
            <ButtonAuth
              code="GroupEnable"
              type="text"
              :icon="record.enabled?'icon-jinyong1':'icon-qiyong'"
              :showTextIndex="record.enabled?1:0"
              @click="updateStatusConfirm(record.id,record.name,record.enabled)" />
            <Dropdown overlayClassName="ant-dropdown-sm" placement="bottomRight">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <MenuItem
                    v-if="app.show('GroupDelete')"
                    :disabled="!app.has('GroupDelete')"
                    @click="delGroupConfirm(record.id,record.name)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('GroupDelete') }}
                  </MenuItem>
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
  <AsyncComponent :visible="userVisible">
    <UserModal
      v-if="userVisible"
      v-model:visible="userVisible"
      :tenantId="selectedGroup?.tenantId"
      :groupId="selectedGroup?.id"
      :updateLoading="updateLoading"
      type="Group"
      @change="userSave" />
  </AsyncComponent>
</template>
