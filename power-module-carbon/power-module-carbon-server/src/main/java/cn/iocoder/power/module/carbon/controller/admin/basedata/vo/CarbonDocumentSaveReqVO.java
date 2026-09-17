package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 文档管理创建/修改 Request VO")
@Data
public class CarbonDocumentSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "文档编号", example = "DOC20260514001")
    private String docCode;

    @Schema(description = "文档标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "文档标题1")
    @NotBlank(message = "文档标题不能为空")
    private String docTitle;

    @Schema(description = "文档名称", example = "文档1.pdf")
    private String docName;


    @Schema(description = "适用边界", example = "河北省农村区域")
    private String applicableBoundary;

    @Schema(description = "计算公式", example = "E=AD×EF")
    private String formula;

    @Schema(description = "文件地址", example = "/uploads/doc/xxx.pdf")
    private String fileUrl;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "详细信息列表")
    private List<CarbonDocumentDetailSaveReqVO> details;

    @Schema(description = "参数说明列表")
    private List<CarbonDocumentParamSaveReqVO> params;
}
