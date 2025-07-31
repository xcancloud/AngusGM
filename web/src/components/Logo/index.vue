<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { DomainManager } from '@xcan-angus/infra';
import {AppOrServiceRoute} from "@xcan-angus/infra/lib/router/apiRouterPrefix";

const officialUrl = ref<string>();
onMounted(async () => {
  officialUrl.value = await DomainManager.getInstance().getApiDomain(AppOrServiceRoute.www);
});
</script>

<template>
  <a :href="officialUrl" target="_self">
    <img
      class="logo"
      src="./assets/logo.svg"
      alt="" />
  </a>
</template>
<style scoped>
.logo {
  width: 113px;
}
</style>
