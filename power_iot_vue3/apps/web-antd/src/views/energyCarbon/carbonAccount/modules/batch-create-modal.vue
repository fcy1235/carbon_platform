<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import {
  Button,
  DatePicker,
  InputNumber,
  message,
  Select,
  Table,
} from 'ant-design-vue';

import {
  addAccountingBatch,
  getAccountingInitData,
} from '#/api/energyCarbon/carbonAccount';

import FactorSelectModal from './factor-select-modal.vue';
import UnaccountedUserSelectModal from './unaccounted-user-select-modal.vue';

const emit = defineEmits(['success']);

// 活动名称字典选项
const activityNameOptions = getDictOptions(
  DICT_TYPE.ENERGY_CBON_ACTIVITY_TYPE,
  'string',
);

/** 已选用户行（包含后端返回的所有计算数据） */
interface SelectedUserRow {
  id: number;
  username?: string;
  division?: string;
  address?: string;
  reformType?: string;
  dataSource?: string;
  heatingArea?: number;
  baselineIntensity?: number;
  baselineEmission?: number;
  electricityUsage?: number;
  gasUsage?: number;
  activityNameCode?: string;
  activityLevel?: number;
  emissionFactor?: number;
  actualEmission?: number;
  reduction?: number;
  carbonDeviceId?: number;
  deviceList?: Array<{ id: number; deviceName: string; deviceCode: string }>;
}

/** 核算配置（整批统一，可覆盖） */
const config = reactive({
  baselineIntensity: undefined as number | undefined,
  activityNameCode: undefined as string | undefined,
  emissionFactor: undefined as number | undefined,
});

/** 核算周期（取暖季年份） */
const accountingYear = ref<string | undefined>(undefined);

/** 已选用户列表 */
const selectedUsers = ref<SelectedUserRow[]>([]);

// 是否允许配置（筛选出用户后才允许）
const configDisabled = computed(() => selectedUsers.value.length === 0);

// 核算周期展示文本
const periodText = computed(() => {
  if (!accountingYear.value) return '';
  return `${accountingYear.value}-11-15 ~ ${Number(accountingYear.value) + 1}-03-15`;
});

// 字典标签
function getDictText(type: string, value?: string) {
  if (!value) return '-';
  return getDictLabel(type, value) || value;
}

// 获取设备下拉选项（从 record.deviceList 转换）
function getDeviceOptions(record: SelectedUserRow) {
  return (record.deviceList || []).map((d) => ({
    label: d.deviceName || d.deviceCode || `设备${d.id}`,
    value: d.id,
  }));
}

// 用户选择弹窗
const userSelectModalRef = ref<InstanceType<typeof UnaccountedUserSelectModal>>();

// 打开用户选择弹窗（校验核算周期）
function handleOpenUserSelect() {
  if (!accountingYear.value) {
    message.warning('请先选择核算周期年份，再选择用户');
    return;
  }
  userSelectModalRef.value?.open();
}

// 多选用户确认：调用后端批量 init API 获取每个用户的计算数据
async function handleUsersConfirm(users: any[]) {
  if (!users || users.length === 0) return;

  const periodStart = `${accountingYear.value}-11-15`;
  const periodEnd = `${Number(accountingYear.value) + 1}-03-15`;

  // 过滤已存在的用户
  const newUsers = users.filter(
    (user) =>
      user?.id && !selectedUsers.value.some((row) => row.id === user.id),
  );

  if (newUsers.length === 0) return;

  // 校验改造类别一致性
  const reformTypes = new Set(
    [...selectedUsers.value, ...newUsers].map((u) => u.reformType).filter(Boolean),
  );
  if (reformTypes.size > 1) {
    message.warning('所选用户的改造类别必须一致，请重新选择');
    return;
  }

  message.loading({ content: '正在获取用户核算数据...', key: 'batchInit', duration: 0 });

  try {
    // 批量调用 init API
    const userIds = newUsers.map((user) => user.id);
    const initDataList = await getAccountingInitData({
      carbonUserInfoIds: userIds,
      accountingPeriodStart: periodStart,
      accountingPeriodEnd: periodEnd,
    });

    // 构造用户行
    const newRows: SelectedUserRow[] = (initDataList || []).map((data) => ({
      id: data.carbonUserInfoId,
      username: data.username,
      division: data.division,
      address: data.address,
      reformType: data.reformType,
      dataSource: data.dataSource,
      heatingArea: data.heatingArea,
      baselineIntensity: data.baselineIntensity,
      baselineEmission: data.baselineEmission,
      electricityUsage: data.electricityUsage,
      gasUsage: data.gasUsage,
      activityNameCode: data.activityNameCode,
      activityLevel: data.activityLevel,
      emissionFactor: data.emissionFactor,
      actualEmission: data.actualEmission,
      reduction: data.reduction,
      carbonDeviceId: data.deviceList?.[0]?.id,
      deviceList: data.deviceList,
    }));

    selectedUsers.value.push(...newRows);

    // 从第一个用户的数据初始化配置（如果还未设置）
    if (selectedUsers.value.length === newRows.length && newRows[0]) {
      config.baselineIntensity = newRows[0].baselineIntensity;
      config.activityNameCode = newRows[0].activityNameCode;
      config.emissionFactor = newRows[0].emissionFactor;
    }

    message.success({ content: `成功加载 ${newRows.length} 个用户数据`, key: 'batchInit' });
  } catch {
    message.error({ content: '获取用户核算数据失败', key: 'batchInit' });
  }
}

