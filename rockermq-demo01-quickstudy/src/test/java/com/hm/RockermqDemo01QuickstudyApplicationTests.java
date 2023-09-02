package com.hm;

import com.hm.constants.MQConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class RockermqDemo01QuickstudyApplicationTests {

	/**
	 * 发消息
	 */
	@Test
	void producerSendMessage() throws Exception {
		//	创建一个生产者 指定一个组名
		DefaultMQProducer producer = new DefaultMQProducer("test-producer-group");
		// 连接 name srv
		producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
		// 启动
		producer.start();
		//	创建一个消息
		Message message = new Message("testTopic","我是一个简单的消息".getBytes());
		for (int i = 0; i < 10; i++) {
			// 发送消息
			SendResult sendResult = producer.send(message);
			System.out.println(sendResult.getSendStatus());
		}
		// 关闭生产者
		producer.shutdown();

	}

	/**
	 * 消费者获取消息
	 */
	@Test
	public void consumerGetMessage() throws Exception {
		// 创建一个消费者
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consumer-group");
		// 设置name srv地址
		consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
		// 订阅一个主题 testTopic ; * 表示订阅这个主题中的所有消息
		consumer.subscribe("testTopic","*");
		consumer.registerMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
				// 消费的方法
				System.out.println("我是消费者");
				System.out.println(list.get(0).toString());
				System.out.println("消息内容:"+new String(list.get(0).getBody()));
				System.out.println("消息消费上下文:"+consumeConcurrentlyContext);
				// 返回值 CONSUME_SUCCESS:成功,表示消息会从mq中出队列.
				// 如果是RECONSUME_LATER(报错或者null值) 失败 消息会重新回到队列 过一会重新投递出来
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});

		// 启动
		consumer.start();

		//	主线程执行完毕不可以直接停止,要阻塞主线程,从而保证消费者可以一直监听队列
		System.in.read();

	}

}
