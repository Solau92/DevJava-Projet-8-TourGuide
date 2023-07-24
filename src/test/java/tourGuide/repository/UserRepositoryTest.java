package tourGuide.repository;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("testFalse")
class UserRepositoryTest {

	@InjectMocks
	private UserRepositoryImpl userRepository;

	private Map<String, User> users = new HashMap<>();

	User user1;
	User user2;


	@BeforeAll
	public static void setUsersTestConfig() {
		InternalTestHelper.setInternalUserNumber(100);
	}

	@BeforeEach
	public void setUp() {
		user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), new Location(10, 10), new Date()));
		user2.addToVisitedLocations(new VisitedLocation(user2.getUserId(), new Location(20, 20), new Date()));

		userRepository.addUser(user1);
		userRepository.addUser(user2);
	}


	@Test
	void getAllUsers_Ok_Test() {

		// GIVEN
		// WHEN
		Map<String, User> usersFound = userRepository.getAllUsers();

		// THEN
		assertEquals(2, usersFound.size());
	}

	@Test
	void getUserByUsername_Ok_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		User userFound = userRepository.getUserByUserName(user1.getUserName()).get();

		// THEN
		assertEquals(user1.getEmailAddress(), userFound.getEmailAddress());
	}

	@Test
	void getUserByUsername_NotFound_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		Optional<User> userFound = userRepository.getUserByUserName("userNotFoundName");

		// THEN
		assertTrue(userFound.isEmpty());
	}

	@Test
	void addUser_Ok_Test() {

		// GIVEN
		User user4 = new User(UUID.randomUUID(), "userName4", "phoneNumber4", "emailAddress4");

		// WHEN
		Optional<User> userAdded = userRepository.addUser(user4);

		// THEN
		assertEquals("userName4", userAdded.get().getUserName());
	}

	@Test
	void addUser_UserAlreadyExists_Test() {

		// GIVEN
		// WHEN
		userRepository.addUser(user1);
		Optional<User> userAdded = userRepository.addUser(user1);

		// THEN
		assertTrue(userAdded.isEmpty());
	}

	@Test
	void getUserLocation_LocationFound_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		Optional<Location> locationFound = userRepository.getUserLocation(userRepository.getUserByUserName(user1.getUserName()).get());

		// THEN
		assertFalse(locationFound.isEmpty());
	}

	@Test
	void getUserLocation_NoLocationFound_Test() throws UserNotFoundException {

		// GIVEN
		User user3 = new User(UUID.randomUUID(), "userName3", "phoneNumber3", "emailAddress3");
		userRepository.addUser(user3);

		// WHEN
		Optional<Location> locationFound = userRepository.getUserLocation(user3);

		// THEN
		assertTrue(locationFound.isEmpty());
	}

	@Test
	void getAllCurrentLocations_Ok_Test() {

		// GIVEN
		// WHEN
		Map<UUID, Location> currentLocationsFound = userRepository.getAllCurrentLocations();

		// THEN
		assertEquals(2, currentLocationsFound.size());
	}

	@Test
	void getUserRewards_Ok_Test() throws UserNotFoundException {

		// GIVEN
		VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), new Location(100, 100), new Date());
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 100, 100);
		UserReward reward1 = new UserReward(visitedLocation1, attraction1);
		userRepository.getAllUsers().get(user1.getUserName()).addUserReward(reward1);

		// WHEN
		List<UserReward> rewardsFound = userRepository.getUserRewards(user1.getUserName());

		// THEN
		assertTrue(rewardsFound.contains(reward1));
	}

	@Test
	void getUserRewards_UserNotFound_Test() {

		// GIVEN
		// WHEN

		// THEN
		assertThrows(UserNotFoundException.class, ()-> userRepository.getUserRewards("userNotFound"));
	}

	@Test
	void getUserTripDeals_Ok_Test() throws UserNotFoundException {

		// GIVEN
		Provider provider1 = new Provider(UUID.randomUUID(), "name1", 100);
		Provider provider2 = new Provider(UUID.randomUUID(), "name2", 200);
		Provider provider3 = new Provider(UUID.randomUUID(), "name3", 300);
		Provider provider4 = new Provider(UUID.randomUUID(), "name4", 400);
		Provider provider5 = new Provider(UUID.randomUUID(), "name5", 500);
		List<Provider> tripDeals = new ArrayList<>();
		tripDeals.add(provider1);
		tripDeals.add(provider2);
		tripDeals.add(provider3);
		tripDeals.add(provider4);
		tripDeals.add(provider5);
		userRepository.getAllUsers().get(user1.getUserName()).setTripDeals(tripDeals);

		// WHEN
		List<Provider> tripDealsFound = userRepository.getTripDeals(user1.getUserName());

		// THEN
		assertTrue(tripDealsFound.contains(provider1));


		// GIVEN
		VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), new Location(100, 100), new Date());
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 100, 100);
		UserReward reward1 = new UserReward(visitedLocation1, attraction1);
		userRepository.getAllUsers().get(user1.getUserName()).addUserReward(reward1);

		// WHEN
		List<UserReward> rewardsFound = userRepository.getUserRewards(user1.getUserName());

		// THEN
		assertTrue(rewardsFound.contains(reward1));
	}

	@Test
	void getUserTripDeals_UserNotFound_Test() {

		// GIVEN
		// WHEN

		// THEN
		assertThrows(UserNotFoundException.class, ()-> userRepository.getTripDeals("userNotFound"));
	}

}
