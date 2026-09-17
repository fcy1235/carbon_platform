package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 撤销户台账导入数据 Excel 解析 VO
 * <p>
 * 对应模板：农村"双代"确户台账调整对照表
 * 表头位于第3行（前两行为标题和分组），使用列序号（index）映射以避免重复列名冲突。
 * </p>
 */
@Data
@ExcelIgnoreUnannotated
public class CarbonLedgerRevokeImportDataVO {

    // ==================== 调整具体地址 ====================

    /** 序号（列0） */
    @ExcelProperty(index = 0)
    private Integer index;

    /** 所属市（列1） */
    @ExcelProperty(index = 1)
    private String cityName;

    /** 所属区县（列2） */
    @ExcelProperty(index = 2)
    private String districtName;

    /** 所属乡镇（列3） */
    @ExcelProperty(index = 3)
    private String townName;

    /** 所属村名称（列4） */
    @ExcelProperty(index = 4)
    private String villageName;

    /** 详细地址（列5） */
    @ExcelProperty(index = 5)
    private String address;

    // ==================== 原改造农户详细信息 ====================

    /** 原户主姓名（列6） */
    @ExcelProperty(index = 6)
    private String username;

    /** 原身份证号（列7） */
    @ExcelProperty(index = 7)
    private String idCard;

    /** 原联系方式（列8） */
    @ExcelProperty(index = 8)
    private String phone;

    /** 原备注（列9） */
    @ExcelProperty(index = 9)
    private String originalRemark;

    /** 变更前使用状态（列10），可选值：1-正常、2-销户 */
    @ExcelProperty(index = 10)
    private String beforeUseStatus;

    // ==================== 变更调整相关信息 ====================

    /** 变更后户主姓名（列11） */
    @ExcelProperty(index = 11)
    private String changedUsername;

    /** 变更后身份证号（列12） */
    @ExcelProperty(index = 12)
    private String changedIdCard;

    /** 变更后联系方式（列13） */
    @ExcelProperty(index = 13)
    private String changedPhone;

    /** 调整时间（列14），格式 yyyy/MM/dd */
    @ExcelProperty(index = 14)
    private String adjustTime;

    /** 变更后使用状态（列15），可选值：1-正常、2-销户 */
    @ExcelProperty(index = 15)
    private String useStatus;

    /** 变更后备注（列16） */
    @ExcelProperty(index = 16)
    private String remark;
}
