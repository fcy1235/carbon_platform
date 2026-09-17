package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonContactDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonContactMapper extends BaseMapperX<CarbonContactDO> {

    default PageResult<CarbonContactDO> selectPage(CarbonContactPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonContactDO>()
                .likeIfPresent(CarbonContactDO::getName, reqVO.getName())
                .likeIfPresent(CarbonContactDO::getPhone, reqVO.getPhone())
                .likeIfPresent(CarbonContactDO::getCompany, reqVO.getCompany())
                .in(CollUtil.isNotEmpty(reqVO.getIds()), CarbonContactDO::getId, reqVO.getIds())
                .orderByDesc(CarbonContactDO::getId));
    }

    default List<CarbonContactDO> selectList(CarbonContactPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonContactDO>()
                .likeIfPresent(CarbonContactDO::getName, reqVO.getName())
                .likeIfPresent(CarbonContactDO::getPhone, reqVO.getPhone())
                .likeIfPresent(CarbonContactDO::getCompany, reqVO.getCompany())
                .in(CollUtil.isNotEmpty(reqVO.getIds()), CarbonContactDO::getId, reqVO.getIds())
                .orderByDesc(CarbonContactDO::getId));
    }
}
