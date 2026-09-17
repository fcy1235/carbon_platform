<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createBaselineData, updateBaselineData } from '#/api/energyCarbon/baseline';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看区域基准线碳排放强度';
  }
  return formData.value?.id ? '编辑区域基准线碳排放强度' : '新增区域基准线碳排放强度';
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-2',
    labelWidth: 140,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  footer: computed(() => !viewMode.value),
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    let data: any = await formApi.getValues();
    const originalData: any = formData.value;
    const baselineId: string = originalData?.id;
    data = {
      ...data,
      ...(baselineId ? { id: baselineId } : undefined),
      provinceCode: data?.division?.[0],
      cityCode: data?.division?.[1],
      districtCode: data?.division?.[2],
    };
    delete data.division;

    try {
      await (baselineId ? updateBaselineData(data) : createBaselineData(data));
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
    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = data;
      viewMode.value = data.viewMode || false;

      // 将分开的行政区划代码转换为数组格式
      const formValues = { ...data };
      if (data.provinceCode || data.cityCode || data.districtCode) {
        formValues.division = [data.provinceCode, data.cityCode, data.districtCode].filter(Boolean);
      }

      await formApi.setValues(formValues);
    } finally {
      modalApi.unlock();
    }
  },
});

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
