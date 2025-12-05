import { useState } from 'react';
import { FileText, Key, User, Download, Search, Filter, Eye, Calendar } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Label } from '@/components/ui/label';

interface ApiLog {
  id: string;
  apiKey: string;
  endpoint: string;
  method: string;
  statusCode: number;
  responseTime: number;
  ip: string;
  timestamp: string;
  userAgent: string;
}

interface UserLog {
  id: string;
  userId: string;
  username: string;
  action: string;
  resource: string;
  ip: string;
  timestamp: string;
  details: string;
}

interface SystemLog {
  id: string;
  filename: string;
  size: string;
  date: string;
  type: 'application' | 'error' | 'access' | 'security';
}

export function AuditLogs() {
  const [activeTab, setActiveTab] = useState('user');
  
  const [apiLogs] = useState<ApiLog[]>([
    {
      id: 'API001',
      apiKey: 'sk_live_***abc123',
      endpoint: '/api/v1/users',
      method: 'GET',
      statusCode: 200,
      responseTime: 45,
      ip: '192.168.1.100',
      timestamp: '2025-10-22 14:30:25',
      userAgent: 'Mozilla/5.0...',
    },
    {
      id: 'API002',
      apiKey: 'sk_live_***def456',
      endpoint: '/api/v1/tenants',
      method: 'POST',
      statusCode: 201,
      responseTime: 123,
      ip: '192.168.1.101',
      timestamp: '2025-10-22 14:28:15',
      userAgent: 'PostmanRuntime/7.29.2',
    },
    {
      id: 'API003',
      apiKey: 'sk_live_***abc123',
      endpoint: '/api/v1/auth/login',
      method: 'POST',
      statusCode: 401,
      responseTime: 234,
      ip: '192.168.1.100',
      timestamp: '2025-10-22 14:25:10',
      userAgent: 'curl/7.68.0',
    },
  ]);

  const [userLogs] = useState<UserLog[]>([
    {
      id: 'U001',
      userId: 'U001',
      username: '张三',
      action: '创建用户',
      resource: '用户管理',
      ip: '192.168.1.50',
      timestamp: '2025-10-22 14:30:00',
      details: '创建新用户：李四',
    },
    {
      id: 'U002',
      userId: 'U002',
      username: '李四',
      action: '修改权限',
      resource: '权限管理',
      ip: '192.168.1.51',
      timestamp: '2025-10-22 14:25:30',
      details: '为用户王五授予管理员权限',
    },
    {
      id: 'U003',
      userId: 'U001',
      username: '张三',
      action: '删除租户',
      resource: '租户管理',
      ip: '192.168.1.50',
      timestamp: '2025-10-22 14:20:15',
      details: '删除租户：测试企业',
    },
  ]);

  const [systemLogs] = useState<SystemLog[]>([
    {
      id: 'SYS001',
      filename: 'application-2025-10-22.log',
      size: '15.2 MB',
      date: '2025-10-22',
      type: 'application',
    },
    {
      id: 'SYS002',
      filename: 'error-2025-10-22.log',
      size: '2.3 MB',
      date: '2025-10-22',
      type: 'error',
    },
    {
      id: 'SYS003',
      filename: 'access-2025-10-22.log',
      size: '45.8 MB',
      date: '2025-10-22',
      type: 'access',
    },
    {
      id: 'SYS004',
      filename: 'security-2025-10-22.log',
      size: '8.1 MB',
      date: '2025-10-22',
      type: 'security',
    },
    {
      id: 'SYS005',
      filename: 'application-2025-10-21.log',
      size: '14.7 MB',
      date: '2025-10-21',
      type: 'application',
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [methodFilter, setMethodFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [actionFilter, setActionFilter] = useState('all');
  const [typeFilter, setTypeFilter] = useState('all');
  const [selectedLog, setSelectedLog] = useState<any>(null);
  const [showDetailDialog, setShowDetailDialog] = useState(false);
  const [logContent, setLogContent] = useState('');
  const [showLogContentDialog, setShowLogContentDialog] = useState(false);

  const getStatusBadge = (code: number) => {
    if (code >= 200 && code < 300) {
      return <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
        {code}
      </Badge>;
    }
    if (code >= 400 && code < 500) {
      return <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-0">
        {code}
      </Badge>;
    }
    if (code >= 500) {
      return <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
        {code}
      </Badge>;
    }
    return <Badge variant="outline">{code}</Badge>;
  };

  const getMethodBadge = (method: string) => {
    const colors: Record<string, string> = {
      GET: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      POST: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      PUT: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      DELETE: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
    };
    return <Badge className={`${colors[method] || ''} border-0`}>{method}</Badge>;
  };

  const getLogTypeBadge = (type: string) => {
    const configs: Record<string, { label: string; className: string }> = {
      application: { label: '应用日志', className: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' },
      error: { label: '错误日志', className: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400' },
      access: { label: '访问日志', className: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' },
      security: { label: '安全日志', className: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400' },
    };
    const config = configs[type] || configs.application;
    return <Badge className={`${config.className} border-0`}>{config.label}</Badge>;
  };

  const handleViewDetail = (log: any) => {
    setSelectedLog(log);
    setShowDetailDialog(true);
  };

  const handleViewLogContent = (log: SystemLog) => {
    // 模拟日志内容
    setLogContent(`[2025-10-22 14:30:25] INFO: Application started successfully
[2025-10-22 14:30:26] INFO: Database connection established
[2025-10-22 14:30:27] INFO: Server listening on port 3000
[2025-10-22 14:31:15] INFO: User 'admin' logged in from 192.168.1.100
[2025-10-22 14:32:40] WARN: High memory usage detected: 85%
[2025-10-22 14:33:20] INFO: API request: GET /api/v1/users - 200 OK
[2025-10-22 14:34:05] ERROR: Failed to send email notification
[2025-10-22 14:35:12] INFO: Cache cleared successfully
[2025-10-22 14:36:30] INFO: Background job completed: user_sync`);
    setShowLogContentDialog(true);
  };

  const handleDownloadLog = (filename: string) => {
    // 模拟下载
    console.log('Downloading:', filename);
  };

  const filteredApiLogs = apiLogs.filter(log => {
    const matchSearch = log.endpoint.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       log.apiKey.toLowerCase().includes(searchQuery.toLowerCase());
    const matchMethod = methodFilter === 'all' || log.method === methodFilter;
    const matchStatus = statusFilter === 'all' || 
      (statusFilter === '2xx' && log.statusCode >= 200 && log.statusCode < 300) ||
      (statusFilter === '4xx' && log.statusCode >= 400 && log.statusCode < 500) ||
      (statusFilter === '5xx' && log.statusCode >= 500);
    return matchSearch && matchMethod && matchStatus;
  });

  const filteredUserLogs = userLogs.filter(log => {
    const matchSearch = log.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       log.action.toLowerCase().includes(searchQuery.toLowerCase());
    const matchAction = actionFilter === 'all' || log.action.includes(actionFilter);
    return matchSearch && matchAction;
  });

  const filteredSystemLogs = systemLogs.filter(log => {
    const matchSearch = log.filename.toLowerCase().includes(searchQuery.toLowerCase());
    const matchType = typeFilter === 'all' || log.type === typeFilter;
    return matchSearch && matchType;
  });

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div>
        <h2 className="text-2xl dark:text-white mb-2">审计日志</h2>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          查看和管理API请求、用户操作和系统日志
        </p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="user" className="dark:data-[state=active]:bg-gray-700">
            <User className="w-4 h-4 mr-2" />
            用户操作日志
          </TabsTrigger>
          <TabsTrigger value="system" className="dark:data-[state=active]:bg-gray-700">
            <FileText className="w-4 h-4 mr-2" />
            系统日志文件
          </TabsTrigger>
          <TabsTrigger value="api" className="dark:data-[state=active]:bg-gray-700">
            <Key className="w-4 h-4 mr-2" />
            API请求日志
          </TabsTrigger>
        </TabsList>

        {/* API请求日志 */}
        <TabsContent value="api" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex flex-wrap gap-4">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="搜索端点或API密钥..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
              
              <Select value={methodFilter} onValueChange={setMethodFilter}>
                <SelectTrigger className="w-[120px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="请求方法" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部方法</SelectItem>
                  <SelectItem value="GET">GET</SelectItem>
                  <SelectItem value="POST">POST</SelectItem>
                  <SelectItem value="PUT">PUT</SelectItem>
                  <SelectItem value="DELETE">DELETE</SelectItem>
                </SelectContent>
              </Select>

              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-[120px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="状态码" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部状态</SelectItem>
                  <SelectItem value="2xx">2xx 成功</SelectItem>
                  <SelectItem value="4xx">4xx 客户端错误</SelectItem>
                  <SelectItem value="5xx">5xx 服务端错误</SelectItem>
                </SelectContent>
              </Select>

              <Button variant="outline" className="dark:border-gray-600 dark:hover:bg-gray-700">
                <Download className="w-4 h-4 mr-2" />
                导出
              </Button>
            </div>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">时间</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">API密钥</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">端点</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">方法</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态码</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">响应时间</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">IP地址</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredApiLogs.map((log) => (
                    <tr key={log.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="p-4 text-sm dark:text-gray-300">{log.timestamp}</td>
                      <td className="p-4">
                        <code className="text-xs dark:text-gray-400 font-mono">{log.apiKey}</code>
                      </td>
                      <td className="p-4 text-sm dark:text-gray-300">{log.endpoint}</td>
                      <td className="p-4">{getMethodBadge(log.method)}</td>
                      <td className="p-4">{getStatusBadge(log.statusCode)}</td>
                      <td className="p-4 text-sm dark:text-gray-300">{log.responseTime}ms</td>
                      <td className="p-4 text-sm dark:text-gray-300">{log.ip}</td>
                      <td className="p-4">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleViewDetail(log)}
                          className="dark:hover:bg-gray-600"
                        >
                          <Eye className="w-4 h-4" />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>

        {/* 用户操作日志 */}
        <TabsContent value="user" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex flex-wrap gap-4">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="搜索用户或操作..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
              
              <Select value={actionFilter} onValueChange={setActionFilter}>
                <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="操作类型" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部操作</SelectItem>
                  <SelectItem value="创建">创建</SelectItem>
                  <SelectItem value="修改">修改</SelectItem>
                  <SelectItem value="删除">删除</SelectItem>
                </SelectContent>
              </Select>

              <Button variant="outline" className="dark:border-gray-600 dark:hover:bg-gray-700">
                <Download className="w-4 h-4 mr-2" />
                出
              </Button>
            </div>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">时间</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">用户</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">资源</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">IP地址</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">详情</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredUserLogs.map((log) => (
                    <tr key={log.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="p-4 text-sm dark:text-gray-300">{log.timestamp}</td>
                      <td className="p-4">
                        <div className="text-sm dark:text-white">{log.username}</div>
                        <div className="text-xs text-gray-500 dark:text-gray-400">{log.userId}</div>
                      </td>
                      <td className="p-4">
                        <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                          {log.action}
                        </Badge>
                      </td>
                      <td className="p-4 text-sm dark:text-gray-300">{log.resource}</td>
                      <td className="p-4 text-sm dark:text-gray-300">{log.ip}</td>
                      <td className="p-4 text-sm text-gray-600 dark:text-gray-400 max-w-xs truncate">
                        {log.details}
                      </td>
                      <td className="p-4">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleViewDetail(log)}
                          className="dark:hover:bg-gray-600"
                        >
                          <Eye className="w-4 h-4" />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>

        {/* 系统日志文件 */}
        <TabsContent value="system" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex flex-wrap gap-4">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="搜索日志文件..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
              
              <Select value={typeFilter} onValueChange={setTypeFilter}>
                <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="日志类型" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部类型</SelectItem>
                  <SelectItem value="application">应用日志</SelectItem>
                  <SelectItem value="error">错误日志</SelectItem>
                  <SelectItem value="access">访问日志</SelectItem>
                  <SelectItem value="security">安全日志</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </Card>

          <div className="grid grid-cols-1 gap-4">
            {filteredSystemLogs.map((log) => (
              <Card key={log.id} className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                      <FileText className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                    </div>
                    <div>
                      <div className="flex items-center gap-3 mb-1">
                        <h4 className="dark:text-white">{log.filename}</h4>
                        {getLogTypeBadge(log.type)}
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                        <span className="flex items-center gap-1">
                          <Calendar className="w-4 h-4" />
                          {log.date}
                        </span>
                        <span>大小: {log.size}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleViewLogContent(log)}
                      className="dark:border-gray-600 dark:hover:bg-gray-700"
                    >
                      <Eye className="w-4 h-4 mr-2" />
                      查看
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDownloadLog(log.filename)}
                      className="dark:border-gray-600 dark:hover:bg-gray-700"
                    >
                      <Download className="w-4 h-4 mr-2" />
                      下载
                    </Button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </TabsContent>
      </Tabs>

      {/* 详情对话框 */}
      <Dialog open={showDetailDialog} onOpenChange={setShowDetailDialog}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">日志详情</DialogTitle>
          </DialogHeader>
          
          {selectedLog && (
            <div className="space-y-4">
              {activeTab === 'api' ? (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">API密钥</Label>
                      <div className="dark:text-white mt-1 font-mono text-sm">{selectedLog.apiKey}</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">请求时间</Label>
                      <div className="dark:text-white mt-1">{selectedLog.timestamp}</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">端点</Label>
                      <div className="dark:text-white mt-1">{selectedLog.endpoint}</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">请求方法</Label>
                      <div className="mt-1">{getMethodBadge(selectedLog.method)}</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">状态码</Label>
                      <div className="mt-1">{getStatusBadge(selectedLog.statusCode)}</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">响应时间</Label>
                      <div className="dark:text-white mt-1">{selectedLog.responseTime}ms</div>
                    </div>
                    <div>
                      <Label className="text-gray-600 dark:text-gray-400">IP地址</Label>
                      <div className="dark:text-white mt-1">{selectedLog.ip}</div>
                    </div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">User Agent</Label>
                    <div className="dark:text-white mt-1 text-sm break-all">{selectedLog.userAgent}</div>
                  </div>
                </>
              ) : (
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">用户</Label>
                    <div className="dark:text-white mt-1">{selectedLog.username}</div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">用户ID</Label>
                    <div className="dark:text-white mt-1">{selectedLog.userId}</div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">操作</Label>
                    <div className="dark:text-white mt-1">{selectedLog.action}</div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">资源</Label>
                    <div className="dark:text-white mt-1">{selectedLog.resource}</div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">IP地址</Label>
                    <div className="dark:text-white mt-1">{selectedLog.ip}</div>
                  </div>
                  <div>
                    <Label className="text-gray-600 dark:text-gray-400">时间</Label>
                    <div className="dark:text-white mt-1">{selectedLog.timestamp}</div>
                  </div>
                  <div className="col-span-2">
                    <Label className="text-gray-600 dark:text-gray-400">详细信息</Label>
                    <div className="dark:text-white mt-1">{selectedLog.details}</div>
                  </div>
                </div>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* 日志内容对话框 */}
      <Dialog open={showLogContentDialog} onOpenChange={setShowLogContentDialog}>
        <DialogContent className="max-w-4xl max-h-[80vh] dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">日志内容</DialogTitle>
          </DialogHeader>
          
          <div className="overflow-auto">
            <pre className="p-4 bg-gray-900 text-green-400 rounded-lg text-xs font-mono">
              {logContent}
            </pre>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}