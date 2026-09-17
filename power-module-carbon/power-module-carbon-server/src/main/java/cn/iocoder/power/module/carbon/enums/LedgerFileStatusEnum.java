package cn.iocoder.power.module.carbon.enums;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账文件上传状态枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerFileStatusEnum implements ArrayValuable<String> {

    SUCCESS("1", "上传成功"),
    FAIL("2", "上传失败");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerFileStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static boolean isSuccess(String status) {
        return ObjUtil.equal(SUCCESS.type, status);
    }
}
