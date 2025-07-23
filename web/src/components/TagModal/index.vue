<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/tools';
import { CheckboxGroup, Checkbox, Divider } from 'ant-design-vue';
import { Modal, Input, Icon, Scroll } from '@xcan-angus/vue-ui';

import { dept, user } from '@/api';

interface Props {
  visible: boolean;
  updateLoading?: boolean;
  relevancyIds?: string[];
  type?: 'Group' | 'Dept' | 'User'
  userId?: string;
  deptId?: string;
  groupId?: string;
  max?: number;
  data: string[];
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  userId: undefined,
  deptId: undefined,
  type: undefined,
  groupId: undefined,
  max: Infinity,
  data: () => []
});

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', ids: string[], tags: { id: string, name: string }[], delIds: string[]): void
}>();

const { t } = useI18n();

const params = ref<{ filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[], fullTextSearch: boolean }>({ filters: [], fullTextSearch: true });
const notify = ref(0);
const dataList = ref<{ id: string, name: string }[]>([]); // 所有人员
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
    params.value.filters[0] = { key: 'name', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
  notify.value++;
});

// 查询关联用户
const loadUserTagList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'tagId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await user.getUserTag(props.userId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.tagId)])];
  oldCheckedList.value.push(...data.list.map(item => item.tagId));
};

// 查询关联部门
const loadDeptTagList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'tagId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await dept.getDeptTags(props.deptId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.tagId)])];
  oldCheckedList.value.push(...data.list.map(item => item.tagId));
};

const handleOk = () => {
  // 新增的标签Ids
  const addTagIds = checkedList.value.filter(item => !oldCheckedList.value.includes(item));
  // 新增的标签
  const tags = dataList.value.filter(f => addTagIds.includes(f.id));
  // 取消关联的标签的Ids
  const deleteTagIds = oldCheckedList.value.filter(item => !checkedList.value.includes(item));
  emit('change', addTagIds, tags, deleteTagIds);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const handleChange = (value, newValue) => {
  dataList.value = value;
  switch (props.type) {
    case 'User':
      loadUserTagList(newValue); // 回显用户关联的标签
      break;
    case 'Dept':
      loadDeptTagList(newValue); // 回显部门关联的标签
      break;
    default:
      break;
  }
};

watch(() => props.visible, (newValue) => {
  if (newValue) {
    notify.value++;
    checkedList.value = props.data;
  }
}, {
  immediate: true
});

const getDisabled = (id) => {
  return !checkedList.value.includes(id) && checkedList.value.length >= props.max;
};

const action = computed(() => {
  return `${GM}/org/tag`;
});

</script>
<template>
  <Modal
    :title="t('relatedTags')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <Input
        :placeholder="t('tagPlaceholder1')"
        class="mb-2 w-1/2"
        size="small"
        @change="handleInputChange">
        <template #suffix>
          <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
        </template>
      </Input>
      <div class="flex py-1 bg-theme-form-head text-theme-title text-3 font-normal">
        <div class="w-1/3 pl-8 mr-2">
          ID
        </div>
        <div class="w-2/3 pl-4">
          {{ t('name') }}
        </div>
      </div>
      <Scroll
        v-model="loading"
        :action="action"
        style="height: 308px;"
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
            v-for="item,index in dataList"
            :key="item.id"
            class="flex flex-1 items-center text-3 text-theme-content"
            :calss="{'mt-1':index>0}">
            <Checkbox
              :value="item.id"
              :disabled="getDisabled(item.id)"
              class="mr-3.5 -mb-0.5">
            </Checkbox>
            <div class="w-1/3 truncate mr-2 pt-1" :title="item.id">
              {{ item.id }}
            </div>
            <div class="w-2/3 truncate pt-1" :title="item.name">
              {{ item.name }}
            </div>
          </div>
        </CheckboxGroup>
      </Scroll>
      <Divider class="my-2" />
      <Checkbox :indeterminate="indeterminate" @change="onCheckAllChange">
        {{ t('selectAll') }}
      </Checkbox>
    </div>
  </Modal>
</template>
