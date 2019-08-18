package com.testingfoo.elasticmq;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.*;

public class BaseTest {

	public String endpoint;
	public String region;
	public String accessKey;
	public String secretKey;

	public static Properties config = new Properties();
	public static FileInputStream fis;

	public AmazonSQS client;
	public SQSQueue sqsQueue;

	@BeforeClass
	public void setupElasticMQ() {

		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "//resources//properties//config.properties");

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		try {
			config.load(fis);
		} catch (IOException e) {

			e.printStackTrace();
		}

		endpoint = config.getProperty("endpoint");
		region = config.getProperty("region");
		accessKey = config.getProperty("accessKey");
		secretKey = config.getProperty("secretKey");

		client = AmazonSQSClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region)).build();

		sqsQueue = new SQSQueue(client);

	}

	@AfterClass(alwaysRun = true)
	private void tearDown() {

		System.out.println("Closing the SQS!!");
		client.shutdown();
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
