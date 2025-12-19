# LDAP集成模块 - 后端API接口文档

## 基础信息
- **模块名称**: LDAP集成
- **模块路径**: `/api/v1/ldap`
- **负责功能**: LDAP服务器配置、用户同步、认证集成

---

## 接口列表

### 1. 获取LDAP配置
**接口地址**: `GET /api/v1/ldap/config`

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "tenantId": "1",
    "createdBy": "1000001",
    "creator": "系统管理员",
    "createdDate": "2024-01-15 10:00:00",
    "modifiedBy": "1000001",
    "modifier": "系统管理员",
    "modifiedDate": "2025-12-03 14:30:00",
    "id": "LDAP_CONFIG_001",
    "isEnabled": true,
    "server": "ldap://ldap.example.com:389",
    "baseDN": "dc=example,dc=com",
    "bindDN": "cn=admin,dc=example,dc=com",
    "bindPassword": "******",
    "userSearchBase": "ou=users,dc=example,dc=com",
    "userSearchFilter": "(uid={0})",
    "groupSearchBase": "ou=groups,dc=example,dc=com",
    "groupSearchFilter": "(member={0})",
    "useSsl": true,
    "syncEnabled": true,
    "syncInterval": 3600,
    "lastSyncTime": "2025-12-19 10:00:00"
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 2. 更新LDAP配置
**接口地址**: `PUT /api/v1/ldap/config`

**请求参数**:
```json
{
  "isEnabled": true,
  "server": "ldap://ldap.example.com:389",
  "baseDN": "dc=example,dc=com",
  "bindDN": "cn=admin,dc=example,dc=com",
  "bindPassword": "password123",
  "userSearchBase": "ou=users,dc=example,dc=com",
  "userSearchFilter": "(uid={0})",
  "groupSearchBase": "ou=groups,dc=example,dc=com",
  "groupSearchFilter": "(member={0})",
  "useSsl": true,
  "syncEnabled": true,
  "syncInterval": 3600
}
```

**响应数据**: 同LDAP配置

---

### 3. 测试LDAP连接
**接口地址**: `POST /api/v1/ldap/test-connection`

**请求参数**:
```json
{
  "server": "ldap://ldap.example.com:389",
  "baseDN": "dc=example,dc=com",
  "bindDN": "cn=admin,dc=example,dc=com",
  "bindPassword": "password123",
  "useSsl": true
}
```

