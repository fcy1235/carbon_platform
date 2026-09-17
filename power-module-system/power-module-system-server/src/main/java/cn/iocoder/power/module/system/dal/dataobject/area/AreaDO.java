package cn.iocoder.power.module.system.dal.dataobject.area;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区 DO
 *
 * @author system
 */
@TableName("system_area")
@Data
@EqualsAndHashCode(callSuper = true)
public class AreaDO extends BaseDO {

    /**
     * 顶级节点
     */
    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 父级编号
     */
    private Long parentId;

    /**
     * 行政级别
     */
    private Integer level;

    /**
     * 名称
     */
    private String name;

    /**
     * 拼音首字母
     */
    private String pinyinPrefix;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 扩展ID
     */
    private String extId;

    /**
     * 扩展名称
     */
    private String extName;

}
