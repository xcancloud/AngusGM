<script setup lang="ts">
import { defineAsyncComponent } from 'vue';

const AppStorage = defineAsyncComponent(() => import('./components/app.vue'));
const BusinessStorage = defineAsyncComponent(() => import('./components/business.vue'));
</script>
<template>
  <AppStorage class="mb-2" />
  <BusinessStorage />
</template>
