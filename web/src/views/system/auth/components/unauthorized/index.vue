<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { app } from '@xcan-angus/tools';

import AuthList from '@/views/system/auth/components/authList/index.vue';
import { tenant } from '@/api';

const { t } = useI18n();
const route = useRoute();

const emit = defineEmits<{(e: 'clickAuth', type: 'PERSONAL' | 'ENTERPRISE' | 'GOVERNMENT') }>();
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

const contentsEnterprise = [
  t('适用于企业用户，账号归属于企业；'),
  t('支持开增值税专用发票；'),
  t('一个证件只允许认证一个账号。')
];

const contentsGoverment = [
  t('适用于党政及国家机关、事业单位、民 办非企业单位、社会团体、个体工商户 等用户，账号归属于政府及事业单位；'),
  t('支持开增值税专用发票；'),
  t('一个证件只允许认证一个账号。')
];

const contentsPerson = [
  t('适用于个人用户，账号归属于个人；'),
  t('不支持开增值税专用发票；'),
  t('认证人员需年满18周岁；'),
  t('一个证件只允许认证一个账号。')
];

const handleClick = (type: 'PERSONAL' | 'ENTERPRISE' | 'GOVERNMENT') => {
  emit('clickAuth', type);
};

</script>
<template>
  <AuthList
    :id="form.id"
    :pageTitle="t('个人认证')"
    :contents="contentsPerson"
    :disbaled="!app.has('PersonalCertification')"
    icon="icon-gerenrenzheng"
    @clickAuth="handleClick('PERSONAL')">
  </AuthList>
  <AuthList
    :id="form.id"
    :pageTitle="t('企业认证')"
    :contents="contentsEnterprise"
    :disbaled="!app.has('EnterpriseCertification')"
    icon="icon-qiyerenzheng"
    class="mt-2"
    @clickAuth="handleClick('ENTERPRISE')">
  </AuthList>
  <AuthList
    :id="form.id"
    :pageTitle="t('政府及事业单位认证')"
    :contents="contentsGoverment"
    :disbaled="!app.has('InstitutionsCertification')"
    icon="icon-shiyedanweirenzheng"
    class="mt-2"
    @clickAuth="handleClick('GOVERNMENT')">
  </AuthList>
</template>
