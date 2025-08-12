<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Arrow, Icon } from '@xcan-angus/vue-ui';
import { service } from '@/api';
import { useI18n } from 'vue-i18n';
import { Checkbox, CheckboxGroup, Col, Row } from 'ant-design-vue';

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
  serviceCode?: string;
  serviceName?: string;
  disabled: boolean;
}

// Regular expression to exclude resources ending with Door or pub
const endReg = /.*(Door|pub)$/g;

const props = withDefaults(defineProps<Props>(), {
  disabledCode: () => [],
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

// Checked source state for API selections
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
const isIndeteminate = (item: Source): boolean => {
  return checkedSource[item.resourceName]?.length !== item.apis.length &&
         !!checkedSource[item.resourceName]?.length;
};

/**
 * Handle select all/deselect all for a resource
 */
const checkAll = (e: any, item: Source) => {
  if (e.target.checked) {
    checkedSource[item.resourceName] = item.apis.map(api => api.id);
  } else {
    delete checkedSource[item.resourceName];
  }
  emit('change', checkedSource);
};

/**
 * Load APIs for selected service
 */
const loadApis = async (serviceId: string) => {
  const [error, res] = await service.getApisByServiceOrResource({
    serviceCode: serviceId,
    enabled: true
  });
  if (error) {
    return;
  }
  // Filter out resources ending with Door or pub
  resource.value = (res.data || []).filter(resource =>
    !resource.resourceName.match(endReg)
  );
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
  loadApis(value);
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
      <span>{{ props.serviceName || '' }}</span>
      <Icon
        icon="icon-shanchuguanbi"
        class="cursor-pointer"
        @click="delSource">
      </Icon>
    </div>

    <!-- Resource list with API selection -->
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
            :checked="checkedSource[item.resourceName]?.length === item.apis.length"
            :indeterminate="isIndeteminate(item)"
            class="ml-2"
            size="small"
            @change="checkAll($event, item)">
            {{ t('common.form.selectAll') }}
          </Checkbox>
          <span>{{ t('systemToken.messages.totalItem', {value: item.apis.length}) }}</span>
          <span class="ml-2 text-theme-special">{{
            t('systemToken.messages.selectItem', {value: checkedSource[item.resourceName]?.length || 0})
          }}</span>
        </div>

        <!-- Expandable API options in grid layout -->
        <div v-show="item.open" class="p-2 pl-8">
          <CheckboxGroup
            v-model:value="checkedSource[item.resourceName]"
            class="w-full"
            @change="handleChecked($event, item.resourceName)">
            <Row>
              <Col
                v-for="api in item.apis"
                :key="api.id"
                :span="6"
                class="mb-2">
                <Checkbox :value="api.id" class="flex items-center">
                  <div>{{ api.code }}</div>
                  <div class="truncate" :title="api.name">
                    {{ api.name }}
                  </div>
                </Checkbox>
              </Col>
            </Row>
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
