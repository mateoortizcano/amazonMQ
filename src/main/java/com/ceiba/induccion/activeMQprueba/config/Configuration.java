package com.ceiba.induccion.activeMQprueba.config;

import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {
	
	@Value("${spring.activemq.broker-url}")
	private String brokerUlr;
	
	@Value("${spring.activemq.user}")
	private String userName;
	
	@Value("${spring.activemq.password}")
	private String password;
	
	@Bean
	public Queue queue() {
		return new ActiveMQQueue("standalone.queue");
	}
	
	@Bean
	public ActiveMQConnectionFactory activeMQConnectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(brokerUlr);
		factory.setUserName(userName);
		factory.setPassword(password);
		return factory;
	}
	
	@Bean
	public JmsTemplate jmsTemplate() {
		return new JmsTemplate(activeMQConnectionFactory());
	}
}
