<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Form, FormItem } from 'ant-design-vue';
import { Card, Hints, Input, notification } from '@xcan-angus/vue-ui';
import { regexpUtils } from '@xcan-angus/infra';

import UploadImage from './uploadImage.vue';
import AuditStatus from '@/components/AuditStatus/index.vue';
import ButtonGroup from './buttonGroup.vue';

import { tenant } from '@/api';

interface EnterpriseCertDataInfo {
  businessLicensePicUrl: string,
  name: string,
  creditCode: string
}

interface LegalCertDataInfo {
  certBackPicUrl: string,
  certFrontPicUrl: string,
  certNo: string,
  name: string
}

interface OrgCertDataInfo {
  name: string,
  orgCode: string,
  orgCertPicUrl: string
}

interface UserCertDataInfo {
  certBackPicUrl: string,
  certFrontPicUrl: string,
  certNo: string,
  name: string
}

interface NewForm {
  enterpriseCert?: EnterpriseCertDataInfo,
  enterpriseLegalPersonCert?: LegalCertDataInfo,
  governmentCert?: OrgCertDataInfo,
  type: string,
  personalCert?: UserCertDataInfo
}

interface FormState {
  id: string,
  autoAudit: boolean,
  enterpriseCert: EnterpriseCertDataInfo,
  enterpriseLegalPersonCert: LegalCertDataInfo,
  governmentCert: OrgCertDataInfo,
  type: string,
  personalCert: UserCertDataInfo
}

interface Props {
  type: string
}

// const router = useRouter();
const route = useRoute();
const query = route.query;

// const emit = defineEmits(['confirm']);
const emit = defineEmits<{(e: 'cancel'): void, (e: 'confrimed'): void }>();

const props = withDefaults(defineProps<Props>(), {
  type: ''
});

const { t } = useI18n();

const confimLoading = ref(false);

const formRef = ref();

// 审核中提示
const unding = ref(false);

// 上传组件的错误状态
const errorUpload = ref<number[]>([]);

/* PERSONAL ENTERPRISE GOVERNMENT  */

const auth = 'systemAuth.';

// 真实姓名
const form = ref<FormState>({
  id: String(query.q),
  autoAudit: false,
  // 企业
  enterpriseCert: {
    businessLicensePicUrl: '',
    name: '',
    creditCode: ''
  },
  // 企业
  enterpriseLegalPersonCert: {
    certBackPicUrl: '',
    certFrontPicUrl: '',
    certNo: '',
    name: ''
  },
  // 单位
  governmentCert: {
    name: '',
    orgCode: '',
    orgCertPicUrl: ''
  },
  type: props.type,
  // 个人
  personalCert: {
    certBackPicUrl: '',
    certFrontPicUrl: '',
    certNo: '',
    name: ''
  }
});

const validateId = () => {
  if (props.type === 'PERSONAL') {
    if (!form.value.personalCert.certNo.trim()) {
      return Promise.reject(new Error(t('idCardRequiredTip')));
    }
    if (!regexpUtils.isId(form.value.personalCert.certNo)) {
      return Promise.reject(new Error('请输入正确的身份证号'));
    }
    return Promise.resolve();
  }
  if (props.type === 'ENTERPRISE') {
    if (!form.value.enterpriseLegalPersonCert.certNo.trim()) {
      return Promise.reject(new Error(t('legalIdCardRequiredTip')));
    }
    if (!regexpUtils.isId(form.value.enterpriseLegalPersonCert.certNo)) {
      return Promise.reject(new Error('请输入正确的身份证号'));
    }
    return Promise.resolve();
  }
  return Promise.resolve();
};

const rules = {
  // 企业
  enterpriseCert: {
    businessLicensePicUrl: [{
      required: true,
      message: t('businessLcene'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 3)
    }],
    name: [{ required: true, messge: t('companyNameRequiredTip'), trigger: 'change' }],
    creditCode: [{ required: true, message: t('businessNumberRequiredTip'), trigger: 'change' }]
  },
  // 企业
  enterpriseLegalPersonCert: {
    certFrontPicUrl: [{
      required: true,
      message: t('请上传法人身份证'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 1)
    }],
    certNo: [{ required: true, validator: validateId, trigger: 'blur' }],
    name: [{ required: true, message: t('输入法人姓名'), trigger: 'change' }]
  },
  // 单位
  governmentCert: {
    name: [{ required: true, message: t('输入组织名称'), trigger: 'change' }],
    orgCode: [{ required: true, message: t('orgNumberRequiredTip'), trigger: 'change' }],
    orgCertPicUrl: [{
      required: true,
      message: t('orgCard'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 3)
    }]
  },
  // 个人
  personalCert: {
    certFrontPicUrl: [{
      required: true,
      message: t('请上传身份证明'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 1)
    }],
    certNo: [{ required: true, validator: validateId, trigger: 'blur' }],
    name: [{ required: true, message: t('输入真实姓名'), trigger: 'change' }]
  }
};

