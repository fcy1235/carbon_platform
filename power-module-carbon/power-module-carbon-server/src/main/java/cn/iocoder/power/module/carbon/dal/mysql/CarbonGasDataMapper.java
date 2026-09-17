package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonGasDataMapper extends BaseMapperX<CarbonGasDataDO> {


    /**
     * 按用户 + 读数时间范围查询燃气数据（按读数时间升序，用于核算周期内首末表底差计算）
     */
    default List<CarbonGasDataDO> selectListByUserIdsAndTimeRange(Collection<Long> carbonUserInfoIds,
                                                                  LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonGasDataDO>()
                .in(CarbonGasDataDO::getCarbonUserInfoId, carbonUserInfoIds)
                .ge(CarbonGasDataDO::getReadingTime, startTime)
                .le(CarbonGasDataDO::getReadingTime, endTime)
                .orderByAsc(CarbonGasDataDO::getReadingTime));
    }

    /**
     * 查询某用户在指定时间范围内的最早一条有效抄表记录（周期起始表底，用于详情单行场景）
     * 有索引时走索引范围扫描 + 早停（仅取 1 行），远优于全量聚合
     */
    default CarbonGasDataDO selectFirstByUserIdAndTimeRange(Long carbonUserInfoId,
                                                            LocalDateTime startTime, LocalDateTime endTime) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonGasDataDO>()
                .eq(CarbonGasDataDO::getCarbonUserInfoId, carbonUserInfoId)
                .ge(CarbonGasDataDO::getReadingTime, startTime)
                .le(CarbonGasDataDO::getReadingTime, endTime)
                .isNotNull(CarbonGasDataDO::getReadingTime)
                .isNotNull(CarbonGasDataDO::getCurrentTotal)
                .orderByAsc(CarbonGasDataDO::getReadingTime)
                .last("LIMIT 1"));
    }

    /**
     * 查询某用户在指定时间范围内的最晚一条有效抄表记录（周期结束表底，用于详情单行场景）
     */
    default CarbonGasDataDO selectLastByUserIdAndTimeRange(Long carbonUserInfoId,
                                                           LocalDateTime startTime, LocalDateTime endTime) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonGasDataDO>()
                .eq(CarbonGasDataDO::getCarbonUserInfoId, carbonUserInfoId)
                .ge(CarbonGasDataDO::getReadingTime, startTime)
                .le(CarbonGasDataDO::getReadingTime, endTime)
                .isNotNull(CarbonGasDataDO::getReadingTime)
                .isNotNull(CarbonGasDataDO::getCurrentTotal)
                .orderByDesc(CarbonGasDataDO::getReadingTime)
                .last("LIMIT 1"));
    }

    /**
     * 构建联表查询 Wrapper（SELECT + LEFT JOIN + 筛选条件）
     * SQL层面按 gas_id 去重，取每户 reading_time 最新的一条
     */
    private static MPJLambdaWrapper<CarbonGasDataDO> buildLatestWrapper(CarbonGasDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonGasDataDO> wrapper = new MPJLambdaWrapper<CarbonGasDataDO>()
                // 燃气数据字段
                .select(CarbonGasDataDO::getId, CarbonGasDataDO::getDataSource,
                        CarbonGasDataDO::getCarbonUserInfoId, CarbonGasDataDO::getGasId,
                        CarbonGasDataDO::getCurrentTotal, CarbonGasDataDO::getReadingTime,
                        CarbonGasDataDO::getCurrentUsage, CarbonGasDataDO::getLastReading)
                // 用户信息字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode,CarbonUserInfoDO::getIdCard)
                // LEFT JOIN carbon_user_info
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId,
                        CarbonGasDataDO::getCarbonUserInfoId)
                // 选中导出：按数据ID筛选
                .in(reqVO.getIds() != null && !reqVO.getIds().isEmpty(),
                        CarbonGasDataDO::getId, reqVO.getIds())
                // SQL去重：只取每个 gas_id 最新一条记录的 id
                .inSql(CarbonGasDataDO::getId,
                        "SELECT id FROM (" +
                        "  SELECT id, ROW_NUMBER() OVER (PARTITION BY gas_id ORDER BY reading_time DESC) AS rn" +
                        "  FROM carbon_gas_data WHERE deleted = 0" +
                        ") tmp WHERE rn = 1")
                // 燃气户号筛选条件
                .likeIfExists(
                        CarbonGasDataDO::getGasId, reqVO.getGasId());
        // 添加公共的用户信息筛选条件
        CarbonMeterMapperHelper.addUserInfoConditionsFromReqVO(wrapper, reqVO);
        return wrapper;
    }

    /**
     * 联表分页查询：每户最新一条燃气数据 + 用户信息
     */
    default PageResult<CarbonGasDataWithUserDTO> selectLatestPageWithUser(
            CarbonGasDataPageReqVO reqVO, int pageNo, int pageSize) {
        MPJLambdaWrapper<CarbonGasDataDO> wrapper = buildLatestWrapper(reqVO);
        Page<CarbonGasDataWithUserDTO> page = new Page<>(pageNo, pageSize);
        page = selectJoinPage(page, CarbonGasDataWithUserDTO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询（不分页，用于导出等场景）：每户最新一条燃气数据 + 用户信息
     */
    default List<CarbonGasDataWithUserDTO> selectLatestListWithUser(CarbonGasDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonGasDataDO> wrapper = buildLatestWrapper(reqVO);
        return selectJoinList(CarbonGasDataWithUserDTO.class, wrapper);
    }

    /**
     * 查询每个燃气户号的最新一条记录（不分页，用于同步等场景）
     * 查询所有记录后由调用方在 Java 层按 gas_id 分组取最新
     */
    default List<CarbonGasDataDO> selectLatestList(CarbonGasDataPageReqVO reqVO) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonGasDataDO>()
                .in(reqVO.getCarbonUserInfoIds() != null && !reqVO.getCarbonUserInfoIds().isEmpty(),
                        CarbonGasDataDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .eq(reqVO.getGasId() != null && !reqVO.getGasId().isEmpty(),
                        CarbonGasDataDO::getGasId, reqVO.getGasId())
                .orderByDesc(CarbonGasDataDO::getReadingTime));
    }

    /**
     * 按燃气户号查询所有记录（用于详情页）
     */
    default List<CarbonGasDataDO> selectListByGasId(String gasId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonGasDataDO>()
                .eq(CarbonGasDataDO::getGasId, gasId)
                .orderByDesc(CarbonGasDataDO::getReadingTime));
    }

    /**
     * 联表查询结果 DTO：包含燃气数据和用户基本信息
     */
    @lombok.Data
    class CarbonGasDataWithUserDTO {
        private Long id;
        private String dataSource;
        private Long carbonUserInfoId;
        private String gasId;
        private java.math.BigDecimal currentTotal;
        private java.time.LocalDateTime readingTime;
        private java.math.BigDecimal currentUsage;
        private java.math.BigDecimal lastReading;
        // 用户信息
        private String username;
        private String address;
        private Long provinceCode;
        private Long cityCode;
        private Long districtCode;
        private String idCard;
    }
}
