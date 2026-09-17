<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Upload, message } from 'ant-design-vue';

import { importGasInfo, getImportTemplate } from '#/api/energyCarbon/gasInfo';

const emit = defineEmits(['success']);

const fileList = ref<any[]>([]);
const loading = ref(false);

// 下载模板
async function handleDownloadTemplate() {
  try {
    const blob = (await getImportTemplate()) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '气体信息导入模板.xls';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
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

const [Modal, modalApi] = useVbenModal({
  title: '导入气体信息',
  async onConfirm() {
    if (fileList.value.length === 0) {
      message.warning('请选择要导入的文件');
      return;
    }

    loading.value = true;
    try {
      await importGasInfo(fileList.value[0].originFileObj || fileList.value[0]);
      message.success('导入成功');
      emit('success');
      await modalApi.close();
    } catch {
      message.error('导入失败');
    } finally {
      loading.value = false;
    }
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      fileList.value = [];
      loading.value = false;
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
        导入说明：请下载模板并按规范填写数据，仅支持Excel格式，必填字段不可为空
      </div>

      <div class="download-template">
        <Button
          type="primary"
          @click="handleDownloadTemplate"
          style="float: right"
          >下载模板</Button
        >
      </div>

      <div class="file-select-area">
        <Upload
          class="upload-demo"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          accept=".xlsx, .xls"
          :max-count="1"
        >
          <div class="file-placeholder">
            <p>点击或拖拽文件到此区域</p>
            <p>支持 .xlsx、.xls 格式文件</p>
          </div>
        </Upload>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.import-description {
  padding: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #389e0d;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.download-template {
  margin-bottom: 20px;
  overflow: hidden;
}

.file-select-area {
  padding: 40px;
  margin-bottom: 20px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  transition: all 0.3s;
}

.file-select-area:hover {
  border-color: #1890ff;
}

.file-placeholder {
  color: #999;
}

.file-placeholder p {
  margin: 5px 0;
}
</style>
