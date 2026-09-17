package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyWithUserDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonSubsidyMapper extends BaseMapperX<CarbonSubsidyDO> {



    default PageResult<CarbonSubsidyDO> selectPage(CarbonSubsidyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonSubsidyDO>()

                .orderByDesc(CarbonSubsidyDO::getId));
    }

    default List<CarbonSubsidyDO> selectList(CarbonSubsidyPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonSubsidyDO>()

                .orderByDesc(CarbonSubsidyDO::getId));
    }

    /**
     * 构建联表查询条件
     */
    private MPJLambdaWrapper<CarbonSubsidyDO> buildWithUserWrapper(CarbonSubsidyPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonSubsidyDO> wrapper = new MPJLambdaWrapper<CarbonSubsidyDO>()
                // 补贴表字段
                .select(CarbonSubsidyDO::getId, CarbonSubsidyDO::getSubsidyCode,
                        CarbonSubsidyDO::getUserInfoId, CarbonSubsidyDO::getSubsidyAmount,

                        CarbonSubsidyDO::getCreateTime)
                // 用户信息表字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getReformType, CarbonUserInfoDO::getIdCard,
                        CarbonUserInfoDO::getPhone, CarbonUserInfoDO::getProvinceCode,
                        CarbonUserInfoDO::getCityCode, CarbonUserInfoDO::getDistrictCode)
                // LEFT JOIN carbon_user_info ON subsidy.user_info_id = user_info.id
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId,
                        CarbonSubsidyDO::getUserInfoId);

        // 如果传了ids，按ids查询
        if (CollUtil.isNotEmpty(reqVO.getIds())) {
            wrapper.in(CarbonSubsidyDO::getId, reqVO.getIds());
        } else {
            // 否则按筛选条件查询
            wrapper.like(reqVO.getUsername() != null, CarbonUserInfoDO::getUsername, reqVO.getUsername())
                    .like(reqVO.getAddress() != null, CarbonUserInfoDO::getAddress, reqVO.getAddress())
                    .eqIfExists( CarbonUserInfoDO::getReformType, reqVO.getReformType())
                    .eqIfExists(CarbonUserInfoDO::getProvinceCode, reqVO.getProvinceCode())
                    .eqIfExists( CarbonUserInfoDO::getCityCode, reqVO.getCityCode())
                    .eqIfExists( CarbonUserInfoDO::getDistrictCode, reqVO.getDistrictCode())
                    ;
        }

        wrapper.orderByDesc(CarbonSubsidyDO::getId);
        return wrapper;
    }

    /**
     * 联表分页查询：补贴 + 用户信息
     */
    default PageResult<CarbonSubsidyWithUserDO> selectPageWithUser(CarbonSubsidyPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonSubsidyDO> wrapper = buildWithUserWrapper(reqVO);
        Page<CarbonSubsidyWithUserDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        page = selectJoinPage(page, CarbonSubsidyWithUserDO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询列表：补贴 + 用户信息（用于导出）
     */
    default List<CarbonSubsidyWithUserDO> selectListWithUser(CarbonSubsidyPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonSubsidyDO> wrapper = buildWithUserWrapper(reqVO);
        return selectJoinList(CarbonSubsidyWithUserDO.class, wrapper);
    }

    /**
     * 联表查询单条：补贴 + 用户信息（用于详情查看）
     */
    default CarbonSubsidyWithUserDO selectByIdWithUser(Long id) {
        MPJLambdaWrapper<CarbonSubsidyDO> wrapper = new MPJLambdaWrapper<CarbonSubsidyDO>()
                // 补贴表字段
                .select(CarbonSubsidyDO::getId, CarbonSubsidyDO::getSubsidyCode,
                        CarbonSubsidyDO::getUserInfoId, CarbonSubsidyDO::getSubsidyAmount,
                        CarbonSubsidyDO::getCreateTime)
                // 用户信息表字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getReformType, CarbonUserInfoDO::getIdCard,
                        CarbonUserInfoDO::getPhone, CarbonUserInfoDO::getProvinceCode,
                        CarbonUserInfoDO::getCityCode, CarbonUserInfoDO::getDistrictCode)
                // LEFT JOIN carbon_user_info ON subsidy.user_info_id = user_info.id
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId,
                        CarbonSubsidyDO::getUserInfoId)
                .eq(CarbonSubsidyDO::getId, id);
        return selectJoinOne(CarbonSubsidyWithUserDO.class, wrapper);
    }
}
