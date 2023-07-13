package tourGuide.repository;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.helper.UsersTestConfig;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

	@InjectMocks
	private UserRepositoryImpl userRepository;

	@Mock
	private static UsersTestConfig usersTestConfig;

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
		users.put(user1.getUserName(), user1);
		users.put(user2.getUserName(), user2);
	}

	@AfterEach
	public void tearDown() {
		users = new HashMap<>();
	}

	@Test
//	@Order(1)
	void getAllUsers_Ok_Test() {

		// GIVEN
		// WHEN
		Map<String, User> usersFound = userRepository.getAllUsers();

		// THEN
		assertEquals(InternalTestHelper.getInternalUserNumber(), usersFound.size());
	}

	@Test
	void getUserByUsername_Ok_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		User userFound = userRepository.getUserByUserName("internalUser5").get();

		// THEN
		assertEquals("internalUser5@tourGuide.com", userFound.getEmailAddress());
	}

	@Test
	void getUserByUsername_NotFound_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		Optional<User> userFound = userRepository.getUserByUserName("userNotFoundName");

		// THEN
		assertTrue(userFound.isEmpty());
	}

//	@Test
//	@Order(3)
//	void addUser_Ok_Test() {
//
//		// GIVEN
//		// WHEN
//		Optional<User> userAdded = userRepository.addUser(user1);
//
//		// THEN
//		assertEquals("userName1", userAdded.get().getUserName());
//	}
//
//	@Test
//	@Order(4)
//	void addUser_UserAlreadyExists_Test() {
//
//		// GIVEN
//		// WHEN
//		userRepository.addUser(user1);
//		Optional<User> userAdded = userRepository.addUser(user1);
//
//		// THEN
//		assertTrue(userAdded.isEmpty());
//	}

	@Test
	void getUserLocation_LocationFound_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		Optional<Location> locationFound = userRepository.getUserLocation(userRepository.getUserByUserName("internalUser1").get());

		// THEN
		assertFalse(locationFound.isEmpty());
	}

	@Test
	void getUserLocation_NoLocationFound_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		Optional<Location> locationFound = userRepository.getUserLocation(user1);

		// THEN
		assertTrue(locationFound.isEmpty());
	}

	@Test
//	@Order(2)
	void getAllCurrentLocations_Ok_Test() {

		// GIVEN
		// WHEN
		Map<UUID, Location> currentLocationsFound = userRepository.getAllCurrentLocations();

		// THEN
		assertEquals(InternalTestHelper.getInternalUserNumber(), currentLocationsFound.size());
	}

	@Test
	void getUserRewards_Ok_Test() throws UserNotFoundException {

		// GIVEN
		VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), new Location(100, 100), new Date());
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 100, 100);
		UserReward reward1 = new UserReward(visitedLocation1, attraction1);
		userRepository.getAllUsers().get("internalUser1").addUserReward(reward1);

		// WHEN
		List<UserReward> rewardsFound = userRepository.getUserRewards("internalUser1");

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
		userRepository.getAllUsers().get("internalUser1").setTripDeals(tripDeals);

		// WHEN
		List<Provider> tripDealsFound = userRepository.getTripDeals("internalUser1");

		// THEN
		assertTrue(tripDealsFound.contains(provider1));


		// GIVEN
		VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), new Location(100, 100), new Date());
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 100, 100);
		UserReward reward1 = new UserReward(visitedLocation1, attraction1);
		userRepository.getAllUsers().get("internalUser1").addUserReward(reward1);

		// WHEN
		List<UserReward> rewardsFound = userRepository.getUserRewards("internalUser1");

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
