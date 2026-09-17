<script lang="ts" setup>
import type {
  Project,
  ProjectDoc,
  ProjectUser,
} from '#/api/energyCarbon/project';

import { computed, nextTick, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import {
  Button,
  Input,
  message,
  Table,
  Upload,
} from 'ant-design-vue';
import dayjs from 'dayjs';

import { useVbenForm } from '#/adapter/form';
import { getAccountingPage } from '#/api/energyCarbon/basicData';
import {
  addProject,
  deleteProjectDoc,
  getProjectDetail,
  updateProject,
  uploadProjectDoc,
} from '#/api/energyCarbon/project';

import UserMultiSelectModal from '../../components/user-multi-select-modal.vue';
import { useBasicFormSchema } from '../data';
import ContactSelectModal from './contact-select-modal.vue';

// 导入 energyCarbon 模块公共样式（修复 disabled 表单看不清）
import '../../style.css';

const emit = defineEmits(['success']);

const viewMode = ref(false);
const editMode = ref(false);
const title = computed<string>(() => {
  if (viewMode.value) return '查看项目信息';
  if (editMode.value) return '编辑项目信息';
  return '新增项目信息';
});

// 用户列表
const users = ref<ProjectUser[]>([]);

// 项目负责人联系人编号（保存用）
const contactId = ref<number>(0);

// 当前选中的行政区（用于用户选择弹窗过滤）
const selectedArea = ref<(number | string)[]>([]);

// 改造类型字典映射
function getReformTypeLabel(value?: string) {
  if (!value) return '-';
  return (
    getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, value) || value
  );
}

// 用户表格列定义
const userColumns = [
  { title: '用户名', dataIndex: 'username', width: 100 },
  {
    title: '身份证号',
    dataIndex: 'idCard',
    width: 120,
    ellipsis: true,
    customCell: (record: any) => ({ title: record.idCard || '-' }),
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    width: 110,
    ellipsis: true,
    customCell: (record: any) => ({ title: record.phone || '-' }),
  },
  { title: '所属行政区划', dataIndex: 'division', width: 180 },
  {
    title: '地址',
    dataIndex: 'address',
    width: 120,
    ellipsis: true,
    customCell: (record: any) => ({ title: record.address || '-' }),
  },
  { title: '改造类别', dataIndex: 'reformType', width: 120 },
  {
    title: '核算周期',
    dataIndex: 'accountingPeriodStart',
    width: 180,
    customRender: ({ record }: any) =>
      record.accountingPeriodStart
        ? `${record.accountingPeriodStart} ~ ${record.accountingPeriodEnd || ''}`
        : '-',
  },
  { title: '基准线排放量(kgCO2e)', dataIndex: 'baselineEmission', width: 130 },
  { title: '实际排放量(kgCO2e)', dataIndex: 'actualEmission', width: 130 },
  { title: '减排量(kgCO2e)', dataIndex: 'reduction', width: 110 },
  // { title: '电力表', dataIndex: 'electricityId', width: 120 },
  // { title: '燃气表', dataIndex: 'gasId', width: 120 },
  {
    title: '操作',
    width: 80,
    key: 'operation',
  },
];

// 文档列表
const documents = ref<ProjectDoc[]>([]);

// 待上传文件（新建项目时暂存）
const pendingFiles = ref<Array<{ docDesc: string; file: File }>>([]);

// 文档表格列定义
const documentColumns = [
  { title: '文档名称', dataIndex: 'docName' },
  { title: '文档描述', dataIndex: 'docDesc' },
  { title: '文档大小', dataIndex: 'docSize' },
  { title: '上传人', dataIndex: 'uploader' },
  { title: '上传时间', dataIndex: 'uploadTime' },
  {
    title: '操作',
    width: 80,
    key: 'operation',
  },
];

// 用户选择弹窗（多选）
const userMultiSelectModalRef = ref<InstanceType<typeof UserMultiSelectModal>>();

// 联系人选择弹窗
const contactSelectModalRef = ref<InstanceType<typeof ContactSelectModal>>();

// 打开联系人选择弹窗
function handleOpenContactModal() {
  if (!contactSelectModalRef.value) {
    message.error('联系人弹窗组件未加载');
    return;
  }
  contactSelectModalRef.value.open();
}

// 选择联系人回填
function handleContactSelect(contact: any) {
  contactId.value = contact.id || 0;
  basicFormApi.setValues({ projectLeader: contact.name || '' });
  message.success(`已选择项目负责人：${contact.name}`);
}

// 打开用户选择弹窗
async function handleOpenUserModal() {
  const values = await basicFormApi.getValues();
  const division = values.division;
  if (!division || division.length === 0) {
    message.warning('请先选择行政区');
    return;
  }
  selectedArea.value = division;
  userMultiSelectModalRef.value?.open();
}

