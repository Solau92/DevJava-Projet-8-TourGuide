package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
class TestRewardsService {

	@BeforeAll
	static void setUp() {
		Locale.setDefault(Locale.US);
	}

	@Test
	void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		assertEquals(1, userRewards.size());
	}

	@Test
	void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	void nearAllAttractions() throws UserNotFoundException, UserAlreadyExistsException {

		GpsUtil gpsUtil = new GpsUtil();
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(0);

		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(100, 100), new Date()));
		userService.addUser(user);

		rewardsService.calculateRewards(userService.getAllUsers().get(user.getUserName()));

		List<UserReward> userRewards = userService.getUserRewards(user.getUserName());

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
