package cn.iocoder.power.module.system.api.area;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.module.system.service.area.AreaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.power.framework.common.pojo.CommonResult.success;

/**
 * 行政区 API 实现类
 *
 * @author system
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AreaApiImpl implements AreaApi {

    @Resource
    private AreaService areaService;

    @Override
    public CommonResult<AreaRespDTO> getArea(Long id) {
        return success(areaService.getAreaDTO(id));
    }

    @Override
    public CommonResult<List<AreaRespDTO>> getAreaList(Collection<Long> ids) {
        return success(areaService.getAreaDTOList(ids));
    }

    @Override
    public CommonResult<List<AreaRespDTO>> getAreaListByParentId(Long parentId) {
        return success(areaService.getChildAreaDTOList(parentId));
    }

    @Override
    public CommonResult<AreaRespDTO> getAreaByName(Long parentId, String name) {
        return success(areaService.getAreaDTOByName(parentId, name));
    }

}
