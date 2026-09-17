package cn.iocoder.power.module.iot.dal.dataobject.device;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

/**
 * IoT 设备分组 DO
 *
 * 
 */
@TableName("iot_device_group")
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceGroupDO extends BaseDO {

    /**
     * 分组 ID
     */
     @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分组名字
     */
    private String name;
    /**
     * 分组状态
     *
     * 枚举 {@link cn.iocoder.power.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 分组描述
     */
    private String description;

}