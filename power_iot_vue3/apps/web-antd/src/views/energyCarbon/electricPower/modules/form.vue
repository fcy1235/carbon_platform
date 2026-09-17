<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  ACTION_ICON,
  TableAction,
  useVbenVxeGrid,
} from '#/adapter/vxe-table';
import {
  deleteElectricityData,
  getElectricityDetailData,
} from '#/api/energyCarbon/electricPower';

import { useHistoryGridColumns } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();

const title = '查看电力数据';

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: true,
    },
    labelWidth: 120,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: [
    {
      fieldName: 'dataSource',
      label: '数据来源',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_ELECTRIC_SOURCE,
          'string',
        ),
        placeholder: '请选择数据来源',
        allowClear: true,
        disabled: true,
      },
    },
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'carbonUserInfoId',
      label: '用户信息编号',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区划',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'electricityId',
      label: '电表ID(户号)',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'currentTotal',
      label: '当前累计电量(kWh)',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },

    {
      fieldName: 'currentUsage',
      label: '本次用电量(kWh)',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'readingTime',
      label: '读数时间',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
  ],
  showDefaultActions: false,
});

// 历史数据选中项
const selectedHistoryKeys = ref<number[]>([]);

function handleHistoryCheckboxChange({ records }: { records: any[] }) {
  selectedHistoryKeys.value = records.map((item) => item.id!);
}

const historyToolbarActions = computed(() => [
  {
    label: '批量删除',
    type: 'primary' as const,
    danger: selectedHistoryKeys.value.length > 0,
    icon: ACTION_ICON.DELETE,
    auth: ['carbon:electricity:delete'],
    disabled: isEmpty(selectedHistoryKeys.value),
    onClick: handleBatchDeleteHistory,
  },
]);

function getHistoryRowActions(row: any) {
  return [
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:electricity:delete'],
      popConfirm: {
        title: '确定要删除这条记录吗？',
        confirm: handleDeleteHistory.bind(null, row),
      },
    },
  ];
}

const [HistoryGrid, historyGridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: [
      { type: 'checkbox', width: 40 },
      ...useHistoryGridColumns()!,
      {
        title: '操作',
        width: 80,
        fixed: 'right',
        slots: { default: 'historyActions' },
      },
    ],
    height: 300,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async () => {
          if (!formData.value?.electricityId) {
            return { list: [], total: 0 };
          }
          const result: any = await getElectricityDetailData(
            formData.value.electricityId,
          );
          if (Array.isArray(result)) {
            return {
              list: result || [],
              total: result?.length || 0,
            };
          }
          return {
            list: result?.list || [],
            total: result?.total || 0,
          };
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
    },
    pagerConfig: {
      pageSize: 20,
      pageSizes: [20, 50, 100],
    },
    emptyText: '暂无该用户历史电力数据',
  },
  gridEvents: {
    checkboxAll: handleHistoryCheckboxChange,
    checkboxChange: handleHistoryCheckboxChange,
  },
});

/** 删除单条历史记录 */
async function handleDeleteHistory(row: any) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteElectricityData([row.id]);
    message.success('删除成功');
    selectedHistoryKeys.value = [];
    historyGridApi.query();
    emit('success');
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

const [DeleteHistoryModal, deleteHistoryModalApi] = useVbenModal({
  title: '确认删除',
  destroyOnClose: true,
  async onConfirm() {
    await handleConfirmDeleteHistory();
  },
});

/** 批量删除历史记录 */
function handleBatchDeleteHistory() {
  deleteHistoryModalApi.open();
}

/** 确认批量删除历史记录 */
async function handleConfirmDeleteHistory() {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteElectricityData(selectedHistoryKeys.value);
    message.success(`成功删除 ${selectedHistoryKeys.value.length} 条数据`);
    selectedHistoryKeys.value = [];
    historyGridApi.query();
    emit('success');
  } catch {
    message.error('批量删除失败');
  } finally {
    hideLoading();
  }
  deleteHistoryModalApi.close();
}

const [Modal, modalApi] = useVbenModal({
  title,
  width: 1000,
  class: 'max-w-[1000px]',
  footer: false,
  async onConfirm() {
    await modalApi.close();
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = data;
      await formApi.setValues(formData.value);
      historyGridApi.query();
    } catch {
      message.error('加载电力数据失败');
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
      <h4 class="text-base font-semibold mb-4">基础电力数据</h4>
      <Form />

      <h4 class="text-base font-semibold mb-4 mt-6">历史电力数据</h4>
      <div class="mb-2">
        <TableAction :actions="historyToolbarActions" />
      </div>
      <HistoryGrid>
        <template #historyActions="{ row }">
          <TableAction :actions="getHistoryRowActions(row)" />
        </template>
      </HistoryGrid>
      <DeleteHistoryModal>
        <p>确定要删除选中的 {{ selectedHistoryKeys.length }} 条记录吗？</p>
      </DeleteHistoryModal>
    </div>
  </Modal>
</template>
