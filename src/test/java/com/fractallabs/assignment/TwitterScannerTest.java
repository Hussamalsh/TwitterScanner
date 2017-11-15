package com.fractallabs.assignment;

import com.fractallabs.assignment.TwitterScanner.TSValue;

import junit.framework.Assert;
import org.junit.Test;


/**
 * TwitterScannerTest.
 *
 * <P>Unit test for simple TwitterScanner class.
 *
 *  Copyright (c) 2017, All rights reserved.
 *  
 * @author Hussam Alshammari
 * @version 1
 */

public class TwitterScannerTest
{
 
	/**
	 * Method that test creating of the TwitterScanner object.
	 */
	@Test
    public void createObjectTestWithValue()
    {
    	TwitterScanner tc = new TwitterScanner("Facebook");
    	String companyN = tc.getCompanyName();
		Assert.assertEquals(companyN, "Facebook");
    }
    
	/**
	 * Method that test creating of the TwitterScanner object with empty value
	 */
	@Test
    public void createObjectTestWithoutValue()
    {
    	//create object with empty value test
    	 new TwitterScanner("");
    }
    
	/**
	 * Test creating of the TwitterScanner object with null
	 */
	@Test
    public void createObjectTestWithNull()
    {
    	//create object with null value test
    	 new TwitterScanner(null);
    }
    
	/**
	 * Test mentionsCalculationTest()
	 */
	@Test
    public void mentionsCalculationTest()
    {
    	TwitterScanner tc = new TwitterScanner("Facebook"); // test return 100%
    	tc.setNewCounter(100);
    	TSValue tsV =  tc.getNumberOfMentions();
    	Assert.assertEquals(tsV.getVal(), 100.0);
    	
    	// test report an decrease by 30%
    	tc.setNewCounter(70);
    	tsV =  tc.getNumberOfMentions();
    	Assert.assertEquals(tsV.getVal(), -30.0);
    	
    	// test report an increase by 30%
    	tc.setNewCounter(100);
    	tc.setOldCounter(70);
    	tsV =  tc.getNumberOfMentions();
    	Assert.assertEquals(tsV.getVal(), 30.0);
    	
    	// test report zero mentions 
    	tc.setNewCounter(0);
    	tc.setOldCounter(0);
    	tsV =  tc.getNumberOfMentions();
    	Assert.assertEquals(tsV.getVal(), 0.0);
    	
    	
    	// test report 100.0% if new mentions is greater than zero and the old mentions less than one.
    	tc.setNewCounter(3);
    	tc.setOldCounter(-1);
    	tsV =  tc.getNumberOfMentions();
    	Assert.assertEquals(tsV.getVal(), 100.0);
    }
    
	/**
	 * runMethodTest Test if the run method and the timer. 
	 */
	@Test
    public void runMethodTest()
    {
    	TwitterScanner tc = new TwitterScanner("Facebook"); // test the run method and the timer 
    	tc.run();
    	Assert.assertEquals(tc.isTimerIsRunning(), true);
    	tc.getTimer().cancel();
    }
	
    /**
     * Test the validation the text for the company name. 
     */
	@Test
    public void txtValidationTest()
    {
    	TwitterScanner tc = new TwitterScanner(); // test the validation method
    	tc.txtValidation("");
    	tc.txtValidation(null);
    }
    
    
}
