<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, message, Upload } from 'ant-design-vue';

const props = defineProps<{
  title?: string;
  onSubmit?: (file: File) => Promise<void>;
}>();

const emit = defineEmits<{
  success: [];
  downloadTemplate: [];
}>();

const fileList = ref<any[]>([]);

function handleDownloadTemplate() {
  emit('downloadTemplate');
}

function handleFileChange(info: any) {
  fileList.value = [info.file];
}

function beforeUpload() {
  return false;
}

const [Modal, modalApi] = useVbenModal({
  title: props.title || '导入数据',
  async onConfirm() {
    if (fileList.value.length === 0) {
      message.error('请选择文件');
      return;
    }
    const file = fileList.value[0].originFileObj || fileList.value[0];
    if (!file) {
      message.error('文件读取失败');
      return;
    }
    modalApi.lock();
    try {
      if (props.onSubmit) {
        await props.onSubmit(file as File);
      }
      message.success('导入成功');
      emit('success');
      await modalApi.close();
    } finally {
      modalApi.unlock();
    }
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      fileList.value = [];
    }
  },
});

defineExpose({
  open: () => {
    modalApi.open();
  },
});
</script>

<template>
  <Modal>
    <div>
      <div class="import-description">
        导入说明：请下载模板并按规范填写数据，仅支持 Excel 格式。
        身份证号、电表号与采暖季必须填写（身份证号与电表号用于关联用户基本信息）；合计用电量未填写时将自动按“终止表底数 - 起始表底数”计算。
      </div>

      <div class="mb-4 text-right">
        <Button type="link" @click="handleDownloadTemplate">
          下载导入模板
        </Button>
      </div>

      <Upload
        v-model:file-list="fileList"
        :auto-upload="false"
        :before-upload="beforeUpload"
        :max-count="1"
        accept=".xls,.xlsx"
        @change="handleFileChange"
      >
        <div class="upload-drag-area">
          <p class="upload-drag-text">单击或拖拽文件到此区域上传</p>
          <p class="upload-drag-hint">仅支持 .xls / .xlsx 格式</p>
        </div>
      </Upload>
    </div>
  </Modal>
</template>

<style scoped>
.import-description {
  padding: 10px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #389e0d;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.upload-drag-area {
  padding: 24px;
  text-align: center;
  cursor: pointer;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  transition: border-color 0.3s;
}

.upload-drag-area:hover {
  border-color: #1890ff;
}

.upload-drag-text {
  margin-bottom: 4px;
  font-size: 14px;
  color: #666;
}

.upload-drag-hint {
  font-size: 12px;
  color: #999;
}
</style>
