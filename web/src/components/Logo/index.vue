<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { site } from '@xcan/utils';

const officialUrl = ref<string>();
onMounted(async () => {
  officialUrl.value = await site.getUrl('www');
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
