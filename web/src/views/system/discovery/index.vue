<script setup lang="ts">
import { onMounted, ref, reactive, watch, onBeforeUnmount, provide, defineAsyncComponent } from 'vue';


import {actuator} from '@/api';

const Information = defineAsyncComponent(() => import('@/views/system/discovery/components/information/index.vue'));
const Summary = defineAsyncComponent(() => import('@/views/system/discovery/components/summary/index.vue'));
const Metrics = defineAsyncComponent(() => import('@/views/system/discovery/components/metrics/index.vue'));
const Service = defineAsyncComponent(() => import('@/views/system/discovery/components/service/index.vue'));

const registryStatus = ref('');
const registeredRuntime = ref('');
const minNum = ref(0);
const lastNum = ref(0);

const loading = ref(true);

provide('loading', loading);

// 基本信息
const values: string[] = reactive([]);

const serviceData: any = reactive([]);

const loadRegisterAll = async () => {
  const [, res] = await actuator.getDiscoveryStatus();
  loading.value = false;
  if (!res.data) {
    return;
  }

  const data = res.data;
  getHeaderData(data);
  getBasicInformationValues(data);
  getTableData(data);
};

const getTableData = (data: any) => {
  const { apps } = data;
  if (apps.length > 0) {
    serviceData.length = 0;
    for (let i = 0; i < apps.length; i++) {
      apps[i].key = i;
      const { instances, status } = apps[i].instanceInfos[0];
      apps[i].instances = instances;
      apps[i].status = status;

      serviceData.push(apps[i]);
    }
  }
};

const getHeaderData = (data: any) => {
  registryStatus.value = data.instanceInfo.status;
  registeredRuntime.value = data.upTime;

  const registry = data.registry;
  minNum.value = registry.numOfRenewsPerMinThreshold;
  lastNum.value = registry.numOfRenewsInLastMin;
};

const getBasicInformationValues = (data: any) => {
  // 环境名称
  values[0] = setPlaceholderString(data.environment);
  values[1] = setPlaceholderString(data.datacenter);
  values[2] = setPlaceholderString(data.instanceInfo.ipAddr);

  const registry = data.registry;
  values[3] = registry.leaseExpirationEnabled === 'true' ? '是' : '否';
};

const setPlaceholderString = (val: any) => {
  if (!val) {
    val = '　';
  }
  return val;
};

const cpuData = reactive({
  percentage: 0,
  totalNuclear: 0,
  used: ''
});

provide('cpuData', cpuData);

const cpuTotal = ref(0);
const cpuUsage = ref(0);

const queryCpuTotalNuclear = async () => {
  const [, res] = await actuator.getCpuMax();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

const queryCpuUsage = async () => {
  const [, res] = await actuator.getCpuUsage();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

watch([cpuTotal, cpuUsage], (newValues) => {
  cpuData.percentage = Number((newValues[1] * 100).toFixed(2));
  cpuData.totalNuclear = newValues[0];
  cpuData.used = (newValues[1] * newValues[0]).toFixed(2);
});

const ramCapacity = ref(0);
const ramUsage = ref(0);

const ramData = reactive({
  percentage: 0,
  totalNuclear: '',
  used: ''
});

provide('ramData', ramData);

const queryRamCapacity = async () => {
  const [, res] = await actuator.getJvmMax();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

const queryRamUsage = async () => {
  const [, res] = await actuator.getJvmUsage();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

watch([ramCapacity, ramUsage], (newValues) => {
  ramData.percentage = Number((newValues[1] / newValues[0] * 100).toFixed(2));
  ramData.totalNuclear = newValues[0].toFixed(2);
  ramData.used = newValues[1].toFixed(2);
});

const diskCapacity = ref(0);
const diskUsage = ref(0);

const diskData = reactive({
  percentage: 0,
  totalNuclear: '',
  used: ''
});

provide('diskData', diskData);

const queryDiskCapacity = async () => {
  const [, res] = await actuator.getMaxDisk();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

const queryDisUsage = async () => {
  const [, res] = await actuator.getDisUsage();
  if (!res.data) {
    return;
  }

  unitConversion(res.data);
};

watch([diskCapacity, diskUsage], (newValues) => {
  diskData.percentage = Number((newValues[1]).toFixed(2));
  diskData.totalNuclear = newValues[0].toFixed(2);
  diskData.used = (newValues[1] / 100 * newValues[0]).toFixed(2);
});

const bytesToGB = (val: string | number) => {
  return +val / 1024 / 1024 / 1024; // 转成GB
};

const unitConversion = (data) => {
  const name: string = data.name;
  const val: number = data.measurements[0].value;
  switch (name) {
    case 'system.cpu.count':
      cpuTotal.value = val; // cpu直接返回GB
      break;
    case 'system.cpu.usage':
      cpuUsage.value = Number(val.toFixed(2)); // CPU返回使用率
      break;
    case 'jvm.memory.max':
      ramCapacity.value = bytesToGB(val); // 内存返回字节
      break;
    case 'jvm.memory.used':
      ramUsage.value = bytesToGB(val); // 内存返回字节
      break;
    case 'diskspace.total':
      diskCapacity.value = bytesToGB(val); // 磁盘返回字节
      break;
    case 'diskspace.usage':
      diskUsage.value = Number(val.toFixed(2)); // 磁盘返回使用率
      break;
    default:
  }
};

let timer: NodeJS.Timer | null = null;

const timedRequest = () => {
  if (timer) {
    clearTimeout(timer);
  }
  timer = setTimeout(async () => {
    await loadRegisterAll();
    queryCpuTotalNuclear();
    queryCpuUsage();
    queryRamCapacity();
    queryRamUsage();
    queryDiskCapacity();
    queryDisUsage();
    timedRequest();
  }, 3000);
};

onMounted(() => {
  timedRequest();
});

onBeforeUnmount(() => {
  if (timer) {
    clearTimeout(timer);
  }
});

</script>
<template>
  <Summary
    class="mb-2"
    :registryStatus="registryStatus"
    :registeredRuntime="registeredRuntime"
    :minNum="minNum"
    :lastNum="lastNum" />
  <div class="flex justify-between text-theme-title space-x-2 mb-2">
    <Information :values="values" />
    <Metrics />
  </div>
  <Service :serviceData="serviceData" />
</template>
<style scoped>
:deep(.ant-progress-text) {
  @apply text-6 leading-6 font-medium !important;
}
</style>
