package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTourGuideService {

	@BeforeAll
	static void setUp() {
		Locale.setDefault(Locale.US);
	}

//	@Test
//	public void getUserLocation() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, new UserServiceImpl(new UserRepositoryImpl()));
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//		tourGuideService.tracker.stopTracking();
//		assertTrue(visitedLocation.userId.equals(user.getUserId()));
//	}
//
//	@Test
//	public void addUser() throws UserAlreadyExistsException, UserNotFoundException {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user = new User(UUID.randomUUID(), "jon", "001", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "002", "jon2@tourGuide.com");
//
//		userService.addUser(user);
//		userService.addUser(user2);
//
//		User retrivedUser = userService.getUserByUserName(user.getUserName()).get();
//		User retrivedUser2 = userService.getUserByUserName(user2.getUserName()).get();
//
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(user.getEmailAddress(), retrivedUser.getEmailAddress());
//		assertEquals(user2.getPhoneNumber(), retrivedUser2.getPhoneNumber());
//	}

//	@Test
//	public void getAllUsers() throws UserAlreadyExistsException {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
//
//		userService.addUser(user);
//		userService.addUser(user2);
//
//		Map<String, User> allUsers = userService.getAllUsers();
//
//		tourGuideService.tracker.stopTracking();
//
//		assertTrue(allUsers.containsValue(user));
//		assertTrue(allUsers.containsValue(user2));
//	}

	@Test
	public void trackUser() {

		GpsUtil gpsUtil = new GpsUtil();
		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}
//
//	@Disabled // Not yet implemented
//	@Test
//	public void getNearbyAttractions() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//
//		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
//
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(5, attractions.size());
//	}
//
//	public void getTripDeals() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//		List<Provider> providers = tourGuideService.getTripDeals(user);
//
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(10, providers.size());
//	}
//
	
}
