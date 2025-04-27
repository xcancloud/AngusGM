<script setup lang="ts">
import { ref, watch } from 'vue';
import { Modal } from '@xcan-angus/vue-ui';

import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';

const viewMarkVue = mavonEditor.mavonEditor;

interface Props {
  value: string,
  visible: boolean
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});
const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const cancel = () => {
  emit('update:visible', false);
};

const values = ref('');
watch(() => props.visible, newValue => {
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
    :title="$t('table-operate2')"
    :footer="null"
    :visible="props.visible"
    :width="700"
    @cancel="cancel">
    <div style="min-height: 550px;max-height: 70vh;overflow-y: auto;">
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
