<script setup lang="ts">
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { GM } from '@xcan-angus/tools';
import { Modal, Tree, Icon, Select } from '@xcan-angus/vue-ui';

import { DataType } from '@/views/organization/dept/PropsType';
import { dept, orgTag, user } from '@/api';

interface Props {
  visible: boolean;
  updateLoading?: boolean;
  type?: 'User' | 'Group' | 'Tag'
  userId?: string;
  tagId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  type: undefined,
  userId: undefined,
  tagId: undefined
});

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', ids: string[], delIds: string[]): void
}>();

const { t } = useI18n();

const handleOk = () => {
  const addIds = checkedKeys.value.filter(item => !oldDeptIds.value.includes(item));
  const delIds = oldDeptIds.value.filter(item => !checkedKeys.value.includes(item));
  emit('change', addIds, delIds);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const params = ref<{ tagId?: string }>({});
const notify = ref(0);
const oldDeptIds = ref<string[]>([]);
const checkedKeys = ref<string[]>([]);

const loadUserDept = async (ids): Promise<void> => {
  const listParams = {
    filters: [{ key: 'deptId', op: 'IN', value: ids }]
  };

  const [error, { data }] = await user.getUserDept(props.userId as string, listParams);
  if (error || !data?.list?.length) {
    return;
  }

  const deptIds = data.list.map(item => item.deptId);
  checkedKeys.value.push(...deptIds);
  oldDeptIds.value.push(...deptIds);
};

const loadTagDept = async (ids): Promise<void> => {
  const listParams = {
    tagId: props.tagId,
    targetType: 'DEPT',
    deptId: props.tagId,
    filters: [{ key: 'targetId', op: 'IN', value: ids }]
  };
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  if (error || !data?.list?.length) {
    return;
  }

  const deptIds = data.list.map(item => item.targetId);
  checkedKeys.value.push(...deptIds);
  oldDeptIds.value.push(...deptIds);
};

const loaded = (options) => {
  if (!options.length) {
    return;
  }
  const newIds = options.map(item => item.id);

  switch (props.type) {
    case 'User':
      loadUserDept(newIds);
      break;
    case 'Tag':
      loadTagDept(newIds);
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

const selectTagId = ref(undefined);
const getSearchDeptParams = computed(() => {
  return {
    tagId: selectTagId.value
  };
});

const treeData = ref<DataType[]>([]);
const deptChange = async (value: string) => {
  if (!value) {
    notify.value++;
    return;
  }
  const [error, res] = await dept.getNavigationByDeptId({ id: value });
  if (error) {
    return;
  }
  const parentChain = (res.data.parentChain || []).map(item => ({ ...item, hasSubDept: true }));
  treeData.value = [...parentChain, res.data.current];
  loadUserDept([res.data.current.id]);
};
</script>
<template>
  <Modal
    :title="t('关联部门')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3 pl-3">
      <div class="flex mb-2">
        <Select
          :action="`${GM}/dept/search`"
          size="small"
          class="w-1/2 mr-2"
          :showSearch="true"
          :params="getSearchDeptParams"
          :allowClear="true"
          :fieldNames="{ label: 'name', value: 'id' }"
          :placeholder="t('deptPlaceholder')"
          @change="deptChange" />
        <Select
          v-model:value="selectTagId"
          :placeholder="t('tagPlaceholder')"
          :action="`${GM}/org/tag/search`"
          :fieldNames="{ label: 'name', value: 'id' }"
          showSearcht
          allowClear
          class="w-1/2"
          @change="tagChange" />
      </div>
      <div class="flex py-1 bg-theme-form-head text-theme-title text-3 font-normal">
        <div class="w-1/2 pl-10 mr-2">
          {{ t('name') }}
        </div>
        <div class="w-1/2">
          {{ t('code') }}
        </div>
      </div>
      <Tree
        v-model:checkedKeys="checkedKeys"
        :action="`${GM}/dept/search?`"
        :fieldNames="{ children: 'hasSubDept' }"
        :checkable="true"
        :params="params"
        :notify="notify"
        :treeData="treeData"
        class="-ml-5"
        style="height: 308px;"
        @loaded="loaded">
        <template #default="item">
          <div class="flex">
            <div class="w-1/2 truncate mr-2" :title="item.name">
              <Icon icon="icon-bumen1" class="mr-1 -mt-0.5" />
              {{ item.name }}
            </div>
            <div class="w-1/2 truncate" :title="item.code">
              {{ item.code }}
            </div>
          </div>
        </template>
      </Tree>
    </div>
  </Modal>
</template>
