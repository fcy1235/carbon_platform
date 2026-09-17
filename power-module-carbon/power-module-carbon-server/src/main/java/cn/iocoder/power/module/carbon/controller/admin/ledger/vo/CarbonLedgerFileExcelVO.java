package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 台账文件 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonLedgerFileExcelVO {

    @ExcelProperty("所属市")
    private String cityName;

    @ExcelProperty("所属区县")
    private String districtName;

    @ExcelProperty("上传类型")
    private String uploadType;

    @ExcelProperty("文件名称")
    private String fileName;

    @ExcelProperty("数据量")
    private Integer dataCount;

    @ExcelProperty("上传状态")
    private String uploadStatus;

    @ExcelProperty("上传时间")
    private LocalDateTime uploadTime;

    @ExcelProperty("上传人")
    private String uploader;
}
