package cn.iocoder.power.module.infra.dal.mysql.db;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * 
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
