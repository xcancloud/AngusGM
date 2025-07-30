<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ButtonAuth, IconRefresh, Input, PureCard, Table } from '@xcan-angus/vue-ui';
import { cookieUtils, ESS, duration, download } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { license } from '@/api';
import { Licensed, SearchParams } from './PropsType';

const { t } = useI18n();

const loading = ref(false);
const disabled = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true });
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
  const [error, { data = { list: [], total: 0 } }] = await license.getLicenseInCloud(params.value);
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
  const token = cookieUtils.get('access_token');
  download(`${ESS}/store/license/${licenseNo}/download?access_token=${token}`); // TODO 移到api
};

const columns = [
  {
    title: t('商品ID'),
    dataIndex: 'goodsId',
    groupName: 'product',
    width: '10%',
    hide: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('商品名称'),
    dataIndex: 'goodsName',
    groupName: 'product',
    width: '10%'
  },
  {
    title: t('商品类型'),
    dataIndex: 'goodsType',
    customRender: ({ text }) => text?.message,
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('商品版本'),
    dataIndex: 'goodsVersion',
    groupName: 'product',
    width: '10%',
    hide: true
  },
  {
    title: t('商品版本类型'),
    dataIndex: 'goodsEditionType',
    groupName: 'product',
    hide: true,
    customRender: ({ text }) => text?.message,
    width: '10%'
  },
  {
    title: t('许可编号'),
    dataIndex: 'licenseNo',
    width: '9%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('适用版本类型'),
    dataIndex: 'installEditionType',
    customRender: ({ text }) => text?.message || '--',
    width: '7%'
  },
  {
    title: t('提供者'),
    dataIndex: 'provider',
    groupName: 'issuer',
    width: '16%'
  },
  {
    title: t('持有者'),
    dataIndex: 'holder',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('发布者'),
    dataIndex: 'issuer',
    groupName: 'issuer',
    width: '16%',
    hide: true
  },
  {
    title: t('主题'),
    dataIndex: 'subject',
    width: '16%',
    groupName: 'issuer',
    hide: true,
    customRender: ({ record, text }) => record.main ? text : text + '(主)'
  },
  {
    title: t('许可签名'),
    dataIndex: 'signature',
    width: '17%',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('订单号'),
    dataIndex: 'orderNo',
    width: '8%',
    customRender: ({ text }) => text || '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('是否到期'),
    dataIndex: 'expired',
    width: '5%',
    customRender: ({ text }) => text ? '是' : '否',
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.expired ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: t('到期时间'),
    dataIndex: 'expiredDate',
    width: '9%',
    sorter: true,
    groupName: 'date',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('发行日期'),
    dataIndex: 'issuedDate',
    groupName: 'date',
    width: '9%',
    sorter: true,
    hide: true
  },
  {
    title: t('是否注销'),
    dataIndex: 'revoke',
    width: '5%',
    customRender: ({ text }) => text ? '是' : '否',
    customCell: (record) => {
      return { style: `min-width:60px;color:${record.revoke ? 'rgba(245,34,45,1);' : 'rgba(82,196,26,1);'}` };
    }
  },
  {
    title: '操作',
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
        placeholder="查询许可编号"
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
