<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import certFrontPicUrl from '../images/idCardFront.png';
import certBackPicUrl from '../images/idCardBack.png';

import PageCard from '@/views/system/realname/cardPage/index.vue';

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
  t('realName.messages.personalAuthDesc'),
  t('realName.messages.personalAuthDesc2'),
  t('realName.messages.personalAuthDesc3'),
  t('realName.messages.personalAuthDesc4')
];

</script>

<template>
  <div class="flex space-x-6">
    <!-- Personal authentication summary card -->
    <PageCard
      :contents="contents"
      :pageTitle="t('realName.titles.personalAuth')"
      icon="icon-gerenrenzheng"
      :success="form.status === 'AUDITED'"
      theme="personal" />

    <!-- Personal certification details card -->
    <Card
      :title="t('realName.titles.personalAuth')"
      class="flex-1 shadow-lg hover:shadow-xl transition-all duration-300 bg-gradient-to-br from-white to-purple-50"
      bodyClass="px-10 py-8">
      <!-- Name field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-purple-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-purple-500 rounded-full mr-3"></span>
          {{ t('realName.columns.name') }}
        </div>
        <div class="text-gray-800 font-semibold">{{ form.name }}</div>
      </div>

      <!-- ID card number field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-purple-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
          {{ t('realName.columns.idCard') }}
        </div>
        <div class="text-gray-800 font-semibold font-mono">{{ form.certNo }}</div>
      </div>

      <!-- ID card images -->
      <div class="flex mt-10 space-x-8">
        <div class="group">
          <div class="w-72 h-44 rounded-xl flex items-center justify-center overflow-hidden border-2 border-gray-200 group-hover:border-purple-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img :src="certFrontPicUrl" class="w-full h-full object-cover" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-purple-600 transition-colors duration-200">
            {{ t('realName.columns.certFront') }}
          </p>
        </div>
        <div class="group">
          <div class="w-72 h-44 rounded-xl flex items-center justify-center overflow-hidden border-2 border-gray-200 group-hover:border-purple-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img :src="certBackPicUrl" class="w-full h-full object-cover" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-purple-600 transition-colors duration-200">
            {{ t('realName.columns.certBack') }}
          </p>
        </div>
      </div>
    </Card>
  </div>
</template>
