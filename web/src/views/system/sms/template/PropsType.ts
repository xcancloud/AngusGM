export interface Template {
  id: string,
  code: string,
  name: string,
  thirdCode: string,
  channelId: string,
  signature: string,
  content: string,
  subject: string,
  language: string,
  verificationCode: boolean,
  verificationCodeValidSecond: string
  showEdit: boolean,
  editValues: {
    name: string;
    thirdCode: string;
    language: string;
    signature: string;
    content: string;
  }
}

export interface Options {
  label: string,
  value: string
}

export const _columns = [
  {
    title: 'templateName',
    dataIndex: 'name',
    key: 'name',
    width: '10%'
  },
  {
    title: 'code',
    dataIndex: 'code',
    key: 'code',
    width: '15%'
  },
  {
    title: 'thridCode',
    dataIndex: 'thirdCode',
    key: 'thirdCode',
    width: '18%'
  },
  {
    title: 'language',
    dataIndex: 'language',
    key: 'language',
    width: '6%'
  },
  {
    title: 'sign',
    dataIndex: 'signature',
    key: 'signature',
    width: '8%'
  },
  {
    title: 'templateContent',
    dataIndex: 'content',
    key: 'content'
  },
  {
    title: 'operation',
    key: 'operate',
    dataIndex: 'operate',
    width: '8%',
    align: 'center'
  }
];
