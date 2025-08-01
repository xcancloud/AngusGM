<script setup lang='ts'>
import {computed, defineAsyncComponent, ref, watch} from 'vue';
import {useI18n} from 'vue-i18n';
import {
  AsyncComponent,
  ButtonAuth,
  Card,
  Icon,
  IconCount,
  IconRefresh,
  Input,
  SelectEnum,
  Table
} from '@xcan-angus/vue-ui';
import {app, duration, utils} from '@xcan-angus/infra';
import {debounce} from 'throttle-debounce';

import {Target, TargetType} from '../../PropsType';
import {orgTag} from '@/api';

const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const DeptModal = defineAsyncComponent(() => import('@/components/DeptModal/index.vue'));
const GroupModal = defineAsyncComponent(() => import('@/components/GroupModal/index.vue'));

interface Props {
  tagId: string;
  visible: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  tagId: undefined,
  visible: true
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const { t } = useI18n();
const loading = ref(false);
const disabled = ref(false);
const params = ref<{ pageNo: number, pageSize: number, filters: any[] }>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const targetList = ref<Target[]>([]);
const targetType = ref<TargetType>('USER');
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const loadTagTargetList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    ...params.value,
    tagId: props.tagId,
    targetType: targetType.value
  };
  loading.value = true;
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  loading.value = false;
  if (error) {
    return;
  }
  targetList.value = data?.list || [];
  total.value = +data?.total;
};

const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
};

