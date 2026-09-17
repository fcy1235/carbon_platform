package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonDocumentDetailMapper extends BaseMapperX<CarbonDocumentDetailDO> {

    default List<CarbonDocumentDetailDO> selectListByDocumentId(Long documentId) {
        return selectList(new LambdaQueryWrapperX<CarbonDocumentDetailDO>()
                .eq(CarbonDocumentDetailDO::getDocumentId, documentId)
                .orderByAsc(CarbonDocumentDetailDO::getId));
    }

    default void deleteByDocumentId(Long documentId) {
        delete(new LambdaQueryWrapperX<CarbonDocumentDetailDO>()
                .eq(CarbonDocumentDetailDO::getDocumentId, documentId));
    }
}
