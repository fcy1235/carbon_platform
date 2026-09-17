package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 变更户台账导入数据 Excel 解析 VO
 * <p>
 * 对应模板：农村"双代"确户台账调整对照表
 * 表头位于第3行（前两行为标题和分组），使用列序号（index）映射以避免重复列名冲突。
 * </p>
 */
@Data
@ExcelIgnoreUnannotated
public class CarbonLedgerChangeImportDataVO {

    // ==================== 调整具体地址（原地址区域信息） ====================

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

    /** 原详细地址（列5） */
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

    /** 原采暖面积（列9） */
    @ExcelProperty(index = 9)
    private BigDecimal heatingArea;

    /** 原备注（列10） */
    @ExcelProperty(index = 10)
    private String originalRemark;

    /** 改造年份（列11） */
    @ExcelProperty(index = 11)
    private String reformYear;

    /**
     * 改造类型（列12）
     * 可选值：1-气代煤、2-直热式电锅炉、3-蓄热式电锅炉、4-电壁挂炉、
     *        5-直热式电暖器、6-蓄热式电暖器、7-石墨烯、8-聚能、
     *        9-空气源（能）热风机、10-空气源热泵、11-地源热泵、12-集中供热、
     *        13-光伏、14-光热、15-醇基燃料、16-生物质
     */
    @ExcelProperty(index = 12)
    private String reformMode;

    // ==================== 变更调整相关信息 ====================

    /** 变更后户主姓名（列13） */
    @ExcelProperty(index = 13)
    private String changedUsername;

    /** 变更后身份证号（列14） */
    @ExcelProperty(index = 14)
    private String changedIdCard;

    /** 变更后联系方式（列15） */
    @ExcelProperty(index = 15)
    private String changedPhone;

    /** 调整时间（列16），格式 yyyy/MM/dd */
    @ExcelProperty(index = 16)
    private String adjustTime;

    /** 变更后详细地址（列17） */
    @ExcelProperty(index = 17)
    private String changedAddress;

    /** 变更后采暖面积（列18） */
    @ExcelProperty(index = 18)
    private BigDecimal changedHeatingArea;

    /** 变更后备注（列19） */
    @ExcelProperty(index = 19)
    private String remark;

    /** 调整原因（列20） */
    @ExcelProperty(index = 20)
    private String adjustReason;
}
