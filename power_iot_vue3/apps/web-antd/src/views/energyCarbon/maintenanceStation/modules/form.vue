<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Input, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createMaintenanceStation,
  updateMaintenanceStation,
} from '#/api/energyCarbon/maintenanceStation';
import { $t } from '#/locales';

import { useFormSchema } from '../data';
import EnterpriseSelectModal from './enterprise-select-modal.vue';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const enterpriseSelectModalRef = ref<InstanceType<typeof EnterpriseSelectModal>>();

const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看维保网点';
  }
  return formData.value?.id ? '编辑维保网点' : '新增维保网点';
});

// 处理维保企业选择回填
function handleEnterpriseSelect(enterprise: any) {
  formApi.setValues({
    enterpriseId: enterprise.id,
    enterpriseName: enterprise.enterpriseName || '',
    serviceType: enterprise.serviceType || undefined,
  });
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 160,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  footer: computed(() => !viewMode.value),
  width: 800,
  centered: true,
  class: 'max-w-[860px]',
  async onConfirm() {
    if (viewMode.value) {
      return;
    }
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    try {
      let data: any = await formApi.getValues();
      const stationId: string = formData.value?.id;

      data = {
        ...data,
        ...(stationId ? { id: stationId } : undefined),
      };

      // enterpriseName 不需要提交到后端
      delete data.enterpriseName;

      await (stationId
        ? updateMaintenanceStation(data)
        : createMaintenanceStation(data));
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

      await formApi.setState({
        commonConfig: {
          componentProps: {
            disabled: viewMode.value,
          },
        },
      });

      await formApi.setValues({
        ...formData.value,
      });
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
    <Form class="mx-4">
      <template #enterpriseName="slotProps">
        <Input
          :value="slotProps.value"
          disabled
          placeholder="请选择所属企业"
        >
          <template #suffix>
            <Button
              type="link"
              size="small"
              :disabled="slotProps.disabled"
              @click="enterpriseSelectModalRef?.open()"
            >
              选择
            </Button>
          </template>
        </Input>
      </template>
    </Form>
  </Modal>
  <EnterpriseSelectModal
    ref="enterpriseSelectModalRef"
    @confirm="handleEnterpriseSelect"
  />
</template>