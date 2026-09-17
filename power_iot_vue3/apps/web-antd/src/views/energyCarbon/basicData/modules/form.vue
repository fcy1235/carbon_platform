<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

import { useVbenForm } from '#/adapter/form';
import { addUser, updateUserInfo } from '#/api/energyCarbon/basicData';
import { $t } from '#/locales';

import '../../style.css';
import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看基本信息';
  }
  return formData.value?.id ? '修改基本信息' : '新增基本信息';
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 110,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  width: 'w-[1000px]',
  footer: computed(() => !viewMode.value),
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    // 提交表单
    let data: any = await formApi.getValues();
    const originalData: any = formData.value;
    const userId: string = originalData?.id;

    // 处理5级行政区划
    const division = data?.division || [];
    data = {
      ...data,
      provinceCode: userId ? originalData?.provinceCode : division[0],
      cityCode: userId ? originalData?.cityCode : division[1],
      districtCode: userId ? originalData?.districtCode : division[2],
      townCode: userId ? originalData?.townCode : division[3],
      villageCode: userId ? originalData?.villageCode : division[4],
    };
    delete data.division;

    // 改造年份格式化为字符串
    if (data.reformYear) {
      data.reformYear = dayjs(data.reformYear).format('YYYY');
    }

    // 非气代煤时清空燃气用户编码和燃气表ID
    if (data.reformType !== '2') {
      data.gasUserCode = undefined;
      data.gasId = undefined;
    }
    // 非电代煤时清空电表ID
    if (data.reformType !== '1') {
      data.electricityId = undefined;
    }

    try {
      // 调用真实 API
      await (originalData?.id ? updateUserInfo(data) : addUser(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      viewMode.value = false;
      return;
    }
    // 加载数据
    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = data;
      viewMode.value = data.viewMode || false;

      // 处理5级行政区划回显
      const division: any[] = [];
      if (data.provinceCode) division[0] = data.provinceCode;
      if (data.cityCode) division[1] = data.cityCode;
      if (data.districtCode) division[2] = data.districtCode;
      if (data.townCode) division[3] = data.townCode;
      if (data.villageCode) division[4] = data.villageCode;

      const setData: any = {
        ...data,
        division: division.length > 0 ? division : undefined,
      };

      // 改造年份转为 DatePicker 可识别的对象
      if (setData.reformYear && typeof setData.reformYear === 'string') {
        setData.reformYear = dayjs(setData.reformYear, 'YYYY');
      }

      // 新增时默认使用状态为正常
      if (!data.id && !viewMode.value) {
        setData.useStatus = '1';
      }

      // 设置到 values
      await formApi.setValues(setData);
    } finally {
      modalApi.unlock();
    }
  },
});

// 暴露方法
defineExpose({
  open: (data?: any) => {
    modalApi.setData(data).open();
  },
});
</script>

<template>
  <Modal>
    <Form class="mx-4" />
  </Modal>
</template>
