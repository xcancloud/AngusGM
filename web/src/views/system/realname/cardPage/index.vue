<script setup lang='ts'>
import { Icon, PureCard } from '@xcan-angus/vue-ui';

interface Props {
  pageTitle: string, // Page title
  icon: string, // Icon identifier
  success: boolean, // Whether authentication is successful
  contents: string[] // Content list for display
}

withDefaults(defineProps<Props>(), { icon: '' });

</script>

<template>
  <PureCard class="flex flex-col items-center w-81 pt-10 px-6 pb-6">
    <Icon class="text-25 text-blue-1" :icon="icon" />

    <!-- Page title -->
    <p class="w-full mb-10 mt-4 text-5 leading-5 text-center">{{ pageTitle }}</p>

    <!-- Content list -->
    <ul class="w-full text-3 leading-5 space-y-3 mb-15">
      <li
        v-for="(item, idx) in contents"
        :key="item"
        class="flex items-start justify-start">
        <span>{{ idx + 1 }}. </span>
        <span class="ml-1">{{ item }}</span>
      </li>
    </ul>

    <!-- Authentication success indicator -->
    <div v-if="success" class="w-full text-right">
      <Icon
        class="text-25 text-success opacity-80"
        icon="icon-renzhengchenggong" />
    </div>
  </PureCard>
</template>
