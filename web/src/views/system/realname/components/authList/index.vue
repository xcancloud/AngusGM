<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { Icon, PureCard } from '@xcan-angus/vue-ui';

interface Props {
  pageTitle: string, // Page title
  icon: string, // Icon identifier
  id: string, // Unique identifier
  contents: string[] // Content list for display
  disbaled: boolean // Whether the component is disabled
}

const props = withDefaults(defineProps<Props>(), {
  pageTitle: '',
  icon: '',
  id: ''
});

const { t } = useI18n();
const emit = defineEmits<{(e: 'clickAuth') }>();

/**
 * Handle authentication click event
 */
const path = function () {
  emit('clickAuth');
};

</script>

<template>
  <PureCard class="w-full py-6 flex px-10 items-center justify-between space-x-10">
    <div class="flex items-center space-x-10">
      <div class="w-20">
        <Icon class="text-20 leading-20 mr-10 text-theme-special" :icon="props.icon" />
      </div>
      <div>
        <!-- Page title -->
        <p class="text-4 leading-4 text-theme-title font-medium ">{{ props.pageTitle }}</p>
        <!-- Content list -->
        <ul class="text-3 leading-3 text-theme-sub-content font-normal mt-3">
          <li
            v-for="(item,index) in contents"
            :key="item"
            class="flex leading-5"
            :class="index!==0?'mt-1':''">
            <span>{{ index + 1 }}、</span>
            <span>{{ item }}</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Authentication button - enabled state -->
    <div
      v-if="!props.disbaled"
      class="flex items-center text-3 leading-3 text-theme-special text-theme-text-hover cursor-pointer"
      @click="path">
      <span>{{ t('common.actions.audit') }}</span>
      <Icon class="text-3 leading-3 ml-2" icon="icon-lijirenzheng" />
    </div>

    <!-- Authentication button - disabled state -->
    <div v-else class="flex items-center text-3 leading-3 text-theme-sub-content cursor-not-allowed">
      <span>{{ t('common.actions.audit') }}</span>
      <Icon class="text-3 leading-3 ml-2" icon="icon-lijirenzheng" />
    </div>
  </PureCard>
</template>
