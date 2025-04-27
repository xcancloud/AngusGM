<script setup lang="ts">
import RichEditor from '@xcan/editor';
import { computed } from 'vue';

interface Props {
  options?: Record<string, any>;
}

const props = withDefaults(defineProps<Props>(), {
  options: () => ({
    menubar: false
  })
});

const initOptions = computed(() => {
  return {
    ...props.options,
    content_style: 'body{font-size: 12px; };.mce-content-body[data-mce-placeholder]:not(.mce-visualblocks)::before{color:#BFBFBF;font-size:12px;}'
  };
});
</script>
<template>
  <RichEditor :options="initOptions" />
</template>
