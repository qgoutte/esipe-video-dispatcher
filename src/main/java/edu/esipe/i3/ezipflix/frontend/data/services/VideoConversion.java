package edu.esipe.i3.ezipflix.frontend.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import edu.esipe.i3.ezipflix.frontend.ConversionRequest;
import edu.esipe.i3.ezipflix.frontend.ConversionResponse;
import edu.esipe.i3.ezipflix.frontend.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.frontend.data.repositories.VideoConversionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Service
public class VideoConversion {

    //WITH RMQ
//    @Value("${conversion.messaging.rabbitmq.conversion-queue}")
//    public String conversionQueue;
//    @Value("${conversion.messaging.rabbitmq.conversion-exchange}")
//    public String conversionExchange;

    @Value("${google-cloud.pubsub.topic}")
    public String topicId;
    @Value("${google-cloud.pubsub.project}")
    public String projectId;
    @Autowired
    VideoConversionRepository videoConversionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoConversion.class);

    //private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();


    public void savePubSub(final ConversionRequest request, final ConversionResponse response) throws Exception {

        final VideoConversions conversion = new VideoConversions(response.getUuid().toString(),request.getPath().toString(),"");

        //Save Request to database
        videoConversionRepository.save(conversion);
        //Convert VideoConversions to Json
        String jsonRequest = conversion.toJson();

        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<ApiFuture<String>>();
        try {
            // Create publisher instance
            publisher = Publisher.newBuilder(topicName).build();

            // convert json to bytes
            ByteString data = ByteString.copyFromUtf8(jsonRequest);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Prepare the message to be published. Messages are automatically batched.
            ApiFuture<String> future = publisher.publish(pubsubMessage);
            futures.add(future);

        } catch (IOException e) {
            LOGGER.error("Error in savePubSub function");
            e.printStackTrace();
        } finally {
            List<String> messageIds = ApiFutures.allAsList(futures).get();

            for (String messageId : messageIds) {
                LOGGER.info("MessageId = ",messageId);
                System.out.println(messageId);
            }

            if (publisher != null) {
                //Shutdown the publisher
                LOGGER.info("Action = ","Shutdown Publisher");
                publisher.shutdown();
            }
        }

    }

//WITH RMQ
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//    @Autowired
//    @Qualifier("video-conversion-template")
//    public void setRabbitTemplate(final RabbitTemplate template) {
//        this.rabbitTemplate = template;
//    }
//
//
//    public void save(
//            final ConversionRequest request,
//            final ConversionResponse response) throws JsonProcessingException {
//
//        final VideoConversions conversion = new VideoConversions(
//                response.getUuid().toString(),
//                request.getPath().toString(),
//                "");
//
//        videoConversionRepository.save(conversion);
//        final Message message = new Message(conversion.toJson().getBytes(), new MessageProperties());
//        rabbitTemplate.convertAndSend(conversionExchange, conversionQueue, conversion.toJson());
//    }



}
