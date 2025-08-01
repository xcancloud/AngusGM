<script setup lang='ts'>
import {onMounted, reactive} from 'vue';
import {useI18n} from 'vue-i18n';
import {Pagination} from 'ant-design-vue';
import {
  ButtonAuth,
  Icon,
  Image,
  modal,
  NoData,
  notification,
  PureCard,
  SelectDept,
  SelectGroup,
  SelectUser
} from '@xcan-angus/vue-ui';
import {cookieUtils} from '@xcan-angus/infra';

import {auth} from '@/api';

interface Props {
  policyId: string | undefined,
  type: 'USER' | 'DEPT' | 'GROUP',
  appId?: string | undefined
}

interface DataType {
  id: string,
  policyId: string,
  appId?: string,
  userId?: string,
  avatar?: string,
  createdByName?: string,
  deptId?: string,
  groupId?: string,
  fullName?: string,
  name: string
}

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), {
  appId: undefined
});

const addTextConfig = {
  USER: 'AuthUserAdd',
  DEPT: 'AuthDeptAdd',
  GROUP: 'AuthGroupAdd'
};

const cancelText = {
  USER: 'AuthUserCancel',
  DEPT: 'AuthDeptCancel',
  GROUP: 'AuthGroupCancel'
};

const typeName = props.type === 'USER'
  ? t('permissionsStrategy.detail.target.user')
  : props.type === 'GROUP'
    ? t('permissionsStrategy.detail.target.group')
    : t('permissionsStrategy.detail.target.dept');

const state = reactive<{
  loading: boolean,
  saving: boolean,
  selectedValue: string | undefined,
  dataSource: DataType[]
}>({
  loading: false, // 查询列表加载状态
  saving: false, // 保存按钮状态
  selectedValue: undefined, // 选择框当前选中的值
  dataSource: [] // 列表数据
});

// 分页参数
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 20,
  showLessItems: true,
  showTotal: (total: number) => {
    const pageNo = pagination.current;
    const totalPage = Math.ceil(total / pagination.pageSize);
    return t('pageShowTotal', { total, pageNo, totalPage });
  },
  showSizeChanger: false,
  size: 'small',
  pageSizeOptions: ['10', '20', '30', '40', '50']
});

// 选择的对象发生变化
const targetChange = (value: string | undefined) => {
  state.selectedValue = value;
};

// 查询当前已授权的对象
const load = async () => {
  if (!props.policyId) {
    return;
  }

  const params: {
    pageNo: number,
    pageSize: number
  } = {
    pageNo: pagination.current,
    pageSize: pagination.pageSize
  };
  state.loading = true;
  let res: [Error | null, any];
  if (props.type === 'USER') {
    res = await auth.getPolicyUsers(props.policyId, params);
  } else if (props.type === 'DEPT') {
    res = await auth.getPolicyDept(props.policyId, params);
  } else {
    res = await auth.getPolicyGroups(props.policyId, params);
  }

  const [error, { data }] = res;
  state.loading = false;
  if (error) {
    return;
  }

  state.dataSource = data.list;
  pagination.total = +data.total;
  if (props.type === 'USER') {
    state.dataSource.forEach(item => {
      item.avatar = item.avatar ? `${item.avatar}&access_token=${cookieUtils.get('access_token')}` : '';
    });
  }
};

// 重置分页查询
const resetPageNoLoad = () => {
  pagination.current = 1;
  load();
};

// 保存所选的对象
const addSave = async () => {
  if (!state.selectedValue) {
    notification.warning(t('permissionsStrategy.detail.target.addWarn', { name: typeName }));
    return;
  }

  if (!props.policyId) {
    return;
  }

  const ids = [state.selectedValue];
  let res: [Error | null, any];
  state.saving = true;
  if (props.type === 'USER') {
    res = await auth.addPolicyUser(props.policyId, ids);
  } else if (props.type === 'DEPT') {
    res = await auth.addPolicyDept(props.policyId, ids);
  } else {
    res = await auth.addPolicyGroup(props.policyId, ids);
  }

  const [error] = res;
  state.saving = false;
  if (error) {
    return;
  }

  notification.success(t('permissionsStrategy.detail.target.addSuccess'));
  state.selectedValue = undefined;
  resetPageNoLoad();
};

