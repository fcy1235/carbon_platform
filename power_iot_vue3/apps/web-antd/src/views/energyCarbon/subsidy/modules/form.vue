<script lang="ts" setup>
import { ref, computed } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { message, Button, Input } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';

import { createSubsidy, getSubsidy, updateSubsidy } from '#/api/energyCarbon/subsidy';
import { getUserInfoList } from '#/api/energyCarbon/basicData';

import UserSelectModal from '../../components/user-select-modal.vue';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const editMode = ref(false);
const loading = ref(false);
const submitting = ref(false);

const userSelectModalRef = ref<InstanceType<typeof UserSelectModal>>();



const getTitle = computed(() => {
  if (viewMode.value) return '查看补贴信息';
  if (editMode.value) return '编辑补贴信息';
  return '新增补贴信息';
});

// 处理用户选择回填
function handleUserSelect(user: any) {
  formApi.setValues({
    userInfoId: user.id,
    username: user.username || '',
    idCard: user.idCard || '',
    phone: user.phone || '',
    address: user.address || '',
    reformType: getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, user.reformType) || user.reformType || '',
  });
}

// 打开用户选择弹窗
function handleOpenUserSelect() {
  userSelectModalRef.value?.open();
}

const [Form, formApi] = useVbenForm({
  layout: 'horizontal',
  schema: [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'userInfoId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'subsidyCode',
      label: '补贴编码',
      component: 'Input',
      componentProps: {
        disabled: true,
        placeholder: '系统自动生成',
      },
      dependencies: {
        triggerFields: [''],
        show: () => editMode.value || viewMode.value,
      },
    },
    {
      fieldName: 'username',
      label: '用户名',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请选择用户',
        disabled: true,
      },
    },
    {
      fieldName: 'idCard',
      label: '身份证号',
      component: 'Input',
      componentProps: {
        placeholder: '身份证号',
        disabled: true,
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '联系电话',
        disabled: true,
      },
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Input',
      componentProps: {
        placeholder: '地址',
        disabled: true,
      },
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Input',
      componentProps: {
        placeholder: '改造类别',
        disabled: true,
      },
    },
    {
      fieldName: 'subsidyAmount',
      label: '补贴金额(元)',
      component: 'InputNumber',
      rules: 'required',
      componentProps: {
        placeholder: '请输入补贴金额',
        min: 0,
        precision: 2,
        style: { width: '100%' },
      },
    },

  ],
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  width: 'w-[600px]',
  footer: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = {};
      viewMode.value = false;
      editMode.value = false;
      loading.value = false;
      submitting.value = false;
      return;
    }

    const data = modalApi.getData<any>();
    if (!data) return;

    viewMode.value = !!data.viewMode;
    editMode.value = !data.viewMode && !!data.id;

    if (data.id) {
      loading.value = true;
      try {
        const detail = (await getSubsidy(data.id)) || data;
        formData.value = { ...detail };
        formApi.setValues({
          id: detail.id,
          subsidyCode: detail.subsidyCode || '',
          userInfoId: detail.userInfoId,
          username: detail.username || '',
          idCard: detail.idCard || '',
          phone: detail.phone || '',
          address: detail.address || '',
          reformType: getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, detail.reformType) || detail.reformType || '',
          subsidyAmount: detail.subsidyAmount || 0,
          status: detail.status || '未发放',
        });
      } catch {
        message.error('获取详情失败');
      } finally {
        loading.value = false;
      }
    } else {
      formData.value = {};
      formApi.resetForm();
      // 新增时默认状态为"未发放"
      formApi.setValues({ status: '未发放' });
    }
  },
});

async function handleConfirm() {
  const values = await formApi.getValues();
  if (!values.userInfoId) {
    message.error('请选择用户');
    return;
  }
  if (values.subsidyAmount === undefined || values.subsidyAmount === null || values.subsidyAmount <= 0) {
    message.error('请输入有效的补贴金额');
    return;
  }


  submitting.value = true;
  try {
    const submitData = {
      id: values.id,
      userInfoId: values.userInfoId,
      subsidyAmount: values.subsidyAmount,
      status: values.status,
    };

    if (editMode.value && values.id) {
      await updateSubsidy(submitData);
      message.success('更新成功');
    } else {
      await createSubsidy(submitData);
      message.success('创建成功');
    }
    emit('success');
    await modalApi.close();
  } catch (error: any) {
    const msg = error?.message || (editMode.value ? '更新失败' : '创建失败');
    message.error(msg);
  } finally {
    submitting.value = false;
  }
}

defineExpose({
  open: (data?: any) => {
    modalApi.setData(data || {}).open();
  },
});
</script>

<template>
  <Modal>
    <div v-loading="loading" class="px-4 py-2">
      <Form>
        <template #username="slotProps">
          <Input :value="slotProps.value" disabled placeholder="请选择用户">
            <template #suffix>
              <Button
                type="link"
                size="small"
                :disabled="viewMode"
                @click="handleOpenUserSelect"
              >
                选择
              </Button>
            </template>
          </Input>
        </template>
      </Form>

      <!-- 底部按钮 -->
      <div class="footer-buttons">
        <template v-if="!viewMode">
          <Button type="primary" :loading="submitting" @click="handleConfirm">
            {{ editMode ? '保存' : '创建' }}
          </Button>
        </template>
        <Button @click="modalApi.close()">
          {{ viewMode ? '关闭' : '取消' }}
        </Button>
      </div>
    </div>

    <!-- 用户选择弹窗 -->
    <UserSelectModal ref="userSelectModalRef" :api="getUserInfoList" @confirm="handleUserSelect" />
  </Modal>
</template>

<style scoped>
.footer-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
