<script setup lang='ts'>
import {computed, defineAsyncComponent, onMounted, ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Input, Table} from '@xcan-angus/vue-ui';
import {duration, utils} from '@xcan-angus/infra';
import {debounce} from 'throttle-debounce';

import {SearchParams, UserDept} from './PropsType';
import {user} from '@/api';

const DeptModal = defineAsyncComponent(() => import('@/components/DeptModal/index.vue'));

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
// 是否更新count 条件查询不更新count
const isContUpdate = ref(true);
const dataList = ref<UserDept[]>([]);
const loadUserDept = async () => {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserDept(props.userId, params.value);
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

// 关联部门
const deptVisible = ref(false);
const addDept = () => {
  deptVisible.value = true;
};

/**
 * updateLoading说明:
 * 1.updateLoading:是新增和删除接口的loading,即关联弹窗确定按钮的loading,区别列表的loading是
 *   防止新增删除的时候控制影响刷新按钮和表格,防止造成多个地方闪动
 * 2.updateLoading之所以没有在 if(error)之前处理，是因为关联弹窗有可能同时有新增和删除,防止确定按钮loading中断；
 */
const updateLoading = ref(false);
const isRefresh = ref(false);
const disabled = ref(false); // 刷新按钮禁用旋转
const deptSave = async (addIds: string[], delIds: string[]) => {
  // 注：删除必须放前面
  if (delIds.length) {
    await delUserDept(delIds, 'Modal');
  }

  if (addIds.length) {
    await addUserDept(addIds);
  }

  deptVisible.value = false;
  updateLoading.value = false;
  if (isRefresh.value) {
    disabled.value = true;
    await loadUserDept();
    disabled.value = false;
    isRefresh.value = false;
  }
};

// 添加关联的部门
const addUserDept = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserDept(props.userId, _addIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

// 删除关联的部门
const delUserDept = async (_delIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserDept(props.userId, { deptIds: _delIds });
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
  // 要求表格操作不影响刷新图标
  if (type === 'Table') {
    disabled.value = true;
    await loadUserDept();
    disabled.value = false;
  }
};

// 删除关联的部门
const handleCancel = async (id) => {
  delUserDept([id], 'Table');
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'deptName', op: 'MATCH_END', value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  isContUpdate.value = false;
  await loadUserDept();
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
  await loadUserDept();
  disabled.value = false;
};

onMounted(() => {
  loadUserDept();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'deptId',
    width: '15%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name'),
    dataIndex: 'deptName'
  },
  {
    title: t('code'),
    dataIndex: 'deptCode',
    width: '20%'
  },
  {
    title: t('是否负责人'),
    dataIndex: 'deptHead',
    customRender: ({ text }): string => text ? t('yes') : t('no'),
    width: '8%'
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

const hintTip = computed(() => t(`每个用户最多允许关联5个部门，当前用户已关联${count.value}个部门。`));
</script>
<template>
  <div>
    <Hints :text="hintTip" class="mb-1" />
    <div class="flex items-center justify-between mb-2">
      <Input
        placeholder="查询部门名称"
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
          code="UserDeptAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="props.hasAuth || total>=5"
          @click="addDept" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserDept" />
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
      <template #bodyCell="{ column ,text,record }">
        <template v-if="column.dataIndex === 'deptName'">
          <div class="flex items-center">
            <Icon icon="icon-bumen1" class="text-4 mr-2" />
            <div class="w-full truncate" :title="text">{{ text }}</div>
          </div>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserDeptUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.deptId)" />
        </template>
      </template>
    </Table>
  </div>
  <AsyncComponent :visible="deptVisible">
    <DeptModal
      v-if="deptVisible"
      v-model:visible="deptVisible"
      :updateLoading="updateLoading"
      :userId="userId"
      type="User"
      @change="deptSave" />
  </AsyncComponent>
</template>
