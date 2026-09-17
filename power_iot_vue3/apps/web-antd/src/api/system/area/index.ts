import { requestClient } from '#/api/request';

export namespace SystemAreaApi {
  /** 地区信息 */
  export interface Area {
    id?: number;
    code?: string;
    name?: string;
    extId?: number;
    extName?: string;
    level?: number;
    parentId?: number;
    parentName?: string;
    levels?: Array<number>;
    pinyinPrefix?: string;
    pinyin?: string;
    children?: Area[];
    parentExtName?: string;
    parentExtId?: number;
    parentLevel?: number;
  }

  /** 地区分页列表信息 */
  export interface AreaPage extends Area {
    pageNo?: number;
    pageSize?: number;
  }
}

/** 获得地区树 */
export function getAreaTree(params: SystemAreaApi.Area) {
  return requestClient.get<SystemAreaApi.Area[]>('/system/area/tree', {
    params,
  });
}

/** 获得地区分页列表 */
export function getAreaPage(params: SystemAreaApi.AreaPage) {
  return requestClient.get<SystemAreaApi.AreaPage[]>('/system/area/list', {
    params,
  });
}

/** 获得地区简洁列表 */
export function getAreaListSimple(params: SystemAreaApi.Area) {
  return requestClient.get<SystemAreaApi.Area[]>('/system/area/list-all-simple', { params });
}

/** 获得 IP 对应的地区名 */
export function getAreaByIp(ip: string) {
  return requestClient.get<string>(`/system/area/get-by-ip?ip=${ip}`);
}

/** 创建地区 */
export function createArea(params: SystemAreaApi.Area) {
  return requestClient.post<SystemAreaApi.Area>('/system/area/create', params);
}

/** 更新地区 */
export function updateArea(params: SystemAreaApi.Area) {
  return requestClient.put<SystemAreaApi.Area>('/system/area/update', params);
}

/** 删除地区 */
export function deleteArea(id: number) {
  return requestClient.delete<any>(`/system/area/delete?id=${id}`);
}

/** 批量删除地区 */
export function deleteListArea(ids: number[]) {
  return requestClient.delete<any>(
    `/system/area/delete-list?ids=${ids.join(',')}`,
  );
}
