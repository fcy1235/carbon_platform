package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 台账文件 Response VO")
@Data
public class CarbonLedgerFileRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "所属市编码", example = "130100")
    private String cityCode;

    @Schema(description = "所属市名称", example = "石家庄市")
    private String cityName;

    @Schema(description = "所属区县编码", example = "130102")
    private String districtCode;

    @Schema(description = "所属区县名称", example = "长安区")
    private String districtName;

    @Schema(description = "上传类型", example = "1")
    private String uploadType;

    @Schema(description = "文件名称", example = "新增户台账.xlsx")
    private String fileName;

    @Schema(description = "文件地址", example = "/uploads/ledger/xxx.xlsx")
    private String fileUrl;

    @Schema(description = "数据量", example = "100")
    private Integer dataCount;

    @Schema(description = "上传状态", example = "2")
    private String uploadStatus;

    @Schema(description = "审核状态：1-待提交 2-审核中 3-已通过 4-已驳回", example = "1")
    private String auditStatus;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "上传人", example = "admin")
    private String uploader;

    @Schema(description = "审核人", example = "admin")
    private String reviewer;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "驳回原因", example = "数据不完整")
    private String rejectReason;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
