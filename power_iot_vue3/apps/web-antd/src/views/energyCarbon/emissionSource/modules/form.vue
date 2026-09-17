<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { $t } from '#/locales';
import {
  createEmissionSource,
  updateEmissionSource,
  getEmissionSourceCode,
} from '#/api/energyCarbon/emissionSource';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看排放源';
  }
  return formData.value?.id ? '编辑排放源' : '新增排放源';
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-2',
    labelWidth: 120,
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
      await (originalData?.id
        ? updateEmissionSource(data)
        : createEmissionSource(data));
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
    modalApi.lock();
    try {
      formData.value = data || {};
      viewMode.value = data?.viewMode || false;
      if (!data?.id && !viewMode.value) {
        const codeResult = await getEmissionSourceCode();
        if (codeResult) {
          formData.value.sourceCode = codeResult;
        }
      }

      await formApi.setValues(formData.value);
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
