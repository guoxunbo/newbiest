package com.newbiest.base.model;

import com.google.common.collect.Maps;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    @Transient
    static Map<String, NBMessage> nbMessageMap = Maps.newConcurrentMap();

    @Column(name="KEY_ID")
    private String keyId;

    @Column(name="MESSAGE")
    private String message;

    @Column(name="MESSAGE_ZH")
    private String messageZh;

    @Column(name="MESSAGE_RES")
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
