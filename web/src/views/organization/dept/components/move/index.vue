<script setup lang="ts">
import { ref } from 'vue';
import { Modal } from '@xcan-angus/vue-ui';
import type { TreeProps } from 'ant-design-vue';
import { Tree } from 'ant-design-vue';
import { appContext } from '@xcan-angus/infra';

import { dept } from '@/api';

interface Props {
  visible: boolean;
  moveId?: string;
  defaultPid?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  moveId: '',
  defaultPid: ''
});

const emit = defineEmits<{(e: 'ok', value: string): void, (e: 'update:visible', value: boolean): void }>();
const selectedKeys = ref<string[]>([]);

const handleOk = () => {
  const targetId = selectedKeys.value[0];
  if (targetId === props.defaultPid) {
    emit('update:visible', false);
    return;
  }
  emit('ok', targetId);
};

const handleClose = () => {
  emit('update:visible', false);
};

const treeData = ref<{ name: string, id: string }[]>([{ name: appContext.getTenant()?.name, id: '-1' }]);

const onLoadData: TreeProps['loadData'] = treeNode => {
  const { id } = treeNode;
  return dept.getDeptList({ pid: id, pageSize: 2000, orderBy: 'createdDate', orderSort: 'ASC' })
    .then(([error, resp]) => {
      if (error) {
        return;
      }
      if (treeNode.dataRef?.children) {
        return;
      }
      if (treeNode.dataRef) {
        treeNode.dataRef.children = (resp.data.list || [])
          .filter(data => data.id !== props.moveId)
          .map(item => ({ ...item, isLeaf: !item.hasSubDept }));
      }
      treeData.value = [...treeData.value];
    });
};

</script>
<template>
  <Modal
    title="移动部门"
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
