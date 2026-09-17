import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemAreaApi } from '#/api/system/area';

/** 新增/修改的表单 */
export function useFormSchema(isEdit = false): VbenFormSchema[] {
  return [
    {
      fieldName: 'parentId',
      label: '上级行政区划编码',
      component: 'Input',
      componentProps: {
        disabled: true,
        placeholder: '请选择上级行政区划编码',
      },
      rules: 'required',
    },
    {
      fieldName: 'id',
      label: '行政区划编码',
      component: 'Input',
      componentProps: {
        disabled: isEdit,
        placeholder: '请输入行政区划编码',
      },
      rules: 'required',
    },
    {
      fieldName: 'name',
      label: '行政区划名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入行政区划名称',
      },
      rules: 'required',
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<SystemAreaApi.Area>['columns'] {
  return [
    { type: 'seq', width: 60 },
    {
      field: 'id',
      title: '行政区划编码',
      minWidth: 120,
      align: 'left',
    },
    {
      field: 'name',
      title: '行政区划名称',
      minWidth: 150,
    },
    {
      field: 'level',
      title: '所属层级',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        const levelMap: Record<number, string> = {
          0: '省级',
          1: '市级',
          2: '县级',
          3: '乡镇/街道',
          4: '村/社区',
        };
        return levelMap[cellValue] || `第${cellValue}级`;
      },
    },
    {
      field: 'parentExtName',
      title: '上级行政区划',
      minWidth: 150,
    },
  ];
}
export function useGridColumns2(): VxeTableGridOptions<SystemAreaApi.Area>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '行政区划编码',
      minWidth: 120,
      align: 'left',
    },
    {
      field: 'name',
      title: '行政区划名称',
      minWidth: 150,
    },
    {
      field: 'level',
      title: '所属层级',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        const levelMap: Record<number, string> = {
          0: '省级',
          1: '市级',
          2: '县级',
          3: '乡镇/街道',
          4: '村/社区',
        };
        return levelMap[cellValue] || `第${cellValue}级`;
      },
    },
    {
      field: 'parentExtName',
      title: '上级行政区划',
      minWidth: 150,
    },
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
