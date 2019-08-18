package com.testingfoo.tests.standardQueue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.model.Message;
import com.testingfoo.elasticmq.BaseTest;
import com.testingfoo.utils.CommonUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class SendAndRecieveMessageWithDelay extends BaseTest {
	
	public String queueUrl;
	public String queueName="udQueue";
	public List<Message> recievedMessages;
	public String sampleMessage="This is a Sample Message";
	public int delayInMessageDelivery=5;
	
	@BeforeMethod
	public void setupMethod() {
		
		queueUrl=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is not added to the server");
		
	}
	
	@Test
	@Severity(SeverityLevel.NORMAL)
	@Description("Test to validate the send message with delay feature")
	public void testSendMessageWithDelay() throws InterruptedException {
		
		sampleMessage="This is a Sample Message";
		
		try {
			
		sqsQueue.sendMessageWithDelay(queueUrl,sampleMessage,delayInMessageDelivery);
		
		// Wait for 10 seconds.
		System.out.println("Waiting for 10 seconds.");
		Thread.sleep(10000L);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 10);
		
		sqsQueue.displayMessages(recievedMessages);
		
		Assert.assertTrue(recievedMessages.get(0).getBody().equals(sampleMessage),"Message is not available in the message Queue");

		}
		
		catch(AmazonServiceException ase) {
			
			sqsQueue.printAllExceptionDetails(ase);
     
			Assert.assertTrue(new Integer(ase.getStatusCode()).toString().equals("400"),"Incorrect error message is displayed");
		}
		
	}
	
	@AfterMethod
	public void tearDownMethod() {
		
		sqsQueue.purge(queueUrl);
		
		recievedMessages=sqsQueue.RecievingMessageFromQueue(queueUrl, 10);
		
		sqsQueue.displayMessages(recievedMessages);
	
	}


}
