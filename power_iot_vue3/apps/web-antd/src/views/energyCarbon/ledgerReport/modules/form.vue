<script lang="ts" setup>
import { computed, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import {
  Button,
  Form,
  FormItem,
  Input,
  message,
  Select,
  Table,
  Tag,
  Upload,
} from 'ant-design-vue';

import { getAvailableFilesForReport, getLedgerFilePage } from '#/api/energyCarbon/ledgerFile';
import {
  createLedgerReport,
  getLedgerReport,
  submitLedgerReport,
  updateLedgerReport,
} from '#/api/energyCarbon/ledgerReport';

import { useCityDistrict } from '../../composables/useCityDistrict';


const emit = defineEmits(['success']);

const ledgerTypeOptions = getDictOptions(
  DICT_TYPE.ENERGY_CBON_LEDGER_TYPE,
  'string',
);

const {
  cityCode,
  cityOptions,
  districtCode,
  districtOptions,
  handleCityChange,
  restoreCityDistrict,
  resetCityDistrict,
} = useCityDistrict();

const formData = ref<any>();
const viewMode = ref(false);
const loading = ref(false);
const isInitializing = ref(false);
const submitting = ref(false);
const submittingReport = ref(false);

// 表单字段
const reportType = ref<string>();
const reportDesc = ref<string>('');
const remark = ref<string>('');
const stampedReportFile = ref<File>();
const stampedFileList = ref<any[]>([]);
const selectedFileId = ref<number>();
const selectedFileName = ref<string>('');

// 可选台账文件
const availableFiles = ref<any[]>([]);

// 当前状态
const currentStatus = computed(() => formData.value?.auditStatus);
const isEditable = computed(() => {
  if (viewMode.value) return false;
  // 新增或待提交状态可编辑
  return !formData.value?.id || currentStatus.value === '1';
});

// 上传文件始终可编辑（除非是查看模式）
const isUploadEditable = computed(() => !viewMode.value);

// 状态标签颜色
function statusColor(status?: string) {
  switch (status) {
    case '1': {
      return 'default';
    }
    case '2': {
      return 'orange';
    }
    case '3': {
      return 'green';
    }
    case '4': {
      return 'red';
    }
    default: {
      return 'default';
    }
  }
}

// 选中文件的数据量
const selectedFileDataCount = computed(() => {
  if (!selectedFileId.value) return 0;
  const file = availableFiles.value.find((f) => f.id === selectedFileId.value);
  return file?.dataCount || 0;
});

// 文件选项
const fileOptions = computed(() =>
  availableFiles.value.map((f) => ({
    label: `${f.fileName}（数据量：${f.dataCount ?? 0}）`,
    value: f.id,
    // 存储完整文件信息，用于回显
    fileData: f,
  })),
);

// 加载可用台账文件
async function loadAvailableFiles(uploadType?: string, district?: string) {
  try {
    // 如果有上报类型，使用专用接口
    if (uploadType) {
      const result = await getAvailableFilesForReport(uploadType, district);
      availableFiles.value = result || [];
    } else {
      // 否则使用分页接口获取待提交的文件
      const result = await getLedgerFilePage({
        pageNo: 1,
        pageSize: 30,
        auditStatus: '1', // 待提交
        districtCode: district,
      });
      availableFiles.value = result?.list || [];
    }
  } catch {
    availableFiles.value = [];
  }
}

// 监听上报类型变化，加载可用台账文件
watch(reportType, async (newVal) => {
  if (isInitializing.value) return;
  // 如果是通过选择文件自动回显的，不清空已选文件
  await loadAvailableFiles(newVal, districtCode.value);
});

// 监听选中文件变化，自动回显市、区县、上报类型等信息
watch(selectedFileId, async (newFileId) => {
  if (isInitializing.value) return;
  if (!newFileId) {
    selectedFileName.value = '';
    return;
  }
  // 从可用文件列表中查找
  let file = availableFiles.value.find((f) => f.id === newFileId);
  // 如果不在列表中，尝试从已选文件信息中获取
  if (!file && selectedFileName.value) {
    file = {
      id: newFileId,
      fileName: selectedFileName.value,
    };
  }
  if (!file) return;
  selectedFileName.value = file.fileName || '';
  // 自动回显市、区县、上报类型
  if (file.cityCode && file.districtCode) {
    await restoreCityDistrict({
      cityCode: file.cityCode,
      cityName: file.cityName,
      districtCode: file.districtCode,
      districtName: file.districtName,
    });
  }
  if (file.uploadType) {
    reportType.value = file.uploadType;
  }
});

// 盖章报告上传
function handleBeforeUploadStamped(file: File) {
  const name = file.name.toLowerCase();
  if (!name.endsWith('.pdf')) {
    message.error('盖章报告仅支持上传 PDF 格式的文件');
    return false;
  }
  return true;
}

function handleStampedUpload(info: any) {
  const rawFile = info.file?.originFileObj || info.file;
  if (!rawFile) return;
  stampedFileList.value = [info.file];
  stampedReportFile.value = rawFile;
}

function handleRemoveStamped() {
  stampedFileList.value = [];
  stampedReportFile.value = undefined;
}

// 构建提交数据（对应后端 CarbonLedgerReportSaveReqVO）
function buildSubmitData(): any {
  return {
    id: formData.value?.id,
    ledgerFileId: selectedFileId.value,
    reportDesc: reportDesc.value,
    remark: remark.value,
  };
}

// 校验
function validate(): boolean {
  if (!cityCode.value) {
    message.error('请选择所属市');
    return false;
  }
  if (!districtCode.value) {
    message.error('请选择所属区县');
    return false;
  }
  if (!reportType.value) {
    message.error('请选择上报类型');
    return false;
  }
  if (!stampedReportFile.value && !formData.value?.stampedReportUrl) {
    message.error('请上传盖章报告');
    return false;
  }
  if (!selectedFileId.value) {
    message.error('请选择上报文件');
    return false;
  }
  return true;
}

// 确定（仅保存）
async function handleConfirm() {
  if (!validate()) return;
  submitting.value = true;
  try {
    const data = buildSubmitData();
    await (data.id ? updateLedgerReport(data, stampedReportFile.value) : createLedgerReport(data, stampedReportFile.value));
    message.success('保存成功');
    emit('success');
    await modalApi.close();
  } catch {
    message.error('保存失败');
  } finally {
    submitting.value = false;
  }
}

// 提交（保存 + 提交审核）
async function handleSubmit() {
  if (!validate()) return;
  submittingReport.value = true;
  try {
    const data = buildSubmitData();
    let reportId = data.id;
    if (reportId) {
      await updateLedgerReport(data, stampedReportFile.value);
    } else {
      reportId = await createLedgerReport(data, stampedReportFile.value);
    }
    await submitLedgerReport(reportId);
    message.success('提交成功');
    emit('success');
    await modalApi.close();
  } catch {
    message.error('提交失败');
  } finally {
    submittingReport.value = false;
  }
}

function handleCancel() {
  modalApi.close();
}

function resetState() {
  formData.value = undefined;
  viewMode.value = false;
  isInitializing.value = false;
  resetCityDistrict();
  reportType.value = undefined;
  reportDesc.value = '';
  remark.value = '';
  stampedReportFile.value = undefined;
  stampedFileList.value = [];
  selectedFileId.value = undefined;
  selectedFileName.value = '';
  availableFiles.value = [];
}

// 审核日志表格列
const auditLogColumns = [
  { title: '序号', key: 'seq', width: 60, customRender: ({ index }: any) => index + 1 },
  {
    title: '流程状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }: any) =>
      getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS, text),
  },
  { title: '状态变更原因', dataIndex: 'reason', ellipsis: true },
  { title: '状态变更时间', dataIndex: 'changeTime', width: 180 },
];

