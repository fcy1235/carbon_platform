<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { $t } from '#/locales';
import { createGasInfo, updateGasInfo, getGasInfoCode } from '#/api/energyCarbon/gasInfo';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看气体信息';
  }
  return formData.value?.id ? '编辑气体信息' : '新增气体信息';
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-2',
    labelWidth: 150,
  },
  layout: 'horizontal',
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
    const data: any = await formApi.getValues();
    const originalData: any = formData.value;

    try {
      await (originalData?.id ? updateGasInfo(data) : createGasInfo(data));
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
      if (!data?.id && !viewMode.value) {
        const codeResult = await getGasInfoCode();
        if (codeResult) {
          formData.value.gasCode = codeResult;
        }
      }
      // 确保 category 转为数字类型，以匹配 Select 组件的选项值
      const formValues = { ...formData.value };
      if (formValues.category !== undefined && formValues.category !== null) {
        formValues.category = Number(formValues.category);
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
