package edu.esipe.i3.ezipflix.frontend.data.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
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

    //@Autowired
    //VideoConversionRepository videoConversionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoConversion.class);

    //private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();


    public void save(final ConversionRequest request, final ConversionResponse response) throws Exception {

        final VideoConversions conversion = new VideoConversions(response.getUuid().toString(),request.getPath().toString(),".");
        //Save Request to database
        this.saveDynamoDB(conversion);
        //Convert VideoConversions to Json
        String jsonRequest = conversion.toJson();

        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<ApiFuture<String>>();
        try {
            // Create publisher instance
            publisher = Publisher.newBuilder(topicName).build();

            // Convert json to bytes
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
            }

            if (publisher != null) {
                //Shutdown the publisher
                LOGGER.info("Action = ","Shutdown Publisher");
                publisher.shutdown();
            }

        }

    }

    public String saveDynamoDB(VideoConversions video){
        AmazonDynamoDB client= AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("film");
        Item item = new Item()
                .withPrimaryKey("uuid",video.getUuid())
                .withString("origin_path",video.getOriginPath())
                .withString("target_path",".");

        PutItemOutcome out = table.putItem(item);
        LOGGER.info("Action = ","Insert : "+out.toString());
        return out.toString();
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
