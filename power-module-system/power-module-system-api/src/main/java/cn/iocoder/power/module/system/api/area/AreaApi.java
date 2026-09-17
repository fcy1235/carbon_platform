package cn.iocoder.power.module.system.api.area;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * RPC 服务 - 行政区
 *
 * @author system
 */
@FeignClient(name = ApiConstants.NAME) // TODO fallbackFactory =
@Tag(name = "RPC 服务 - 行政区")
public interface AreaApi {

    String PREFIX = ApiConstants.PREFIX + "/area";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得行政区信息")
    @Parameter(name = "id", description = "行政区编号", example = "110000", required = true)
    CommonResult<AreaRespDTO> getArea(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得行政区信息数组")
    @Parameter(name = "ids", description = "行政区编号数组", example = "110000,120000", required = true)
    CommonResult<List<AreaRespDTO>> getAreaList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/list-by-parent")
    @Operation(summary = "获得指定行政区的所有子行政区")
    @Parameter(name = "parentId", description = "父行政区编号", example = "0", required = true)
    CommonResult<List<AreaRespDTO>> getAreaListByParentId(@RequestParam("parentId") Long parentId);

    @GetMapping(PREFIX + "/get-by-name")
    @Operation(summary = "根据父级编号和名称获得行政区信息")
    @Parameter(name = "parentId", description = "父行政区编号", example = "0", required = true)
    @Parameter(name = "name", description = "行政区名称", example = "北京市", required = true)
    CommonResult<AreaRespDTO> getAreaByName(@RequestParam("parentId") Long parentId,
                                             @RequestParam("name") String name);

}
