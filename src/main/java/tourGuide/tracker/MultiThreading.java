//package tourGuide.tracker;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import tourGuide.service.implementation.TourGuideServiceImpl;
//import tourGuide.service.implementation.UserServiceImpl;
//import tourGuide.user.User;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class MultiThreading {
//
//	private Logger logger = LoggerFactory.getLogger(MultiThreading.class);
//
//	private UserServiceImpl userService;
//
//	private TourGuideServiceImpl tourGuideService;
//
//	private ExecutorService executorService;
//
//	private int numberOfThreads = 20 ;
//
//	public MultiThreading(UserServiceImpl userService, TourGuideServiceImpl tourGuideService){
//		this.userService = userService;
//		this.tourGuideService = tourGuideService;
//		this.executorService = Executors.newFixedThreadPool(numberOfThreads);
//		this.track();
//	}
//
//	public void track() {
//
//		List<User> users = new ArrayList<>(userService.getAllUsers().values());
//		int size = users.size();
//
//		logger.info("size dans multithreading : " + size);
//
//		for(int i = 0 ; i < numberOfThreads ; i ++) {
//
//			logger.info("Thread " + i + " will treat users between " + (i*size/numberOfThreads) + " and " + ((i+1)*size/numberOfThreads-1));
//			executorService.execute(new WorkerTracking(this.tourGuideService, users.subList(i*size/numberOfThreads, (i+1)*size/numberOfThreads)));
//
//		}
//
//		logger.debug("shutdown");
//		executorService.shutdown();
////		try {
////			executorService.awaitTermination(15, TimeUnit.MINUTES);
////		} catch (InterruptedException e) {
////			throw new RuntimeException(e);
////		}
//
//	}
//
//}
