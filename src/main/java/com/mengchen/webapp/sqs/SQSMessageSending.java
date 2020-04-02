package com.mengchen.webapp.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.mengchen.webapp.entity.Bill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SQSMessageSending {

    @Value("${aws.sqs.queue.name}")
    String AWS_SQS_QUEUE_NAME;

    @Value("${domain}")
    String DOMAIN ;

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    String AWS_SQS_QUEUE_URL = sqs.getQueueUrl(AWS_SQS_QUEUE_NAME).getQueueUrl();

    public void sqsSendMsg(List<Bill> theBills, int due_in, String user_email){

        Map<String, MessageAttributeValue> msg_atrr = new LinkedHashMap<>();
        msg_atrr.put("User" , new MessageAttributeValue().withDataType("String").withStringValue(user_email));
        msg_atrr.put("Due_in" , new MessageAttributeValue().withDataType("String").withStringValue(String.valueOf(due_in)));

        List<String> billUrls = new ArrayList<>();
        theBills.forEach(bill -> billUrls.add("http://" + DOMAIN + "/v1/bill/" + bill.getBill_id()));

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(AWS_SQS_QUEUE_URL)
                .withMessageAttributes(msg_atrr)
                .withMessageBody(billUrls.toString())
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
    }



}
