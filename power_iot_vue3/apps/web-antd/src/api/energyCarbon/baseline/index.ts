import { requestClient } from '#/api/request';

export namespace SystemBaselineApi {
  /** 同步基准线碳排放数据参数 */
  export interface BaselineData {
    id?: number;
    provinceCode?: number;
    cityCode?: number;
    districtCode?: number;
    reformType?: string;
    intensity?: number;
    unit?: string;
  }
  /** 基准线碳排放分页列表信息 */
  export interface BaselinePage{
    pageNo: number;
    pageSize: number;
    ids?: number[];
    division?: string;
    reformType?: string;
    provinceCode?: number;
    cityCode?: number;
    districtCode?: number;
  }
}

/** 获得基准线碳排放分页列表 */
export function getBaselinePage(params: SystemBaselineApi.BaselinePage) {
  return requestClient.get<any>('carbon/baseline/page', { params });
}

/** 根据行政区编码获得单个基准线 */
export function getBaselineByCode(params: {
  cityCode?: number;
  districtCode?: number;
  provinceCode?: number;
}) {
  return requestClient.get<any>(`/carbon/baseline/get-by-code`, { params });
}

/** 获得基准线碳排放数据 */
export function getBaselineData(id: number) {
  return requestClient.get<string>(`/carbon/baseline/get?id=${id}`);
}

/** 删除基准线碳排放数据 */
export function deleteBaselineData(id: number) {
  return requestClient.delete<string>(`/carbon/baseline/delete?id=${id}`);
}

/** 创建基准线碳排放数据 */
export function createBaselineData(params: SystemBaselineApi.BaselineData) {
  return requestClient.post<string>(`/carbon/baseline/create`, params);
}

/** 更新基准线碳排放数据 */
export function updateBaselineData(params: SystemBaselineApi.BaselineData) {
  return requestClient.put<string>(`/carbon/baseline/update`, params);
}

/** 导出基准线碳排放数据 */
export function exportBaseline(params: SystemBaselineApi.BaselinePage) {
  return requestClient.download('/carbon/baseline/export-excel', { params });
}
