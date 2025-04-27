<script setup lang="ts">
import { ref } from 'vue';
import { Modal, Input, Icon, IconRequired } from '@xcan/design';

import { setting } from '@/api';

interface Props {
  visible: boolean;
  defaults: number;
  max: number;
  min: number;
  name: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  defaults: 0,
  max: 0,
  min: 0,
  name: undefined
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void, (e: 'ok'): void }>();

const newQuota = ref(props.defaults);

const handleOk = async () => {
  if (!newQuota.value) {
    return;
  }
  const [error] = await setting.updateTenantQuota(props.name, newQuota.value);
  if (error) {
    return;
  }
  emit('update:visible', false);
  emit('ok');
};
const handleCancel = () => {
  emit('update:visible', false);
};
</script>
<template>
  <Modal
    title="修改配额"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    width="540px"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="text-3 leading-3 text-theme-sub-content mb-4 flex items-center">
      <Icon class="text-blue-tips text-3.5 mr-1" icon="icon-tishi1" />
      允许最小配额<em class="not-italic mr-1">:</em>
      <span class="text-theme-content">{{ props.min }}</span>
      <Icon class="text-blue-tips text-3.5 ml-5 mr-1" icon="icon-tishi1" />
      允许最大配额<em class="not-italic mr-1">:</em>
      <span class="text-theme-content">{{ props.max }}</span>
    </div>
    <div class="flex items-center space-x-2 whitespace-nowrap text-3 leading-3">
      <IconRequired class="mr-1" />
      <span>当前配额</span>
      <Input
        v-model:value="newQuota"
        dataType="number"
        :class="{'border-err':!newQuota}"
        :placeholder="`${props.min}~${props.max}`"
        :min="props.min"
        :max="props.max" />
    </div>
  </Modal>
</template>
<style scoped>
.border-err {
  border-color: #ff4d4f;
}
</style>
