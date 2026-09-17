<script lang="ts" setup>
import type { Activities } from '#/api/energyCarbon/carbonAccount';

import { computed, nextTick, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import {
  Button,
  DatePicker,
  Input,
  InputNumber,
  message,
  Select,
  Table,
} from 'ant-design-vue';
import dayjs from 'dayjs';

import { useVbenForm } from '#/adapter/form';
import {
  addAccounting,
  getAccountingDetail,
  getAccountingInitData,
  updateAccounting,
} from '#/api/energyCarbon/carbonAccount';
import { getDeviceSimpleList } from '#/api/energyCarbon/device';

import { useAccountingFormSchema, useBaselineFormSchema } from '../data';
import FactorSelectModal from '../modules/factor-select-modal.vue';
import UnaccountedUserSelectModal from './unaccounted-user-select-modal.vue';

const props = defineProps<{
  model?: Record<string, any>;
  viewMode?: boolean;
}>();

const emit = defineEmits(['success']);

const viewMode = ref(!!props.viewMode);
const isEditMode = ref(false);
const title = computed(() => {
  if (viewMode.value) return '查看碳排放核算';
  return isEditMode.value ? '编辑碳排放核算' : '新增碳排放核算';
});

const userSelectModalRef = ref<InstanceType<typeof UnaccountedUserSelectModal>>();
const factorSelectModalRef = ref<InstanceType<typeof FactorSelectModal>>();
const currentEditActivityIndex = ref<number>(-1); // 当前正在编辑排放因子的活动行索引

// 核算周期年份（响应式，用于控制选择用户按钮禁用状态）
const accountingYear = ref<string | undefined>(undefined);

// 四舍五入工具（默认保留3位小数）
function roundNum(value: number, digits = 3) {
  const factor = 10 ** digits;
  return Math.round(value * factor) / factor;
}

// 处理用户选择回填：调用后端 init-data 接口一次性获取所有计算数据
async function handleUserSelect(users: any[]) {
  const user = users?.[0];
  if (!user) return;

  try {
    const dataList = await getAccountingInitData({
      carbonUserInfoIds: [user.id],
      accountingPeriodStart: accountingPeriodStart.value,
      accountingPeriodEnd: accountingPeriodEnd.value,
    });
    const data = dataList?.[0];
    if (!data) {
      message.error('获取核算初始化数据失败');
      return;
    }

    // 回填基础信息表单
    basicFormApi.setValues({
      carbonUserInfoId: data.carbonUserInfoId,
      username: data.username || '',
      idCard: data.idCard || '',
      phone: data.phone || '',
      division: data.division,
      address: data.address || '',
      reformType: data.reformType || '',
      heatingArea: data.heatingArea || undefined,
      electricityUsage: data.electricityUsage || undefined,
      gasUsage: data.gasUsage || undefined,
      reduction: data.reduction ?? undefined,
    });

    // 设置活动列表
    activities.value = [
      {
        activityName: data.activityNameCode,
        activityNameCode: data.activityNameCode,
        activityLevel: data.activityLevel ?? undefined,
        emissionFactor: data.emissionFactor ?? undefined,
        emission: data.actualEmission ?? 0,
      },
    ];

    // 回填基准线排放量表单
    baselineFormApi.setValues({
      baselineIntensity: data.baselineIntensity,
      baselineEmission: data.baselineEmission,
    });

    // 检查区域基准碳排放强度是否存在
    if (data.baselineIntensity == null) {
      message.warning('当前行政区划区域基准碳排放强度不存在，无法进行碳核算，请先新增！');
    }

    // 设置设备下拉选项（后端已返回设备列表，无需额外请求）
    loadDeviceOptions(undefined, data.deviceList);
  } catch {
    message.error('获取核算初始化数据失败');
  }
}

// 打开排放因子选择弹窗
function handleOpenFactorSelect(index: number) {
  currentEditActivityIndex.value = index;
  factorSelectModalRef.value?.open();
}

// 处理排放因子选择回填
function handleFactorSelect(factor: any) {
  const index = currentEditActivityIndex.value;
  if (index >= 0 && index < activities.value.length) {
    activities.value[index].emissionFactor = factor.factorValue || 0;
    // 同时更新活动名称（如果有需要）
    // activities.value[index].activityName = factor.factorName || activities.value[index].activityName;
  }
  currentEditActivityIndex.value = -1;
}

// 活动列表
const activities = ref<
  Array<{
    accountingId?: number;
    activityLevel?: number;
    activityName?: string;
    activityNameCode?: string;
    carbonDeviceId?: string;
    carbonDeviceName?: string;
    emission?: number;
    emissionFactor?: number;
    id?: number;
  }>
>([]);

// 活动名称字典选项
const activityNameOptions = getDictOptions(
  DICT_TYPE.ENERGY_CBON_ACTIVITY_TYPE,
  'string',
);

// 活动表格列定义
const activityColumns = [
  { title: '活动名称', dataIndex: 'activityName', width: 160 },
  { title: '关联设备', dataIndex: 'carbonDeviceId', width: 170 },
  { title: '活动水平数据', dataIndex: 'activityLevel', width: 130 },
  { title: '排放因子', dataIndex: 'emissionFactor', width: 110 },
  { title: '排放量(kgCO2e)', dataIndex: 'emission', width: 140 },
  { title: '操作', key: 'operation', width: 80 },
];

// 关联设备下拉选项：根据当前核算用户加载（仅 isCarbon=1 的碳设备）
const deviceOptions = ref<Array<{ label: string; value: number }>>([]);

// 加载用户关联的碳设备下拉选项
// serverDevices: 由 init-data 接口返回的设备列表（无需额外请求）
// carbonUserInfoId: 编辑/查看模式下通过 API 加载
function loadDeviceOptions(
  carbonUserInfoId?: null | number | string,
  serverDevices?: Array<{ deviceCode?: string; deviceName?: string; id: number }>,
) {
  deviceOptions.value = [];

  if (serverDevices) {
    // 使用 init-data 接口返回的设备列表
    const options = (serverDevices || []).map((item) => ({
      label: item.deviceName || item.deviceCode || `设备${item.id}`,
      value: item.id as number,
    }));
    // 保留已选中但不在列表中的设备（编辑/查看回显）
    mergeSelectedDevices(options);
    return;
  }

  if (!carbonUserInfoId) {
    return;
  }

  // 编辑/查看模式：通过 API 加载设备列表
  getDeviceSimpleList({
    carbonUserInfoId: Number(carbonUserInfoId),
    isCarbon: 1,
  })
    .then((list) => {
      const options = (list || []).map((item) => ({
        label: item.deviceName || item.deviceCode || `设备${item.id}`,
        value: item.id as number,
      }));
      mergeSelectedDevices(options);
    })
    .catch(() => {
      deviceOptions.value = [];
    });
}

// 合并已选中设备并自动选择第一个设备
function mergeSelectedDevices(options: Array<{ label: string; value: number }>) {
  const optionIds = new Set(options.map((o) => o.value));
  activities.value.forEach((row) => {
    if (
      row.carbonDeviceId != null &&
      !optionIds.has(row.carbonDeviceId) &&
      row.carbonDeviceName
    ) {
      options.push({
        label: row.carbonDeviceName,
        value: row.carbonDeviceId,
      });
    }
  });
  deviceOptions.value = options;
  // 自动选择第一个设备（仅对未设置设备的行）
  if (options.length > 0) {
    activities.value.forEach((row) => {
      if (row.carbonDeviceId == null) {
        row.carbonDeviceId = options[0]!.value;
      }
    });
  }
}

// 关联设备列显示文本（查看模式）
function getDeviceLabel(row: any) {
  if (row?.carbonDeviceId == null || row?.carbonDeviceId === '') {
    return '-';
  }
  return (
    row?.carbonDeviceName ||
    deviceOptions.value.find((o) => o.value === row.carbonDeviceId)?.label ||
    String(row.carbonDeviceId)
  );
}

// 新增活动行
function handleAddActivityRow() {
  activities.value.push({
    activityName: undefined,
    activityLevel: undefined,
    emissionFactor: undefined,
    emission: 0,
  });
}

// 删除活动行
function handleDeleteActivityRow(index: number) {
  activities.value.splice(index, 1);
}

// 监听活动数据变化，自动计算排放量
watch(
  activities,
  () => {
    activities.value.forEach((item) => {
      const newEmission = roundNum(
        (item.activityLevel || 0) * (item.emissionFactor || 0),
        3,
      );
      if (item.emission !== newEmission) {
        item.emission = newEmission;
      }
    });
  },
  { deep: true },
);

// 基础信息表单
const [BasicForm, basicFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 140,
  },
  layout: 'horizontal',
  schema: useAccountingFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

// 核算周期年份变化时同步到基础表单
watch(
  () => accountingYear.value,
  (val) => {
    basicFormApi.setValues({ accountingPeriodYear: val });
  },
);

// 基准线排放量表单
const [BaselineForm, baselineFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 140,
  },
  layout: 'horizontal',
  schema: useBaselineFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

// 计算实际排放量（保留3位小数）
const actualEmissions = computed(() => {
  return roundNum(
    activities.value.reduce((sum, item) => sum + (item.emission || 0), 0),
    3,
  );
});

// // 自动计算减排量 = 基准线排放量 - 实际排放量
// watch(
//   [() => baselineFormApi.getValues().baselineEmission, actualEmissions],
//   ([baselineEmission, actual]) => {
//     const reduction = roundNum((baselineEmission || 0) - (actual || 0));
//     basicFormApi.setValues({ reduction });
//   },
// );

// 核算周期对应的开始/结束日期
const accountingPeriodStart = computed(() => {
  if (!accountingYear.value) return '';
  return `${accountingYear.value}-11-15`;
});
const accountingPeriodEnd = computed(() => {
  if (!accountingYear.value) return '';
  return `${Number(accountingYear.value) + 1}-03-15`;
});

// 打开用户选择弹窗（核算周期校验）
function handleOpenUserSelect() {
  if (!accountingYear.value) {
    message.warning('请先选择核算周期年份，再选择用户');
    return;
  }
  userSelectModalRef.value?.open();
}

// 确认提交
async function handleConfirm() {
  // 验证表单
  const { valid: basicValid } = await basicFormApi.validate();

  if (!basicValid) {
    return;
  }

  // 获取表单数据（getValues返回Promise）
  const basicData: any = (await basicFormApi.getValues()) || {};
  const baselineData: any = (await baselineFormApi.getValues()) || {};

  // 检查表单数据
  if (!basicData) {
    message.error('无法获取表单数据');
    return;
  }

  // 提交前检查区域基准碳排放强度是否存在
  const baselineIntensity = baselineData.baselineIntensity;
  if (!baselineIntensity && baselineIntensity !== 0) {
    message.warning('当前行政区划区域基准碳排放强度不存在，无法进行碳核算，请先新增！');
    return;
  }

  // 构建提交数据
  const submitData: any = {
    id: basicData?.id || undefined,
    carbonUserInfoId: basicData.carbonUserInfoId,
    accountingPeriodStart: basicData.accountingPeriodYear
      ? `${basicData.accountingPeriodYear}-11-15`
      : undefined,
    accountingPeriodEnd: basicData.accountingPeriodYear
      ? `${Number(basicData.accountingPeriodYear) + 1}-03-15`
      : undefined,
    baselineEmission: baselineData.baselineEmission,
    baselineIntensity: baselineData.baselineIntensity,
    electricityUsage: basicData.electricityUsage,
    gasUsage: basicData.gasUsage,
    activities: (activities.value || []).map((item: any) => ({
      activityNameCode: item?.activityNameCode || item?.activityName || '',
      activityLevel: item?.activityLevel,
      emissionFactor: item?.emissionFactor,
      emission: item?.emission,
      carbonDeviceId: item?.carbonDeviceId ?? null,
      accountingId: basicData?.id,
      ...(item?.id && typeof item?.id === 'number'
        ? { id: item.id }
        : undefined),
    })),
  };

  modalApi.lock();
  try {
    // 调用真实 API
    await (submitData.id
      ? updateAccounting(submitData)
      : addAccounting(submitData));
    message.success('保存成功');
    emit('success');
    modalApi.close();
  } catch {
    message.error('保存失败');
  } finally {
    modalApi.unlock();
  }
}

const [Modal, modalApi] = useVbenModal({
  title: title as any,
  footer: false,
  width: 1000,
  centered: true,
  class: 'max-w-[1000px]',
  onCancel() {
    // 关闭时重置状态
    viewMode.value = false;
    isEditMode.value = false;
    activities.value = [];
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      viewMode.value = false;
      isEditMode.value = false;
      activities.value = [];
    }
  },
});