const updateLoading = ref(false);
const isRefresh = ref(false);
const userVisible = ref(false);
const relevancyUser = () => {
  userVisible.value = true;
};
const userSave = async (_userIds: string[], users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  if (deleteUserIds.length) {
    await delTagTarget(deleteUserIds);
  }

  if (users.length) {
    const targetList = users.map(item => ({ targetId: item.id, targetType: targetType.value }));
    await addTagTarget(targetList);
  }

  updateLoading.value = false;
  userVisible.value = false;
  if (isRefresh.value) {
    disabled.value = true;
    loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

const deptVisible = ref(false);
const relevancyDept = () => {
  deptVisible.value = true;
};
const deptSave = async (addIds: string[], delIds: string[]) => {
  if (delIds.length) {
    await delTagTarget(delIds, 'Modal');
  }

  if (addIds.length) {
    await addTagDept(addIds);
  }

  deptVisible.value = false;
  updateLoading.value = false;
  if (isRefresh.value) {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

const addTagDept = async (_addIds: string[]) => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  await orgTag.addTagTarget(props.tagId, _addIds.map(item => ({ targetId: item, targetType: 'DEPT' })));
  loading.value = false;
  isRefresh.value = true;
};

const groupVisible = ref(false);
const relevancyGroup = () => {
  groupVisible.value = true;
};
const groupSave = async (_groupIds: string[], groups: { id: string, name: string }[], deleteGroupIds: string[]) => {
  if (deleteGroupIds.length) {
    await delTagTarget(deleteGroupIds, 'Modal');
  }
  if (groups.length) {
    const targetList = groups.map(item => ({ targetId: item.id, targetName: item.name, targetType: targetType.value }));
    await addTagTarget(targetList);
  }

  groupVisible.value = false;
  updateLoading.value = false;
  if (isRefresh.value) {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
    isRefresh.value = false;
  }
};

const addTagTarget = async (targetList: { targetId: string, targetType: TargetType }[]) => {
  updateLoading.value = true;
  const [error] = await orgTag.addTagTarget(props.tagId, targetList);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

const delTagTarget = async (targetIds: string[], type?: 'Table' | 'Modal') => {
  updateLoading.value = true;
  const [error] = await orgTag.deleteTagTarget(props.tagId, targetIds);
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
  if (type === 'Table') {
    disabled.value = true;
    await loadTagTargetList();
    disabled.value = false;
  }
};

watch(() => targetType.value, async () => {
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

watch(() => props.tagId, async (newValue) => {
  if (!newValue) {
    return;
  }
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

const searchUserName = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'targetName', op: 'MATCH_END', value }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  await loadTagTargetList();
  disabled.value = false;
});

const toggle = () => {
  emit('update:visible', !props.visible);
};

const getTargetType = (value: TargetType) => {
  switch (value) {
    case 'USER':
      return t('user');
    case 'DEPT':
      return t('sector');
    case 'GROUP':
      return t('group');
  }
};

const columns = ref([
  {
    title: '用户ID',
    dataIndex: 'id',
    width: '22%'
  },
  {
    title: t('userName1'),
    dataIndex: 'targetName',
    ellipsis: true
  },
  {
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '20%'
  },
  {
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
]);

const targetTypeChange = () => {
  switch (targetType.value) {
    case 'USER':
      columns.value[0].title = t('用户ID');
      columns.value[1].title = t('userName1');
      break;
    case 'DEPT':
      columns.value[0].title = t('部门ID');
      columns.value[1].title = t('deptName');
      break;
    case 'GROUP':
      columns.value[0].title = t('组ID');
      columns.value[1].title = t('groupName');
      break;
  }
};

const placeholder = computed(() => {
  switch (targetType.value) {
    case 'USER':
      return t('groupPlaceholder2');
    case 'DEPT':
      return t('groupPlaceholder4');
    case 'GROUP':
      return t('groupPlaceholder3');
    default:
      return t('groupPlaceholder2');
  }
});

const cancelBtnDisabled = {
  USER: !app.has('TagUserUnassociate'),
  DEPT: !app.has('TagDeptUnassociate'),
  GROUP: !app.has('TagGroupUnassociate')
};

const cancelText = {
  USER: app.getName('TagUserUnassociate'),
  DEPT: app.getName('TagDeptUnassociate'),
  GROUP: app.getName('TagGroupUnassociate')
};
</script>
<template>
  <Card class="flex-1">
    <div class="flex justify-between mb-2">
      <div class="flex items-center space-x-2">
        <SelectEnum
          v-model:value="targetType"
          internal
          class="w-50"
          size="small"
          enumKey="OrgTargetType"
          @change="targetTypeChange" />
        <Input
          :placeholder="placeholder"
          class="w-60"
          size="small"
          allowClear
          @change="searchUserName">
          <template #suffix>
            <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
          </template>
        </Input>
      </div>
      <div class="space-x-2 flex items-center">
        <template v-if="targetType === 'USER'">
          <ButtonAuth
            code="TagUserAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyUser" />
        </template>
        <template v-if="targetType === 'DEPT'">
          <ButtonAuth
            code="TagDeptAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyDept" />
        </template>
        <template v-if="targetType === 'GROUP'">
          <ButtonAuth
            code="TagGroupAssociate"
            type="primary"
            icon="icon-tianjia"
            @click="relevancyGroup" />
        </template>
        <IconCount :value="props.visible" @click="toggle" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadTagTargetList" />
      </div>
    </div>
    <Table
      :dataSource="targetList"
      rowKey="id"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column, text,record }">
        <template v-if="column.dataIndex === 'targetType'">
          {{ getTargetType(text) }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="TagGroupAssociate"
            type="text"
            :dsiabled="cancelBtnDisabled[targetType]"
            :text="cancelText[targetType]"
            icon="icon-quxiao"
            @click="delTagTarget([record.targetId],'Table')" />
        </template>
      </template>
    </Table>
    <AsyncComponent :visible="userVisible">
      <UserModal
        v-if="userVisible"
        v-model:visible="userVisible"
        :tagId="props.tagId"
        :updateLoading="updateLoading"
        type="Tag"
        @change="userSave" />
    </AsyncComponent>
    <AsyncComponent :visible="deptVisible">
      <DeptModal
        v-if="deptVisible"
        v-model:visible="deptVisible"
        type="Tag"
        :updateLoading="updateLoading"
        :tagId="props.tagId"
        @change="deptSave" />
    </AsyncComponent>
    <AsyncComponent :visible="groupVisible">
      <GroupModal
        v-if="groupVisible"
        v-model:visible="groupVisible"
        :tagId="props.tagId"
        :updateLoading="updateLoading"
        type="Tag"
        @change="groupSave" />
    </AsyncComponent>
  </Card>
</template>
<style scoped>
@keyframes circle {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.circle-move {
  animation-name: circle;
  animation-duration: 1000ms;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  animation-direction: normal;
}
</style>
