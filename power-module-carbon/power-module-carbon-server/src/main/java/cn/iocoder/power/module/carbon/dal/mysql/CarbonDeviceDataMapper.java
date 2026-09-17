package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonDeviceDataMapper extends BaseMapperX<CarbonDeviceDataDO> {

    /**
     * 构建联表查询 Wrapper（SELECT + LEFT JOIN + 筛选条件）
     * SQL 层面按 device_code 去重，取每设备 report_time 最新的一条
     */
    private static MPJLambdaWrapper<CarbonDeviceDataDO> buildLatestWrapper(CarbonDeviceDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonDeviceDataDO> wrapper = new MPJLambdaWrapper<CarbonDeviceDataDO>()
                .selectAll(CarbonDeviceDataDO.class)
                // 用户信息字段（user_code 为本表字段，由 selectAll 带出）
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getIdCard,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode)
                // LEFT JOIN carbon_user_info（按 user_code 关联）
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getUserCode,
                        CarbonDeviceDataDO::getUserCode)
                // 选中导出：按数据ID筛选
                .in(reqVO.getIds() != null && !reqVO.getIds().isEmpty(),
                        CarbonDeviceDataDO::getId, reqVO.getIds())
                // SQL去重：只取每个 device_code 最新一条记录的 id
                .inSql(CarbonDeviceDataDO::getId,
                        "SELECT id FROM (" +
                        "  SELECT id, ROW_NUMBER() OVER (PARTITION BY device_code ORDER BY report_time DESC) AS rn" +
                        "  FROM carbon_device_data WHERE deleted = 0" +
                        ") tmp WHERE rn = 1")
                // 设备/用户筛选条件
                .like(StrUtil.isNotBlank(reqVO.getDeviceCode()),
                        CarbonDeviceDataDO::getDeviceCode, reqVO.getDeviceCode())
                .eq(StrUtil.isNotBlank(reqVO.getDeviceType()),
                        CarbonDeviceDataDO::getDeviceType, reqVO.getDeviceType())
                .like(StrUtil.isNotBlank(reqVO.getDeviceName()),
                        CarbonDeviceDataDO::getDeviceName, reqVO.getDeviceName())
                .eq(StrUtil.isNotBlank(reqVO.getDeviceStatus()),
                        CarbonDeviceDataDO::getDeviceStatus, reqVO.getDeviceStatus())
                .eq(StrUtil.isNotBlank(reqVO.getUserCode()),
                        CarbonDeviceDataDO::getUserCode, reqVO.getUserCode())
                .like(StrUtil.isNotBlank(reqVO.getUsername()),
                        CarbonUserInfoDO::getUsername, reqVO.getUsername())
                // 同步时间范围
                .ge(reqVO.getReportTimeStart() != null,
                        CarbonDeviceDataDO::getReportTime, reqVO.getReportTimeStart())
                .le(reqVO.getReportTimeEnd() != null,
                        CarbonDeviceDataDO::getReportTime, reqVO.getReportTimeEnd())
                .orderByDesc(CarbonDeviceDataDO::getReportTime);
        return wrapper;
    }

    /**
     * 联表分页查询：每设备最新一条设备数据 + 用户信息
     */
    default PageResult<CarbonDeviceDataWithUserDTO> selectLatestPageWithUser(
            CarbonDeviceDataPageReqVO reqVO, int pageNo, int pageSize) {
        MPJLambdaWrapper<CarbonDeviceDataDO> wrapper = buildLatestWrapper(reqVO);
        Page<CarbonDeviceDataWithUserDTO> page = new Page<>(pageNo, pageSize);
        page = selectJoinPage(page, CarbonDeviceDataWithUserDTO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询（不分页，用于导出等场景）：每设备最新一条设备数据 + 用户信息
     */
    default List<CarbonDeviceDataWithUserDTO> selectLatestListWithUser(CarbonDeviceDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonDeviceDataDO> wrapper = buildLatestWrapper(reqVO);
        return selectJoinList(CarbonDeviceDataWithUserDTO.class, wrapper);
    }

    /**
     * 按设备编码查询所有记录（用于详情页，同步时间倒序）
     */
    default List<CarbonDeviceDataDO> selectListByDeviceCode(String deviceCode) {
        return selectList(new LambdaQueryWrapper<CarbonDeviceDataDO>()
                .eq(CarbonDeviceDataDO::getDeviceCode, deviceCode)
                .orderByDesc(CarbonDeviceDataDO::getReportTime));
    }

    /**
     * 按用户编码和时间范围查询设备数据（按同步时间升序），用于采暖季用电量计算
     */
    default List<CarbonDeviceDataDO> selectListByUserCodeAndTimeRange(String userCode,
                                                                      java.time.LocalDateTime start,
                                                                      java.time.LocalDateTime end) {
        return selectList(new LambdaQueryWrapper<CarbonDeviceDataDO>()
                .eq(CarbonDeviceDataDO::getUserCode, userCode)
                .ge(CarbonDeviceDataDO::getReportTime, start)
                .le(CarbonDeviceDataDO::getReportTime, end)
                .orderByAsc(CarbonDeviceDataDO::getReportTime));
    }

    /**
     * 批量按用户编码集合和时间范围查询设备数据（按同步时间升序），用于采暖季用电量批量计算
     */
    default List<CarbonDeviceDataDO> selectListByUserCodesAndTimeRange(java.util.Collection<String> userCodes,
                                                                        java.time.LocalDateTime start,
                                                                        java.time.LocalDateTime end) {
        return selectList(new LambdaQueryWrapper<CarbonDeviceDataDO>()
                .in(CarbonDeviceDataDO::getUserCode, userCodes)
                .ge(CarbonDeviceDataDO::getReportTime, start)
                .le(CarbonDeviceDataDO::getReportTime, end)
                .orderByAsc(CarbonDeviceDataDO::getReportTime));
    }

    /**
     * 查询指定时间范围内有数据的用户ID集合（去重，联表查询用户信息）
     */
    default List<Long> selectDistinctUserIdsByTimeRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        MPJLambdaWrapper<CarbonDeviceDataDO> wrapper = new MPJLambdaWrapper<CarbonDeviceDataDO>()
                .select(CarbonUserInfoDO::getId)
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getUserCode, CarbonDeviceDataDO::getUserCode)
                .ge(CarbonDeviceDataDO::getReportTime, start)
                .le(CarbonDeviceDataDO::getReportTime, end)
                .groupBy(CarbonUserInfoDO::getId);
        return selectObjs(wrapper);
    }

    /**
     * 联表查询结果 DTO：包含设备数据和用户基本信息
     */
    @lombok.Data
    class CarbonDeviceDataWithUserDTO extends CarbonDeviceDataDO {
        // 用户信息（userCode 继承自 CarbonDeviceDataDO）
        private String username;
        private String idCard;
        private Long provinceCode;
        private Long cityCode;
        private Long districtCode;
    }
}
