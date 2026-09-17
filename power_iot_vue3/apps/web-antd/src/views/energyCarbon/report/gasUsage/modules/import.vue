<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, message, Upload } from 'ant-design-vue';

import {
  getGasUsageImportTemplate,
  importGasUsageReport,
  type GasUsageImportResult,
} from '#/api/energyCarbon/report';

const emit = defineEmits(['success']);

const fileList = ref<any[]>([]);
const result = ref<GasUsageImportResult | null>(null);

// 下载模板
async function handleDownloadTemplate() {
  try {
    const blob = (await getGasUsageImportTemplate()) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '燃气数据报表导入模板.xlsx';
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
  // 重新选择文件后清空上次结果
  result.value = null;
}

// 阻止自动上传
function beforeUpload() {
  return false;
}

const [Modal, modalApi] = useVbenModal({
  title: '导入燃气数据',
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
      const data = await importGasUsageReport(file);
      result.value = data || null;
      // 刷新列表
      emit('success');
      // 只有真正读到行（totalCount > 0）才走"导入成功"；否则把行数（包括 0 行）暴露出来
      if (!data?.failCount && (data?.successCount ?? 0) > 0) {
        message.success(`导入成功，共 ${data.successCount} 行`);
        await modalApi.close();
      } else {
        const total = (data?.successCount ?? 0) + (data?.failCount ?? 0);
        if (total === 0) {
          message.warning('未导入任何数据，请检查模板是否填写正确（数据需从第 2 行开始）');
        } else {
          message.warning(`导入完成：成功 ${data?.successCount ?? 0} 行，失败 ${data?.failCount ?? 0} 行`);
        }
      }
    } catch {
      message.error('导入失败');
    } finally {
      modalApi.unlock();
    }
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      fileList.value = [];
      result.value = null;
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
        格式。「户主姓名」必填；「采暖季」按 2025年 格式填写；表底数/用气量填数字，
        合计用气量为空时会按「终止表底数 - 起始表底数」自动计算。
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

      <!-- 导入结果 -->
      <div v-if="result" class="import-result">
        <div class="result-summary">
          <span class="result-item success">成功：{{ result.successCount ?? 0 }} 行</span>
          <span class="result-item fail">失败：{{ result.failCount ?? 0 }} 行</span>
        </div>
        <div v-if="result.errors && result.errors.length > 0" class="error-list">
          <div class="error-title">错误明细：</div>
          <div
            v-for="(item, index) in result.errors"
            :key="index"
            class="error-item"
          >
            第 {{ item.rowNum }} 行：{{ item.message }}
          </div>
        </div>
      </div>
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

.import-result {
  padding: 12px;
  margin-top: 16px;
  background-color: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.result-summary {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.result-item {
  font-size: 14px;
}

.result-item.success {
  color: #389e0d;
}

.result-item.fail {
  color: #cf1322;
}

.error-list {
  max-height: 180px;
  overflow-y: auto;
}

.error-title {
  margin-bottom: 4px;
  font-size: 13px;
  font-weight: 600;
  color: #666;
}

.error-item {
  padding: 2px 0;
  font-size: 12px;
  color: #cf1322;
}
</style>
