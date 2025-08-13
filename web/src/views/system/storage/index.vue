<script setup lang="ts">
import { defineAsyncComponent } from 'vue';

const AppStorage = defineAsyncComponent(() => import('./app.vue'));
const BusinessStorage = defineAsyncComponent(() => import('./business.vue'));
</script>
<template>
  <AppStorage class="mb-2" />
  <BusinessStorage />
</template>
