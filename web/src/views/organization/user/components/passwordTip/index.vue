<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';

interface Props {
  length: boolean,
  chart: boolean
}

withDefaults(defineProps<Props>(), {});

const { t } = useI18n();
</script>

<template>
  <div class="w-full text-3 leading-3 leading-4">
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
    <div class="flex items-start mt-4">
      <div class="w-3.5 mr-2">
        <Icon icon="icon-gantanhao-yuankuang" class="text-blue-tips -mt-0.5" />
      </div>
      <div class="text-theme-contentbreak-all">
        {{ t('user.validation.passwordSpecialCharType') }} /‘”\:;~!@ #$%^&*()
      </div>
    </div>
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