const deleteCode = function (code: number) {
  errorUpload.value = errorUpload.value.filter(item => item !== code);
};

async function errorRules (rules: { message: string }, errCode: number) {
  // 获取延时
  await new Promise<void>((resolve) => {
    setTimeout(() => {
      resolve();
    }, 1000);
  });
  if (errCode === 3) {
    const rules3 = form.value.enterpriseCert.businessLicensePicUrl === '' && form.value.governmentCert.orgCertPicUrl === '';
    if (rules3) {
      errorUpload.value.push(errCode);
      return Promise.reject(new Error(rules.message));
    }
    deleteCode(errCode);
    return Promise.resolve();
  } else {
    const rules1 = form.value.personalCert.certFrontPicUrl === '' && form.value.enterpriseLegalPersonCert.certFrontPicUrl === '';
    const rules2 = form.value.personalCert.certBackPicUrl === '' && form.value.enterpriseLegalPersonCert.certBackPicUrl === '';
    if (rules1 && rules2) {
      errorUpload.value.push(1);
      errorUpload.value.push(2);
      return Promise.reject(new Error(rules.message));
    } else if (rules1) {
      errorUpload.value.push(1);
      deleteCode(2);
      return Promise.reject(new Error(rules.message));
    } else if (rules2) {
      errorUpload.value.push(2);
      deleteCode(1);
      return Promise.reject(new Error(rules.message));
    } else {
      deleteCode(1);
      deleteCode(2);
      return Promise.resolve();
    }
  }
}

// 取消事件
const cancel = function () {
  emit('cancel');
};

// 确认事件
const confirm = function () {
  confimLoading.value = true;
  formRef.value.validate()
    .then(async () => {
      const newForm: NewForm = {
        type: form.value.type
      };
      /* PERSONAL ENTERPRISE GOVERNMENT  */
      if (props.type === 'PERSONAL') {
        newForm.personalCert = form.value.personalCert;
      } else if (props.type === 'ENTERPRISE') {
        newForm.enterpriseCert = form.value.enterpriseCert;
        newForm.enterpriseLegalPersonCert = form.value.enterpriseLegalPersonCert;
      } else {
        newForm.governmentCert = form.value.governmentCert;
      }
      const [error] = await tenant.submitCertAudit(newForm);
      confimLoading.value = false;

      if (error) {
        return;
      }

      notification.success(t('successSubmit'));
      // if (query.l !== 'logined') {
      //   router.push('/system/auth');
      // } else {
      //   unding.value = true;
      // }
      emit('confrimed');
    }).catch(() => {
      confimLoading.value = false;
    });
};

// 文件上传后回调
const loadChange = function (label: string, value: any) {
  /* 接收图片的id或地址 */
  /* PERSONAL ENTERPRISE GOVERNMENT  */
  switch (label) {
    case '0':
      /* 身份证正面 */
      if (props.type === 'PERSONAL') {
        form.value.personalCert.certFrontPicUrl = value;
      } else {
        form.value.enterpriseLegalPersonCert.certFrontPicUrl = value;
      }
      break;
    case '1':
      /* 身份证反面 */
      if (props.type === 'PERSONAL') {
        form.value.personalCert.certBackPicUrl = value;
      } else {
        form.value.enterpriseLegalPersonCert.certBackPicUrl = value;
      }
      break;
    default:
      /* 其他证书 */
      if (props.type === 'ENTERPRISE') {
        form.value.enterpriseCert.businessLicensePicUrl = value;
      } else {
        form.value.governmentCert.orgCertPicUrl = value;
      }
      break;
  }
};

const trimValue = (type, key) => {
  form.value[type][key] = form.value[type][key].trim();
};

