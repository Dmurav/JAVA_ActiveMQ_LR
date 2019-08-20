/*
 * LoadRunner Java script. (Build: _build_number_)
 * 
 * Script Description: 
 *                     
 */

import lrapi.lr;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;


public class Actions
{

	public class Producer {

    	private String queueName;
    	private String brokerURL;
    	private Connection connection;
    	private Session session;
    	private Queue queue;


    	private Producer(String brokerURL, String queueName){
        	this.brokerURL = brokerURL;
        	this.queueName = queueName;
    	}

    	private void connect() throws JMSException {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        	this.connection = connectionFactory.createConnection();
        	this.connection.start();
    	}

    	private void startSession() throws JMSException {
        	this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        	this.queue = this.session.createQueue(queueName);
    	}

    	private void sendMsg(String msg) throws JMSException {
        	MessageProducer messageProducer = this.session.createProducer(this.queue);
        	TextMessage textMessage = this.session.createTextMessage();
        	textMessage.setText(msg);
        	messageProducer.send(textMessage);
    	}

   		private void closeCon() throws JMSException {
        	this.connection.close();
    	}

	}
		
	public class Consumer {

    	private Message message;
    	public TextMessage textMessage;
    	private String queueName;
    	private String brokerURL;
    	private Connection connection;
    	private Session session;
    	private Queue queue;
    	private Queue queue2;

    	private Consumer(String brokerURL, String queueName) {
        	this.brokerURL = brokerURL;
        	this.queueName = queueName;
    	}

    	private void connect() throws JMSException {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        	this.connection = connectionFactory.createConnection();
        	this.connection.start();
    	}

    	private void startSession() throws JMSException {
        	this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        	this.queue = this.session.createQueue(queueName);
    	}

    	private void rcvMsg() throws JMSException {
        	MessageConsumer messageConsumer = this.session.createConsumer(this.queue);
        	this.message = messageConsumer.receive();
    	}

    	private void printMsg() throws JMSException {
        	if (this.message instanceof TextMessage) {
            	this.textMessage = (TextMessage) this.message;
            	System.out.println("Received message '" + this.textMessage.getText() + "'");
        	}
    	}

    	private void transMsg(String Q) throws JMSException {
        	this.queue2 = this.session.createQueue(Q);
        	Producer TransProd = new Producer("tcp://localhost:61616", "ActiveMQ2");
        	TransProd.connect();
        	TransProd.startSession();
        	TransProd.sendMsg(textMessage.getText());
    	}

    	private void closeCon() throws JMSException {
        	this.connection.close();
    	}

	}
	
	public Producer activeProd = new Producer("tcp://localhost:61616", "ActiveMQ");
	public Consumer activeCons = new Consumer("tcp://localhost:61616", "ActiveMQ");
	
	
	public int init() throws Throwable {
		activeProd.connect();
    	activeProd.startSession();
		activeCons.connect();
   		activeCons.startSession();
   		
		return 0;
	}//end of init


	public int action() throws Throwable {
	
    	activeProd.sendMsg("Hello World");
    	activeCons.rcvMsg();
    	activeCons.printMsg();
    	activeCons.transMsg("ActiveM2");
    	
		return 0;
	}//end of action


	public int end() throws Throwable {
		activeProd.closeCon();
		activeCons.closeCon();
		
		return 0;
	}//end of end
}
