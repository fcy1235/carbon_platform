package cn.iocoder.power.module.iot.dal.dataobject.alert;

import cn.iocoder.power.framework.common.enums.CommonStatusEnum;
import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.power.framework.mybatis.core.type.IntegerListTypeHandler;
import cn.iocoder.power.framework.mybatis.core.type.LongListTypeHandler;
import cn.iocoder.power.module.iot.dal.dataobject.rule.IotSceneRuleDO;
import cn.iocoder.power.module.iot.enums.DictTypeConstants;
import cn.iocoder.power.module.iot.enums.alert.IotAlertReceiveTypeEnum;
import cn.iocoder.power.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * IoT 告警配置 DO
 *
 * 
 */
@TableName(value = "iot_alert_config", autoResultMap = true)
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotAlertConfigDO extends BaseDO {

    /**
     * 配置编号
     */
     @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 配置名称
     */
    private String name;
    /**
     * 配置描述
     */
    private String description;
    /**
     * 配置状态
     *
     * 字典 {@link DictTypeConstants#ALERT_LEVEL}
     */
    private Integer level;
    /**
     * 配置状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 关联的场景联动规则编号数组
     *
     * 关联 {@link IotSceneRuleDO#getId()}
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> sceneRuleIds;

    /**
     * 接收的用户编号数组
     *
     * 关联 {@link AdminUserRespDTO#getId()}
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> receiveUserIds;
    /**
     * 接收的类型数组
     *
     * 枚举 {@link IotAlertReceiveTypeEnum}
     */
    @TableField(typeHandler = IntegerListTypeHandler.class)
    private List<Integer> receiveTypes;

    /**
     * 短信模板编号
     *
     * 关联 SmsTemplateDO 的 code 属性
     */
    private String smsTemplateCode;
    /**
     * 邮件模板编号
     *
     * 关联 MailTemplateDO 的 code 属性
     */
    private String mailTemplateCode;
    /**
     * 站内信模板编号
     *
     * 关联 NotifyTemplateDO 的 code 属性
     */
    private String notifyTemplateCode;
}
