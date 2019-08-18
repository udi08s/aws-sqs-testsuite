package com.testingfoo.elasticmq;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Assert;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

import io.qameta.allure.Step;


/**
 * @author udayseshadri
 *
 */
public class SQSQueue {

	private final AmazonSQS client;

	//Class Constructor
	public SQSQueue(AmazonSQS client) {
		this.client = client;
	}


	
	/**
	 * @author udayseshadri
	 * Description: Deletes all the messages in the queue.
	 * 
	 * @param: QueueUrl
	 * @return: void
	 *
	 */
	@Step("Purging the message in the Queue")
	public void purge(String queueUrl) {
		client.purgeQueue(new PurgeQueueRequest(queueUrl));
	}

	/**
	 * @author udayseshadri
	 * Description: Method to create a Queue
	 * 
	 * @param: QueueName
	 * @return: QueueURL for the created queue
	 *
	 */
	@Step("Creating a Queue : {1}")
	public String createQueueWithName(String queueName) {

		System.out.println("Creating a new SQS queue called " + queueName + " \n");
		final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
		final String myQueueUrl = client.createQueue(createQueueRequest).getQueueUrl();
		System.out.println(myQueueUrl);
		return myQueueUrl;
	}

	/**
	 * @author udayseshadri
	 * Description: Sending the message to the queue.
	 * 
	 * @param: QueueName , MessageBody
	 * @return: void
	 *
	 */
	@Step("Sending a Message to the Queue:{1} with MessageBody: {2}")
	public void sendSimpleMessage(String queueUrl, String MessageBody) {

		System.out.println("Sending a message to " + queueUrl + " \n");
		client.sendMessage(new SendMessageRequest(queueUrl, MessageBody));

	}

	/**
	 * @author udayseshadri
	 * Description: Receiving Message from Queue
	 * 
	 * @param: QueueName , MaxNumberOfMessages
	 * @return: List of messages from the Queue with max count of MaxNumberLimit
	 *
	 */
	@Step("Receiving a Messages from the Queue:{1} with MaximumCount: {2}")
	public List<Message> RecievingMessageFromQueue(String queueUrl, int MaxNumberOfMessages) {

		System.out.println("Receiving messages from " + queueUrl + " \n");
		final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		receiveMessageRequest.setMaxNumberOfMessages(MaxNumberOfMessages);
		final List<Message> messages = client.receiveMessage(receiveMessageRequest).getMessages();
		System.out.println(messages.size());
		
		return messages;
	}
	
