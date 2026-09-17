package cn.iocoder.power.module.carbon.framework.datapermission.config;

import cn.iocoder.power.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * carbon 模块的数据权限配置
 */
@Configuration(proxyBeanMethods = false, value = "carbonDataPermissionConfiguration")
public class CarbonDataPermissionConfiguration {

    @Bean("carbonDeptDataPermissionRuleCustomizer")
    public DeptDataPermissionRuleCustomizer deptDataPermissionRuleCustomizer() {
        return rule -> {
            // 台账文件表：基于 dept_id 做部门数据权限过滤
            rule.addDeptColumn(CarbonLedgerFileDO.class);
            // 台账上报表：基于 dept_id 做部门数据权限过滤
            rule.addDeptColumn(CarbonLedgerReportDO.class);
            // 台账导入表：基于 dept_id 做部门数据权限过滤
            rule.addDeptColumn(CarbonLedgerImportTaskDO.class);
        };
    }

}
