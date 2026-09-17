package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonDocumentMapper extends BaseMapperX<CarbonDocumentDO> {

    default PageResult<CarbonDocumentDO> selectPage(CarbonDocumentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonDocumentDO>()
                .likeIfPresent(CarbonDocumentDO::getDocTitle, reqVO.getDocTitle())
                .likeIfPresent(CarbonDocumentDO::getDocName, reqVO.getDocName())
                .likeIfPresent(CarbonDocumentDO::getUploader, reqVO.getUploader())
                .orderByDesc(CarbonDocumentDO::getId));
    }

    default CarbonDocumentDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonDocumentDO>()
                .orderByDesc(CarbonDocumentDO::getId)
                .last("limit 1"));
    }

    default List<CarbonDocumentDO> selectList(CarbonDocumentReqVO reqVO){
        return selectList(new LambdaQueryWrapperX<CarbonDocumentDO>()
                .likeIfPresent(CarbonDocumentDO::getDocTitle, reqVO.getDocTitle())
                .likeIfPresent(CarbonDocumentDO::getDocName, reqVO.getDocName()));
    }
}
