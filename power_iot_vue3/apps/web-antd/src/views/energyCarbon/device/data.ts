import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

/** 新增/修改的表单 - 基础信息 */
export function useBasicFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
      },
      rules: 'required',
    },
    {
      fieldName: 'idCard',
      label: '身份证号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入身份证号',
        disabled: true,
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
        disabled: true,
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属行政区',
        disabled: true,
      },
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入地址',
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择改造类别',
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'gasUserCode',
      label: '燃气用户编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气用户编码',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
  ];
}

// 气代煤判断
function isGasType(values: any) {
  return !!values.reformType && values.reformType === '2';
}

// 非气代煤（电代煤等）判断
function isElectricType(values: any) {
  return !!values.reformType && values.reformType === '1';
}

/** 设备信息表单 */
export function useDeviceFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'gasEnterpriseId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    // 隐藏的reformType，用于设备表单内字段联动
    {
      fieldName: 'reformType',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'deviceCode',
      label: '设备编码',
      component: 'Input',
      componentProps: {
        placeholder: '设备编码由系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'deviceName',
      label: '设备名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设备名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'deviceType',
      label: '设备类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择设备类型',
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_TYPE, 'number'),
      },
      rules: 'required',
    },
    // 供气企业（气代煤时显示）- 使用slot方式，点击弹框选择
    {
      fieldName: 'gasEnterpriseName',
      label: '供气企业',
      component: 'Input',
      componentProps: {
        placeholder: '请选择供气企业',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'enterpriseCreditCode',
      label: '企业信用代码',
      component: 'Input',
      componentProps: {
        placeholder: '根据供气企业自动带入',
        disabled: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'wallMountedStoveInstallYear',
      label: '壁挂炉安装年份',
      component: 'DatePicker',
      componentProps: {
        picker: 'year',
        placeholder: '请选择壁挂炉安装年份',
        style: { width: '100%' },
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'gasId',
      label: '燃气表具号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气表具号',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'deviceBrandModel',
      label: '设备品牌及型号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设备品牌及型号',
      },
    },
    {
      fieldName: 'gasMeterType',
      label: '燃气表类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择燃气表类型',
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_GAS_METER_TYPE,
          'string',
        ),
        allowClear: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'safetyDeviceStatus',
      label: '安全装置配备情况',
      component: 'Select',
      componentProps: {
        placeholder: '请选择安全装置配备情况',
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_SAFE_DEVICE_STATUS,
          'string',
        ),
        allowClear: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'electricityId',
      label: '电表号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电表号',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isElectricType(values),
      },
    },
    {
      fieldName: 'manufacturer',
      label: '生产厂家',
      component: 'Input',
      componentProps: {
        placeholder: '请输入生产厂家',
      },
      rules: 'required',
    },
    {
      fieldName: 'productionDate',
      label: '生产日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择生产日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'serviceLife',
      label: '使用年限',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入使用年限（年）',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'installDate',
      label: '安装日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择安装日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'operationDate',
      label: '投运日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择投运日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'stopDate',
      label: '停运日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择停运日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'status',
      label: '设备状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择设备状态',
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_STATUS, 'number'),
      },
      rules: 'required',
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
        allowClear: true,
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择行政区',
        allowClear: true,
        maxLevel: 3,
      },
    },
    {
      fieldName: 'deviceName',
      label: '设备名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设备名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'deviceType',
      label: '设备类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_TYPE, 'number'),
        placeholder: '请选择设备类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, 'string'),
        placeholder: '请选择改造类别',
        allowClear: true,
      },
    },
    // {
    //   fieldName: 'manufacturer',
    //   label: '生产厂家',
    //   component: 'Input',
    //   componentProps: {
    //     placeholder: '请输入生产厂家',
    //     allowClear: true,
    //   },
    // },
    {
      fieldName: 'status',
      label: '设备状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_STATUS, 'number'),
        placeholder: '请选择设备状态',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 60 },
    { type: 'seq', width: 60 },
    {
      field: 'username',
      title: '用户名',
      minWidth: 100,
    },
    {
      field: 'division',
      title: '所属行政区',
      minWidth: 200,
    },
    // {
    //   field: 'address',
    //   title: '地址',
    //   minWidth: 200,
    // },
    {
      field: 'reformType',
      title: '改造类别',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },
    {
      field: 'deviceCode',
      title: '设备编码',
      minWidth: 130,
    },
    {
      field: 'deviceName',
      title: '设备名称',
      minWidth: 150,
    },
    {
      field: 'deviceType',
      title: '设备类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_DEVICE_TYPE },
      },
    },
    // {
    //   field: 'gasEnterpriseName',
    //   title: '供气企业',
    //   minWidth: 150,
    // },
    // {
    //   field: 'gasId',
    //   title: '燃气表具号',
    //   minWidth: 130,
    // },
    // {
    //   field: 'electricityId',
    //   title: '电表号',
    //   minWidth: 130,
    // },
    // {
    //   field: 'deviceBrandModel',
    //   title: '设备品牌及型号',
    //   minWidth: 150,
    // },
    // {
    //   field: 'manufacturer',
    //   title: '生产厂家',
    //   minWidth: 150,
    // },
    // {
    //   field: 'serviceLife',
    //   title: '使用年限(年)',
    //   minWidth: 120,
    // },
    {
      field: 'status',
      title: '设备状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_DEVICE_STATUS },
      },
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 设备数据表格列配置参数 */
interface DeviceDataColumnsOptions {
  /** 改造类型：1-电代煤 2-气代煤 */
  reformType?: string;
  /** 改造模式：10-空气源热泵模式 */
  reformMode?: string;
  /** 设备类型标签（用于判断是否为空气源热泵） */
  deviceTypeLabel?: string;
}

