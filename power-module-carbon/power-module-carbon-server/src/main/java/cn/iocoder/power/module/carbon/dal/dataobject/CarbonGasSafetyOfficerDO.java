package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@TableName("carbon_gas_safety_officer")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasSafetyOfficerDO extends TenantBaseDO {

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
     * 身份证正面图片地址
     */
    private String idCardFrontImage;

    /**
     * 身份证反面图片地址
     */
    private String idCardBackImage;

    /**
     * 学历
     */
    private String education;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 从业资格证编号
     */
    private String qualificationNo;

    /**
     * 负责区域1（省,市,区,街道,村 逗号分隔的区划代码）
     */
    private String area1;

    /**
     * 负责区域2（省,市,区,街道,村 逗号分隔的区划代码）
     */
    private String area2;

    /**
     * 负责区域3（省,市,区,街道,村 逗号分隔的区划代码）
     */
    private String area3;

    /**
     * 负责区域4（省,市,区,街道,村 逗号分隔的区划代码）
     */
    private String area4;

    /**
     * 负责区域5（省,市,区,街道,村 逗号分隔的区划代码）
     */
    private String area5;

    /**
     * 所属燃气企业（维保企业ID）
     */
    private Long enterpriseId;

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
