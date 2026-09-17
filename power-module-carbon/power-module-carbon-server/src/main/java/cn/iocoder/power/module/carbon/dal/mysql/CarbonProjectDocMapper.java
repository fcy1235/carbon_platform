package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDocDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonProjectDocMapper extends BaseMapperX<CarbonProjectDocDO> {

    default List<CarbonProjectDocDO> selectListByProjectId(Long projectId) {
        return selectList(new LambdaQueryWrapperX<CarbonProjectDocDO>()
                .eq(CarbonProjectDocDO::getProjectId, projectId)
                .orderByDesc(CarbonProjectDocDO::getUploadTime));
    }

    default void deleteByProjectId(Long projectId) {
        delete(new LambdaQueryWrapperX<CarbonProjectDocDO>()
                .eq(CarbonProjectDocDO::getProjectId, projectId));
    }

    default void deleteByProjectIdAndDocName(Long projectId, String docName) {
        delete(new LambdaQueryWrapperX<CarbonProjectDocDO>()
                .eq(CarbonProjectDocDO::getProjectId, projectId)
                .eq(CarbonProjectDocDO::getDocName, docName));
    }
}
