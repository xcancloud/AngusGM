import { useState } from 'react';
import { Bell, Search, RefreshCw, Check, Star, Archive, Trash2, ChevronDown, Circle, CheckCircle, AlertCircle, TrendingUp, ChevronLeft, ChevronRight } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

interface Notification {
  id: string;
  title: string;
  content: string;
  source: string;
  category: string;
  status: 'success' | 'warning' | 'error' | 'info';
  isRead: boolean;
  isStarred: boolean;
  isArchived: boolean;
  time: string;
  tags: string[];
}

export function NotificationCenter() {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedSource, setSelectedSource] = useState('all');
  const [selectedNotifications, setSelectedNotifications] = useState<string[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const [notifications, setNotifications] = useState<Notification[]>([
    {
      id: '1',
      title: '租户 TechFlow Inc 升级为企业版',
      content: '租户 TechFlow Inc 已成功从社区版升级至企业版。新增100+用户授权，月费用提加至 ¥4,999。',
      source: 'Subscription Service',
      category: 'all',
      status: 'success',
      isRead: false,
      isStarred: false,
      isArchived: false,
      time: '2024-12-03 15:30:00',
      tags: ['核已归档', '星级推荐', '归档'],
    },
    {
      id: '2',
      title: '新用户注册：StartupHub',
      content: '新租户 StartupHub 已成功注册体系。团队规模 15人，选择月付计划。预计月收入 ¥299。',
      source: 'User Management',
      category: 'all',
      status: 'success',
      isRead: false,
      isStarred: true,
      isArchived: false,
      time: '2024-12-03 14:00:00',
      tags: ['核已归档', '星级推荐', '归档'],
    },
    {
      id: '3',
      title: '账单支付超期警告：CloudNet Systems',
      content: 'CloudNet Systems 的 ¥4,999 账单已逾期7天未支付。系统已发送第二次催款邮件。建议人工跟进。',
      source: 'Billing System',
      category: 'unread',
      status: 'error',
      isRead: false,
      isStarred: true,
      isArchived: false,
      time: '2024-12-03 13:45:00',
      tags: ['核已归档', '星级推荐', '归档'],
    },
    {
      id: '4',
      title: '优惠券使用量激增提醒',
      content: '优惠券 BLKFRIDAY2024 在过去24小时内使用激增30%。已使用568次。预计券金额 ¥28,400。',
      source: 'Marketing Service',
      category: 'starred',
      status: 'warning',
      isRead: true,
      isStarred: true,
      isArchived: false,
      time: '2024-12-03 12:00:00',
      tags: ['星级推荐', '归档'],
    },
    {
      id: '5',
      title: '系统维护通知：数据库升级',
      content: '系统将于今晚22:00-24:00进行数据库升级维护。期间部分功能可能受影响，请提前做好准备。',
      source: 'System Management',
      category: 'all',
      status: 'warning',
      isRead: false,
      isStarred: false,
      isArchived: false,
      time: '2024-12-03 11:20:00',
      tags: ['归档'],
    },
    {
      id: '6',
      title: '租户 DataFlow Pro 资源配额告警',
      content: 'DataFlow Pro 的存储空间使用率已达85%，建议及时清理或升级配额。当前使用量 42.5GB / 50GB。',
      source: 'Resource Monitor',
      category: 'unread',
      status: 'warning',
      isRead: false,
      isStarred: false,
      isArchived: false,
      time: '2024-12-03 10:15:00',
      tags: ['归档'],
    },
    {
      id: '7',
      title: 'LDAP集成测试成功',
      content: '企业客户 MegaCorp 的LDAP集成测试已成功完成。已同步156个用户账户，组织结构映射正常。',
      source: 'Integration Service',
      category: 'all',
      status: 'success',
      isRead: true,
      isStarred: false,
      isArchived: false,
      time: '2024-12-03 09:40:00',
      tags: ['核已归档'],
    },
    {
      id: '8',
      title: '安全警告：异常登录检测',
      content: '检测到租户 SecureNet 有异常登录行为。来自IP 192.168.1.100的连续失败登录尝试已达10次。',
      source: 'Security Service',
      category: 'unread',
      status: 'error',
      isRead: false,
      isStarred: true,
      isArchived: false,
      time: '2024-12-03 08:30:00',
      tags: ['核已归档', '星级推荐'],
    },
    {
      id: '9',
      title: '新功能发布：多因素认证',
      content: '系统新增多因素认证(MFA)功能，支持短信验证码、邮箱验证和authenticator应用。建议推荐给企业客户。',
      source: 'Product Team',
      category: 'all',
      status: 'info',
      isRead: true,
      isStarred: false,
      isArchived: false,
      time: '2024-12-02 16:00:00',
      tags: ['归档'],
    },
    {
      id: '10',
      title: '月度报告：11月营收统计',
      content: '11月总营收达 ¥856,200，环比增长12.5%。新增租户37个，续费率92.3%。详细数据已发送至管理层邮箱。',
      source: 'Analytics Service',
      category: 'starred',
      status: 'success',
      isRead: true,
      isStarred: true,
      isArchived: false,
      time: '2024-12-01 10:00:00',
      tags: ['核已归档', '星级推荐'],
    },
    {
      id: '11',
      title: '租户 InnovateLabs 申请定制开发',
      content: 'InnovateLabs 提交了定制开发需求，要求增加API接口和第三方集成功能。预估项目周期4周，预算 ¥50,000。',
      source: 'Sales Team',
      category: 'all',
      status: 'info',
      isRead: false,
      isStarred: false,
      isArchived: false,
      time: '2024-12-01 14:30:00',
      tags: ['归档'],
    },
    {
      id: '12',
      title: '系统性能优化完成',
      content: '数据库查询优化和缓存策略调整已完成。平均响应时间从320ms降至180ms，性能提升43.8%。',
      source: 'DevOps Team',
      category: 'all',
      status: 'success',
      isRead: true,
      isStarred: false,
      isArchived: false,
      time: '2024-11-30 18:00:00',
      tags: ['核已归档'],
    },
  ]);

  // 统计数据
  const totalCount = notifications.length;
  const unreadCount = notifications.filter(n => !n.isRead).length;
  const starredCount = notifications.filter(n => n.isStarred).length;
  const todayCount = 12; // 今日新增
  const yesterdayCount = 9; // 昨日数量
  const todayDiff = todayCount - yesterdayCount;

  const stats = [
    {
      label: '总消息',
      value: totalCount,
      subtext: '所有●●●消息',
      icon: Bell,
      iconBg: 'bg-blue-100 dark:bg-blue-900/30',
      iconColor: 'text-blue-600 dark:text-blue-400',
    },
    {
      label: '未读消息',
      value: unreadCount,
      subtext: '待待关注',
      icon: Circle,
      iconBg: 'bg-orange-100 dark:bg-orange-900/30',
      iconColor: 'text-orange-600 dark:text-orange-400',
    },
    {
      label: '星标消息',
      value: starredCount,
      subtext: '重要消息',
      icon: Star,
      iconBg: 'bg-yellow-100 dark:bg-yellow-900/30',
      iconColor: 'text-yellow-600 dark:text-yellow-400',
    },
    {
      label: '今日新增',
      value: todayCount,
      subtext: `↗️ +${todayDiff} 较昨日`,
      icon: TrendingUp,
      iconBg: 'bg-green-100 dark:bg-green-900/30',
      iconColor: 'text-green-600 dark:text-green-400',
    },
  ];

  const categories = [
    { id: 'all', label: '全部消息', count: totalCount },
    { id: 'unread', label: '未读消息', count: unreadCount },
    { id: 'starred', label: '星标消息', count: starredCount },
    { id: 'archived', label: '已归档', count: 0 },
  ];

  // 过滤通知
  const filteredNotifications = notifications.filter(notification => {
    const matchesSearch = notification.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         notification.content.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'all' ||
                           (selectedCategory === 'unread' && !notification.isRead) ||
                           (selectedCategory === 'starred' && notification.isStarred) ||
                           (selectedCategory === 'archived' && notification.isArchived);
    return matchesSearch && matchesCategory;
  });

  // 计算分页相关数据
  const totalPages = Math.ceil(filteredNotifications.length / itemsPerPage);
  const paginatedNotifications = filteredNotifications.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  // 重置页码
  const handleCategoryChange = (categoryId: string) => {
    setSelectedCategory(categoryId);
    setCurrentPage(1);
  };

  const handleSearchChange = (value: string) => {
    setSearchQuery(value);
    setCurrentPage(1);
  };

  // 标记全部已读
  const markAllAsRead = () => {
    setNotifications(notifications.map(n => ({ ...n, isRead: true })));
    toast.success('已标记全部消息为已读');
  };

  // 刷新
  const handleRefresh = () => {
    toast.success('已刷新消息列表');
  };

  // 切换星标
  const toggleStar = (id: string) => {
    setNotifications(notifications.map(n =>
      n.id === id ? { ...n, isStarred: !n.isStarred } : n
    ));
  };

  // 切换选中
  const toggleSelection = (id: string) => {
    setSelectedNotifications(prev =>
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    );
  };

  // 标记已读
  const markAsRead = (id: string) => {
    setNotifications(notifications.map(n =>
      n.id === id ? { ...n, isRead: true } : n
    ));
  };

  // 归档
  const archiveNotification = (id: string) => {
    setNotifications(notifications.map(n =>
      n.id === id ? { ...n, isArchived: true } : n
    ));
    toast.success('消息已归档');
  };

  // 删除
  const deleteNotification = (id: string) => {
    setNotifications(notifications.filter(n => n.id !== id));
    toast.success('消息已删除');
  };

  // 获取状态图标和样式
  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'success':
        return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'warning':
        return <AlertCircle className="w-5 h-5 text-orange-500" />;
      case 'error':
        return <AlertCircle className="w-5 h-5 text-red-500" />;
      default:
        return <Circle className="w-5 h-5 text-blue-500" />;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'success':
        return <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">成功</Badge>;
      case 'warning':
        return <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400">警告</Badge>;
      case 'error':
        return <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400">维护</Badge>;
      default:
        return <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400">信息</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
            <Bell className="w-6 h-6 text-blue-600 dark:text-blue-400" />
          </div>
          <div>
            <h1 className="text-2xl dark:text-white">消息通知</h1>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              查看和管理系统通知消息
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={markAllAsRead}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            <Check className="w-4 h-4 mr-2" />
            全部已读
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={handleRefresh}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            刷新
          </Button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <Card key={stat.label} className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{stat.label}</p>
                <p className="text-3xl dark:text-white mb-1">{stat.value}</p>
                <p className="text-xs text-gray-500 dark:text-gray-400">{stat.subtext}</p>
              </div>
              <div className={`p-3 rounded-lg ${stat.iconBg}`}>
                <stat.icon className={`w-6 h-6 ${stat.iconColor}`} />
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Left Sidebar - Categories */}
        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700 h-fit">
          <div className="space-y-4">
            <div>
              <h3 className="text-sm text-gray-600 dark:text-gray-400 mb-3">消息分类</h3>
              <div className="space-y-1">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    onClick={() => handleCategoryChange(category.id)}
                    className={`w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors ${
                      selectedCategory === category.id
                        ? 'bg-gray-900 dark:bg-gray-700 text-white'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      {category.id === 'all' && <Bell className="w-4 h-4" />}
                      {category.id === 'unread' && <Circle className="w-4 h-4" />}
                      {category.id === 'starred' && <Star className="w-4 h-4" />}
                      {category.id === 'archived' && <Archive className="w-4 h-4" />}
                      <span>{category.label}</span>
                    </div>
                    <Badge
                      variant="secondary"
                      className={`${
                        selectedCategory === category.id
                          ? 'bg-white/20 text-white'
                          : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300'
                      }`}
                    >
                      {category.count}
                    </Badge>
                  </button>
                ))}
              </div>
            </div>

            <div>
              <h3 className="text-sm text-gray-600 dark:text-gray-400 mb-3">按发送端分类</h3>
              <Select value={selectedSource} onValueChange={setSelectedSource}>
                <SelectTrigger className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200">
                  <SelectValue placeholder="所有来源" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                  <SelectItem value="all">所有来源</SelectItem>
                  <SelectItem value="subscription">订阅服务</SelectItem>
                  <SelectItem value="user">用户管理</SelectItem>
                  <SelectItem value="billing">账单系统</SelectItem>
                  <SelectItem value="marketing">营销服务</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </Card>

        {/* Right Content - Notifications List */}
        <div className="lg:col-span-3 space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg dark:text-white">消息列表</h3>
              <div className="relative flex-1 max-w-md ml-4">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索消息..."
                  value={searchQuery}
                  onChange={(e) => handleSearchChange(e.target.value)}
                  className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:placeholder-gray-400"
                />
              </div>
            </div>

            <div className="space-y-3">
              {paginatedNotifications.length === 0 ? (
                <div className="text-center py-12 text-gray-500 dark:text-gray-400">
                  暂无消息
                </div>
              ) : (
                paginatedNotifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`p-4 rounded-lg border transition-colors ${
                      notification.isRead
                        ? 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700'
                        : 'bg-blue-50 dark:bg-blue-900/10 border-blue-200 dark:border-blue-800'
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      {/* Checkbox */}
                      <input
                        type="checkbox"
                        checked={selectedNotifications.includes(notification.id)}
                        onChange={() => toggleSelection(notification.id)}
                        className="mt-1 w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />

                      {/* Status Icon */}
                      <div className="mt-0.5">
                        {getStatusIcon(notification.status)}
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-4 mb-2">
                          <h4 className="text-sm dark:text-white">
                            {notification.title}
                          </h4>
                          <div className="flex items-center gap-2 shrink-0">
                            {getStatusBadge(notification.status)}
                            <span className="text-xs text-gray-500 dark:text-gray-400">●</span>
                            <span className="text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">
                              {notification.time}
                            </span>
                          </div>
                        </div>
                        
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                          {notification.content}
                        </p>

                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2 flex-wrap">
                            {notification.tags.map((tag, index) => (
                              <div key={index} className="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
                                {tag === '核已归档' && <CheckCircle className="w-3 h-3" />}
                                {tag === '星级推荐' && <Star className="w-3 h-3 fill-orange-400 text-orange-400" />}
                                {tag === '归档' && <Archive className="w-3 h-3" />}
                                {tag === '删除' && <Trash2 className="w-3 h-3" />}
                                <span>{tag}</span>
                              </div>
                            ))}
                          </div>
                          <div className="text-xs text-gray-500 dark:text-gray-400">
                            {notification.source}
                          </div>
                        </div>

                        {/* Actions */}
                        <div className="flex items-center gap-2 mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => markAsRead(notification.id)}
                            disabled={notification.isRead}
                            className="h-7 text-xs"
                          >
                            <Check className="w-3 h-3 mr-1" />
                            标记已读
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => toggleStar(notification.id)}
                            className="h-7 text-xs"
                          >
                            <Star className={`w-3 h-3 mr-1 ${notification.isStarred ? 'fill-yellow-400 text-yellow-400' : ''}`} />
                            星标
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => archiveNotification(notification.id)}
                            className="h-7 text-xs"
                          >
                            <Archive className="w-3 h-3 mr-1" />
                            归档
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => deleteNotification(notification.id)}
                            className="h-7 text-xs text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                          >
                            <Trash2 className="w-3 h-3 mr-1" />
                            删除
                          </Button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* Pagination */}
            <div className="flex items-center justify-between mt-4">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                disabled={currentPage === 1}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <ChevronLeft className="w-4 h-4 mr-2" />
                上一页
              </Button>
              <div className="text-sm text-gray-500 dark:text-gray-400">
                第 {currentPage} 页，共 {totalPages} 页
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                disabled={currentPage === totalPages}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                下一页
                <ChevronRight className="w-4 h-4 ml-2" />
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}