const [Modal, modalApi] = useVbenModal({
  title: '数据上报',
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
    if (data.id) {
      loading.value = true;
      isInitializing.value = true;
      try {
        const detail = (await getLedgerReport(data.id)) || data;
        formData.value = detail;
        reportType.value = detail.uploadType;
        reportDesc.value = detail.reportDesc || '';
        remark.value = detail.remark || '';
        if (detail.stampedReportUrl) {
          stampedFileList.value = [
            {
              uid: '-1',
              name: detail.stampedReportName || '盖章报告.pdf',
              status: 'done',
              url: detail.stampedReportUrl,
            },
          ];
        }

        if (viewMode.value) {
          // 查看模式：不调用 area 接口，直接用返回的中文文本回填
          if (detail.cityCode) {
            cityCode.value = String(detail.cityCode);
            // 确保市选项中包含当前市
            if (!cityOptions.value.some((c) => c.value === String(detail.cityCode))) {
              cityOptions.value = [
                ...cityOptions.value,
                { label: detail.cityName || '', value: String(detail.cityCode) },
              ];
            }
          }
          if (detail.districtCode) {
            districtCode.value = String(detail.districtCode);
            districtOptions.value = [
              { label: detail.districtName || '', value: String(detail.districtCode) },
            ];
          }
          // 查看模式：不加载可用文件列表，直接用详情中的文件信息
          if (detail.ledgerFileId) {
            availableFiles.value = [
              { id: detail.ledgerFileId, fileName: detail.ledgerFileName || '' },
            ];
            selectedFileId.value = detail.ledgerFileId;
            selectedFileName.value = detail.ledgerFileName || '';
          } else if (Array.isArray(detail.files) && detail.files.length > 0) {
            // 兼容旧数据结构
            const firstFile = detail.files[0];
            const fileId = firstFile.fileId ?? firstFile.id;
            availableFiles.value = [
              { id: fileId, fileName: firstFile.fileName || '' },
            ];
            selectedFileId.value = fileId;
            selectedFileName.value = firstFile.fileName || '';
          }
        } else {
          // 编辑模式：需要加载 area 接口和可用文件列表
          if (detail.cityCode) {
            await restoreCityDistrict({
              cityCode: detail.cityCode,
              cityName: detail.cityName,
              districtCode: detail.districtCode,
              districtName: detail.districtName,
            });
          }
          // 加载可用文件并回填已选
          await loadAvailableFiles(detail.uploadType, detail.districtCode);
          // 回填已选文件（单选）
          if (detail.ledgerFileId) {
            selectedFileId.value = detail.ledgerFileId;
            // 如果已选文件不在可选列表中，添加到可选列表
            const existFile = availableFiles.value.find((af) => af.id === detail.ledgerFileId);
            if (!existFile && detail.ledgerFileName) {
              availableFiles.value.push({
                id: detail.ledgerFileId,
                fileName: detail.ledgerFileName,
              });
            }
            selectedFileName.value = existFile?.fileName || detail.ledgerFileName || '';
          } else if (Array.isArray(detail.files) && detail.files.length > 0) {
            // 兼容旧数据结构
            const firstFile = detail.files[0];
            selectedFileId.value = firstFile.fileId ?? firstFile.id;
            selectedFileName.value = firstFile.fileName || '';
            if (!availableFiles.value.some((af) => af.id === selectedFileId.value)) {
              availableFiles.value.push({
                id: selectedFileId.value,
                fileName: firstFile.fileName,
                fileUrl: firstFile.fileUrl,
                uploadType: firstFile.uploadType,
                dataCount: firstFile.dataCount,
              });
            }
          }
        }
      } catch (error) {
        console.error('获取详情失败:', error);
        message.error('获取详情失败');
      } finally {
        isInitializing.value = false;
        loading.value = false;
      }
    } else {
      formData.value = data;
      isInitializing.value = false;
      // 新增模式下加载所有可用文件
      await loadAvailableFiles();
    }
  },
});

