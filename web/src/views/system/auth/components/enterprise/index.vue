<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan-angus/vue-ui';
import certFrontPicUrl from '../../images/idCardFront.png';
import certBackPicUrl from '../../images/idCardBack.png';
import businessCertPicUrl from '../../images/businessCert.png';

import PageCard from '@/views/system/auth/components/cardPage/index.vue';

const { t } = useI18n();

interface Props {
  data: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
});

const auditType = ['AUDITING', 'FAILED_AUDIT'];

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

// 状态组件展示
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

onMounted(async () => {
  start(props.data);
});

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

const contents = [
  t('适用于企业用户，账号归属于企业；'),
  t('支持开增值税专用发票；'),
  t('一个证件只允许认证一个账号。')
];

</script>
<template>
  <div class="flex space-x-2 flex-1">
    <PageCard
      :pageTitle="t('企业认证')"
      icon="icon-qiyerenzheng"
      :contents="contents"
      :success="form.status === 'AUDITED'" />
    <Card
      :title="t('企业认证')"
      class=" flex-1"
      bodyClass="px-8 py-5 text-3 leading-3">
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('enterpriseLabel') }}</div>
        <div>{{ form.companyName }}</div>
      </div>
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('buinessNumber') }}</div>
        <div>{{ form.creditCode }}</div>
      </div>
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">{{ t('legalName') }}</div>
        <div>{{ form.name }}</div>
      </div>
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('legalIdNumber') }}
        </div>
        <div>{{ form.certNo }}</div>
      </div>
      <div class="flex mt-8.75">
        <div class="mr-7.5">
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="certFrontPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('cardFront') }}</p>
        </div>
        <div class="mr-7.5">
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="certBackPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('cardBack') }}</p>
        </div>
        <div>
          <div class="w-54 h-33.5 rounded overflow-hidden flex justify-center items-center border-theme-divider border">
            <img class="w-full" :src="businessCertPicUrl" />
          </div>
          <p class="text-center text-black-content mt-3">{{ t('businessCard') }}</p>
        </div>
      </div>
    </Card>
  </div>
</template>
