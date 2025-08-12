<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import PageCard from '@/views/system/realname/components/cardPage/index.vue';
import { Card } from '@xcan-angus/vue-ui';
import orgCertPicUrl from '../../images/orgCert.png';

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
 * @param data - Government certification data from API
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
  <div class="flex space-x-2 flex-1">
    <!-- Government organization authentication summary card -->
    <PageCard
      :pageTitle="t('realName.titles.governmentAuth')"
      icon="icon-shiyedanweirenzheng"
      :contents="contents"
      :count="3"
      :success="form.status === 'AUDITED'" />

    <!-- Government organization certification details card -->
    <Card
      :title="t('realName.titles.governmentAuth')"
      class=" flex-1"
      bodyClass="px-8 py-5">
      <!-- Organization name field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('realName.columns.orgName') }}
        </div>
        <div>{{ form.name }}</div>
      </div>

      <!-- Organization code field -->
      <div class="flex py-3.75">
        <div class="text-black-content w-28 mr-7.5">
          {{ t('realName.columns.orgCode') }}
        </div>
        <div>{{ form.orgCode }}</div>
      </div>

      <!-- Organization certificate image -->
      <div class="flex mt-8.75">
        <div class="mr-7.5">
          <div class="w-90 h-56  rounded overflow-hidden flex items-center justify-center border-theme-divider border">
            <img :src="orgCertPicUrl" class="w-full" />
          </div>
          <p class="text-center text-black-content mt-3">
            {{ t('realName.columns.orgCertificate') }}
          </p>
        </div>
      </div>
    </Card>
  </div>
</template>
