<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { Icon, PureCard } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';
import { Button } from 'ant-design-vue';

type status = 'AUDITING' | 'FAILED_AUDIT';

interface Props {
  status: status,
  visible: boolean,
  cancel?: boolean,
  confirm?: boolean,
  message?: string,
  backUrl?: string
}

const props = withDefaults(defineProps<Props>(), {
  status: 'FAILED_AUDIT',
  visible: true,
  cancel: true,
  confirm: true,
  message: '',
  backUrl: '/'
});

const { t } = useI18n();

const emit = defineEmits<{(e: 'reAudit'): void }>();

const reAudit = function () {
  emit('reAudit');
};

</script>
<template>
  <PureCard class="flex-1 w-full flex flex-col justify-center py-25 text-center">
    <template v-if="props.status === 'AUDITING'">
      <div>
        <Icon
          icon="icon-tijiaoshenhezhong"
          class="text-success mb-10"
          style="font-size: 120px;" />
      </div>
      <p class="text-theme-content mb-5 font-medium">{{ t('您提交的认证资料正在审核中...') }}</p>
      <p class="text-theme-content">{{ t('请耐心等待，我们会在1-3个工作日内完成') }}</p>
    </template>
    <template v-else>
      <div>
        <Icon
          icon="icon-shenheshibai"
          class="text-danger mb-10"
          style="font-size: 120px;" />
      </div>
      <p class="text-theme-content mb-5 font-medium">{{ t('非常遗憾，审核未通过！') }}</p>
      <p class="text-theme-content">{{ t('不通过原因：') }}{{ message }}</p>
    </template>
    <div class="mt-15">
      <Button
        v-if="props.status === 'FAILED_AUDIT' && confirm"
        size="small"
        class="mr-5"
        type="primary"
        :disabled="!app.has('ReCertification')"
        @click="reAudit">
        {{ app.getName('ReCertification') }}
      </Button>
    </div>
  </PureCard>
</template>
