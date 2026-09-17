import { requestClient } from '#/api/request';

export namespace SystemElectricityApi {
  /** 同步电力数据参数 */
  export interface syncElectricityData {
    id?: number;
  }
  /** 电力分页列表信息 */
  export interface ElectricityPage{
    pageNo: number;
    pageSize: number;
    username?: string;
    carbonUserInfoId?: number;
    carbonUserInfoIds?: Array<number>;
    provinceCode?: number;
    cityCode?: number;
    districtCode?: number;
    electricityId?: string;
  }
}

/** 获得电力分页列表 */
export function getElectricityPage(params: SystemElectricityApi.ElectricityPage) {
  return requestClient.get<any>('/carbon/electricity/page', { params });
}

/** 获得电力数据 */
export function getElectricityData(id: number) {
  return requestClient.get<string>(`/carbon/electricity/get?id=${id}`);
}

/** 获得电力数据详情（该户所有记录） */
export function getElectricityDetailData(electricityId: string) {
  return requestClient.get<any>(`/carbon/electricity/detail?electricityId=${electricityId}`);
}

/** 同步单条电力数据 */
export function syncElectricityData(id: number) {
  return requestClient.post<string>(`/carbon/electricity/sync?id=${id}`);
}

/** 同步所有电力数据 */
export function syncAllElectricityData() {
  return requestClient.post<string>('/carbon/electricity/sync-all');
}

/** 导出电力数据 */
export function exportElectricity(params: SystemElectricityApi.ElectricityPage) {
  return requestClient.download('/carbon/electricity/export-excel', { params });
}

/** 下载电力数据导入模板 */
export function getElectricityImportTemplate() {
  return requestClient.download('/carbon/electricity/get-import-template');
}

/** 导入电力数据 */
export function importElectricity(file: File) {
  return requestClient.upload('/carbon/electricity/import', { file });
}

/** 根据电力号删除电力数据（外层列表，支持单条和批量） */
export function deleteElectricityDataByElectricIds(electricIds: number[]) {
  return requestClient.delete<any>(
    `/carbon/electricity/deleteByElectricIds?electricIds=${electricIds.join(',')}`,
  );
}

/** 删除电力数据记录（弹框内历史记录，支持单条和批量） */
export function deleteElectricityData(ids: number[]) {
  return requestClient.delete<any>(
    `/carbon/electricity/delete?ids=${ids.join(',')}`,
  );
}
