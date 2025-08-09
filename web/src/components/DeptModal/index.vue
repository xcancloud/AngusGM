<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { GM } from '@xcan-angus/infra';
import { Icon, Modal, Select, Tree } from '@xcan-angus/vue-ui';

import { DataType } from '@/views/organization/dept/PropsType';
import { dept, orgTag, user } from '@/api';

/**
 * Component props interface
 * Defines the properties passed to the department modal component
 */
interface Props {
  visible: boolean; // Modal visibility flag
  updateLoading?: boolean; // Loading state for update operations
  type?: 'User' | 'Group' | 'Tag' // Type of association (User, Group, or Tag)
  userId?: string; // User ID for user associations
  tagId?: string; // Tag ID for tag associations
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  type: undefined,
  userId: undefined,
  tagId: undefined
});

/**
 * Component emits definition
 * Defines events that this component can emit to parent
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void, // Update modal visibility
  (e: 'change', ids: string[], delIds: string[]): void // Handle department changes
}>();

// Internationalization setup
const { t } = useI18n();

/**
 * Handle modal confirmation
 * Calculates added and deleted department IDs and emits change event
 */
const handleOk = () => {
  const addIds = checkedKeys.value.filter(item => !oldDeptIds.value.includes(item));
  const delIds = oldDeptIds.value.filter(item => !checkedKeys.value.includes(item));
  emit('change', addIds, delIds);
};

/**
 * Handle modal cancellation
 * Emits update event to close modal
 */
const handleCancel = () => {
  emit('update:visible', false);
};

/**
 * Reactive state management for component
 */
const params = ref<{ tagId?: string, fullTextSearch: boolean, pageSize: number }>({ fullTextSearch: true, pageSize: 100 }); // Search parameters
const notify = ref(0); // Notification counter for tree refresh
const oldDeptIds = ref<string[]>([]); // Previously selected department IDs
const checkedKeys = ref<string[]>([]); // Currently checked department keys

/**
 * Double-click a row to toggle its selection state in the tree
 */
const handleNodeDblClick = (item: { id: string }): void => {
  const id = String(item?.id ?? '');
  if (!id) return;
  const index = checkedKeys.value.indexOf(id);
  if (index >= 0) {
    checkedKeys.value = checkedKeys.value.filter(k => k !== id);
  } else {
    checkedKeys.value = [...checkedKeys.value, id];
  }
};

/**
 * Load user departments for association
 * Fetches existing department associations for a user
 * @param ids - Array of department IDs to check
 */
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

/**
 * Load tag departments for association
 * Fetches existing department associations for a tag
 * @param ids - Array of department IDs to check
 */
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

/**
 * Handle tree node loading completion
 * Loads existing associations when new departments are loaded
 * @param options - Array of loaded department options
 */
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

/**
 * Handle tag selection change
 * Updates search parameters when tag filter changes
 * @param value - Selected tag ID
 */
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

/**
 * Tag selection state
 */
const selectTagId = ref(undefined);

/**
 * Computed search parameters for department selection
 * Includes tag filter if selected
 */
const getSearchDeptParams = computed(() => {
  return {
    tagId: selectTagId.value,
    fullTextSearch: true
  };
});

/**
 * Tree data for department hierarchy display
 */
const treeData = ref<DataType[]>([]);

/**
 * Handle department selection change
 * Loads department hierarchy and existing associations
 * @param value - Selected department ID
 */
const deptChange = async (value: any) => {
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
  await loadUserDept([res.data.current.id]);
};

/**
 * Build tree options with required id/name as string, filtering invalid nodes
 */
const treeOptions = computed(() => {
  return treeData.value
    .filter(item => item?.id && item?.name)
    .map(item => ({
      ...item,
      id: String(item.id),
      name: String(item.name)
    }));
});
</script>
<template>
  <!-- Department association modal -->
  <Modal
    :title="t('department.assocTitle')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3 pl-3">
      <!-- Search and filter controls -->
      <div class="flex mb-2">
        <!-- Department selection dropdown -->
        <Select
          :placeholder="t('department.placeholder.name')"
          :action="`${GM}/dept`"
          size="small"
          class="w-1/2 mr-2"
          :showSearch="true"
          :params="getSearchDeptParams"
          :allowClear="true"
          :fieldNames="{ label: 'name', value: 'id' }"
          @change="deptChange" />

        <!-- Tag filter dropdown -->
        <Select
          v-model:value="selectTagId"
          :placeholder="t('department.placeholder.tag')"
          :action="`${GM}/org/tag`"
          :fieldNames="{ label: 'name', value: 'id' }"
          :params="{fullTextSearch: true}"
          showSearcht
          allowClear
          class="w-1/2"
          @change="tagChange" />
      </div>

      <!-- Table header -->
      <div class="flex py-1 bg-theme-form-head text-theme-title text-3 font-normal">
        <div class="w-1/2 pl-10 mr-2">
          {{ t('common.columns.name') }}
        </div>
        <div class="w-1/2 pl-10 mr-2">
          {{ t('common.columns.code') }}
        </div>
      </div>

      <!-- Department tree with checkboxes -->
      <Tree
        v-model:checkedKeys="checkedKeys"
        :action="`${GM}/dept?`"
        :fieldNames="{ title: 'name', key: 'id', children: 'hasSubDept' }"
        :checkable="true"
        :params="params"
        :notify="notify"
        :treeData="treeOptions as any"
        class="-ml-5"
        style="height: 308px;"
        @loaded="loaded">
        <!-- Custom tree node template -->
        <template #default="item">
          <div class="flex" @dblclick.stop="handleNodeDblClick(item)">
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
