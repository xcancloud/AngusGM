<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Arrow, Icon } from '@xcan-angus/vue-ui';
import { Checkbox, CheckboxGroup } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { service } from '@/api';

const { t } = useI18n();

// Interface definitions
interface Source {
  resourceName: string;
  resourceDesc: string;
  apis: {
    code: string;
    description: string;
    enabled: true;
    id: string;
    name: string;
  }[];
  open?: boolean;
}

interface Props {
  disabledCode: string[];
  enumOpt: { value: string; message: string }[];
  serviceCode?: string;
  serviceName?: string;
  disabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabledCode: () => [],
  enumOpt: () => [],
  disabled: false
});

// Event emitter for parent component communication
const emit = defineEmits<{
  (e: 'del'): void;
  (e: 'update:serviceCode', value: string): void;
  (e: 'change', checked: any): void;
}>();

// Internal service code state
const _serviceCode = ref<string>();

// Resource data state
const resource = ref<Source[]>([]);

// Checked source state for ACL selections
const checkedSource = reactive<{ [key: string]: string[] | undefined }>({});

/**
 * Handle checkbox change for specific resource
 */
const handleChecked = (checked: string[], key: string) => {
  checkedSource[key] = checked;
  emit('change', checkedSource);
};

/**
 * Check if resource has indeterminate state (partially selected)
 */
const getIndeterminate = (item: Source): boolean => {
  return checkedSource[item.resourceName]?.length !== props.enumOpt.length &&
         !!checkedSource[item.resourceName]?.length;
};

/**
 * Handle select all/deselect all for a resource
 */
const checkAll = (e: any, item: Source) => {
  if (e.target.checked) {
    checkedSource[item.resourceName] = props.enumOpt.map(type => type.value);
  } else {
    delete checkedSource[item.resourceName];
  }
  emit('change', checkedSource);
};

/**
 * Load service resources from API
 */
const loadServiceResources = async (serviceId: string) => {
  const [error, res] = await service.getServiceResources({ serviceCode: serviceId });
  if (error) {
    return;
  }
  resource.value = res.data?.[0]?.resources || [];
};

/**
 * Handle service selection change
 */
const selectService = (value: string) => {
  // Clear previous selections
  Object.keys(checkedSource).forEach(key => {
    delete checkedSource[key];
  });
  emit('change', checkedSource);

  _serviceCode.value = value;
  emit('update:serviceCode', value);
  loadServiceResources(value);
};

// Watch for service code changes
watch(() => props.serviceCode, newValue => {
  if (newValue) {
    selectService(newValue);
  } else {
    _serviceCode.value = undefined;
    resource.value = [];
  }
}, {
  immediate: true
});

/**
 * Delete this source component
 */
const delSource = () => {
  emit('del');
};
</script>

<template>
  <div class="border py-2">
    <!-- Service header with delete button -->
    <div class="flex justify-between items-center px-2">
      <span>{{ props.serviceName }}</span>
      <Icon
        icon="icon-shanchuguanbi"
        class="cursor-pointer"
        @click="delSource">
      </Icon>
    </div>

    <!-- Resource list with ACL selection -->
    <div class="mt-2 border-t h-70 overflow-y-auto">
      <div
        v-for="item in resource"
        :key="item.resourceName"
        class="border-b">
        <!-- Resource header with select all checkbox -->
        <div class="p-2 flex items-center">
          <Arrow v-model:open="item.open" />
          <span class="ml-2">{{ item.resourceName }}</span>
          <Checkbox
            :checked="checkedSource[item.resourceName]?.length === props.enumOpt.length"
            :indeterminate="getIndeterminate(item)"
            class="ml-2"
            size="small"
            @change="checkAll($event, item)">
            {{ t('common.form.selectAll') }}
          </Checkbox>
          <span>{{ t('systemToken.messages.totalItem', {value: props.enumOpt.length}) }}</span>
          <span class="ml-2 text-theme-special">{{
            t('systemToken.messages.selectItem', {value: checkedSource[item.resourceName]?.length || 0})
          }}</span>
        </div>

        <!-- Expandable ACL options -->
        <div v-show="item.open" class="p-2 pl-8 flex items-center">
          <Checkbox
            :checked="checkedSource[item.resourceName]?.length === props.enumOpt.length"
            :indeterminate="getIndeterminate(item)"
            class="mr-2"
            size="small"
            @change="checkAll($event, item)">
            {{ t('common.labels.all') }}
          </Checkbox>
          <CheckboxGroup
            v-model:value="checkedSource[item.resourceName]"
            class="flex-1"
            @change="handleChecked($event, item.resourceName)">
            <Checkbox
              v-for="opt in enumOpt"
              :key="opt.value"
              :value="opt.value">
              {{ opt.message }}
            </Checkbox>
          </CheckboxGroup>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(label.ant-checkbox-wrapper > span:last-of-type) {
  max-width: 95%;
}
</style>