// 选择用户回填（支持多选）
function handleUserSelect(selectedUsers: any[]) {
  let addedCount = 0;
  for (const user of selectedUsers) {
    const exists = users.value.some(
      (u: ProjectUser) => u.carbonUserInfoId === user.id,
    );
    if (exists) {
      continue;
    }
    users.value.push({
      accountingId: user.accountingId || '',
      carbonUserInfoId: user.id,
      username: user.username || '',
      idCard: user.idCard || '',
      phone: user.phone || '',
      division: user.division || '',
      address: user.address || '',
      reformType: user.reformType || '',
      baselineEmission: user.baselineEmission || undefined,
      actualEmission: user.actualEmission || undefined,
      reduction: user.reduction || undefined,
      accountingPeriodStart: user.accountingPeriodStart || '',
      accountingPeriodEnd: user.accountingPeriodEnd || '',
      electricityId: user.electricityId || '',
      gasId: user.gasId || '',
    });
    addedCount++;
  }
  if (addedCount > 0) {
    message.success(`已添加 ${addedCount} 个用户`);
  } else {
    message.warning('所选用户均已添加');
  }
}

// 删除用户行
function removeUserRow(index: number) {
  users.value.splice(index, 1);
}

// 基础信息表单
const [BasicForm, basicFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 140,
  },
  layout: 'horizontal',
  schema: useBasicFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
  handleValuesChange: (
    values: Record<string, any>,
    fieldsChanged: string[],
  ) => {
    if (fieldsChanged.includes('division')) {
      selectedArea.value = values.division || [];
    }
    if (
      (fieldsChanged.includes('planStartDate') ||
        fieldsChanged.includes('planEndDate')) &&
      values.planStartDate &&
      values.planEndDate
    ) {
      const start = dayjs(values.planStartDate);
      const end = dayjs(values.planEndDate);
      if (end.isAfter(start) || end.isSame(start)) {
        const months = end.diff(start, 'month');
        const years = Math.floor(months / 12);
        const remainingMonths = months % 12;
        let cycle = '';
        if (years > 0) cycle += `${years}年`;
        if (remainingMonths > 0) cycle += `${remainingMonths}个月`;
        if (!cycle) cycle = '0个月';
        basicFormApi.setValues({ projectCycle: cycle });
      }
    }
  },
});

// 监听用户减排量变化，自动计算项目减排量
watch(
  () => users.value.map((u: ProjectUser) => u.reduction),
  () => {
    const total = users.value.reduce(
      (sum: number, u: ProjectUser) => sum + (Number(u.reduction) || 0),
      0,
    );
    basicFormApi.setValues({ totalReduction: total });
  },
  { deep: true },
);

// 上传文档前处理
async function handleBeforeUpload(file: any) {
  const values = await basicFormApi.getValues();
  const projectId = values.id;

  const size =
    file.size > 0 ? `${(file.size / 1024 / 1024).toFixed(2)} MB` : '0 MB';

  if (!projectId) {
    pendingFiles.value.push({ file, docDesc: '' });
    documents.value.push({
      docName: file.name,
      docDesc: '',
      docSize: size,
      uploadTime: new Date().toLocaleString('zh-CN'),
    });
    message.success('文档已暂存，保存项目后将自动上传');
    return false;
  }

  try {
    const res = await uploadProjectDoc(file, projectId, '');
    documents.value.push(res);
    message.success('文档上传成功');
  } catch {
    message.error('文档上传失败');
  }
  return false;
}

// 查看文档
function handleViewDocument(docPath?: string) {
  if (docPath) {
    window.open(docPath, '_blank');
  }
}

// 删除文档
async function handleDeleteDocument(index: number) {
  const doc = documents.value[index];
  if (!doc) return;
  const values = await basicFormApi.getValues();
  const projectId = values.id;
  if (doc.id && projectId) {
    try {
      await deleteProjectDoc(projectId, doc.docName || '');
      message.success('文档删除成功');
    } catch {
      message.error('文档删除失败');
      return;
    }
  }
  documents.value.splice(index, 1);
}

