<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ButtonAuth, IconRefresh, Input, PureCard, Table } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, cookieUtils, download, duration, ESS } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { license } from '@/api';
import { Licensed } from './types';

const { t } = useI18n();

const loading = ref(false);
const disabled = ref(false);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true });
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
/**
 * <p>Load licensed list from cloud with current params.</p>
 * <p>Updates table data and total count.</p>
 */
const loadLicensedList = async (): Promise<void> => {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await license.getLicenseInCloud(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  licensedList.value = data.list;
  total.value = +data.total;
};

/**
 * <p>Handle table pagination/sorting change.</p>
 */
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

/**
 * <p>Debounced search by license number (suffix match).</p>
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = (event.target.value || '').trim();
  if (!value) {
    params.value.filters = [];
  } else {
    params.value.filters = [{ key: 'licenseNo', op: SearchCriteria.OpEnum.MatchEnd, value: value }];
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

/**
 * <p>Download license file by license number using current access token.</p> TODO 移动到 api
 */
const downloadLicense = async (licenseNo: string): Promise<void> => {
  const token = cookieUtils.getTokenInfo().access_token;
  await download(`${ESS}/store/license/${licenseNo}/download?access_token=${token}`);
};

const columns = [
  {
    title: t('cloud.columns.goodsId'),
    dataIndex: 'goodsId',
    key: 'goodsId',
    groupName: 'product',
    width: '10%',
    hide: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('cloud.columns.goodsName'),
    dataIndex: 'goodsName',
    key: 'goodsName',
    groupName: 'product',
    width: '10%'
  },
  {
    title: t('cloud.columns.goodsType'),
    dataIndex: 'goodsType',
    key: 'goodsType',
    customRender: ({ text }) => text?.message,
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('cloud.columns.goodsVersion'),
    dataIndex: 'goodsVersion',
    key: 'goodsVersion',
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('cloud.columns.goodsEditionType'),
    dataIndex: 'goodsEditionType',
    key: 'goodsEditionType',
    groupName: 'product',
    hide: true,
    customRender: ({ text }) => text?.message,
    width: '10%'
  },
  {
    title: t('cloud.columns.licenseNo'),
    dataIndex: 'licenseNo',
    key: 'licenseNo',
    width: '9%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('cloud.columns.installEditionType'),
    dataIndex: 'installEditionType',
    key: 'installEditionType',
    customRender: ({ text }) => text?.message || '--',
    width: '7%'
  },
  {
    title: t('cloud.columns.provider'),
    dataIndex: 'provider',
    key: 'provider',
    groupName: 'issuer',
    width: '16%'
  },
  {
    title: t('cloud.columns.holder'),
    dataIndex: 'holder',
    key: 'holder',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('cloud.columns.issuer'),
    dataIndex: 'issuer',
    key: 'issuer',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('cloud.columns.subject'),
    dataIndex: 'subject',
    key: 'subject',
    width: '16%',
    groupName: 'issuer',
    hide: true,
    customRender: ({ record, text }) => record.main ? text : text + t('cloud.messages.main')
  },
  {
    title: t('cloud.columns.signature'),
    dataIndex: 'signature',
    key: 'signature',
    width: '17%',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('cloud.columns.orderNo'),
    dataIndex: 'orderNo',
    key: 'orderNo',
    width: '8%',
    customRender: ({ text }) => text || '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('cloud.columns.expired'),
    dataIndex: 'expired',
    key: 'expired',
    width: '5%',
    customRender: ({ text }) => text ? t('cloud.messages.yes') : t('cloud.messages.no'),
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.expired ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: t('cloud.columns.expiredDate'),
    dataIndex: 'expiredDate',
    key: 'expiredDate',
    width: '9%',
    sorter: true,
    groupName: 'date',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('cloud.columns.issuedDate'),
    dataIndex: 'issuedDate',
    key: 'issuedDate',
    groupName: 'date',
    width: '9%',
    sorter: true,
    hide: true
  },
  {
    title: t('cloud.columns.revoke'),
    dataIndex: 'revoke',
    key: 'revoke',
    width: '5%',
    customRender: ({ text }) => text ? t('cloud.messages.yes') : t('cloud.messages.no'),
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.revoke ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: t('cloud.columns.action'),
    dataIndex: 'action',
    key: 'action',
    align: 'center' as const,
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
        :placeholder="t('cloud.placeholder.searchLicenseNo')"
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
      noDataText=""
      noDataSize="small"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="download"
            size="small"
            type="text"
            icon="icon-xiazai"
            @click="downloadLicense(record.licenseNo)" />
        </template>
      </template>
    </Table>
  </PureCard>
</template>
