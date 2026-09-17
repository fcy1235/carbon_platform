import type { PageParam } from '@vben/request';
import { requestClient } from '#/api/request';

// 排放源分页请求参数
export interface EmissionSourcePageParam extends PageParam {
  ids?: number[]; // 排放源编号列表（选中导出时使用）
  sourceCode?: string; // 排放源编码
  sourceName?: string; // 排放源名称
  scope?: number; // 排放范围
}

// 排放源
export interface EmissionSource {
  id?: number; // 编号
  sourceCode?: string; // 排放源编码
  sourceName: string; // 排放源名称
  scope: number; // 排放范围
}

// 获得排放源分页
export function getEmissionSourcePage(params: EmissionSourcePageParam) {
  return requestClient.get<any>('/carbon/emission-source/page', { params });
}

// 获得排放源列表
export function getEmissionSourceList() {
  return requestClient.get<any>('/carbon/emission-source/getList');
}

// 获得排放源
export function getEmissionSource(id: number) {
  return requestClient.get<any>(`/carbon/emission-source/get?id=${id}`);
}

// 新增排放源
export function createEmissionSource(data: EmissionSource) {
  return requestClient.post<any>('/carbon/emission-source/create', data);
}

// 更新排放源
export function updateEmissionSource(data: EmissionSource) {
  return requestClient.put<any>('/carbon/emission-source/update', data);
}

// 删除排放源
export function deleteEmissionSource(id: number) {
  return requestClient.delete<any>(`/carbon/emission-source/delete?id=${id}`);
}

// 导出排放源
export function exportEmissionSource(params: EmissionSourcePageParam) {
  return requestClient.download('/carbon/emission-source/export-excel', { params });
}
// 获取排放源编码
export function getEmissionSourceCode() {
  return requestClient.get<any>('/carbon/emission-source/getCode');
}
