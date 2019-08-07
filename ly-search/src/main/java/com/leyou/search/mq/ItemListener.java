package com.leyou.search.mq;

import com.leyou.search.search.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq消费者监听
 */
@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    /**
     * 消息队列的新增或者修改
     * @param SpuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="search.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "leyou.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void listenInsertOrUpdate(Long SpuId){
        if(SpuId == null){
            return;
        }
        //处理消息，对商品进行新增或者修改
        searchService.creatOrUpdateIndex(SpuId);
    }

    /**
     * 消息队列删除
     * @param SpuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="search.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "leyou.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void listenDelete(Long SpuId){
        if(SpuId == null){
            return;
        }
        //处理消息，对商品进行新增或者修改
        searchService.deleteIndex(SpuId);
    }
}