	/**
	 * @author udayseshadri
	 * Description: Receiving Message from Queue with Timeout delay
	 * 
	 * @param: QueueName , MaxNumberOfMessages
	 * @return: List of messages from the Queue with max count of MaxNumberLimit
	 *
	 */
	@Step("Receiving a Messages from the Queue:{1} with MaximumCount: {2}")
	public List<Message> RecievingMessageFromQueueWithVisibility(String queueUrl, int MaxNumberOfMessages,int timeoutDuration) {

		System.out.println("Receiving messages from " + queueUrl + " \n");
		final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		receiveMessageRequest.setMaxNumberOfMessages(MaxNumberOfMessages);
		receiveMessageRequest.withVisibilityTimeout(timeoutDuration);
		final List<Message> messages = client.receiveMessage(receiveMessageRequest).getMessages();
		System.out.println(messages.size());
		
		return messages;
	}

	
	/**
	 * @author udayseshadri
	 * Description: Viewing the messages
	 * 
	 * @param: List<Message> : List of messages
	 * @return: void
	 *
	 */
	@Step("Displaying all the messages in the queue")
	public void displayMessages(List<Message> messages) {

		for (final Message message : messages) {
			System.out.println("Message");
			System.out.println("  MessageId:     " + message.getMessageId());
			System.out.println("  ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("  MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("  Body:          " + message.getBody());
			for (final Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("Attribute");
				System.out.println("  Name:  " + entry.getKey());
				System.out.println("  Value: " + entry.getValue());
			}
		}
		System.out.println();

	}
	
	
	/**
	 * @author udayseshadri
	 * Description: Sending message with Attribute to the queue.
	 * 
	 * @param: QueueUrl , Message body
	 * @return: void
	 *
	 */
	@Step("Sending Message with Attribute")
	public void sendMessageWithAttribute(String queueUrl, String MessageBody) {

		Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();
		messageAttributes.put("AttributeOne",
				new MessageAttributeValue().withStringValue("This is an attribute").withDataType("String"));

		SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(queueUrl)
				.withMessageBody(MessageBody).withDelaySeconds(0).withMessageAttributes(messageAttributes);

		client.sendMessage(sendMessageStandardQueue);
	}
	
	/**
	 * @author udayseshadri
	 * Description: Sending message with Attribute to the queue.
	 * 
	 * @param: QueueUrl , Message body
	 * @return: void
	 *
	 */
	@Step("Sending Message with Delay Visibility Timeout")
	public void sendMessageWithVisibilityTimeout(String queueUrl, String MessageBody,int timeoutDuration ) {


		SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(queueUrl)
				.withMessageBody(MessageBody).withDelaySeconds(timeoutDuration);

		client.sendMessage(sendMessageStandardQueue);
	}
	
	
	/**
	 * @author udayseshadri
	 * Description: Sending multiple Messages as batch to the queue
	 * 
	 * @param: QueueUrl , Message body
	 * @return: void
	 *
	 */
	@Step("Sending Batch Messages to the queue")
	public void sendMultipleMessages(String queueUrl,List<String> messages) {

		List<SendMessageBatchRequestEntry> messageEntries = new ArrayList<SendMessageBatchRequestEntry>();
		
		int id=1;
		for(String message:messages) {
			messageEntries.add(new SendMessageBatchRequestEntry().withId(String.valueOf(id)).withMessageBody(message));
			id++;
		}
		
		SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest(queueUrl, messageEntries);
		client.sendMessageBatch(sendMessageBatchRequest);
	}

	/**
	 * @author udayseshadri
	 * Description: Deleting the queue
	 * 
	 * @param: QueueUrl
	 * @return: void
	 *
	 */
	@Step("Deleting the Queue")
	public void deleteQueue(String myQueueUrl) {

			System.out.println("Deleting the test queue:" + myQueueUrl + "\n");

			client.deleteQueue(new DeleteQueueRequest(myQueueUrl));

	}

	/**
	 * @author udayseshadri
	 * Description: Listing the Queues
	 * 
	 * @param: void
	 * @return: void
	 *
	 */
	@Step("Listing the Queues")
	public void listingTheQueues() {

		System.out.println("Listing all queues in your account.\n");
		for (final String queueUrl : client.listQueues().getQueueUrls()) {
			System.out.println("  QueueUrl: " + queueUrl);
		}
		System.out.println();

	}

	/**
	 * @author udayseshadri
	 * Description: Checking whether queue is available in the queuelist or not
	 * 
	 * @param: QueueURL
	 * @return: boolean
	 *
	 */
	@Step("Checking if Queue is available in the queuelist or not")
	public boolean isQueueAvailableInTheList(String myQueueUrl) {

		System.out.println("Checking if the queue is available or not .\n");

		for (final String queueUrl : client.listQueues().getQueueUrls()) {
			if (queueUrl.equals(myQueueUrl)) {
				System.out.println(myQueueUrl + " is found in the list ");
				System.out.println();
				return true;
			}
		}
		System.out.println(myQueueUrl + " is not found in the list ");
		System.out.println();
		return false;

	}

	
	/**
	 * @author udayseshadri
	 * Description:Creating Multiple queues at a time 
	 * 
	 * @param: List of all QueueNames
	 * @return: List of all the QueueURL created
	 *
	 */
	@Step("Creating Multiple queues at a time")
	public List<String> createMultipleQueue(List<String> myQueueNames) {

		List<String> queueUrl = new ArrayList<String>();

		for (String queue : myQueueNames) {

			System.out.println("Creating a new SQS queue called " + queue + " \n");
			final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queue);
			final String myQueueUrl = client.createQueue(createQueueRequest).getQueueUrl();
			System.out.println(myQueueUrl);
			queueUrl.add(myQueueUrl);

		}

		return queueUrl;
	}

	/**
	 * @author udayseshadri
	 * Description: Creating Queue with SpecifiedTimeout
	 * 
	 * @param: timeout,queueName
	 * @return: queueURL
	 *
	 */
	@Step("Creating Queue with SpecifiedTimeout")
	public String createQueueWithSpecifiedTimeout(String timeout, String queueName) {

		System.out.println("Creating a new SQS queue called " + queueName + " \n");
		final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);

		final String myQueueUrl = client.createQueue(createQueueRequest).getQueueUrl();

		final SetQueueAttributesRequest request = new SetQueueAttributesRequest().withQueueUrl(myQueueUrl)
				.addAttributesEntry(QueueAttributeName.VisibilityTimeout.toString(), timeout);

		client.setQueueAttributes(request);

		System.out.println(
				"Created queue " + queueName + " with " + "visibility timeout set to " + timeout + " seconds.");

