<script lang="ts" setup>
import type {
  DocumentDetail,
  DocumentParam,
} from '#/api/energyCarbon/document';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Input, message, Table, Upload } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createDocumentWithFile,
  getDocument,
  getDocumentCode,
  updateDocumentWithFile,
} from '#/api/energyCarbon/document';

import { useBaseFormSchema } from '../data';

const props = defineProps<{
  model?: Record<string, any>;
  viewMode?: boolean;
}>();

const emit = defineEmits(['success']);

const viewMode = ref(false);
const currentId = ref<number | undefined>(undefined);
const title = computed(() => {
  if (viewMode.value) return '查看碳核算标准文档';
  return currentId.value ? '编辑碳核算标准文档' : '新增碳核算标准文档';
});

// 文件列表
const fileList = ref<any[]>([]);
// 当前选中的文件（用于上传）
const selectedFile = ref<File | null>(null);
// 已有文件地址（编辑时回显）
const existingFileUrl = ref<string>('');

// 详细信息列表
const details = ref<DocumentDetail[]>([]);

// 参数说明列表
const params = ref<DocumentParam[]>([]);

// 详细信息表格列定义
const detailColumns = [
  { title: '名称', dataIndex: 'name' },
  { title: '描述', dataIndex: 'description' },
  { title: '计算公式', dataIndex: 'formula' },
  {
    title: '操作',
    width: 80,
    key: 'operation',
  },
];

// 参数说明表格列定义
const paramColumns = [
  { title: '参数名称', dataIndex: 'paramName' },
  { title: '参数描述', dataIndex: 'paramDescription' },
  {
    title: '操作',
    width: 80,
    key: 'operation',
  },
];

// 基础信息表单
const [BaseForm, baseFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: useBaseFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2 gap-3',
});

// 添加详细信息行
function addDetailRow() {
  details.value.push({
    id: Date.now(),
    name: '',
    description: '',
    formula: '',
  });
}

// 删除详细信息行
function removeDetailRow(index: number) {
  details.value.splice(index, 1);
}

// 添加参数行
function addParamRow() {
  params.value.push({
    id: Date.now(),
    paramName: '',
    paramDescription: '',
  });
}

// 删除参数行
function removeParamRow(index: number) {
  params.value.splice(index, 1);
}

// 阻止自动上传
function beforeUpload(file: File) {
  selectedFile.value = file;
  return false;
}

// 文件变化
function handleFileChange(info: any) {
  const list = info.fileList || [];
  // 过滤已移除的文件
  const validList = list.filter((f: any) => f.status !== 'removed');
  fileList.value = validList.length > 0 ? [validList[validList.length - 1]] : [];
  if (fileList.value.length > 0 && fileList.value[0].originFileObj) {
    selectedFile.value = fileList.value[0].originFileObj;
  } else {
    selectedFile.value = null;
  }
}

// 提交表单
async function handleSubmit() {
  const { valid: baseValid } = await baseFormApi.validate();
  if (!baseValid) {
    return;
  }

  const baseData = await baseFormApi.getValues();

  // 新增时必须上传文件
  if (!currentId.value && !selectedFile.value) {
    message.error('请上传文件');
    return;
  }

  const submitData: any = {
    id: currentId.value,
    ...baseData,
    details: details.value.map((item) => ({
      name: item.name,
      description: item.description,
      formula: item.formula,
    })),
    params: params.value.map((item) => ({
      paramName: item.paramName,
      paramDescription: item.paramDescription,
    })),
  };

  modalApi.lock();
  try {
    await (currentId.value
      ? updateDocumentWithFile(submitData, selectedFile.value || undefined)
      : createDocumentWithFile(submitData, selectedFile.value!));
    message.success('保存成功');
    emit('success');
    await modalApi.close();
  } catch {
    message.error('保存失败');
  } finally {
    modalApi.unlock();
  }
}

const [Modal, modalApi] = useVbenModal({
  title,
  footer: false,
  width: 900,
  centered: true,
  class: 'max-w-[900px]',
  onConfirm: async () => {
    // 由 handleSubmit 处理
    return true;
  },
  onCancel() {
    viewMode.value = false;
    currentId.value = undefined;
    details.value = [];
    params.value = [];
    fileList.value = [];
    selectedFile.value = null;
    existingFileUrl.value = '';
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      viewMode.value = false;
      currentId.value = undefined;
      details.value = [];
      params.value = [];
      fileList.value = [];
      selectedFile.value = null;
      existingFileUrl.value = '';
    }
  },
});

