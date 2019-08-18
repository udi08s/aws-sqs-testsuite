package com.testingfoo.tests.standardQueue;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.testingfoo.elasticmq.BaseTest;
import com.testingfoo.utils.CommonUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class SendMessageWithDifferentMessageAttribute extends BaseTest {
	
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
	@Severity(SeverityLevel.NORMAL)
	@Description("Test to validate the Send Message with Different Message attribute feature")
	public void testSendXMLMessagesToTheQueue(String attributeType, String attributeName, String attributeValue) {
		
		sampleMessage="This is a sample Message with "+ attributeType + " attribute.";		
		
		try {
			
		sqsQueue.sendMessageWithAttribute(queueUrl, sampleMessage , attributeType , attributeName , attributeValue);
		
		recievedMessages=sqsQueue.recievingMessageFromQueueWithMessageAttribute(queueUrl, 10, attributeName);
		
		sqsQueue.displayMessages(recievedMessages);
				
		Assert.assertTrue(recievedMessages.get(0).getAttributes().containsValue(attributeValue),"Message Attributes are not correct");

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
	
	@DataProvider
    public Object[][] getData() {
        return new Object[][]{{"Binary","ByteArray","10"}, 
        					  {"String","Name","Jane"} , 
        					  {"Number","AccurateWeight","230.000000000000000001"},
        	                  {"String.Custom","EmployeeId","ABC123456"},
        	                  {"Number.Custom","AccountId","000123456"}};
    }
	
	
	

}