// 移除用户行
function handleRemoveUser(index: number) {
  selectedUsers.value.splice(index, 1);
  if (selectedUsers.value.length === 0) {
    config.baselineIntensity = undefined;
    config.activityNameCode = undefined;
    config.emissionFactor = undefined;
  }
}

// 排放因子选择弹窗
const factorSelectModalRef = ref<InstanceType<typeof FactorSelectModal>>();

// 排放因子选择回填（配置区）
function handleFactorSelect(factor: any) {
  if (factor?.factorValue != null) {
    config.emissionFactor = Number(factor.factorValue);
  }
}

// 重置状态
function resetState() {
  selectedUsers.value = [];
  accountingYear.value = undefined;
  config.baselineIntensity = undefined;
  config.activityNameCode = undefined;
  config.emissionFactor = undefined;
}

// 表格列
const columns = [
  { title: '用户名', dataIndex: 'username', width: 100, ellipsis: true },
  { title: '所属行政区划', dataIndex: 'division', width: 170, ellipsis: true },
  { title: '地址', dataIndex: 'address', width: 160, ellipsis: true },
  { title: '改造类别', dataIndex: 'reformType', width: 100, ellipsis: true },
  { title: '数据来源', dataIndex: 'dataSource', width: 100, ellipsis: true },
  { title: '清洁建筑取暖面积(㎡)', dataIndex: 'heatingArea', width: 140 },
  { title: '基准线排放量(kgCO2e)', dataIndex: 'baselineEmission', width: 150 },
  { title: '关联设备', dataIndex: 'carbonDeviceId', width: 180 },
  { title: '活动水平数据', dataIndex: 'activityLevel', width: 140 },
  { title: '排放因子', dataIndex: 'emissionFactor', width: 120 },
  { title: '实际排放量(kgCO2e)', dataIndex: 'actualEmission', width: 140 },
  { title: '减排量(kgCO2e)', dataIndex: 'reduction', width: 130 },
  { title: '操作', key: 'operation', width: 70, fixed: 'right' },
];

// 提交批量创建
async function handleSubmit() {
  if (selectedUsers.value.length === 0) {
    message.warning('请先选择用户');
    return;
  }
  if (!accountingYear.value) {
    message.warning('请选择核算周期年份');
    return;
  }

  const periodStart = `${accountingYear.value}-11-15`;
  const periodEnd = `${Number(accountingYear.value) + 1}-03-15`;

  const payload = selectedUsers.value.map((row) => ({
    carbonUserInfoId: row.id,
    accountingPeriodStart: periodStart,
    accountingPeriodEnd: periodEnd,
    heatingArea: row.heatingArea,
    baselineIntensity: row.baselineIntensity ?? config.baselineIntensity,
    baselineEmission: row.baselineEmission,
    activities: [
      {
        activityNameCode: row.activityNameCode ?? config.activityNameCode,
        activityLevel: row.activityLevel,
        emissionFactor: row.emissionFactor ?? config.emissionFactor,
        emission: row.actualEmission,
        carbonDeviceId: row.carbonDeviceId ?? null,
      },
    ],
  }));

  modalApi.lock();
  try {
    await addAccountingBatch(payload);
    message.success(`批量保存成功，共 ${payload.length} 条`);
    emit('success');
    modalApi.close();
  } catch {
    message.error('批量保存失败');
  } finally {
    modalApi.unlock();
  }
}

const [Modal, modalApi] = useVbenModal({
  title: '批量新增碳排放核算',
  footer: false,
  width: 1170,
  centered: true,
  class: 'max-w-[1170px]',
  onCancel() {
    resetState();
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      resetState();
    }
  },
});

