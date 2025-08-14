<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Modal } from '@xcan-angus/vue-ui';
import type { TreeProps } from 'ant-design-vue';
import { Tree } from 'ant-design-vue';
import { appContext } from '@xcan-angus/infra';

import { dept } from '@/api';
import { MoveProps, MoveEmits } from '../types';

// Component props with default values
const props = withDefaults(defineProps<MoveProps>(), {
  visible: false,
  moveId: '',
  defaultPid: ''
});

// Component emit events
const emit = defineEmits<MoveEmits>();

// Internationalization instance
const { t } = useI18n();

// Selected tree node keys for move operation
const selectedKeys = ref<string[]>([]);

/**
 * Handle move confirmation
 * Validates that target department is different from current parent
 * Emits the selected target department ID
 */
const handleOk = () => {
  const targetId = selectedKeys.value[0];
  // Prevent moving to the same parent department
  if (targetId === props.defaultPid) {
    emit('update:visible', false);
    return;
  }
  emit('ok', targetId);
};

/**
 * Handle modal close
 * Emits event to update visibility state
 */
const handleClose = () => {
  emit('update:visible', false);
};

// Initialize tree data with root node (tenant name or 'Root')
const treeData = ref<{ name: string, id: string }[]>([{
  name: appContext.getTenant()?.name || 'Root',
  id: '-1'
}]);

/**
 * Lazy load tree data for department hierarchy
 * Fetches child departments when expanding a node
 * Filters out the department being moved to prevent circular references
 */
const onLoadData: TreeProps['loadData'] = treeNode => {
  const { id } = treeNode;

  // Fetch department list for the current node
  return dept.getDeptList({
    pid: id,
    pageSize: 2000,
    orderBy: 'createdDate',
    orderSort: 'ASC'
  })
    .then(([error, resp]) => {
      if (error) {
        return;
      }

      // Skip if children already loaded
      if (treeNode.dataRef?.children) {
        return;
      }

      if (treeNode.dataRef) {
        // Filter out the department being moved and map to tree structure
        treeNode.dataRef.children = (resp.data.list || [])
          .filter(data => data.id !== props.moveId)
          .map(item => ({
            ...item,
            isLeaf: !item.hasSubDept
          }));
      }

      // Force reactivity update by creating new array reference
      treeData.value = [...treeData.value];
    });
};
</script>
<template>
  <Modal
    :title="t('department.actions.moveDept')"
    :visible="props.visible"
    :okButtonProps="{
      disabled: !selectedKeys.length
    }"
    @ok="handleOk"
    @cancel="handleClose">
    <Tree
      v-if="props.visible"
      v-model:selectedKeys="selectedKeys"
      class="max-h-100 overflow-y-auto text-3"
      :loadData="onLoadData"
      :treeData="treeData"
      :fieldNames="{title: 'name', key: 'id', children:'children'}">
    </Tree>
  </Modal>
</template>
