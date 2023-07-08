package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.user.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerTracking extends Thread {
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private Logger logger = LoggerFactory.getLogger(WorkerTracking.class);
	private TourGuideServiceImpl tourGuideServiceImpl;
	private boolean stop = false;

	private List<User> users;

	public WorkerTracking(TourGuideServiceImpl tourGuideServiceImpl, List<User> users) {
		this.tourGuideServiceImpl = tourGuideServiceImpl;
		this.users = users;
		logger.debug("new track");
	}

	public void stopTracking() {
		stop = true;
		this.interrupt();
	}

	@Override
	public void run() {

		while(true) {
			StopWatch stopWatch = new StopWatch();

			logger.info("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();

			logger.info("Thread name :" + Thread.currentThread().getName());

			for (User u : users) {
				tourGuideServiceImpl.trackUserLocation(u);
//				logger.info("trackedUserLocation for user " + u.getUserName());
			}
			//			users.forEach(u -> tourGuideServiceImpl.trackUserLocation(u));


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
