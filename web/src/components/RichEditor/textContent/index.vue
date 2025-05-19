<script setup lang="ts">
import { ref, watch, onMounted, nextTick } from 'vue';
import { Quill } from '@xcan-angus/vue-ui';
import '@xcan-angus/quill/dist/quill.snow.css';
import '@xcan-angus/quill/dist/quill.core.css';

interface Props {
  value: string;
  uploadOptions?: {
    bizKey: string;
  };
  disabled?: boolean;
  mode?: 'edit'|'view';
  height?: number;
  options?: {[key: string]: any};
  emptyText?: string
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  mode: 'view',
  emptyText: ''
});

const quillRef = ref();
const contents = ref();
const isQuillValue = ref(true);
const loaded = ref(false);
const quillContents = ref([]);

onMounted(() => {
  watch(() => props.value, () => {
    try {
      if (props.value) {
        const values = JSON.parse(props.value);
        if (typeof values !== 'object') {
          isQuillValue.value = false;
          contents.value = props.value;
        } else {
          quillContents.value = values || [];
        }
      }
    } catch {
      contents.value = props.value;
      isQuillValue.value = false;
    } finally {
      loaded.value = true;
    }
    nextTick(() => {
      if (quillRef.value) {
        contents.value = quillRef.value.getText();
        contents.value && (contents.value = contents.value.replaceAll('\n', ''));
      }
    });
  }, {
    immediate: true
  });
});

</script>
<template>
  <template v-if="loaded">
    <template v-if="contents">{{ contents }}</template>
    <div v-else class="text-text-sub-content">{{ props.emptyText }}</div>
  </template>
  <template v-if="isQuillValue && loaded">
    <Quill
      ref="quillRef"
      v-model:value="quillContents"
      :mode="props.mode"
      :disabled="true"
      style="display: none;" />
  </template>
</template>
