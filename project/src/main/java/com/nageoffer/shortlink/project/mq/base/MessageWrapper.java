package com.nageoffer.shortlink.project.mq.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageWrapper<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String keys;

    private T message;

    private Long timeOut = System.currentTimeMillis();

    public MessageWrapper(String keys, T record) {
        this(keys, record, System.currentTimeMillis());
    }
}
