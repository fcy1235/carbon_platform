package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 台账导入明细 DO
 */
@TableName("carbon_ledger_import_detail")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerImportDetailDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    // ==================== 用户基本信息字段（与 carbon_user_info 一致） ====================

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区县名称
     */
    private String districtName;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 村名称
     */
    private String villageName;

    /**
     * 地址
     */
    private String address;

    /**
     * 采暖面积(m²)
     */
    private BigDecimal heatingArea;

    /**
     * 改造类型
     */
    private String reformType;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 电力用户编号
     */
    private String electricityId;

    /**
     * 燃气用户编号
     */
    private String gasId;

    /**
     * 改造年份
     */
    private String reformYear;

    /**
     * 改造批次
     */
    private String reformBatch;

    /**
     * 补贴方式
     */
    private String subsidyMethod;

    /**
     * 房屋用途
     */
    private String houseUsage;

    /**
     * 用户类别
     */
    private String userCategory;

    /**
     * 燃气用户编码
     */
    private String gasUserCode;

    /**
     * 使用状态
     */
    private String useStatus;

    /**
     * 备注
     */
    private String remark;

    // ==================== 导入相关字段 ====================

    /**
     * 导入状态：1-成功 2-失败
     */
    private String importStatus;

    /**
     * 错误信息
     */
    private String errorMsg;
}
