package cn.iocoder.power.module.system.service.area;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.module.system.controller.admin.area.vo.*;
import cn.iocoder.power.module.system.dal.dataobject.area.AreaDO;

import java.util.Collection;
import java.util.List;

/**
 * 行政区 Service 接口
 *
 * @author system
 */
public interface AreaService {

    /**
     * 创建行政区
     *
     * @param createReqVO 行政区信息
     * @return 行政区编号
     */
    Long createArea(AreaSaveReqVO createReqVO);

    /**
     * 更新行政区
     *
     * @param updateReqVO 行政区信息
     */
    void updateArea(AreaSaveReqVO updateReqVO);

    /**
     * 删除行政区
     *
     * @param id 行政区编号
     */
    void deleteArea(Long id);

    /**
     * 批量删除行政区
     *
     * @param ids 行政区编号数组
     */
    void deleteAreaList(List<Long> ids);

    /**
     * 获得行政区信息
     *
     * @param id 行政区编号
     * @return 行政区信息
     */
    AreaRespVO getArea(Long id);

    /**
     * 筛选行政区列表
     *
     * @param reqVO 筛选条件请求 VO
     * @return 行政区列表
     */
    List<AreaRespVO> getAreaList(AreaListReqVO reqVO);

    /**
     * 获得行政区分页
     *
     * @param pageReqVO 分页查询
     * @return 行政区分页
     */
    PageResult<AreaRespVO> getAreaPage(AreaPageReqVO pageReqVO);

    /**
     * 获得行政区精简列表
     *
     * @param reqVO 筛选条件请求 VO
     * @return 行政区精简列表
     */
    List<AreaSimpleRespVO> getAreaSimpleList(AreaListReqVO reqVO);

    /**
     * 获得行政区树
     *
     * @param reqVO 筛选条件请求 VO
     * @return 行政区树
     */
    List<AreaTreeRespVO> getAreaTree(AreaListReqVO reqVO);

    /**
     * 获得所有行政区列表（内部使用）
     *
     * @return 行政区列表
     */
    List<AreaDO> getAllAreaList();

    /**
     * 获得指定行政区的所有子行政区（内部使用）
     *
     * @param id 行政区编号
     * @return 子行政区列表
     */
    List<AreaDO> getChildAreaList(Long id);

    /**
     * 获得行政区信息（RPC 使用）
     *
     * @param id 行政区编号
     * @return 行政区信息
     */
    AreaRespDTO getAreaDTO(Long id);

    /**
     * 获得行政区信息列表（RPC 使用）
     *
     * @param ids 行政区编号数组
     * @return 行政区信息列表
     */
    List<AreaRespDTO> getAreaDTOList(Collection<Long> ids);

    /**
     * 获得指定行政区的所有子行政区（RPC 使用）
     *
     * @param id 行政区编号
     * @return 子行政区列表
     */
    List<AreaRespDTO> getChildAreaDTOList(Long id);

    /**
     * 根据父级编号和名称获得行政区信息（RPC 使用）
     *
     * @param parentId 父行政区编号
     * @param name 行政区名称
     * @return 行政区信息
     */
    AreaRespDTO getAreaDTOByName(Long parentId, String name);

}
