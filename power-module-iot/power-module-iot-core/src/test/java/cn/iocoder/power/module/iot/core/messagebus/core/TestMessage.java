package cn.iocoder.power.module.iot.core.messagebus.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestMessage {

    private String nickname;

    private Integer age;

}