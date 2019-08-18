package com.testingfoo.tests.standardQueue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amazonaws.services.sqs.model.Message;
import com.testingfoo.elasticmq.BaseTest;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class SendAndRecieveDifferentMessages extends BaseTest{

	public String queueUrl;
	public String queueName="udQueue";
	public List<Message> recievedMessages;
	public String sampleMessage;
	
	@BeforeMethod
	public void setupMethod() {
		queueUrl=sqsQueue.createQueueWithName(queueName);
		
		sqsQueue.listingTheQueues();
	
		Assert.assertTrue(sqsQueue.isQueueAvailableInTheList(queueUrl),"Queue is not added to the server");
		
	}
	
	@Test(dataProvider="getData")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test to validate the Send Message feature with Different Message Body")
	public void testSendDifferentMessagesToTheQueue(String Message) {
		
		sampleMessage=Message;
		
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
	
	@DataProvider
    public Object[][] getData() {
        return new Object[][]{{"!@#$%^&*(!@@##(!(@*@*!*!"}, {"1234567890122344444121212"} , {"ababbabdbadbjadbajsdbjasdbasdbjasdjasbdasdbjabdjabdjasbdjabdjasdbjasbdjasbdjasbdjasdbjabsdjasdbasdbj"},
        	                  {"a"},{"HelloWorld"},
        	                  {"U+0904"}};
    }
	
}

