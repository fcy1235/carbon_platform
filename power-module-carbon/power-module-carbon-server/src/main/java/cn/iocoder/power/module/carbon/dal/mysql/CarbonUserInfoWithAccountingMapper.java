package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.CarbonAccountingPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 碳核算 + 用户信息 联表分页查询 Mapper
 * 以 CarbonAccountingDO（碳核算）为主表，CarbonUserInfoDO（用户信息）为关联表
 */
@Mapper
public interface CarbonUserInfoWithAccountingMapper extends BaseMapperX<CarbonAccountingDO> {

    /**
     * 连表分页查询：碳核算（主表，左连接用户信息）
     * 用于 getAccountUserInfoPage
     */
    default PageResult<CarbonUserInfoWithAccountingDTO> selectPageWithAccounting(CarbonAccountingPageReqVO reqVO) {
        return selectJoinPage(reqVO, CarbonUserInfoWithAccountingDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 连表分页查询：碳核算（主表，左连接用户信息）
     * 只返回有碳核算记录的数据，用于项目管理选择用户
     */
    default PageResult<CarbonUserInfoWithAccountingDTO> selectPageWithAccountingOnly(CarbonAccountingPageReqVO reqVO) {
        return selectJoinPage(reqVO, CarbonUserInfoWithAccountingDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 构建碳核算（主表） + 用户信息（关联表，左连接）的联查 Wrapper
     */
    private MPJLambdaWrapper<CarbonAccountingDO> buildJoinWrapper(CarbonAccountingPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonAccountingDO> wrapper = new MPJLambdaWrapper<CarbonAccountingDO>()
                // 碳核算字段（主表）：同名列直查，id/carbonUserInfoId 等按 DTO 同名属性自动映射
                .select(CarbonAccountingDO::getId, CarbonAccountingDO::getCarbonUserInfoId,
                        CarbonAccountingDO::getAccountingPeriodStart, CarbonAccountingDO::getAccountingPeriodEnd,
                        CarbonAccountingDO::getActualEmission, CarbonAccountingDO::getReduction)
                // 用户信息字段（关联表）：同名列直查；
                // 用户 id 由主表 carbon_user_info_id 承载（即 DTO.carbonUserInfoId），无需再 select 用户表 id 列
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getUserCode,
                        CarbonUserInfoDO::getIdCard, CarbonUserInfoDO::getPhone,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode, CarbonUserInfoDO::getTownCode,
                        CarbonUserInfoDO::getVillageCode, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getHeatingArea, CarbonUserInfoDO::getReformType,
                        CarbonUserInfoDO::getReformMode, CarbonUserInfoDO::getDataSource,
                        CarbonUserInfoDO::getElectricityId, CarbonUserInfoDO::getGasId,
                        CarbonUserInfoDO::getReformYear, CarbonUserInfoDO::getReformBatch,
                        CarbonUserInfoDO::getSubsidyMethod, CarbonUserInfoDO::getHouseUsage,
                        CarbonUserInfoDO::getUserCategory, CarbonUserInfoDO::getGasUserCode,
                        CarbonUserInfoDO::getUseStatus, CarbonUserInfoDO::getRemark,
                        CarbonUserInfoDO::getCreateTime);
        // LEFT JOIN 用户信息表（保留无关联用户的碳核算记录）
        wrapper.leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId, CarbonAccountingDO::getCarbonUserInfoId);
        // 用户信息筛选条件
        wrapper.like(StrUtil.isNotBlank(reqVO.getUsername()), CarbonUserInfoDO::getUsername, reqVO.getUsername())
                .like(StrUtil.isNotBlank(reqVO.getIdCard()), CarbonUserInfoDO::getIdCard, reqVO.getIdCard())
                .eq(StrUtil.isNotBlank(reqVO.getReformType()), CarbonUserInfoDO::getReformType, reqVO.getReformType())
                .eq(reqVO.getProvinceCode() != null, CarbonUserInfoDO::getProvinceCode, reqVO.getProvinceCode())
                .eq(reqVO.getCityCode() != null, CarbonUserInfoDO::getCityCode, reqVO.getCityCode())
                .eq(reqVO.getDistrictCode() != null, CarbonUserInfoDO::getDistrictCode, reqVO.getDistrictCode())
                // 碳核算筛选条件
                .eq(reqVO.getCarbonUserInfoId() != null, CarbonAccountingDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoId())
                .in(reqVO.getCarbonUserInfoIds() != null && !reqVO.getCarbonUserInfoIds().isEmpty(),
                        CarbonAccountingDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .orderByDesc(CarbonAccountingDO::getCreateTime);
        return wrapper;
    }
}
