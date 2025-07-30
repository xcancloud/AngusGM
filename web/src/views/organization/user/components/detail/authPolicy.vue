<script setup lang='ts'>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Popover } from 'ant-design-vue';
import { Hints, Table, AsyncComponent, Input, IconRefresh, Icon, ButtonAuth } from '@xcan-angus/vue-ui';
import { utils, duration } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { auth } from '@/api';
import { SearchParams } from './PropsType';

const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

interface Props {
  userId: string;
  hasAuth: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  userId: '',
  hasAuth: false
});

const { t } = useI18n();

const loading = ref(false);
const disabled = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const dataList = ref([]);
const getUserPolicy = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await auth.getUserPolicy(props.userId, params.value);
  loading.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = +data.total;
};

const policyVisible = ref(false);
const addPolicy = () => {
  policyVisible.value = true;
};

const updateLoading = ref(false);
const policySave = (addIds: string[]) => {
  if (!addIds.length) {
    return;
  }
  addUserPolicy(addIds);
};

const addUserPolicy = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await auth.addUserPolicy(props.userId, _addIds);
  updateLoading.value = false;
  policyVisible.value = false;
  if (error) {
    return;
  }
  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

const handleCancel = async (id) => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error] = await auth.deleteUserPolicy(props.userId, [id]);
  loading.value = false;

  if (error) {
    return;
  }
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'name', op: 'MATCH_END', value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
});

const handleChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await getUserPolicy();
  disabled.value = false;
};

const getShowOrgType = (record) => {
  const resultArr: string[] = [];
  if (record.currentDefault) {
    resultArr.push('应用默认授权');
  }
  if (record.openAuth) {
    resultArr.push('开通授权');
  }
  return resultArr.join(',');
};

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

onMounted(() => {
  getUserPolicy();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '15%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name'),
    dataIndex: 'name',
    width: '15%'
  },
  {
    title: t('code'),
    dataIndex: 'code'
  },
  {
    title: t('授权来源'),
    dataIndex: 'orgType',
    width: '15%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('授权人'),
    dataIndex: 'authByName',
    width: '15%'
  },
  {
    title: t('授权时间'),
    dataIndex: 'authDate',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: '6%',
    align: 'center'
  }
];

const hintTip = t('当前只允许取消用户来源授权。如果要取消“应用默认授权”策略，请进入"全局管理"-"权限策略"-"默认权限策略"禁用应用默认策略；取消部门和组关联授权请进入"全局管理"-"组织人员"-"部门"或"组"-"权限策略"取消授权；“开通初始化授权”不可取消。');
</script>
<template>
  <div>
    <Hints :text="hintTip" class="mb-1" />
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
      <div class="flex items-center">
        <ButtonAuth
          code="AuthorizeUser"
          type="primary"
          icon="icon-tianjia"
          class="mr-2"
          :disabled="props.hasAuth || total>=10"
          @click="addPolicy" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="getUserPolicy" />
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
          <div class="w-full truncate cursor-pointer">
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
        <template v-if="column.dataIndex === 'openAuth'">
          {{ text?'是':'否' }}
        </template>
        <template v-if="column.dataIndex === 'enabled'">
          {{ text?'有效':'无效' }}
        </template>
        <template v-if="column.dataIndex === 'orgType'">
          <template v-if="['USER','DEPT','GROUP'].includes(text?.value)">
            {{ text?.message }}({{ record.orgName }})
          </template>
          <template v-else>
            {{ getShowOrgType(record) }}
          </template>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserAuthorizeCancel"
            type="text"
            :disabled="record.orgType?.value !== 'USER'"
            icon="icon-quxiao"
            @click="handleCancel(record.id)" />
        </template>
      </template>
    </Table>
  </div>
  <AsyncComponent :visible="policyVisible">
    <PolicyModal
      v-if="policyVisible"
      v-model:visible="policyVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="policySave" />
  </AsyncComponent>
</template>