const [Modal, modalApi] = useVbenModal({
  title: title as any,
  footer: false,
  width: 1000,
  centered: true,
  class: 'max-w-[1000px]',
  async onConfirm() {
    // 验证表单
    const { valid: basicValid } = await basicFormApi.validate();

    if (!basicValid) {
      return false;
    }

    // 获取表单数据
    const values = await basicFormApi.getValues();

    // 项目负责人校验
    if (!contactId.value) {
      message.warning('请选择项目负责人');
      return false;
    }

    // 用户信息校验：电力表和燃气表二选一
    for (const u of users.value) {
      if (!u.electricityId && !u.gasId) {
        message.warning(
          `用户 ${u.username || ''} 的电力表和燃气表至少填写一项`,
        );
        return false;
      }
    }

    // 构造请求数据（剔除后端不需要的展示字段）
    const data: Project = {
      ...values,
      contactId: contactId.value,
      provinceCode: values.division?.[0],
      cityCode: values.division?.[1],
      districtCode: values.division?.[2],
      userList: users.value.map((u: ProjectUser) => ({
        carbonUserInfoId: u.carbonUserInfoId,
        accountingId: u.accountingId,
        electricityId: u.electricityId,
        gasId: u.gasId,
      })),
    };

    // 删除后端 SaveReqVO 中不存在的字段
    delete (data as any).projectLeader;
    delete (data as any).projectCycle;
    delete (data as any).totalReduction;
    delete (data as any).division;

    let projectId: number;

    try {
      if (values.id) {
        await updateProject(data);
        projectId = values.id;
        message.success('更新成功');
      } else {
        projectId = await addProject(data);
        message.success('创建成功');
      }
      // 新建项目时上传暂存文档
      if (!values.id && pendingFiles.value.length > 0) {
        for (const item of pendingFiles.value) {
          try {
            await uploadProjectDoc(item.file, projectId, item.docDesc);
          } catch {
            // 忽略单个失败
          }
        }
        pendingFiles.value = [];
      }

      emit('success');
      await modalApi.close();
      return true;
    } catch {
      message.error('保存失败');
      return false;
    }
  },
  onCancel() {
    // 关闭时重置状态
    viewMode.value = false;
    users.value = [];
    documents.value = [];
    pendingFiles.value = [];
    contactId.value = 0;
    selectedArea.value = [];
  },
  onOpenChange(isOpen: boolean) {
    if (isOpen) {
      const data = modalApi.getData();
      if (data) {
        viewMode.value = !!data.viewMode;
        const formData = { ...data };
        delete formData.viewMode;
        delete formData.userList;
        delete formData.docs;

        // 兼容后端返回 contactName / contactId
        contactId.value = data.contactId || 0;
        formData.projectLeader = data.contactName || data.projectLeader;

        // 将分开的行政区划代码转换为级联数组格式
        if (data.provinceCode || data.cityCode || data.districtCode) {
          formData.division = [
            data.provinceCode,
            data.cityCode,
            data.districtCode,
          ].filter(Boolean);
          selectedArea.value = formData.division;
        }

        // 设置基础信息
        basicFormApi.setValues(formData);

        // 设置用户列表
        if (data.userList) {
          users.value = [...data.userList];
        }

        // 设置文档列表
        if (data.docs) {
          documents.value = [...data.docs];
        }
      }
    } else {
      viewMode.value = false;
      users.value = [];
      documents.value = [];
      pendingFiles.value = [];
      contactId.value = 0;
      selectedArea.value = [];
    }
  },
});

