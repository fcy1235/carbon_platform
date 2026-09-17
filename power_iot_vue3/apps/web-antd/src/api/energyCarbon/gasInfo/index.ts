import type { PageParam } from '@vben/request';
import { requestClient } from '#/api/request';

// 气体分页请求参数
export interface GasInfoPageParam extends PageParam {
  ids?: number[]; // 气体编号列表（选中导出时使用）
  gasCode?: string; // 气体编码
  gasName?: string; // 气体名称
  category?: number; // 分类
}

// 气体
export interface GasInfo {
  id?: number; // 编号
  gasCode?: string; // 气体编码
  gasName: string; // 气体名称
  gwp: number; // GWP值
  category: number; // 分类
}

// 获得气体分页
export function getGasInfoPage(params: GasInfoPageParam) {
  return requestClient.get<any>('/carbon/gas-info/page', { params });
}

// 获得气体列表
export function getGasInfoList() {
  return requestClient.get<any>('/carbon/gas-info/list');
}

// 获得气体
export function getGasInfo(id: number) {
  return requestClient.get<any>(`/carbon/gas-info/get?id=${id}`);
}

// 新增气体
export function createGasInfo(data: GasInfo) {
  return requestClient.post<any>('/carbon/gas-info/create', data);
}

// 更新气体
export function updateGasInfo(data: GasInfo) {
  return requestClient.put<any>('/carbon/gas-info/update', data);
}

// 删除气体
export function deleteGasInfo(id: number) {
  return requestClient.delete<any>(`/carbon/gas-info/delete?id=${id}`);
}

// 导出气体
export function exportGasInfo(params: GasInfoPageParam) {
  return requestClient.download('/carbon/gas-info/export-excel', { params });
}

// 导入气体
export function importGasInfo(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post<any>('/carbon/gas-info/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 获取导入模板
export function getImportTemplate() {
  return requestClient.download('/carbon/gas-info/get-import-template');
}

// 获取气体编码
export function getGasInfoCode() {
  return requestClient.get<any>('/carbon/gas-info/getCode');
}
