package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 台账文件创建/修改 Request VO")
@Data
public class CarbonLedgerFileSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "所属市编码", example = "130100")
    private String cityCode;

    @Schema(description = "所属区县编码", example = "130102")
    private String districtCode;

    @Schema(description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "上传类型不能为空")
    private String uploadType;

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "新增户台账.xlsx")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "文件地址", example = "/uploads/ledger/xxx.xlsx")
    private String fileUrl;

    @Schema(description = "数据量", example = "100")
    private Integer dataCount;

    @Schema(description = "上传状态：1-上传成功 2-上传失败", example = "1")
    private String uploadStatus;

    @Schema(description = "备注", example = "")
    private String remark;
}
