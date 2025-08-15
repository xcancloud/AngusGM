<script setup lang="ts">
import { ref, watch } from 'vue';
import { Modal } from '@xcan-angus/vue-ui';

import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';

import { ViewProps } from './types';

// Markdown editor component
const viewMarkVue = mavonEditor.mavonEditor;

// Component props with proper typing
const props = withDefaults(defineProps<ViewProps>(), {
  visible: false
});

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
}>();

// Component state
const values = ref<string>('');

/**
 * Cancel modal and close it
 */
const cancel = (): void => {
  emit('update:visible', false);
};

// Watch for visible changes to update content
watch(() => props.visible, (newValue) => {
  if (newValue) {
    values.value = props.value;
  }
}, {
  immediate: true
});
</script>

<template>
  <Modal
    class="rounded"
    :title="$t('event.records.messages.viewEventContent')"
    :footer="null"
    :visible="props.visible"
    :width="700"
    @cancel="cancel">
    <!-- Markdown content viewer -->
    <div style="min-height: 550px; max-height: 70vh; overflow-y: auto;">
      <viewMarkVue
        v-model="values"
        class="!text-3 -mt-2"
        defaultOpen="preview"
        :editable="false"
        :subfield="false"
        :toolbarsFlag="false"
        :scrollStyle="true"
        :boxShadow="false">
      </viewMarkVue>
    </div>
  </Modal>
</template>
