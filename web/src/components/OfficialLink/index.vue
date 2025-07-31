<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {DomainManager} from "@xcan-angus/infra";
import {AppOrServiceRoute} from "@xcan-angus/infra/lib/router/apiRouterPrefix";

const officialUrl = ref<string>();
onMounted(async () => {
  officialUrl.value = await DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
});
</script>
<template>
  <a class="text-gray-content hover:text-blue-hover"
    :href="officialUrl"
    target="_self">{{ $t('official-website') }}</a>
</template>
