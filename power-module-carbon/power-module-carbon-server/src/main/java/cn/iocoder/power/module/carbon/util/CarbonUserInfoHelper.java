package cn.iocoder.power.module.carbon.util;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoListReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceRespVO;
import cn.iocoder.power.module.carbon.service.CarbonUserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 用户基本信息辅助类，封装用户信息查询与关联填充
 */
@Component
public class CarbonUserInfoHelper {

    @Resource
    private CarbonUserInfoService carbonUserInfoService;

    @Resource
    private AreaNameCache areaNameCache;

    /**
     * 根据查询条件查询关联的用户信息列表（不分页，用于导出等场景）
     */
    public List<CarbonUserInfoRespVO> queryUserInfo(Long carbonUserInfoId, String username,
                                                     Long provinceCode, Long cityCode, Long districtCode) {
        CarbonUserInfoListReqVO reqVO = new CarbonUserInfoListReqVO();
        reqVO.setId(carbonUserInfoId);
        reqVO.setUsername(username);
        reqVO.setProvinceCode(provinceCode);
        reqVO.setCityCode(cityCode);
        reqVO.setDistrictCode(districtCode);
        return carbonUserInfoService.getUserInfoList(reqVO);
    }

    /**
     * 根据查询条件分页查询关联的用户信息（用于分页场景）
     */
    public PageResult<CarbonUserInfoRespVO> queryUserInfoPage(Long carbonUserInfoId, String username,
                                                               Long provinceCode, Long cityCode, Long districtCode,
                                                               Integer pageNo, Integer pageSize) {
        CarbonUserInfoPageReqVO pageReqVO = new CarbonUserInfoPageReqVO();
        pageReqVO.setPageNo(pageNo);
        pageReqVO.setPageSize(pageSize);
        pageReqVO.setId(carbonUserInfoId);
        pageReqVO.setUsername(username);
        pageReqVO.setProvinceCode(provinceCode);
        pageReqVO.setCityCode(cityCode);
        pageReqVO.setDistrictCode(districtCode);
        return carbonUserInfoService.getUserInfoPage(pageReqVO);
    }

    /**
     * 根据用户 ID 集合查询用户信息并转为 Map
     */
    public Map<Long, CarbonUserInfoRespVO> queryUserInfoMap(Collection<Long> userInfoIds) {
        CarbonUserInfoListReqVO reqVO = new CarbonUserInfoListReqVO();
        reqVO.setIds(userInfoIds);
        List<CarbonUserInfoRespVO> userVoList = carbonUserInfoService.getUserInfoList(reqVO);
        return CollectionUtils.convertMap(userVoList, CarbonUserInfoRespVO::getId, userVo -> userVo);
    }

    public List<CarbonUserInfoRespVO> queryUserInfoList(Collection<Long> userInfoIds) {
        CarbonUserInfoListReqVO reqVO = new CarbonUserInfoListReqVO();
        reqVO.setIds(userInfoIds);
        return carbonUserInfoService.getUserInfoList(reqVO);
    }

    /**
     * 批量填充目标列表中的 username、division、address
     *
     * @param list          目标列表
     * @param userInfoMap   用户信息 Map
     * @param getUserInfoId 获取目标对象关联的用户 ID
     * @param setUsername   设置用户姓名
     * @param setDivision   设置所属行政区
     * @param setAddress    设置详细地址
     */
    public <T> void fillUserInfo(List<T> list, Map<Long, CarbonUserInfoRespVO> userInfoMap,
                                 Function<T, Long> getUserInfoId,
                                 BiConsumer<T, String> setUsername,
                                 BiConsumer<T, String> setDivision,
                                 BiConsumer<T, String> setAddress) {
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(item -> {
            CarbonUserInfoRespVO user = userInfoMap.getOrDefault(getUserInfoId.apply(item), new CarbonUserInfoRespVO());
            setUsername.accept(item, user.getUsername());
            setDivision.accept(item, user.getDivision());
            setAddress.accept(item, user.getAddress());
        });
    }

    /**
     * 填充设备管理关联的用户信息（扩展字段：改造类别、改造类型、乡镇/街道、村/社区、燃气用户编码、燃气表具号、电表号）
     */
    public void fillDeviceUserInfo(List<CarbonDeviceRespVO> list, Map<Long, CarbonUserInfoRespVO> userInfoMap) {
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(item -> {
            CarbonUserInfoRespVO user = userInfoMap.getOrDefault(item.getCarbonUserInfoId(), new CarbonUserInfoRespVO());
            item.setUsername(user.getUsername());
            item.setIdCard(user.getIdCard());
            item.setPhone(user.getPhone());
            item.setDivision(user.getDivision());
            item.setAddress(user.getAddress());
            item.setReformType(user.getReformType());
            item.setReformMode(user.getReformMode());
            item.setTownName(user.getTownName());
            item.setVillageName(user.getVillageName());
            item.setGasUserCode(user.getGasUserCode());
            item.setGasId(user.getGasId());
            item.setElectricityId(user.getElectricityId());
        });
    }

    /**
     * 通用 division 填充：根据省市区编码批量查询区县名称，拼接为 division 字符串
     * 适用于联表查询后的 VO/DTO 填充
     *
     * @param list            目标列表
     * @param getProvinceCode 获取省编码
     * @param getCityCode     获取市编码
     * @param getDistrictCode 获取区编码
     * @param setDivision     设置 division 字段
     */
    public <T> void fillDivision(List<T> list,
                                 Function<T, Long> getProvinceCode,
                                 Function<T, Long> getCityCode,
                                 Function<T, Long> getDistrictCode,
                                 BiConsumer<T, String> setDivision) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 收集所有不为空的区划编码
        Set<Long> areaIds = new HashSet<>();
        for (T item : list) {
            Long pc = getProvinceCode.apply(item);
            Long cc = getCityCode.apply(item);
            Long dc = getDistrictCode.apply(item);
            if (pc != null) areaIds.add(pc);
            if (cc != null) areaIds.add(cc);
            if (dc != null) areaIds.add(dc);
        }
        if (areaIds.isEmpty()) {
            return;
        }
        // 本地缓存优先批量查询区县名称（未命中部分才远程查询）
        Map<Long, String> areaMap = areaNameCache.getNameMap(areaIds);
        // 填充 division
        list.forEach(item -> {
            String province = areaMap.get(getProvinceCode.apply(item));
            String city = areaMap.get(getCityCode.apply(item));
            String district = areaMap.get(getDistrictCode.apply(item));
            setDivision.accept(item, StringUtils.join(province, city, district));
        });
    }
}
