package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.CarbonAccountingPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoListReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonUserInfoMapper extends BaseMapperX<CarbonUserInfoDO> {

    default CarbonUserInfoDO selectByUserCode(String userCode) {
        return selectOne(CarbonUserInfoDO::getUserCode, userCode);
    }

    default CarbonUserInfoDO selectByIdCard(String idCard) {
        return selectOne(CarbonUserInfoDO::getIdCard, idCard);
    }

    default CarbonUserInfoDO selectByElectricityId(String electricityId) {
        return selectOne(CarbonUserInfoDO::getElectricityId, electricityId);
    }

    default CarbonUserInfoDO selectByGasId(String gasId) {
        return selectOne(CarbonUserInfoDO::getGasId, gasId);
    }

    /**
     * 根据身份证号和电力表户号查询用户
     */
    default CarbonUserInfoDO selectByIdCardAndElectricityId(String idCard, String electricityId) {
        return selectOne(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eq(CarbonUserInfoDO::getIdCard, idCard)
                .eq(CarbonUserInfoDO::getElectricityId, electricityId)
                .last("LIMIT 1"));
    }

    /**
     * 根据身份证号和燃气表户号查询用户
     */
    default CarbonUserInfoDO selectByIdCardAndGasId(String idCard, String gasId) {
        return selectOne(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eq(CarbonUserInfoDO::getIdCard, idCard)
                .eq(CarbonUserInfoDO::getGasId, gasId)
                .last("LIMIT 1"));
    }

    default CarbonUserInfoDO selectByIdCardAndAddress(String idCard, String address) {
        return selectOne(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eq(CarbonUserInfoDO::getIdCard, idCard)
                .eq(CarbonUserInfoDO::getAddress, address)
                .last("LIMIT 1"));
    }

    /**
     * 根据用户名+身份证号+地址查询唯一用户
     */
    default CarbonUserInfoDO selectByUsernameAndIdCardAndAddress(String username, String idCard, String address) {
        return selectOne(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eq(CarbonUserInfoDO::getUsername, username)
                .eq(CarbonUserInfoDO::getIdCard, idCard)
                .eq(CarbonUserInfoDO::getAddress, address)
                .last("LIMIT 1"));
    }

    default PageResult<CarbonUserInfoDO> selectPage(CarbonUserInfoPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eqIfPresent(CarbonUserInfoDO::getId, pageReqVO.getId())
                .likeIfPresent(CarbonUserInfoDO::getUsername, pageReqVO.getUsername())
                .likeIfPresent(CarbonUserInfoDO::getIdCard, pageReqVO.getIdCard())
                .likeIfPresent(CarbonUserInfoDO::getPhone, pageReqVO.getPhone())
                .eqIfPresent(CarbonUserInfoDO::getReformType, pageReqVO.getReformType())
                .eqIfPresent(CarbonUserInfoDO::getReformMode, pageReqVO.getReformMode())
                .eqIfPresent(CarbonUserInfoDO::getDataSource, pageReqVO.getDataSource())
                .eqIfPresent(CarbonUserInfoDO::getProvinceCode, pageReqVO.getProvinceCode())
                .eqIfPresent(CarbonUserInfoDO::getCityCode, pageReqVO.getCityCode())
                .eqIfPresent(CarbonUserInfoDO::getDistrictCode, pageReqVO.getDistrictCode())
                .eqIfPresent(CarbonUserInfoDO::getTownCode, pageReqVO.getTownCode())
                .eqIfPresent(CarbonUserInfoDO::getVillageCode, pageReqVO.getVillageCode())
                .eqIfPresent(CarbonUserInfoDO::getReformYear, pageReqVO.getReformYear())
                .eqIfPresent(CarbonUserInfoDO::getReformBatch, pageReqVO.getReformBatch())
                .eqIfPresent(CarbonUserInfoDO::getUserCategory, pageReqVO.getUserCategory())
                .eqIfPresent(CarbonUserInfoDO::getUseStatus, pageReqVO.getUseStatus())
                .orderByDesc(CarbonUserInfoDO::getId));
    }

   default List<CarbonUserInfoDO> selectList(CarbonUserInfoListReqVO listReqVO){
        return selectList(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .likeIfPresent(CarbonUserInfoDO::getUsername, listReqVO.getUsername())
                .likeIfPresent(CarbonUserInfoDO::getIdCard, listReqVO.getIdCard())
                .likeIfPresent(CarbonUserInfoDO::getPhone, listReqVO.getPhone())
                .eqIfPresent(CarbonUserInfoDO::getReformType, listReqVO.getReformType())
                .eqIfPresent(CarbonUserInfoDO::getReformMode, listReqVO.getReformMode())
                .eqIfPresent(CarbonUserInfoDO::getDataSource, listReqVO.getDataSource())
                .eqIfPresent(CarbonUserInfoDO::getId, listReqVO.getId())
                .inIfPresent(CarbonUserInfoDO::getId, listReqVO.getIds())
                .eqIfPresent(CarbonUserInfoDO::getReformYear, listReqVO.getReformYear())
                .eqIfPresent(CarbonUserInfoDO::getReformBatch, listReqVO.getReformBatch())
                .eqIfPresent(CarbonUserInfoDO::getUserCategory, listReqVO.getUserCategory())
                .eqIfPresent(CarbonUserInfoDO::getUseStatus, listReqVO.getUseStatus())
        );
   }

    /**
     * 分页查询尚未核算的用户（用户表为主表，排除核算周期与查询周期存在重叠的已核算用户）
     * 核算周期为日期区间，判断重叠用范围比较（核算开始 <= 查询结束 且 核算结束 >= 查询开始），不能等值匹配
     * @param reqVO 查询条件
     * @param userIdsWithData 在取暖季有数据的用户ID集合，为空则返回空结果
     */
    default PageResult<CarbonUserInfoDO> selectPageUnaccounted(CarbonAccountingPageReqVO reqVO, List<Long> userIdsWithData) {
        // 没有在取暖季有数据的用户，直接返回空
        if (userIdsWithData == null || userIdsWithData.isEmpty()) {
            return PageResult.empty();
        }
        // 同一用户、核算周期与查询周期重叠即视为已核算（deleted 由子查询自行过滤）
        // NOT IN 子查询需排除 NULL，否则任一 NULL 会导致整页为空
        String subSql = "SELECT carbon_user_info_id FROM carbon_accounting WHERE deleted = 0" +
                " AND carbon_user_info_id IS NOT NULL" +
                " AND accounting_period_start <= '" + reqVO.getAccountingPeriodEnd() + "'" +
                " AND accounting_period_end >= '" + reqVO.getAccountingPeriodStart() + "'";
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonUserInfoDO>()
                .eqIfPresent(CarbonUserInfoDO::getReformType, reqVO.getReformType())
                .eqIfPresent(CarbonUserInfoDO::getProvinceCode, reqVO.getProvinceCode())
                .eqIfPresent(CarbonUserInfoDO::getCityCode, reqVO.getCityCode())
                .eqIfPresent(CarbonUserInfoDO::getDistrictCode, reqVO.getDistrictCode())
                .in(CarbonUserInfoDO::getId, userIdsWithData)
                .likeIfPresent(CarbonUserInfoDO::getUsername, reqVO.getUsername())
                .likeIfPresent(CarbonUserInfoDO::getIdCard, reqVO.getIdCard())
                .notInSql(CarbonUserInfoDO::getId, subSql)
                .orderByDesc(CarbonUserInfoDO::getId));
    }
}
