<script setup lang='ts'>
import { Icon, PureCard } from '@xcan-angus/vue-ui';

import { useI18n } from 'vue-i18n';
const { t } = useI18n();

interface Props {
  pageTitle: string, // Page title
  icon: string, // Icon identifier
  success: boolean, // Whether authentication is successful
  contents: string[] // Content list for display
  theme?: 'enterprise' | 'government' | 'personal' // Theme color variant
}

const props = withDefaults(defineProps<Props>(), {
  icon: '',
  theme: 'enterprise'
});

// Theme color configurations
const themeConfig = {
  enterprise: {
    bg: 'from-white to-blue-50',
    icon: 'text-blue-500',
    number: 'bg-blue-500',
    hover: 'hover:bg-blue-100',
    text: 'group-hover:text-blue-800',
    border: 'border-blue-200'
  },
  government: {
    bg: 'from-white to-green-50',
    icon: 'text-green-500',
    number: 'bg-green-500',
    hover: 'hover:bg-green-100',
    text: 'group-hover:text-green-800',
    border: 'border-green-200'
  },
  personal: {
    bg: 'from-white to-purple-50',
    icon: 'text-purple-500',
    number: 'bg-purple-500',
    hover: 'hover:bg-purple-100',
    text: 'group-hover:text-purple-800',
    border: 'border-purple-200'
  }
};

const currentTheme = themeConfig[props.theme];
</script>

<template>
  <PureCard :class="`flex flex-col items-center w-81 pt-10 px-6 pb-6 shadow-lg hover:shadow-xl transition-all duration-300 bg-gradient-to-br ${currentTheme.bg} border border-gray-100`">
    <div class="relative">
      <Icon :class="`icon-large ${currentTheme.icon}`" :icon="icon" />
      <div :class="`absolute inset-0 w-full h-full ${currentTheme.icon.replace('text-', 'bg-').replace('-500', '-100')} rounded-full opacity-30 animate-pulse`"></div>
    </div>

    <!-- Page title -->
    <p class="w-full mb-10 mt-6 text-5 leading-5 text-center font-semibold text-gray-800">{{ pageTitle }}</p>

    <!-- Content list -->
    <ul class="w-full text-3 leading-5 space-y-4 mb-15">
      <li
        v-for="(item, idx) in contents"
        :key="item"
        :class="`flex items-start justify-start group ${currentTheme.hover} transition-all duration-200 rounded-lg px-3 py-2`">
        <span :class="`flex-shrink-0 w-6 h-6 ${currentTheme.number} text-white text-xs rounded-full flex items-center justify-center font-medium mr-3 mt-0.5`">
          {{ idx + 1 }}
        </span>
        <span :class="`text-gray-700 leading-relaxed ${currentTheme.text} transition-colors duration-200 mt-0.5`">{{ item }}</span>
      </li>
    </ul>

    <!-- Authentication success indicator -->
    <div v-if="success" class="w-full flex justify-center items-center space-x-2 bg-green-50 border border-green-200 rounded-lg px-4 py-3">
      <Icon
        class="text-20 text-green-500"
        icon="icon-renzhengchenggong" />
      <span class="text-green-700 text-sm font-medium">{{ t('common.status.authPassed') }}</span>
    </div>
  </PureCard>
</template>

<style scoped>
.icon-large {
  font-size: 4rem !important;
  width: 4rem !important;
  height: 4rem !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

.relative {
  min-height: 5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
