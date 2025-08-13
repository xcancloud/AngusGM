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
  min-width: 1280px;
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

::-webkit-scrollbar-track {
  border-radius: 10px;
  background-color: #f5f5f5;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0%);
}

::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #c8c8c8;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 10%);
}

.sign-container .ant-select-dropdown {
  border-color: #f0f0f0;
  background-color: #fff;
}

.sign-container img {
  pointer-events: none;
  user-select: none;
}

.sign-container .shadow-big {
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 5%);
}

.sign-container .error-message {
  @apply text-danger hidden px-5;

  font-size: 12px;
}

.sign-container .error .error-message {
  @apply block;
}

.sign-container .absolute-fixed .error-message {
  @apply absolute;

  top: 44px;
}

.sign-container .block-fixed .error-message {
  @apply px-5;
}

.sign-container .error.block-fixed {
  @apply mb-2;
}

.sign-container .ant-btn-primary:not(:disabled) {
  border-color: #1890ff;
  background: #1890ff;
}

.sign-container .tip-container {
  display: none;
  position: absolute;
  z-index: 50;
  top: 0;
  right: calc(100% + 12px);
  width: 12rem;
  padding: 20px 15px;
  border-radius: 0.25rem;
  background-color: rgba(247, 248, 251, 100%);
  box-shadow: 0 3px 6px -4px #0000001f, 0 6px 16px #00000014, 0 9px 28px 8px #0000000d;
  color: rgba(68, 76, 90, 100%);
  font-size: 12px;
}

.sign-container .focus .tip-container,
.sign-container .tip-container:hover {
  display: block;
}

.sign-container .ant-input-affix-wrapper {
  padding: 0;
  border: 0;
}

.sign-container .ant-input-affix-wrapper .ant-input {
  height: 42px;
  padding-right: 12px;
  padding-left: 40px;
  border: 1px solid rgba(200, 202, 208, 100%);
  border-radius: 21px;
  line-height: 42px;
}

.sign-container .pr7\.5.ant-input-affix-wrapper .ant-input {
  padding-right: 30px;
}

.sign-container .pl22.ant-input-affix-wrapper .ant-input {
  padding-left: 88px;
}

.sign-container .ant-input-affix-wrapper .ant-input-prefix {
  position: absolute;
  z-index: 1;
  top: 50%;
  left: 12px;
  margin-right: 0;
  transform: translateY(-50%);
}

.sign-container .ant-input-affix-wrapper .ant-input-suffix {
  position: absolute;
  z-index: 1;
  top: 50%;
  right: 12px;
  margin-right: 0;
  transform: translateY(-50%);
}

.sign-container .ant-input-affix-wrapper .ant-input:focus,
.sign-container .ant-input-affix-wrapper .ant-input:hover {
  border-color: #1890ff;
  background-color: rgba(24, 144, 255, 4%);
  box-shadow: none;
}
</style>
