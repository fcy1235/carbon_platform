import type { VbenFormSchema } from '#/adapter/form';

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'projectId',
      label: '项目',
      component: 'Select',
      componentProps: {
        placeholder: '请选择项目',
        allowClear: true,
        showSearch: true,
        filterOption: (input: string, option: any) =>
          String(option?.label ?? '')
            .toLowerCase()
            .includes(input.toLowerCase()),
      },
      rules: 'required',
    },
  ];
}