// 打开模态框
async function open(data?: Record<string, any>) {
  // 重置状态
  activities.value = [];
  deviceOptions.value = [];
  isEditMode.value = false;
  viewMode.value = !!(data?.viewMode);
  accountingYear.value = undefined;

  // 先打开弹框，避免等待数据
  modalApi.open();

  // 等待弹框及表单渲染完成后再回填数据
  await nextTick();

  if (!data) {
    return;
  }

  // 复制数据，避免修改传入对象
  const payload = { ...data };
  delete payload.viewMode;

  // 如果有 accountingId，说明是编辑或查看，调用接口获取详情
  if (payload.accountingId) {
    isEditMode.value = true;
    modalApi.lock();
    try {
      const apiData = await getAccountingDetail(payload.accountingId);
      if (apiData) {
        // 设置基础信息
        const formValues: any = { ...apiData };
        // 处理核算周期 - 提取年份
        if (formValues.accountingPeriodStart) {
          formValues.accountingPeriodYear = dayjs(formValues.accountingPeriodStart).format('YYYY');
        }
        delete formValues.accountingPeriodStart;
        delete formValues.accountingPeriodEnd;
        basicFormApi.setValues(formValues);
        accountingYear.value = formValues.accountingPeriodYear;

        // 设置基准线排放量表单
        baselineFormApi.setValues({
          heatingArea: apiData.heatingArea,
          baselineIntensity: apiData.baselineIntensity,
          baselineEmission: apiData.baselineEmission,
        });

        // 编辑时检查区域基准碳排放强度是否存在
        if (!apiData.baselineIntensity && !viewMode.value) {
          message.warning('当前行政区划区域基准碳排放强度不存在，无法进行碳核算，请先新增！');
        }

        // 设置活动列表
        let activitiesData: any[] = [];
        if (apiData.activities) {
          // 处理可能的JSON字符串
          if (typeof apiData.activities === 'string') {
            try {
              activitiesData = JSON.parse(apiData.activities);
            } catch {
              activitiesData = [];
            }
          } else if (Array.isArray(apiData.activities)) {
            activitiesData = apiData.activities;
          }
        }

        activities.value = activitiesData
          .filter(
            (item: any) =>
              item && (item.activityNameCode || item.activityName),
          )
          .map((item: Activities) => ({
            activityName: item?.activityName || item?.activityNameCode || '',
            activityLevel: item.activityLevel,
            emissionFactor: item.emissionFactor,
            emission: (item.activityLevel || 0) * (item.emissionFactor || 0),
            carbonDeviceId: item?.carbonDeviceId,
            carbonDeviceName: item?.carbonDeviceName || '',
            accountingId: item.accountingId,
            id: item?.id || '',
          }));

        // 加载该用户可关联的碳设备下拉选项
        void loadDeviceOptions(apiData.carbonUserInfoId);
      }
    } catch (error: any) {
      console.error('获取碳排放核算详情失败:', error);
      message.error('获取碳排放核算详情失败');
    } finally {
      modalApi.unlock();
    }
    return;
  }

  // 设置基础信息
  const formValues: any = { ...payload };
  // 处理核算周期 - 提取年份
  if (formValues.accountingPeriodStart) {
    formValues.accountingPeriodYear = dayjs(formValues.accountingPeriodStart).format('YYYY');
  }
  delete formValues.accountingPeriodStart;
  delete formValues.accountingPeriodEnd;
  basicFormApi.setValues(formValues);
  accountingYear.value = formValues.accountingPeriodYear;

  // 设置基准线排放量表单
  baselineFormApi.setValues({
    heatingArea: payload.heatingArea,
    baselineIntensity: payload.baselineIntensity,
    baselineEmission: payload.baselineEmission,
  });

  // 设置活动列表
  let activitiesData: any[] = [];
  if (payload.activities) {
    // 处理可能的JSON字符串
    if (typeof payload.activities === 'string') {
      try {
        activitiesData = JSON.parse(payload.activities);
      } catch {
        activitiesData = [];
      }
    } else if (Array.isArray(payload.activities)) {
      activitiesData = payload.activities;
    }
  }

  activities.value = activitiesData
    .filter(
      (item: any) => item && (item.activityNameCode || item.activityName),
    )
    .map((item: any) => ({
      activityNameCode: item?.activityName || item?.activityNameCode || '',
      activityLevel: item.activityLevel,
      emissionFactor: item.emissionFactor,
      emission: (item.activityLevel || 0) * (item.emissionFactor || 0),
      carbonDeviceId: item?.carbonDeviceId,
      carbonDeviceName: item?.carbonDeviceName || '',
      accountingId: item.accountingId || '',
      id: item?.id || '',
    }));

  // 加载该用户可关联的碳设备下拉选项
  void loadDeviceOptions(payload.carbonUserInfoId);
}

