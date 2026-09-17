package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collection;

import static cn.iocoder.power.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 台账上报分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerReportPageReqVO extends PageParam {

    @Schema(description = "所属市编码", example = "130100")
    private String cityCode;

    @Schema(description = "所属区县编码", example = "130102")
    private String districtCode;

    @Schema(description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", example = "1")
    private String uploadType;

    @Schema(description = "审核状态：1-待提交 2-审核中 3-已通过 4-已驳回", example = "2")
    private String auditStatus;

    @Schema(description = "审核状态集合")
    private Collection<String> auditStatusCollection;

    @Schema(description = "上传时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] uploadTime;
}
