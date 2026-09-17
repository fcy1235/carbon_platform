<script lang="ts" setup>
import { computed, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createFactorLib,
  getFactorLib,
  getFactorLibCode,
  updateFactorLib,
} from '#/api/energyCarbon/factorLib';
import { getGasInfoList } from '#/api/energyCarbon/gasInfo';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>({});
const viewMode = ref(false);
const gasOptionsSource = ref<any[]>([]);
const selectedGasIds = ref<number[]>([]);
const isLoadingGas = ref(false);

const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看排放因子';
  }
  return formData.value?.id ? '编辑排放因子' : '新增排放因子';
});

const gasOptions = computed(() => {
  const gasCodes = new Set(
    gasListData.value
      .filter((gas) => gas && gas.gasCode)
      .map((gas) => gas.gasCode),
  );
  return gasOptionsSource.value.map((item) => ({
    ...item,
    disabled: gasCodes.has(item.gasCode),
  }));
});

async function loadGasOptions() {
  isLoadingGas.value = true;
  try {
    const res = await getGasInfoList();
    gasOptionsSource.value = (res || []).map((item: any) => ({
      value: item.id,
      gasCode: item.gasCode,
      label: item.gasName,
      gwp: item.gwp,
      category: item.category,
    }));
  } catch {
    message.error('加载气体列表失败');
  } finally {
    isLoadingGas.value = false;
  }
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    labelWidth: 120,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const gasListData = ref<any[]>([]);

const columns1 = [
  {
    title: '气体名称',
    dataIndex: 'gasName',
    key: 'gasName',
  },
  {
    title: '全球变暖潜能值(GWP)',
    dataIndex: 'gwp',
    key: 'gwp',
  },
  {
    title: '所属类别',
    dataIndex: 'category',
    key: 'category',
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
  },
];
const columns2 = [
  {
    title: '气体名称',
    dataIndex: 'gasName',
    key: 'gasName',
  },
  {
    title: '全球变暖潜能值(GWP)',
    dataIndex: 'gwp',
    key: 'gwp',
  },
  {
    title: '所属类别',
    dataIndex: 'category',
    key: 'category',
  },
];

function handleAddGas() {
  if (selectedGasIds.value.length === 0) {
    message.warning('请选择要添加的气体');
    return;
  }

  const existingGasCodes = new Set(gasListData.value.map((gas) => gas.gasCode));

  const gasesToAdd = gasOptionsSource.value.filter(
    (opt) =>
      selectedGasIds.value.includes(opt.value) &&
      !existingGasCodes.has(opt.gasCode),
  );

  if (gasesToAdd.length === 0) {
    message.warning('所选气体已在列表中');
    return;
  }

  const newRows = gasesToAdd.map((gas) => ({
    id: gas.gasCode,
    gasCode: gas.gasCode,
    gasName: gas.label,
    gwp: gas.gwp,
    category: gas.category,
  }));

  gasListData.value = [...gasListData.value, ...newRows];

  selectedGasIds.value = [];
  message.success('添加成功');
}

function handleDeleteGas(row: any) {
  gasListData.value = gasListData.value.filter((item) => item.id !== row.id);
}

// 同步排放因子值状态（有气体时自动计算GWP总和并禁用，无气体时恢复手动输入）
function syncFactorValueState() {
  const hasGases = gasListData.value.length > 0;
  const sumGwp = gasListData.value.reduce(
    (acc, g) => acc + (Number(g.gwp) || 0),
    0,
  );

  if (hasGases) {
    formApi.setFieldValue('factorValue', sumGwp);
  }

  formApi.updateSchema([
    {
      fieldName: 'factorValue',
      componentProps: {
        disabled: viewMode.value || hasGases,
      },
    },
  ]);
}

// 监听气体列表变化，自动同步排放因子值
watch(
  gasListData,
  () => {
    syncFactorValueState();
  },
  { deep: true },
);

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  width: 1000,
  class: 'max-w-[1000px]',
  footer: computed(() => !viewMode.value),
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    const data: any = await formApi.getValues();
    const originalData: any = formData.value;
    data.gasList = gasListData.value.map((item: any) => ({
      gasCode: item.gasCode,
    }));

    try {
      await (originalData?.id ? updateFactorLib(data) : createFactorLib(data));
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = {};
      viewMode.value = false;
      selectedGasIds.value = [];
      return;
    }
    await loadGasOptions();
    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      let formValues = data || {};

      if (data.id) {
        try {
          const res = await getFactorLib(data.id);
          if (res) {
            const apiData = res;
            formValues = {
              id: apiData.id,
              factorCode: apiData.factorCode,
              factorName: apiData.factorName,
              relatedDoc: apiData.relatedDoc,
              emissionSource: apiData.emissionSource,
              unit: apiData.unit,
              factorValue: apiData.factorValue,
            };

            if (apiData.gasList) {
              let gasListArray: any[] = [];
              try {
                gasListArray =
                  typeof apiData.gasList === 'string'
                    ? JSON.parse(apiData.gasList)
                    : apiData.gasList;
              } catch {
                gasListArray = [];
              }

              if (Array.isArray(gasListArray)) {
                const seenGasCodes = new Set();
                gasListData.value = gasListArray
                  .filter((gas: any) => {
                    if (!gas || !gas.gasCode) return false;
                    if (seenGasCodes.has(gas.gasCode)) return false;
                    seenGasCodes.add(gas.gasCode);
                    return true;
                  })
                  .map((gas: any) => ({
                    id: gas.gasCode,
                    gasCode: gas.gasCode,
                    gasName: gas.gasName,
                    gwp: gas.gwp,
                    category: gas.category,
                  }));
              } else {
                gasListData.value = [];
              }
            } else {
              gasListData.value = [];
            }
          }
        } catch (error: any) {
          console.error('获取排放因子详情失败:', error);
          message.error('获取排放因子详情失败');
          gasListData.value = [];
        }
      } else {
        gasListData.value = [];
      }

      formData.value = formValues;
      viewMode.value = data.viewMode || false;
      await formApi.setValues(formData.value);

      // 同步排放因子值禁用状态
      syncFactorValueState();

      if (!data.id && !data.factorCode) {
        try {
          const codeRes = await getFactorLibCode();
          if (codeRes) {
            await formApi.setFieldValue('factorCode', codeRes);
          }
        } catch {
          message.error('获取排放因子编码失败');
        }
      }
    } finally {
      modalApi.unlock();
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
    <div class="mx-4">
      <h4 class="text-base font-semibold mb-4">基础信息</h4>
      <Form />

      <div class="mt-6">
        <div>
          <h4 class="text-base font-semibold">气体列表</h4>
          <div v-if="!viewMode" class="flex items-center gap-2 px-2 mt-4 mb-4">
            <a-select
              v-model:value="selectedGasIds"
              mode="multiple"
              placeholder="请选择气体"
              :options="gasOptions"
              :loading="isLoadingGas"
              :max-tag-count="2"
              allow-clear
              style="flex: 1"
            />
            <a-button type="primary" @click="handleAddGas"> 添加气体 </a-button>
          </div>
        </div>
        <a-table
          :columns="!viewMode ? columns1 : columns2"
          :data-source="gasListData"
          :pagination="false"
          :row-key="(record: any) => record.id"
          :locale="{ emptyText: '暂无关联气体' }"
          size="small"
          bordered
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <a-button
                v-if="!viewMode"
                type="link"
                danger
                size="small"
                @click="handleDeleteGas(record)"
              >
                删除
              </a-button>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </Modal>
</template>
<style scoped lang="scss">
.ant-table-wrapper .ant-table.ant-table-small .ant-table-tbody > tr > td {
  padding: 4px 6px;
}
</style>
