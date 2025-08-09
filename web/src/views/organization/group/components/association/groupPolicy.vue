<script setup lang='ts'>
import {computed, defineAsyncComponent, onMounted, ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {Popover} from 'ant-design-vue';
import {ButtonAuth, Hints, Icon, IconRefresh, Input, Table} from '@xcan-angus/vue-ui';
import {debounce} from 'throttle-debounce';
import {duration, PageQuery, SearchCriteria, utils} from '@xcan-angus/infra';
import {OrgTargetType} from '@/enums/enums';

import {auth} from '@/api';
import {createAuthPolicyColumns} from '@/views/organization/user/PropsType';
import {Props} from '../../PropsType';

const { t } = useI18n();

const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

const props = withDefaults(defineProps<Props>(), {
  groupId: '',
  notify: 0,
  enabled: false,
  policyName: ''
});

/** Loading flags and data states */
const loading = ref(false);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

/**
 * Table pagination computed from current params + backend total.
 */
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

/**
 * Handle table pagination/sort changes and requery.
 */
const handleChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  await getGroupPolicy();
};

/** Table dataset */
const dataList = ref([]);

/**
 * Fetch policies authorized to the group.
 */
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

const policyVisible = ref(false);
const addPolicy = () => {
  policyVisible.value = true;
};

const updateLoading = ref(false);
const disabled = ref(false);

/** Save newly selected policies then refresh list. */
const policySave = async (addIds: string[]) => {
  if (addIds.length) {
    await addGroupPolicy(addIds);
  }
  policyVisible.value = false;
};

/** Add policies to current group. */
const addGroupPolicy = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await auth.addGroupPolicy(props.groupId, _addIds);
  updateLoading.value = false;
  if (error) {
    return;
  }
  await getGroupPolicy();
};

/** Remove a policy authorization from the group and keep table page valid. */
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

/**
 * Debounced search by policy name. Updates filters and reloads list.
 */
const handleSearch = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'name', op: SearchCriteria.OpEnum.MatchEnd, value: value }];
  } else {
    params.value.filters = [];
  }
  getGroupPolicy();
});

/** Initial load */
onMounted(() => {
  getGroupPolicy();
});

/**
 * Table columns configuration
 * Defines the structure and behavior of each table column
 */
const columns = createAuthPolicyColumns(t, OrgTargetType.GROUP);

</script>
<template>
  <Hints class="mb-1" :text="t('group.disabledAuthPolicyTip')" />
  <div class="flex items-center justify-between mb-2">
    <Input
      :placeholder="t('permission.placeholder.policyName')"
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
    rowKey="id"
    :columns="columns"
    :pagination="pagination"
    :noDataSize="'small'"
    :noDataText="t('common.messages.noData')"
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
