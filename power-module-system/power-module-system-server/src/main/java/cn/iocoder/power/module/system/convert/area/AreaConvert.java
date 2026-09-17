package cn.iocoder.power.module.system.convert.area;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaRespVO;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaSaveReqVO;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaSimpleRespVO;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaTreeRespVO;
import cn.iocoder.power.module.system.dal.dataobject.area.AreaDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 行政区 Convert
 *
 * @author system
 */
@Mapper
public interface AreaConvert {

    AreaConvert INSTANCE = Mappers.getMapper(AreaConvert.class);

    AreaDO convert(AreaSaveReqVO bean);

    AreaRespVO convert(AreaDO bean);

    List<AreaRespVO> convertList(List<AreaDO> list);

    default PageResult<AreaRespVO> convertPage(PageResult<AreaDO> pageResult) {
        return new PageResult<>(convertList(pageResult.getList()), pageResult.getTotal());
    }

    List<AreaSimpleRespVO> convertSimpleList(List<AreaDO> list);

    AreaTreeRespVO convertTree(AreaDO bean);

    AreaRespDTO convertDTO(AreaDO bean);

    List<AreaRespDTO> convertDTOList(List<AreaDO> list);

}
