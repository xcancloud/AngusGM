<script setup lang="ts">
import { Button } from 'ant-design-vue';

withDefaults(defineProps<{ confimLoading: boolean }>(), { confimLoading: false });

const emit = defineEmits(['confirm', 'cancel']);
const buttonClick = function (type: boolean) {
  emit(type ? 'confirm' : 'cancel');
};

</script>
<template>
  <div class="flex items-center justify-center">
    <Button
      size="small"
      type="primary"
      :loading="confimLoading"
      class="mr-5"
      @click="buttonClick(true)">
      确定
    </Button>
    <Button
      size="small"
      class="mr-22"
      @click="buttonClick(false)">
      取消
    </Button>
  </div>
</template>