// 打开模态框
async function open(data?: Record<string, any>) {
  // 重置状态
  viewMode.value = false;
  editMode.value = false;
  users.value = [];
  documents.value = [];
  pendingFiles.value = [];
  contactId.value = 0;
  selectedArea.value = [];

  // 重置表单
  basicFormApi.setValues({});

  // 打开模态框（立即打开，避免等待 API）
  modalApi.open();

  // 等待模态框和表单渲染完成后再设置数据
  await nextTick();

  if (!data) return;

  viewMode.value = !!data.viewMode;

  // 如果有 id 但没有详情数据，先显示 loading 再请求详情
  if (data.id && !data.projectName) {
    modalApi.lock();
    try {
      const detail = await getProjectDetail(data.id);
      data = { ...data, ...detail };
    } catch {
      message.error('获取项目详情失败');
      modalApi.close();
      return;
    } finally {
      modalApi.unlock();
    }
  }

  editMode.value = !!data.id && !data.viewMode;

  const formData = { ...data };
  delete formData.viewMode;
  delete formData.userList;
  delete formData.docs;

  // 兼容后端返回 contactName / contactId
  contactId.value = data.contactId || 0;
  formData.projectLeader = data.contactName || data.projectLeader;

  // 将分开的行政区划代码转换为级联数组格式
  if (data.provinceCode || data.cityCode || data.districtCode) {
    formData.division = [
      data.provinceCode,
      data.cityCode,
      data.districtCode,
    ].filter(Boolean);
    selectedArea.value = formData.division;
  }

  // 设置基础信息
  basicFormApi.setValues(formData);

  // 设置禁用状态
  await basicFormApi.setState({
    commonConfig: {
      componentProps: {
        disabled: viewMode.value,
      },
    },
  });

  // 设置用户列表
  if (data.userList) {
    users.value = [...data.userList];
  }

  // 设置文档列表
  if (data.docs) {
    documents.value = [...data.docs];
  }
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
        <BasicForm>
          <template #projectLeader="slotProps">
            <Input
              :value="slotProps.value"
              disabled
              placeholder="请选择项目负责人"
            >
              <template #suffix>
                <Button
                  type="link"
                  size="small"
                  :disabled="slotProps.disabled"
                  @click="handleOpenContactModal"
                >
                  选择
                </Button>
              </template>
            </Input>
          </template>
        </BasicForm>
      </div>

      <!-- 用户信息 -->
      <div class="form-section">
        <div style="display: flex; align-items: center; margin: 5px 0">
          <h3 class="section-title" style="flex: 1">用户信息</h3>
          <Button
            v-if="!viewMode"
            class="add-row-btn"
            type="primary"
            @click="handleOpenUserModal"
          >
            + 选择用户
          </Button>
        </div>
        <Table
          :columns="userColumns"
          :data-source="users"
          :pagination="false"
          :row-key="(record: any) => record.carbonUserInfoId || record.id"
          :scroll="{ x: 'max-content' }"
          class="energy-carbon-table"
          bordered
          size="small"
        >
          <template #emptyText>暂无用户信息</template>
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.dataIndex === 'username'">
              {{ record.username || '-' }}
            </template>
            <template v-if="column.dataIndex === 'idCard'">
              {{ record.idCard || '-' }}
            </template>
            <template v-if="column.dataIndex === 'phone'">
              {{ record.phone || '-' }}
            </template>
            <template v-if="column.dataIndex === 'division'">
              {{ record.division || '-' }}
            </template>
            <template v-if="column.dataIndex === 'address'">
              {{ record.address || '-' }}
            </template>
            <template v-if="column.dataIndex === 'reformType'">
              {{ getReformTypeLabel(record.reformType) }}
            </template>
            <template v-if="column.dataIndex === 'baselineEmission'">
              {{ record.baselineEmission ?? '-' }}
            </template>
            <template v-if="column.dataIndex === 'actualEmission'">
              {{ record.actualEmission ?? '-' }}
            </template>
            <template v-if="column.dataIndex === 'reduction'">
              {{ record.reduction ?? '-' }}
            </template>
            <template v-if="column.dataIndex === 'electricityId'">
              {{ record.electricityId || '-' }}
            </template>
            <template v-if="column.dataIndex === 'gasId'">
              {{ record.gasId || '-' }}
            </template>
            <template v-if="column.key === 'operation' && !viewMode">
              <Button
                danger
                size="small"
                type="primary"
                @click="removeUserRow(index)"
              >
                删除
              </Button>
            </template>
          </template>
        </Table>
      </div>

      <!-- 项目文档管理 -->
      <div class="form-section">
        <div style="display: flex; align-items: center; margin: 5px 0">
          <h3 class="section-title" style="flex: 1">项目文档管理</h3>
          <Upload
            v-if="!viewMode"
            accept=".doc,.docx,.xls,.xlsx,.pdf,.jpg,.png"
            :before-upload="handleBeforeUpload"
            :show-upload-list="false"
            multiple
          >
            <Button type="primary">上传文档</Button>
          </Upload>
        </div>
        <Table
          :columns="documentColumns"
          :data-source="documents"
          :pagination="false"
          :row-key="(record: any) => record.id"
          bordered
          size="small"
        >
          <template #emptyText>暂无文档信息</template>
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.dataIndex === 'docName'">
              <a
                v-if="record.docPath"
                :href="record.docPath"
                target="_blank"
                class="doc-link"
              >
                {{ record.docName }}
              </a>
              <span v-else>{{ record.docName }}</span>
            </template>
            <template v-if="column.key === 'operation'">
              <Button
                v-if="record.docPath"
                size="small"
                type="link"
                @click="handleViewDocument(record.docPath)"
              >
                查看
              </Button>
              <Button
                v-if="!viewMode"
                danger
                size="small"
                type="primary"
                @click="handleDeleteDocument(index)"
              >
                删除
              </Button>
            </template>
          </template>
        </Table>
      </div>

      <!-- 操作按钮 -->
      <div class="modal-footer">
        <Button v-if="viewMode" @click="modalApi.close()">关闭</Button>
        <template v-else>
          <Button @click="modalApi.close()">取消</Button>
          <Button type="primary" @click="modalApi.onConfirm()">确认</Button>
        </template>
      </div>
    </div>
  </Modal>
  <UserMultiSelectModal
    ref="userMultiSelectModalRef"
    :api="getAccountingPage"
    :province-code="selectedArea[0]"
    :city-code="selectedArea[1]"
    :district-code="selectedArea[2]"
    lock-area
    @confirm="handleUserSelect"
  />
  <ContactSelectModal
    ref="contactSelectModalRef"
    @confirm="handleContactSelect"
  />
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
