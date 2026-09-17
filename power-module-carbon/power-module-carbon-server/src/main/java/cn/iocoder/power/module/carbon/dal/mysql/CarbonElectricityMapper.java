package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonElectricityMapper extends BaseMapperX<CarbonElectricityDO> {

    default CarbonElectricityDO selectByElectricityId(String electricityId) {
        return selectOne(CarbonElectricityDO::getElectricityId, electricityId);
    }

    /**
     * 查询某用户在指定时间范围内的最早一条有效抄表记录（周期起始表底，用于详情单行场景）
     * 有索引时走索引范围扫描 + 早停（仅取 1 行），远优于全量聚合
     */
    default CarbonElectricityDO selectFirstByUserIdAndTimeRange(Long carbonUserInfoId,
                                                                LocalDateTime startTime, LocalDateTime endTime) {
        return selectOne(new LambdaQueryWrapper<CarbonElectricityDO>()
                .eq(CarbonElectricityDO::getCarbonUserInfoId, carbonUserInfoId)
                .ge(CarbonElectricityDO::getReadingTime, startTime)
                .le(CarbonElectricityDO::getReadingTime, endTime)
                .isNotNull(CarbonElectricityDO::getReadingTime)
                .isNotNull(CarbonElectricityDO::getCurrentTotal)
                .orderByAsc(CarbonElectricityDO::getReadingTime)
                .last("LIMIT 1"));
    }

    /**
     * 查询某用户在指定时间范围内的最晚一条有效抄表记录（周期结束表底，用于详情单行场景）
     */
    default CarbonElectricityDO selectLastByUserIdAndTimeRange(Long carbonUserInfoId,
                                                               LocalDateTime startTime, LocalDateTime endTime) {
        return selectOne(new LambdaQueryWrapper<CarbonElectricityDO>()
                .eq(CarbonElectricityDO::getCarbonUserInfoId, carbonUserInfoId)
                .ge(CarbonElectricityDO::getReadingTime, startTime)
                .le(CarbonElectricityDO::getReadingTime, endTime)
                .isNotNull(CarbonElectricityDO::getReadingTime)
                .isNotNull(CarbonElectricityDO::getCurrentTotal)
                .orderByDesc(CarbonElectricityDO::getReadingTime)
                .last("LIMIT 1"));
    }

    /**
     * 按用户 + 读数时间范围查询电力数据（按读数时间升序，用于核算周期内首末表底差计算）
     */
    default List<CarbonElectricityDO> selectListByUserIdsAndTimeRange(Collection<Long> carbonUserInfoIds,
                                                                     LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapper<CarbonElectricityDO>()
                .in(CarbonElectricityDO::getCarbonUserInfoId, carbonUserInfoIds)
                .ge(CarbonElectricityDO::getReadingTime, startTime)
                .le(CarbonElectricityDO::getReadingTime, endTime)
                .orderByAsc(CarbonElectricityDO::getReadingTime));
    }

    /**
     * 构建联表查询 Wrapper（SELECT + LEFT JOIN + 筛选条件）
     * SQL层面按 electricity_id 去重，取每户 reading_time 最新的一条
     */
    private static MPJLambdaWrapper<CarbonElectricityDO> buildLatestWrapper(CarbonElectricityPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonElectricityDO> wrapper = new MPJLambdaWrapper<CarbonElectricityDO>()
                // 电力数据字段
                .select(CarbonElectricityDO::getId, CarbonElectricityDO::getDataSource,
                        CarbonElectricityDO::getCarbonUserInfoId, CarbonElectricityDO::getElectricityId,
                        CarbonElectricityDO::getCurrentTotal, CarbonElectricityDO::getReadingTime,
                        CarbonElectricityDO::getCurrentUsage, CarbonElectricityDO::getLastReading)
                // 用户信息字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode,CarbonUserInfoDO::getIdCard)
                // LEFT JOIN carbon_user_info
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId,
                        CarbonElectricityDO::getCarbonUserInfoId)
                // 选中导出：按数据ID筛选
                .in(reqVO.getIds() != null && !reqVO.getIds().isEmpty(),
                        CarbonElectricityDO::getId, reqVO.getIds())
                // SQL去重：只取每个 electricity_id 最新一条记录的 id
                .inSql(CarbonElectricityDO::getId,
                        "SELECT id FROM (" +
                        "  SELECT id, ROW_NUMBER() OVER (PARTITION BY electricity_id ORDER BY reading_time DESC) AS rn" +
                        "  FROM carbon_electricity WHERE deleted = 0" +
                        ") tmp WHERE rn = 1")
                // 电力户号筛选条件
                .likeIfExists(CarbonElectricityDO::getElectricityId, reqVO.getElectricityId());
        // 添加公共的用户信息筛选条件
        CarbonMeterMapperHelper.addUserInfoConditionsFromReqVO(wrapper, reqVO);
        return wrapper;
    }

    /**
     * 联表分页查询：每户最新一条电力数据 + 用户信息
     */
    default PageResult<CarbonElectricityWithUserDTO> selectLatestPageWithUser(
            CarbonElectricityPageReqVO reqVO, int pageNo, int pageSize) {
        MPJLambdaWrapper<CarbonElectricityDO> wrapper = buildLatestWrapper(reqVO);
        Page<CarbonElectricityWithUserDTO> page = new Page<>(pageNo, pageSize);
        page = selectJoinPage(page, CarbonElectricityWithUserDTO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询（不分页，用于导出等场景）：每户最新一条电力数据 + 用户信息
     */
    default List<CarbonElectricityWithUserDTO> selectLatestListWithUser(CarbonElectricityPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonElectricityDO> wrapper = buildLatestWrapper(reqVO);
        return selectJoinList(CarbonElectricityWithUserDTO.class, wrapper);
    }

    /**
     * 查询每个电力户号的最新一条记录（不分页，用于同步等场景）
     * 查询所有记录后由调用方在 Java 层按 electricity_id 分组取最新
     */
    default List<CarbonElectricityDO> selectLatestList(CarbonElectricityPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapper<CarbonElectricityDO>()
                .in(reqVO.getCarbonUserInfoIds() != null && !reqVO.getCarbonUserInfoIds().isEmpty(),
                        CarbonElectricityDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .eq(reqVO.getElectricityId() != null && !reqVO.getElectricityId().isEmpty(),
                        CarbonElectricityDO::getElectricityId, reqVO.getElectricityId())
                .orderByDesc(CarbonElectricityDO::getReadingTime));
    }

    /**
     * 按电力户号查询所有记录（用于详情页）
     */
    default List<CarbonElectricityDO> selectListByElectricityId(String electricityId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonElectricityDO>()
                .eq(CarbonElectricityDO::getElectricityId, electricityId)
                .orderByDesc(CarbonElectricityDO::getReadingTime));
    }

    /**
     * 联表查询结果 DTO：包含电力数据和用户基本信息
     */
    @lombok.Data
    class CarbonElectricityWithUserDTO {
        private Long id;
        private String dataSource;
        private Long carbonUserInfoId;
        private String electricityId;
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
