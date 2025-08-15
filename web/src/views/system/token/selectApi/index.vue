<script lang="ts" setup>
import { computed, ref } from 'vue';
import SelectApis from './selectApi.vue';
import { Button } from 'ant-design-vue';
import { Icon, notification, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { GM } from '@xcan-angus/infra';

import { ServiceSelectionData, SelectApiProps } from '../types';
import { processServiceSelectionData, getSelectedServiceCodes, validateServiceSelection } from '../utils';

const { t } = useI18n();

// Component props interface
type Props = SelectApiProps

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

// Data source for selected services and their API resources
const dataSource = ref<ServiceSelectionData[]>([]);

// Service selection state
const serviceCode = ref<string>();
const serviceName = ref<string>();

/**
 * Handle service selection change
 */
const selectService = (value: any, opt: any) => {
  serviceCode.value = value;
  serviceName.value = opt.name;
};

// Event emitter for parent component communication
const emit = defineEmits<{(e: 'change', value: any): void }>();

// Computed property for selected service codes
const selectedCode = computed(() => {
  return getSelectedServiceCodes(dataSource.value);
});

/**
 * Delete service data by index
 */
const delData = (idx: number) => {
  dataSource.value.splice(idx, 1);
  emit('change', source.value);
};

/**
 * Update service data source
 */
const setData = (data: any, idx: number) => {
  dataSource.value[idx].source = data;
  emit('change', source.value);
};

// Computed property for processed source data
const source = computed(() => {
  return processServiceSelectionData(dataSource.value);
});

/**
 * Clear all form data and reset to initial state
 */
const clearData = () => {
  serviceCode.value = undefined;
  serviceName.value = undefined;
  dataSource.value = [];
  emit('change', source.value);
};

/**
 * Add new service to data source
 */
const add = () => {
  const validation = validateServiceSelection(serviceCode.value, dataSource.value, t);

  if (!validation.isValid) {
    notification.error(validation.errorMessage || 'Invalid service selection');
    return;
  }

  dataSource.value.push({
    serviceCode: serviceCode.value,
    serviceName: serviceName.value,
    source: {}
  });

  // Reset selection
  serviceCode.value = undefined;
  serviceName.value = undefined;
};

// Expose methods for parent component
defineExpose({
  clearData
});
</script>

<template>
  <div>
    <!-- Service selection and add button -->
    <div>
      <Select
        v-model:value="serviceCode"
        class="w-100"
        :action="`${GM}/service`"
        :fieldNames="{value: 'code', label: 'name'}"
        :disabled="props.disabled"
        :placeholder="t('systemToken.placeholder.selectService')"
        @change="selectService">
      </Select>
      <Button
        size="small"
        class="ml-2"
        :disabled="props.disabled"
        @click="add">
        <Icon icon="icon-tianjia" class="-mt-0.5" />
        {{ t('common.actions.add') }}
      </Button>
    </div>

    <!-- API selection components for each service -->
    <SelectApis
      v-for="(service, idx) in dataSource"
      :key="idx"
      v-model:serviceCode="service.serviceCode"
      :serviceName="service.serviceName"
      class="mt-2"
      :disabled="props.disabled"
      :disabledCode="selectedCode"
      @change="setData($event, idx)"
      @del="delData(idx)" />
  </div>
</template>
