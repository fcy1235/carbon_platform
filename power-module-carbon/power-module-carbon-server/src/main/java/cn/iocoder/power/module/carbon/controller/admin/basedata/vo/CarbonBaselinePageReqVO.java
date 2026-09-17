package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Schema(description = "管理后台 - 基准线碳排放强度分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonBaselinePageReqVO extends PageParam {

    @Schema(description = "基准线编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "行政区划", example = "河北省石家庄市裕华区")
    private String division;

    @Schema(description = "改造类型", example = "气代煤")
    private String reformType;

    @Schema(description = "行政区划-省", example = "11")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "1111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode;

    @Schema(description = "行政区划-省列表")
    private Collection<Long> provinceCodes;

    @Schema(description = "行政区划-市列表")
    private Collection<Long> cityCodes;

    @Schema(description = "行政区划-区列表")
    private Collection<Long> districtCodes;
}
