package com.testingfoo.tests.standardQueue;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.sqs.model.Message;
import com.testingfoo.elasticmq.BaseTest;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class SendAndRecieveBatchMessages extends BaseTest {
	
	public String queueUrl;
	public String queueName="udQueue";
	public List<String> messagesToBeSend;
	public List<Message> recievedMessages;
	public String sampleMessage1="This is a Sample Message1";
	public String sampleMessage2="This is a Sample Message2";
	
	@BeforeMethod
	public void setupMethod() {
		
		queueUrl=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is not added to the server");
		
		messagesToBeSend=new ArrayList<String>();
		messagesToBeSend.add(sampleMessage1);
		messagesToBeSend.add(sampleMessage2);
		
	}
	
	@Test
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test to validate the Send Batch Message feature.")
	public void testSendBatchMessagesToTheQueue() {
			
		sqsQueue.sendMultipleMessages(queueUrl,messagesToBeSend);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 9);
		
		sqsQueue.displayMessages(recievedMessages);
		
		Assert.assertTrue(recievedMessages.get(1).getBody().equals(sampleMessage1),"Message1 is not available in the message Queue");
		Assert.assertTrue(recievedMessages.get(0).getBody().equals(sampleMessage2),"Message2 is not available in the message Queue");
		

		}
		
		
	
	@AfterMethod
	public void tearDownMethod() {
		
		
		sqsQueue.purge(queueUrl);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 10);
		
		sqsQueue.displayMessages(recievedMessages);
	
	}


}
