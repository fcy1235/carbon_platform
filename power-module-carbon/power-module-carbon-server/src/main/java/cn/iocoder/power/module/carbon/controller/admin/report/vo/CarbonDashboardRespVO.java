package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 首页仪表盘统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarbonDashboardRespVO {

    // ==================== 1. 取暖改造户数统计 ====================
    @Schema(description = "取暖改造户数统计")
    private ReformHouseholdStats reformHouseholdStats;

    // ==================== 2. 减碳排量 ====================
    @Schema(description = "减碳排量统计")
    private CarbonReductionStats carbonReductionStats;

    // ==================== 3. 两员人数 ====================
    @Schema(description = "两员人数统计")
    private StaffStats staffStats;

    // ==================== 4. 维保企业总数 ====================
    @Schema(description = "维保企业总数统计")
    private MaintenanceStats maintenanceStats;

    // ==================== 5. 改造类别统计 ====================
    @Schema(description = "改造类别统计列表（气代煤、电代煤）")
    private List<ReformCategoryItem> reformCategoryStats;

    // ==================== 6. 石家庄市各区县碳排放量 ====================
    @Schema(description = "各区县碳排放量列表（柱状图）")
    private List<DistrictCarbonEmission> districtCarbonEmissions;

    // ==================== 7. 石家庄市区域分布 ====================
    @Schema(description = "区域分布列表（地图）")
    private List<RegionDistributionItem> regionDistribution;

    // ==================== 8. 设备状态 ====================
    @Schema(description = "设备状态统计列表（故障、停运、正常）")
    private List<DeviceStatusItem> deviceStatusStats;

    // ==================== 9. 石家庄市各区县双代实施情况 ====================
    @Schema(description = "各区县双代实施情况列表")
    private List<DistrictReformStats> districtReformStats;

    // ==================== 内部类定义 ====================

    /**
     * 1. 取暖改造户数统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReformHouseholdStats {
        @Schema(description = "气代煤户数")
        private Long gasCoalCount;

        @Schema(description = "电代煤户数")
        private Long electricCoalCount;

        @Schema(description = "总户数")
        private Long totalCount;
    }

    /**
     * 2. 减碳排量统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarbonReductionStats {
        @Schema(description = "基准线排放量（tCO₂e）")
        private BigDecimal baselineEmission;

        @Schema(description = "实际排放量（tCO₂e）")
        private BigDecimal actualEmission;

        @Schema(description = "总碳减排量（tCO₂e）")
        private BigDecimal totalReduction;
    }

    /**
     * 3. 两员人数统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffStats {
        @Schema(description = "安全员人数")
        private Long safetyOfficerCount;

        @Schema(description = "协管员人数")
        private Long coordinatorCount;

        @Schema(description = "总人数")
        private Long totalCount;
    }

    /**
     * 4. 维保企业总数统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceStats {
        @Schema(description = "网点总数")
        private Long stationCount;

        @Schema(description = "覆盖村数")
        private Long coveredVillageCount;

        @Schema(description = "维保企业总数")
        private Long enterpriseCount;
    }

    /**
     * 5. 改造类别统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReformCategoryItem {
        @Schema(description = "类别名称（气代煤/电代煤）")
        private String name;

        @Schema(description = "数量")
        private Long count;

        @Schema(description = "百分比（如 62.50 表示 62.50%）")
        private BigDecimal percentage;
    }

    /**
     * 6. 区县碳排放量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictCarbonEmission {
        @Schema(description = "区县名称")
        private String districtName;

        @Schema(description = "区县编码")
        private Long districtCode;

        @Schema(description = "碳排放量（tCO₂e）")
        private BigDecimal carbonEmission;
    }

    /**
     * 7. 区域分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionDistributionItem {
        @Schema(description = "区域名称")
        private String regionName;

        @Schema(description = "区域编码")
        private Long regionCode;

        @Schema(description = "数值（如户数）")
        private Long value;
    }

    /**
     * 8. 设备状态统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceStatusItem {
        @Schema(description = "状态名称（正常/停运/故障）")
        private String name;

        @Schema(description = "状态编码")
        private String status;

        @Schema(description = "数量")
        private Long count;

        @Schema(description = "百分比（如 70.00 表示 70.00%）")
        private BigDecimal percentage;
    }

    /**
     * 9. 区县双代实施情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictReformStats {
        @Schema(description = "区县名称")
        private String districtName;

        @Schema(description = "区县编码")
        private Long districtCode;

        @Schema(description = "气代煤户数")
        private Long gasCoalCount;

        @Schema(description = "电代煤户数")
        private Long electricCoalCount;
    }
}
