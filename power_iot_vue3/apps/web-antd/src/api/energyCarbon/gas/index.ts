import { requestClient } from '#/api/request';

export namespace SystemGasApi {
  /** 同步燃气数据参数 */
  export interface syncGasData {
    id?: number;
  }
  /** 燃气分页列表信息 */
  export interface GasPage{
    pageNo: number;
    pageSize: number;
    username?: string;
    carbonUserInfoId?: number;
    carbonUserInfoIds?: Array<number>;
    provinceCode?: number;
    cityCode?: number;
    districtCode?: number;
    gasId?: string;
  }
}

/** 获得燃气分页列表 */
export function getGasPage(params: SystemGasApi.GasPage) {
  return requestClient.get<any>('/carbon/gas-data/page', { params });
}

/** 获得燃气数据 */
export function getGasData(id: number) {
  return requestClient.get<string>(`/carbon/gas-data/get?id=${id}`);
}

/** 获得燃气数据详情（该户所有记录） */
export function getGasDetailData(gasId: string) {
  return requestClient.get<any>(`/carbon/gas-data/detail?gasId=${gasId}`);
}

/** 同步单条燃气数据 */
export function syncGasData(id: number) {
  return requestClient.post<string>(`/carbon/gas-data/sync?id=${id}`);
}

/** 同步所有燃气数据 */
export function syncAllGasData() {
  return requestClient.post<string>('/carbon/gas-data/sync-all');
}

/** 导出燃气数据 */
export function exportGasData(params: SystemGasApi.GasPage) {
  return requestClient.download('/carbon/gas-data/export-excel', { params });
}

/** 下载燃气数据导入模板 */
export function getGasDataImportTemplate() {
  return requestClient.download('/carbon/gas-data/get-import-template');
}

/** 导入燃气数据 */
export function importGasData(file: File) {
  return requestClient.upload('/carbon/gas-data/import', { file });
}

/** 根据燃气表ID删除燃气数据（外层列表，支持单条和批量） */
export function deleteGasDataByGasId(gasIds: number[]) {
  return requestClient.delete<any>(
    `/carbon/gas-data/deleteByGasId?ids=${gasIds.join(',')}`,
  );
}

/** 删除燃气数据记录（弹框内历史记录，支持单条和批量） */
export function deleteGasData(ids: number[]) {
  return requestClient.delete<any>(
    `/carbon/gas-data/delete?ids=${ids.join(',')}`,
  );
}
