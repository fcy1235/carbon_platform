package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectProgressDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonProjectProgressMapper extends BaseMapperX<CarbonProjectProgressDO> {

    default List<CarbonProjectProgressDO> selectListByProjectId(Long projectId) {
        return selectList(new LambdaQueryWrapperX<CarbonProjectProgressDO>()
                .eq(CarbonProjectProgressDO::getProjectId, projectId)
                .orderByDesc(CarbonProjectProgressDO::getUpdateTime));
    }

    default void deleteByProjectId(Long projectId) {
        delete(new LambdaQueryWrapperX<CarbonProjectProgressDO>()
                .eq(CarbonProjectProgressDO::getProjectId, projectId));
    }
}
