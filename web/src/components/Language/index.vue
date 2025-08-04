<script setup lang="ts">
import { ref } from 'vue';
import { cookieUtils } from '@xcan-angus/infra';

const options = [
  { label: '简体中文', value: 'zh_CN' },
  { label: 'English', value: 'en' }
];

const language = (cookieUtils.get('localeCookie') || navigator.language);
const currentLanguage = options.find(item => item.value === language) || options[0];
const activeValue = ref(currentLanguage.value);
const activeLabel = ref(currentLanguage.label);
const hide = ref(true);
const timer = ref<NodeJS.Timeout>();
const timer2 = ref<NodeJS.Timeout>();

const mouseenter = (e) => {
  e.stopPropagation();
  clearTimeout(timer.value);
  clearTimeout(timer2.value);
  timer2.value = setTimeout(() => {
    hide.value = false;
  }, 200);
};

const mouseleave = (e) => {
  e.stopPropagation();
  clearTimeout(timer.value);
  clearTimeout(timer2.value);
  timer.value = setTimeout(() => {
    hide.value = true;
  }, 300);
};

const change = (data) => {
  activeLabel.value = data.label;
  activeValue.value = data.value;
  hide.value = true;
  cookieUtils.set('localeCookie', data.value);
  window.location.reload();
};
</script>

<template>
  <div
    :class="{'hide':hide}"
    class="x-select-container relative w-32 flex items-center cursor-pointer"
    @mouseenter="mouseenter"
    @mouseleave="mouseleave">
    <img class="w-4" src="./assets/earth.png" />
    <div class="ml-1.5">
      {{ activeLabel }}
    </div>
    <img
      class="arrow-icon w-4 ml-1.5"
      src="./assets/arrow.png"
      alt="">
    <div class="mask-container">
      <div
        v-for="item in options"
        :key="item.value"
        class="select-item"
        @click="change(item)">
        {{ item.label }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="css">
.arrow-icon {
  margin-top: -4px;
  transform: rotate(0deg);
  transition: all 150ms linear 100ms;
}

.hide .arrow-icon {
  margin-top: -1px;
  transform: rotate(180deg);
}

.mask-container {
  position: absolute;
  top: 30px;
  left: 0;
  align-items: center;
  height: 78px;
  overflow: hidden;
  transition: all 150ms linear 100ms;
  border-width: 0;
  border-style: solid;
  border-radius: 4px;
  border-color: #e5e5e5;
  background: #fff;
  box-shadow: 0 5px 10px 0 rgba(0, 0, 0, 5%);
  color: #444c5a;
  font-size: 14px;
}

.hide.x-select-container .mask-container {
  height: 0;
  border-width: 0;
}

.select-item {
  width: 100%;
  height: 30px;
  padding: 0 32px;
  line-height: 30px;
  cursor: pointer;
}

.select-item:first-child {
  margin-top: 8px;
}

.select-item:hover {
  background-color: #f7f8fb;
  color: #07f;
}
</style>
