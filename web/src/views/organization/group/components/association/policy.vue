<script setup lang='ts'>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Popover } from 'ant-design-vue';
import { Input, Hints, Icon, Table, IconRefresh, ButtonAuth } from '@xcan/design';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan/configs';

import { auth } from '@/api';
import { utils } from '@xcan-angus/tools';
import { SearchParams } from './PropsType';

const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

interface Props {
  groupId: string;
  notify: number;
  enabled: boolean;
  policyName: string;
}

const props = withDefaults(defineProps<Props>(), {
  groupId: '',
  notify: 0,
  enabled: false,
  policyName: ''
});

const { t } = useI18n();

const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const handleChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  await getGroupPolicy();
};

const dataList = ref([]);

const getGroupPolicy = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  disabled.value = true;
  const [error, { data = { list: [], total: 0 } }] = await auth.getGroupPolicy(props.groupId, params.value);
  loading.value = false;
  disabled.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = data.total;
};

// 关联策略
const policyVisible = ref(false);
const addPolicy = () => {
  policyVisible.value = true;
};

const updateLoading = ref(false);
const disabled = ref(false);
const policySave = async (addIds: string[]) => {
  if (addIds.length) {
    await addGroupPolicy(addIds);
  }
  policyVisible.value = false;
};

// 添加策略
const addGroupPolicy = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await auth.addGroupPolicy(props.groupId, _addIds);
  updateLoading.value = false;
  if (error) {
    return;
  }
  await getGroupPolicy();
};

const cancelLoading = ref(false);
const handleCancel = async (id) => {
  if (cancelLoading.value) {
    return;
  }
  cancelLoading.value = true;
  const [error] = await auth.deleteGroupPolicy(props.groupId, [id]);
  cancelLoading.value = false;
  if (error) {
    return;
  }
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
  await getGroupPolicy();
};

const handleSearch = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'name', op: 'MATCH_END', value: value }];
  } else {
    params.value.filters = [];
  }
  getGroupPolicy();
});

onMounted(() => {
  getGroupPolicy();
});

const columns = [
  {
    title: '策略ID',
    dataIndex: 'id',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('策略名称'),
    dataIndex: 'name',
    ellipsis: true
  },
  {
    title: t('策略编码'),
    dataIndex: 'code',
    width: '22%'
  },
  {
    title: t('授权来源'),
    dataIndex: 'grantStage',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('授权人'),
    dataIndex: 'authByName',
    width: '13%'
  },
  {
    title: t('授权时间'),
    dataIndex: 'authDate',
    width: '19%',
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
  <Hints class="mb-1" :text="t('禁用状态下不允许关联策略。')" />
  <div class="flex items-center justify-between mb-2">
    <Input
      :placeholder="t('查询策略名称')"
      class="w-60"
      allowClear
      @change="handleSearch">
      <template #suffix>
        <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
      </template>
    </Input>
    <div class="flex space-x-2 items-center">
      <ButtonAuth
        code="AuthorizedGroup"
        type="primary"
        icon="icon-tianjia"
        :disabled="!props.enabled"
        @click="addPolicy" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        @click="getGroupPolicy" />
    </div>
  </div>
  <Table
    size="small"
    :loading="loading"
    :dataSource="dataList"
    :rowKey="(record: any) => record.id"
    :columns="columns"
    :pagination="pagination"
    @change="handleChange">
    <template #bodyCell="{ column,text,record }">
      <template v-if="column.dataIndex === 'name'">
        <div class="w-full truncate text-theme-special text-theme-text-hover cursor-pointer">
          <Popover
            placement="bottomLeft"
            :overlayStyle="{maxWidth:'600px',fontSize:'12px'}">
            <template #title>
              {{ text }}
            </template>
            <template #content>
              {{ record.description }}
            </template>
            {{ text }}
          </Popover>
        </div>
      </template>
      <template v-if="column.dataIndex === 'grantStage'">
        {{ text?.message }}
      </template>
      <template v-if="column.dataIndex === 'action'">
        <ButtonAuth
          code="GroupAuthorizeCancel"
          type="text"
          :disabled="!props.enabled"
          icon="icon-quxiao"
          @click="handleCancel(record.id)" />
      </template>
    </template>
  </Table>
  <AsyncComponent :visible="policyVisible">
    <PolicyModal
      v-if="policyVisible"
      v-model:visible="policyVisible"
      :groupId="props.groupId"
      :updateLoading="updateLoading"
      type="Group"
      @change="policySave" />
  </AsyncComponent>
</template>
