<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import PageCard from '@/views/system/auth/components/cardPage/index.vue';
import { Card } from '@xcan-angus/vue-ui';
import orgCertPicUrl from '../../images/orgCert.png';

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
  orgCode: '',
  orgCertPicUrl: '',
  status: '',
  reason: ''
});

// 状态组件展示
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

onMounted(() => {
  start(props.data);
});

function start (data: any) {
  form.value = { ...data.governmentCert, status: data.status.value, reason: data?.auditRecord?.reason };
  statusOption.visible = auditType.includes(form.value.status);
  statusOption.reason = form.value.reason;
}

const contents = [
  t('适用于党政及国家机关、事业单位、民 办非企业单位、社会团体、个体工商户 等用户，账号归属于政府及事业单位；'),
  t('支持开增值税专用发票；'),
  t('一个证件只允许认证一个账号。')
];

</script>
<template>
  <div class="flex space-x-2 flex-1">
    <PageCard
      :pageTitle="t('政府及事业单位认证')"
      icon="icon-shiyedanweirenzheng"
      :contents="contents"
      :count="3"
      :success="form.status === 'AUDITED'" />
    <Card
      :title="t('政府及事业单位认证')"
      class=" flex-1"
      bodyClass="px-8 py-5">
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('单位名称') }}
        </div>
        <div>{{ form.name }}</div>
      </div>
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('orgName') }}
        </div>
        <div>{{ form.orgCode }}</div>
      </div>
      <div class="flex mt-8.75">
        <div class="mr-7.5">
          <div class="w-90 h-56  rounded overflow-hidden flex items-center justify-center border-theme-divider border">
            <img :src="orgCertPicUrl" class="w-full" />
          </div>
          <p class="text-center text-black-content mt-3">
            {{ t('govermentCard') }}
          </p>
        </div>
      </div>
    </Card>
  </div>
</template>
