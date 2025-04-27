<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { Icon, Arrow } from '@xcan-angus/vue-ui';
import { service } from '@/api';
import { useI18n } from 'vue-i18n';
import { Checkbox, CheckboxGroup, Row, Col } from 'ant-design-vue';

const { t } = useI18n();

interface Source {
  resourceName: string,
  resourceDesc: string,
  apis: {
    code: string,
    description: string,
    enabled: true,
    id: string,
    name: string
  }[],
  open?: boolean
}

interface Props {
  disabledCode: string[],
  serviceCode?: string,
  serviceName?: string,
  disabled: boolean
}

const endReg = /.*(Door|pub)$/g; // 排除资源名称以 Door 或者 pub 为结尾的资源和接口
const props = withDefaults(defineProps<Props>(), {
  disabledCode: () => [],
  disabled: false
});

const emit = defineEmits<{
  (e: 'del'): void,
  (e: 'update:serviceCode', value: string): void,
  (e: 'change', checked): void
}>();

const _serviceCode = ref();

const resource = ref<Source[]>([]);

const checkedSource = reactive<{ [key: string]: string[] | undefined }>({});

const handleChecked = (checked, key) => {
  checkedSource[key] = checked;
  emit('change', checkedSource);
};

const isIndeteminate = (item: Source) => {
  return checkedSource[item.resourceName]?.length !== item.apis.length && !!checkedSource[item.resourceName]?.length;
};

const checkAll = (e, item: Source) => {
  if (e.target.checked) {
    checkedSource[item.resourceName] = item.apis.map(api => api.id);
  } else {
    delete checkedSource[item.resourceName];
  }
  emit('change', checkedSource);
};

const loadApis = async (serviceId) => {
  const [error, res] = await service.getApisByServiceOrResource({ serviceCode: serviceId, enabled: true });
  if (error) {
    return;
  }
  resource.value = (res.data || []).filter(resource => !resource.resourceName.match(endReg));
};

const selectService = (value) => {
  Object.keys(checkedSource).forEach(key => {
    delete checkedSource[key];
  });
  emit('change', checkedSource);
  _serviceCode.value = value;
  emit('update:serviceCode', value);
  loadApis(value);
};

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

const delSource = () => {
  emit('del');
};
</script>
<template>
  <div class="border py-2">
    <div class="flex justify-between items-center px-2">
      <span>{{ props.serviceName || '' }}</span>
      <Icon
        icon="icon-shanchuguanbi"
        class="cursor-pointer"
        @click="delSource">
      </Icon>
    </div>
    <div class="mt-2 border-t h-70 overflow-y-auto">
      <div
        v-for="item in resource"
        :key="item.resourceName"
        class="border-b">
        <div class="p-2 flex items-center">
          <Arrow v-model:open="item.open" />
          <span class="ml-2">{{ item.resourceName }}</span>
          <Checkbox
            :checked="checkedSource[item.resourceName]?.length === item.apis.length"
            :indeterminate="isIndeteminate(item)"
            class="ml-2"
            size="small"
            @change="checkAll($event, item)">
            {{ t('selectAll') }}
          </Checkbox>
          <span>{{ t('systemToken.total_item', {value: item.apis.length}) }}</span>
          <span class="ml-2 text-theme-special">{{ t('systemToken.select_item', {value: checkedSource[item.resourceName]?.length || 0}) }}</span>
        </div>
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