</script>
<template>
  <Card v-if="unding" class="mx-auto overflow-hidden">
    <AuditStatus backUrl="/" />
  </Card>
  <Card v-else class="flex-1">
    <div class="w-139 mx-auto mt-10">
      <Form
        ref="formRef"
        :model="form"
        :rules="rules"
        :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 17 }">
        <template v-if="props.type === 'PERSONAL'">
          <FormItem
            :label="t('realName')"
            :name="['personalCert','name']"
            :colon="false">
            <Input
              v-model:value="form.personalCert.name"
              :maxlength="80"
              :placeholder="t('输入真实姓名')"
              @blur="trimValue('personalCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('idCardLabel')"
            :name="['personalCert','certNo']"
            :colon="false">
            <Input
              v-model:value="form.personalCert.certNo"
              :maxlenth="50"
              :placeholder="t('idCardRequiredTip')"
              @blur="trimValue('personalCert', 'certNo')" />
          </FormItem>
          <FormItem
            :label="t('userCertFrontPicUrlLabel')"
            :name="['enterpriseLegalPersonCert','certFrontPicUrl']"
            :colon="false">
            <UploadImage
              type="1"
              :mess="t('idCardTip1')"
              class="mr-8 inline-block"
              :error="errorUpload.includes(1)"
              @change="(value)=> loadChange('0', value)" />
            <UploadImage
              type="1"
              class="inline-block"
              :mess="t('idCardTip2')"
              :error="errorUpload.includes(2)"
              @change="(value)=> loadChange('1', value)" />
          </FormItem>
        </template>
        <template v-else-if="props.type === 'ENTERPRISE'">
          <FormItem
            :label="t('enterpriseLabel')"
            :name="['enterpriseCert','name']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseCert.name"
              :maxlength="30"
              :placeholder="t('companyNameRequiredTip')"
              @blur="trimValue('enterpriseCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('enterpriseCreditCode')"
            :name="['enterpriseCert','creditCode']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseCert.creditCode"
              :maxlength="50"
              :placeholder="t('businessNumberRequiredTip')" />
          </FormItem>
          <FormItem
            :label="t('legalName')"
            :name="['enterpriseLegalPersonCert','name']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseLegalPersonCert.name"
              :placeholder="t('输入法人姓名')"
              :maxlenth="50"
              @blur="trimValue('enterpriseLegalPersonCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('legalIdCard')"
            :name="['enterpriseLegalPersonCert','certNo']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseLegalPersonCert.certNo"
              :placeholder="t('legalIdCardRequiredTip')"
              :maxlenth="50"
              @blur="trimValue('enterpriseLegalPersonCert', 'certNo')" />
          </FormItem>
          <FormItem
            :label="t('legalCertLabel')"
            :name="['enterpriseLegalPersonCert','certFrontPicUrl']"
            :colon="false">
            <div class="flex">
              <UploadImage
                type="1"
                :mess="t('legalIdCard1')"
                class="mr-5"
                :error="errorUpload.includes(1)"
                @change="(value)=> loadChange('0', value)" />
              <UploadImage
                type="1"
                :mess="t('legalIdCard1')"
                :error="errorUpload.includes(2)"
                @change="(value)=> loadChange('1', value)" />
            </div>
          </FormItem>
          <FormItem
            :label="t('businessLabel')"
            :name="['enterpriseCert','businessLicensePicUrl']"
            :colon="false">
            <UploadImage
              type="2"
              :mess="t(auth + 'cate-id-1')"
              :error="errorUpload.includes(3)"
              class="mr-5"
              @change="(value)=> loadChange('2', value)" />
          </FormItem>
        </template>
        <template v-else>
          <FormItem
            :label="t('nameOfAssociation')"
            :maxlength="30"
            :name="['governmentCert','name']"
            :colon="false">
            <Input
              v-model:value="form.governmentCert.name"
              :placeholder="t('输入组织名称')"
              :maxlenth="80"
              @blur="trimValue('governmentCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('orgName')"
            :name="['governmentCert','orgCode']"
            :colon="false">
            <Input
              v-model:value="form.governmentCert.orgCode"
              :maxlength="50"
              :placeholder="t('orgNumberRequiredTip')"
              @blur="trimValue('governmentCert', 'orgCode')" />
          </FormItem>
          <FormItem
            :label="t('govermentCard')"
            :name="['governmentCert','orgCertPicUrl']"
            :colon="false">
            <UploadImage
              type="2"
              :mess="t('idCardTip1')"
              class="mr-5"
              @change="(value)=> loadChange('2', value)" />
          </FormItem>
        </template>
      </Form>
      <!-- 横线 -->
      <div class="w-250 h-0.25 bg-gray-border -ml-60 mt-9"></div>
      <!-- 提示信息 -->
      <Hints :text="t('注册账号默认为系统管理员')" class="ml-40 mt-10" />
    </div>
    <ButtonGroup
      class="my-7.5"
      :confimLoading="confimLoading"
      @cancel="cancel"
      @confirm="confirm" />
  </Card>
</template>
