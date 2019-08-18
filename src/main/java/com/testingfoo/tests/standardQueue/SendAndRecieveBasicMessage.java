package com.testingfoo.tests.standardQueue;

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

public class SendAndRecieveBasicMessage extends BaseTest {
	
	public String queueUrl;
	public String queueName="udQueue";
	public List<Message> recievedMessages;
	public String sampleMessage="This is a Sample message!";
	
	@BeforeMethod
	public void setupMethod() {
		
		queueUrl=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is not added to the server");
		
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test to validate the Send Message feature")
	public void testSendSimpleMessage() {
		
		sqsQueue.sendSimpleMessage(queueUrl, sampleMessage);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 10);
		
		sqsQueue.displayMessages(recievedMessages);
		
		Assert.assertTrue(recievedMessages.get(0).getBody().equals(sampleMessage),"Message is not available in the message Queue");
		
	}
	
	@AfterMethod
	public void tearDownMethod() {
		
		sqsQueue.purge(queueUrl);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 10);
		
		sqsQueue.displayMessages(recievedMessages);
	
		
	}
	

}
