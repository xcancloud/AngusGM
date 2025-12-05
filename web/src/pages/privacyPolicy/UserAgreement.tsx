import { ArrowLeft, FileText } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { AngusGMLogo } from '@/components/AngusGMLogo';
import { useNavigate } from 'react-router-dom';

interface UserAgreementProps {
  onBack?: () => void;
}

export function UserAgreement({ onBack }: UserAgreementProps) {
  const navigate = useNavigate();
  const handleBack = () => {
    navigate(-1);
  };
  return (
    <div className="w-full min-h-screen h-fit bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-950 dark:via-blue-950 dark:to-gray-950">
      <div className="max-w-4xl mx-auto p-6 lg:p-12">
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
        <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border-0 shadow-2xl">
          <div className="p-8 lg:p-12">
            {/* 标题 */}
            <div className="flex items-center gap-4 mb-8 pb-8 border-b border-gray-200 dark:border-gray-700">
              <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center">
                <FileText className="w-7 h-7 text-white" />
              </div>
              <div>
                <h1 className="text-3xl mb-2 bg-gradient-to-r from-blue-600 to-indigo-600 dark:from-blue-400 dark:to-indigo-400 bg-clip-text text-transparent">
                  AngusGM 用户协议
                </h1>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  最后更新日期：2024年12月4日
                </p>
              </div>
            </div>

            {/* 协议内容 */}
            <div className="prose prose-gray dark:prose-invert max-w-none">
              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">1. 协议接受</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  欢迎使用 AngusGM 全局管理平台（以下简称"本平台"）。本协议是您（以下简称"用户"）与 AngusGM 之间就使用本平台服务所订立的协议。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  在使用本平台服务之前，请您仔细阅读本协议的全部内容。如果您不同意本协议的任何内容，请不要注册或使用本平台服务。您一旦注册、登录、使用本平台，即表示您已充分理解并完全同意本协议的各项内容。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">2. 服务说明</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  AngusGM 是一个企业级全局管理平台，为用户提供租户管理、组织人员、应用服务、用户权限、系统消息等功能。具体服务内容由 AngusGM 根据实际情况提供。
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400">
                  <li>本平台仅向注册用户提供服务</li>
                  <li>用户需要遵守本协议及相关法律法规</li>
                  <li>本平台保留随时修改或中断服务的权利</li>
                </ul>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">3. 用户注册</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  3.1 用户资格：用户应为具备完全民事行为能力的自然人，或依法设立并有效存续的企业法人或其他组织。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  3.2 注册信息：用户在注册时应提供真实、准确、完整的个人信息，并及时更新。如因提供虚假信息造成的一切后果由用户自行承担。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  3.3 账号安全：用户应妥善保管账号及密码，不得将账号转让、出借或授权他人使用。因用户原因导致的账号安全问题，由用户自行承担责任。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">4. 用户行为规范</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  用户在使用本平台服务时，应遵守以下规范：
                </p>
                <ul className="list-disc list-inside space-y-2 text-gray-600 dark:text-gray-400 mb-4">
                  <li>不得发布违法、有害、虚假、侵权等信息</li>
                  <li>不得利用本平台从事任何违法犯罪活动</li>
                  <li>不得干扰或破坏本平台的正常运行</li>
                  <li>不得侵犯他人的合法权益</li>
                  <li>不得进行任何危害网络安全的行为</li>
                </ul>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  如用户违反上述规范，本平台有权采取警告、限制功能、暂停服务、终止服务等措施，并保留追究法律责任的权利。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">5. 知识产权</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  5.1 本平台的所有内容，包括但不限于文字、图片、标识、图标、软件、程序等，均属于 AngusGM 或其关联公司所有，受相关知识产权法律保护。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  5.2 未经 AngusGM 书面许可，用户不得以任何方式使用本平台的知识产权。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">6. 免责声明</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  6.1 本平台按"现状"提供服务，不对服务的及时性、安全性、准确性做任何明示或暗示的保证。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  6.2 因不可抗力、黑客攻击、系统维护等原因导致的服务中断或数据丢失，本平台不承担责任。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  6.3 用户因使用本平台服务而产生的任何直接、间接、偶然、特殊或后果性的损害，本平台不承担赔偿责任。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">7. 协议变更</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  AngusGM 有权随时修改本协议内容，修改后的协议将在本平台公布。如用户继续使用本平台服务，即视为同意修改后的协议。如不同意修改后的协议，用户应立即停止使用本平台服务。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">8. 法律适用与争议解决</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-4">
                  8.1 本协议的订立、执行、解释及争议解决均适用中华人民共和国法律。
                </p>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  8.2 如就本协议内容或其执行发生任何争议，双方应友好协商解决；协商不成时，任何一方均可向 AngusGM 所在地人民法院提起诉讼。
                </p>
              </section>

              <section className="mb-8">
                <h2 className="text-xl mb-4 text-gray-900 dark:text-white">9. 联系方式</h2>
                <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
                  如您对本协议有任何疑问，或需要与我们联系，请通过以下方式：
                </p>
                <ul className="list-none space-y-2 text-gray-600 dark:text-gray-400 mt-4">
                  <li>📧 邮箱: support@angusgm.com</li>
                  <li>📞 电话: 400-888-8888</li>
                  <li>🏢 地址: 北京市朝阳区建国路XX号</li>
                </ul>
              </section>
            </div>

            {/* 底部按钮 */}
            <div className="mt-12 pt-8 border-t border-gray-200 dark:border-gray-700">
              <Button
                onClick={handleBack}
                className="w-full h-12 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40 transition-all duration-300"
              >
                我已阅读并同意
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
