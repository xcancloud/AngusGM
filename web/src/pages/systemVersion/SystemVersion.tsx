import { Cloud, Package, Calendar, Shield, User, Building2, Clock, Info } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';

export function SystemVersion() {
  // 版本信息
  const versionInfo = {
    versionType: '云服务版',
    appCode: 'AngusGM',
    appVersion: '1.0.0',
    provider: 'CN=XCan Cloud @ @ https://www.xcan.cloud',
    publisher: 'CN=XCan Cloud',
    holder: 'CN=XCan Cloud',
    releaseDate: '--',
    expiryDate: '剩余90天',
    expiryDays: 90,
    totalDays: 365,
  };

  // 更新历史
  const updateHistory = [
    {
      version: '1.0.0',
      date: '2025-10-01',
      type: '正式版本',
      description: 'AngusGM全局管理平台正式发布，提供完整的组织人员管理、权限管理、系统消息等功能。',
    },
    {
      version: '0.9.5',
      date: '2025-09-20',
      type: 'Beta版本',
      description: '完成系统测试，修复已知问题，优化用户体验。',
    },
    {
      version: '0.9.0',
      date: '2025-09-01',
      type: 'Beta版本',
      description: '新增短信和邮件消息管理功能，完善权限策略配置。',
    },
  ];

  const getExpiryColor = (days: number) => {
    if (days > 60) return 'text-green-600 dark:text-green-400';
    if (days > 30) return 'text-orange-600 dark:text-orange-400';
    return 'text-red-600 dark:text-red-400';
  };

  const getExpiryProgress = (days: number, total: number) => {
    return ((total - days) / total) * 100;
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div>
        <h2 className="text-2xl dark:text-white mb-2">系统版本</h2>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          查看系统版本信息、许可证状态和更新历史
        </p>
      </div>

      {/* 版本信息卡片 */}
      <Card className="p-8 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex items-start justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center">
              <Package className="w-8 h-8 text-white" />
            </div>
            <div>
              <h3 className="text-2xl dark:text-white mb-1">{versionInfo.appCode}</h3>
              <div className="flex items-center gap-2">
                <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0">
                  版本 {versionInfo.appVersion}
                </Badge>
                <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                  <Cloud className="w-3 h-3 mr-1" />
                  {versionInfo.versionType}
                </Badge>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* 左列 */}
          <div className="space-y-6">
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <Building2 className="w-4 h-4" />
                <span>应用编码</span>
              </div>
              <div className="dark:text-white pl-6">{versionInfo.appCode}</div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <Package className="w-4 h-4" />
                <span>应用版本</span>
              </div>
              <div className="dark:text-white pl-6">{versionInfo.appVersion}</div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <Shield className="w-4 h-4" />
                <span>提供者</span>
              </div>
              <div className="dark:text-white pl-6 text-sm break-all">{versionInfo.provider}</div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <User className="w-4 h-4" />
                <span>发行者</span>
              </div>
              <div className="dark:text-white pl-6">{versionInfo.publisher}</div>
            </div>
          </div>

          {/* 右列 */}
          <div className="space-y-6">
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <User className="w-4 h-4" />
                <span>持有者</span>
              </div>
              <div className="dark:text-white pl-6">{versionInfo.holder}</div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <Calendar className="w-4 h-4" />
                <span>发行日期</span>
              </div>
              <div className="dark:text-white pl-6">{versionInfo.releaseDate}</div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                <Clock className="w-4 h-4" />
                <span>过期日期</span>
              </div>
              <div className={`pl-6 ${getExpiryColor(versionInfo.expiryDays)}`}>
                {versionInfo.expiryDate}
              </div>
            </div>

            {/* 许可证有效期进度 */}
            <div className="space-y-3 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-600 dark:text-gray-400">许可证有效期</span>
                <span className={getExpiryColor(versionInfo.expiryDays)}>
                  {versionInfo.expiryDays}/{versionInfo.totalDays} 天
                </span>
              </div>
              <Progress 
                value={getExpiryProgress(versionInfo.expiryDays, versionInfo.totalDays)} 
                className="h-2"
              />
            </div>
          </div>
        </div>
      </Card>

      {/* 更新历史 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <Clock className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">更新历史</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            系统版本更新记录和变更说明
          </p>
        </div>

        <div className="p-6">
          <div className="space-y-6">
            {updateHistory.map((update, index) => (
              <div key={index} className="relative pl-8 pb-6 border-l-2 border-gray-200 dark:border-gray-700 last:border-l-0 last:pb-0">
                <div className="absolute left-0 top-0 w-4 h-4 -ml-2 bg-blue-500 rounded-full border-4 border-white dark:border-gray-800"></div>
                
                <div className="space-y-2">
                  <div className="flex items-center gap-3">
                    <h4 className="dark:text-white">版本 {update.version}</h4>
                    <Badge 
                      variant="outline" 
                      className={
                        update.type === '正式版本' 
                          ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0'
                          : 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-0'
                      }
                    >
                      {update.type}
                    </Badge>
                  </div>
                  
                  <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                    <Calendar className="w-4 h-4" />
                    <span>{update.date}</span>
                  </div>
                  
                  <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                    {update.description}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </Card>

      {/* 许可证提示 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700 border-l-4 border-l-blue-500">
        <div className="flex items-start gap-4">
          <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
            <Info className="w-5 h-5 text-blue-600 dark:text-blue-400" />
          </div>
          <div className="flex-1">
            <h4 className="dark:text-white mb-2">许可证信息</h4>
            <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
              当前使用的是{versionInfo.versionType}，许可证剩余 <span className={getExpiryColor(versionInfo.expiryDays)}>{versionInfo.expiryDays} 天</span>。
              许可证到期前，请及时联系服务提供商进行续期，以确保系统正常运行。
              如有任何问题，请访问 <a href="https://www.xcan.cloud" className="text-blue-500 hover:underline">https://www.xcan.cloud</a> 获取技术支持。
            </p>
          </div>
        </div>
      </Card>
    </div>
  );
}