<script lang="ts" setup>
import type { VbenFormSchema } from '#/adapter/form';
import type { SystemAreaApi } from '#/api/system/area';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createArea, updateArea } from '#/api/system/area';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<SystemAreaApi.Area>();

// 使用 ref 存储 schema，以便动态修改
const formSchema = ref<VbenFormSchema[]>(useFormSchema(false));

const getTitle = computed(() => {
  if (!formData.value) {
    return $t('ui.actionTitle.create', ['行政区划']);
  }
  return $t('ui.actionTitle.edit', ['行政区划']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
    labelWidth: 130,
  },
  layout: 'horizontal',
  schema: formSchema.value,
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    try {
      // 获取表单数据
      const formValues = await formApi.getValues();

      // 判断是新增还是编辑
      if (formData.value?.id) {
        // 编辑操作
        await updateArea({ ...formValues, id: formData.value.id, level: 4 });
        message.success($t('ui.actionMessage.updateSuccess', ['行政区划']));
      } else {
        // 新增操作
        const params = {
          ...formValues,
          id: Number(formValues.parentId + formValues.id),
          level: 4,
        };
        await createArea(params);
        message.success($t('ui.actionMessage.createSuccess', ['行政区划']));
      }

      emit('success');
      await modalApi.close();
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    // 加载数据
    const data = modalApi.getData<
      SystemAreaApi.Area & {
        parentId?: number;
        parentName?: string;
      }
    >();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      // 如果是新增，回填上级行政区划信息，行政区划编码可编辑
      if (!data.id && data.parentId !== undefined) {
        // 新增时行政区划编码可编辑
        const idField = formSchema.value.find(
          (item) => item.fieldName === 'id',
        );
        if (idField && idField.componentProps) {
          idField.componentProps.disabled = false;
        }
        await formApi.setValues({
          parentId: data.parentId,
          parentName: data.parentName || '',
        });
      } else if (data.id) {
        // 如果是编辑，设置完整数据，行政区划编码禁用
        formData.value = data;
        // 编辑时行政区划编码禁用
        const idField = formSchema.value.find(
          (item) => item.fieldName === 'id',
        );
        if (idField && idField.componentProps) {
          idField.componentProps.disabled = true;
        }
        await formApi.setValues(formData.value);
      }
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle" class="w-1/3">
    <Form class="mx-4" />
  </Modal>
</template>
