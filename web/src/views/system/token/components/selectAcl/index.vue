<script lang="ts" setup>
import { enumLoader, GM } from '@xcan-angus/infra';
import { computed, onMounted, ref } from 'vue';
import { Button } from 'ant-design-vue';
import { Icon, notification, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import SelectAcls from './selectAcl.vue';

const { t } = useI18n();

interface Props {
  disabled: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});
const serviceCode = ref();
const serviceName = ref();

const selectService = (value, opt) => {
  serviceCode.value = value;
  serviceName.value = opt.name;
};

const dataSource = ref<{ serviceCode?: string, serviceName?: string, source: Record<string, any> }[]>([]);

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
        acls: data.source[sourceName].length === ResourceAclTypeOpt.value.length
          ? ['ALL']
          : data.source[sourceName]
      });
    });
  });
  return result;
});

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

const ResourceAclTypeOpt = ref([]);
const getResourceAclType = async () => {
  const [error, data] = await enumLoader.load('ResourceAclType');
  if (error) {
    return;
  }
  ResourceAclTypeOpt.value = data.filter(type => type.value !== 'ALL');
};

const clearData = () => {
  serviceCode.value = undefined;
  serviceName.value = undefined;
  dataSource.value = [];
  emit('change', source.value);
};

onMounted(() => {
  getResourceAclType();
});

defineExpose({ clearData });
</script>
<template>
  <div>
    <div>
      <Select
        v-model:value="serviceCode"
        class="w-100"
        :action="`${GM}/service`"
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
