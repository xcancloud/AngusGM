<script setup lang="ts">
import { defineAsyncComponent, ref, watch, onMounted } from 'vue';
import { Quill } from '@xcan-angus/vue-ui';
import '@xcan-angus/quill/dist/quill.snow.css';
import '@xcan-angus/quill/dist/quill.core.css';

const Browser = defineAsyncComponent(() => import('./browser/index.vue'));

interface Props {
  value: string;
  uploadOptions?: {
    bizKey: string;
  } & Record<string, string>;
  disabled: boolean;
  mode: 'edit'|'view';
  height: number;
  options?: {[key: string]: any};
  toolbarOptions?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  uploadOptions: () => ({ bizKey: 'angusTesterEditorFile' }),
  disabled: false,
  mode: 'edit',
  height: 200,
  options: () => ({}),
  toolbarOptions: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:value', value: string);
  (e: 'change', value: string);
}>();

const quillRef = ref();
const contents = ref([]);
const isQuillValue = ref(true);
const loaded = ref(false);

onMounted(() => {
  watch(() => props.value, () => {
    loaded.value = false;
    try {
      if (props.value) {
        const values = JSON.parse(props.value);
        if (typeof values !== 'object') {
          contents.value = [{insert: props.value}];
        } else {
          contents.value = values || [];
        }
      } else {
        contents.value = [];;
      }
      isQuillValue.value = true;
    } catch {
      isQuillValue.value = false;
    } finally {
      loaded.value = true;
    }
  }, {
    immediate: true
  });


  watch(() => contents.value, () => {
    if (contents.value?.length) {
      emit('update:value', JSON.stringify(contents.value));
      emit('change', JSON.stringify(contents.value));
    } else {
      emit('update:value', '');
      emit('change', '');
    }
  });
});

const isMax = ref(false);
const zoomHandler = (type) => {
  if (type === 'max') {
    isMax.value = true;
  } else {
    isMax.value = false;
  }
};

defineExpose({
  getLength: () => {
    return quillRef.value && quillRef.value.getLength();
  },
  getText: () => {
    return quillRef.value && quillRef.value.getText();
  },
  getData: () => {
    if (contents.value?.length) {
      if (contents.value?.length === 1) {
        const content = (contents.value[0]?.insert || '').replace('\n', '');
        if (!content?.length) {
          return undefined;
        }
      }
      return JSON.stringify(contents.value);
    } else {
      return undefined;
    }
  }
});

</script>
<template>
  <template v-if="!isQuillValue && props.mode === 'view' && loaded">
    <Browser :value="props.value" />
  </template>
  <template v-else-if="loaded">
    <div :class="{'fixed z-999 bg-white left-0 top-0 bottom-0 right-0 flex flex-col': isMax}">
      <Quill
        v-model:value="contents"
        ref="quillRef"
        :toolbarOptions="props.toolbarOptions"
        :options="props.options"
        :mode="props.mode"
        :disabled="props.disabled"
        :uploadOptions="props.uploadOptions"
        :style="props.mode === 'edit' && `height: ${props.height}px;`"
        :class="[`ql-${props.mode}-mode`, 'flex-1']"
        :zoomHandler="zoomHandler" />
    </div>
  </template>
</template>
<style>
.ql-view-mode.ql-container .ql-editor {
  padding: 8px !important;
}

.ql-toolbar.ql-snow[role='toolbar'] {
  border-color: #e5e7eb;
}

.ql-toolbar.ql-snow+.ql-container.ql-snow.ql-edit-mode {
  border-color: #e5e7eb;
}

.ql-toolbar.ql-snow[role='toolbar'] > .ql-formats {
  margin-right: 0;
}

</style>
