package cn.iocoder.power.module.iot.dal.dataobject.rule;

import cn.iocoder.power.framework.common.enums.CommonStatusEnum;
import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.power.module.iot.dal.dataobject.rule.config.IotAbstractDataSinkConfig;
import cn.iocoder.power.module.iot.enums.rule.IotDataSinkTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

/**
 * IoT 数据流转目的 DO
 *
 * 
 */
@TableName(value = "iot_data_sink", autoResultMap = true)
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDataSinkDO extends BaseDO {

    /**
     * 数据流转目的编号
     */
     @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 数据流转目的名称
     */
    private String name;
    /**
     * 数据流转目的描述
     */
    private String description;
    /**
     * 数据流转目的状态
     *
     *  枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 数据流转目的类型
     *
     * 枚举 {@link IotDataSinkTypeEnum}
     */
    private Integer type;
    /**
     * 数据流转目的配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IotAbstractDataSinkConfig config;

}
