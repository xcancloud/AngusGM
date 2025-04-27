<script setup lang="ts">
const year = new Date().getFullYear();
</script>
<template>
  <div class="flex flex-wrap justify-center items-end whitespace-pre-line text-3 leading-4.5 text-gray-tip">
    <span>©{{ year }} xcan.cloud 版权所有</span><span class="ml-1.5">京ICP备2021037962号-1</span><span
      class="ml-1.5"><a
      href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=11011402012129"
      target="_blank"
      rel="noopener noreferrer">京公网安备 11011402012129号</a></span>
  </div>
</template>
