package com.mengchen.webapp.sqs;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;

public class SQSPollThread implements Runnable {

//    @Value("${aws.sqs.queue.name}")
    String AWS_SQS_QUEUE_NAME= "SQSQueueA10";

//    @Value("${aws.sns.topic.arn}")
    String AWS_SNS_TOPIC_ARN = "arn:aws:sns:us-east-1:469580218939:SNSTopicA10";

    private final org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(org.jboss.logging.Logger.class);


    @Override
    public void run() {

        if(AWS_SQS_QUEUE_NAME == null){
            AWS_SQS_QUEUE_NAME = "null ai";
        }
        logger.info(">>>>>>>>> AWS_SQS_QUEUE_NAME : " + AWS_SQS_QUEUE_NAME);

        AmazonSQS sqs = AmazonSQSClientBuilder
                                        .standard()
                                        .withRegion(Regions.US_EAST_1)
                                        .withCredentials(new InstanceProfileCredentialsProvider(false))
                                        .build();


        AmazonSNS sns = AmazonSNSClientBuilder
                                        .standard()
                                        .withRegion(Regions.US_EAST_1)
                                        .withCredentials(new InstanceProfileCredentialsProvider(false))
                                        .build();

        String AWS_SQS_QUEUE_URL = sqs.getQueueUrl(AWS_SQS_QUEUE_NAME).getQueueUrl();

        while(true){
            ReceiveMessageRequest receive_request = new ReceiveMessageRequest()
                    .withQueueUrl(AWS_SQS_QUEUE_URL)
                    .withAttributeNames("All")
                    .withMessageAttributeNames("User", "Due_in")
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(20);

            List<Message> messages = sqs.receiveMessage(receive_request).getMessages();
            // Publish a message to an Amazon SNS topic.
            for (Message msg : messages){

                SNSMessageAttributes snsMsgAttr = new SNSMessageAttributes(msg.getBody());

                Map<String, MessageAttributeValue> sqsAttrMap = msg.getMessageAttributes();
                System.out.println(">>>>>>>>  " +  sqsAttrMap.size());

                Map<String,String> sqsAttr= msg.getAttributes();

                System.out.println(">>>>>>>>  " +  sqsAttr.toString());

                snsMsgAttr.addAttribute("User",sqsAttrMap.get("User").getStringValue());
                snsMsgAttr.addAttribute("Due_in", sqsAttrMap.get("Due_in").getStringValue());

                System.out.println(">>>>>>>>  " + snsMsgAttr.getMessage());

                PublishResult publishResponse = snsMsgAttr.publish(sns, AWS_SNS_TOPIC_ARN);
                logger.info(">>>>>>  MessageId: " + publishResponse.getMessageId());

                sqs.deleteMessage(AWS_SQS_QUEUE_URL, msg.getReceiptHandle());
                logger.info(">>>>>>  Deleted Msg from SQS queue: " + publishResponse.getMessageId());

            }
        }
    }
}
