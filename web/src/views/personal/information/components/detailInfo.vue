<script setup lang="ts">
import { computed, ref, inject, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { PureCard, Grid, Icon, Input } from '@xcan-angus/vue-ui';
import { Tag } from 'ant-design-vue';
import { regexp } from '@xcan-angus/tools';
import { user } from '@/api';

const tenantInfo = inject('tenantInfo', ref());

const { t } = useI18n();

const nameInput = ref(false);
const firstName = ref<string>('');
const prevFirstName = ref<string>('');
const lastName = ref<string>('');
const prevLastName = ref<string>('');
const fullName = ref<string>('');

const firstNameError = ref(false);
const lastNameError = ref(false);
const fullNameError = ref(false);

const titleInput = ref(false);
const title = ref<string>();

const landlineInput = ref(false);
const landline = ref<string>();

const addressInput = ref(false);
const address = ref<string>();

const columns = [
  [
    { dataIndex: 'name', label: t('personalCenter.information.name') },
    { dataIndex: 'mobile', label: t('personalCenter.mobile') },
    { dataIndex: 'email', label: t('personalCenter.email') },
    { dataIndex: 'title', label: t('personalCenter.information.title') },
    { dataIndex: 'landline', label: t('personalCenter.information.landline') },
    { dataIndex: 'address', label: t('personalCenter.information.address'), divide: true }
  ],
  [
    { dataIndex: 'createdDate', label: t('personalCenter.information.registerTime') },
    { dataIndex: 'sysAdmin', label: t('personalCenter.information.systemId') },
    { dataIndex: 'group', label: t('personalCenter.information.group') },
    { dataIndex: 'depts', label: t('personalCenter.information.ownDept') },
    { dataIndex: 'tags', label: t('用户标签') },
    { dataIndex: 'otherAccount', label: t('personalCenter.information.otherAccount'), offset: true }
    // {
    //   label: t('所属租户'), dataIndex: 'tenantName'
    // }
  ]
];

const editName = () => {
  firstName.value = tenantInfo.value.firstName;
  prevFirstName.value = firstName.value;
  lastName.value = tenantInfo.value.lastName;
  prevLastName.value = lastName.value;
  fullName.value = tenantInfo.value.fullName;
  nameInput.value = true;
  firstNameError.value = false;
  lastNameError.value = false;
  fullNameError.value = false;
};

const firstNameBlur = () => {
  firstNameError.value = !firstName.value;
  if (fullName.value && prevFirstName.value === firstName.value) {
    return;
  }

  prevFirstName.value = firstName.value;
  nameChange();
};

const lastNameBlur = () => {
  lastNameError.value = !lastName.value;
  if (fullName.value && prevLastName.value === lastName.value) {
    return;
  }

  prevLastName.value = lastName.value;
  nameChange();
};

const fullNameBlur = () => {
  fullNameError.value = !fullName.value;
};

const nameChange = () => {
  if (!firstName.value || !lastName.value) {
    return;
  }

  if (regexp.hasZh(firstName.value || '') || regexp.hasZh(lastName.value || '')) {
    fullName.value = (firstName.value || '') + (lastName.value || '');
    return;
  }

  fullName.value = (lastName.value || '') + (firstName.value || '');
};

const nameSaving = ref(false);
const saveName = async () => {
  if (nameSaving.value) {
    return;
  }

  if (!firstName.value || !lastName.value || !fullName.value) {
    firstNameError.value = !firstName.value;
    lastNameError.value = !lastName.value;
    fullNameError.value = !fullName.value;
    return;
  }

  const params = {
    firstName: firstName.value,
    lastName: lastName.value,
    fullName: fullName.value
  };
  nameSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  nameSaving.value = false;
  if (error) {
    return;
  }

  tenantInfo.value.firstName = params.firstName;
  tenantInfo.value.lastName = params.lastName;
  tenantInfo.value.fullName = params.fullName;
  nameInput.value = false;
};

const cancelNameEdit = () => {
  nameInput.value = false;
  firstName.value = '';
  lastName.value = '';
  fullName.value = '';
};

const editTitle = () => {
  title.value = tenantInfo.value.title;
  titleInput.value = true;
};

const titleSaving = ref(false);
const saveTitle = async () => {
  if (titleSaving.value) {
    return;
  }

  const params = { title: title.value };
  titleSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  titleSaving.value = false;
  if (error) {
    return;
  }

  tenantInfo.value.title = params.title;
  titleInput.value = false;
};

const cancelTitleEdit = () => {
  titleInput.value = false;
  title.value = undefined;
};

const editLandline = () => {
  landline.value = tenantInfo.value.landline;
  landlineInput.value = true;
};

const landlineSaving = ref(false);
const saveLandline = async () => {
  if (landlineSaving.value) {
    return;
  }

  const params = { landline: landline.value };
  landlineSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  landlineSaving.value = false;
  if (error) {
    return;
  }

  tenantInfo.value.landline = params.landline;
  landlineInput.value = false;
};

const cancelLandlineEdit = () => {
  landlineInput.value = false;
  landline.value = undefined;
};

const editAddress = () => {
  address.value = tenantInfo.value.address;
  addressInput.value = true;
};

const addressSaving = ref(false);
const saveAddress = async () => {
  if (addressSaving.value) {
    return;
  }

  const params = { address: address.value };
  addressSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  addressSaving.value = false;
  if (error) {
    return;
  }

  tenantInfo.value.address = params.address;
  addressInput.value = false;
};

const cancelAddressEdit = () => {
  addressInput.value = false;
  address.value = undefined;
};

const depts = computed(() => {
  return tenantInfo.value?.depts || [];
});

const groups = computed(() => {
  return tenantInfo.value?.groups || [];
});

const phone = computed(() => {
  const temp = (+tenantInfo.value?.itc + ' ' + tenantInfo.value?.mobile).replace(/\+/, '');
  return temp ? ('+' + temp) : '';
});

watch(() => tenantInfo.value, (newValue: any) => {
  if (!newValue?.id) {
    return;
  }

  title.value = newValue.title;
  landline.value = newValue.landline;
  firstName.value = newValue.firstName;
  lastName.value = newValue.lastName;
  fullName.value = newValue.fullName;

  firstNameError.value = false;
  lastNameError.value = false;
  fullNameError.value = false;
}, {
  immediate: true
});
</script>
<template>
  <PureCard class="py-7.5 px-25 w-11/12 2xl:px-6 mx-auto">
    <Grid :columns="columns" :dataSource="tenantInfo">
      <template #name>
        <div class="relative flex justify-between">
          <template v-if="!nameInput">
            {{ tenantInfo.fullName }}
            <a class="mr-20 text-theme-special" @click="editName">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="lastName"
                :error="lastNameError"
                placeholder="名称"
                title="名称"
                class="mr-2"
                @blur="lastNameBlur" />
              <Input
                v-model:value="firstName"
                :error="firstNameError"
                placeholder="姓氏"
                title="姓氏"
                class="mr-2"
                @blur="firstNameBlur" />
              <Input
                v-model:value="fullName"
                :error="fullNameError"
                placeholder="全名"
                title="全名"
                @blur="fullNameBlur" />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveName">{{ t('personalCenter.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelNameEdit">{{ t('personalCenter.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #mobile>{{ phone }}</template>
      <template #title>
        <div class="relative flex justify-between">
          <template v-if="!titleInput">
            {{ tenantInfo.title }}
            <a class="mr-20 text-theme-special" @click="editTitle">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="title"
                title="职务"
                placeholder="职务"
                allowClear />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveTitle">{{ t('personalCenter.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelTitleEdit">{{ t('personalCenter.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #landline>
        <div class="relative flex justify-between">
          <template v-if="!landlineInput">
            {{ tenantInfo.landline }}
            <a class="mr-20 text-theme-special" @click="editLandline">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="landline"
                title="座机号"
                placeholder="座机号"
                allowClear
                dataType="number"
                includes="-" />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveLandline">{{ t('personalCenter.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelLandlineEdit">{{ t('personalCenter.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #address>
        <div class="relative flex justify-between">
          <template v-if="!addressInput">
            {{ tenantInfo.address }}
            <a class="mr-20 text-theme-special" @click="editAddress">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="address"
                title="地址"
                placeholder="地址"
                allowClear />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveAddress">{{ t('personalCenter.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelAddressEdit">{{ t('personalCenter.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #group>
        <template v-if="groups?.length">
          <div
            v-for="item in groups"
            :key="item.id"
            class="inline-flex space-x-1 mr-2">
            <div class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
              <Icon class="inline h-full text-theme-content text-4" icon="icon-zu" />
            </div>
            <span>{{ item.name }}</span>
          </div>
        </template>
      </template>
      <template #sysAdmin>
        {{ tenantInfo.sysAdmin ? t('personalCenter.information.systemAdmin') :
          t('personalCenter.information.generalUser') }}
      </template>
      <template #deptHead>
        {{ tenantInfo.deptHead ? t('personalCenter.information.principal') :
          t('personalCenter.information.generalUser') }}
      </template>
      <template #depts>
        <template v-if="depts?.length" >
          <div
            v-for="item in depts"
            :key="item.id"
            class="inline-flex  space-x-1 mr-2">
            <div class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
              <Icon class="inline h-full text-theme-content text-4" icon="icon-bumen" />
            </div>
            <span>{{ item.name }}</span>
          </div>
        </template>
      </template>
      <template #tags>
        <div v-if="tenantInfo.tags">
          <Tag v-for="item in tenantInfo.tags" :key="item.id">{{item.name}}</Tag>
        </div>
      </template>
      <template #otherAccount>
        <div class="flex justify-between">
          <div>
            <Icon
              class="mr-4 text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-wechat': tenantInfo.wechatUserId }"
              icon="icon-weixin" />
            <Icon
              class="mr-4 text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-google': tenantInfo.googleUserId }"
              icon="icon-google" />
            <Icon
              class="text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-github': tenantInfo.githubUserId }"
              icon="icon-Github" />
          </div>
        </div>
      </template>
      <template #tenantName="{text}">
        {{ text }}&nbsp;&nbsp;(ID:&nbsp;{{ tenantInfo?.tenantId }})
      </template>
    </Grid>
  </PureCard>
</template>
<style scoped>
.active-wechat {
  @apply text-green-wechat;
}

.active-google {
  @apply text-blue-google;
}

.active-github {
  @apply text-black-title;
}
</style>