defineExpose({
  open,
});
</script>

<template>
  <Modal>
    <div>
      <!-- 用户选择弹窗（独立组件，调用 getUnaccountedUserPage） -->
      <UnaccountedUserSelectModal
        ref="userSelectModalRef"
        :accounting-period-start="accountingPeriodStart"
        :accounting-period-end="accountingPeriodEnd"
        :multiple="false"
        @confirm="handleUserSelect"
      />

      <!-- 排放因子选择弹窗 -->
      <FactorSelectModal ref="factorSelectModalRef" @confirm="handleFactorSelect" />

      <!-- 基础信息 -->
      <div class="form-section">
        <h3 class="section-title">基础信息</h3>
        <BasicForm>
          <template #username="slotProps">
            <Input :value="slotProps.value" disabled placeholder="请选择用户">
              <template #suffix>
                <Button
                  type="link"
                  size="small"
                  :disabled="viewMode || !accountingYear"
                  @click="handleOpenUserSelect"
                >
                  选择
                </Button>
              </template>
            </Input>
          </template>
          <template #accountingPeriodYear="slotProps">
            <div style="display: flex; align-items: center; gap: 8px; width: 100%;">
              <DatePicker
                v-model:value="accountingYear"
                :disabled="viewMode"
                picker="year"
                value-format="YYYY"
                placeholder="请选择核算年份"
                style="width: 150px;"
              />
              <span
                v-if="accountingYear || slotProps.value"
                style="color: #666; white-space: nowrap; font-size: 13px;"
              >
                {{ accountingYear || slotProps.value }}-11-15 ~ {{ Number(accountingYear || slotProps.value) + 1 }}-03-15
              </span>
            </div>
          </template>
        </BasicForm>
      </div>

      <!-- 基准线排放量 -->
      <div class="form-section">
        <h3 class="section-title">基准线排放量</h3>
        <BaselineForm />
      </div>

      <!-- 实际排放量 -->
      <div class="form-section">
        <h3 class="section-title">实际排放量</h3>
        <div
          class="emissions-summary"
          style="display: flex; align-items: center"
        >
          <div style="flex: 1">
            <span class="emissions-label">实际排放量：</span>
            <span class="emissions-value">{{ actualEmissions }} kgCO2e</span>
          </div>
          <Button
            v-if="!viewMode"
            class="add-row-btn"
            type="primary"
            @click="handleAddActivityRow"
          >
            + 新增活动
          </Button>
        </div>

        <Table
          :columns="activityColumns"
          :data-source="activities"
          :pagination="false"
          :row-key="
            (record: any) => record.id || record.activityName || Math.random()
          "
          :scroll="{ x: 'max-content' }"
          class="energy-carbon-table"
          bordered
          size="small"
        >
          <template #emptyText>暂无活动数据</template>
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.dataIndex === 'activityName'">
              <Select
                v-if="!viewMode"
                v-model:value="record.activityName"
                :options="activityNameOptions as any"
                placeholder="请选择"
                size="small"
                style="width: 100%"
              />
              <span
                v-else
                :title="
                  activityNameOptions.find(
                    (o: any) => o.value === record.activityName,
                  )?.label ||
                  record.activityName ||
                  '-'
                "
              >
                {{
                  activityNameOptions.find(
                    (o: any) => o.value === record.activityName,
                  )?.label ||
                  record.activityName ||
                  '-'
                }}
              </span>
            </template>
            <template v-if="column.dataIndex === 'activityLevel'">
              <InputNumber
                v-if="!viewMode"
                v-model:value="record.activityLevel"
                :min="0"
                placeholder="请输入"
                size="small"
                style="width: 100%"
              />
              <span v-else :title="String(record.activityLevel ?? '-')">{{
                record.activityLevel ?? '-'
              }}</span>
            </template>
            <template v-if="column.dataIndex === 'emissionFactor'">
              <div v-if="!viewMode" style="display: flex; align-items: center; gap: 4px">
                <InputNumber
                  v-model:value="record.emissionFactor"
                  :min="0"
                  disabled
                  placeholder="请选择"
                  size="small"
                  style="flex: 1"
                />
                <Button
                  type="link"
                  size="small"
                  @click="handleOpenFactorSelect(index)"
                >
                  选择
                </Button>
              </div>
              <span v-else :title="String(record.emissionFactor ?? '-')">{{
                record.emissionFactor ?? '-'
              }}</span>
            </template>
            <template v-if="column.dataIndex === 'emission'">
              <InputNumber
                v-if="!viewMode"
                v-model:value="record.emission"
                :min="0"
                disabled
                placeholder="自动计算"
                size="small"
                style="width: 100%"
              />
              <span v-else :title="String(record.emission ?? '-')">{{
                record.emission ?? '-'
              }}</span>
            </template>
            <template v-if="column.dataIndex === 'carbonDeviceId'">
              <Select
                v-if="!viewMode"
                v-model:value="record.carbonDeviceId"
                :options="deviceOptions"
                :placeholder="deviceOptions.length > 0 ? '请选择设备' : '请先选择用户'"
                allow-clear
                size="small"
                style="width: 100%"
              />
              <span v-else :title="getDeviceLabel(record)">{{
                getDeviceLabel(record)
              }}</span>
            </template>
            <template v-if="column.key === 'operation' && !viewMode">
              <Button
                danger
                size="small"
                type="primary"
                @click="handleDeleteActivityRow(index)"
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
          <Button type="primary" @click="handleConfirm">确认</Button>
        </template>
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

.activity-actions {
  margin-top: 16px;
  text-align: right;
}

.emissions-summary {
  padding: 12px;
  margin-bottom: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.emissions-label {
  font-weight: 500;
  color: #646a73;
}

.emissions-value {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.modal-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>
