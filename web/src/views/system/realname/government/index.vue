<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import PageCard from '@/views/system/realname/cardPage/index.vue';
import { Card } from '@xcan-angus/vue-ui';
import orgCertPicUrl from '../images/orgCert.png';

const { t } = useI18n();

interface Props {
  data: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// Audit status types that require status display
const auditType = ['AUDITING', 'FAILED_AUDIT'];

// Government organization certification form data
const form = ref({
  name: '',
  orgCode: '',
  orgCertPicUrl: '',
  status: '',
  reason: ''
});

// Status component display options
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

onMounted(() => {
  start(props.data);
});

/**
 * Initialize government organization certification form data
 */
function start (data: any) {
  form.value = { ...data.governmentCert, status: data.status.value, reason: data?.auditRecord?.reason };
  statusOption.visible = auditType.includes(form.value.status);
  statusOption.reason = form.value.reason;
}

// Government organization authentication content descriptions
const contents = [
  t('realName.messages.governmentAuthDesc'),
  t('realName.messages.governmentAuthDesc2'),
  t('realName.messages.governmentAuthDesc3')
];

</script>

<template>
  <div class="flex space-x-6 flex-1">
    <!-- Government organization authentication summary card -->
    <PageCard
      :pageTitle="t('realName.titles.governmentAuth')"
      icon="icon-shiyedanweirenzheng"
      :contents="contents"
      :count="3"
      :success="form.status === 'AUDITED'"
      theme="government" />

    <!-- Government organization certification details card -->
    <Card
      :title="t('realName.titles.governmentAuth')"
      class="flex-1 shadow-lg hover:shadow-xl transition-all duration-300 bg-gradient-to-br from-white to-green-50"
      bodyClass="px-10 py-8">
      <!-- Organization name field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-green-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
          {{ t('realName.columns.orgName') }}
        </div>
        <div class="text-gray-800 font-semibold">{{ form.name }}</div>
      </div>

      <!-- Organization code field -->
      <div class="flex py-4 border-b border-gray-100 hover:bg-green-50 transition-colors duration-200 rounded-lg px-3">
        <div class="text-gray-600 font-medium w-52 mr-8 flex items-center">
          <span class="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
          {{ t('realName.columns.orgCode') }}
        </div>
        <div class="text-gray-800 font-semibold font-mono">{{ form.orgCode }}</div>
      </div>

      <!-- Organization certificate image -->
      <div class="flex mt-10">
        <div class="group">
          <div class="w-96 h-60 rounded-xl overflow-hidden flex items-center justify-center border-2 border-gray-200 group-hover:border-green-400 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
            <img :src="orgCertPicUrl" class="w-full h-full object-cover" />
          </div>
          <p class="text-center text-gray-600 mt-4 font-medium group-hover:text-green-600 transition-colors duration-200">
            {{ t('realName.columns.orgCertificate') }}
          </p>
        </div>
      </div>
    </Card>
  </div>
</template>