		System.out.println(myQueueUrl);
		return myQueueUrl;

	}

	
	/**
	 * @author udayseshadri
	 * Description: Get Default Timeout value
	 * 
	 * @param: queueName
	 * @return: VisibilityTimeout attribute value
	 *
	 */
	@Step("Get Default Timeout value")
	public String getDefaultTimeOutValue(String queueName) {

		List<String> attributeNames = new ArrayList<String>();
		attributeNames.add(QueueAttributeName.VisibilityTimeout.toString());
		final GetQueueAttributesResult attributes = client.getQueueAttributes(queueName, attributeNames);
		Map<String, String> attributeMap = attributes.getAttributes();
		return attributeMap.get("VisibilityTimeout");
	}

	/**
	 * @author udayseshadri
	 * Description: printAllExceptionDetails
	 * 
	 * @param: AmazonServiceException
	 * @return: Void
	 *
	 */
	@Step("Printing the exception")
	public void printAllExceptionDetails(AmazonServiceException ase) {

		System.out.println("Caught an AmazonServiceException, which means "
				+ "your request made it to Amazon SQS, but was " + "rejected with an error response for some reason.");
		System.out.println("Error Message:    " + ase.getMessage());
		System.out.println("HTTP Status Code: " + ase.getStatusCode());
		System.out.println("AWS Error Code:   " + ase.getErrorCode());
		System.out.println("Error Type:       " + ase.getErrorType());
		System.out.println("Request ID:       " + ase.getRequestId());
	}

	/**
	 * @author udayseshadri
	 * Description: Sending Message With Attribute
	 * 
	 * @param: queueUrl,Sample Message,attributeType,AttributeName,AttributeValue
	 * @return: Void
	 *
	 */
	@Step("Sending Message With Attribute")
	public void sendMessageWithAttribute(String queueUrl, String sampleMessage, String attributeType,
			String attributeName, String attributeValue) {

		
		  final Map<String, MessageAttributeValue> messageAttributes = new
		  HashMap<String, MessageAttributeValue>();
		  
		  if(!attributeType.contains("Binary")) {
		  
		  messageAttributes.put(attributeName, new MessageAttributeValue()
		  .withDataType(attributeType) .withStringValue(attributeValue));
		  
		  }
		  
		  else{ int i=Integer.parseInt(attributeValue); ByteBuffer binaryValue =
		  ByteBuffer.wrap(new byte[i]); messageAttributes.put(attributeName, new
		  MessageAttributeValue() .withDataType(attributeType)
		  .withBinaryValue(binaryValue));
		  
		  
		  }
		 

		final SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.withMessageBody(sampleMessage);
		sendMessageRequest.withQueueUrl(queueUrl);
		sendMessageRequest.addMessageAttributesEntry(attributeName,
				new MessageAttributeValue().withDataType(attributeType).withStringValue(attributeValue));

		/*
		 * sendMessageRequest.addMessageAttributesEntry( "attributeTest", new
		 * MessageAttributeValue() .withDataType("String")
		 * .withStringValue("attributeTest123"));
		 * 
		 * sendMessageRequest.addMessageAttributesEntry("key", "100");
		 */

		Map<String, MessageAttributeValue> sample = sendMessageRequest.getMessageAttributes();

		System.out.print(sample.get(attributeName));

		client.sendMessage(sendMessageRequest);

	}

	/**
	 * @author udayseshadri
	 * Description: Receiving Message from Queue with Message Attribute
	 * 
	 * @param: QueueName , MaxNumberOfMessages, AttributeName
	 * @return: List of Messages with particular attribute
	 *
	 */
	@Step("Receiving a Messages from the Queue:{1} with MaximumCount: {2} with attributeName: {3}")
	public List<Message> recievingMessageFromQueueWithMessageAttribute(String queueUrl, int MaxNumberOfMessages,
			String attributeName) {

		System.out.println("Receiving messages from " + queueUrl + " \n");
		final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		receiveMessageRequest.setMaxNumberOfMessages(MaxNumberOfMessages);
		receiveMessageRequest.withAttributeNames("All");
		receiveMessageRequest.withMessageAttributeNames("All");
		final List<Message> messages = client.receiveMessage(receiveMessageRequest).getMessages();
		System.out.println(messages.size());

		return messages;
	}

	/**
	 * @author udayseshadri
	 * Description: Sending message with Delay
	 * 
	 * @param: QueueName , Sample Message, DelayInMessageDelivery
	 * @return: void
	 *
	 */
	@Step("Sending message to Queue:{1} with delay:{3}")
	public void sendMessageWithDelay(String queueUrl, String sampleMessage, int delayInMessageDelivery) {
		// TODO Auto-generated method stub

		// Send a message with a 5-second timer.
		System.out.println("Sending a message with a 5-second timer to MyQueue.\n");
		SendMessageRequest request = new SendMessageRequest(queueUrl, sampleMessage);
		request.setDelaySeconds(delayInMessageDelivery);
		client.sendMessage(request);

	}

}
