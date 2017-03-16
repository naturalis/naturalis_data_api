package nl.naturalis.nba.client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestNbaClient {

	public static void main(String[] args) throws IOException {
		// https://www.tutorialspoint.com/junit/junit_using_assertion.htm
		Result result = JUnitCore.runClasses(TestNbaClientAssertions.class);

		for (Failure failure : result.getFailures()) {
			System.out.println("Failed tests:");
			System.out.println(failure.toString());
		}

		System.out.println(result.wasSuccessful());
		

	}

}
