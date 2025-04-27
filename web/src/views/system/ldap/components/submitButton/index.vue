<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';

interface Props {
  testLoading: boolean,
  saveLoading: boolean
}

withDefaults(defineProps<Props>(), {
  testLoading: false,
  saveLoading: false
});

const emit = defineEmits(['testConfig', 'saveConfig']);

const { t } = useI18n();
const testButton = ref();
const router = useRouter();

const saveConfig = function () {
  emit('saveConfig');
};

const goBackList = () => {
  router.push('/system/ldap');
};
// 触发测试点击
const testClick = function () {
  testButton.value.onClick();
};

defineExpose({ testClick });

</script>
<template>
  <div class="my-10 -ml-50">
    <Button
      size="small"
      :loading="saveLoading"
      :disabled="testLoading"
      type="primary"
      @click="saveConfig">
      {{ t('systemLdap.detail-options-2') }}
    </Button>
    <Button
      size="small"
      class="ml-2"
      :loading="saveLoading"
      :disabled="testLoading"
      @click="goBackList">
      {{ t('cancel') }}
    </Button>
  </div>
</template>
