<script setup lang='ts'>
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem, Tooltip } from 'ant-design-vue';
import { ButtonAuth, Card, Icon, IconRefresh, Input, modal, notification, Table } from '@xcan-angus/vue-ui';
import { app, duration } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { auth } from '@/api';

import AuthModal from '@/views/permission/policy/components/auth/auth.vue';

interface PolicyRecordType {
  id: string | undefined,
  name: string | undefined,
  appId: string | number | undefined,
  appName: string | undefined,
  createdByName: string | undefined,
  createdDate: string | undefined,
  type: { value: 'PRE_DEFINED' | 'USER_DEFINED', message: string },
  enabled: boolean,
}

interface Props {
  defaultPolicies: string[];
}

const props = withDefaults(defineProps<Props>(), {
  defaultPolicies: () => ([])
});

const { t } = useI18n();
const emit = defineEmits<{
  (e: 'edit', item: { policyId: string | number | undefined, appId: string | number | undefined }): void
}>();

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('permissionsStrategy.added.columns.name'),
    dataIndex: 'name',
    width: '10%'
  },
  {
    title: '策略编码',
    dataIndex: 'code',
    width: '12%'
  },
  {
    title: t('permissionsStrategy.added.columns.appName'),
    dataIndex: 'appName',
    width: '8%'
  },
  {
    title: '类型',
    dataIndex: 'type',
    width: '8%',
    customRender: ({ text }) => {
      return text?.message || '--';
    }
  },
  {
    title: '描述',
    dataIndex: 'description'
  },
  {
    title: t('permissionsStrategy.added.columns.enabled'),
    dataIndex: 'enabled',
    width: '5%'
  },
  {
    title: t('permissionsStrategy.added.columns.fullName'),
    dataIndex: 'createdByName',
    groupName: 'create',
    width: '9%'
  },
  {
    title: t('permissionsStrategy.added.columns.createdDate'),
    dataIndex: 'createdDate',
    groupName: 'create',
    hide: true,
    width: '10%'
  },
  {
    title: t('permissionsStrategy.added.columns.action'),
    dataIndex: 'action',
    width: 130
  }
];

const state = reactive<{
  loading: boolean,
  searchValue: string | undefined,
  dataSource: PolicyRecordType[]
}>({
  loading: false,
  searchValue: undefined,
  dataSource: []
});

// 分页参数
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10
});

const loadList = async () => {
  const params: {
    pageNo: number,
    pageSize: number,
    filters: { key: string, op: string, value: string }[],
    fullTextSearch: boolean
  } = {
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    filters: [],
    fullTextSearch: true
  };
  if (state.searchValue) {
    params.filters = [{ key: 'name', op: 'MATCH_END', value: state.searchValue }];
  }
  state.loading = true;
  const [error, res] = await auth.getPolicyList(params);
  state.loading = false;
  if (error) {
    return;
  }

  state.dataSource = res.data.list || [];
  pagination.total = +res.data.total || 0;
};

// 重置页码查询
const resetPageNoLoad = debounce(duration.search, () => {
  pagination.current = 1;
  loadList();
});

// 表格分页发生变化方法
const listChange = (page) => {
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  loadList();
};

// 启用、禁用权限策略
const toggleEnable = async (record: PolicyRecordType) => {
  const params = [{ id: record.id as string, enabled: !record.enabled }];
  if (record.enabled) {
    modal.confirm({
      centered: true,
      title: t('confirmTitle'),
      content: t('userTip6', { name: record.name }),
      async onOk () {
        const [error] = await auth.togglePolicyEnabled(params);
        if (error) {
          return;
        }
        notification.success(t('permissionsStrategy.added.disableSuccess'));
        loadList();
      }
    });
  } else {
    const [error] = await auth.togglePolicyEnabled(params);
    if (error) {
      return;
    }

    notification.success(t('permissionsStrategy.added.enableSuccess'));
    loadList();
  }
};

