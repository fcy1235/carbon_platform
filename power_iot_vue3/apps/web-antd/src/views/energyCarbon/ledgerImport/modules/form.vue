<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import {
  Button,
  Form,
  FormItem,
  message,
  Select,
  Upload,
} from 'ant-design-vue';

import {
  createLedgerImportTask,
  downloadLedgerImportTemplate,
} from '#/api/energyCarbon/ledgerImport';

import { useCityDistrict } from '../../composables/useCityDistrict';

const emit = defineEmits(['success']);

const {
  cityCode,
  cityOptions,
  loadCityOptions,
  districtCode,
  districtOptions,
  districtLoading,
  handleCityChange,
  resetCityDistrict,
} = useCityDistrict();

const ledgerTypeOptions = getDictOptions(
  DICT_TYPE.ENERGY_CBON_LEDGER_TYPE,
  'string',
);

const uploadType = ref<string>();
const selectedFile = ref<File | null>(null);
const submitting = ref(false);

const title = computed(() => '添加导入任务');

const canSubmit = computed(() => {
  if (!cityCode.value) return false;
  if (!districtCode.value) return false;
  return !!uploadType.value && !!selectedFile.value;
});

const templateList = ledgerTypeOptions.map((item) => ({
  value: item.value,
  label: `${item.label}模板`,
}));

async function handleDownloadTemplate(type: string, label: string) {
  try {
    const blob = (await downloadLedgerImportTemplate(type)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${label}.xlsx`;
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('模板下载成功');
  } catch {
    message.error('模板下载失败');
  }
}

function handleBeforeUpload(file: File) {
  const name = file.name.toLowerCase();
  if (!name.endsWith('.xls') && !name.endsWith('.xlsx')) {
    message.error('仅支持上传 .xls/.xlsx 格式文件');
    return false;
  }
  selectedFile.value = file;
  return false;
}

function handleRemoveFile() {
  selectedFile.value = null;
}

async function handleConfirm() {
  if (!cityCode.value) {
    message.error('请选择所属市');
    return;
  }
  if (!districtCode.value) {
    message.error('请选择所属区县');
    return;
  }
  if (!uploadType.value) {
    message.error('请选择上传类型');
    return;
  }
  if (!selectedFile.value) {
    message.error('请选择上传文件');
    return;
  }

  submitting.value = true;
  try {
    await createLedgerImportTask(
      uploadType.value,
      selectedFile.value,
      cityCode.value,
      districtCode.value,
    );
    message.success('导入任务创建成功，正在检测文件...');
    emit('success');
    await modalApi.close();
  } catch {
    message.error('导入任务创建失败');
  } finally {
    submitting.value = false;
  }
}

function handleCancel() {
  modalApi.close();
}

function resetState() {
  uploadType.value = undefined;
  selectedFile.value = null;
  submitting.value = false;
  resetCityDistrict();
}

const [Modal, modalApi] = useVbenModal({
  title: title as any,
  width: 'w-[640px]',
  footer: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      resetState();
      return;
    }
    await loadCityOptions();
  },
});

defineExpose({
  open: () => {
    modalApi.setData({}).open();
  },
});
</script>

<template>
  <Modal>
    <div class="px-4 py-2">
      <!-- 基本信息（所属市/区县） -->
      <div class="section">
        <div class="section-title">基本信息</div>
        <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
          <FormItem label="所属市" required>
            <Select
              v-model:value="cityCode"
              :options="cityOptions"
              option-filter-prop="label"
              placeholder="请选择所属市"
              allow-clear
              show-search
              @change="handleCityChange"
            />
          </FormItem>
          <FormItem label="所属区县" required>
            <Select
              v-model:value="districtCode"
              :options="districtOptions"
              option-filter-prop="label"
              :loading="districtLoading"
              :disabled="!cityCode"
              placeholder="请先选择所属市"
              allow-clear
              show-search
            />
          </FormItem>
        </Form>
      </div>

      <!-- 下载上传模板分区 -->
      <div class="section">
        <div class="section-title">下载上传模板</div>
        <div class="template-list">
          <div
            v-for="item in templateList"
            :key="item.value"
            class="template-item"
          >
            <span class="template-name">{{ item.label }}</span>
            <Button
              type="primary"
              size="small"
              @click="handleDownloadTemplate(item.value, item.label)"
            >
              点击下载
            </Button>
          </div>
        </div>
      </div>

      <!-- 上传文件分区 -->
      <div class="section">
        <div class="section-title">上传文件</div>
        <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
          <FormItem label="上传类型" required>
            <Select
              v-model:value="uploadType"
              :options="ledgerTypeOptions"
              placeholder="请选择上传类型"
              allow-clear
            />
          </FormItem>
          <FormItem label="上传文件" required>
            <div class="upload-area">
              <Upload
                :before-upload="handleBeforeUpload"
                :disabled="!uploadType"
                :max-count="1"
                :file-list="[]"
                accept=".xls,.xlsx"
              >
                <Button type="primary" :disabled="!uploadType">
                  点击上传
                </Button>
              </Upload>
              <div v-if="selectedFile" class="file-info">
                <span class="file-name">{{ selectedFile.name }}</span>
                <Button type="link" danger size="small" @click="handleRemoveFile">
                  移除
                </Button>
              </div>
            </div>
            <div class="format-hint">
              仅支持上传符合上述模板要求的.xls/.xlsx 文件
            </div>
          </FormItem>
        </Form>
      </div>

      <!-- 底部按钮区 -->
      <div class="footer-buttons">
        <Button
          type="primary"
          :loading="submitting"
          :disabled="!canSubmit"
          @click="handleConfirm"
        >
          导入
        </Button>
        <Button danger @click="handleCancel">取消</Button>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.section {
  margin-bottom: 20px;
}

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #1890ff;
  padding-left: 8px;
}

.template-list {
  background: #fafafa;
  padding: 12px;
  border-radius: 4px;
}

.template-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
}

.template-name {
  font-size: 14px;
  color: #333;
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.file-name {
  font-size: 13px;
  color: #1890ff;
}

.format-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.footer-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