// 删除策略下的对象
const delTarget = (item: DataType) => {
  modal.confirm({
    centered: true,
    title: t('confirmTitle'),
    content: t('permissionsStrategy.detail.target.delConfirm', { name: typeName }),
    async onOk () {
      if (!props.policyId) {
        return;
      }

      let res: [Error | null, any];
      if (props.type === 'USER') {
        res = await auth.deletePolicyUsers(props.policyId, [item.id]);
      } else if (props.type === 'DEPT') {
        res = await auth.deletePolicyDept(props.policyId, [item.id]);
      } else {
        res = await auth.deletePolicyGroups(props.policyId, [item.id]);
      }

      const [error] = res;
      if (error) {
        return;
      }

      notification.success(t('permissionsStrategy.detail.target.delSuccess'));
      resetPageNoLoad();
    }
  });
};

onMounted(() => {
  load();
});
</script>

<template>
  <PureCard class="flex-1 flex flex-col p-3.5">
    <div class="mb-3 text-3 leading-3 text-theme-title font-medium">
      {{ t('permissionsStrategy.detail.target.title', { name: typeName }) }}
    </div>
    <div class="pb-3 flex justify-between border-b border-solid border-theme-divider ">
      <SelectUser
        v-if="type === 'USER'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permissionsStrategy.detail.target.placeholder', { name: typeName })"
        :internal="true"
        size="small"
        @change="targetChange" />
      <SelectDept
        v-if="type === 'DEPT'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permissionsStrategy.detail.target.placeholder', { name: typeName })"
        :internal="true"
        size="small"
        @change="targetChange" />
      <SelectGroup
        v-if="type === 'GROUP'"
        class="flex-1 w-70"
        :value="state.selectedValue"
        :placeholder="t('permissionsStrategy.detail.target.placeholder', { name: typeName })"
        :internal="true"
        size="small"
        @change="targetChange" />
      <ButtonAuth
        :code="addTextConfig[props.type]"
        size="small"
        type="primary"
        class="ml-3"
        icon="icon-tianjia"
        :loading="state.saving"
        @click="addSave" />
    </div>
    <div class="flex-1 py-2 overflow-y-auto text-3 leading-7">
      <div
        v-for="item in state.dataSource"
        :key="item.id"
        class="flex items-center px-3 rounded cursor-pointer hover:bg-blue-hover-light py-0.5">
        <Image
          v-if="type === 'USER'"
          type="avatar"
          class="w-5 h-5 rounded-2xl"
          :src="item.avatar" />
        <div v-if="type === 'DEPT'" class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline h-full text-theme-content text-4" icon="icon-bumen" />
        </div>
        <div v-if="type === 'GROUP'" class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline h-full text-theme-content text-4" icon="icon-zu" />
        </div>
        <div class="w-70 flex-1 text-3 leading-3 text-theme-content px-2 truncate">
          {{ type === 'USER' ? item.fullName : item.name }}
        </div>
        <div class="text-3 leading-3">
          <ButtonAuth
            :code="cancelText[props.type]"
            size="small"
            type="text"
            icon="icon-quxiao"
            @click="delTarget(item)" />
        </div>
      </div>
      <NoData
        v-if="state.dataSource.length === 0"
        class="h-full"
        :text="t('permissionsStrategy.detail.target.noData', { name: typeName })" />
    </div>
    <Pagination
      v-if="pagination.total >= 10"
      v-model:current="pagination.current"
      v-model:pageSize="pagination.pageSize"
      class="w-full my-5 pr-5 h-6 text-right"
      :size="pagination.size"
      :showLessItems="pagination.showLessItems"
      :showSizeChanger="pagination.showSizeChanger"
      :total="pagination.total"
      :showTotal="pagination.showTotal"
      :pageSizeOptions="pagination.pageSizeOptions"
      @showSizeChange="load"
      @change="load">
    </Pagination>
  </PureCard>
</template>
