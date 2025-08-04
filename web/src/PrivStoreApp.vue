<script setup lang="ts">
import { ConfigProvider } from 'ant-design-vue';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import 'dayjs/locale/zh-cn';
</script>

<template>
  <ConfigProvider :locale="zhCN">
    <RouterView class="sign-container" />
  </ConfigProvider>
</template>

<style>
body,
html {
  height: 100%;
  overflow: auto;
  font-size: 16px;
}

#app {
  position: relative;
  z-index: 1;
  min-width: 1200px;
  height: 100%;
  font-family: Inter,
  "Apple System",
  "SF Pro SC",
  "SF Pro Display",
  "Helvetica Neue",
  Arial,
  "PingFang SC",
  "Hiragino Sans GB",
  STHeiti,
  "Microsoft YaHei",
  "Microsoft JhengHei",
  "Source Han Sans SC",
  "Noto Sans CJK SC",
  "Source Han Sans CN",
  sans-serif;
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

::-webkit-scrollbar {
  width: 7px;
  height: 7px;
  background-color: #f5f5f5;
}

/* 定义滚动条轨道 内阴影+圆角 */
::-webkit-scrollbar-track {
  border-radius: 10px;
  background-color: #f5f5f5;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0%);
}

/* 定义滑块 内阴影+圆角 */
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #c8c8c8;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 10%);
}
</style>