**响应数据**:
```json
{
  "code": "S",
  "message": "连接测试成功",
  "data": {
    "connected": true,
    "testTime": "2025-12-19 11:00:00",
    "responseTime": 125,
    "serverInfo": {
      "version": "3",
      "vendor": "OpenLDAP"
    }
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 4. 测试LDAP认证
**接口地址**: `POST /api/v1/ldap/test-auth`

**请求参数**:
```json
{
  "username": "testuser",
  "password": "userpassword"
}
```

**响应数据**:
```json
{
  "code": "S",
  "message": "认证成功",
  "data": {
    "authenticated": true,
    "userDN": "uid=testuser,ou=users,dc=example,dc=com",
    "attributes": {
      "cn": "Test User",
      "mail": "testuser@example.com",
      "department": "IT Department"
    }
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 5. 手动同步LDAP用户
**接口地址**: `POST /api/v1/ldap/sync-users`

**响应数据**:
```json
{
  "code": "S",
  "message": "同步任务已启动",
  "data": {
    "tenantId": "1",
    "createdBy": "1000001",
    "creator": "系统管理员",
    "createdDate": "2025-12-19 11:00:00",
    "modifiedBy": "1000001",
    "modifier": "系统管理员",
    "modifiedDate": "2025-12-19 11:05:00",
    "id": "SYNC_001",
    "startTime": "2025-12-19 11:00:00",
    "endTime": "2025-12-19 11:05:00",
    "status": "成功",
    "totalUsers": 156,
    "newUsers": 5,
    "updatedUsers": 12,
    "deletedUsers": 2,
    "failedUsers": 0
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 6. 获取同步历史记录（分页）
**接口地址**: `GET /api/v1/ldap/sync-history`

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `status`: 状态筛选（成功、失败、进行中）

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "total": "45",
    "list": [
      {
        "tenantId": "1",
        "createdBy": "SYSTEM",
        "creator": "系统自动",
        "createdDate": "2025-12-19 10:00:00",
        "modifiedBy": "SYSTEM",
        "modifier": "系统自动",
        "modifiedDate": "2025-12-19 10:05:00",
        "id": "SYNC_002",
        "startTime": "2025-12-19 10:00:00",
        "endTime": "2025-12-19 10:05:00",
        "duration": "5分钟",
        "status": "成功",
        "totalUsers": 156,
        "newUsers": 0,
        "updatedUsers": 3,
        "deletedUsers": 0,
        "failedUsers": 0,
        "syncType": "auto"
      }
    ]
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 7. 获取同步详情
**接口地址**: `GET /api/v1/ldap/sync-history/{id}`

**路径参数**:
- `id`: 同步记录ID

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "id": "SYNC_001",
    "startTime": "2025-12-19 11:00:00",
    "endTime": "2025-12-19 11:05:00",
    "duration": "5分钟",
    "status": "成功",
    "totalUsers": 156,
    "newUsers": 5,
    "updatedUsers": 12,
    "deletedUsers": 2,
    "failedUsers": 0,
    "syncType": "manual",
    "details": {
      "newUsersList": [
        {
          "username": "newuser1",
          "name": "新用户1",
          "email": "newuser1@example.com"
        }
      ],
      "updatedUsersList": [
        {
          "username": "user1",
          "name": "用户1",
          "changes": ["email", "department"]
        }
      ],
      "deletedUsersList": [
        {
          "username": "olduser1",
          "name": "旧用户1"
        }
      ],
      "failedUsersList": []
    }
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 8. 搜索LDAP用户
**接口地址**: `POST /api/v1/ldap/search-users`

**请求参数**:
```json
{
  "keyword": "zhang",
  "searchBase": "ou=users,dc=example,dc=com",
  "searchFilter": "(|(uid=*{keyword}*)(cn=*{keyword}*))",
  "limit": 50
}
```

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": [
    {
      "dn": "uid=zhangsan,ou=users,dc=example,dc=com",
      "uid": "zhangsan",
      "cn": "张三",
      "mail": "zhangsan@example.com",
      "department": "技术部",
      "title": "高级工程师",
      "mobile": "13800138000"
    },
    {
      "dn": "uid=zhangli,ou=users,dc=example,dc=com",
      "uid": "zhangli",
      "cn": "张力",
      "mail": "zhangli@example.com",
      "department": "产品部",
      "title": "产品经理",
      "mobile": "13900139000"
    }
  ],
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 9. 获取LDAP组列表
**接口地址**: `GET /api/v1/ldap/groups`

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": [
    {
      "dn": "cn=developers,ou=groups,dc=example,dc=com",
      "cn": "developers",
      "description": "开发团队",
      "memberCount": 25
    },
    {
      "dn": "cn=admins,ou=groups,dc=example,dc=com",
      "cn": "admins",
      "description": "管理员组",
      "memberCount": 5
    }
  ],
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 10. 获取LDAP组成员
**接口地址**: `GET /api/v1/ldap/groups/{groupDN}/members`

**路径参数**:
- `groupDN`: 组DN（URL编码）

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "groupDN": "cn=developers,ou=groups,dc=example,dc=com",
    "groupName": "developers",
    "members": [
      {
        "dn": "uid=zhangsan,ou=users,dc=example,dc=com",
        "uid": "zhangsan",
        "cn": "张三",
        "mail": "zhangsan@example.com"
      },
      {
        "dn": "uid=lisi,ou=users,dc=example,dc=com",
        "uid": "lisi",
        "cn": "李四",
        "mail": "lisi@example.com"
      }
    ]
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 11. 获取字段映射配置
**接口地址**: `GET /api/v1/ldap/field-mapping`

**响应数据**:
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "username": "uid",
    "name": "cn",
    "email": "mail",
    "phone": "mobile",
    "department": "department",
    "title": "title",
    "employeeNumber": "employeeNumber"
  },
  "timestamp": "1763958969885",
  "extensions": {}
}
```

---

### 12. 更新字段映射配置
**接口地址**: `PUT /api/v1/ldap/field-mapping`

**请求参数**:
```json
{
  "username": "uid",
  "name": "cn",
  "email": "mail",
  "phone": "telephoneNumber",
  "department": "ou",
  "title": "title",
  "employeeNumber": "employeeNumber"
}
```

**响应数据**: 同字段映射配置

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| LDAP_001 | LDAP服务器连接失败 |
| LDAP_002 | LDAP认证失败 |
| LDAP_003 | LDAP配置错误 |
| LDAP_004 | 用户不存在于LDAP |
| LDAP_005 | 同步任务进行中 |
| LDAP_006 | LDAP搜索失败 |
