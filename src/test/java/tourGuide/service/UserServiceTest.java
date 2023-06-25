package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	@Mock
	private UserRepositoryImpl userRepository;
	private Map<String, User> users = new HashMap<>();

	User user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
	User user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");

	@BeforeEach
	public void setUp() {
		users.put(user1.getUserName(), user1);
		users.put(user2.getUserName(), user2);
	}

//	Map<UUID, Location> setUpLocation(){
//		Location location1 = new Location(0.50, 0.60);
//		Location location2 = new Location(0.55, 0.66);
//		Map<UUID, Location> locations = new HashMap<>();
//		locations.put(UUID.randomUUID(), location1);
//		locations.put(UUID.randomUUID(), location2);
//		return locations;
//	}

	@AfterEach
	public void tearDown() {
		users = new HashMap<>();
	}

	@Test
	void getAllUsers_Ok_Test() {

		// GIVEN
		when(userRepository.getAllUsers()).thenReturn(users);

		// WHEN
		Map<String, User> usersFound = userService.getAllUsers();

		// THEN
		assertEquals(2, usersFound.size());
		assertTrue(usersFound.containsValue(user1));
	}

	@Test
	void getUserByUserName_Ok_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));

		// WHEN
		Optional<User> userFound = userService.getUserByUserName("userName1");

		// THEN
		assertEquals("phoneNumber1", userFound.get().getPhoneNumber());
	}

	@Test
	void getUserByUserName_UserNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.empty());

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> userService.getUserByUserName("userNotFound"));
	}

	@Test
	void addUser_Ok_Test() throws UserAlreadyExistsException {

		// GIVEN
		when(userRepository.addUser(user1)).thenReturn(Optional.of(user1));

		// WHEN
		Optional<User> userAdded = userService.addUser(user1);

		// THEN
		assertEquals("phoneNumber1", userAdded.get().getPhoneNumber());
	}

	@Test
	void addUser_UserAlreadyExists_Test() throws UserAlreadyExistsException {

		// GIVEN
		when(userRepository.addUser(user1)).thenReturn(Optional.empty());

		// WHEN
		// THEN
		assertThrows(UserAlreadyExistsException.class, ()-> userService.addUser(user1));
	}

	@Test
	void getUserLocation_Found_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		Location location = new Location(50.0, 60.0);
		when(userRepository.getUserLocation(any(User.class))).thenReturn(Optional.of(location));

		// WHEN
		Optional<Location> locationFound = userService.getUserLocation("userName1");

		// THEN
		assertEquals(50.0, locationFound.get().latitude);
	}

	@Test
	void getUserLocation_LocationNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userRepository.getUserLocation(any(User.class))).thenReturn(Optional.empty());

		// WHEN
		Optional<Location> locationFound = userService.getUserLocation("userName1");

		// THEN
		assertTrue(locationFound.isEmpty());
	}

	@Test
	void getAllCurrentLocation_Ok_Test() {

		// GIVEN
		Location location1 = new Location(0.50, 0.60);
		Location location2 = new Location(0.55, 0.66);
		Map<UUID, Location> locations = new HashMap<>();
		locations.put(UUID.randomUUID(), location1);
		locations.put(UUID.randomUUID(), location2);
		when(userRepository.getAllCurrentLocations()).thenReturn(locations);

		// WHEN
		Map<UUID, Location> locationsFound = userService.getAllCurrentLocations();

		// THEN
		assertEquals(2, locationsFound.size());
		assertTrue(locationsFound.containsValue(location1));
	}

	@Test
	void getUserRewards_Ok_Test() throws UserNotFoundException {

		// TODO
		// GIVEN
		Location location1 = new Location(0.50, 0.60);
		VisitedLocation visitedLocation1 = new VisitedLocation(UUID.randomUUID(), location1, new Date());
		Attraction attraction1 = new Attraction("name1", "city1", "state1", 0.50, 0.60);
		UserReward reward1 = new UserReward(visitedLocation1, attraction1, 11);

		Location location2 = new Location(0.55, 0.66);
		VisitedLocation visitedLocation2 = new VisitedLocation(UUID.randomUUID(), location2, new Date());
		Attraction attraction2 = new Attraction("name2", "city2", "state2", 0.50, 0.60);
		UserReward reward2 = new UserReward(visitedLocation1, attraction1, 22);

		List<UserReward> rewards = new ArrayList<>();
		rewards.add(reward1);
		rewards.add(reward2);

		when(userRepository.getUserRewards(anyString())).thenReturn(rewards);

		// WHEN
		List<UserReward> rewardsFound = userService.getUserRewards("userName1");

		// THEN
		assertEquals(2, rewardsFound.size());
		assertTrue(rewardsFound.contains(reward1));

	}

	@Test
	void getUserRewards_UserNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.empty());
		when(userRepository.getUserRewards(anyString())).thenThrow(UserNotFoundException.class);

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> userService.getUserRewards("userNotFound"));
	}

	@Disabled
	@Test
	void getTripDeas_Ok_Test() {

		// TODO
		// GIVEN

		// WHEN

		// THEN
		fail("not yet implemented");

	}

	@Test
	void getTripDeals_UserNotFound_Test() throws UserNotFoundException {

		// GIVEN
		when(userRepository.getUserByUserName(anyString())).thenReturn(Optional.empty());
		when(userRepository.getTripDeals(anyString())).thenThrow(UserNotFoundException.class);

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> userService.getTripDeals("userNotFound"));
	}

}
