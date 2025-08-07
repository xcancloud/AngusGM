<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';

/**
 * Component props interface
 * Defines the validation status props for password strength indicators
 */
interface Props {
  length: boolean, // Password length validation status
  chart: boolean // Password complexity validation status
}

withDefaults(defineProps<Props>(), {});

// Internationalization setup
const { t } = useI18n();
</script>

<template>
  <!-- Password strength validation tips container -->
  <div class="w-full text-3 leading-3 leading-4">
    <!-- Password length requirement indicator -->
    <div class="flex items-start">
      <div class="w-3.5 mr-2">
        <Icon
          v-if="length"
          icon="icon-right"
          class="text-success -mt-0.5" />
        <Icon
          v-else
          icon="icon-cuowu"
          class="text-danger -mt-0.5" />
      </div>
      <div class="text-theme-content">
        {{ t('user.validation.passwordLengthRange') }}
      </div>
    </div>
    
    <!-- Password complexity requirement indicator -->
    <div class="flex items-start mt-4">
      <div class="w-3.5 mr-2">
        <Icon
          v-if="chart"
          icon="icon-right"
          class="text-success -mt-0.5" />
        <Icon
          v-else
          icon="icon-cuowu"
          class="text-danger -mt-0.5" />
      </div>
      <div class="text-theme-content">
        {{ t('user.validation.passwordRequiredCombination') }}
      </div>
    </div>
    
    <!-- Special character types information -->
    <div class="flex items-start mt-4">
      <div class="w-3.5 mr-2">
        <Icon icon="icon-gantanhao-yuankuang" class="text-blue-tips -mt-0.5" />
      </div>
      <div class="text-theme-contentbreak-all">
        {{ t('user.validation.passwordSpecialCharType') }} /'"\:;~!@ #$%^&*()
      </div>
    </div>
    
    <!-- Space restriction information -->
    <div class="flex items-start mt-4">
      <div class="w-3.5 mr-2">
        <Icon icon="icon-gantanhao-yuankuang" class="text-blue-tips -mt-0.5" />
      </div>
      <div class="text-theme-content">
        {{ t('user.validation.passwordSpaceNotAllowed') }}
      </div>
    </div>
  </div>
</template>
