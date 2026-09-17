package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 导入任务 Response VO")
@Data
public class CarbonLedgerImportTaskRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "关联台账文件ID", example = "1")
    private Long ledgerFileId;

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

    @Schema(description = "正常数量", example = "95")
    private Integer successCount;

    @Schema(description = "失败数量", example = "5")
    private Integer failCount;

    @Schema(description = "上传状态", example = "1")
    private String uploadStatus;

    @Schema(description = "导入状态", example = "5")
    private String importStatus;

    @Schema(description = "上传人", example = "admin")
    private String uploader;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "导入结果文件地址", example = "/uploads/ledger/result.xlsx")
    private String resultFileUrl;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
