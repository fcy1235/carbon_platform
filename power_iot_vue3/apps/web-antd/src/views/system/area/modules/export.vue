<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Radio, message } from 'ant-design-vue';

const emit = defineEmits(['success']);

const exportType = ref('all');

const [Modal, modalApi] = useVbenModal({
  title: '导出行政区划',
  async onConfirm() {
    message.success(`导出${exportType.value === 'all' ? '全部' : exportType.value === 'selected' ? '选中' : '筛选结果'}数据成功`);
    await modalApi.close();
    emit('success');
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      exportType.value = 'all';
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
      <div class="export-description">
        确认导出当前选择记录？仅支持Excel格式导出
      </div>
      
      <div class="export-options">
        <Radio.Group v-model:value="exportType">
          <Radio value="all">导出全部</Radio>
          <Radio value="selected">导出选中</Radio>
          <Radio value="filtered">导出筛选结果</Radio>
        </Radio.Group>
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
</style>
