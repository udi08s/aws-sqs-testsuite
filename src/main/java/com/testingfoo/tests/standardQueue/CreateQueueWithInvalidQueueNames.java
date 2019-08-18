package com.testingfoo.tests.standardQueue;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amazonaws.AmazonServiceException;
import com.testingfoo.elasticmq.BaseTest;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class CreateQueueWithInvalidQueueNames extends BaseTest {

	public String queueName;
	public String queueUrl;

	@Test(dataProvider="getData")
	@Severity(SeverityLevel.NORMAL)
	@Description("Test to validate the create queue feature with InvalidQueue Names")
	public void testCreateQueueWithInvalidQueueNames(String name) {
		
		queueName=name;
		
		try {
		queueUrl=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueName),"Queue is not added to the server");
		
		}
		catch(AmazonServiceException ase) {
			
			sqsQueue.printAllExceptionDetails(ase);
     
			Assert.assertTrue(new Integer(ase.getStatusCode()).toString().equals("400"),"Incorrect error message is displayed");
		}
		
	}
	
	@AfterMethod
	public void tearDownMethod() {
				
		sqsQueue.listingTheQueues();
		
		Assert.assertFalse(sqsQueue.isQueueAvailableInTheList(queueName),"Queue is still available in the server");
		
	}
	
	/*
	 * TestData:
	 * 1. More than 80 characters
	 * 2. Non Alphanumeric character.
	 * 
	 */
	@DataProvider
    public Object[][] getData() {
        return new Object[][]{{"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl"}, 
        	                  {"#"}};
    }

	
	
}
