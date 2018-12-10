package edu.esipe.i3.ezipflix.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.esipe.i3.ezipflix.frontend.data.services.VideoConversion;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */

@SpringBootApplication
@RestController
@EnableRabbit
@EnableWebSocket
public class VideoDispatcher implements WebSocketConfigurer {

    // rabbitmqadmin -H localhost -u ezip -p pize -V ezip delete queue name=video-conversion-queue
    // rabbitmqadmin -H localhost -u ezip -p pize -V ezip delete exchange name=video-conversion-exchange
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare exchange name=video-conversion-exchange type=direct
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare queue name=video-conversion-queue durable=true
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare binding source="video-conversion-exchange" destination_type="queue" destination="video-conversion-queue" routing_key="video-conversion-queue"
    // MONGO : db.video_conversions.remove({})

    //sudo rabbitmq-server start
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDispatcher.class);

    //With RMQ
//    @Value("${rabbitmq-server.credentials.username}") private String username;
//    @Value("${rabbitmq-server.credentials.password}") private String password;
//    @Value("${rabbitmq-server.credentials.vhost}") private String vhost;
//    @Value("${rabbitmq-server.server}") private String host;
//    @Value("${rabbitmq-server.port}") private String port;
//    @Value("${conversion.messaging.rabbitmq.conversion-queue}") public  String conversionQueue;
//    @Value("${conversion.messaging.rabbitmq.conversion-exchange}") public  String conversionExchange;

    @Autowired VideoConversion videoConversion;
    public static void main(String[] args) throws Exception {
        SpringApplication.run(VideoDispatcher.class, args);
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
    // │ REST Resources                                                                                                │
    // └───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
    @RequestMapping(method = RequestMethod.POST,value = "/convert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ConversionResponse requestConversion(@RequestBody ConversionRequest request) throws Exception {
        LOGGER.info("File = {}", request.getPath());
        final ConversionResponse response = new ConversionResponse();
        LOGGER.info("UUID = {}", response.getUuid().toString());
        videoConversion.savePubSub(request, response);
        return response;
    }


//With RMQ
//    @Bean
//    ConnectionFactory connectionFactory() {
//        final CachingConnectionFactory c = new CachingConnectionFactory(host, Integer.parseInt(port));
//        c.setVirtualHost(vhost);
//        c.setUsername(username);
//        c.setPassword(password);
//        return c;
//    }

    @Bean
    public WebSocketHandler videoStatusHandler() {
        return new VideoStatusHandler();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(videoStatusHandler(), "/video_status");
    }


    //With RMQ
//    @Bean
//    AmqpAdmin amqpAdmin() {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
//        q = rabbitAdmin.declareQueue(new Queue(conversionQueue));
//        rabbitAdmin.declareExchange(new DirectExchange(conversionExchange));
//        Binding binding = BindingBuilder.bind(new Queue(conversionQueue)).to(new DirectExchange(conversionExchange))
//                .with(COMMANDS_QUEUE);
//        rabbitAdmin.declareBinding(binding);
//
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }

//    @Bean(name="video-conversion-template")
//    public RabbitTemplate getVideoConversionTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//
//        template.setExchange(conversionExchange);
//        template.setRoutingKey(conversionQueue);
//        template.setQueue(conversionQueue);
//        return template;
//    }

}
