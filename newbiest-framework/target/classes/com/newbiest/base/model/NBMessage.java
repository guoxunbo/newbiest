package com.newbiest.base.model;

import com.google.common.collect.Maps;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/1/25.
 */
@Entity
@Table(name="NB_MESSAGE")
@Data
public class NBMessage extends NBUpdatable {

    static Map<String, NBMessage> nbMessageMap = Maps.newConcurrentMap();

    private String keyId;

    private String message;

    private String messageZh;

    private String messageRes;

    public static void put(NBMessage message) {
        nbMessageMap.put(message.getKeyId(), message);
    }

    public static void putAll(List<NBMessage> messages) {
        Map<String, NBMessage> messageMap = messages.stream().collect(Collectors.toConcurrentMap(NBMessage :: getKeyId, Function.identity()));
        nbMessageMap.putAll(messageMap);
    }

    public static NBMessage get(String keyId) {
        return nbMessageMap.get(keyId);
    }

    public static NBMessage contains(String keyId) {
        return nbMessageMap.get(keyId);
    }
}
