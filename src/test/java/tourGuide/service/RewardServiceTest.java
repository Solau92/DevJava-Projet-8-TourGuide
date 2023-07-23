package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class RewardServiceTest {

	// TODO

	@InjectMocks
	private RewardsServiceImpl rewardsService;

	@Mock
	private GpsUtil gpsUtil;

	@Mock
	private GpsRepositoryImpl gpsRepository;

	@Mock
	RewardCentral rewardCentral;

	@Test
	void calculateRewards_Ok_Test() {

		// TODO : à revoir... pourquoi une seule ??
		List<Attraction> attractions;

		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 50.0, 60.0);
		Attraction attraction2 = new Attraction("attraction2", "city2", "state2", 100.0, 110.0);
		Attraction attraction3 = new Attraction("attraction3", "city3", "state3", 60.0, 70.0);
		Attraction attraction4 = new Attraction("attraction4", "city4", "state4", -65.0, -77.0);
		Attraction attraction5 = new Attraction("attraction5", "city5", "state5", 70.0, 80.0);
		Attraction attraction6 = new Attraction("attraction6", "city6", "state6", 0.0, 0.0);
		attractions = new ArrayList<>();
		attractions.add(attraction1);
		attractions.add(attraction2);
		attractions.add(attraction3);
		attractions.add(attraction4);
		attractions.add(attraction5);
		attractions.add(attraction6);

		Location location1 = new Location(50.0, 60.0);
		Location location2 = new Location(100.0, 110.0);
		Location location3 = new Location(-65.0, -77.0);
		Location location4 = new Location(0.0, 0.0);

		User user1 = new User(UUID.randomUUID(), "userName1", "phoneNumber1", "emailAddress1");
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), location1, new Date()));
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), location2, new Date()));
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), location3, new Date()));
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), location4, new Date()));

		when(gpsRepository.getAllAttractions()).thenReturn(attractions);

		rewardsService.calculateRewards(user1);

		assertEquals(6, attractions.size(), "attractions.size()");
		assertEquals(4, user1.getVisitedLocations().size(), "user1.getVisitedLocations().size()");

		assertNotEquals(0, user1.getUserRewards().size(), "user1.getUserRewards().size()");
		assertEquals(4, user1.getUserRewards().size(), "user1.getUserRewards().size()");

	}


}
