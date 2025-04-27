import type { Config } from 'jest';

const config: Config = {
  verbose: true,
  // 使用 ts-jest 预设来编译 TypeScript 代码
  preset: 'ts-jest',
  // 使用 jest-environment-jsdom 环境来模拟浏览器环境
  // testEnvironment: 'jest-environment-jsdom',
  // 指定模块文件的扩展名
  moduleFileExtensions: ['vue', 'js', 'jsx', 'ts', 'tsx', 'json'],
  // 定义 Jest 如何转换各种文件类型
  transform: {
    // '^.+\\.(vue)$': 'vue-jest',
    '^.+\\.(js|jsx|ts|tsx)$': 'ts-jest'
  },
  // 定义模块名称的映射关系，这里将 @ 映射为 src 目录
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '^lib/(.*)$': '<rootDir>/lib/$1'
  },
  // 使用 Jest 官方提供的 Vue.js 组件快照序列化器来序列化测试代码中的 Vue.js 组件
  snapshotSerializers: ['jest-serializer-vue'],
  // 指定 Jest 查找测试用例的文件匹配模式。这里使用通配符 ** 来匹配任意目录
  testMatch: ['**/__tests__/**/*.(spec|test).(js|ts)'],
  testPathIgnorePatterns: [
    '/node_modules/',
    '/build/',
    '/dist/',
    '/src/',
    '/.husky/',
    '/assets/',
    '/configs/',
    '/coverage/',
    '/public/',
    '/scripts/',
    '/lib/',
    '/types/',
    '/.vscode/'
  ],
  // 指定在运行测试之前需要执行的脚本文件
  setupFiles: ['./__tests__/setup.ts']
  // 收集测试代码的覆盖率信息
  // collectCoverage: true,
  // 指定需要收集覆盖率信息的文件和目录
  // collectCoverageFrom: ['src/**/*.{js,jsx,ts,tsx,vue}', '!**/node_modules/**'],
  // 指定生成覆盖率报告的格式，这里同时生成 HTML 和文本格式的报告
  // coverageReporters: ['html', 'text']
};

export default config;
