<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import certFrontPicUrl from '../images/idCardFront.png';
import certBackPicUrl from '../images/idCardBack.png';
import businessCertPicUrl from '../images/businessCert.png';

import PageCard from '@/views/system/realname/cardPage/index.vue';

const { t } = useI18n();

interface Props {
  data: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// Audit status types that require status display
const auditType = ['AUDITING', 'FAILED_AUDIT'];

// Enterprise certification form data
const form = ref({
  name: '',
  certNo: '',
  creditCode: '',
  companyName: '',
  certBackPicUrl: '',
  certFrontPicUrl: '',
  businessLicensePicUrl: '',
  status: '',
  reason: ''
});

// Status component display options
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

onMounted(async () => {
  start(props.data);
});

/**
 * Initialize enterprise certification form data
 */
function start (data: any) {
  form.value = {
    ...data.enterpriseCert,
    companyName: data.enterpriseCert.name,
    ...data.enterpriseLegalPersonCert,
    status: data.status.value,
    reason: data?.auditRecord?.reason
  };
  statusOption.visible = auditType.includes(form.value.status);
  statusOption.reason = form.value.reason;
}

// Enterprise authentication content descriptions
const contents = [
  t('realName.messages.enterpriseAuthDesc'),
  t('realName.messages.enterpriseAuthDesc2'),
  t('realName.messages.enterpriseAuthDesc3')
];

</script>

<template>
  <div class="flex space-x-6 flex-1">
    <!-- Enterprise authentication summary card -->
    <PageCard
      :pageTitle="t('realName.titles.enterpriseAuth')"
      icon="icon-qiyerenzheng"
      :contents="contents"
      :success="form.status === 'AUDITED'"
      theme="enterprise" />

    <!-- Enterprise certification details card -->
    <Card
      :title="t('realName.titles.enterpriseAuth')"
      class="flex-1 shadow-lg hover:shadow-xl transition-all duration-300 bg-gradient-to-br from-white to-gray-50"
      bodyClass="px-10 py-8 text-3 leading-3">
      <!-- Company name field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-blue-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
          {{ t('realName.columns.enterpriseName') }}
        </div>
        <div class="text-gray-800 font-semibold">{{ form.companyName }}</div>
      </div>

      <!-- Business license number field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-blue-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
          {{ t('realName.columns.creditCode') }}
        </div>
        <div class="text-gray-800 font-semibold font-mono">{{ form.creditCode }}</div>
      </div>

      <!-- Legal person name field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-blue-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-purple-500 rounded-full mr-3"></span>
          {{ t('realName.columns.legalName') }}
        </div>
        <div class="text-gray-800 font-semibold">{{ form.name }}</div>
      </div>

      <!-- Legal person ID number field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-blue-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-orange-500 rounded-full mr-3"></span>
          {{ t('realName.columns.legalIdCard') }}
        </div>
        <div class="text-gray-800 font-semibold font-mono">{{ form.certNo }}</div>
      </div>

      <!-- ID card images -->
      <div class="flex mt-10 space-x-8">
        <div class="group">
          <div class="w-56 h-36 rounded-xl overflow-hidden flex justify-center items-center border-2 border-gray-200 group-hover:border-blue-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img class="w-full h-full object-cover" :src="certFrontPicUrl" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-blue-600 transition-colors duration-200">
            {{ t('realName.columns.certFront') }}
          </p>
        </div>
        <div class="group">
          <div class="w-56 h-36 rounded-xl overflow-hidden flex justify-center items-center border-2 border-gray-200 group-hover:border-blue-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img class="w-full h-full object-cover" :src="certBackPicUrl" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-blue-600 transition-colors duration-200">
            {{ t('realName.columns.certBack') }}
          </p>
        </div>
        <div class="group">
          <div class="w-56 h-36 rounded-xl overflow-hidden flex justify-center items-center border-2 border-gray-200 group-hover:border-blue-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img class="w-full h-full object-cover" :src="businessCertPicUrl" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-blue-600 transition-colors duration-200">
            {{ t('realName.columns.businessLicense') }}
          </p>
        </div>
      </div>
    </Card>
  </div>
</template>
