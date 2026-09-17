package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDevicePageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonDeviceMapper extends BaseMapperX<CarbonDeviceDO> {

    default CarbonDeviceDO selectByDeviceCode(String deviceCode) {
        return selectOne(CarbonDeviceDO::getDeviceCode, deviceCode);
    }

    /**
     * 简易列表查询：仅查询设备表，按设备自身字段筛选（用于轻量展示/下拉选择）
     */
    default List<CarbonDeviceDO> selectSimpleList(CarbonDevicePageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonDeviceDO>()
                .eqIfPresent(CarbonDeviceDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoId())
                .eqIfPresent(CarbonDeviceDO::getDeviceType, reqVO.getDeviceType())
                .eqIfPresent(CarbonDeviceDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CarbonDeviceDO::getIsCarbon, reqVO.getIsCarbon())
                .inIfPresent(CarbonDeviceDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .likeIfPresent(CarbonDeviceDO::getDeviceName, reqVO.getDeviceName())
                .likeIfPresent(CarbonDeviceDO::getDeviceBrandModel, reqVO.getDeviceBrandModel())
                .likeIfPresent(CarbonDeviceDO::getManufacturer, reqVO.getManufacturer())
                .inIfPresent(CarbonDeviceDO::getId, reqVO.getIds())
                .orderByDesc(CarbonDeviceDO::getId));
    }

    /**
     * 连表分页查询：设备 + 用户信息
     */
    default PageResult<CarbonDeviceWithUserDTO> selectPageWithUser(CarbonDevicePageReqVO reqVO) {
        return selectJoinPage(reqVO, CarbonDeviceWithUserDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 连表列表查询：设备 + 用户信息
     */
    default List<CarbonDeviceWithUserDTO> selectListWithUser(CarbonDevicePageReqVO reqVO) {
        return selectJoinList(CarbonDeviceWithUserDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 构建关联查询的 Wrapper
     */
    private MPJLambdaWrapper<CarbonDeviceDO> buildJoinWrapper(CarbonDevicePageReqVO reqVO) {
        return new MPJLambdaWrapper<CarbonDeviceDO>()
                // 设备字段
                .selectAll(CarbonDeviceDO.class)
                // 用户信息字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getIdCard,
                        CarbonUserInfoDO::getPhone, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode, CarbonUserInfoDO::getTownCode,
                        CarbonUserInfoDO::getVillageCode, CarbonUserInfoDO::getReformType,
                        CarbonUserInfoDO::getReformMode, CarbonUserInfoDO::getGasUserCode,
                        CarbonUserInfoDO::getGasId, CarbonUserInfoDO::getElectricityId)
                // LEFT JOIN
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId, CarbonDeviceDO::getCarbonUserInfoId)
                // 用户信息筛选条件
                .like(StrUtil.isNotBlank(reqVO.getUsername()), CarbonUserInfoDO::getUsername, reqVO.getUsername())
                .eqIfExists(CarbonUserInfoDO::getReformType, reqVO.getReformType())
                .eqIfExists(CarbonUserInfoDO::getProvinceCode, reqVO.getProvinceCode())
                .eqIfExists( CarbonUserInfoDO::getCityCode, reqVO.getCityCode())
                .eqIfExists( CarbonUserInfoDO::getDistrictCode, reqVO.getDistrictCode())
                // 设备筛选条件
                .eqIfExists( CarbonDeviceDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoId())
                .in(reqVO.getCarbonUserInfoIds() != null && !reqVO.getCarbonUserInfoIds().isEmpty(),
                        CarbonDeviceDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .likeIfExists(CarbonDeviceDO::getDeviceName, reqVO.getDeviceName())
                .eqIfExists( CarbonDeviceDO::getDeviceType, reqVO.getDeviceType())
                .likeIfExists(CarbonDeviceDO::getDeviceBrandModel, reqVO.getDeviceBrandModel())
                .likeIfExists( CarbonDeviceDO::getManufacturer, reqVO.getManufacturer())
                .eqIfExists(CarbonDeviceDO::getStatus, reqVO.getStatus())
                .in(CollUtil.isNotEmpty(reqVO.getIds()), CarbonDeviceDO::getId, reqVO.getIds())
                .orderByDesc(CarbonDeviceDO::getId);
    }

    default CarbonDeviceDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonDeviceDO>()
                .orderByDesc(CarbonDeviceDO::getId)
                .last("LIMIT 1"));
    }

    /**
     * 查询指定编码前缀（如 "GM-"）下最新的一条设备。
     * 编码序列为定长 6 位数字（GM-000001），按字符串倒序即为序号最大者。
     */
    default CarbonDeviceDO selectLatestByCodePrefix(String prefix) {
        return selectOne(new LambdaQueryWrapperX<CarbonDeviceDO>()
                .likeRight(CarbonDeviceDO::getDeviceCode, prefix + "-")
                .orderByDesc(CarbonDeviceDO::getDeviceCode)
                .last("LIMIT 1"));
    }
}
