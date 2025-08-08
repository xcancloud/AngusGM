<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Checkbox, CheckboxGroup } from 'ant-design-vue';
import { Icon, Image, Input, Modal, Scroll, Select } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

import { dept, group, orgTag } from '@/api';

interface Props {
  visible: boolean;
  updateLoading?: boolean;
  relevancyIds?: string[];
  type?: 'Group' | 'Dept' | 'Tag' | 'Operation'
  tagId?: string;
  deptId?: string;
  groupId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  tagId: undefined,
  deptId: undefined,
  type: undefined,
  groupId: undefined
});

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', ids: string[], users: { id: string, fullName: string }[], delIds: string[], checkedUsers: {
    id: string,
    fullName: string
  }[]): void
}>();

const { t } = useI18n();

const params = ref<{
  filters: { key: 'fullName', op: 'MATCH_END', value: string | undefined }[],
  enabled: boolean,
  tagId?: string,
  fullTextSearch: boolean;
}>({ filters: [], enabled: true, fullTextSearch: true });
const notify = ref(0);
const dataList = ref<{ id: string, fullName: string, avatar?: '', enabled: boolean }[]>([]);
const checkedList = ref<string[]>([]);
const oldCheckedList = ref<string[]>([]); // 后台已关联的用户
const indeterminate = ref(false);
const loading = ref(false);

const onCheckAllChange = e => {
  if (e.target.checked) {
    checkedList.value = dataList.value.map(m => m.id);
    indeterminate.value = true;
  } else {
    indeterminate.value = false;
    checkedList.value = [];
  }
};

const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters[0] = { key: 'fullName', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
});

// 回显标签关联的用户
const loadTagTargetList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    tagId: props.tagId,
    targetType: 'USER',
    filters: [{ key: 'targetId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  loading.value = false;
  if (error) {
    return;
  }
  if (!data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.targetId)])];
  oldCheckedList.value.push(...data.list.map(item => item.targetId));
};

// 回显组关联的用户
const loadGroupUserList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'userId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await group.getGroupUser(props.groupId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.userId)])];
  oldCheckedList.value.push(...data.list.map(item => item.userId));
};

// 回显部门关联的用户
const loadDepUserList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'userId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await dept.getDeptUsers(props.deptId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.userId)])];
  oldCheckedList.value.push(...data.list.map(item => item.userId));
};

const handleOk = () => {
  // 新增的用户Ids
  const addUserIds = checkedList.value.filter(item => !oldCheckedList.value.includes(item));
  // 新增的用户
  const users = dataList.value.filter(f => addUserIds.includes(f.id));
  // 取消关联的用户的Ids
  const deleteUserIds = oldCheckedList.value.filter(item => !checkedList.value.includes(item));

  // 所有选择的用户checkedList
  const checkedUsers = dataList.value.filter(item => checkedList.value.includes(item.id));
  emit('change', addUserIds, users, deleteUserIds, checkedUsers);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const handleChange = (value, newValue) => {
  dataList.value = value;
  switch (props.type) {
    case 'Tag':
      loadTagTargetList(newValue);
      break;
    case 'Group':
      loadGroupUserList(newValue);
      break;
    case 'Dept':
      loadDepUserList(newValue);
      break;
    default:
      checkedList.value = props.relevancyIds;
      break;
  }
};

const tagChange = (value) => {
  if (value) {
    params.value.tagId = value;
  } else {
    if (params.value.tagId) {
      delete params.value.tagId;
    }
  }
  notify.value++;
};

watch(() => props.tagId, () => {
  oldCheckedList.value = [];
  checkedList.value = [];
  notify.value++;
});

watch(() => props.relevancyIds, () => {
  checkedList.value = props.relevancyIds;
}, {
  immediate: true
});

const modalTitle = computed(() => {
  return props.type === 'Operation' ? t('添加运营人员') : t('关联用户');
});
</script>
<template>
  <Modal
    :title="modalTitle"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :confirmLoading="props.updateLoading"
    :width="680"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="mb-3 flex space-x-2">
      <Input
        :placeholder="t('user.placeholder.name')"
        size="small"
        class="w-1/2"
        @change="handleInputChange">
        <template #suffix>
          <Icon icon="icon-sousuo" class="text-3.5" />
        </template>
      </Input>
      <Select
        :placeholder="t('user.placeholder.selectTag')"
        :action="`${GM}/org/tag?fullTextSearch=true`"
        :fieldNames="{ label: 'name', value: 'id' }"
        showSearcht
        allowClear
        class="w-1/2"
        @change="tagChange" />
    </div>
    <div class="flex items-center h-8 leading-5 px-2 space-x-2 mb-2 rounded bg-theme-form-head text-theme-title">
      <Checkbox
        class="w-4"
        :indeterminate="indeterminate"
        @change="onCheckAllChange"></Checkbox>
      <div class="w-50">ID</div>
      <div class="flex-1 ">{{ t('user.columns.name') }}</div>
      <div class="w-15">{{ t('user.columns.status') }}</div>
    </div>
    <Scroll
      v-model="loading"
      :action="`${GM}/user`"
      :params="params"
      :notify="notify"
      :lineHeight="28"
      style="height: 312px;"
      @change="handleChange">
      <CheckboxGroup
        v-model:value="checkedList"
        style="width: 100%;"
        class="space-y-1 px-2">
        <div
          v-for="item in dataList"
          :key="item.id"
          class="flex-1 items-center flex leading-5 h-7 space-x-2 text-theme-content">
          <Checkbox :value="item.id" />
          <div class="truncate flex-shrink-0 w-50 flex items-center space-x-2">
            <Image
              :src="item?.avatar"
              class="flex-shrink-0 w-5 h-5 rounded-full"
              type="avatar" />
            <div class="pt-0.5">{{ item.id }}</div>
          </div>
          <div class="truncate flex-1" :title="item.fullName">{{ item.fullName }}</div>
          <div class=" flex-shrink-0 w-15">{{ item.enabled?'启用':'禁用' }}</div>
        </div>
      </CheckboxGroup>
    </Scroll>
  </Modal>
</template>
