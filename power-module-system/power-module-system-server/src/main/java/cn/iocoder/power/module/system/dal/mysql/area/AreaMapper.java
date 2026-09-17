package cn.iocoder.power.module.system.dal.mysql.area;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaListReqVO;
import cn.iocoder.power.module.system.controller.admin.area.vo.AreaPageReqVO;
import cn.iocoder.power.module.system.dal.dataobject.area.AreaDO;
import cn.iocoder.power.framework.common.pojo.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 行政区 Mapper
 *
 * @author system
 */
@Mapper
public interface AreaMapper extends BaseMapperX<AreaDO> {

    default List<AreaDO> selectList(AreaListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<AreaDO>()
                .eqIfPresent(AreaDO::getParentId, reqVO.getParentId())
                .likeIfPresent(AreaDO::getName, reqVO.getName())
                .inIfPresent(AreaDO::getLevel, reqVO.getLevels())
                .likeIfPresent(AreaDO::getExtName, reqVO.getName())
                .likeIfPresent(AreaDO::getPinyinPrefix, reqVO.getPinyinPrefix())
                .orderByAsc(AreaDO::getId));
    }

    default PageResult<AreaDO> selectPage(AreaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AreaDO>()
                .eqIfPresent(AreaDO::getParentId, reqVO.getParentId())
                .likeIfPresent(AreaDO::getName, reqVO.getName())
                .likeIfPresent(AreaDO::getExtName, reqVO.getName())
                .inIfPresent(AreaDO::getLevel, reqVO.getLevels())
                .likeIfPresent(AreaDO::getPinyinPrefix, reqVO.getPinyinPrefix())
                .orderByAsc(AreaDO::getId));
    }

    default AreaDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(new LambdaQueryWrapperX<AreaDO>()
                .eq(AreaDO::getParentId, parentId)
                .likeIfPresent(AreaDO::getExtName, name)
                .likeIfPresent(AreaDO::getName, name));
    }

    default List<AreaDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<AreaDO>()
                .eq(AreaDO::getParentId, parentId)
                .orderByAsc(AreaDO::getId));
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(new LambdaQueryWrapperX<AreaDO>()
                .eq(AreaDO::getParentId, parentId));
    }


}
