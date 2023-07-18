package tourGuide.worker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.user.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerTracking extends Thread {

	/**
	 * Not used in this version of the code, but could be used for a continuous tracking
	 * (the method that creates WorkerTracking should not call the stopTracking() method)
	 */
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(1);
	private boolean stop = false;

	private Logger logger = LoggerFactory.getLogger(WorkerTracking.class);
	private TourGuideServiceImpl tourGuideServiceImpl;

	private List<User> users;

	public WorkerTracking(TourGuideServiceImpl tourGuideServiceImpl, List<User> users) {
		this.tourGuideServiceImpl = tourGuideServiceImpl;
		this.users = users;
		logger.debug("new track");
	}

	/**
	 * Stops the run() method
	 */
	public void stopTracking() {
		stop = true;
		this.interrupt();
	}

	/**
	 * Tracks user location for each user of the users list.
	 */
	@Override
	public void run() {

		while (true) {
			StopWatch stopWatch = new StopWatch();

			logger.info("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();

			logger.info("Thread name :" + Thread.currentThread().getName());

			for (User u : users) {
				tourGuideServiceImpl.trackUserLocation(u);
			}

			stopWatch.stop();
			logger.info("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
