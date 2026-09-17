package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("carbon_project_progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonProjectProgressDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String stage;

    private String status;

    private String content;
}
