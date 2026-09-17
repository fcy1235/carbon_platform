package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentParamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonDocumentParamMapper extends BaseMapperX<CarbonDocumentParamDO> {

    default List<CarbonDocumentParamDO> selectListByDocumentId(Long documentId) {
        return selectList(new LambdaQueryWrapperX<CarbonDocumentParamDO>()
                .eq(CarbonDocumentParamDO::getDocumentId, documentId)
                .orderByAsc(CarbonDocumentParamDO::getId));
    }

    default void deleteByDocumentId(Long documentId) {
        delete(new LambdaQueryWrapperX<CarbonDocumentParamDO>()
                .eq(CarbonDocumentParamDO::getDocumentId, documentId));
    }
}
