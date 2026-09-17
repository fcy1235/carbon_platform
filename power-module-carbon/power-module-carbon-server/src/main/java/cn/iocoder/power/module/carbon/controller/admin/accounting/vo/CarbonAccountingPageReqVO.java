package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Schema(description = "管理后台 - 碳排放核算分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonAccountingPageReqVO extends PageParam {

    @Schema(description = "用户信息编号", example = "1024")
    private Long carbonUserInfoId;
    @Schema(description = "用户信息编号列表")
    private Collection<Long> carbonUserInfoIds;

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    private String idCard;

    @Schema(description = "改造类别 1电代煤 2气代煤", example = "1")
    private String reformType;

    @Schema(description = "行政区划-省", example = "11")
    private Long provinceCode; // 省编码
    @Schema(description = "行政区划-市", example = "1111")
    private Long cityCode;
    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode; // 区编码

    @Schema(description = "活动名称编码（字典值）", example = "electricity_consumption")
    private String activityNameCode;

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;

    @Schema(description = "是否只返回有碳核算记录的用户", example = "false")
    private Boolean onlyWithAccounting;

    @Schema(description = "核算周期开始时间")
    private LocalDate accountingPeriodStart;
    @Schema(description = "核算周期结束时间")
    private LocalDate accountingPeriodEnd;
}
