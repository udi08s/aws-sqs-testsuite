package com.testingfoo.tests.standardQueue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.sqs.model.Message;
import com.testingfoo.elasticmq.BaseTest;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class CreateQueueWithSpecifiedVisibilityTimeout extends BaseTest {

	public String queueUrl;
	public String queueName="udQueue";
	public String timeout="14";
	public List<Message> recievedMessages;
	public int timeoutDuration=5;

	@Test
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test to validate the create queue feature with Visibility Timeout")
	public void testCreateQueuewithVisibilityTimeOut() throws InterruptedException {
		
		queueUrl=sqsQueue.createQueueWithSpecifiedTimeout(timeout, queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is not added to the server");
		
		Assert.assertTrue(sqsQueue.getDefaultTimeOutValue(queueUrl).equals(timeout),"Timeout is incorrect");
		
		
		sqsQueue.sendMessageWithVisibilityTimeout(queueUrl, "This is a Sample Message1", timeoutDuration);
		
		System.out.println("Waiting for 6 seconds.");
		Thread.sleep(6000L);

		recievedMessages=sqsQueue.RecievingMessageFromQueueWithVisibility(queueUrl, 10, timeoutDuration);
		
		sqsQueue.displayMessages(recievedMessages);
		
	}
	

	@AfterMethod
	public void tearDownMethod() {
		
		sqsQueue.deleteQueue(queueUrl);
		
		sqsQueue.listingTheQueues();
		
		Assert.assertFalse(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is still available in the server");
		
	}

	
}
