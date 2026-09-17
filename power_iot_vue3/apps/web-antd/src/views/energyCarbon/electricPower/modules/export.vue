<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { message, Radio } from 'ant-design-vue';

import { exportElectricity } from '#/api/energyCarbon/electricPower';

const emit = defineEmits(['success']);

// 接收参数
interface ExportData {
  selectedIds?: number[];
  queryParams?: Record<string, any>;
}

const exportType = ref('all');
const exportData = ref<ExportData>({});

// 计算选中数量
const selectedCount = computed(() => exportData.value.selectedIds?.length || 0);
// 是否有选中行
const hasSelected = computed(() => selectedCount.value > 0);

const [Modal, modalApi] = useVbenModal({
  title: '导出数据',
  async onConfirm() {
    // 如果选择导出选中但没有选中行
    if (exportType.value === 'selected' && !hasSelected.value) {
      message.warning('请先选择要导出的数据行');
      return;
    }

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
        if (queryParams.username) params.username = queryParams.username;
        if (queryParams.provinceCode) params.provinceCode = queryParams.provinceCode;
        if (queryParams.cityCode) params.cityCode = queryParams.cityCode;
        if (queryParams.districtCode) params.districtCode = queryParams.districtCode;
        if (queryParams.electricityId) params.electricityId = queryParams.electricityId;
      }

      const blob = (await exportElectricity(params as any)) as Blob;
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '电力数据.xlsx';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      const exportTypeName =
        exportType.value === 'all'
          ? '全部'
          : exportType.value === 'selected'
            ? '选中'
            : '筛选结果';
      message.success(`导出${exportTypeName}数据成功`);
      emit('success');
      await modalApi.close();
    } catch {
      message.error('导出失败');
    } finally {
      modalApi.unlock();
    }
  },
});

function open(data?: ExportData) {
  exportData.value = data || {};
  // 如果有选中行，默认选中"导出选中"
  exportType.value =
    data?.selectedIds && data.selectedIds.length > 0 ? 'selected' : 'all';
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal>
    <div>
      <!-- 导出说明 -->
      <div class="export-description">确认导出电力数据吗？导出格式为 Excel</div>

      <!-- 导出选项 -->
      <div class="export-options">
        <Radio.Group v-model:value="exportType">
          <Radio value="all">导出全部</Radio>
          <Radio value="selected" :disabled="!hasSelected">
            导出选中{{ hasSelected ? ` (${selectedCount}条)` : '' }}
          </Radio>
          <Radio value="filtered">导出筛选结果</Radio>
        </Radio.Group>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.export-description {
  padding: 10px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #1890ff;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
}

.export-options {
  margin-top: 16px;
}
</style>
