<script lang="ts" setup>
import { EnumMessage, enumUtils, ResourceAclType, GM } from '@xcan-angus/infra';
import { computed, onMounted, ref } from 'vue';
import { Button } from 'ant-design-vue';
import { Icon, notification, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import SelectAcls from './selectAcl.vue';

import { ServiceSelectionData, SelectAclProps } from '../types';
import { getSelectedServiceCodes, validateServiceSelection } from '../utils';

const { t } = useI18n();

// Component props interface
type Props = SelectAclProps

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

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

// Data source for selected services and their ACL resources
const dataSource = ref<ServiceSelectionData[]>([]);

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
  const result: any[] = [];
  dataSource.value.forEach(data => {
    Object.keys(data.source).forEach(sourceName => {
      result.push({
        resource: sourceName,
        acls: data.source[sourceName].length === ResourceAclTypeOpt.value.length
          ? ['ALL']
          : data.source[sourceName]
      });
    });
  });
  return result;
});

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

// Resource ACL type options
const ResourceAclTypeOpt = ref<EnumMessage<string>[]>([]);

/**
 * Fetch resource ACL type options from enum utils
 */
const getResourceAclType = async () => {
  ResourceAclTypeOpt.value = enumUtils.enumToMessages(ResourceAclType)
    .filter(type => type.value !== ResourceAclType.ALL);
};

/**
 * Clear all form data and reset to initial state
 */
const clearData = () => {
  serviceCode.value = undefined;
  serviceName.value = undefined;
  dataSource.value = [];
  emit('change', source.value);
};

// Lifecycle hook
onMounted(() => {
  getResourceAclType();
});

// Expose methods to parent component
defineExpose({ clearData });
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

    <!-- ACL selection components for each service -->
    <SelectAcls
      v-for="(service, idx) in dataSource"
      :key="idx"
      v-model:serviceCode="service.serviceCode"
      :serviceName="service.serviceName"
      class="mt-2"
      :disabled="props.disabled"
      :disabledCode="selectedCode"
      :enumOpt="ResourceAclTypeOpt"
      @change="setData($event, idx)"
      @del="delData(idx)" />
  </div>
</template>
