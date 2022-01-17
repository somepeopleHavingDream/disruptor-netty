package org.yangxin.disruptor.disruptor;

import com.lmax.disruptor.WorkHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yangxin.disruptor.entity.TranslatorDataWrapper;

/**
 * @author yangxin
 * 2022/1/17 20:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractMessageConsumer implements WorkHandler<TranslatorDataWrapper> {

    protected String consumerId;
}
