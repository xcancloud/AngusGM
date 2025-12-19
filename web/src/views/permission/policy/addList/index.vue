<script setup lang='ts'>
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem, Tooltip } from 'ant-design-vue';
import { ButtonAuth, Card, Icon, IconRefresh, Input, modal, notification, Table } from '@xcan-angus/vue-ui';
import { app, duration } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';
import { auth } from '@/api';
import { PolicyRecordType, TableColumn } from '../types';
import { showTip } from '../utils';

import AuthModal from '@/views/permission/policy/auth/auth.vue';

/**
 * Component props interface
 */
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

// Reactive state for the component
const state = reactive<{
  loading: boolean,
  searchValue: string | undefined,
  dataSource: PolicyRecordType[]
}>({
  loading: false,
  searchValue: undefined,
  dataSource: []
});

// Pagination configuration
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10
});

/**
 * Load policy list with search and pagination
 */
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

/**
 * Reset page number and reload with debounce
 */
const resetPageNoLoad = debounce(duration.search, () => {
  pagination.current = 1;
  loadList();
});

/**
 * Handle table pagination changes
 */
const listChange = (page: any) => {
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  loadList();
};

/**
 * Toggle policy enable/disable status
 */
const toggleEnable = async (record: PolicyRecordType) => {
  const params = [{ id: record.id as string, enabled: !record.enabled }];

  if (record.enabled) {
    // Disable policy with confirmation
    modal.confirm({
      centered: true,
      title: t('common.messages.confirmTitle'),
      content: t('common.messages.confirmDisable', { name: record.name }),
      async onOk () {
        const [error] = await auth.togglePolicyEnabled(params);
        if (error) {
          return;
        }
        notification.success(t('common.messages.disableSuccess'));
        await loadList();
      }
    });
  } else {
    // Enable policy directly
    const [error] = await auth.togglePolicyEnabled(params);
    if (error) {
      return;
    }

    notification.success(t('common.messages.enableSuccess'));
    await loadList();
  }
};

/**
 * Delete policy by ID
 */
const delByIds = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('common.messages.confirmTitle'),
    content: t('common.messages.confirmDelete', { name: name }),
    async onOk () {
      const [error] = await auth.deletePolicy([id]);
      if (error) {
        return;
      }

      notification.success(t('common.messages.deleteSuccess'));

      // Adjust pagination if needed
      if (pagination.current > 1 && state.dataSource.length === 1) {
        pagination.current = pagination.current - 1;
      }
      await loadList();
    }
  });
};

// Authorization modal state
const authVisible = ref(false);
const policyId = ref('');

/**
 * Open authorization modal for a policy
 */
const grantAuth = (id: string) => {
  authVisible.value = true;
  policyId.value = id;
};

/**
 * Edit policy - emit edit event to parent
 */
const edit = (record: PolicyRecordType) => {
  emit('edit', { policyId: record.id, appId: record.appId });
};

// Table column definitions
const columns: TableColumn[] = [
  {
    key: 'id',
    title: 'ID',
    dataIndex: 'id',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'name',
    title: t('permission.policy.added.columns.name'),
    dataIndex: 'name',
    width: '10%'
  },
  {
    key: 'code',
    title: t('permission.policy.added.columns.code'),
    dataIndex: 'code',
    width: '12%'
  },
  {
    key: 'appName',
    title: t('permission.policy.added.columns.appName'),
    dataIndex: 'appName',
    width: '8%'
  },
  {
    key: 'type',
    title: t('common.columns.type'),
    dataIndex: 'type',
    width: '8%',
    customRender: ({ text }) => {
      return text?.message || t('common.table.noData');
    }
  },
  {
    key: 'description',
    title: t('common.columns.description'),
    dataIndex: 'description'
  },
  {
    key: 'enabled',
    title: t('common.status.validStatus'),
    dataIndex: 'enabled',
    width: '5%'
  },
  {
    key: 'creator',
    title: t('common.columns.creator'),
    dataIndex: 'creator',
    groupName: 'create',
    width: '9%'
  },
  {
    key: 'createdDate',
    title: t('common.columns.createdDate'),
    dataIndex: 'createdDate',
    groupName: 'create',
    hide: true,
    width: '10%'
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 130
  }
];

onMounted(() => {
  loadList();
});

defineExpose({
  load: loadList
});
</script>

<template>
  <Card class="flex-1">
    <template #title>
      <div class="flex items-center space-x-2">
        <div>{{ t('permission.policy.added.title') }}</div>

        <!-- Search input -->
        <Input
          v-model:value="state.searchValue"
          :placeholder="t('permission.policy.added.searchPlaceholder')"
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
      <!-- Refresh button -->
      <IconRefresh
        class="ml-1"
        :loading="state.loading"
        @click="loadList" />
    </template>

    <!-- Policy list table -->
    <Table
      :dataSource="state.dataSource"
      rowKey="id"
      :loading="state.loading"
      :columns="columns"
      :pagination="pagination"
      size="small"
      @change="listChange">
      <template #bodyCell="{ column, text, record }">
        <!-- Policy name column with link -->
        <template v-if="column.dataIndex === 'name'">
          <RouterLink
            v-if="app.has('PolicyDetail')"
            :to="`/permissions/policy/${record.id}?showTip=${showTip(record.id, props.defaultPolicies)}`"
            class="text-theme-special text-theme-text-hover">
            <Tooltip :title="record.name">{{ record.name }}</Tooltip>
          </RouterLink>
        </template>

        <!-- Enabled status column -->
        <template v-if="column.dataIndex === 'enabled'">
          <Badge
            v-if="record.enabled"
            status="success"
            :text="t('common.status.enabled')">
          </Badge>
          <Badge
            v-else
            status="error"
            :text="t('common.status.disabled')">
          </Badge>
        </template>

        <!-- Created by name column -->
        <template v-if="column.dataIndex === 'creator'">
          {{ text || t('common.messages.noData') }}
        </template>

        <!-- Action column -->
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <!-- Enable/Disable button -->
            <ButtonAuth
              code="PolicyEnable"
              type="text"
              :icon="record.enabled ? 'icon-jinyong1' : 'icon-qiyong'"
              :disabled="record.type.value !== 'USER_DEFINED'"
              :showTextIndex="record.enabled ? 1 : 0"
              @click="toggleEnable(record)" />

            <!-- Delete button -->
            <ButtonAuth
              code="PolicyDelete"
              type="text"
              icon="icon-lajitong"
              :disabled="record.type.value !== 'USER_DEFINED'"
              @click="delByIds(record.id, record.name || '')" />

            <!-- More actions dropdown -->
            <Dropdown placement="bottomRight">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <!-- Edit menu item -->
                  <MenuItem
                    v-if="app.show('PolicyModify')"
                    :disabled="record.type.value !== 'USER_DEFINED' || !app.has('PolicyModify')"
                    @click="edit(record)">
                    <template #icon>
                      <Icon icon="icon-shuxie" />
                    </template>
                    {{ app.getName('PolicyModify') }}
                  </MenuItem>

                  <!-- Authorize menu item -->
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

    <!-- Authorization modal -->
    <AuthModal :id="policyId" v-model:visible="authVisible" />
  </Card>
</template>
