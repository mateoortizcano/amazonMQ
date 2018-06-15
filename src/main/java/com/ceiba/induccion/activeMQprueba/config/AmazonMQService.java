package com.ceiba.induccion.activeMQprueba.config;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/amazonMq")
public class AmazonMQService {

	@Value("${spring.activemq.broker-url}")
	private String wireLevelEndpoint;
	
	@Value("${spring.activemq.user}")
	private String activeMqUsername;
	
	@Value("${spring.activemq.password}")
	private String activeMqPassword;
	
	@GetMapping("/create")
	public void createMessageProducer() throws JMSException {
		
		// Create a connection factory.
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(wireLevelEndpoint);
	
		// Pass the username and password.
		connectionFactory.setUserName(activeMqUsername);
		connectionFactory.setPassword(activeMqPassword);
	
		// Create a pooled connection factory.
		final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(connectionFactory);
		pooledConnectionFactory.setMaxConnections(10);
	
		// Establish a connection for the producer.
		final Connection producerConnection = pooledConnectionFactory.createConnection();
		producerConnection.start();
		
		// Create a session.
		final Session producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a queue named "MyQueue".
		final Destination producerDestination = producerSession.createQueue("MyQueue");

		// Create a producer from the session to the queue.
		final MessageProducer producer = producerSession.createProducer(producerDestination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		// Create a message.
		final String text = "Hello from Amazon MQ!";
		TextMessage producerMessage = producerSession.createTextMessage(text);

		// Send the message.
		producer.send(producerMessage);
		System.out.println("Message sent.");
		
		// clean up the producer
		producer.close();
		producerSession.close();
		producerConnection.close();
		
		//close connection when left usage
		
		this.createMessageConsumer();
		pooledConnectionFactory.stop();
		
	}
	
	public void createMessageConsumer() throws JMSException {
		
		// Create a connection factory.
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(wireLevelEndpoint);

		// Pass the username and password.
		connectionFactory.setUserName(activeMqUsername);
		connectionFactory.setPassword(activeMqPassword);

		// Establish a connection for the consumer.
		final Connection consumerConnection = connectionFactory.createConnection();
		consumerConnection.start();
		
		// Create a session.
		final Session consumerSession = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a queue named "MyQueue".
		final Destination consumerDestination = consumerSession.createQueue("MyQueue");

		// Create a message consumer from the session to the queue.
		final MessageConsumer consumer = consumerSession.createConsumer(consumerDestination);
		
		// Begin to wait for messages.
		final Message consumerMessage = consumer.receive(1000);
		
		// Receive the message when it arrives.
		final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
		System.out.println("Message received: " + consumerTextMessage.getText());
		
		// close the consumer, sesion
		consumer.close();
		consumerSession.close();
		consumerConnection.close();
		
	}
}
