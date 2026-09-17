package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarbonUserInfoImportVO {

    /** 台账记录的顺序编号 */
//    @ExcelProperty("序号")
//    private Integer index;

    /** 改造户所属的市级行政区域 */
    @ExcelProperty("所属市")
    private String cityName;

    /** 改造户所属的区县行政区域 */
    @ExcelProperty("所属区县")
    private String districtName;

    /** 改造户所属的乡镇行政区域 */
    @ExcelProperty("所属乡镇")
    private String townName;

    /** 改造户所属的行政村名称 */
    @ExcelProperty("所属村名称")
    private String villageName;

    /** 改造户的详细居住地址 */
    @ExcelProperty("地址")
    private String address;

    /** 改造户的户主姓名 */
    @ExcelProperty("户主姓名")
    private String username;

    /** 户主的居民身份证号码 */
    @ExcelProperty("身份证号")
    private String idCard;

    /** 户主的联系电话 */
    @ExcelProperty("联系方式")
    private String phone;

    /** 改造房屋的采暖面积，单位为平方米 */
    @ExcelProperty("采暖面积（㎡）")
    private BigDecimal heatingArea;

    /** 完成清洁取暖改造的年份 */
    @ExcelProperty("改造年限")
    private String reformYear;

    /**
     * 改造类别
     * 可选值：1-电代煤、2-气代煤
     */
    @ExcelProperty(value = "改造类别（可选：1-电代煤、2-气代煤）")
    @DictFormat("carbon_reform_type")
    private String reformType;

    /**
     * 改造类型
     * 可选值：1-气代煤、2-直热式电锅炉、3-蓄热式电锅炉、4-电壁挂炉、
     *        5-直热式电暖器、6-蓄热式电暖器、7-石墨烯、8-聚能、
     *        9-空气源（能）热风机、10-空气源热泵、11-地源热泵、12-集中供热、
     *        13-光伏、14-光热、15-醇基燃料、16-生物质
     */
    @ExcelProperty(value = "改造类型（可选：1-气代煤、2-直热式电锅炉、3-蓄热式电锅炉…）")
    @DictFormat("carbon_reform_mode")
    private String reformMode;

    /**
     * 改造批次
     * 可选值：1-国家部委下达任务、2-省下达任务、3-市级自定完成、4-市级自定-农户自改
     */
    @ExcelProperty(value = "改造批次（可选：1-国家部委下达任务、2-省下达任务、3-市级自定完成、4-市级自定-农户自改）")
    @DictFormat("carbon_reform_batch")
    private String reformBatch;

    /**
     * 发放补贴方式
     * 可选值：1-银行卡、2-一卡通、3-现金、4-气表、5-电表
     */
    @ExcelProperty(value = "发放补贴方式（可选：1-银行卡、2-一卡通、3-现金、4-气表、5-电表）")
    @DictFormat("carbon_subsidy_method")
    private String subsidyMethod;

    /**
     * 房屋用途
     * 可选值：1-用于居住、2-用于经营、3-商住两用
     */
    @ExcelProperty(value = "房屋用途（可选：1-用于居住、2-用于经营、3-商住两用）")
    @DictFormat("carbon_house_usage")
    private String houseUsage;

    /**
     * 用户分类
     * 可选值：1-正常用户、2-闲置用户、3-其他用户
     */
    @ExcelProperty(value = "用户分类（可选：1-正常用户、2-闲置用户、3-其他用户）")
    @DictFormat("carbon_user_category")
    private String userCategory;

    /**
     * 使用状态
     * 可选值：1-正常、2-销户
     */
    @ExcelProperty(value = "使用状态（可选：1-正常、2-销户）")
    @DictFormat("carbon_use_status")
    private String useStatus;

    /** 其他需要补充说明的信息 */
    @ExcelProperty("备注")
    private String remark;

    // ==================== 以下为业务所需字段，不在导入模板列中 ====================

    /** 数据来源 */
    @ExcelProperty(value = "数据来源（1-导入数据，2-接口数据）")
    @DictFormat("carbon_user_data_source")
    private String dataSource;


    /** 所属省（用于行政区编码反查） */
    private String provinceName;

    /** 电力户号 */
    private String electricityId;

    /** 燃气户号 */
    private String gasId;

    /** 燃气用户编码 */
    private String gasUserCode;
}
