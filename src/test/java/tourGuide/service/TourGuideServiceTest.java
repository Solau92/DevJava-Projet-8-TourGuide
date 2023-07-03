package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
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
public class TourGuideServiceTest {

	// QUand
	@InjectMocks
	private TourGuideServiceImpl tourGuideService;

	@Mock
	private GpsUtil gpsUtil;

	@Mock
	private RewardsServiceImpl rewardService;

	@Mock
	private TripPricer tripPricer;

//	@Mock
//	private Tracker tracker;

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
	void trackUserLocation_Test_Ok(){

		//TODO ? Revoir quand rewardsService.calculateRewards implémenté

		// GIVEN
		when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation1);

		// WHEN
		VisitedLocation visitedLocationFound = tourGuideService.trackUserLocation(user1);

		// THEN
		assertEquals(location1, visitedLocationFound.location);
		assertEquals(1, user1.getVisitedLocations().size());
		verify(rewardService, Mockito.times(1)).calculateRewards(user1);
		assertNotEquals(0, user1.getUserRewards().size());
	}

	@Test
	void getTripDeals_Ok_Test() {

		// TODO : revoir

		// GIVEN
		List<Provider> providers = new ArrayList<>();
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(providers);

		// WHEN
		List<Provider> providersFound = tourGuideService.getTripDeals(user1);

		// THEN
		assertEquals(5, user1.getTripDeals().size());

	}

}
