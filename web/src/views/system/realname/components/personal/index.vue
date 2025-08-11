<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import certFrontPicUrl from '../../images/idCardFront.png';
import certBackPicUrl from '../../images/images/idCardBack.png';

import PageCard from '@/views/system/realname/components/cardPage/index.vue';

// Personal certification form interface
interface Form {
  certBackPicUrl: string
  certFrontPicUrl: string
  certNo: string
  name: string,
  status: string,
  reason: string
}

const { t } = useI18n();
const params = ref();

interface Props {
  data: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// Audit status types that require status display
const auditType = ['AUDITING', 'FAILED_AUDIT'];

// Form data for personal certification
const form = ref<Form>({
  certBackPicUrl: '',
  certFrontPicUrl: '',
  certNo: '',
  name: '',
  status: '',
  reason: ''
});

// Status component display options
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

/**
 * Initialize form data after receiving data
 * Ensures data is properly loaded and formatted
 */
const start = function () {
  form.value = {
    ...params.value.personalCert,
    status: params.value.status.value,
    reason: params.value?.auditRecord?.reason
  };
  statusOption.visible = auditType.includes(form.value.status);
  statusOption.reason = form.value.reason;
};

onMounted(() => {
  params.value = props.data;
  start();
});

// Personal authentication content descriptions
const contents = [
  t('realname.messages.personalAuthDesc'),
  t('realname.messages.personalAuthDesc2'),
  t('realname.messages.personalAuthDesc3'),
  t('realname.messages.personalAuthDesc4')
];

</script>

<template>
  <div class="flex space-x-2">
    <!-- Personal authentication summary card -->
    <PageCard
      :contents="contents"
      :pageTitle="t('realname.titles.personalAuth')"
      icon="icon-gerenrenzheng"
      :success="form.status === 'AUDITED'" />

    <!-- Personal certification details card -->
    <Card
      :title="t('realname.titles.personalAuth')"
      class="flex-1"
      bodyClass="px-8 py-5">
      <!-- Name field -->
      <div class="flex py-3.75">
        <div class="text-theme-content w-28 mr-7.5">{{ t('realname.columns.name') }}</div>
        <div>{{ form.name }}</div>
      </div>

      <!-- ID card number field -->
      <div class="flex py-3.75">
        <div class="text-theme-content w-28 mr-7.5">{{ t('realname.columns.idCard') }}</div>
        <div>{{ form.certNo }}</div>
      </div>

      <!-- ID card images -->
      <div class="flex mt-8.75">
        <div class="mr-15">
          <div class="w-67.5 h-42 rounded flex items-center justify-center overflow-hidden border-theme-divider border">
            <img :src="certFrontPicUrl" class="w-full" />
          </div>
          <p class="text-center text-theme-content mt-3">{{ t('realname.columns.certFront') }}</p>
        </div>
        <div>
          <div class="w-67.5 h-42 rounded flex items-center justify-center overflow-hidden border-theme-divider border">
            <img :src="certBackPicUrl" class="w-full" />
          </div>
          <p class="text-center text-theme-content mt-3">{{ t('realname.columns.certBack') }}</p>
        </div>
      </div>
    </Card>
  </div>
</template>
