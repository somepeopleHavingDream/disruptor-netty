package org.yangxin.disruptor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yangxin
 * 2022/1/13 22:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorData implements Serializable {

    private static final long serialVersionUID = -6314040868778838917L;

    private String id;

    private String name;

    /**
     * 传输的消息体内容
     */
    private String message;
}
