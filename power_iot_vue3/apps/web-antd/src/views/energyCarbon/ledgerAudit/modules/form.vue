<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import {
  Button,
  Descriptions,
  DescriptionsItem,
  Form,
  FormItem,
  message,
  Select,
  Upload,
} from 'ant-design-vue';

import {
  createLedgerFile,
  downloadLedgerTemplate,
  getLedgerFile,
  updateLedgerFile,
} from '#/api/energyCarbon/ledgerFile';

import { useCityDistrict } from '../../composables/useCityDistrict';

const emit = defineEmits(['success']);

const {
  cityCode,
  cityOptions,
  districtCode,
  districtOptions,
  districtLoading,
  handleCityChange,
  restoreCityDistrict,
  getCityDistrictData,
  resetCityDistrict,
} = useCityDistrict();

const ledgerTypeOptions = getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, 'string');

const templateList = ledgerTypeOptions.map((item) => ({
  value: item.value,
  label: `${item.label}模板`,
}));

async function handleDownloadTemplate(type: string, label: string) {
  try {
    const blob = (await downloadLedgerTemplate(type)) as Blob;
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

const formData = ref<any>({});
const viewMode = ref(false);
const editMode = ref(false);
const selectedFile = ref<File | null>(null);
const submitting = ref(false);
const loading = ref(false);

const title = computed(() => {
  if (viewMode.value) return '查看台账审定';
  if (editMode.value) return '编辑台账审定';
  return '新增台账审定';
});

const canSubmit = computed(() => {
  if (viewMode.value) return false;
  if (!cityCode.value) return false;
  if (!districtCode.value) return false;
  return !!formData.value?.uploadType && (!!selectedFile.value || editMode.value);
});

async function handleBeforeUpload(file: File) {
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
  if (!formData.value?.uploadType) {
    message.error('请选择上传类型');
    return;
  }
  if (!selectedFile.value && !editMode.value) {
    message.error('请选择上传文件');
    return;
  }

  submitting.value = true;
  try {
    const cityDistrictData = getCityDistrictData();
    if (editMode.value && formData.value?.id) {
      await updateLedgerFile(
        formData.value.id,
        formData.value.uploadType,
        selectedFile.value || undefined,
        cityDistrictData.cityCode,
        cityDistrictData.districtCode,
      );
      message.success('更新成功');
    } else {
      await createLedgerFile(
        formData.value.uploadType,
        selectedFile.value!,
        cityDistrictData.cityCode,
        cityDistrictData.districtCode,
      );
      message.success('创建成功');
    }
    emit('success');
    await modalApi.close();
  } catch (error: any) {
    const msg = error?.message || (editMode.value ? '更新失败' : '创建失败');
    message.error(msg);
  } finally {
    submitting.value = false;
  }
}

function handleCancel() {
  modalApi.close();
}

function resetState() {
  formData.value = {};
  viewMode.value = false;
  editMode.value = false;
  selectedFile.value = null;
  submitting.value = false;
  loading.value = false;
  resetCityDistrict();
}

const [Modal, modalApi] = useVbenModal({
  title: title as any,
  width: 'w-[800px]',
  footer: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      resetState();
      return;
    }
    const data = modalApi.getData<any>();
    if (!data) return;
    viewMode.value = !!data.viewMode;
    editMode.value = !data.viewMode && !!data.id;

    if (data.id) {
      loading.value = true;
      try {
        const detail = (await getLedgerFile(data.id)) || data;
        formData.value = { ...detail };
        // 回填市/区县
        if (detail.cityCode) {
          await restoreCityDistrict({
            cityCode: detail.cityCode,
            cityName: detail.cityName,
            districtCode: detail.districtCode,
            districtName: detail.districtName,
          });
        }
      } catch {
        message.error('获取详情失败');
      } finally {
        loading.value = false;
      }
    } else {
      formData.value = { uploadType: undefined };
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
    <div v-loading="loading" class="px-4 py-2">
      <!-- 查看模式 -->
      <template v-if="viewMode">
        <div class="section">
          <div class="section-title">基本信息</div>
          <Descriptions :column="2" bordered size="small">
            <DescriptionsItem label="所属市">
              {{ formData?.cityName || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="所属区县">
              {{ formData?.districtName || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="上传类型">
              {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, formData?.uploadType) }}
            </DescriptionsItem>
            <DescriptionsItem label="文件名称">
              {{ formData?.fileName || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="数据量">
              {{ formData?.dataCount ?? '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="上传状态">
              {{ formData?.uploadStatus || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="上传时间">
              {{ formData?.uploadTime || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="上传人">
              {{ formData?.uploader || '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="备注" :span="2">
              {{ formData?.remark || '-' }}
            </DescriptionsItem>
          </Descriptions>
        </div>
      </template>

      <!-- 编辑/新增模式 -->
      <template v-else>
        <!-- 基本信息 -->
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
            <FormItem label="上传类型" required>
              <Select
                v-model:value="formData.uploadType"
                :options="ledgerTypeOptions"
                placeholder="请选择上传类型"
                allow-clear
              />
            </FormItem>
          </Form>
        </div>

        <!-- 下载上传模板 -->
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

        <!-- 上传文件 -->
        <div class="section">
          <div class="section-title">上传文件</div>
          <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
            <FormItem label="上传文件" :required="!editMode">
              <div class="upload-area">
                <Upload
                  :before-upload="handleBeforeUpload"
                  :disabled="!formData.uploadType"
                  :max-count="1"
                  :file-list="[]"
                  accept=".xls,.xlsx"
                >
                  <Button type="primary" :disabled="!formData.uploadType">
                    点击上传
                  </Button>
                </Upload>
                <div v-if="selectedFile" class="file-info">
                  <span class="file-name">{{ selectedFile.name }}</span>
                  <Button type="link" danger size="small" @click="handleRemoveFile">
                    移除
                  </Button>
                </div>
                <div v-else-if="editMode && formData.fileName" class="file-info">
                  <span class="file-name">当前文件：{{ formData.fileName }}</span>
                </div>
              </div>
              <div class="format-hint">
                仅支持上传符合模板要求的 .xls/.xlsx 文件
                <template v-if="editMode">
                  ，不选择文件则保留原文件
                </template>
              </div>
            </FormItem>
          </Form>
        </div>
      </template>

      <!-- 底部按钮 -->
      <div class="footer-buttons">
        <template v-if="!viewMode">
          <Button type="primary" :loading="submitting" :disabled="!canSubmit" @click="handleConfirm">
            {{ editMode ? '保存' : '创建' }}
          </Button>
        </template>
        <Button @click="handleCancel">
          {{ viewMode ? '关闭' : '取消' }}
        </Button>
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
