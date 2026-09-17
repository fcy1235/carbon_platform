package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("carbon_gas_coordinator")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasCoordinatorDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 学历
     */
    private String education;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 是否为村两委干部成员 1是 2否
     */
    private String isVillageCommitteeMember;

    /**
     * 负责村（社区）区划代码（省,市,区,街道,村 逗号分隔）
     */
    private String area;

    /**
     * 入职培训企业类型
     */
    private String trainingEnterpriseType;

    /**
     * 维保企业ID
     */
    private Long enterpriseId;

    /**
     * 入职专业操作技能培训成绩
     */
    private BigDecimal trainingScore;

    /**
     * 人员状态 1在岗 2离岗
     */
    private String staffStatus;

    /**
     * 到岗日期
     */
    private LocalDate onDutyDate;

    /**
     * 离岗日期
     */
    private LocalDate offDutyDate;
}
