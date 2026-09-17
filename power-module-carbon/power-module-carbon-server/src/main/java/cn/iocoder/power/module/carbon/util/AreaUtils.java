package cn.iocoder.power.module.carbon.util;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonReformAccountReportRespVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 省市区工具类，统一处理编码转中文名称
 */
@Component
public class AreaUtils {

    @Resource
    private AreaNameCache areaNameCache;

    /**
     * 填充用户基本信息的省市区乡镇村中文名称（含 provinceName/cityName/districtName/townName/villageName/division）
     */
    public void fillAreaNames(List<CarbonUserInfoRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<Long, String> areaMap = buildAreaNameMap(list);
        list.forEach(item -> {
            String province = areaMap.get(item.getProvinceCode());
            String city = areaMap.get(item.getCityCode());
            String district = areaMap.get(item.getDistrictCode());
            String town = areaMap.get(item.getTownCode());
            String village = areaMap.get(item.getVillageCode());
            item.setProvinceName(province);
            item.setCityName(city);
            item.setDistrictName(district);
            item.setTownName(town);
            item.setVillageName(village);
            item.setDivision(StringUtils.join(province, city, district, town, village));
        });
    }

    public void fillReformAreaNames(CarbonReformAccountReportRespVO item) {
        if (item == null) {
            return;
        }
        java.util.Set<Long> areaIds = new java.util.HashSet<>();
        areaIds.add(item.getProvinceCode());
        areaIds.add(item.getCityCode());
        areaIds.add(item.getDistrictCode());
        areaIds.add(item.getTownCode());
        areaIds.add(item.getVillageCode());
        areaIds.remove(null);
        Map<Long, String> areaMap = areaNameCache.getNameMap(areaIds);
        item.setDistrictName(areaMap.get(item.getDistrictCode()));
        item.setTownName(areaMap.get(item.getTownCode()));
        item.setVillageName(areaMap.get(item.getVillageCode()));
    }

    public void fillReformAreaNames(List<CarbonReformAccountReportRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        java.util.Set<Long> areaIds = new java.util.HashSet<>();
        for (CarbonReformAccountReportRespVO item : list) {
            areaIds.add(item.getProvinceCode());
            areaIds.add(item.getCityCode());
            areaIds.add(item.getDistrictCode());
            areaIds.add(item.getTownCode());
            areaIds.add(item.getVillageCode());
        }
        areaIds.remove(null);
        Map<Long, String> areaMap = areaNameCache.getNameMap(areaIds);
        list.forEach(item -> {
            item.setDistrictName(areaMap.get(item.getDistrictCode()));
            item.setTownName(areaMap.get(item.getTownCode()));
            item.setVillageName(areaMap.get(item.getVillageCode()));
        });
    }

    /**
     * 仅填充基准线的所属行政区 division
     */
    public void fillDivision(List<CarbonBaselineRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<Long, String> areaMap = buildAreaNameMap(list);
        list.forEach(item -> {
            String province = areaMap.get(item.getProvinceCode());
            String city = areaMap.get(item.getCityCode());
            String district = areaMap.get(item.getDistrictCode());
            item.setDivision(StringUtils.join(province, city, district));
        });
    }

    private <T> Map<Long, String> buildAreaNameMap(List<T> list) {
        Set<Long> areaIds = CollectionUtils.convertSet(list, item -> {
            if (item instanceof CarbonUserInfoRespVO) {
                return ((CarbonUserInfoRespVO) item).getProvinceCode();
            }
            if (item instanceof CarbonBaselineRespVO) {
                return ((CarbonBaselineRespVO) item).getProvinceCode();
            }
            if (item instanceof CarbonReformAccountReportRespVO) {
                return ((CarbonReformAccountReportRespVO) item).getProvinceCode();
            }
            return null;
        });
        areaIds.addAll(CollectionUtils.convertSet(list, item -> {
            if (item instanceof CarbonUserInfoRespVO) {
                return ((CarbonUserInfoRespVO) item).getCityCode();
            }
            if (item instanceof CarbonBaselineRespVO) {
                return ((CarbonBaselineRespVO) item).getCityCode();
            }
            if (item instanceof CarbonReformAccountReportRespVO) {
                return ((CarbonReformAccountReportRespVO) item).getCityCode();
            }
            return null;
        }));
        areaIds.addAll(CollectionUtils.convertSet(list, item -> {
            if (item instanceof CarbonUserInfoRespVO) {
                return ((CarbonUserInfoRespVO) item).getDistrictCode();
            }
            if (item instanceof CarbonBaselineRespVO) {
                return ((CarbonBaselineRespVO) item).getDistrictCode();
            }
            if (item instanceof CarbonReformAccountReportRespVO) {
                return ((CarbonReformAccountReportRespVO) item).getDistrictCode();
            }
            return null;
        }));
        areaIds.addAll(CollectionUtils.convertSet(list, item -> {
            if (item instanceof CarbonUserInfoRespVO) {
                return ((CarbonUserInfoRespVO) item).getTownCode();
            }
            if (item instanceof CarbonReformAccountReportRespVO) {
                return ((CarbonReformAccountReportRespVO) item).getTownCode();
            }
            return null;
        }));
        areaIds.addAll(CollectionUtils.convertSet(list, item -> {
            if (item instanceof CarbonUserInfoRespVO) {
                return ((CarbonUserInfoRespVO) item).getVillageCode();
            }
            if (item instanceof CarbonReformAccountReportRespVO) {
                return ((CarbonReformAccountReportRespVO) item).getVillageCode();
            }
            return null;
        }));
        areaIds.remove(null);
        return areaNameCache.getNameMap(areaIds);
    }
}