// 盖章报告 PDF 预览
const previewPdfUrl = ref('');
const [PreviewModal, previewModalApi] = useVbenModal({
  title: '盖章报告预览',
  width: 'w-[900px]',
  footer: false,
});

function handlePreviewStamped() {
  if (formData.value?.stampedReportUrl) {
    previewPdfUrl.value = formData.value.stampedReportUrl;
    previewModalApi.open();
  }
}

defineExpose({
  open: (data?: any) => {
    modalApi.setData(data).open();
  },
});
</script>

<template>
  <Modal>
    <div v-loading="loading" class="px-4 py-2">
      <!-- 基本信息分区 -->
      <div class="section">
        <div class="section-title">基本信息</div>
        <Form
          layout="horizontal"
          :label-col="{ span: 5 }"
          :wrapper-col="{ span: 18 }"
        >
          <FormItem label="上报文件" required>
            <Select
              v-model:value="selectedFileId"
              :options="fileOptions"
              :disabled="!isUploadEditable"
              placeholder="请选择上报文件"
              allow-clear
              show-search
              :filter-option="(input: string, option: any) => (option?.label || '').toLowerCase().includes(input.toLowerCase())"
            />
            <div v-if="selectedFileId" class="format-hint">
              已选择文件，数据量 {{ selectedFileDataCount }}
            </div>
          </FormItem>
          <FormItem label="所属市" required>
            <Select
              v-model:value="cityCode"
              :options="cityOptions"
              :disabled="!isEditable"
              placeholder="请选择所属市"
              allow-clear
              @change="handleCityChange"
            />
          </FormItem>
          <FormItem label="所属区县" required>
            <Select
              v-model:value="districtCode"
              :options="districtOptions"
              :disabled="!isEditable || !cityCode"
              :placeholder="cityCode ? '请选择所属区县' : '请先选择所属市'"
              allow-clear
            />
          </FormItem>
          <FormItem label="上报类型" required>
            <Select
              v-model:value="reportType"
              :options="ledgerTypeOptions"
              :disabled="!isEditable"
              placeholder="请选择上报类型"
              allow-clear
            />
          </FormItem>
          <FormItem label="上报说明">
            <Input
              v-model:value="reportDesc"
              :disabled="!isEditable"
              :maxlength="500"
              placeholder="请填写上报说明"
            />
          </FormItem>
          <FormItem label="备注">
            <Input.TextArea
              v-model:value="remark"
              :disabled="!isEditable"
              :maxlength="500"
              :rows="3"
              placeholder="请填写备注"
            />
          </FormItem>
          <FormItem label="盖章报告" required>
            <div v-if="isUploadEditable" class="upload-area">
              <Upload
                v-model:file-list="stampedFileList"
                :before-upload="handleBeforeUploadStamped"
                :max-count="1"
                accept=".pdf"
                @change="handleStampedUpload"
              >
                <Button type="primary" size="small">点击上传</Button>
              </Upload>
              <div v-if="stampedFileList.length > 0" class="file-info">
                <span class="file-name">{{ stampedFileList[0]?.name }}</span>
                <Button type="link" danger size="small" @click="handleRemoveStamped">
                  移除
                </Button>
              </div>
              <div class="format-hint">盖章报告仅支持上传 PDF 格式的文件</div>
            </div>
            <div v-else class="stamped-report-view">
              <a
                v-if="formData?.stampedReportUrl"
                :href="formData.stampedReportUrl"
                target="_blank"
              >
                {{ formData.stampedReportName || '盖章报告.pdf' }}
              </a>
              <Button
                v-if="formData?.stampedReportUrl"
                type="link"
                size="small"
                @click="handlePreviewStamped"
              >
                在线预览
              </Button>
              <span v-else class="text-gray-400">暂无</span>
            </div>
          </FormItem>
        </Form>
      </div>

      <!-- 审核信息分区 -->
      <div v-if="formData?.id" class="section">
        <div class="section-header">
          <div class="section-title">审核信息</div>
          <Tag v-if="currentStatus" :color="statusColor(currentStatus)">
            {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS, currentStatus) }}
          </Tag>
        </div>
        <Table
          :columns="auditLogColumns as any"
          :data-source="formData?.auditLogs || []"
          :pagination="false"
          row-key="id"
          size="small"
          bordered
        >
          <template #emptyText>暂无审核轨迹</template>
        </Table>
      </div>

      <!-- 底部按钮区 -->
      <div class="footer-buttons">
        <Button
          v-if="isEditable"
          type="primary"
          :loading="submitting"
          @click="handleConfirm"
        >
          保存
        </Button>
        <Button
          v-if="isEditable"
          style="background: #52c41a; border-color: #52c41a; color: #fff"
          :loading="submittingReport"
          @click="handleSubmit"
        >
          提交审核
        </Button>
        <Button danger @click="handleCancel">取消</Button>
      </div>
    </div>
  </Modal>
  <PreviewModal>
    <iframe
      v-if="previewPdfUrl"
      :src="previewPdfUrl"
      style="width: 100%; height: 75vh; border: none;"
    ></iframe>
  </PreviewModal>
</template>

<style scoped>
.section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #1890ff;
  padding-left: 8px;
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

.stamped-report-view {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
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
