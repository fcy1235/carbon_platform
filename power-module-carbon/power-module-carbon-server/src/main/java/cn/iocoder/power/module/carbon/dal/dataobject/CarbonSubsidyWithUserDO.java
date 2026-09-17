package cn.iocoder.power.module.carbon.dal.dataobject;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 补贴管理联表查询 DO（包含关联的用户信息）
 */
@Data
public class CarbonSubsidyWithUserDO {

    /**
     * 编号
     */
    private Long id;

    /**
     * 补贴编码
     */
    private String subsidyCode;

    /**
     * 用户信息ID
     */
    private Long userInfoId;

    /**
     * 补贴金额
     */
    private BigDecimal subsidyAmount;

    /**
     * 状态
     */
    private String status;

    /**
     * 发放时间
     */
    private LocalDateTime grantTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // ========== 用户信息（来自用户信息表） ==========

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 地址
     */
    private String address;

    /**
     * 改造类型
     */
    private String reformType;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 省编码
     */
    private Long provinceCode;

    /**
     * 市编码
     */
    private Long cityCode;

    /**
     * 区编码
     */
    private Long districtCode;
}
