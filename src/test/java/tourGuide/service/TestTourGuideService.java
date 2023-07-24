//package tourGuide.service;
//
//import gpsUtil.GpsUtil;
//import gpsUtil.location.VisitedLocation;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import rewardCentral.RewardCentral;
//import tourGuide.dto.NearByAttractionDto;
//import tourGuide.dto.TripDealsPrefDto;
//import tourGuide.exception.UserAlreadyExistsException;
//import tourGuide.exception.UserNotFoundException;
//import tourGuide.helper.InternalTestHelper;
//import tourGuide.repository.implementation.GpsRepositoryImpl;
//import tourGuide.repository.implementation.UserRepositoryImpl;
//import tourGuide.service.GpsService;
//import tourGuide.service.implementation.GpsServiceImpl;
//import tourGuide.service.implementation.RewardsServiceImpl;
//import tourGuide.service.implementation.UserServiceImpl;
//import tourGuide.user.User;
//import tripPricer.Provider;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class TestTourGuideService {
//
//	@BeforeAll
//	static void setUp() {
//		Locale.setDefault(Locale.US);
//	}
//
//	@Test
//	void getUserLocation() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, new UserServiceImpl(new UserRepositoryImpl()));
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//		assertTrue(visitedLocation.userId.equals(user.getUserId()));
//	}
//
//	@Test
//	void addUser() throws UserAlreadyExistsException, UserNotFoundException {
//
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user1 = new User(UUID.randomUUID(), "jon1", "001", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "002", "jon2@tourGuide.com");
//
//		userService.addUser(user1);
//		userService.addUser(user2);
//
//		User retrivedUser1 = userService.getUserByUserName(user1.getUserName()).get();
//		User retrivedUser2 = userService.getUserByUserName(user2.getUserName()).get();
//
//		assertEquals(user1.getEmailAddress(), retrivedUser1.getEmailAddress());
//		assertEquals(user2.getPhoneNumber(), retrivedUser2.getPhoneNumber());
//	}
//
//	@Test
//	void getAllUsers() throws UserAlreadyExistsException {
//
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user3 = new User(UUID.randomUUID(), "jon3", "000", "jon3@tourGuide.com");
//		User user4 = new User(UUID.randomUUID(), "jon4", "000", "jon4@tourGuide.com");
//
//		userService.addUser(user3);
//		userService.addUser(user4);
//
//		Map<String, User> allUsers = userService.getAllUsers();
//
//		assertTrue(allUsers.containsValue(user3));
//		assertTrue(allUsers.containsValue(user4));
//	}
//
//	@Test
//	void trackUser() {
//
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//
//		assertEquals(user.getUserId(), visitedLocation.userId);
//	}
//
//	@Test
//	void getNearbyAttractions() throws UserNotFoundException, UserAlreadyExistsException {
//
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//		GpsService gpsService = new GpsServiceImpl(new GpsRepositoryImpl(gpsUtil), userService, tourGuideService, rewardsService);
//
//		User user6 = new User(UUID.randomUUID(), "jon6", "000", "jon6@tourGuide.com");
//		userService.addUser(user6);
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user6);
//
//		List<NearByAttractionDto> attractions = gpsService.getNearbyAttractions(user6.getUserName());
//
//		assertEquals(5, attractions.size());
//	}
//
//	@Test
//	void getTripDeals() throws UserNotFoundException, UserAlreadyExistsException {
//
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
//		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userService);
//
//		User user7 = new User(UUID.randomUUID(), "jon7", "000", "jon7@tourGuide.com");
//		userService.addUser(user7);
//
//		TripDealsPrefDto tripDealsPrefDto = new TripDealsPrefDto();
//		tripDealsPrefDto.setUserName(user7.getUserName());
//		tripDealsPrefDto.setTripDuration(7);
//		tripDealsPrefDto.setNumberOfAdults(2);
//		tripDealsPrefDto.setNumberOfChildren(1);
//
//		List<Provider> providers = tourGuideService.getTripDeals(tripDealsPrefDto);
//
//		assertEquals(5, providers.size());
//	}
//
//
//}
