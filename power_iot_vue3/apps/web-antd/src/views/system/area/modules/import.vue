<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Upload, Progress, message } from 'ant-design-vue';

const emit = defineEmits(['success']);

const importProgress = ref(0);
const importProgressText = ref('');
const fileList = ref<any[]>([]);

// 下载模板
function handleDownloadTemplate() {
  message.success('模板下载中...');
}

// 文件变化
function handleFileChange(info: any) {
  fileList.value = [info.file];
}

const [Modal, modalApi] = useVbenModal({
  title: '导入行政区划',
  async onConfirm() {
    if (fileList.value.length === 0) {
      message.error('请选择文件');
      return;
    }
    
    importProgress.value = 0;
    importProgressText.value = '上传中...';
    
    const interval = setInterval(() => {
      importProgress.value += 10;
      if (importProgress.value >= 100) {
        clearInterval(interval);
        importProgressText.value = '导入完成';
        setTimeout(async () => {
          await modalApi.close();
          emit('success');
          message.success('导入成功');
        }, 500);
      }
    }, 300);
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      importProgress.value = 0;
      importProgressText.value = '';
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
        导入说明：请下载模板并按规范填写数据，仅支持Excel格式，必填字段不可为空
      </div>
      
      <div class="download-template">
        <Button type="primary" @click="handleDownloadTemplate" style="float: right;">下载模板</Button>
      </div>
      
      <div class="file-select-area">
        <Upload
          class="upload-demo"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          accept=".xlsx, .xls"
        >
          <div class="file-placeholder">
            <p>行政区划数据 (xlsx)</p>
            <p>支持Excel格式文件</p>
          </div>
        </Upload>
      </div>
      
      <div v-if="importProgress > 0" class="import-progress">
        <Progress :percent="importProgress" />
        <span class="progress-text">{{ importProgressText }}</span>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.import-description {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #389e0d;
}

.download-template {
  margin-bottom: 20px;
  overflow: hidden;
}

.file-select-area {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 40px;
  text-align: center;
  margin-bottom: 20px;
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

.import-progress {
  margin-bottom: 20px;
}

.progress-text {
  display: block;
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}
</style>
