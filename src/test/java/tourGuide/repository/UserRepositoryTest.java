package tourGuide.repository;

import gpsUtil.location.Location;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.helper.UsersTestConfig;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
class UserRepositoryTest {

//	@InjectMocks
//	private UserRepositoryImpl userRepository;
//
//	@Mock
//	private static UsersTestConfig usersTestConfig;
//
//	private Map<String, User> users = new HashMap<>();
//
//	User user1;
//	User user2;
//
//
//	@BeforeAll
//	public static void setUsersTestConfig() {
//		InternalTestHelper.setInternalUserNumber(100);
//	}
//
//	@BeforeEach
//	public void setUp() {
//		user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
//		user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
////		users.put(user1.getUserName(), user1);
////		users.put(user2.getUserName(), user2);
//	}
//
//	@AfterEach
//	public void tearDown() {
//		users = new HashMap<>();
//	}
//
//	@Test
//	void getAllUsers_Ok_Test() {
//
//		// GIVEN
//		// WHEN
//		Map<String, User> usersFound = userRepository.getAllUsers();
//
//		// THEN
//		assertEquals(InternalTestHelper.getInternalUserNumber(), usersFound.size());
//	}
//
//	@Test
//	void getUserByUsername_Ok_Test() throws UserNotFoundException {
//
//		// GIVEN
//		// WHEN
//		User userFound = userRepository.getUserByUserName("internalUser5").get();
//
//		// THEN
//		assertEquals("internalUser5@tourGuide.com", userFound.getEmailAddress());
//	}
//
//	@Test
//	void getUserByUsername_NotFound_Test() throws UserNotFoundException {
//
//		// GIVEN
//		// WHEN
//		Optional<User> userFound = userRepository.getUserByUserName("userNotFoundName");
//
//		// THEN
//		assertTrue(userFound.isEmpty());
//	}
//
//	@Test
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
//
//	@Test
//	void getUserLocation_LocationFound_Test() throws UserNotFoundException {
//
//		/////////////////////////////// TODO Test à revoir
//
//		// GIVEN
//		// WHEN
//		Optional<Location> locationFound = userRepository.getUserLocation(userRepository.getUserByUserName("internalUser32").get());
//
//		// THEN
//		assertFalse(locationFound.isEmpty());
//	}
//
//	@Disabled
//	@Test
//	void getUserLocation_NoLocationFound_Test() throws UserNotFoundException {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//
//		// THEN
//		fail("not yet implemented");
//	}
//
//	@Test
//	void getAllCurrentLocations_Ok_Test() {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//		Map<UUID, Location> currentLocationsFound = userRepository.getAllCurrentLocations();
//
//		// THEN
//		assertEquals(100, currentLocationsFound.size());
//	}
//
//	@Disabled
//	@Test
//	void getUserRewards_Ok_Test() {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//
//		// THEN
//		fail("not yet implemented");
//	}
//
//	@Disabled
//	@Test
//	void getUserRewards_UserNotFound_Test() {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//
//		// THEN
//		fail("not yet implemented");
//	}
//
//	@Disabled
//	@Test
//	void getUserTripDeals_Ok_Test() {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//
//		// THEN
//		fail("not yet implemented");
//	}
//
//	@Disabled
//	@Test
//	void getUserTripDeals_UserNotFound_Test() {
//
//		//TODO
//
//		// GIVEN
//		// WHEN
//
//		// THEN
//		fail("not yet implemented");
//	}

}
