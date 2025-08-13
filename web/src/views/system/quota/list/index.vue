<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, IconRefresh, PureCard, Select, Table } from '@xcan-angus/vue-ui';
import { app, appContext } from '@xcan-angus/infra';

import { Quota } from '../types';
import { setting } from '@/api';

const EditQuota = defineAsyncComponent(() => import('@/views/system/quota/edit/index.vue'));

const { t } = useI18n();

const editionType = ref<string>();
const appCode = ref(undefined);
const loading = ref(false);
const total = ref(0);
const params = ref<{ pageNo: number, pageSize: number, appCode?: string }>({
  pageNo: 1,
  pageSize: 10
});

const tableList = ref<Quota[]>([]);
const init = async () => {
  editionType.value = appContext.getEditionType();
  await loadList();
};

const disabled = ref(false);
const loadList = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const _params = appCode.value ? { ...params.value, appCode: appCode.value } : params.value;
  const [error, { data = { list: [], total: 0 } }] = await setting.getTenantQuotaList(_params);
  loading.value = false;
  if (error) {
    return;
  }

  tableList.value = data.list;
  total.value = +data.total;
};

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const changePagination = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadList();
  disabled.value = false;
};

const appChange = async () => {
  disabled.value = true;
  await loadList();
  disabled.value = false;
};

const visible = ref(false);
const currQuota = ref<Quota>();
const openEditModal = (rowData: Quota) => {
  currQuota.value = rowData;
  visible.value = true;
};

const editSuccess = async () => {
  disabled.value = true;
  await loadList();
  disabled.value = false;
};

const options = ref<{ label: string; value: string }[]>([]);
const loadAppList = async () => {
  const [, { data = [] }] = await setting.getTenantQuotaApp();
  options.value = data?.map(item => ({ label: item, value: item }));
};

onMounted(() => {
  init();
  loadAppList();
});

const columnsCloud = [
  {
    key: 'value',
    title: t('quota.columns.resourceName'),
    dataIndex: 'value',
    width: '20%'
  },
  {
    key: 'name',
    title: t('quota.columns.quotaDescription'),
    dataIndex: 'name'
  },
  {
    key: 'appCode',
    title: t('quota.columns.appCode'),
    dataIndex: 'appCode',
    width: '10%'
  },
  {
    key: 'quota',
    title: t('quota.columns.currentQuota'),
    dataIndex: 'quota',
    width: '10%'
  },
  {
    key: 'default0',
    title: t('quota.columns.defaultQuota'),
    dataIndex: 'default0',
    width: '10%'
  },
  {
    key: 'max',
    title: t('quota.columns.allowUpperLimit'),
    dataIndex: 'max',
    width: '10%'
  }
];

const columnsPrivate = [
  {
    key: 'name',
    title: t('quota.columns.resourceName'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'value',
    title: t('quota.columns.quotaKey'),
    dataIndex: 'value',
    width: '20%'
  },
  {
    key: 'appCode',
    title: t('quota.columns.appCode'),
    dataIndex: 'appCode',
    width: '10%'
  },
  {
    key: 'quota',
    title: t('quota.columns.currentQuota'),
    dataIndex: 'quota',
    width: '10%'
  },
  {
    key: 'default0',
    title: t('quota.columns.defaultQuota'),
    dataIndex: 'default0',
    width: '10%'
  },
  {
    key: 'max',
    title: t('quota.columns.allowUpperLimit'),
    dataIndex: 'max',
    width: '10%'
  },
  {
    key: 'allowChange',
    title: t('quota.columns.allowChange'),
    dataIndex: 'allowChange',
    width: '10%'
  },
  {
    key: 'action',
    title: t('quota.columns.operation'),
    dataIndex: 'action',
    width: '10%'
  }
];

const columns = computed(() => {
  if (editionType.value === 'CLOUD_SERVICE') {
    return columnsCloud;
  }

  return columnsPrivate;
});
</script>
<template>
  <PureCard class="flex-1 p-3.5">
    <div class="mb-2 flex justify-between items-center">
      <Select
        v-model:value="appCode"
        showSearch
        allowClear
        :placeholder="t('quota.placeholder.selectApp')"
        :options="options"
        internal
        size="small"
        class="w-60"
        @change="appChange" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        class="ml-2"
        @click="loadList" />
    </div>
    <Table
      :loading="loading"
      :dataSource="tableList"
      :columns="columns"
      :pagination="pagination"
      size="small"
      rowKey="id"
      @change="changePagination">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'name'">
          {{ text.message }}
        </template>
        <template v-if="column.dataIndex === 'value'">
          {{ record.name.value }}
        </template>
        <template v-if="column.dataIndex === 'allowChange'">
          {{ text ? t('common.status.yes') : t('common.status.no') }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <template v-if="record.allowChange && app.has('ResourceQuotaModify')">
            <a class="text-theme-text-hover cursor-pointer" @click="openEditModal(record)">{{
              app.getName('ResourceQuotaModify') }}</a>
          </template>
          <template v-else>
            <a class="text-theme-sub-content">{{ app.getName('ResourceQuotaModify') }}</a>
          </template>
        </template>
      </template>
    </Table>
    <AsyncComponent :visible="visible">
      <EditQuota
        v-if="currQuota"
        v-model:visible="visible"
        :default0="+currQuota.default0"
        :max="+currQuota.max"
        :min="+currQuota.min"
        :name="currQuota.name.value"
        @ok="editSuccess" />
    </AsyncComponent>
  </PureCard>
</template>