// 打开弹框
function open() {
  resetState();
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal>
    <div class="batch-create-body">
      <!-- 用户选择弹窗（独立组件，调用 getUnaccountedUserPage） -->
      <UnaccountedUserSelectModal
        ref="userSelectModalRef"
        :accounting-period-start="accountingYear ? `${accountingYear}-11-15` : ''"
        :accounting-period-end="accountingYear ? `${Number(accountingYear) + 1}-03-15` : ''"
        @confirm="handleUsersConfirm"
      />

      <!-- 排放因子选择弹窗 -->
      <FactorSelectModal ref="factorSelectModalRef" @confirm="handleFactorSelect" />

      <!-- 用户信息筛选 -->
      <div class="form-section">
        <h3 class="section-title">用户信息筛选</h3>
        <div class="filter-row">
          <span class="period-label">核算周期（取暖季）：</span>
          <DatePicker
            v-model:value="accountingYear"
            picker="year"
            value-format="YYYY"
            placeholder="请选择核算年份"
            style="width: 140px"
          />
          <span v-if="periodText" class="period-text">{{ periodText }}</span>
          <Button
            type="primary"
            :disabled="!accountingYear"
            @click="handleOpenUserSelect"
          >
            选择用户
          </Button>
          <span class="selected-tip">
            已选择
            <span class="count">{{ selectedUsers.length }}</span>
            个用户
          </span>
        </div>
      </div>

      <!-- 碳核算配置（选中用户后可配置，整批统一） -->
      <div class="form-section">
        <h3 class="section-title">碳核算配置</h3>
        <div>
          <div class="config-row" >
            <span class="config-label">区域基准碳排放强度：</span>
            <InputNumber
              v-model:value="config.baselineIntensity"
              :disabled="configDisabled"
              :min="0"
              placeholder="按所选用户行政区带出，可修改"
              style="width: 200px"
            />
            <span class="config-label">活动名称：</span>
            <Select
              v-model:value="config.activityNameCode"
              :disabled="configDisabled"
              :options="activityNameOptions"
              placeholder="请选择活动名称"
              allow-clear
              style="width: 180px"
            />
            <span class="config-label">排放因子：</span>
            <InputNumber
              v-model:value="config.emissionFactor"
              :disabled="configDisabled"
              :min="0"
              placeholder="请选择排放因子"
              style="width: 150px"
            />
            <Button
              type="link"
              :disabled="configDisabled"
              @click="factorSelectModalRef?.open()"
            >
              选择
            </Button>
          </div>
          <div v-if="configDisabled" class="config-hint">
            请先筛选并选择用户，再进行碳核算配置
          </div>
        </div>
      </div>

      <!-- 用户信息列表 -->
      <div class="form-section">
        <h3 class="section-title">用户信息列表</h3>
        <Table
          :columns="columns"
          :data-source="selectedUsers"
          :pagination="false"
          :row-key="(record: any) => record.id"
          :scroll="{ x: 1500, y: 320 }"
          class="energy-carbon-table"
          bordered
                  >
          <template #emptyText>请先点击「选择用户」添加需要核算的用户</template>
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.dataIndex === 'reformType'">
              {{ getDictText(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, record.reformType) }}
            </template>
            <template v-if="column.dataIndex === 'dataSource'">
              {{ getDictText(DICT_TYPE.ENERGY_CBON_DATA_SOURCE, record.dataSource) }}
            </template>
            <template v-if="column.dataIndex === 'baselineEmission'">
              {{ record.baselineEmission }}
            </template>
            <template v-if="column.dataIndex === 'carbonDeviceId'">
              <Select
                v-model:value="record.carbonDeviceId"
                :options="getDeviceOptions(record)"
                :placeholder="
                  (record.deviceList || []).length > 0
                    ? '请选择设备'
                    : '暂无碳设备'
                "
                allow-clear
                style="width: 100%"
              />
            </template>
            <template v-if="column.dataIndex === 'activityLevel'">
              <InputNumber
                v-model:value="record.activityLevel"
                :min="0"
                placeholder="请输入"
                style="width: 100%"
              />
            </template>
            <template v-if="column.dataIndex === 'emissionFactor'">
              {{ record.emissionFactor }}
            </template>
            <template v-if="column.dataIndex === 'actualEmission'">
              {{ record.actualEmission }}
            </template>
            <template v-if="column.dataIndex === 'reduction'">
              {{ record.reduction }}
            </template>
            <template v-if="column.key === 'operation'">
              <Button
                danger
                type="primary"
                @click="handleRemoveUser(index)"
              >
                移除
              </Button>
            </template>
          </template>
        </Table>
      </div>

      <!-- 操作按钮 -->
      <div class="modal-footer">
        <Button @click="modalApi.close()">取消</Button>
        <Button
          type="primary"
          :disabled="selectedUsers.length === 0"
          @click="handleSubmit"
        >
          批量保存
        </Button>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.form-section {
  padding-bottom: 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
}

.filter-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.selected-tip {
  font-size: 14px;
  font-weight: 500;
  color: #1890ff;
}

.selected-tip .count {
  font-weight: 600;
}

.period-label {
  font-size: 14px;
  font-weight: 500;
  color: #646a73;
}

.period-text {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.config-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.config-row.config-disabled {
  opacity: 0.5;
}

.config-label {
  font-size: 14px;
  font-weight: 600;
  color: #1f2329;
  white-space: nowrap;
}

.config-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #ffa940;
}

:deep(.ant-table) {
  font-size: 13px;
  font-weight: 500;
}

:deep(.ant-table-thead > tr > th) {
  padding: 5px 8px !important;
  font-weight: 600;
  background-color: #f8f9fa;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 4px 6px !important;
}

.modal-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>
