package tourGuide.controller;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest
public class TourGuideControllerTest {

	@InjectMocks
	private TourGuideController tourGuideController;
	@Mock
	private TourGuideServiceImpl tourGuideService;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private UserRepositoryImpl userRepository;
	@Mock
	private GpsServiceImpl gpsService;
	@Mock
	private GpsRepositoryImpl gpsRepository;

	List<Attraction> attractions;
	List<NearByAttractionDto> attractionsDto = new ArrayList<>();
	User user1;

	Map<UUID, Location> currentLocations;

	void setUpAttractions() {

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

		NearByAttractionDto attractionDto1 = new NearByAttractionDto();
		attractionDto1.setAttractionName(attraction1.attractionName);
		NearByAttractionDto attractionDto2 = new NearByAttractionDto();
		attractionDto2.setAttractionName(attraction2.attractionName);
		NearByAttractionDto attractionDto3 = new NearByAttractionDto();
		attractionDto3.setAttractionName(attraction3.attractionName);
		NearByAttractionDto attractionDto4 = new NearByAttractionDto();
		attractionDto4.setAttractionName(attraction4.attractionName);
		NearByAttractionDto attractionDto5 = new NearByAttractionDto();
		attractionDto5.setAttractionName(attraction5.attractionName);
		attractionsDto.add(attractionDto1);
		attractionsDto.add(attractionDto2);
		attractionsDto.add(attractionDto3);
		attractionsDto.add(attractionDto4);
		attractionsDto.add(attractionDto5);
	}

	void setUpUsers() {

		user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		Location location1 = new Location(0.5, 0.6);
		VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), location1, new Date());
		user1.addToVisitedLocations(visitedLocation1);

		User user2 = new User(UUID.randomUUID(), "userName2", "phoneNumber2", "emailAddress2");
		Location location2 = new Location(0.6, 0.7);
		VisitedLocation visitedLocation2 = new VisitedLocation(user2.getUserId(), location2, new Date());
		user2.addToVisitedLocations(visitedLocation2);

		User user3 = new User(UUID.randomUUID(), "userName3", "phoneNumber3", "emailAddress3");
		Location location3 = new Location(0.7, 0.8);
		VisitedLocation visitedLocation3 = new VisitedLocation(user3.getUserId(), location3, new Date());
		user3.addToVisitedLocations(visitedLocation1);

		currentLocations = new HashMap<>();
		currentLocations.put(user1.getUserId(), location1);
		currentLocations.put(user2.getUserId(), location2);
		currentLocations.put(user3.getUserId(), location3);
	}

	@Test
	void index_Ok_Test() {

		// GIVEN
		// WHEN
		String result = tourGuideController.index();

		// THEN
		assertEquals("Greetings from TourGuide!", result);
	}

	@Test
	void getLocation_Ok_Test() throws UserNotFoundException {

		User user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		Location location1 = new Location(0.50, 0.60);

		// GIVEN
		when(userService.getUserByUserName(anyString())).thenReturn(Optional.of(user1));
		when(userRepository.getUserLocation(any(User.class))).thenReturn(Optional.of(location1));
		when(userService.getUserLocation(anyString())).thenReturn(Optional.of(location1));
		when(tourGuideService.getUserLocation(anyString())).thenReturn(location1);

		// WHEN
		ResponseEntity<Location> responseResult = tourGuideController.getLocation("userName1");
		Location locationResult = responseResult.getBody();
		HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		assertEquals(location1, locationResult);
		assertEquals(ACCEPTED, statusResponse);

	}

	@Test
	void getNearbyAtttractions_Ok_Test() throws UserNotFoundException {

		Location location1 = new Location(0.50, 0.60);

		setUpAttractions();

		// GIVEN
		when(tourGuideService.getUserLocation(anyString())).thenReturn(location1);
		when(gpsRepository.getAllAttractions()).thenReturn(attractions);
		when(gpsService.getNearbyAttractions(anyString())).thenReturn(attractionsDto);

		// WHEN
		ResponseEntity<List<NearByAttractionDto>> responseResult = tourGuideController.getNearbyAttractions("userName1");
		List<NearByAttractionDto> listResult = responseResult.getBody();
		HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		assertEquals(5, listResult.size());
		assertTrue(listResult.contains(attractionsDto.get(1)));
		assertEquals(ACCEPTED, statusResponse);
	}

	@Test
	void getAllAttractions_Ok_Test() {

		// GIVEN
		setUpAttractions();
		when(gpsRepository.getAllAttractions()).thenReturn(attractions);
		when(gpsService.getAllAttractions()).thenReturn(attractions);

		// WHEN
		ResponseEntity<List<Attraction>> responseResult = tourGuideController.getAllAttractions();
		List<Attraction> listResult = responseResult.getBody();
		HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		assertEquals(6, listResult.size());
		assertTrue(listResult.contains(attractions.get(1)));
		assertEquals(ACCEPTED, statusResponse);
	}

	@Test
	void getAllCurrentLocations_Ok_Test() {

		// GIVEN
		setUpUsers();
		when(userRepository.getAllCurrentLocations()).thenReturn(currentLocations);
		when(userService.getAllCurrentLocations()).thenReturn(currentLocations);

		// WHEN
		ResponseEntity<Map<UUID, Location>> responseResult = tourGuideController.getAllCurrentLocations();
		Map<UUID, Location> listResult = responseResult.getBody();
		HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		assertEquals(3, listResult.size());
		assertEquals(0.5, listResult.get(user1.getUserId()).latitude);
		assertEquals(ACCEPTED, statusResponse);
	}

	@Disabled
	@Test
	void getRewards_Ok_Test() {

		// TODO

		// GIVEN

		// WHEN
		//	ResponseEntity<----> responseResult = tourGuideController.get
		//	---- Result = responseResult.getBody();
		//	HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		//	assertEquals(ACCEPTED, statusResponse);
		fail("not yet implemented");

	}

	@Disabled
	@Test
	void getTripDeals_Ok_Test() {

		// TODO

		// GIVEN

		// WHEN
		//	ResponseEntity<----> responseResult = tourGuideController.get
		//	---- Result = responseResult.getBody();
		//	HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		//	assertEquals(ACCEPTED, statusResponse);
		fail("not yet implemented");
	}

	@Test
	void addUser_Ok_Test() throws UserAlreadyExistsException {

		// GIVEN
		setUpUsers();
		when(userRepository.addUser(any(User.class))).thenReturn(Optional.of(user1));
		when(userService.addUser(any(User.class))).thenReturn(Optional.of(user1));

		// WHEN
		ResponseEntity<User> responseResult = tourGuideController.addUser(user1);
		User userCreated = responseResult.getBody();
		HttpStatusCode statusResponse = responseResult.getStatusCode();

		// THEN
		assertEquals(CREATED, statusResponse);
		assertEquals(user1.getUserId(), userCreated.getUserId());
	}


}
