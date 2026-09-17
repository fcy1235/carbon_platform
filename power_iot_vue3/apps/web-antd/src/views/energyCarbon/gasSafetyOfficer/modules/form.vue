<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import { Button, Image, Input, message, Upload, type UploadFile, type UploadProps } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createGasSafetyOfficer,
  getGasSafetyOfficer,
  updateGasSafetyOfficer,
} from '#/api/energyCarbon/gasSafetyOfficer';
import { getMaintenanceEnterprise } from '#/api/energyCarbon/maintenanceEnterprise';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { $t } from '#/locales';

import { useFormSchema } from '../data';
import EnterpriseSelectModal from './enterprise-select-modal.vue';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const enterpriseSelectModalRef = ref<InstanceType<typeof EnterpriseSelectModal>>();

// 身份证图片：保存原始 File 对象，提交时作为 MultipartFile 发送
const idCardFrontFile = ref<File | undefined>();
// 编辑时回显已有的图片 URL
const frontFileList = ref<UploadFile[]>([]);

const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看燃气安全员';
  }
  return formData.value?.id ? '编辑燃气安全员' : '新增燃气安全员';
});

// 处理维保企业选择回填
function handleEnterpriseSelect(enterprise: any) {
  formApi.setValues({
    enterpriseId: enterprise.id,
    enterpriseName: enterprise.enterpriseName || '',
    unifiedSocialCreditCode: enterprise.unifiedSocialCreditCode || '',
  });
}

// 将文件转为 base64 用于本地预览
function getBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', () => resolve(reader.result as string));
    reader.addEventListener('error', reject);
  });
}

// 身份证正面：文件变化
function handleFrontChange(info: { file: UploadFile; fileList: UploadFile[] }) {
  frontFileList.value = info.fileList.slice(-1);
  if (info.file.status === 'removed') {
    idCardFrontFile.value = undefined;
  }
}

// 身份证正面自定义上传：暂存 File 对象，生成预览后标记成功
const frontCustomRequest: UploadProps['customRequest'] = async (options) => {
  const { file, onSuccess } = options;
  idCardFrontFile.value = file as File;
  // 生成本地预览
  const preview = await getBase64(file as File);
  // 更新 fileList 中的预览图和状态
  const item = frontFileList.value.find((f) => f.uid === (file as UploadFile).uid);
  if (item) {
    item.status = 'done';
    item.url = preview;
  }
  onSuccess?.('ok');
};

// 预览身份证图片
const previewOpen = ref(false);
const previewImage = ref('');
function handlePreview(file: UploadFile) {
  previewImage.value = file.url || file.preview || '';
  previewOpen.value = true;
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
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
      const officerId: string = formData.value?.id;

      // 删除不需要提交到后端的字段
      delete data.enterpriseName;
      delete data.unifiedSocialCreditCode;
      // 图片字段不作为 JSON 字段提交，而是作为 MultipartFile 单独提交
      delete data.idCardFrontImage;
      delete data.idCardBackImage;

      data = {
        ...data,
        ...(officerId ? { id: officerId } : undefined),
      };

      await (officerId
        ? updateGasSafetyOfficer(data, idCardFrontFile.value)
        : createGasSafetyOfficer(data, idCardFrontFile.value));
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
      idCardFrontFile.value = undefined;
      frontFileList.value = [];
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
        const detail = await getGasSafetyOfficer(data.id);
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

      // 回显身份证图片
      frontFileList.value = formData.value.idCardFrontImage
        ? [{
            uid: '-1',
            name: '照片',
            status: 'done',
            url: formData.value.idCardFrontImage,
          }]
        : [];

      // 查看模式下，区域字段替换为 Input 纯文本；编辑模式恢复 AreaCascaderItem
      if (viewMode.value) {
        const viewSchema = useFormSchema().map((item) => {
          if (item.fieldName?.startsWith('area')) {
            return { ...item, component: 'Input' as const };
          }
          return item;
        });
        await formApi.setState({ schema: viewSchema });
        // 从 area JSON 中提取 label 纯文本
        const formValues = { ...formData.value };
        for (let i = 1; i <= 5; i++) {
          const key = `area${i}`;
          if (formValues[key]) {
            try {
              const parsed = JSON.parse(formValues[key]);
              formValues[key] = parsed.label || '';
            } catch {
              // 已经是纯文本则保持
            }
          }
        }
        await formApi.setValues(formValues);
      } else {
        // 恢复原始 schema（含 AreaCascaderItem）
        await formApi.setState({ schema: useFormSchema() });
        await formApi.setValues({ ...formData.value });
      }
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
    <!-- 照片上传（不在表单schema中，单独渲染） -->
    <div class="mx-4 mb-2">
      <div class="photo-upload-row">
        <div class="photo-label">照片</div>
        <Upload
          v-model:file-list="frontFileList"
          :custom-request="frontCustomRequest"
          list-type="picture-card"
          accept="image/*"
          :max-count="1"
          :disabled="viewMode"
          @change="handleFrontChange"
          @preview="handlePreview"
        >
          <div v-if="frontFileList.length === 0" class="flex flex-col items-center">
            <IconifyIcon icon="lucide:plus" />
            <div class="mt-1 text-xs">上传</div>
          </div>
        </Upload>
      </div>
    </div>

    <Form class="mx-4">
      <template #enterpriseName="slotProps">
        <Input
          :value="slotProps.value"
          disabled
          placeholder="请选择所属燃气企业"
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

  <!-- 图片预览 -->
  <Image
    :preview="{
      visible: previewOpen,
      onVisibleChange: (vis: boolean) => (previewOpen = vis),
    }"
    :src="previewImage"
    style="display: none"
  />

  <EnterpriseSelectModal
    ref="enterpriseSelectModalRef"
    @confirm="handleEnterpriseSelect"
  />
</template>

<style scoped>
.photo-upload-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.photo-label {
  width: 140px;
  text-align: right;
  padding-right: 8px;
  font-size: 14px;
  line-height: 32px;
  flex-shrink: 0;
}
</style>
