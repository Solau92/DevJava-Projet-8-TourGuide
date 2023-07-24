package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class GpsServiceTest {

	List<Attraction> attractions;
	User user1;
	Location location1;
	VisitedLocation visitedLocation1;
	@InjectMocks
	private GpsServiceImpl gpsService;
	@Mock
	private GpsRepositoryImpl gpsRepository;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private UserRepositoryImpl userRepository;
	@Mock
	private RewardsServiceImpl rewardsService;

	@BeforeAll
	static void setUpFormat() {
		Locale.setDefault(Locale.US);
	}

	@BeforeEach
	void setUp() {
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 50.0, 60.0);
		Attraction attraction2 = new Attraction("attraction2", "city2", "state2", 55.0, 66.0);
		Attraction attraction3 = new Attraction("attraction3", "city3", "state3", 60.0, 70.0);
		Attraction attraction4 = new Attraction("attraction4", "city4", "state4", 65.0, 77.0);
		Attraction attraction5 = new Attraction("attraction5", "city5", "state5", 70.0, 80.0);
		Attraction attraction6 = new Attraction("attraction6", "city6", "state6", 75.0, 88.0);
		attractions = new ArrayList<>();
		attractions.add(attraction1);
		attractions.add(attraction2);
		attractions.add(attraction3);
		attractions.add(attraction4);
		attractions.add(attraction5);
		attractions.add(attraction6);

		user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		location1 = new Location(50.0, 60.0);
		visitedLocation1 = new VisitedLocation(UUID.randomUUID(), location1, new Date());
	}

	@Test
	void getAllAttractions_Ok_Test() {

		// GIVEN
		when(gpsRepository.getAllAttractions()).thenReturn(attractions);

		// WHEN
		List<Attraction> attractionsFound = gpsService.getAllAttractions();

		// THEN
		assertEquals(6, attractionsFound.size());
		assertTrue(attractionsFound.contains(attractions.get(0)));
	}

	@Test
	void getNearbyAttractions_Ok_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userService.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.of(location1));
		when(gpsRepository.getAllAttractions()).thenReturn(attractions);
		when(rewardsService.getRewardPoints(any(Attraction.class), any(User.class))).thenReturn(10);

		// WHEN
		List<NearByAttractionDto> nearByAttractions = gpsService.getNearbyAttractions("userName1");

		// THEN
		assertEquals(5, nearByAttractions.size());
	}

	@Test
	void getNearbyAttractions_UserNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.empty());
		when(userService.getUserByUserName(anyString())).thenThrow(UserNotFoundException.class);
		when(userService.getUserLocation(anyString())).thenThrow(UserNotFoundException.class);

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> gpsService.getNearbyAttractions("userName1"));
	}

	@Test
	void getUserLocation_LocationFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.of(location1));

		// WHEN
		Location locationFound = gpsService.getUserLocation("userName1");

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
		when(gpsService.getUserLocation(any(UUID.class))).thenReturn(visitedLocation1);

		// WHEN
		Location locationFound = gpsService.getUserLocation("userName1");

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
		assertThrows(UserNotFoundException.class, () -> gpsService.getUserLocation("userName1"));
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
		Map<UUID, Location> locationsFound = gpsService.getAllCurrentLocations();

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
		gpsService.trackAllUsersLocationOnce();

		// THEN
		for (int i = 0; i < users.size(); i++) {
			assertEquals(1, allUsers.get(i).getVisitedLocations().size(), "user " + i);
		}
	}

	@Test
	void trackUserLocation_Test_Ok() {

		// GIVEN
		when(gpsRepository.getUserLocation(any(UUID.class))).thenReturn(visitedLocation1);

		// WHEN
		VisitedLocation visitedLocationFound = gpsService.trackUserLocation(user1);

		// THEN
		assertEquals(location1, visitedLocationFound.location);
		assertEquals(1, user1.getVisitedLocations().size());
		verify(rewardsService, Mockito.times(1)).calculateRewards(user1);
	}

}
