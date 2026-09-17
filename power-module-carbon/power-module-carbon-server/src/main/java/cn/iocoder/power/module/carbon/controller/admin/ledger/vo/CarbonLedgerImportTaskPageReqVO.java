package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.power.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 导入任务分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerImportTaskPageReqVO extends PageParam {

    @Schema(description = "所属市编码", example = "130100")
    private String cityCode;

    @Schema(description = "所属区县编码", example = "130102")
    private String districtCode;

    @Schema(description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", example = "1")
    private String uploadType;

    @Schema(description = "导入状态：1-待检测 2-检测通过 3-检测失败 4-导入中 5-导入成功 6-部分失败 7-导入失败", example = "5")
    private String importStatus;

    @Schema(description = "上传时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] uploadTime;

    @Schema(description = "审核状态：0-待提交 1-待提交 2-审核中 3-已通过 4-已驳回", example = "3")
    private String auditStatus;
}
