import { ArrowLeft, Shield } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { AngusGMLogo } from '@/components/AngusGMLogo';
import { useNavigate } from 'react-router-dom';

interface PrivacyPolicyProps {
  onBack?: () => void;
}

export function PrivacyPolicy({ onBack }: PrivacyPolicyProps) {
  const navigate = useNavigate();
  const handleBack = () => {
    navigate(-1);
  };
  return (
    <div className="w-full min-h-screen h-fit bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-50 dark:from-gray-950 dark:via-purple-950 dark:to-gray-950">
      <div className="max-w-4xl mx-auto p-6 lg:p-12  fle flex-col">
        {/* 顶部 */}
        <div className="mb-8">
          <AngusGMLogo className="h-10 mb-6" />
          <button
            onClick={handleBack}
            className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200 transition-colors group"
          >
            <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
            返回
          </button>
        </div>

        {/* 内容卡片 */}
        <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border-0 shadow-2xl flex-1 min-h-0">
          <div className="p-8 lg:p-12">
            {/* 标题 */}
            <div className="flex items-center gap-4 mb-8 pb-8 border-b border-gray-200 dark:border-gray-700">
              <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-purple-500 to-indigo-600 flex items-center justify-center">
                <Shield className="w-7 h-7 text-white" />
              </div>
              <div>
                <h1 className="text-3xl mb-2 bg-gradient-to-r from-purple-600 to-indigo-600 dark:from-purple-400 dark:to-indigo-400 bg-clip-text text-transparent">
                  AngusGM 隐私政策
                </h1>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  最后更新日期：2024年12月4日
                </p>
              </div>
            </div>

            {/* 政策内容 */}
            <div className="prose prose-gray dark:prose-invert max-w-none">
              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">引言</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  AngusGM（以下简称"我们"）非常重视用户的隐私保护。本隐私政策说明了我们如何收集、使用、存储和保护您的个人信息。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  在使用我们的服务之前，请您仔细阅读并充分理解本隐私政策。如果您不同意本隐私政策的任何内容，请不要使用我们的服务。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">1. 我们收集的信息</h2>
                <h3 className="text-lg mb-3 text-gray-800 dark:text-gray-200">1.1 您主动提供的信息</h3>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400 mb-4">
                  <li>注册信息：手机号、邮箱地址、用户名等</li>
                  <li>账户信息：密码、头像、个人资料等</li>
                  <li>组织信息：公司名称、部门、职位等</li>
                  <li>业务数据：您在使用平台功能时产生的数据</li>
                </ul>
                
                <h3 className="text-lg mb-3 text-gray-800 dark:text-gray-200">1.2 自动收集的信息</h3>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400">
                  <li>设备信息：设备型号、操作系统、浏览器类型等</li>
                  <li>日志信息：IP地址、访问时间、访问页面等</li>
                  <li>Cookie和类似技术收集的信息</li>
                </ul>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">2. 信息的使用</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  我们收集和使用您的个人信息主要用于以下目的：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400">
                  <li>提供、维护和改进我们的服务</li>
                  <li>处理您的注册和登录请求</li>
                  <li>向您发送服务相关的通知和更新</li>
                  <li>保护服务安全，防止欺诈和滥用</li>
                  <li>进行数据分析，优化用户体验</li>
                  <li>遵守法律法规要求</li>
                </ul>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">3. 信息的共享与披露</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  我们承诺不会出售您的个人信息。在以下情况下，我们可能会共享您的信息：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400 mb-4">
                  <li>经您同意的情况下</li>
                  <li>与服务提供商共享（仅限于提供服务所必需）</li>
                  <li>法律法规要求或政府部门要求</li>
                  <li>为保护我们或他人的合法权益</li>
                  <li>在企业并购、重组等情况下</li>
                </ul>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  我们会要求所有接收您个人信息的第三方遵守本隐私政策及相关法律法规，采取适当的安全措施保护您的信息。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">4. 信息的存储与保护</h2>
                <h3 className="text-lg mb-3 text-gray-800 dark:text-gray-200">4.1 存储位置和期限</h3>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  您的个人信息将存储在中华人民共和国境内的服务器上。我们会在实现本隐私政策所述目的所必需的期间内保留您的个人信息。
                </p>
                
                <h3 className="text-lg mb-3 text-gray-800 dark:text-gray-200">4.2 安全措施</h3>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  我们采用业界标准的安全措施保护您的个人信息：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400">
                  <li>数据加密传输（HTTPS/SSL）</li>
                  <li>数据访问权限控制</li>
                  <li>定期安全审计和漏洞扫描</li>
                  <li>员工保密协议和培训</li>
                  <li>应急响应机制</li>
                </ul>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">5. 您的权利</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  根据相关法律法规，您享有以下权利：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400">
                  <li>访问权：您有权访问您的个人信息</li>
                  <li>更正权：您有权更正不准确的个人信息</li>
                  <li>删除权：在特定情况下，您有权要求删除个人信息</li>
                  <li>撤回同意权：您有权撤回您之前授予的同意</li>
                  <li>可携带权：您有权获取您的个人信息副本</li>
                  <li>拒绝权：您有权拒绝我们处理您的个人信息</li>
                </ul>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">6. Cookie 和类似技术</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  我们使用 Cookie 和类似技术来改善您的用户体验。Cookie 是存储在您设备上的小型文本文件，可帮助我们：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400 mb-4">
                  <li>记住您的登录状态</li>
                  <li>了解您如何使用我们的服务</li>
                  <li>优化服务性能</li>
                  <li>提供个性化内容</li>
                </ul>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  您可以通过浏览器设置管理或删除 Cookie，但这可能会影响您使用某些功能。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">7. 未成年人保护</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  我们的服务主要面向成年用户。如果您是未满18周岁的未成年人，请在监护人的指导下使用我们的服务。我们不会故意收集未成年人的个人信息。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">8. 隐私政策的更新</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  我们可能会不时更新本隐私政策。更新后的隐私政策将在本平台公布，并在公布后立即生效。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  如果更新涉及重大变更，我们会通过显著方式通知您。继续使用我们的服务即表示您接受更新后的隐私政策。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">9. 如何联系我们</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  如您对本隐私政策有任何疑问、意见或建议，或希望行使您的权利，请通过以下方式联系我们：
                </p>
                <ul className="list-none space-y-2 text-gray-600 dark:text-gray-400">
                  <li>📧 隐私邮箱: privacy@angusgm.com</li>
                  <li>📞 客服电话: 400-888-8888</li>
                  <li>🏢 联系地址: 北京市朝阳区建国路XX号</li>
                </ul>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mt-4">
                  我们将在收到您的请求后15个工作日内予以答复。
                </p>
              </section>
            </div>

            {/* 底部按钮 */}
            <div className="mt-12 pt-8 border-t border-gray-200 dark:border-gray-700">
              <Button
                onClick={handleBack}
                className="w-full h-12 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-purple-500/30 hover:shadow-xl hover:shadow-purple-500/40 transition-all duration-300"
              >
                我已阅读并理解
              </Button>
            </div>
          </div>
        </Card>

        {/* 底部信息 */}
        <div className="text-center mt-8">
          <p className="text-sm text-gray-500 dark:text-gray-500">
            © 2024 AngusGM. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}
