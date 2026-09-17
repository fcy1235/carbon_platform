<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, Progress, Radio } from 'ant-design-vue';

import { exportProject } from '#/api/energyCarbon/project';

const emit = defineEmits(['success']);

// 接收参数
interface ExportData {
  selectedIds?: number[];
  queryParams?: Record<string, any>;
}

const exportType = ref('all');
const exportProgress = ref(0);
const exportProgressText = ref('');
const exportData = ref<ExportData>({});

// 计算选中数量
const selectedCount = computed(() => exportData.value.selectedIds?.length || 0);
// 是否有选中行
const hasSelected = computed(() => selectedCount.value > 0);

const [Modal, modalApi] = useVbenModal({
  title: '导出项目数据',
  async onConfirm() {
    // 如果选择导出选中但没有选中行
    if (exportType.value === 'selected' && !hasSelected.value) {
      message.warning('请先选择要导出的数据行');
      return;
    }

    exportProgress.value = 0;
    exportProgressText.value = '导出中...';

    modalApi.lock();
    try {
      // 构建导出参数
      const params: Record<string, any> = {};

      if (exportType.value === 'selected') {
        // 选择导出：传递选中的ID列表
        params.ids = exportData.value.selectedIds;
      } else if (exportType.value === 'filtered') {
        // 根据查询条件导出：传递当前查询参数
        const queryParams = exportData.value.queryParams || {};
        if (queryParams.projectName) params.projectName = queryParams.projectName;
        if (queryParams.contactName) params.contactName = queryParams.contactName;
        if (queryParams.projectStatus) params.projectStatus = queryParams.projectStatus;
        if (queryParams.planStartDateStart) params.planStartDateStart = queryParams.planStartDateStart;
        if (queryParams.planEndDateEnd) params.planEndDateEnd = queryParams.planEndDateEnd;
      }

      const blob = await exportProject(params);

      // 处理文件下载
      if (blob) {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `项目管理数据_${Date.now()}.xlsx`;
        document.body.append(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      }

      exportProgress.value = 100;
      exportProgressText.value = '导出完成';

      setTimeout(async () => {
        await modalApi.close();
        const exportTypeName = exportType.value === 'all' ? '全部' : (exportType.value === 'selected' ? '选中' : '筛选结果');
        message.success(`导出${exportTypeName}数据成功`);
        emit('success');
      }, 500);
    } catch (error) {
      console.error('导出失败:', error);
      exportProgressText.value = '导出失败';
      message.error('导出失败');
    } finally {
      modalApi.unlock();
    }
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      exportType.value = 'all';
      exportProgress.value = 0;
      exportProgressText.value = '';
    }
  },
});

// 暴露方法
defineExpose({
  open: (data?: ExportData) => {
    exportData.value = data || {};
    // 如果有选中行，默认选中"导出选中"
    exportType.value = (data?.selectedIds && data.selectedIds.length > 0) ? 'selected' : 'all';
    modalApi.open();
  },
});
</script>

<template>
  <Modal>
    <div>
      <!-- 导出说明 -->
      <div class="export-description">
        确认导出当前记录吗？仅支持Excel格式导出
      </div>

      <!-- 导出选项 -->
      <div class="export-options">
        <Radio.Group v-model:value="exportType">
          <Radio value="all">导出全部</Radio>
          <Radio value="selected" :disabled="!hasSelected">导出选中{{ hasSelected ? ` (${selectedCount}条)` : '' }}</Radio>
          <Radio value="filtered">导出筛选结果</Radio>
        </Radio.Group>
      </div>

      <!-- 导出进度 -->
      <div v-if="exportProgress > 0" class="export-progress">
        <Progress :percent="exportProgress" />
        <span class="progress-text">{{ exportProgressText }}</span>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.export-description {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #1890ff;
}

.export-options {
  margin-bottom: 20px;
}

.export-progress {
  margin-top: 20px;
}

.progress-text {
  display: block;
  text-align: center;
  margin-top: 8px;
  color: #646a73;
}
</style>
