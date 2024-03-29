package tourGuide.performance;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.helper.UsersTestConfig;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testTrue")
class TestPerformance {

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
	void highVolumeTrackLocation() {

		//// GIVEN ////

		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		int nbOfUsers = 100;
		InternalTestHelper.setInternalUserNumber(nbOfUsers);

		GpsUtil gpsUtil = new GpsUtil();
		GpsRepositoryImpl gpsRepository = new GpsRepositoryImpl(gpsUtil);
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsRepository, new RewardCentral());
		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
		GpsServiceImpl gpsService = new GpsServiceImpl(gpsRepository, userService, rewardsService);

		List<User> allUsers = new ArrayList<>(userService.getAllUsers().values());

		Map<User, Integer> visitedLocationSize = new HashMap<>();
		for (User u : allUsers) {
			visitedLocationSize.put(u, u.getVisitedLocations().size());
		}

		// Checking that there are three visited locations for each user
		for (int i = 0; i < nbOfUsers; i++) {
			assertEquals(3, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}

		//// WHEN ////

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		gpsService.trackAllUsersLocationOnce();

		stopWatch.stop();

		//// THEN ////

		// Checking that there are now four visited locations for each user
		for (int i = 0; i < nbOfUsers; i++) {
			assertEquals(4, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}

		// Checking the performance
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}


	@Test
	void highVolumeGetRewards() {

		//// GIVEN ////

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		int nbOfUsers = 100;
		InternalTestHelper.setInternalUserNumber(nbOfUsers);

		GpsUtil gpsUtil = new GpsUtil();
		GpsRepositoryImpl gpsRepository = new GpsRepositoryImpl(gpsUtil);
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsRepository, new RewardCentral());
		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>(userService.getAllUsers().values());

		// Checking that there is no reward for each user
		for (User user : allUsers) {
			assertEquals(0, user.getUserRewards().size());
		}

		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		//// WHEN ////

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		rewardsService.calculateAllRewards(allUsers);

		stopWatch.stop();

		//// THEN ////

		// Checking that there is at least one reward for each user
		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
			for (UserReward reward : user.getUserRewards()) {
				assertTrue(reward.getRewardPoints() > 0);
			}
		}

		// Checking the performance
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