// 打开模态框
async function open(data?: Record<string, any>) {
  if (data) {
    viewMode.value = !!data.viewMode;
    currentId.value = data.id;
    delete data.viewMode;

    if (data.id) {
      try {
        const apiData = await getDocument(data.id);
        if (apiData) {
          baseFormApi.setValues(apiData);

          if (apiData.details) {
            details.value = Array.isArray(apiData.details)
              ? [...apiData.details]
              : [];
          } else {
            details.value = [];
          }

          if (apiData.params) {
            params.value = Array.isArray(apiData.params)
              ? [...apiData.params]
              : [];
          } else {
            params.value = [];
          }

          // 回显已有文件
          existingFileUrl.value = apiData.fileUrl || '';
          if (apiData.fileUrl) {
            const fileName = apiData.docName || apiData.fileUrl.split('/').pop() || '已上传文件';
            fileList.value = [
              {
                uid: '-1',
                name: fileName,
                status: 'done',
                url: apiData.fileUrl,
              },
            ];
          }

          modalApi.open();
          return;
        }
      } catch (error: any) {
        console.error('获取文档详情失败:', error);
        message.error('获取文档详情失败');
      }
    }

    baseFormApi.setValues(data);
    details.value = data.details ? [...data.details] : [];
    params.value = data.params ? [...data.params] : [];

    if (!data.id && !data.docCode) {
      try {
        const codeRes = await getDocumentCode();
        if (codeRes) {
          baseFormApi.setFieldValue('docCode', codeRes);
        }
      } catch {
        message.error('获取文档编码失败');
      }
    }
  } else {
    currentId.value = undefined;
    details.value = [];
    params.value = [];

    try {
      const codeRes = await getDocumentCode();
      if (codeRes) {
        baseFormApi.setFieldValue('docCode', codeRes);
      }
    } catch {
      message.error('获取文档编码失败');
    }
  }
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal>
    <div>
      <!-- 基础信息 -->
      <div class="form-section">
        <h3 class="section-title">基础信息</h3>
        <BaseForm />
      </div>

      <!-- 详细信息列表 -->
      <div class="form-section">
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 5px;
          "
        >
          <h3 class="section-title" style="margin-bottom: 0">详细信息列表</h3>
          <Button
            v-if="!viewMode"
            class="add-row-btn"
            type="primary"
            @click="addDetailRow"
          >
            + 添加明细
          </Button>
        </div>
        <Table
          :columns="detailColumns"
          :data-source="details"
          :pagination="false"
          :row-key="(record: any) => record.id || record.name"
          bordered
          size="small"
        >
          <template #emptyText>暂无详细信息</template>
          <template #bodyCell="{ column, index }">
            <template v-if="column.dataIndex === 'name'">
              <Input
                v-model:value="details[index].name"
                :disabled="viewMode"
                placeholder="名称"
                size="small"
              />
            </template>
            <template v-if="column.dataIndex === 'description'">
              <Input
                v-model:value="details[index].description"
                :disabled="viewMode"
                placeholder="描述"
                size="small"
              />
            </template>
            <template v-if="column.dataIndex === 'formula'">
              <Input
                v-model:value="details[index].formula"
                :disabled="viewMode"
                placeholder="计算公式"
                size="small"
              />
            </template>
            <template v-if="column.key === 'operation' && !viewMode">
              <Button
                danger
                size="small"
                type="primary"
                @click="removeDetailRow(index)"
              >
                删除
              </Button>
            </template>
          </template>
        </Table>
      </div>

      <!-- 参数说明列表 -->
      <div class="form-section">
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 5px;
          "
        >
          <h3 class="section-title" style="margin-bottom: 0">参数说明列表</h3>
          <Button
            v-if="!viewMode"
            class="add-row-btn"
            type="primary"
            @click="addParamRow"
          >
            + 添加参数
          </Button>
        </div>
        <Table
          :columns="paramColumns"
          :data-source="params"
          :pagination="false"
          :row-key="(record: any) => record.id || record.paramName"
          bordered
          size="small"
        >
          <template #emptyText>暂无参数说明</template>
          <template #bodyCell="{ column, index }">
            <template v-if="column.dataIndex === 'paramName'">
              <Input
                v-model:value="params[index].paramName"
                :disabled="viewMode"
                placeholder="参数名称"
                size="small"
              />
            </template>
            <template v-if="column.dataIndex === 'paramDescription'">
              <Input
                v-model:value="params[index].paramDescription"
                :disabled="viewMode"
                placeholder="参数描述"
                size="small"
              />
            </template>
            <template v-if="column.key === 'operation' && !viewMode">
              <Button
                danger
                size="small"
                type="primary"
                @click="removeParamRow(index)"
              >
                删除
              </Button>
            </template>
          </template>
        </Table>
      </div>

      <!-- 上传文件 -->
      <div class="form-section">
        <h3 class="section-title">上传文件</h3>
        <div style="padding: 0 8px">
          <Upload
            v-model:file-list="fileList"
            :before-upload="beforeUpload"
            :disabled="viewMode"
            :max-count="1"
            @change="handleFileChange"
          >
            <Button v-if="!viewMode" type="primary">选择文件</Button>
          </Upload>
          <!-- 显示当前文件名称 -->
          <div v-if="fileList.length > 0" style="margin-top: 8px; display: flex; align-items: center; gap: 8px">
            <span style="font-size: 13px; color: rgba(0,0,0,0.65)">
              已选文件：
            </span>
            <a
              v-if="fileList[0].url || existingFileUrl"
              :href="fileList[0].url || existingFileUrl"
              target="_blank"
              style="font-size: 13px"
            >
              {{ fileList[0].name || fileList[0].originFileObj?.name || existingFileUrl?.split('/').pop() }}
            </a>
            <span v-else style="font-size: 13px; color: rgba(0,0,0,0.85)">
              {{ fileList[0].name || fileList[0].originFileObj?.name }}
            </span>
          </div>
          <div v-else-if="existingFileUrl" style="margin-top: 8px; display: flex; align-items: center; gap: 8px">
            <span style="font-size: 13px; color: rgba(0,0,0,0.65)">
              已选文件：
            </span>
            <a :href="existingFileUrl" target="_blank" style="font-size: 13px">
              {{ existingFileUrl.split('/').pop() }}
            </a>
          </div>
          <div v-else style="margin-top: 8px; font-size: 13px; color: #999">
            暂无文件
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="modal-footer">
        <Button v-if="viewMode" @click="modalApi.close()">关闭</Button>
        <template v-else>
          <Button @click="modalApi.close()">取消</Button>
          <Button type="primary" @click="handleSubmit">确认</Button>
        </template>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.form-section {
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  margin-bottom: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2329;
}

.add-row-btn {
  margin-top: 10px;
}

.modal-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  padding-top: 12px;
  margin-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
