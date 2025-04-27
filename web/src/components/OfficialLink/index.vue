<script setup lang="ts">
import { site } from '@xcan-angus/tools';
import { onMounted, ref } from 'vue';

const officialUrl = ref<string>();
onMounted(async () => {
  officialUrl.value = await site.getUrl('www');
});
</script>
<template>
  <a
    class="text-gray-content hover:text-blue-hover"
    :href="officialUrl"
    target="_self">{{ $t('official-website') }}</a>
</template>
