<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Input, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createGasCoordinator,
  getGasCoordinator,
  updateGasCoordinator,
} from '#/api/energyCarbon/gasCoordinator';
import { getMaintenanceEnterprise } from '#/api/energyCarbon/maintenanceEnterprise';
import { $t } from '#/locales';

import { useFormSchema } from '../data';
import EnterpriseSelectModal from './enterprise-select-modal.vue';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const enterpriseSelectModalRef = ref<InstanceType<typeof EnterpriseSelectModal>>();

const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看协管员';
  }
  return formData.value?.id ? '编辑协管员' : '新增协管员';
});

// 处理维保企业选择回填
function handleEnterpriseSelect(enterprise: any) {
  formApi.setValues({
    enterpriseId: enterprise.id,
    enterpriseName: enterprise.enterpriseName || '',
    unifiedSocialCreditCode: enterprise.unifiedSocialCreditCode || '',
  });
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 180,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  footer: computed(() => !viewMode.value),
  width: 1000,
  centered: true,
  class: 'max-w-[1060px]',
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
      const coordinatorId: string = formData.value?.id;

      // 删除不需要提交到后端的字段（保存时只保存维保企业ID）
      delete data.enterpriseName;
      delete data.unifiedSocialCreditCode;

      // 将 area 从 AreaCascaderItem 的 JSON 格式转为逗号分隔字符串
      if (data.area) {
        try {
          const parsed = JSON.parse(data.area);
          if (parsed.codes && Array.isArray(parsed.codes)) {
            data.area = parsed.codes.join(',');
          }
        } catch {
          // 如果已经是逗号分隔格式则保持不变
        }
      }

      data = {
        ...data,
        ...(coordinatorId ? { id: coordinatorId } : undefined),
      };

      await (coordinatorId
        ? updateGasCoordinator(data)
        : createGasCoordinator(data));
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
      // 查看或编辑时调用/get接口获取详情数据
      if (data.id) {
        const detail = await getGasCoordinator(data.id);
        // 如果缺少统一社会信用编码，通过企业ID补全
        if (detail.enterpriseId && !detail.unifiedSocialCreditCode) {
          try {
            const enterprise = await getMaintenanceEnterprise(detail.enterpriseId);
            detail.unifiedSocialCreditCode = enterprise?.unifiedSocialCreditCode || '';
          } catch {
            // 忽略企业查询失败
          }
        }
        formData.value = { ...detail, viewMode: data.viewMode };
        viewMode.value = data.viewMode || false;
      } else {
        formData.value = data;
        viewMode.value = data.viewMode || false;
      }

      await formApi.setState({
        commonConfig: {
          componentProps: {
            disabled: viewMode.value,
          },
        },
      });

      const formValues = { ...formData.value };
      if (viewMode.value) {
        // 查看模式：area 字段替换为 Input 纯文本
        const viewSchema = useFormSchema().map((item) => {
          if (item.fieldName === 'area') {
            return { ...item, component: 'Input' as const };
          }
          return item;
        });
        await formApi.setState({ schema: viewSchema });
        // 直接用 areaName 显示纯文本
        if (formValues.areaName) {
          formValues.area = formValues.areaName;
        }
      } else {
        // 编辑模式：恢复 AreaCascaderItem schema
        await formApi.setState({ schema: useFormSchema() });
        // 将 area 数据转为 AreaCascaderItem 需要的 JSON 格式
        if (formValues.area && formValues.areaName) {
          const codes = formValues.area.split(',').map(Number);
          formValues.area = JSON.stringify({ codes, label: formValues.areaName.replace(/\//g, '') });
        } else if (formValues.areaName) {
          formValues.area = JSON.stringify({ codes: [], label: formValues.areaName.replace(/\//g, '') });
        }
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
    <Form class="mx-4">
      <template #enterpriseName="slotProps">
        <Input
          :value="slotProps.value"
          disabled
          placeholder="请选择企业"
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