/** 设备数据表格列（查看设备弹框内展示，煤改电设备上报数据） */
export function useDeviceDataGridColumns(
  options: DeviceDataColumnsOptions = {},
): VxeTableGridOptions['columns'] {
  const { reformType, reformMode, deviceTypeLabel } = options;
  const isElectric = reformType === '1';
  const isHeatPump = isElectric && reformMode === '10' && !!deviceTypeLabel?.includes('热泵');
  const isElectricMeter = deviceTypeLabel?.includes('电表');

  // 基础列：序号 + 同步时间
  const baseColumns: VxeTableGridOptions['columns'] = [
    { type: 'seq', width: 50, fixed: 'left' },
    {
      field: 'reportTime',
      title: '同步时间',
      width: 150,
      fixed: 'left',
      formatter: 'formatDateTime',
    },
    { field: 'totalElectricity', title: '总用电量', width: 100 },
    { field: 'voltage', title: '电压', width: 90 },
    { field: 'electricCurrent', title: '电流', width: 90 }
  ];

  // 空气源热泵模式：只显示温度相关字段
  if (isHeatPump) {
    return [
      ...baseColumns,
      { field: 'setTemperature', title: '设定温度', width: 95 },
      { field: 'liquidPipeTemperature', title: '液管温度', width: 95 },
      { field: 'moduleTemperature', title: '模块温度', width: 95 },
      { field: 'economizerInTemperature', title: '经济器进温度', width: 115 },
      { field: 'economizerOutTemperature', title: '经济器出温度', width: 115 },
      { field: 'returnWaterTemperature', title: '回水温度', width: 95 },
      { field: 'outletWaterTemperature', title: '出水温度', width: 95 },
      { field: 'outdoorTemperature', title: '室外温度', width: 95 },
      { field: 'coilTemperature', title: '盘管温度', width: 95 },
      { field: 'exhaustTemperature', title: '排气温度', width: 95 },
      { field: 'suctionWaterTemperature', title: '吸水温度', width: 95 },
      { field: 'wireControllerIndoorTemperature', title: '线控器室内温度', width: 125 },
      { field: 'indoorAmbientTemperature', title: '内机环温度', width: 105 },
      { field: 'indoorCoilTemperature', title: '内盘管温度', width: 105 },
    ];
  }

  // 其他煤改电：同步时间 + 电表相关字段（电表类型设备才显示）
  if (isElectric) {
    const columns = [...baseColumns];
    if (isElectricMeter) {
      columns.push(
        { field: 'totalElectricity', title: '总用电量', width: 100 },
        { field: 'voltage', title: '电压', width: 90 },
        { field: 'electricCurrent', title: '电流', width: 90 },
      );
    }
    return columns;
  }

  // 默认：显示所有列
  return [
    ...baseColumns,
    { field: 'totalElectricity', title: '总用电量', width: 100 },
    { field: 'voltage', title: '电压', width: 90 },
    { field: 'electricCurrent', title: '电流', width: 90 },
    { field: 'setTemperature', title: '设定温度', width: 95 },
    { field: 'liquidPipeTemperature', title: '液管温度', width: 95 },
    { field: 'moduleTemperature', title: '模块温度', width: 95 },
    { field: 'economizerInTemperature', title: '经济器进温度', width: 115 },
    { field: 'economizerOutTemperature', title: '经济器出温度', width: 115 },
    { field: 'returnWaterTemperature', title: '回水温度', width: 95 },
    { field: 'outletWaterTemperature', title: '出水温度', width: 95 },
    { field: 'outdoorTemperature', title: '室外温度', width: 95 },
    { field: 'coilTemperature', title: '盘管温度', width: 95 },
    { field: 'exhaustTemperature', title: '排气温度', width: 95 },
    { field: 'suctionWaterTemperature', title: '吸水温度', width: 95 },
    { field: 'wireControllerIndoorTemperature', title: '线控器室内温度', width: 125 },
    { field: 'indoorAmbientTemperature', title: '内机环温度', width: 105 },
    { field: 'indoorCoilTemperature', title: '内盘管温度', width: 105 },
  ];
}
