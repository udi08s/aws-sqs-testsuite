package com.testingfoo.tests.standardQueue;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.testingfoo.elasticmq.BaseTest;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class CreateBlankNameQueueTest extends BaseTest {
	
	public String queueName;

	@Test
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test to validate the create queue feature when queueName is Blank")
	public void testCreateQueueWithBlankQueueName() {
		
		queueName="";
		
		queueName=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
			
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueName),"Queue is not added to the server");
		
	}
	
	@AfterMethod
	public void tearDownMethod() {
		
		sqsQueue.deleteQueue(queueName);
		
		sqsQueue.listingTheQueues();
		
		Assert.assertFalse(sqsQueue.isQueueAvailableInTheList(queueName),"Queue is still available in the server");
		
	}
}
