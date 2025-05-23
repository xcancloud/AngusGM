<script lang="ts" setup>
import { ref, computed } from 'vue';
import SelectApis from './selectApi.vue';
import { Button } from 'ant-design-vue';
import { notification, Select, Icon } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { GM } from '@xcan-angus/tools';

const { t } = useI18n();

interface Props {
  disabled: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

const dataSource = ref<{ serviceCode?: string, serviceName?: string, source: Record<string, any> }[]>([]);

const serviceCode = ref();
const serviceName = ref();

const selectService = (value, opt) => {
  serviceCode.value = value;
  serviceName.value = opt.name;
};

const emit = defineEmits<{(e: 'change', value): void }>();

const selectedCode = computed(() => {
  return dataSource.value.map(i => i.serviceCode).filter(Boolean);
});

const delData = (idx) => {
  dataSource.value.splice(idx, 1);
  emit('change', source.value);
};

const setData = (data, idx) => {
  dataSource.value[idx].source = data;
  emit('change', source.value);
};

const source = computed(() => {
  const result: any[] = [];
  dataSource.value.forEach(data => {
    Object.keys(data.source).forEach(sourceName => {
      result.push({
        resource: sourceName,
        apiIds: data.source[sourceName]
      });
    });
  });
  return result;
});

const clearData = () => {
  serviceCode.value = undefined;
  serviceName.value = undefined;
  dataSource.value = [];
  emit('change', source.value);
};

const add = () => {
  if (!serviceCode.value) {
    notification.error(t('systemToken.service_placeholder'));
    return;
  }
  if (dataSource.value.find(service => service.serviceCode === serviceCode.value)) {
    notification.error(t('systemToken.service_tip'));
    return;
  }
  dataSource.value.push({ serviceCode: serviceCode.value, serviceName: serviceName.value, source: {} });
};

defineExpose({ clearData });
</script>
<template>
  <div>
    <div>
      <Select
        v-model:value="serviceCode"
        class="w-100"
        :action="`${GM}/service/search`"
        :fieldNames="{value: 'code', label: 'name'}"
        :disabled="props.disabled"
        :placeholder="t('selectToken')"
        @change="selectService">
      </Select>
      <Button
        size="small"
        class="ml-2"
        :disabled="props.disabled"
        @click="add">
        <Icon icon="icon-tianjia" class="-mt-0.5" />
        {{ t('new') }}
      </Button>
    </div>
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
