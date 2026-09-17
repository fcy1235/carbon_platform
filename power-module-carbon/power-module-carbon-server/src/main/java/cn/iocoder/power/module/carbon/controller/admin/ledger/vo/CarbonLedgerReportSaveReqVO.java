package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - 台账上报创建/修改 Request VO")
@Data
public class CarbonLedgerReportSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "关联台账文件ID", example = "1")
    private Long ledgerFileId;

    @Schema(description = "上报说明", example = "2026年第一季度新增户台账上报")
    private String reportDesc;

    @Schema(description = "盖章报告PDF地址", example = "/uploads/report/stamped.pdf")
    private String stampedReportUrl;

    @Schema(description = "盖章报告PDF文件名", example = "盖章报告.pdf")
    private String stampedReportName;

    @Schema(description = "盖章报告PDF文件")
    private MultipartFile stampedReportFile;

    @Schema(description = "备注", example = "")
    private String remark;
}
