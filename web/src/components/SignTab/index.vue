<script setup lang="ts">
import { computed, ref, watch } from 'vue';

interface Props {
  tabList: { value: string, label: string }[];
  activeKey: string;

}

const props = withDefaults(defineProps<Props>(), {
  tabList: () => ([]),
  activeKey: ''
});

const emit = defineEmits<{(e: 'update:activeKey', value: string): void }>();
const activeValue = ref();

const activeIndex = ref(-1);

const style = computed(() => {
  return { 'margin-left': '-' + (activeIndex.value * 100) + '%' };
});

watch(() => props.activeKey, newValue => {
  activeValue.value = newValue;
  activeIndex.value = props.tabList.findIndex(item => item.value === newValue);
}, {
  immediate: true
});

const change = (value, index) => {
  activeIndex.value = index;
  activeValue.value = value;
  emit('update:activeKey', value);
};
</script>
<template>
  <div class="w-full flex-col overflow-hidden">
    <div
      class="flex items-center justify-start mb-8 leading-5 text-5 text-gray-content cursor-pointer font-medium select-none">
      <div
        v-for="(item,index) in tabList"
        :key="item.value"
        :class="{'checked':activeValue===item.value}"
        class="tab-item"
        @click="change(item.value,index)">
        {{ item.label }}
      </div>
    </div>
    <div
      v-if="Object.keys($slots).length"
      :style="style"
      class="tab-item-main-container">
      <div
        v-for="item in tabList"
        :key="item.value"
        :class="{'tab-panel-item-active':activeValue===item.value}"
        class="tab-panel-item w-full flex-shrink-0 flex-grow-0">
        <slot :name="item.value"></slot>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tab-item {
  position: relative;
  margin-right: 40px;
}

.tab-item:last-child {
  margin-right: 0;
}

.tab-item.checked {
  @apply text-blue-tab-active;
}

.tab-item.checked::after {
  @apply absolute block h-0.75 bg-blue-tab-active rounded;

  content: "";
  bottom: -15px;
  left: 50%;
  width: 80%;
  transform: translateX(-50%);
}

.tab-item-main-container {
  @apply flex flex-row w-full whitespace-nowrap;

  transition: margin-left 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  will-change: margin-left;
}

.tab-item-main-container .tab-panel-item {
  height: 0;
  padding: 0 !important;
  overflow: hidden;
  opacity: 0;
}

.tab-item-main-container .tab-panel-item.tab-panel-item-active {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  transition: opacity 0.45s;
  opacity: 1;
}
</style>
