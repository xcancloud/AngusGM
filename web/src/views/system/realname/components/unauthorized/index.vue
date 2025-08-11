<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { TenantType, app } from '@xcan-angus/infra';

import AuthList from '@/views/system/realname/components/authList/index.vue';
import { tenant } from '@/api';

const { t } = useI18n();
const route = useRoute();

const emit = defineEmits<{(e: 'clickAuth', type: TenantType) }>();
const params = route.params;
const form = ref<any>({});

onMounted(async () => {
  if (typeof params?.data === 'string') {
    form.value = JSON.parse(params.data);
  } else {
    const [, res] = await tenant.getCertAudit();
    if (res) {
      form.value = res.data || {};
    }
  }
});

// Enterprise authentication content descriptions
const contentsEnterprise = [
  t('realname.messages.enterpriseAuthDesc'),
  t('realname.messages.enterpriseAuthDesc2'),
  t('realname.messages.enterpriseAuthDesc3')
];

// Government organization authentication content descriptions
const contentsGovernment = [
  t('realname.messages.governmentAuthDesc'),
  t('realname.messages.governmentAuthDesc2'),
  t('realname.messages.governmentAuthDesc3')
];

// Personal authentication content descriptions
const contentsPerson = [
  t('realname.messages.personalAuthDesc'),
  t('realname.messages.personalAuthDesc2'),
  t('realname.messages.personalAuthDesc3'),
  t('realname.messages.personalAuthDesc4')
];

/**
 * Handle authentication type selection
 * @param type - Authentication type (PERSONAL, ENTERPRISE, GOVERNMENT)
 */
const handleClick = (type: TenantType) => {
  emit('clickAuth', type);
};

</script>

<template>
  <!-- Personal authentication option -->
  <AuthList
    :id="form.id"
    :pageTitle="t('realname.titles.personalAuth')"
    :contents="contentsPerson"
    :disbaled="!app.has('PersonalCertification')"
    icon="icon-gerenrenzheng"
    @clickAuth="handleClick(TenantType.PERSONAL)">
  </AuthList>

  <!-- Enterprise authentication option -->
  <AuthList
    :id="form.id"
    :pageTitle="t('realname.titles.enterpriseAuth')"
    :contents="contentsEnterprise"
    :disbaled="!app.has('EnterpriseCertification')"
    icon="icon-qiyerenzheng"
    class="mt-2"
    @clickAuth="handleClick(TenantType.ENTERPRISE)">
  </AuthList>

  <!-- Government organization authentication option -->
  <AuthList
    :id="form.id"
    :pageTitle="t('realname.titles.governmentAuth')"
    :contents="contentsGovernment"
    :disbaled="!app.has('InstitutionsCertification')"
    icon="icon-shiyedanweirenzheng"
    class="mt-2"
    @clickAuth="handleClick(TenantType.GOVERNMENT)">
  </AuthList>
</template>
