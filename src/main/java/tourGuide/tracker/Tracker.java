//package tourGuide.tracker;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.commons.lang3.time.StopWatch;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import tourGuide.service.implementation.TourGuideServiceImpl;
//import tourGuide.service.implementation.UserServiceImpl;
//import tourGuide.user.User;
//
//public class Tracker extends Thread {
//	private Logger logger = LoggerFactory.getLogger(Tracker.class);
//	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
//
////	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//	private TourGuideServiceImpl tourGuideServiceImpl;
//	private UserServiceImpl userService;
//	private boolean stop = false;
//
//	public Tracker(TourGuideServiceImpl tourGuideServiceImpl, UserServiceImpl userService) {
//		this.tourGuideServiceImpl = tourGuideServiceImpl;
//		this.userService = userService;
//		executorService.submit(this);
//	}
//
//
//	/**
//	 * Assures to shut down the Tracker thread
//	 */
//	public void stopTracking() {
//		stop = true;
//		executorService.shutdownNow();
//	}
//
//	@Override
//	public void run() {
//
//		StopWatch stopWatch = new StopWatch();
//
//		while(true) {
//			if(Thread.currentThread().isInterrupted() || stop) {
//				logger.debug("Tracker stopping");
//				break;
//			}
//
//			Collection<User> userCollection = userService.getAllUsers().values();
//			List<User> users = new ArrayList<>(userCollection);
//
//			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
//			stopWatch.start();
//
//			logger.debug("Thread name :" + Thread.currentThread().getName());
//
//			users.forEach(u -> tourGuideServiceImpl.trackUserLocation(u));
//			logger.debug("trackedUserLocation !");
//
//			stopWatch.stop();
//			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
//
//			try {
//				logger.debug("Tracker sleeping");
//				TimeUnit.SECONDS.sleep(trackingPollingInterval);
//			} catch (InterruptedException e) {
//				break;
//			}
//		}
//
//	}
//}
