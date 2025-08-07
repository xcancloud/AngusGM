<script setup lang="ts">
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Checkbox, CheckboxGroup, Divider } from 'ant-design-vue';
import { Icon, Input, Modal, Scroll, Select } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

import { orgTag, user } from '@/api';

interface Props {
  visible: boolean;
  updateLoading?: boolean;
  relevancyIds?: string[];
  type?: 'User' | 'Dept' | 'Tag'
  tagId?: string;
  deptId?: string;
  userId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  type: undefined,
  tagId: undefined,
  deptId: undefined,
  userId: undefined
});

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', ids: string[], groups: { id: string, name: string }[], delIds: string[]): void
}>();

const { t } = useI18n();

const params = ref<{
  filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[],
  enabled: boolean,
  tagId?: string,
  fullTextSearch: boolean
}>({ filters: [], enabled: true, fullTextSearch: true });
const dataList = ref<{ id: string, name: string, code: string }[]>([]);
const checkedList = ref<string[]>([]);
const indeterminate = ref<boolean>(false);
const oldCheckedList = ref<string[]>([]);
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
const notify = ref(0);
const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters[0] = { key: 'name', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
  notify.value++;
});

// 查询标签关联的组
const loadTagTargetList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    tagId: props.tagId,
    targetType: 'GROUP',
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

// 查询用户关联的组
const loadUserGroup = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'groupId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await user.getUserGroup(props.userId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }
  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.groupId)])];
  oldCheckedList.value.push(...data.list.map(item => item.groupId));
};

const handleOk = () => {
  // 新增的组Ids
  const addGroupIds = checkedList.value.filter(item => !oldCheckedList.value.includes(item));
  // 新增的组
  const groups = dataList.value.filter(f => addGroupIds.includes(f.id));
  // 取消关联的组的Ids
  const deleteGroupIds = oldCheckedList.value.filter(item => !checkedList.value.includes(item));
  emit('change', checkedList.value, groups, deleteGroupIds);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const handleChange = (value, newValue) => {
  dataList.value = value;
  dataList.value = value;
  switch (props.type) {
    case 'Tag':
      loadTagTargetList(newValue); // 回显关联的标签
      break;
    case 'User':
      loadUserGroup(newValue); // 回显关联的标签
      break;

    default:
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

</script>
<template>
  <Modal
    :title="t('group.assocTitle')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <div class="mb-2 flex space-x-2">
        <Input
          :placeholder="t('group.placeholder.name')"
          class="w-1/2"
          size="small"
          allowClear
          @change="handleInputChange">
          <template #suffix>
            <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
          </template>
        </Input>
        <Select
          :placeholder="t('group.placeholder.tag')"
          :action="`${GM}/org/tag`"
          :fieldNames="{ label: 'name', value: 'id' }"
          :params="{fullTextSearch: true}"
          showSearcht
          allowClear
          class="w-1/2"
          @change="tagChange" />
      </div>
      <div class="flex py-1 bg-theme-form-head text-theme-title text-3 font-normal mb-1">
        <div class="w-1/3 pl-11 mr-2">
          ID
        </div>
        <div class="w-1/3 mr-2 pl-5">
          {{ t('group.columns.userGroup.name') }}
        </div>
        <div class="w-1/3">
          {{ t('group.columns.userGroup.code') }}
        </div>
      </div>
      <Scroll
        :action="`${GM}/group`"
        style="height: 292px;"
        class="py-1"
        :lineHeight="30"
        :params="params"
        :notify="notify"
        @change="handleChange">
        <CheckboxGroup
          v-model:value="checkedList"
          style="width: 100%;"
          class="space-y-2">
          <div
            v-for="item, index in dataList"
            :key="item.id"
            class="flex-1 items-end flex text-3 text-theme-content"
            :calss="{ 'mt-2': index > 0 }">
            <Checkbox :value="item.id">
              <Icon icon="icon-zu1" class="-mt-0.75 text-4" />
            </Checkbox>
            <div class="truncate -ml-1 w-1/3">{{ item.id }}</div>
            <div class="truncate w-1/3" :title="item.name">{{ item.name }}</div>
            <div class="truncate w-1/3" :title="item.code">{{ item.code }}</div>
          </div>
        </CheckboxGroup>
      </Scroll>
      <Divider class="my-2" />
      <Checkbox :indeterminate="indeterminate" @change="onCheckAllChange">
        {{ t('common.form.selectAll') }}
      </Checkbox>
    </div>
  </Modal>
</template>
