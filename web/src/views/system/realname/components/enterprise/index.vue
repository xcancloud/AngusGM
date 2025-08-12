<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import certFrontPicUrl from '../../images/idCardFront.png';
import certBackPicUrl from '../../images/idCardBack.png';
import businessCertPicUrl from '../../images/businessCert.png';

import PageCard from '@/views/system/realname/components/cardPage/index.vue';

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
 * @param data - Enterprise certification data from API
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
  <div class="flex space-x-2 flex-1">
    <!-- Enterprise authentication summary card -->
    <PageCard
      :pageTitle="t('realName.titles.enterpriseAuth')"
      icon="icon-qiyerenzheng"
      :contents="contents"
      :success="form.status === 'AUDITED'" />

    <!-- Enterprise certification details card -->
    <Card
      :title="t('realName.titles.enterpriseAuth')"
      class=" flex-1"
      bodyClass="px-8 py-5 text-3 leading-3">
      <!-- Company name field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('realName.columns.enterpriseName') }}</div>
        <div>{{ form.companyName }}</div>
      </div>

      <!-- Business license number field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('realName.columns.creditCode') }}</div>
        <div>{{ form.creditCode }}</div>
      </div>

      <!-- Legal person name field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('realName.columns.legalName') }}</div>
        <div>{{ form.name }}</div>
      </div>

      <!-- Legal person ID number field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('realName.columns.legalIdCard') }}
        </div>
        <div>{{ form.certNo }}</div>
      </div>

      <!-- ID card images -->
      <div class="flex mt-8.75">
        <div class="mr-7.5">
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="certFrontPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('realName.columns.certFront') }}</p>
        </div>
        <div class="mr-7.5">
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="certBackPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('realName.columns.certBack') }}</p>
        </div>
        <div>
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="businessCertPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('realName.columns.businessLicense') }}</p>
        </div>
      </div>
    </Card>
  </div>
</template>
