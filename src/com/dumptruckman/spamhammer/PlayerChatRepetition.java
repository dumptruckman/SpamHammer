package com.dumptruckman.spamhammer;

import java.util.ArrayDeque;

public class PlayerChatRepetition {

	/* Internal types */
	private class TimedMessage {
		// Data
		public String message = "";
		public long timestamp = 0;
		// Constructor
		public TimedMessage (String message_, Long timestamp_) {
			message = message_;
			timestamp = timestamp_;
		}
	}

	/* Data */
	long maxPeriod = 40 * 1000; // milliseconds, must be > 0
	int maxMessages = 8; // within the interval
    private ArrayDeque<TimedMessage> log; // cumulated messages
    
    /**
     * Constructor.
     */
    public PlayerChatRepetition() {
    	log = new ArrayDeque<TimedMessage>();
    }
    
    /**
     * Add message to the repetition queue.
     * See isSpamming().
     * 
     * @param message Message from player. If null, nothing is done.
     * @param repeatTimePeriod 
     * @param repeatMessageLimit 
     * @return true, if player is spamming repetitive lines.
     */
	public void addMessage(String message, int repeatMessageLimit, long repeatTimePeriod) {
		if (message == null) return;
		
		maxMessages = repeatMessageLimit;
		maxPeriod = repeatTimePeriod;
		
		if (log.size() == 0) {
			// just add it
			log.add(new TimedMessage(message, System.currentTimeMillis()));
		} else if (message.equals(log.getFirst().message)) {
			// the new message is same as the previous one
	        log.add(new TimedMessage(message, System.currentTimeMillis()));

			// get rid of expired messages; at least one is saved (with interval 0)
	        while (log.getLast().timestamp - log.getFirst().timestamp > maxPeriod) {
	        	log.removeFirst();
	        }
		} else {
			// the message is different than the previous one
			// start cumulating again
			log.clear();
			log.add(new TimedMessage(message, System.currentTimeMillis()));
		}
	}
	
	/**
	 * Returns true, if the player is spamming.
	 * See addMessage().
	 */
	public boolean isSpamming() {
		return log.size() > maxMessages; 
	}
}
