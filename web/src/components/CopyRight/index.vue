<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const year = new Date().getFullYear();
</script>
<template>
  <div class="flex flex-wrap justify-center items-end whitespace-pre-line text-3 leading-4.5 text-gray-tip">
    <span>©{{ year }} xcan.cloud {{ t('components.copyRight.allRightsReserved') }}</span><span class="ml-1.5">{{ t('components.copyRight.icpRecord') }}</span><span
      class="ml-1.5"><a
      href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=11011402012129"
      target="_blank"
      rel="noopener noreferrer">{{ t('components.copyRight.publicSecurityRecord') }}</a></span>
  </div>
</template>
