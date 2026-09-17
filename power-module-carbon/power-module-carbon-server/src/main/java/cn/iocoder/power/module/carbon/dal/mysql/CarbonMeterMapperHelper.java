package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

/**
 * 燃气/电力 Mapper 公共辅助类
 * 提取公共的联表查询条件构建逻辑
 */
public class CarbonMeterMapperHelper {

    private CarbonMeterMapperHelper() {
    }

    /**
     * 向 MPJLambdaWrapper 添加用户信息筛选条件（公共部分）
     * 适用于电力和燃气数据的联表查询
     *
     * @param wrapper           MPJLambdaWrapper
     * @param carbonUserInfoId  用户信息编号（精确匹配）
     * @param username          用户姓名（模糊匹配）
     * @param provinceCode      省编码
     * @param cityCode          市编码
     * @param districtCode      区编码
     * @param <T>               主表实体类型
     * @return 链式调用的 wrapper
     */
    public static <T> MPJLambdaWrapper<T> addUserInfoConditions(
            MPJLambdaWrapper<T> wrapper,
            Long carbonUserInfoId, String username,
            Long provinceCode, Long cityCode, Long districtCode) {
        return wrapper
                .eq(carbonUserInfoId != null, CarbonUserInfoDO::getId, carbonUserInfoId)
                .like(username != null && !username.isEmpty(), CarbonUserInfoDO::getUsername, username)
                .eq(provinceCode != null, CarbonUserInfoDO::getProvinceCode, provinceCode)
                .eq(cityCode != null, CarbonUserInfoDO::getCityCode, cityCode)
                .eq(districtCode != null, CarbonUserInfoDO::getDistrictCode, districtCode);
    }

    /**
     * 向 MPJLambdaWrapper 添加用户信息筛选条件（从分页请求 VO 提取参数）
     *
     * @param wrapper MPJLambdaWrapper
     * @param reqVO   分页请求 VO（同时适用于电力和燃气）
     * @param <T>     主表实体类型
     * @return 链式调用的 wrapper
     */
    public static <T> MPJLambdaWrapper<T> addUserInfoConditionsFromReqVO(
            MPJLambdaWrapper<T> wrapper,
            CarbonElectricityPageReqVO reqVO) {
        return addUserInfoConditions(wrapper,
                reqVO.getCarbonUserInfoId(), reqVO.getUsername(),
                reqVO.getProvinceCode(), reqVO.getCityCode(), reqVO.getDistrictCode());
    }

    /**
     * 向 MPJLambdaWrapper 添加用户信息筛选条件（从燃气请求 VO 提取参数）
     */
    public static <T> MPJLambdaWrapper<T> addUserInfoConditionsFromReqVO(
            MPJLambdaWrapper<T> wrapper,
            CarbonGasDataPageReqVO reqVO) {
        return addUserInfoConditions(wrapper,
                reqVO.getCarbonUserInfoId(), reqVO.getUsername(),
                reqVO.getProvinceCode(), reqVO.getCityCode(), reqVO.getDistrictCode());
    }


}
