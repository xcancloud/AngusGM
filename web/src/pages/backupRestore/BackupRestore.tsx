import { useState } from 'react';
import { Database, Download, Upload, HardDrive, Calendar, Clock, FileCheck, AlertCircle, CheckCircle2, XCircle, Play, Trash2, MoreVertical, Search, Filter, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';

interface BackupRecord {
  id: string;
  name: string;
  type: '完整备份' | '增量备份' | '差异备份';
  size: string;
  status: '成功' | '失败' | '进行中';
  createdAt: string;
  createdBy: string;
  description: string;
  path: string;
  duration: string;
}

interface BackupSchedule {
  id: string;
  name: string;
  type: '完整备份' | '增量备份' | '差异备份';
  frequency: '每日' | '每周' | '每月';
  time: string;
  retention: string;
  status: '启用' | '禁用';
  lastRun: string;
  nextRun: string;
}

export function BackupRestore() {
  const [activeTab, setActiveTab] = useState('backup-list');
  const [searchTerm, setSearchTerm] = useState('');
  const [isCreatingBackup, setIsCreatingBackup] = useState(false);
  const [backupName, setBackupName] = useState('');
  const [backupType, setBackupType] = useState('完整备份');
  const [backupDescription, setBackupDescription] = useState('');

  // 模拟备份记录数据
  const backupRecords: BackupRecord[] = [
    {
      id: 'BK001',
      name: '系统完整备份_20241203',
      type: '完整备份',
      size: '2.5 GB',
      status: '成功',
      createdAt: '2024-12-03 02:00:00',
      createdBy: '系统自动',
      description: '每日自动完整备份',
      path: '/backup/full/20241203_020000.bak',
      duration: '15分钟',
    },
    {
      id: 'BK002',
      name: '系统增量备份_20241202',
      type: '增量备份',
      size: '456 MB',
      status: '成功',
      createdAt: '2024-12-02 14:30:00',
      createdBy: '张管理员',
      description: '手动增量备份',
      path: '/backup/incremental/20241202_143000.bak',
      duration: '5分钟',
    },
    {
      id: 'BK003',
      name: '系统完整备份_20241201',
      type: '完整备份',
      size: '2.4 GB',
      status: '成功',
      createdAt: '2024-12-01 02:00:00',
      createdBy: '系统自动',
      description: '每日自动完整备份',
      path: '/backup/full/20241201_020000.bak',
      duration: '14分钟',
    },
    {
      id: 'BK004',
      name: '系统差异备份_20241130',
      type: '差异备份',
      size: '890 MB',
      status: '成功',
      createdAt: '2024-11-30 18:00:00',
      createdBy: '系统自动',
      description: '每周差异备份',
      path: '/backup/differential/20241130_180000.bak',
      duration: '8分钟',
    },
    {
      id: 'BK005',
      name: '系统完整备份_20241129',
      type: '完整备份',
      size: '2.3 GB',
      status: '失败',
      createdAt: '2024-11-29 02:00:00',
      createdBy: '系统自动',
      description: '每日自动完整备份（磁盘空间不足）',
      path: '/backup/full/20241129_020000.bak',
      duration: '2分钟',
    },
  ];

  // 模拟备份计划数据
  const backupSchedules: BackupSchedule[] = [
    {
      id: 'BS001',
      name: '每日完整备份',
      type: '完整备份',
      frequency: '每日',
      time: '02:00',
      retention: '保留30天',
      status: '启用',
      lastRun: '2024-12-03 02:00:00',
      nextRun: '2024-12-04 02:00:00',
    },
    {
      id: 'BS002',
      name: '每周差异备份',
      type: '差异备份',
      frequency: '每周',
      time: '18:00',
      retention: '保留90天',
      status: '启用',
      lastRun: '2024-11-30 18:00:00',
      nextRun: '2024-12-07 18:00:00',
    },
    {
      id: 'BS003',
      name: '每月归档备份',
      type: '完整备份',
      frequency: '每月',
      time: '00:00',
      retention: '保留365天',
      status: '启用',
      lastRun: '2024-12-01 00:00:00',
      nextRun: '2025-01-01 00:00:00',
    },
  ];

  const filteredBackups = backupRecords.filter(backup =>
    backup.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    backup.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleCreateBackup = () => {
    if (!backupName.trim()) {
      toast.error('请输入备份名称');
      return;
    }
    
    toast.success('备份任务已创建，正在执行中...');
    setIsCreatingBackup(false);
    setBackupName('');
    setBackupDescription('');
    setBackupType('完整备份');
  };

  const handleRestoreBackup = (backupId: string, backupName: string) => {
    toast.success(`正在恢复备份: ${backupName}`);
  };

  const handleDownloadBackup = (backupId: string, backupName: string) => {
    toast.success(`正在下载备份: ${backupName}`);
  };

  const handleDeleteBackup = (backupId: string, backupName: string) => {
    toast.success(`已删除备份: ${backupName}`);
  };

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { variant: any; icon: any }> = {
      '成功': { variant: 'default', icon: CheckCircle2 },
      '失败': { variant: 'destructive', icon: XCircle },
      '进行中': { variant: 'secondary', icon: Play },
    };
    
    const config = variants[status] || variants['成功'];
    const Icon = config.icon;
    
    return (
      <Badge variant={config.variant} className="gap-1">
        <Icon className="w-3 h-3" />
        {status}
      </Badge>
    );
  };

  const getTypeBadge = (type: string) => {
    const colors: Record<string, string> = {
      '完整备份': 'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300',
      '增量备份': 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300',
      '差异备份': 'bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300',
    };
    
    return (
      <Badge variant="outline" className={colors[type] || ''}>
        {type}
      </Badge>
    );
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div>
        <h2 className="text-2xl dark:text-white mb-2">备份恢复</h2>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          管理系统备份和数据恢复
        </p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="backup-list">
            <Database className="w-4 h-4 mr-2" />
            备份列表
          </TabsTrigger>
          <TabsTrigger value="backup-schedule">
            <Calendar className="w-4 h-4 mr-2" />
            备份计划
          </TabsTrigger>
          <TabsTrigger value="restore">
            <Upload className="w-4 h-4 mr-2" />
            数据恢复
          </TabsTrigger>
          <TabsTrigger value="settings">
            <HardDrive className="w-4 h-4 mr-2" />
            备份设置
          </TabsTrigger>
        </TabsList>

        {/* 备份列表 */}
        <TabsContent value="backup-list" className="space-y-4">
          {/* 统计卡片 */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm dark:text-gray-300">总备份数</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-2xl dark:text-white">156</div>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">全部备份文件</p>
                  </div>
                  <Database className="w-8 h-8 text-blue-500" />
                </div>
              </CardContent>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm dark:text-gray-300">总备份大小</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-2xl dark:text-white">68.5 GB</div>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">已使用空间</p>
                  </div>
                  <HardDrive className="w-8 h-8 text-green-500" />
                </div>
              </CardContent>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm dark:text-gray-300">成功率</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-2xl dark:text-white">98.7%</div>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">近30天成功率</p>
                  </div>
                  <CheckCircle2 className="w-8 h-8 text-green-500" />
                </div>
              </CardContent>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm dark:text-gray-300">最近备份</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-sm dark:text-white">12-03 02:00</div>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">完整备份成功</p>
                  </div>
                  <Clock className="w-8 h-8 text-blue-500" />
                </div>
              </CardContent>
            </Card>
          </div>

          {/* 操作栏 */}
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
            <div className="flex-1 w-full sm:w-auto">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  placeholder="搜索备份名称或描述..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-9 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                />
              </div>
            </div>
            <div className="flex gap-2 w-full sm:w-auto">
              <Button
                onClick={() => setIsCreatingBackup(true)}
                className="flex-1 sm:flex-initial bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Plus className="w-4 h-4 mr-2" />
                创建备份
              </Button>
            </div>
          </div>

          {/* 备份列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200 dark:border-gray-700">
                      <th className="text-left p-4 text-sm dark:text-gray-300">备份名称</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">类型</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">大小</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">状态</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">创建时间</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">创建人</th>
                      <th className="text-left p-4 text-sm dark:text-gray-300">耗时</th>
                      <th className="text-right p-4 text-sm dark:text-gray-300">操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredBackups.map((backup) => (
                      <tr key={backup.id} className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750">
                        <td className="p-4">
                          <div>
                            <div className="dark:text-white">{backup.name}</div>
                            <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">{backup.description}</div>
                          </div>
                        </td>
                        <td className="p-4">{getTypeBadge(backup.type)}</td>
                        <td className="p-4 dark:text-gray-300">{backup.size}</td>
                        <td className="p-4">{getStatusBadge(backup.status)}</td>
                        <td className="p-4 text-sm dark:text-gray-300">{backup.createdAt}</td>
                        <td className="p-4 text-sm dark:text-gray-300">{backup.createdBy}</td>
                        <td className="p-4 text-sm dark:text-gray-300">{backup.duration}</td>
                        <td className="p-4">
                          <div className="flex justify-end gap-2">
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="sm">
                                  <MoreVertical className="w-4 h-4" />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                                <DropdownMenuItem
                                  onClick={() => handleRestoreBackup(backup.id, backup.name)}
                                  className="dark:text-gray-300 dark:hover:bg-gray-700"
                                >
                                  <Upload className="w-4 h-4 mr-2" />
                                  恢复备份
                                </DropdownMenuItem>
                                <DropdownMenuItem
                                  onClick={() => handleDownloadBackup(backup.id, backup.name)}
                                  className="dark:text-gray-300 dark:hover:bg-gray-700"
                                >
                                  <Download className="w-4 h-4 mr-2" />
                                  下载备份
                                </DropdownMenuItem>
                                <DropdownMenuItem
                                  onClick={() => handleDeleteBackup(backup.id, backup.name)}
                                  className="text-red-600 dark:text-red-400 dark:hover:bg-gray-700"
                                >
                                  <Trash2 className="w-4 h-4 mr-2" />
                                  删除备份
                                </DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* 备份计划 */}
        <TabsContent value="backup-schedule" className="space-y-4">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="dark:text-white">备份计划</CardTitle>
                  <CardDescription className="dark:text-gray-400">配置自动备份任务计划</CardDescription>
                </div>
                <Button className="bg-blue-600 hover:bg-blue-700 text-white">
                  <Plus className="w-4 h-4 mr-2" />
                  新建计划
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {backupSchedules.map((schedule) => (
                  <Card key={schedule.id} className="dark:bg-gray-750 dark:border-gray-600">
                    <CardContent className="p-6">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-3">
                            <h3 className="dark:text-white">{schedule.name}</h3>
                            {getTypeBadge(schedule.type)}
                            <Badge variant={schedule.status === '启用' ? 'default' : 'secondary'}>
                              {schedule.status}
                            </Badge>
                          </div>
                          
                          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                            <div>
                              <div className="text-gray-500 dark:text-gray-400">执行频率</div>
                              <div className="dark:text-white mt-1">{schedule.frequency} {schedule.time}</div>
                            </div>
                            <div>
                              <div className="text-gray-500 dark:text-gray-400">保留策略</div>
                              <div className="dark:text-white mt-1">{schedule.retention}</div>
                            </div>
                            <div>
                              <div className="text-gray-500 dark:text-gray-400">上次执行</div>
                              <div className="dark:text-white mt-1">{schedule.lastRun}</div>
                            </div>
                            <div>
                              <div className="text-gray-500 dark:text-gray-400">下次执行</div>
                              <div className="dark:text-white mt-1">{schedule.nextRun}</div>
                            </div>
                          </div>
                        </div>
                        
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreVertical className="w-4 h-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                            <DropdownMenuItem className="dark:text-gray-300 dark:hover:bg-gray-700">
                              <Play className="w-4 h-4 mr-2" />
                              立即执行
                            </DropdownMenuItem>
                            <DropdownMenuItem className="dark:text-gray-300 dark:hover:bg-gray-700">
                              编辑计划
                            </DropdownMenuItem>
                            <DropdownMenuItem className="dark:text-gray-300 dark:hover:bg-gray-700">
                              {schedule.status === '启用' ? '禁用' : '启用'}计划
                            </DropdownMenuItem>
                            <DropdownMenuItem className="text-red-600 dark:text-red-400 dark:hover:bg-gray-700">
                              <Trash2 className="w-4 h-4 mr-2" />
                              删除计划
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* 数据恢复 */}
        <TabsContent value="restore" className="space-y-4">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <CardTitle className="dark:text-white">数据恢复</CardTitle>
              <CardDescription className="dark:text-gray-400">从备份文件恢复系统数据</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800 rounded-lg p-4">
                <div className="flex gap-3">
                  <AlertCircle className="w-5 h-5 text-amber-600 dark:text-amber-500 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-amber-900 dark:text-amber-400 mb-1">重要提示</h4>
                    <ul className="text-sm text-amber-800 dark:text-amber-300 space-y-1">
                      <li>• 数据恢复将覆盖当前系统数据，请谨慎操作</li>
                      <li>• 建议在恢复前先创建当前系统的完整备份</li>
                      <li>• 恢复过程中系统将暂时不可用，请选择合适的时间窗口</li>
                      <li>• 恢复完成后需要重启系统服务</li>
                    </ul>
                  </div>
                </div>
              </div>

              <div className="space-y-4">
                <div>
                  <Label className="dark:text-gray-300">选择备份文件</Label>
                  <select className="mt-2 w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-750 dark:text-white">
                    <option>系统完整备份_20241203 (2.5 GB) - 2024-12-03 02:00:00</option>
                    <option>系统增量备份_20241202 (456 MB) - 2024-12-02 14:30:00</option>
                    <option>系统完整备份_20241201 (2.4 GB) - 2024-12-01 02:00:00</option>
                  </select>
                </div>

                <div>
                  <Label className="dark:text-gray-300">恢复选项</Label>
                  <div className="mt-2 space-y-2">
                    <label className="flex items-center gap-2">
                      <input type="checkbox" defaultChecked className="rounded" />
                      <span className="text-sm dark:text-gray-300">恢复数据库</span>
                    </label>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" defaultChecked className="rounded" />
                      <span className="text-sm dark:text-gray-300">恢复配置文件</span>
                    </label>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" defaultChecked className="rounded" />
                      <span className="text-sm dark:text-gray-300">恢复用户上传文件</span>
                    </label>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" className="rounded" />
                      <span className="text-sm dark:text-gray-300">恢复系统日志</span>
                    </label>
                  </div>
                </div>

                <div>
                  <Label className="dark:text-gray-300">验证密码</Label>
                  <Input
                    type="password"
                    placeholder="请输入管理员密码以确认恢复操作"
                    className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                  />
                </div>

                <div className="flex gap-2 pt-4">
                  <Button className="bg-blue-600 hover:bg-blue-700 text-white">
                    <Upload className="w-4 h-4 mr-2" />
                    开始恢复
                  </Button>
                  <Button variant="outline" className="dark:border-gray-600 dark:text-gray-300">
                    验证备份文件
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 恢复历史 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <CardTitle className="dark:text-white">恢复历史</CardTitle>
              <CardDescription className="dark:text-gray-400">查看历史恢复记录</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {[
                  { date: '2024-11-15 10:30:00', backup: '系统完整备份_20241115', operator: '张管理员', status: '成功', duration: '25分钟' },
                  { date: '2024-10-20 09:00:00', backup: '系统完整备份_20241020', operator: '李管理员', status: '成功', duration: '22分钟' },
                  { date: '2024-09-10 14:20:00', backup: '系统完整备份_20240910', operator: '王管理员', status: '失败', duration: '5分钟' },
                ].map((record, index) => (
                  <div key={index} className="flex items-center justify-between p-3 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="dark:text-white">{record.backup}</span>
                        {getStatusBadge(record.status)}
                      </div>
                      <div className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                        {record.date} · 操作人: {record.operator} · 耗时: {record.duration}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* 备份设置 */}
        <TabsContent value="settings" className="space-y-4">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <CardTitle className="dark:text-white">备份存储配置</CardTitle>
              <CardDescription className="dark:text-gray-400">配置备份文件存储位置和策略</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label className="dark:text-gray-300">备份存储路径</Label>
                <Input
                  defaultValue="/var/backup/angusgm"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">最大存储空间 (GB)</Label>
                <Input
                  type="number"
                  defaultValue="100"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">备份文件保留天数</Label>
                <Input
                  type="number"
                  defaultValue="90"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">备份压缩级别</Label>
                <select defaultValue="标准压缩" className="mt-2 w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-750 dark:text-white">
                  <option>无压缩</option>
                  <option>快速压缩</option>
                  <option>标准压缩</option>
                  <option>最大压缩</option>
                </select>
              </div>

              <div className="flex items-center gap-2 pt-2">
                <input type="checkbox" defaultChecked className="rounded" />
                <Label className="dark:text-gray-300">备份前验证磁盘空间</Label>
              </div>

              <div className="flex items-center gap-2">
                <input type="checkbox" defaultChecked className="rounded" />
                <Label className="dark:text-gray-300">备份完成后发送通知</Label>
              </div>

              <div className="flex items-center gap-2">
                <input type="checkbox" className="rounded" />
                <Label className="dark:text-gray-300">启用异地备份同步</Label>
              </div>

              <Button className="mt-4 bg-blue-600 hover:bg-blue-700 text-white">
                保存配置
              </Button>
            </CardContent>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <CardTitle className="dark:text-white">备份通知配置</CardTitle>
              <CardDescription className="dark:text-gray-400">配置备份任务的通知方式</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label className="dark:text-gray-300">通知接收人</Label>
                <Input
                  defaultValue="admin@example.com, backup@example.com"
                  placeholder="多个邮箱用逗号分隔"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">通知类型</Label>
                <div className="mt-2 space-y-2">
                  <label className="flex items-center gap-2">
                    <input type="checkbox" defaultChecked className="rounded" />
                    <span className="text-sm dark:text-gray-300">备份成功时通知</span>
                  </label>
                  <label className="flex items-center gap-2">
                    <input type="checkbox" defaultChecked className="rounded" />
                    <span className="text-sm dark:text-gray-300">备份失败时通知</span>
                  </label>
                  <label className="flex items-center gap-2">
                    <input type="checkbox" className="rounded" />
                    <span className="text-sm dark:text-gray-300">备份开始时通知</span>
                  </label>
                  <label className="flex items-center gap-2">
                    <input type="checkbox" defaultChecked className="rounded" />
                    <span className="text-sm dark:text-gray-300">存储空间不足时通知</span>
                  </label>
                </div>
              </div>

              <Button className="mt-4 bg-blue-600 hover:bg-blue-700 text-white">
                保存配置
              </Button>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* 创建备份对话框 */}
      {isCreatingBackup && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md dark:bg-gray-800 dark:border-gray-700">
            <CardHeader>
              <CardTitle className="dark:text-white">创建备份</CardTitle>
              <CardDescription className="dark:text-gray-400">创建新的系统备份任务</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label className="dark:text-gray-300">备份名称</Label>
                <Input
                  value={backupName}
                  onChange={(e) => setBackupName(e.target.value)}
                  placeholder="例如: 系统完整备份_20241203"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">备份类型</Label>
                <select
                  value={backupType}
                  onChange={(e) => setBackupType(e.target.value)}
                  className="mt-2 w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-750 dark:text-white"
                >
                  <option>完整备份</option>
                  <option>增量备份</option>
                  <option>差异备份</option>
                </select>
              </div>

              <div>
                <Label className="dark:text-gray-300">备份描述</Label>
                <Input
                  value={backupDescription}
                  onChange={(e) => setBackupDescription(e.target.value)}
                  placeholder="备份说明（可选）"
                  className="mt-2 dark:bg-gray-750 dark:border-gray-600 dark:text-white"
                />
              </div>

              <div className="flex gap-2 pt-4">
                <Button
                  onClick={handleCreateBackup}
                  className="flex-1 bg-blue-600 hover:bg-blue-700 text-white"
                >
                  创建备份
                </Button>
                <Button
                  onClick={() => setIsCreatingBackup(false)}
                  variant="outline"
                  className="flex-1 dark:border-gray-600 dark:text-gray-300"
                >
                  取消
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
