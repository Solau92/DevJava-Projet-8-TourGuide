package tourGuide.repository;

import gpsUtil.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserRepositoryTest {

	// TODO : classe à revoir (/repository initialisé...), finir et nettoyer
	// Setup, Users dans le setUp

	User user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
	User user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
	@InjectMocks
	private UserRepositoryImpl userRepository;
	private Map<String, User> users = new HashMap<>();

	@BeforeEach
	public void setUp() {
		//		users.put(user1.getUserName(), user1);
		//		users.put(user2.getUserName(), user2);
	}

	@AfterEach
	public void tearDown() {
		users = new HashMap<>();
	}

	@Test
	void getAllUsers_Ok_Test() {

		// GIVEN
		// WHEN
		Map<String, User> usersFound = userRepository.getAllUsers();

		// THEN
		assertEquals(100, usersFound.size());
	}

	@Test
	void getUserByUsername_Ok_Test() throws UserNotFoundException {

		// GIVEN
		// WHEN
		User userFound = userRepository.getUserByUserName("internalUser32").get();

		// THEN
		assertEquals("internalUser32@tourGuide.com", userFound.getEmailAddress());
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
		// WHEN
		Optional<User> userAdded = userRepository.addUser(user1);

		// THEN
		assertEquals("userName1", userAdded.get().getUserName());
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

		/////////////////////////////// TODO Test à revoir

		// GIVEN
		// WHEN
		Optional<Location> locationFound = userRepository.getUserLocation(userRepository.getUserByUserName("internalUser32").get());

		// THEN
		assertFalse(locationFound.isEmpty());
	}

	@Disabled
	@Test
	void getUserLocation_NoLocationFound_Test() throws UserNotFoundException {

		//TODO

		// GIVEN
		// WHEN

		// THEN
		fail("not yet implemented");
	}

	@Test
	void getAllCurrentLocations_Ok_Test() {

		//TODO

		// GIVEN
		// WHEN
		Map<UUID, Location> currentLocationsFound = userRepository.getAllCurrentLocations();

		// THEN
		assertEquals(100, currentLocationsFound.size());
	}

	@Disabled
	@Test
	void getUserRewards_Ok_Test() {

		//TODO

		// GIVEN
		// WHEN

		// THEN
		fail("not yet implemented");
	}

	@Disabled
	@Test
	void getUserRewards_UserNotFound_Test() {

		//TODO

		// GIVEN
		// WHEN

		// THEN
		fail("not yet implemented");
	}

	@Disabled
	@Test
	void getUserTripDeals_Ok_Test() {

		//TODO

		// GIVEN
		// WHEN

		// THEN
		fail("not yet implemented");
	}

	@Disabled
	@Test
	void getUserTripDeals_UserNotFound_Test() {

		//TODO

		// GIVEN
		// WHEN

		// THEN
		fail("not yet implemented");
	}

}
