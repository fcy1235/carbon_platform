<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, message, Upload } from 'ant-design-vue';

import {
  getElectricityImportTemplate,
  importElectricity,
} from '#/api/energyCarbon/electricPower';

const emit = defineEmits(['success']);

const fileList = ref<any[]>([]);

// 下载模板
async function handleDownloadTemplate() {
  try {
    const blob = (await getElectricityImportTemplate()) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '电力数据导入模板.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('模板下载成功');
  } catch {
    message.error('模板下载失败');
  }
}

// 文件变化
function handleFileChange(info: any) {
  fileList.value = [info.file];
}

// 阻止自动上传
function beforeUpload() {
  return false;
}

const [Modal, modalApi] = useVbenModal({
  title: '导入电力数据',
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
      await importElectricity(file);
      message.success('导入成功');
      emit('success');
      await modalApi.close();
    } catch {
      message.error('导入失败');
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

// 暴露方法
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
        导入说明：请下载模板并按规范填写数据，仅支持 Excel
        格式，必填字段不可为空。
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
