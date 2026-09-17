<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import {
  Modal as AModal,
  Button,
  Descriptions,
  DescriptionsItem,
  Input,
  message,
  Table,
  Tag,
} from 'ant-design-vue';

import { downloadLedgerFile, updateAuditStatus } from '#/api/energyCarbon/ledgerFile';
import { getLedgerReport } from '#/api/energyCarbon/ledgerReport';



const emit = defineEmits(['success']);

const formData = ref<any>();
const viewMode = ref(false);
const auditMode = ref(false);
const loading = ref(false);
const approving = ref(false);
const rejecting = ref(false);
const showRejectForm = ref(false);
const rejectReason = ref('');

const title = computed(() => {
  if (auditMode.value) return '审核';
  return '查看';
});

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

// 上报文件表格列
const fileColumns = [
  { title: '文件名称', dataIndex: 'fileName', ellipsis: true },
  {
    title: '上传类型',
    dataIndex: 'uploadType',
    width: 120,
    customRender: ({ text }: any) => getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, text),
  },
  { title: '数据量', dataIndex: 'dataCount', width: 100 },
  { title: '操作', key: 'operation', width: 120 },
];

// 下载盖章报告
function handleDownloadStamped() {
  if (formData.value?.stampedReportUrl) {
    window.open(formData.value.stampedReportUrl, '_blank');
  }
}

// 下载上报文件
async function handleDownloadFile(record: any) {
  const fileId = record.fileId ?? record.ledgerFileId;
  if (!fileId) return;
  try {
    const blob = (await downloadLedgerFile(fileId)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = record.fileName || '台账文件.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch {
    message.error('文件下载失败');
  }
}

// 打开上报文件（新标签页）
function handleOpenFile(record: any) {
  const url = record.ledgerFileUrl;
  if (url) {
    window.open(url, '_blank');
  }
}

// 通过审核（二次确认）
function handleApprove() {
  AModal.confirm({
    title: '审核确认',
    content: '确定要通过该上报记录吗？通过后操作不可逆。',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      approving.value = true;
      try {
        await updateAuditStatus(formData.value.ledgerFileId, '3'); // 审核通过，状态改为已通过
        message.success('审核通过');
        emit('success');
        await modalApi.close();
      } catch {
        message.error('审核操作失败');
      } finally {
        approving.value = false;
      }
    },
  });
}

// 显示驳回表单
function handleShowReject() {
  showRejectForm.value = true;
  rejectReason.value = '';
}

// 取消驳回
function handleCancelReject() {
  showRejectForm.value = false;
  rejectReason.value = '';
}

// 确认驳回
async function handleConfirmReject() {
  if (!rejectReason.value.trim()) {
    message.error('请输入驳回原因');
    return;
  }
  if (rejectReason.value.length > 500) {
    message.error('驳回原因不能超过500字');
    return;
  }
  rejecting.value = true;
  try {
    await updateAuditStatus(formData.value.ledgerFileId, '4', rejectReason.value); // 驳回，状态改为已驳回
    message.success('驳回成功');
    emit('success');
    await modalApi.close();
  } catch {
    message.error('驳回操作失败');
  } finally {
    rejecting.value = false;
  }
}

function handleCancel() {
  modalApi.close();
}

function resetState() {
  formData.value = undefined;
  viewMode.value = false;
  auditMode.value = false;
  loading.value = false;
  approving.value = false;
  rejecting.value = false;
  showRejectForm.value = false;
  rejectReason.value = '';
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
    auditMode.value = !!data.auditMode;
    if (data.id) {
      loading.value = true;
      try {
        const detail = (await getLedgerReport(data.id)) || data;
        formData.value = detail;
      } catch {
        message.error('获取详情失败');
      } finally {
        loading.value = false;
      }
    } else {
      formData.value = data;
    }
  },
});

// PDF 预览
const previewPdfUrl = ref('');
const [PreviewModal, previewModalApi] = useVbenModal({
  title: '文件预览',
  width: 'w-[900px]',
  footer: false,
  onOpenChange(isOpen: boolean) {
    if (!isOpen && previewPdfUrl.value?.startsWith('blob:')) {
      window.URL.revokeObjectURL(previewPdfUrl.value);
      previewPdfUrl.value = '';
    }
  },
});

function handlePreviewStamped() {
  if (formData.value?.stampedReportUrl) {
    previewPdfUrl.value = formData.value.stampedReportUrl;
    previewModalApi.open();
  }
}

// 预览上报文件 - 下载文件流后使用对象URL在弹框中展示
function handlePreviewFile(record: any) {
  const fileId = record.fileId ?? record.ledgerFileId;
  if (!fileId) return;
  downloadFileForPreview(fileId);
}

function handlePreviewLedgerFile() {
  const fileId = formData.value?.ledgerFileId;
  if (!fileId) return;
  downloadFileForPreview(fileId);
}

