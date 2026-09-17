package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonContactDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonProjectMapper extends BaseMapperX<CarbonProjectDO> {

    default CarbonProjectDO selectByProjectCode(String projectCode) {
        return selectOne(CarbonProjectDO::getProjectCode, projectCode);
    }

    /**
     * 连表分页查询：项目 + 联系人
     */
    default PageResult<CarbonProjectWithContactDTO> selectPageWithContact(CarbonProjectPageReqVO reqVO) {
        return selectJoinPage(reqVO, CarbonProjectWithContactDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 连表列表查询：项目 + 联系人
     */
    default List<CarbonProjectWithContactDTO> selectListWithContact(CarbonProjectPageReqVO reqVO) {
        return selectJoinList(CarbonProjectWithContactDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 构建关联查询的 Wrapper
     */
    private MPJLambdaWrapper<CarbonProjectDO> buildJoinWrapper(CarbonProjectPageReqVO reqVO) {
        return new MPJLambdaWrapper<CarbonProjectDO>()
                // 项目字段
                .selectAll(CarbonProjectDO.class)
                // 联系人字段
                .selectAs(CarbonContactDO::getName, CarbonProjectWithContactDTO::getContactName)
                .selectAs(CarbonContactDO::getPhone, CarbonProjectWithContactDTO::getContactPhone)
                // LEFT JOIN
                .leftJoin(CarbonContactDO.class, CarbonContactDO::getId, CarbonProjectDO::getContactId)
                // 项目筛选条件
                .likeIfExists(CarbonProjectDO::getProjectName, reqVO.getProjectName())
                .eqIfExists( CarbonProjectDO::getProjectStatus, reqVO.getProjectStatus())
                .eqIfExists(CarbonProjectDO::getProvinceCode, reqVO.getProvinceCode())
                .eqIfExists(CarbonProjectDO::getCityCode, reqVO.getCityCode())
                .eqIfExists(CarbonProjectDO::getDistrictCode, reqVO.getDistrictCode())
                .ge(reqVO.getPlanStartDateStart() != null, CarbonProjectDO::getPlanStartDate, reqVO.getPlanStartDateStart())
                .le(reqVO.getPlanStartDateEnd() != null, CarbonProjectDO::getPlanStartDate, reqVO.getPlanStartDateEnd())
                .ge(reqVO.getPlanEndDateStart() != null, CarbonProjectDO::getPlanEndDate, reqVO.getPlanEndDateStart())
                .le(reqVO.getPlanEndDateEnd() != null, CarbonProjectDO::getPlanEndDate, reqVO.getPlanEndDateEnd())
                .in(CollUtil.isNotEmpty(reqVO.getIds()), CarbonProjectDO::getId, reqVO.getIds())
                // 联系人筛选条件
                .likeIfExists(CarbonContactDO::getName, reqVO.getContactName())
                .orderByDesc(CarbonProjectDO::getId);
    }

    default CarbonProjectDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonProjectDO>()
                .orderByDesc(CarbonProjectDO::getId)
                .last("LIMIT 1"));
    }
}
