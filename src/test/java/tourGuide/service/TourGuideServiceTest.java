package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TourGuideServiceTest {

	// QUand
	@InjectMocks
	private TourGuideServiceImpl tourGuideService;

	@Mock
	private GpsUtil gpsUtil;

	@Mock
	private RewardsServiceImpl rewardService;

	@Mock
	private TripPricer tripPricer;

	@Mock
	private UserServiceImpl userService;

	@Mock
	private UserRepositoryImpl userRepository;

	User user1;
	Location location1;
	VisitedLocation visitedLocation1;

	@BeforeAll
	static void setUpFormat() {
		Locale.setDefault(Locale.US);
	}

	@BeforeEach
	void setUp() {
		user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		location1 = new Location(50.0, 60.0);
		visitedLocation1 = new VisitedLocation(UUID.randomUUID(), location1, new Date());
	}

	@Test
	void getUserLocation_LocationFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.of(location1));

		// WHEN
		Location locationFound = tourGuideService.getUserLocation("userName1");

		// THEN
		assertEquals(location1.latitude, locationFound.latitude);
	}

	@Test
	void getUserLocation_LocationTracked_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userService.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userRepository.getUserLocation(any(User.class))).thenReturn(Optional.of(location1));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.empty());
		when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation1);

		// WHEN
		Location locationFound = tourGuideService.getUserLocation("userName1");

		// THEN
		assertEquals(location1.latitude, locationFound.latitude);
	}

	@Test
	void getUserLocation_UserNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.empty());
		when(userService.getUserByUserName(anyString())).thenThrow(UserNotFoundException.class);

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> tourGuideService.getUserLocation("userName1"));
	}

	@Test
	void getAllCurrentLocations_Ok_Test() throws UserNotFoundException {

		// GIVEN
		user1.addToVisitedLocations(visitedLocation1);

		User user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
		Location location2 = new Location(50.0, 60.0);
		VisitedLocation visitedLocation2 = new VisitedLocation(UUID.randomUUID(), location2, new Date());
		user2.addToVisitedLocations(visitedLocation2);

		Map<String, User> users = new HashMap<>();
		users.put(user1.getUserName(), user1);
		users.put(user2.getUserName(), user2);

		when(userService.getAllUsers()).thenReturn(users);
		when(userRepository.getUserLocation(user1)).thenReturn(Optional.of(location1), Optional.of(location2));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.of(location1), Optional.of(location2));
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1), Optional.of(user2));
		when(userService.getUserByUserName(anyString())).thenReturn(Optional.of(user1), Optional.of(user2));

		// WHEN
		Map<UUID, Location> locationsFound = tourGuideService.getAllCurrentLocations();

		// THEN
		assertEquals(2, locationsFound.size());
		assertTrue(locationsFound.containsKey(user2.getUserId()));
		assertTrue(locationsFound.containsValue(location2));

	}

	@Test
	void trackAllUsersLocationOnce_Ok_Test() {

		// GIVEN

		User user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
		User user3 = new User(UUID.randomUUID(), "userName3", "phoneNumber3", "emailAddress3");
		Map<String, User> users = new HashMap<String, User>();
		users.put(user1.getUserName(), user1);
		users.put(user2.getUserName(), user2);
		users.put(user3.getUserName(), user3);

		when(userService.getAllUsers()).thenReturn(users);

		// Pre-check
		List<User> allUsers = new ArrayList<>(userService.getAllUsers().values());
		Map<User, Integer> visitedLocationSize = new HashMap<>();
		for (User u : allUsers) {
			visitedLocationSize.put(u, u.getVisitedLocations().size());
		}
		for (int i = 0; i < users.size(); i++) {
			assertEquals(0, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}

		// WHEN
		tourGuideService.trackAllUsersLocationOnce();

		// THEN
		for (int i = 0; i < users.size(); i++) {
			assertEquals(1, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}
	}

	@Test
	void trackUserLocation_Test_Ok(){

		// GIVEN
		when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation1);

		// WHEN
		VisitedLocation visitedLocationFound = tourGuideService.trackUserLocation(user1);

		// THEN
		assertEquals(location1, visitedLocationFound.location);
		assertEquals(1, user1.getVisitedLocations().size());
		verify(rewardService, Mockito.times(1)).calculateRewards(user1);
	}

	@Test
	void getTripDeals_Ok_Test() throws UserNotFoundException {

		// GIVEN
		when(userService.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));

		List<Provider> providers = new ArrayList<>();
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(providers);

		TripDealsPrefDto tripDealsPrefDto = new TripDealsPrefDto();
		tripDealsPrefDto.setUserName(user1.getUserName());
		tripDealsPrefDto.setTripDuration(2);
		tripDealsPrefDto.setNumberOfAdults(7);
		tripDealsPrefDto.setNumberOfChildren(1);

		// WHEN
		List<Provider> providersFound = tourGuideService.getTripDeals(tripDealsPrefDto);

		// THEN
		assertEquals(5, user1.getTripDeals().size());

	}

}
