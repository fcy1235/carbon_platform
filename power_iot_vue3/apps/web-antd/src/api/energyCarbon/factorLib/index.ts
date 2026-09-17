import type { PageParam } from '@vben/request';
import { requestClient } from '#/api/request';

// 排放因子分页请求参数
export interface FactorLibPageParam extends PageParam {
  factorCode?: string; // 排放因子编码
  factorName?: string; // 排放因子名称
  emissionSource?: string; // 排放源
}

// 排放因子
export interface FactorLib {
  id?: number; // 编号
  factorCode?: string; // 排放因子编码
  factorName: string; // 排放因子名称
  relatedDoc?: string; // 关联文档
  emissionSource?: string; // 排放源
  unit: string; // 单位
  factorValue: number; // 因子值
  gasList?: string; // 关联气体
}

// 获得排放因子分页
export function getFactorLibPage(params: FactorLibPageParam) {
  return requestClient.get<any>('/carbon/factor-lib/page', { params });
}

// 获得排放因子
export function getFactorLib(id: number) {
  return requestClient.get<any>(`/carbon/factor-lib/get?id=${id}`);
}

// 新增排放因子
export function createFactorLib(data: FactorLib) {
  return requestClient.post<any>('/carbon/factor-lib/create', data);
}

// 更新排放因子
export function updateFactorLib(data: FactorLib) {
  return requestClient.post<any>('/carbon/factor-lib/update', data);
}

// 删除排放因子
export function deleteFactorLib(id: number) {
  return requestClient.delete<any>(`/carbon/factor-lib/delete?id=${id}`);
}

// 导出排放因子Excel
export function exportFactorLib(params: FactorLibPageParam) {
  return requestClient.download('/carbon/factor-lib/export-excel', { params });
}

// 获取排放因子编码
export function getFactorLibCode() {
  return requestClient.get<any>('/carbon/factor-lib/getCode');
}
