package cn.iocoder.power.module.iot.dal.dataobject.product;

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
 * IoT 产品分类 DO
 *
 * 
 */
@TableName("iot_product_category")
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotProductCategoryDO extends BaseDO {

    /**
     * 分类 ID
     */
     @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类名字
     */
    private String name;
    /**
     * 分类排序
     */
    private Integer sort;
    /**
     * 分类状态
     *
     * 枚举 {@link cn.iocoder.power.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 分类描述
     */
    private String description;

}