// 删除权限策略
const delByIds = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('confirmTitle'),
    content: t('userTip4', { name: name }),
    async onOk () {
      const [error] = await auth.deletePolicy([id]);
      if (error) {
        return;
      }

      notification.success(t('permissionsStrategy.added.delSuccess'));
      if (pagination.current > 1 && state.dataSource.length === 1) {
        pagination.current = pagination.current - 1;
      }
      loadList();
    }
  });
};

const authVisible = ref(false);
const policyId = ref('');
const grantAuth = (id: string) => {
  authVisible.value = true;
  policyId.value = id;
};

// 修改策略
const edit = (record: PolicyRecordType) => {
  emit('edit', { policyId: record.id, appId: record.appId });
};

onMounted(() => {
  loadList();
});

defineExpose({
  load: loadList
});

const showTip = (id: string) => {
  return props.defaultPolicies.includes(id) ? 1 : 0;
};
</script>

<template>
  <Card class="flex-1">
    <template #title>
      <div class="flex items-center space-x-2">
        <div>{{ t('permissionsStrategy.added.title') }}</div>
        <Input
          v-model:value="state.searchValue"
          :placeholder="t('permissionsStrategy.added.searchPlaceholder')"
          class="w-70"
          size="small"
          allowClear
          @change="resetPageNoLoad">
          <template #suffix>
            <Icon
              icon="icon-sousuo"
              class="text-3 leading-3 text-theme-content cursor-pointer"
              @click="resetPageNoLoad" />
          </template>
        </Input>
      </div>
    </template>

    <template #rightExtra>
      <IconRefresh
        class="ml-1"
        :loading="state.loading"
        @click="loadList" />
    </template>
    <Table
      :dataSource="state.dataSource"
      :rowKey="(record:any) => record.id"
      :loading="state.loading"
      :columns="columns"
      :pagination="pagination"
      size="small"
      @change="listChange">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'name'">
          <RouterLink
            v-if="app.has('PolicyDetail')"
            :to="`/permissions/policy/${record.id}?showTip=${showTip(record.id)}`"
            class="text-theme-special text-theme-text-hover">
            <Tooltip :title="record.name">{{ record.name }}</Tooltip>
          </RouterLink>
        </template>
        <template v-if="column.dataIndex === 'enabled'">
          <Badge
            v-if="record.enabled"
            status="success"
            :text="t('permissionsStrategy.added.enable')">
          </Badge>
          <Badge
            v-else
            status="error"
            :text="t('permissionsStrategy.added.disable')">
          </Badge>
        </template>
        <template v-if="column.dataIndex === 'createdByName'">
          {{ text || '--' }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <ButtonAuth
              code="PolicyEnable"
              type="text"
              :icon="record.enabled?'icon-jinyong1':'icon-qiyong'"
              :disabled="record.type.value !== 'USER_DEFINED'"
              :showTextIndex="record.enabled?1:0"
              @click="toggleEnable(record)" />
            <ButtonAuth
              code="PolicyDelete"
              type="text"
              icon="icon-lajitong"
              :disabled="record.type.value !== 'USER_DEFINED'"
              @click="delByIds(record.id,record.name)" />
            <Dropdown
              placement="bottomRight">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <MenuItem
                    v-if="app.show('PolicyModify')"
                    :disabled="record.type.value !== 'USER_DEFINED'||!app.has('PolicyModify')"
                    @click="edit(record)">
                    <template #icon>
                      <Icon icon="icon-shuxie" />
                    </template>
                    {{ app.getName('PolicyModify') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('Authorize')"
                    :disabled="!app.has('Authorize')"
                    @click="grantAuth(record.id)">
                    <template #icon>
                      <Icon icon="icon-quanxiancelve" />
                    </template>
                    {{ app.getName('Authorize') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </div>
        </template>
      </template>
    </Table>
    <AuthModal :id="policyId" v-model:visible="authVisible" />
  </Card>
</template>
