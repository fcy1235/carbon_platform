package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportRespVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportRespVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonReformAccountReportRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CarbonReportMapper {

    @Select("<script>" +
            "SELECT " +
            "  u.username, " +
            "  u.gas_user_code as gasUserCode, " +
            "  u.id_card as idCard, " +
            "  u.gas_id as gasId, " +
            "  u.remark, " +
            "  (SELECT reading_time FROM carbon_gas_data WHERE gas_id = u.gas_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time ASC LIMIT 1) as startTime, " +
            "  (SELECT current_total FROM carbon_gas_data WHERE gas_id = u.gas_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time ASC LIMIT 1) as startReading, " +
            "  (SELECT reading_time FROM carbon_gas_data WHERE gas_id = u.gas_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time DESC LIMIT 1) as endTime, " +
            "  (SELECT current_total FROM carbon_gas_data WHERE gas_id = u.gas_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time DESC LIMIT 1) as endReading " +
            "FROM carbon_user_info u " +
            "WHERE u.deleted = 0 AND u.gas_id IS NOT NULL " +
            "<if test='gasId != null and gasId != \"\"'>AND u.gas_id LIKE '%' || #{gasId} || '%'</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND (u.username LIKE '%' || #{keyword} || '%' OR u.id_card LIKE '%' || #{keyword} || '%')</if>" +
            "ORDER BY u.id DESC" +
            "</script>")
    List<CarbonGasUsageReportRespVO> selectGasUsageReport(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword,
            @Param("gasId") String gasId);

    @Select("<script>" +
            "SELECT " +
            "  u.username, " +
            "  u.id_card as idCard, " +
            "  u.electricity_id as electricityId, " +
            "  u.remark, " +
            "  (SELECT reading_time FROM carbon_electricity WHERE electricity_id = u.electricity_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time ASC LIMIT 1) as startTime, " +
            "  (SELECT current_total FROM carbon_electricity WHERE electricity_id = u.electricity_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time ASC LIMIT 1) as startReading, " +
            "  (SELECT reading_time FROM carbon_electricity WHERE electricity_id = u.electricity_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time DESC LIMIT 1) as endTime, " +
            "  (SELECT current_total FROM carbon_electricity WHERE electricity_id = u.electricity_id AND deleted = 0 " +
            "    <if test='startTime != null'>AND reading_time &gt;= #{startTime}</if>" +
            "    <if test='endTime != null'>AND reading_time &lt;= #{endTime}</if>" +
            "    ORDER BY reading_time DESC LIMIT 1) as endReading " +
            "FROM carbon_user_info u " +
            "WHERE u.deleted = 0 AND u.electricity_id IS NOT NULL " +
            "<if test='electricityId != null and electricityId != \"\"'>AND u.electricity_id LIKE '%' || #{electricityId} || '%'</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND (u.username LIKE '%' || #{keyword} || '%' OR u.id_card LIKE '%' || #{keyword} || '%')</if>" +
            "ORDER BY u.id DESC" +
            "</script>")
    List<CarbonElectricityUsageReportRespVO> selectElectricityUsageReport(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword,
            @Param("electricityId") String electricityId);

    @Select("<script>" +
            "SELECT " +
            "  u.username, " +
            "  u.id_card as idCard, " +
            "  u.phone, " +
            "  u.address, " +
            "  u.heating_area as heatingArea, " +
            "  u.reform_year as reformYear, " +
            "  u.use_status as useStatus, " +
            "  u.reform_type as reformType, " +
            "  u.remark, " +
            "  u.province_code as provinceCode, " +
            "  u.city_code as cityCode, " +
            "  u.district_code as districtCode, " +
            "  u.town_code as townCode, " +
            "  u.village_code as villageCode " +
            "FROM carbon_user_info u " +
            "WHERE u.deleted = 0 " +
            "<if test='provinceCode != null'>AND u.province_code = #{provinceCode}</if>" +
            "<if test='cityCode != null'>AND u.city_code = #{cityCode}</if>" +
            "<if test='districtCode != null'>AND u.district_code = #{districtCode}</if>" +
            "<if test='townCode != null'>AND u.town_code = #{townCode}</if>" +
            "<if test='villageCode != null'>AND u.village_code = #{villageCode}</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND (u.username LIKE '%' || #{keyword} || '%' OR u.id_card LIKE '%' || #{keyword} || '%')</if>" +
            "<if test='phone != null and phone != \"\"'>AND u.phone LIKE '%' || #{phone} || '%'</if>" +
            "<if test='reformType != null and reformType != \"\"'>AND u.reform_type = #{reformType}</if>" +
            "<if test='useStatus != null and useStatus != \"\"'>AND u.use_status = #{useStatus}</if>" +
            "<if test='reformYear != null and reformYear != \"\"'>AND u.reform_year = #{reformYear}</if>" +
            "ORDER BY u.id DESC" +
            "</script>")
    List<CarbonReformAccountReportRespVO> selectReformAccountReport(
            @Param("provinceCode") Long provinceCode,
            @Param("cityCode") Long cityCode,
            @Param("districtCode") Long districtCode,
            @Param("townCode") Long townCode,
            @Param("villageCode") Long villageCode,
            @Param("keyword") String keyword,
            @Param("phone") String phone,
            @Param("reformType") String reformType,
            @Param("useStatus") String useStatus,
            @Param("reformYear") String reformYear);

    // ==================== 仪表盘统计查询已迁移至各业务 Mapper，通过 MyBatis-Plus API 实现 ====================
}
