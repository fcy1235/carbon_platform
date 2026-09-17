package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 文档管理 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonDocumentExcelVO {

    @ExcelProperty("文档编号")
    private String docCode;

    @ExcelProperty("文档标题")
    private String docTitle;

    @ExcelProperty("文档名称")
    private String docName;

    @ExcelProperty("适用边界")
    private String applicableBoundary;

    @ExcelProperty("计算公式")
    private String formula;

    @ExcelProperty("上传人")
    private String uploader;

    @ExcelProperty("文件地址")
    private String fileUrl;

    @ExcelProperty("备注")
    private String remark;
}
