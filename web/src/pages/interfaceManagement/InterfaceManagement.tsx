import { useState } from 'react';
import { Network, Search, RefreshCw, Eye, ChevronLeft, ChevronRight as ChevronRightIcon, Clock, CheckCircle2, Server, Code, FileJson, Tag, List, Grid } from 'lucide-react';
import { Card } from '@/components/ui/card' ;
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';

interface ApiParameter {
  name: string;
  type: string;
  required: boolean;
  description: string;
  in: 'query' | 'path' | 'body' | 'header';
}

interface ApiInterface {
  id: string;
  code: string;
  path: string;
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  summary: string;
  description: string;
  tags: string[];
  parameters: ApiParameter[];
  responses: { [key: string]: string };
  deprecated: boolean;
}

interface ServiceInterfaces {
  serviceName: string;
  displayName: string;
  version: string;
  baseUrl: string;
  syncTime: string;
  interfaceCount: number;
  interfaces: ApiInterface[];
}

export function InterfaceManagement() {
  const [services] = useState<ServiceInterfaces[]>([
    {
      serviceName: 'ANGUS-GM-SERVICE',
      displayName: 'AngusGM全局管理服务',
      version: '1.0.0',
      baseUrl: 'http://192.168.1.101:8080',
      syncTime: '2024-12-03 14:30:25',
      interfaceCount: 25,
      interfaces: [
        {
          id: 'gm-001',
          code: 'GM001',
          path: '/api/v1/users',
          method: 'GET',
          summary: '获取用户列表',
          description: '分页查询系统用户列表，支持多条件筛选',
          tags: ['用户管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码，从1开始', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
            { name: 'keyword', type: 'string', required: false, description: '搜索关键词', in: 'query' },
            { name: 'status', type: 'string', required: false, description: '用户状态', in: 'query' },
          ],
          responses: {
            '200': '成功返回用户列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-002',
          code: 'GM002',
          path: '/api/v1/users',
          method: 'POST',
          summary: '创建用户',
          description: '创建新的系统用户',
          tags: ['用户管理'],
          parameters: [
            { name: 'username', type: 'string', required: true, description: '用户名', in: 'body' },
            { name: 'email', type: 'string', required: true, description: '邮箱地址', in: 'body' },
            { name: 'phone', type: 'string', required: false, description: '手机号', in: 'body' },
            { name: 'departmentId', type: 'string', required: true, description: '部门ID', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-003',
          code: 'GM003',
          path: '/api/v1/users/{id}',
          method: 'GET',
          summary: '获取用户详情',
          description: '根据用户ID获取用户详细信息',
          tags: ['用户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '用户ID', in: 'path' },
          ],
          responses: {
            '200': '成功返回用户详情',
            '404': '用户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-004',
          code: 'GM004',
          path: '/api/v1/users/{id}',
          method: 'PUT',
          summary: '更新用户信息',
          description: '根据用户ID更新用户信息',
          tags: ['用户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '用户ID', in: 'path' },
            { name: 'username', type: 'string', required: false, description: '用户名', in: 'body' },
            { name: 'email', type: 'string', required: false, description: '邮箱地址', in: 'body' },
          ],
          responses: {
            '200': '更新成功',
            '400': '参数错误',
            '404': '用户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-005',
          code: 'GM005',
          path: '/api/v1/users/{id}',
          method: 'DELETE',
          summary: '删除用户',
          description: '根据用户ID删除用户',
          tags: ['用户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '用户ID', in: 'path' },
          ],
          responses: {
            '200': '删除成功',
            '404': '用户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-006',
          code: 'GM006',
          path: '/api/v1/users/batch',
          method: 'POST',
          summary: '批量创建用户',
          description: '批量导入创建用户',
          tags: ['用户管理'],
          parameters: [
            { name: 'users', type: 'array', required: true, description: '用户列表', in: 'body' },
          ],
          responses: {
            '201': '批量创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-007',
          code: 'GM007',
          path: '/api/v1/tenants',
          method: 'GET',
          summary: '获取租户列表',
          description: '分页查询租户列表',
          tags: ['租户管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
          ],
          responses: {
            '200': '成功返回租户列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-008',
          code: 'GM008',
          path: '/api/v1/tenants',
          method: 'POST',
          summary: '创建租户',
          description: '创建新的租户',
          tags: ['租户管理'],
          parameters: [
            { name: 'name', type: 'string', required: true, description: '租户名称', in: 'body' },
            { name: 'code', type: 'string', required: true, description: '租户编码', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-009',
          code: 'GM009',
          path: '/api/v1/tenants/{id}',
          method: 'GET',
          summary: '获取租户详情',
          description: '根据租户ID获取租户详细信息',
          tags: ['租户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '租户ID', in: 'path' },
          ],
          responses: {
            '200': '成功返回租户详情',
            '404': '租户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-010',
          code: 'GM010',
          path: '/api/v1/tenants/{id}',
          method: 'PUT',
          summary: '更新租户信息',
          description: '根据租户ID更新租户信息',
          tags: ['租户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '租户ID', in: 'path' },
            { name: 'name', type: 'string', required: false, description: '租户名称', in: 'body' },
          ],
          responses: {
            '200': '更新成功',
            '404': '租户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-011',
          code: 'GM011',
          path: '/api/v1/tenants/{id}',
          method: 'DELETE',
          summary: '删除租户',
          description: '根据租户ID删除租户',
          tags: ['租户管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '租户ID', in: 'path' },
          ],
          responses: {
            '200': '删除成功',
            '404': '租户不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-012',
          code: 'GM012',
          path: '/api/v1/departments',
          method: 'GET',
          summary: '获取部门列表',
          description: '分页查询部门列表',
          tags: ['部门管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
          ],
          responses: {
            '200': '成功返回部门列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-013',
          code: 'GM013',
          path: '/api/v1/departments/tree',
          method: 'GET',
          summary: '获取部门树',
          description: '获取部门树形结构数据',
          tags: ['部门管理'],
          parameters: [],
          responses: {
            '200': '成功返回部门树',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-014',
          code: 'GM014',
          path: '/api/v1/departments',
          method: 'POST',
          summary: '创建部门',
          description: '创建新的部门',
          tags: ['部门管理'],
          parameters: [
            { name: 'name', type: 'string', required: true, description: '部门名称', in: 'body' },
            { name: 'parentId', type: 'string', required: false, description: '上级部门ID', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-015',
          code: 'GM015',
          path: '/api/v1/departments/{id}',
          method: 'PUT',
          summary: '更新部门信息',
          description: '根据部门ID更新部门信息',
          tags: ['部门管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '部门ID', in: 'path' },
            { name: 'name', type: 'string', required: false, description: '部门名称', in: 'body' },
          ],
          responses: {
            '200': '更新成功',
            '404': '部门不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-016',
          code: 'GM016',
          path: '/api/v1/departments/{id}',
          method: 'DELETE',
          summary: '删除部门',
          description: '根据部门ID删除部门',
          tags: ['部门管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '部门ID', in: 'path' },
          ],
          responses: {
            '200': '删除成功',
            '404': '部门不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-017',
          code: 'GM017',
          path: '/api/v1/groups',
          method: 'GET',
          summary: '获取组列表',
          description: '分页查询组列表',
          tags: ['组管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
          ],
          responses: {
            '200': '成功返回组列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-018',
          code: 'GM018',
          path: '/api/v1/groups',
          method: 'POST',
          summary: '创建组',
          description: '创建新的组',
          tags: ['组管理'],
          parameters: [
            { name: 'name', type: 'string', required: true, description: '组名称', in: 'body' },
            { name: 'description', type: 'string', required: false, description: '组描述', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-019',
          code: 'GM019',
          path: '/api/v1/groups/{id}',
          method: 'PUT',
          summary: '更新组信息',
          description: '根据组ID更新组信息',
          tags: ['组管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '组ID', in: 'path' },
            { name: 'name', type: 'string', required: false, description: '组名称', in: 'body' },
          ],
          responses: {
            '200': '更新成功',
            '404': '组不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-020',
          code: 'GM020',
          path: '/api/v1/groups/{id}',
          method: 'DELETE',
          summary: '删除组',
          description: '根据组ID删除组',
          tags: ['组管理'],
          parameters: [
            { name: 'id', type: 'string', required: true, description: '组ID', in: 'path' },
          ],
          responses: {
            '200': '删除成功',
            '404': '组不存在',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-021',
          code: 'GM021',
          path: '/api/v1/roles',
          method: 'GET',
          summary: '获取角色列表',
          description: '分页查询角色列表',
          tags: ['角色管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
          ],
          responses: {
            '200': '成功返回角色列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-022',
          code: 'GM022',
          path: '/api/v1/roles',
          method: 'POST',
          summary: '创建角色',
          description: '创建新的角色',
          tags: ['角色管理'],
          parameters: [
            { name: 'name', type: 'string', required: true, description: '角色名称', in: 'body' },
            { name: 'code', type: 'string', required: true, description: '角色编码', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-023',
          code: 'GM023',
          path: '/api/v1/permissions/policies',
          method: 'GET',
          summary: '获取权限策略列表',
          description: '分页查询权限策略列表',
          tags: ['权限管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
          ],
          responses: {
            '200': '成功返回权限策略列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-024',
          code: 'GM024',
          path: '/api/v1/permissions/policies',
          method: 'POST',
          summary: '创建权限策略',
          description: '创建新的权限策略',
          tags: ['权限管理'],
          parameters: [
            { name: 'name', type: 'string', required: true, description: '策略名称', in: 'body' },
            { name: 'resources', type: 'array', required: true, description: '资源列表', in: 'body' },
          ],
          responses: {
            '201': '创建成功',
            '400': '参数错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gm-025',
          code: 'GM025',
          path: '/api/v1/audit/logs',
          method: 'GET',
          summary: '获取审计日志',
          description: '分页查询系统审计日志',
          tags: ['审计管理'],
          parameters: [
            { name: 'page', type: 'integer', required: false, description: '页码', in: 'query' },
            { name: 'size', type: 'integer', required: false, description: '每页大小', in: 'query' },
            { name: 'startTime', type: 'string', required: false, description: '开始时间', in: 'query' },
            { name: 'endTime', type: 'string', required: false, description: '结束时间', in: 'query' },
          ],
          responses: {
            '200': '成功返回审计日志',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
      ],
    },
    {
      serviceName: 'ANGUS-AUTH-SERVICE',
      displayName: 'AngusAI认证服务',
      version: '2.1.0',
      baseUrl: 'http://192.168.1.201:9090',
      syncTime: '2024-12-03 14:28:15',
      interfaceCount: 28,
      interfaces: [
        {
          id: 'auth-001',
          code: 'AUTH001',
          path: '/oauth/token',
          method: 'POST',
          summary: '获取访问令牌',
          description: '通过用户名密码或刷新令牌获取访问令牌',
          tags: ['认证授权'],
          parameters: [
            { name: 'grant_type', type: 'string', required: true, description: '授权类型', in: 'body' },
            { name: 'username', type: 'string', required: false, description: '用户名', in: 'body' },
            { name: 'password', type: 'string', required: false, description: '密码', in: 'body' },
            { name: 'refresh_token', type: 'string', required: false, description: '刷新令牌', in: 'body' },
          ],
          responses: {
            '200': '成功获取令牌',
            '400': '参数错误',
            '401': '认证失败',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'auth-002',
          code: 'AUTH002',
          path: '/oauth/check_token',
          method: 'POST',
          summary: '验证令牌',
          description: '验证访问令牌的有效性',
          tags: ['认证授权'],
          parameters: [
            { name: 'token', type: 'string', required: true, description: '访问令牌', in: 'body' },
          ],
          responses: {
            '200': '令牌有效',
            '401': '令牌无效',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'auth-003',
          code: 'AUTH003',
          path: '/api/v1/permissions',
          method: 'GET',
          summary: '获取用户权限',
          description: '获取当前用户的权限列表',
          tags: ['权限管理'],
          parameters: [
            { name: 'Authorization', type: 'string', required: true, description: 'Bearer token', in: 'header' },
          ],
          responses: {
            '200': '成功返回权限列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
      ],
    },
    {
      serviceName: 'ANGUS-USER-SERVICE',
      displayName: '用户管理服务',
      version: '1.5.2',
      baseUrl: 'http://192.168.1.301:8081',
      syncTime: '2024-12-03 14:25:10',
      interfaceCount: 32,
      interfaces: [
        {
          id: 'user-001',
          code: 'USER001',
          path: '/api/v1/user/profile',
          method: 'GET',
          summary: '获取用户资料',
          description: '获取当前登录用户的详细资料',
          tags: ['用户资料'],
          parameters: [
            { name: 'Authorization', type: 'string', required: true, description: 'Bearer token', in: 'header' },
          ],
          responses: {
            '200': '成功返回用户资料',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'user-002',
          code: 'USER002',
          path: '/api/v1/user/avatar',
          method: 'POST',
          summary: '上传头像',
          description: '上传用户头像图片',
          tags: ['用户资料'],
          parameters: [
            { name: 'file', type: 'file', required: true, description: '头像文件', in: 'body' },
            { name: 'Authorization', type: 'string', required: true, description: 'Bearer token', in: 'header' },
          ],
          responses: {
            '200': '上传成功',
            '400': '文件格式错误',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'user-003',
          code: 'USER003',
          path: '/api/v1/user/password',
          method: 'PUT',
          summary: '修改密码',
          description: '修改当前用户密码',
          tags: ['用户资料'],
          parameters: [
            { name: 'oldPassword', type: 'string', required: true, description: '旧密码', in: 'body' },
            { name: 'newPassword', type: 'string', required: true, description: '新密码', in: 'body' },
            { name: 'Authorization', type: 'string', required: true, description: 'Bearer token', in: 'header' },
          ],
          responses: {
            '200': '修改成功',
            '400': '密码格式错误',
            '401': '未授权或旧密码错误',
            '500': '服务器错误',
          },
          deprecated: false,
        },
      ],
    },
    {
      serviceName: 'ANGUS-GATEWAY-SERVICE',
      displayName: 'API网关服务',
      version: '3.0.1',
      baseUrl: 'http://192.168.1.10:80',
      syncTime: '2024-12-03 14:32:00',
      interfaceCount: 15,
      interfaces: [
        {
          id: 'gateway-001',
          code: 'GATEWAY001',
          path: '/api/routes',
          method: 'GET',
          summary: '获取路由列表',
          description: '获取网关所有路由配置',
          tags: ['网关管理'],
          parameters: [],
          responses: {
            '200': '成功返回路由列表',
            '401': '未授权',
            '500': '服务器错误',
          },
          deprecated: false,
        },
        {
          id: 'gateway-002',
          code: 'GATEWAY002',
          path: '/actuator/health',
          method: 'GET',
          summary: '健康检查',
          description: '检查网关服务健康状态',
          tags: ['监控'],
          parameters: [],
          responses: {
            '200': '服务正常',
            '503': '服务不可用',
          },
          deprecated: false,
        },
      ],
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [methodFilter, setMethodFilter] = useState('all');
  const [tagFilter, setTagFilter] = useState('all');
  const [selectedService, setSelectedService] = useState<ServiceInterfaces | null>(services[0]);
  const [selectedInterface, setSelectedInterface] = useState<ApiInterface | null>(null);
  const [showDetailDialog, setShowDetailDialog] = useState(false);
  const [syncing, setSyncing] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [viewMode, setViewMode] = useState<'list' | 'group'>('list');
  const pageSize = 10;

  // 统计数据
  const totalServices = services.length;
  const totalInterfaces = services.reduce((sum, service) => sum + service.interfaceCount, 0);
  const syncedToday = services.filter((s) => s.syncTime.startsWith('2024-12-03')).length;

  // 获取所有标签
  const allTags = Array.from(
    new Set(
      services.flatMap((service) =>
        service.interfaces.flatMap((iface) => iface.tags)
      )
    )
  );

  // 获取HTTP方法标签
  const getMethodBadge = (method: string) => {
    const configs = {
      GET: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      POST: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      PUT: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      DELETE: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      PATCH: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
    };
    const className = configs[method as keyof typeof configs] || 'bg-gray-100 text-gray-700';
    return <Badge className={`${className} border-0 font-mono text-xs`}>{method}</Badge>;
  };

  // 同步接口
  const handleSync = async () => {
    setSyncing(true);
    setTimeout(() => {
      setSyncing(false);
      toast.success('接口同步成功');
    }, 2000);
  };

  // 查看接口详情
  const handleViewDetail = (iface: ApiInterface) => {
    setSelectedInterface(iface);
    setShowDetailDialog(true);
  };

  // 过滤接口
  const filteredInterfaces = selectedService
    ? selectedService.interfaces.filter((iface) => {
        const matchSearch =
          iface.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
          iface.path.toLowerCase().includes(searchQuery.toLowerCase()) ||
          iface.summary.toLowerCase().includes(searchQuery.toLowerCase()) ||
          iface.description.toLowerCase().includes(searchQuery.toLowerCase());

        const matchMethod = methodFilter === 'all' || iface.method === methodFilter;
        const matchTag = tagFilter === 'all' || iface.tags.includes(tagFilter);

        return matchSearch && matchMethod && matchTag;
      })
    : [];

  // 分页
  const totalPages = Math.ceil(filteredInterfaces.length / pageSize);
  const paginatedInterfaces = filteredInterfaces.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // 切换服务时重置分页
  const handleSelectService = (service: ServiceInterfaces) => {
    setSelectedService(service);
    setCurrentPage(1);
  };

  // 按标签分组接口
  const groupedInterfaces = (() => {
    const groups: { [key: string]: ApiInterface[] } = {};
    
    filteredInterfaces.forEach((iface) => {
      iface.tags.forEach((tag) => {
        if (!groups[tag]) {
          groups[tag] = [];
        }
        groups[tag].push(iface);
      });
    });
    
    return groups;
  })();

  // 渲染接口项
  const renderInterfaceItem = (iface: ApiInterface) => (
    <div
      key={iface.id}
      className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 group"
    >
      <div className="flex items-start gap-4">
        <div className="mt-1">{getMethodBadge(iface.method)}</div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0 text-xs font-mono">
              {iface.code}
            </Badge>
            <code className="text-sm dark:text-white font-mono">{iface.path}</code>
            {iface.deprecated && (
              <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0 text-xs">
                已弃用
              </Badge>
            )}
          </div>
          <p className="text-sm dark:text-gray-300 mb-1">{iface.summary}</p>
          <div className="flex items-center gap-2">
            {iface.tags.map((tag) => (
              <Badge
                key={tag}
                className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0 text-xs"
              >
                {tag}
              </Badge>
            ))}
          </div>
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={() => handleViewDetail(iface)}
          className="dark:hover:bg-gray-600 opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <Eye className="w-4 h-4 mr-1" />
          详情
        </Button>
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
              <Network className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl dark:text-white">接口管理</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">从Eureka服务同步的OpenAPI接口信息</p>
            </div>
          </div>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总服务数</p>
              <p className="text-2xl mt-2 dark:text-white">{totalServices}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <Server className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总接口数</p>
              <p className="text-2xl mt-2 dark:text-white">{totalInterfaces}</p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <Network className="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">今日已同步</p>
              <p className="text-2xl mt-2 dark:text-white">{syncedToday}</p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <CheckCircle2 className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">接口标签</p>
              <p className="text-2xl mt-2 dark:text-white">{allTags.length}</p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <Tag className="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
          </div>
        </Card>
      </div>

      {/* 筛选工具栏 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
        <div className="flex items-center gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索接口编码、路径、描述..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 dark:bg-gray-900 dark:border-gray-700"
            />
          </div>
          <div className="w-40">
            <Select value={methodFilter} onValueChange={setMethodFilter}>
              <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                <SelectValue placeholder="请求方法" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部方法</SelectItem>
                <SelectItem value="GET">GET</SelectItem>
                <SelectItem value="POST">POST</SelectItem>
                <SelectItem value="PUT">PUT</SelectItem>
                <SelectItem value="DELETE">DELETE</SelectItem>
                <SelectItem value="PATCH">PATCH</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="w-40">
            <Select value={tagFilter} onValueChange={setTagFilter}>
              <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                <SelectValue placeholder="接口标签" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部标签</SelectItem>
                {allTags.map((tag) => (
                  <SelectItem key={tag} value={tag}>
                    {tag}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <Button onClick={handleSync} disabled={syncing} className="bg-blue-600 hover:bg-blue-700">
            <RefreshCw className={`w-4 h-4 mr-2 ${syncing ? 'animate-spin' : ''}`} />
            {syncing ? '同步中...' : '同步接口'}
          </Button>
        </div>
      </Card>

      {/* 左右布局 */}
      <div className="grid grid-cols-12 gap-4">
        {/* 左侧服务列表 */}
        <div className="col-span-3">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-4 border-b dark:border-gray-700">
              <h3 className="dark:text-white">服务列表</h3>
            </div>
            <div className="divide-y dark:divide-gray-700">
              {services.map((service) => (
                <div
                  key={service.serviceName}
                  onClick={() => handleSelectService(service)}
                  className={`p-4 cursor-pointer transition-colors ${
                    selectedService?.serviceName === service.serviceName
                      ? 'bg-blue-50 dark:bg-blue-900/20 border-l-4 border-blue-600'
                      : 'hover:bg-gray-50 dark:hover:bg-gray-700/50'
                  }`}
                >
                  <div className="flex items-center gap-2 mb-2">
                    <Server className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                    <h4
                      className={`text-sm truncate ${
                        selectedService?.serviceName === service.serviceName
                          ? 'text-blue-600 dark:text-blue-400'
                          : 'dark:text-white'
                      }`}
                    >
                      {service.displayName}
                    </h4>
                  </div>
                  <div className="space-y-1">
                    <code className="text-xs text-gray-500 dark:text-gray-400 block truncate">
                      {service.serviceName}
                    </code>
                    <div className="flex items-center gap-2">
                      <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 text-xs">
                        v{service.version}
                      </Badge>
                      <span className="text-xs text-gray-500 dark:text-gray-400">
                        {service.interfaceCount} 个接口
                      </span>
                    </div>
                    <div className="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
                      <Clock className="w-3 h-3" />
                      {service.syncTime}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* 右侧接口列表 */}
        <div className="col-span-9">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-4 border-b dark:border-gray-700">
              <div className="flex items-center justify-between">
                <h3 className="dark:text-white">
                  {selectedService?.displayName} - 接口列表
                </h3>
                <div className="flex items-center gap-3">
                  <Tabs value={viewMode} onValueChange={(v) => setViewMode(v as 'list' | 'group')}>
                    <TabsList className="dark:bg-gray-900">
                      <TabsTrigger value="list" className="data-[state=active]:bg-blue-600 data-[state=active]:text-white">
                        <List className="w-4 h-4 mr-1" />
                        列表视图
                      </TabsTrigger>
                      <TabsTrigger value="group" className="data-[state=active]:bg-blue-600 data-[state=active]:text-white">
                        <Grid className="w-4 h-4 mr-1" />
                        分组视图
                      </TabsTrigger>
                    </TabsList>
                  </Tabs>
                  <span className="text-sm text-gray-500 dark:text-gray-400">
                    共 {filteredInterfaces.length} 个接口
                  </span>
                </div>
              </div>
            </div>

            {filteredInterfaces.length > 0 ? (
              <>
                {viewMode === 'list' ? (
                  <>
                    <div className="divide-y dark:divide-gray-700">
                      {paginatedInterfaces.map((iface) => renderInterfaceItem(iface))}
                    </div>

                    {/* 分页 */}
                    {totalPages > 1 && (
                      <div className="p-4 border-t dark:border-gray-700 flex items-center justify-between">
                        <div className="text-sm text-gray-500 dark:text-gray-400">
                          显示 {(currentPage - 1) * pageSize + 1} 到{' '}
                          {Math.min(currentPage * pageSize, filteredInterfaces.length)} 条，共{' '}
                          {filteredInterfaces.length} 条
                        </div>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                            disabled={currentPage === 1}
                            className="dark:border-gray-600 dark:hover:bg-gray-700"
                          >
                            <ChevronLeft className="w-4 h-4" />
                            上一页
                          </Button>
                          <div className="flex items-center gap-1">
                            {Array.from({ length: totalPages }, (_, i) => i + 1)
                              .filter((page) => {
                                return (
                                  page === 1 ||
                                  page === totalPages ||
                                  (page >= currentPage - 1 && page <= currentPage + 1)
                                );
                              })
                              .map((page, index, array) => (
                                <div key={page} className="flex items-center">
                                  {index > 0 && array[index - 1] !== page - 1 && (
                                    <span className="px-2 text-gray-400">...</span>
                                  )}
                                  <Button
                                    variant={currentPage === page ? 'default' : 'outline'}
                                    size="sm"
                                    onClick={() => setCurrentPage(page)}
                                    className={
                                      currentPage === page
                                        ? 'bg-blue-600 hover:bg-blue-700'
                                        : 'dark:border-gray-600 dark:hover:bg-gray-700'
                                    }
                                  >
                                    {page}
                                  </Button>
                                </div>
                              ))}
                          </div>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                            disabled={currentPage === totalPages}
                            className="dark:border-gray-600 dark:hover:bg-gray-700"
                          >
                            下一页
                            <ChevronRightIcon className="w-4 h-4" />
                          </Button>
                        </div>
                      </div>
                    )}
                  </>
                ) : (
                  <div className="max-h-[700px] overflow-y-auto">
                    {Object.entries(groupedInterfaces).map(([tag, interfaces]) => (
                      <div key={tag} className="border-b dark:border-gray-700 last:border-0">
                        <div className="bg-gray-50 dark:bg-gray-900/50 px-4 py-3 sticky top-0 z-10">
                          <div className="flex items-center gap-2">
                            <Tag className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                            <h4 className="dark:text-white">{tag}</h4>
                            <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 text-xs">
                              {interfaces.length} 个接口
                            </Badge>
                          </div>
                        </div>
                        <div className="divide-y dark:divide-gray-700">
                          {interfaces.map((iface) => renderInterfaceItem(iface))}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </>
            ) : (
              <div className="p-12 text-center text-gray-500 dark:text-gray-400">
                <Network className="w-12 h-12 mx-auto mb-3 opacity-50" />
                <p>未找到匹配的接口</p>
              </div>
            )}
          </Card>
        </div>
      </div>

      {/* 接口详情对话框 */}
      <Dialog open={showDetailDialog} onOpenChange={setShowDetailDialog}>
        <DialogContent className="max-w-4xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white flex items-center gap-2">
              <Code className="w-5 h-5" />
              接口详情 - {selectedService?.displayName}
            </DialogTitle>
          </DialogHeader>

          {selectedInterface && (
            <div className="space-y-6 max-h-[70vh] overflow-y-auto">
              {/* 基本信息 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">基本信息</h3>
                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">接口编码:</span>
                    <code className="text-sm dark:text-white font-mono bg-gray-100 dark:bg-gray-900 px-2 py-1 rounded">
                      {selectedInterface.code}
                    </code>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">请求方法:</span>
                    {getMethodBadge(selectedInterface.method)}
                  </div>
                  <div className="flex items-start gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">接口路径:</span>
                    <code className="text-sm dark:text-white font-mono bg-gray-100 dark:bg-gray-900 px-2 py-1 rounded flex-1">
                      {selectedInterface.path}
                    </code>
                  </div>
                  <div className="flex items-start gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">接口描述:</span>
                    <p className="text-sm dark:text-white flex-1">{selectedInterface.summary}</p>
                  </div>
                  <div className="flex items-start gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">详细说明:</span>
                    <p className="text-sm dark:text-gray-300 flex-1">{selectedInterface.description}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-sm text-gray-500 dark:text-gray-400 w-24">接口标签:</span>
                    <div className="flex items-center gap-2">
                      {selectedInterface.tags.map((tag) => (
                        <Badge
                          key={tag}
                          className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0"
                        >
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* 请求参数 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">请求参数</h3>
                {selectedInterface.parameters.length > 0 ? (
                  <div className="border dark:border-gray-700 rounded-lg overflow-hidden">
                    <table className="w-full text-sm">
                      <thead className="bg-gray-50 dark:bg-gray-900/50">
                        <tr>
                          <th className="text-left p-3 text-gray-600 dark:text-gray-400">参数名</th>
                          <th className="text-left p-3 text-gray-600 dark:text-gray-400">类型</th>
                          <th className="text-left p-3 text-gray-600 dark:text-gray-400">位置</th>
                          <th className="text-left p-3 text-gray-600 dark:text-gray-400">必填</th>
                          <th className="text-left p-3 text-gray-600 dark:text-gray-400">说明</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y dark:divide-gray-700">
                        {selectedInterface.parameters.map((param, index) => (
                          <tr key={index}>
                            <td className="p-3">
                              <code className="text-sm dark:text-white">{param.name}</code>
                            </td>
                            <td className="p-3">
                              <code className="text-xs text-gray-600 dark:text-gray-400">{param.type}</code>
                            </td>
                            <td className="p-3">
                              <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0 text-xs">
                                {param.in}
                              </Badge>
                            </td>
                            <td className="p-3">
                              {param.required ? (
                                <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0 text-xs">
                                  必填
                                </Badge>
                              ) : (
                                <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0 text-xs">
                                  可选
                                </Badge>
                              )}
                            </td>
                            <td className="p-3 text-gray-600 dark:text-gray-400">{param.description}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p className="text-sm text-gray-500 dark:text-gray-400">无请求参数</p>
                )}
              </div>

              {/* 响应状态码 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">响应状态码</h3>
                <div className="space-y-2">
                  {Object.entries(selectedInterface.responses).map(([code, description]) => (
                    <div
                      key={code}
                      className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg"
                    >
                      <Badge
                        className={`${
                          code.startsWith('2')
                            ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                            : code.startsWith('4')
                            ? 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400'
                            : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                        } border-0 font-mono`}
                      >
                        {code}
                      </Badge>
                      <span className="text-sm dark:text-gray-300">{description}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* 使用说明 */}
              <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4 border border-blue-200 dark:border-blue-800">
                <h4 className="text-sm mb-2 dark:text-blue-300 flex items-center gap-2">
                  <FileJson className="w-4 h-4" />
                  授权说明
                </h4>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  此接口信息可用于配置接口授权和应用授权。通过权限策略模块，您可以将此接口授权给特定用户、角色或应用程序访问。
                </p>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
