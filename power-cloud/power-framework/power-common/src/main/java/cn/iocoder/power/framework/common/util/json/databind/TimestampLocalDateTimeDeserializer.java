package cn.iocoder.power.framework.common.util.json.databind;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 反序列化器（兼容字符串格式和时间戳）
 *
 * @author 老五
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 情况一：数字类型，按时间戳解析（兼容旧数据）
        if (p.currentToken().isNumeric()) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }

        // 情况二：字符串类型
        String text = p.getText();
        if (StrUtil.isBlank(text)) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, DEFAULT_FORMATTER);
        } catch (Exception e) {
            // 兼容：可能是时间戳字符串
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(text.trim())), ZoneId.systemDefault());
        }
    }

}
