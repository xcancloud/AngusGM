# Vue i18n 资源文件优化

## 📁 文件结构

```
web/src/locales/
├── en/                          # 英文资源文件
│   ├── common.json              # 通用消息
│   ├── auth.json                # 认证相关消息
│   ├── user.json                # 用户管理消息
│   ├── statistics.json          # 统计相关消息
├── zh_CN/                       # 中文资源文件
│   ├── common.json              # 通用消息
│   ├── auth.json                # 认证相关消息
│   ├── user.json                # 用户管理消息
│   ├── statistics.json          # 统计相关消息
└── README.md                    # 说明文档
```

## 🏗️ 分类结构

### 1. **common.json** - 通用消息
按功能模块分类：
- **actions**: 操作按钮和动作
- **status**: 状态相关
- **time**: 时间相关
- **form**: 表单字段和占位符
- **table**: 表格相关
- **messages**: 提示消息

### 2. **auth.json** - 认证相关
按认证流程分类：
- **login**: 登录相关
- **register**: 注册相关
- **resetPassword**: 密码重置
- **verification**: 验证码相关
- **password**: 密码规则和强度

### 3. **user.json** - 用户管理
按用户功能分类：
- **management**: 用户管理操作
- **profile**: 用户资料
- **security**: 安全设置
- **tokens**: 访问令牌
- **messages**: 用户消息

### 4. **statistics.json** - 统计相关
按统计功能分类：
- **chart**: 图表相关
- **filters**: 筛选条件
- **resources**: 资源类型
- **timeRanges**: 时间范围
- **metrics**: 指标类型
- **status**: 状态类型
- **types**: 类型分类
- **messages**: 统计消息

## 🔧 使用方式

### 在Vue组件中使用

```vue
<template>
  <div>
    <!-- 使用嵌套路径 -->
    <button>{{ $t('common.actions.add') }}</button>
    <span>{{ $t('auth.login.title') }}</span>
    <p>{{ $t('user.profile.basicInfo') }}</p>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 在JavaScript中使用
const message = t('common.messages.deleteSuccess')
const placeholder = t('auth.login.placeholder.account')
</script>
```

### 带参数的翻译

```vue
<template>
  <!-- 使用参数 -->
  <p>{{ $t('common.table.pageShowTotal', { total: 100, pageNo: 1, totalPage: 10 }) }}</p>
  <p>{{ $t('user.security.description.mobileSecurity', { mobile: '138****8888' }) }}</p>
</template>
```

## 📋 迁移指南

### 从旧格式迁移

**旧格式:**
```javascript
// 旧的方式
export default {
  add: '添加',
  delete: '删除',
  // ...
}
```

**新格式:**
```json
{
  "common": {
    "actions": {
      "add": "添加",
      "delete": "删除"
    }
  }
}
```

### 更新引用

**旧引用:**
```vue
{{ $t('add') }}
{{ $t('delete') }}
```

**新引用:**
```vue
{{ $t('common.actions.add') }}
{{ $t('common.actions.delete') }}
```

## 🎯 优势

### 1. **更好的组织结构**
- 按功能模块分类，便于维护
- 清晰的命名空间，避免冲突
- 支持深层嵌套，表达更丰富

### 2. **类型安全**
- JSON格式提供更好的IDE支持
- 可以配合TypeScript类型定义
- 减少拼写错误

### 3. **可扩展性**
- 模块化设计，易于添加新功能
- 支持按需加载
- 便于团队协作

### 4. **维护性**
- 结构清晰，易于查找
- 支持注释和文档
- 便于版本控制

## 🔄 配置更新

需要在Vue i18n配置中更新资源文件路径：

```javascript
// i18n配置示例
import { createI18n } from 'vue-i18n'
import enCommon from './locales/en/common.json'
import enAuth from './locales/en/auth.json'
import enUser from './locales/en/user.json'
import enStatistics from './locales/en/statistics.json'

import zhCommon from './locales/zh_CN/common.json'
import zhAuth from './locales/zh_CN/auth.json'
import zhUser from './locales/zh_CN/user.json'
import zhStatistics from './locales/zh_CN/statistics.json'

const messages = {
  en: {
    ...enCommon,
    ...enAuth,
    ...enUser,
    ...enStatistics
  },
  zh_CN: {
    ...zhCommon,
    ...zhAuth,
    ...zhUser,
    ...zhStatistics
  }
}

export default createI18n({
  locale: 'zh_CN',
  fallbackLocale: 'en',
  messages
})
```

## 📝 注意事项

1. **保持一致性**: 确保中英文版本的结构完全一致
2. **命名规范**: 使用小驼峰命名法，保持语义清晰
3. **参数处理**: 使用 `{param}` 格式处理动态参数
4. **版本控制**: 新增翻译时同步更新中英文版本
5. **测试验证**: 确保所有翻译路径都能正确访问 