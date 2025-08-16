<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Icon, IconRefresh, Input, PureCard, Table } from '@xcan-angus/vue-ui';
import { Button } from 'ant-design-vue';
import { cookieUtils, download, duration, routerUtils } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { privLicense } from '@/api';
import { Licensed, SearchParams } from './types';

const { t } = useI18n();

const loading = ref(false);
const disabled = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const init = () => {
  loadLicensedList();
};

const licensedList = ref<Licensed[]>([]);
const loadLicensedList = async (): Promise<void> => {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await privLicense.getLicenseInPriv(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  licensedList.value = data.list;
  total.value = +data.total;
};

const tableChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await loadLicensedList();
  disabled.value = false;
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  if (!value) {
    params.value.filters = [];
  } else {
    params.value.filters = [{ key: 'licenseNo', op: 'MATCH_END', value: value }];
  }
  disabled.value = true;
  await loadLicensedList();
  disabled.value = false;
});

const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadLicensedList();
};

const downloadLicense = async (licenseNo: string): Promise<void> => {
  const token = cookieUtils.getTokenInfo().access_token;
  const url = routerUtils.getGMApiUrl(`/store/license/${licenseNo}/download?access_token=${token}`);
  await download(url);
};

const columns = [
  {
    title: t('priv.columns.goodsId'),
    dataIndex: 'goodsId',
    groupName: 'product',
    width: '10%',
    hide: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('priv.columns.goodsName'),
    dataIndex: 'goodsName',
    groupName: 'product',
    width: '10%'
  },
  {
    title: t('priv.columns.goodsType'),
    dataIndex: 'goodsType',
    customRender: ({ text }) => text?.message,
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('priv.columns.goodsVersion'),
    dataIndex: 'goodsVersion',
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('priv.columns.goodsEditionType'),
    dataIndex: 'goodsEditionType',
    groupName: 'product',
    hide: true,
    customRender: ({ text }) => text?.message,
    width: '10%'
  },
  {
    title: t('priv.columns.licenseNo'),
    dataIndex: 'licenseNo',
    width: '9%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('priv.columns.installEditionType'),
    dataIndex: 'installEditionType',
    customRender: ({ text }) => text?.message || '--',
    width: '7%'
  },
  {
    title: t('priv.columns.provider'),
    dataIndex: 'provider',
    groupName: 'issuer',
    width: '16%'
  },
  {
    title: t('priv.columns.holder'),
    dataIndex: 'holder',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('priv.columns.issuer'),
    dataIndex: 'issuer',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('priv.columns.subject'),
    dataIndex: 'subject',
    width: '16%',
    groupName: 'issuer',
    hide: true,
    customRender: ({ record, text }) => record.main ? text : text + t('priv.messages.main')
  },
  {
    title: t('priv.columns.signature'),
    dataIndex: 'signature',
    width: '17%',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('priv.columns.orderNo'),
    dataIndex: 'orderNo',
    width: '8%',
    customRender: ({ text }) => text || '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('priv.columns.expired'),
    dataIndex: 'expired',
    width: '5%',
    customRender: ({ text }) => text ? t('priv.messages.yes') : t('priv.messages.no'),
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.expired ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: t('priv.columns.endDate'),
    dataIndex: 'endDate',
    width: '9%',
    sorter: true,
    groupName: 'date'
  },
  {
    title: t('priv.columns.beginDate'),
    dataIndex: 'beginDate',
    width: '9%',
    sorter: true,
    groupName: 'date',
    hide: true
  },
  {
    title: t('priv.columns.issuedDate'),
    dataIndex: 'issuedDate',
    groupName: 'date',
    width: '9%',
    sorter: true,
    hide: true
  },
  {
    title: t('priv.columns.revoke'),
    dataIndex: 'revoke',
    width: '5%',
    customRender: ({ text }) => text ? t('priv.messages.yes') : t('priv.messages.no'),
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.revoke ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: t('common.actions.operation'),
    dataIndex: 'action',
    align: 'center',
    width: '5%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];

onMounted(() => {
  init();
});
</script>
<template>
  <PureCard class="p-3.5 min-h-full">
    <div class="mb-2 flex justify-between items-center">
      <Input
        class="w-52"
        :placeholder="t('priv.placeholder.searchLicenseNo')"
        @change="handleSearch" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        @click="handleRefresh" />
    </div>
    <Table
      :dataSource="licensedList"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <Button
            size="small"
            type="text"
            @click="downloadLicense(record.licenseNo)">
            <Icon icon="icon-xiazai" />
            {{ t('priv.messages.download') }}
          </Button>
        </template>
      </template>
    </Table>
  </PureCard>
</template>
