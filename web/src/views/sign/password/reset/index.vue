<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext } from '@xcan-angus/infra';

import Logo from '@/components/Logo/index.vue';
import OfficialLink from '@/components/OfficialLink/index.vue';
import Tab from '@/components/SignTab/index.vue';
import Records from '@/components/CopyRight/index.vue';
import Form from './form.vue';

const { t } = useI18n();

const isPrivate = ref();

const activeValue = ref<'mobile' | 'email'>('email');
const tabList = computed(() => {
  return [
    {
      label: t('email-verification'), value: 'email'
    },
    !isPrivate.value && {
      label: t('sms-verification'), value: 'mobile'
    }
  ].filter(Boolean);
});

onMounted(async () => {
  isPrivate.value = await appContext.isPrivateEdition();
});
</script>

<template>
  <div class="outer-container-f w-full pt-7.5">
    <div class="flex items-center justify-between px-10">
      <Logo type="white" />
      <div class="flex justify-end items-center text-4 leading-5">
        <OfficialLink />
        <!-- <Language class="ml-8 mr-5" /> -->
      </div>
    </div>
    <div class="main-content-col flex items-center justify-center">
      <div class="w-115 p-10 rounded-3xl shadow-big bg-white">
        <Tab
          v-model:activeKey="activeValue"
          :tabList="tabList" />
        <Form :type="activeValue" />
      </div>
    </div>
    <Records class="justify-center pb-3" />
  </div>
</template>

<style scoped>
img {
  user-select: none;
}

.main-content-col {
  height: calc(100vh - 90px);
  min-height: 550px;
}

.outer-container-f {
  background-image: url("./assets/bg.png");
  background-repeat: no-repeat;
  background-size: cover;
}

.main-container {
  transform: translateY(-30px);
}
</style>
