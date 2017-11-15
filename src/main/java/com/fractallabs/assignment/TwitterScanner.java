package com.fractallabs.assignment;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.RawStreamListener;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * TwitterScanner.
 *
 * <P>This class can Find out how the number of mentions of a company on Twitter change over time.
 *
 *  Copyright (c) 2017, All rights reserved.
 *
 * @author Hussam Alshammari
 * @version 1
 */

public class TwitterScanner 
{
	private Timer timer = new Timer();  //A facility for threads to schedule tasks for future execution in a background thread
	private String companyName;         //The name of the company to check mentions in twitter.
	private int newCounter;				//Counter of the new number of mentions every one hour.
	private int oldCounter;             //Counter of the old number of mentions every one hour.
	private boolean timerIsRunning = false;


	/** 
	 * Constructor of the class TwitterScanner
	 * This constructor validate and initialize the variables.
	 * */
	public TwitterScanner(String companyName) 
	{
		txtValidation(companyName);
		this.companyName = companyName;
		this.newCounter = 0;
		this.oldCounter =-1;
	}

	/** 
	 * Empty Constructor 
	 * */
	public TwitterScanner() {}

	/**
	 * run() Method
	 * This method run the program. it have a task that can be scheduled for repeated execution by a Timer
	 * Every one hour its calculate the value of mentions of a company in twitter. 
	 * 
	 */
	public void run() 
	{
		// Begin aggregating mentions. Every hour, "store" the relative change
		TimerTask task = new TimerTask() //task to be scheduled.
				{
			@Override
			public void run() 
			{
				// task to run every one hour.
				storeValue(getNumberOfMentions());
			}
				};

				long delay = 3600*1000;      //delay in milliseconds before task is to be executed.
				long intevalPeriod =delay;   //time in milliseconds between successive task executions.
				// schedules the task to be run in an interval 
				this.timer.scheduleAtFixedRate(task, delay, intevalPeriod);
				runTwitterStream(); //here we run the Twitter stream.
				timerIsRunning = true;
	}

	/**
	 * runTwitterStream() Method
	 * set up the Configuration object that is responsible for specifying which LoginModules 
	 * should be used for a particular application, and in what order the LoginModules should be invoked.
	 * If the word facebook founded then the counter will be increased by 1.
	 * Add listener to the twitterStream object to listen for new text from the Twitter API. 
	 */
	private void runTwitterStream()
	{
		//Make sure to write the right API keys to make it works.
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(Constants.consumerKey)
		.setOAuthConsumerSecret(Constants.consumerSecret)
		.setOAuthAccessToken(Constants.token)
		.setOAuthAccessTokenSecret(Constants.tokenSecret);

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		RawStreamListener listener = new RawStreamListener() 
		{
			public void onMessage(String rawJSON) 
			{
				Status status = null;
				try {
					status = TwitterObjectFactory.createStatus(rawJSON); //Constructs a Status object from rawJSON string.
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				String text = status.getText();
				if (text.contains(companyName)) // check if the text have the word facebook
				{
					setNewCounter(getNewCounter()+1); // increment of counter for the mentions 
				}

			}

			public void onException(Exception ex) 
			{
				ex.printStackTrace();
			}

		};
		twitterStream.addListener(listener);
		twitterStream.sample();
	}

	/**
	 * storeValue() 
	 * Print out the informations about timestamp and the number of mentions of a company on Twitter 
	 * change over time. 
	 * @param value   the object that hold the value of mentions and the timestamp
	 */
	private void storeValue(TSValue value) 
	{
		System.out.println("TimeStamp in seconds = "+(value.getTimestamp().getEpochSecond()) + " And " + value.getVal() + '%');
	}

	/**
	 * txtValidation() method
	 * making validation of the text to see if its null or empty.
	 * @param name the company name to be tested. 
	 */
	public void txtValidation(String name)
	{
		if(name.isEmpty() || name == null)
		{
			throw new IllegalArgumentException("Wrong: please write the company name and try again!");
		}
	}

	/**
	 * getNumberOfMentions() 
	 * Geting the calculated number of mentions by calling the method calculateMentions()
	 * create a new TSValue object to be used and and to print out out.
	 * @return  the TSValue object that hold the value of mentions and the timestamp.
	 */
	public TSValue getNumberOfMentions()
	{
		int newCounter = getNewCounter();
		TSValue mentionsObj = new TSValue(Instant.now(),calculateMentions(newCounter,getOldCounter()));
		setOldCounter(newCounter);
		setNewCounter(0);
		return mentionsObj;
	}

	/**
	 * calculateMentions()
	 * Calculating the number of mentions and return percentage changes.
	 * @param newCounter = Counter of the new number of mentions every one hour.
	 * @param oldCounter = Counter of the old number of mentions every one hour.
	 * @return the calculated value of percentage changes.
	 */
	private double calculateMentions(int newCounter, int oldCounter)
	{
		double mentions = 0.0;
		if(newCounter > 0 && oldCounter <1)
		{
			mentions = 100.0;
		}else{
			mentions = (100.0 * (newCounter - oldCounter) / oldCounter);
		}
		return mentions;
	}

	/**
	 * getNewCounter() 
	 * @return the new number of mentions
	 */
	public int getNewCounter() 
	{
		return this.newCounter;
	}

	/**
	 * set the new value to the newCounter.
	 * @param newCounter = the new number of mentions
	 */
	public void setNewCounter(int newCounter) {
		this.newCounter = newCounter;
	}

	/**
	 * getOldCounter()
	 * @return the old number of mentions every hour.
	 */
	public int getOldCounter() 
	{
		return this.oldCounter;
	}

	/**
	 * set the old number of mentions every hour.
	 * @param oldCounter
	 */
	public void setOldCounter(int oldCounter) 
	{
		this.oldCounter = oldCounter;
	}


	/**
	 * @return the timer object
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * 
	 * @param timer = The timer object
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * @return the companyName 
	 */
	public String getCompanyName() {
		return companyName;
	}
	
	/**
	 * @param companyName = the name of the company
	 */
	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	/**
	 * @return timerIsRunning = boolean to check if the time is running or not 
	 */
	public boolean isTimerIsRunning() {
		return timerIsRunning;
	}

	/**
	 * @param timerIsRunning = a variable that hold a true or false value. 
	 */
	public void setTimerIsRunning(boolean timerIsRunning) 
	{
		this.timerIsRunning = timerIsRunning;
	}

	/**
	 * This is the main method to initialize the TwitterScanner and to run the program.
	 */
	public static void main(String ... args)
	{
		TwitterScanner scanner = new TwitterScanner("Facebook");
		scanner.run();
	}

	/**
	 * TSValue
	 * TSValue Class hold the variables of the twitter text like the timestamp and the number of mentions.
	 */
	public static class TSValue 
	{
		private final Instant timestamp;
		private final double val;

		public TSValue(Instant timestamp, double val) 
		{
			this.timestamp = timestamp;
			this.val = val;
		}

		public Instant getTimestamp() 
		{
			return timestamp;
		}

		public double getVal() 
		{
			return val;
		}
	}



}
