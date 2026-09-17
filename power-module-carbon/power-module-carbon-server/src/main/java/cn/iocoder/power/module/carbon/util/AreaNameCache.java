package cn.iocoder.power.module.carbon.util;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 行政区名称本地缓存。
 * 行政区数据基本静态，而 AreaApi 为跨服务 Feign 调用，高频场景（详情/列表/导出/分页）每次远程查询成本高，
 * 故按 id 缓存名称，未命中部分才发起远程批量查询并回填；缓存过期后自动失效（重启/次日重建）。
 */
@Component
public class AreaNameCache {

    private static final long MAX_SIZE = 50_000;
    private static final long EXPIRE_HOURS = 24;

    private final Cache<Long, String> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterWrite(EXPIRE_HOURS, TimeUnit.HOURS)
            .build();

    @Resource
    private AreaApi areaApi;

    /**
     * 批量获取行政区 id -> 名称 映射（本地缓存优先，未命中部分远程查询并回填缓存）
     */
    public Map<Long, String> getNameMap(Collection<Long> areaIds) {
        if (CollUtil.isEmpty(areaIds)) {
            return Collections.emptyMap();
        }
        Map<Long, String> result = new HashMap<>();
        Set<Long> missIds = new HashSet<>();
        for (Long id : areaIds) {
            if (id == null) {
                continue;
            }
            String name = cache.getIfPresent(id);
            if (name != null) {
                result.put(id, name);
            } else {
                missIds.add(id);
            }
        }
        if (missIds.isEmpty()) {
            return result;
        }
        // 未命中部分批量远程查询并回填
        CommonResult<List<AreaRespDTO>> areaDtoResult = areaApi.getAreaList(missIds);
        if (areaDtoResult != null && areaDtoResult.getData() != null) {
            for (AreaRespDTO area : areaDtoResult.getData()) {
                if (area.getId() != null && area.getName() != null) {
                    cache.put(area.getId(), area.getName());
                    result.put(area.getId(), area.getName());
                }
            }
        }
        return result;
    }

    /**
     * 清空缓存（行政区名称维护后调用，低频）
     */
    public void clear() {
        cache.invalidateAll();
    }
}