async function downloadFileForPreview(fileId: number) {
  try {
    const blob = (await downloadLedgerFile(fileId)) as Blob;
    if (previewPdfUrl.value && previewPdfUrl.value.startsWith('blob:')) {
      window.URL.revokeObjectURL(previewPdfUrl.value);
    }
    previewPdfUrl.value = window.URL.createObjectURL(blob);
    previewModalApi.open();
  } catch {
    message.error('文件预览失败');
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
      <!-- 基本信息（只读） -->
      <div class="section">
        <div class="section-title">基本信息</div>
        <Descriptions :column="2" bordered size="small">
          <DescriptionsItem label="所属市">
            {{ formData?.cityName || '-' }}
          </DescriptionsItem>
          <DescriptionsItem label="所属区县">
            {{ formData?.districtName || '-' }}
          </DescriptionsItem>
          <DescriptionsItem label="上报类型">
            {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, formData?.uploadType) }}
          </DescriptionsItem>
          <DescriptionsItem label="流程状态">
            <Tag v-if="formData?.auditStatus" :color="statusColor(formData.auditStatus)">
              {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS, formData.auditStatus) }}
            </Tag>
            <span v-else>-</span>
          </DescriptionsItem>
          <DescriptionsItem label="上报说明" :span="2">
            {{ formData?.reportDesc || '-' }}
          </DescriptionsItem>
        </Descriptions>
      </div>

      <!-- 附件区（只读） -->
      <div class="section">
        <div class="section-title">附件</div>
        <div class="attachment-row">
          <span class="attachment-label">盖章报告：</span>
          <template v-if="formData?.stampedReportUrl">
            <Button
              type="link"
              size="small"
              @click="handleDownloadStamped"
            >
              {{ formData?.stampedReportName || '盖章报告.pdf' }}
            </Button>
            <Button
              type="link"
              size="small"
              @click="handlePreviewStamped"
            >
              在线预览
            </Button>
          </template>
          <span v-else class="text-gray-400">暂无</span>
        </div>
        <div class="attachment-row">
          <span class="attachment-label">上报文件：</span>
          <Table
            v-if="formData?.files?.length"
            :columns="fileColumns"
            :data-source="formData.files"
            :pagination="false"
            row-key="fileId"
            size="small"
            bordered
            class="mt-1"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'fileName'">
                <Button
                  type="link"
                  size="small"
                  @click="handleOpenFile(record)"
                >
                  {{ record.fileName }}
                </Button>
              </template>
              <template v-if="column.key === 'operation'">
                <Button
                  type="link"
                  size="small"
                  @click="handlePreviewFile(record)"
                >
                  预览
                </Button>
                <Button
                  type="link"
                  size="small"
                  @click="handleDownloadFile(record)"
                >
                  下载
                </Button>
              </template>
            </template>
          </Table>
          <div v-else-if="formData?.ledgerFileUrl" class="mt-1">
            <Button
              type="link"
              size="small"
              @click="handleOpenFile(formData)"
            >
              {{ formData.ledgerFileName || '上报文件' }}
            </Button>
<!--            <Button-->
<!--              type="link"-->
<!--              size="small"-->
<!--              @click="handlePreviewLedgerFile"-->
<!--            >-->
<!--              在线预览-->
<!--            </Button>-->
            <Button
              type="link"
              size="small"
              @click="handleDownloadFile(formData)"
            >
              下载
            </Button>
          </div>
          <span v-else class="text-gray-400">暂无</span>
        </div>
      </div>

      <!-- 审核日志（只读） -->
      <div v-if="formData?.auditLogs?.length" class="section">
        <div class="section-title">审核日志</div>
        <Table
          :columns="auditLogColumns as any"
          :data-source="formData.auditLogs"
          :pagination="false"
          row-key="id"
          size="small"
          bordered
        />
      </div>

      <!-- 驳回表单（仅驳回时显示） -->
      <div v-if="showRejectForm" class="section">
        <div class="section-title">驳回原因</div>
        <Input.TextArea
          v-model:value="rejectReason"
          placeholder="请输入驳回原因"
          :rows="3"
          :maxlength="500"
          show-count
        />
        <div class="mt-2 text-right">
          <Button size="small" class="mr-2" @click="handleCancelReject">
            取消
          </Button>
          <Button
            type="primary"
            danger
            size="small"
            :loading="rejecting"
            @click="handleConfirmReject"
          >
            确认驳回
          </Button>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div v-if="auditMode && !showRejectForm" class="footer-buttons">
        <Button :loading="approving" type="primary" @click="handleApprove">
          通过
        </Button>
        <Button danger @click="handleShowReject">驳回</Button>
        <Button @click="handleCancel">取消</Button>
      </div>
      <div v-else-if="!showRejectForm" class="footer-buttons">
        <Button @click="handleCancel">取消</Button>
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

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #1890ff;
  padding-left: 8px;
}

.attachment-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
}

.attachment-label {
  flex-shrink: 0;
  width: 80px;
  font-size: 14px;
  color: #666;
  line-height: 28px;
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
