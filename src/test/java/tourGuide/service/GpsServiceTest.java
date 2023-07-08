package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Attr;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class GpsServiceTest {

	List<Attraction> attractions;
	User user1;
	Location location1;
	@InjectMocks
	private GpsServiceImpl gpsService;
	@Mock
	private GpsRepositoryImpl gpsRepository;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private UserRepositoryImpl userRepository;
	@Mock
	private TourGuideServiceImpl tourGuideService;
	@Mock
	private RewardsServiceImpl rewardsService;

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
		when(tourGuideService.getUserLocation(anyString())).thenReturn(location1);
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
		when(tourGuideService.getUserLocation(anyString())).thenThrow(UserNotFoundException.class);

		// WHEN
		// THEN
		assertThrows(UserNotFoundException.class, () -> gpsService.getNearbyAttractions("userName1"));
	}

}
