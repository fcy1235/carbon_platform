package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonProjectUserMapper extends BaseMapperX<CarbonProjectUserDO> {

    default List<CarbonProjectUserDO> selectListByProjectId(Long projectId) {
        return selectList(new LambdaQueryWrapperX<CarbonProjectUserDO>()
                .eq(CarbonProjectUserDO::getProjectId, projectId)
                .orderByAsc(CarbonProjectUserDO::getId));
    }

    default void deleteByProjectId(Long projectId) {
        delete(new LambdaQueryWrapperX<CarbonProjectUserDO>()
                .eq(CarbonProjectUserDO::getProjectId, projectId));
    }
}
