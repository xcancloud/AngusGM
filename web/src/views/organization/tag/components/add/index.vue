<script setup lang="ts">
import {ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {IconRequired, Input, Modal} from '@xcan-angus/vue-ui';

interface Props {
  visible: boolean;
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  loading: false
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void, (e: 'ok', name: string | undefined): void }>();

const { t } = useI18n();

const name = ref<string | undefined>(undefined);

const handleOk = async (): Promise<void> => {
  if (!name.value) {
    showRule.value = true;
    return;
  }
  emit('ok', name.value);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const showRule = ref(false);
const handleChange = (event: any) => {
  showRule.value = !event.target.value;
};
</script>
<template>
  <Modal
    width="540px"
    :title="t('addTitle',{name:t('label')})"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :confirmLoading="props.loading"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="flex items-start">
      <div class="whitespace-nowrap mr-2 pt-1">
        <IconRequired class="mr-1" />
        {{ t('name') }}
      </div>
      <div class="flex-1">
        <Input
          v-model:value="name"
          :maxlength="100"
          :placeholder="t('标签名称')"
          @change="handleChange" />
        <div class="h-6 text-rule text-3 leading-3 mt-1">
          <template v-if="showRule">
            {{ t('tagPlaceholder3') }}
          </template>
        </div>
      </div>
    </div>
  </Modal>
</template>
