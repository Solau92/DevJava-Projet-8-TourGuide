package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.tracker.MultiThreading;
import tourGuide.user.User;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

	@BeforeAll
	static void setUp() {
		Locale.setDefault(Locale.US);
	}
	/*
	 * A note on performance improvements:
	 *
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *
	 *
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent.
	 *
	 *     These are performance metrics that we are trying to hit:
	 *
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Test
	public void highVolumeTrackLocation() {

		int nbOfUsers = 50000;

		GpsUtil gpsUtil = new GpsUtil();
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(nbOfUsers);
		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());

		List<User> allUsers = new ArrayList<>(userService.getAllUsers().values());

		Map<User, Integer> visitedLocationSize = new HashMap<>();
		for (User u : allUsers) {
			visitedLocationSize.put(u, u.getVisitedLocations().size());
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);

		for (int i = 0; i < nbOfUsers; i++) {
			assertEquals(3, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}

		tourGuideService.trackAllUsersLocationOnce();

		stopWatch.stop();

//		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

		for (int i = 0; i < nbOfUsers; i++) {
			assertEquals(4, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}

	}


	@Disabled
	@Test
	public void highVolumeGetRewards() {

//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//
//		// Users should be incremented up to 100,000, and test finishes within 20 minutes
//		InternalTestHelper.setInternalUserNumber(100);
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//	    Attraction attraction = gpsUtil.getAttractions().get(0);
//		List<User> allUsers = new ArrayList<>(userService.getAllUsers().values());
////		allUsers = tourGuideService.getAllUsers();
//		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));
//
////		allUsers.forEach(u -> rewardsService.calculateRewards(u));
//
//		allUsers.forEach(u -> rewardsService.calculateRewards(u));
//
//		for(User user : allUsers) {
//			assertTrue(user.getUserRewards().size() > 0);
//		}
//		stopWatch.stop();
//		tourGuideService.tracker.stopTracking();
//
//		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
//		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
