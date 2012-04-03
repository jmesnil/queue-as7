package net.jmesnil;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class QueueTestCase {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsWebInfResource("hornetq-jms.xml");
    }

    @Resource(mappedName = "/queue/test")
    private Queue queue;

    @Resource(mappedName = "/ConnectionFactory")
    private ConnectionFactory factory;

    private Connection connection;

    @After
    public void tearDown() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void useQueueHostedInJBossAS() throws Exception {

        String messageBody = randomUUID().toString();

        connection = factory.createConnection();
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        MessageConsumer echoConsumer = session.createConsumer(queue);
        echoConsumer.setMessageListener(new MessageListener() {

            public void onMessage(Message message) {
                try {
                    String text = ((TextMessage) message).getText();
                    MessageProducer prod = session.createProducer(message.getJMSReplyTo());
                    prod.send(session.createTextMessage(text));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        QueueRequestor requestor = new QueueRequestor((QueueSession) session, queue);

        connection.start();

        Message request = session.createTextMessage(messageBody);
        Message response = requestor.request(request);

        assertEquals("Should have received the same message", messageBody, ((TextMessage) response).getText());
    }
}