package tourGuide.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("testFalse")
@AutoConfigureMockMvc
class ITTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserServiceImpl userService;

	@BeforeAll
	static void setUp() {
		Locale.setDefault(Locale.US);
	}

	@Test
	void index_Test() throws Exception {

		MvcResult result = mockMvc.perform(get("/"))
				.andDo(print())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Greetings from TourGuide!"));
	}

	@Test
	void addUser_Test() throws Exception {

		JSONObject user = new JSONObject();
		user.put("userName", "userName1");
		user.put("phoneNumber", "001");
		user.put("emailAddress", "userName1@tourguide.com");
		String jsonUser = user.toString();

		// User added a first time
		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andDo(print())
				.andExpect(status().isCreated());

		User userFound = userService.getUserByUserName("userName1").get();

		assertEquals("001", userFound.getPhoneNumber());

		// Trying to add the same user a second time
		MvcResult result = mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("already exists"));
	}

	@Test
	void getLocation_Test() throws Exception {

		JSONObject user = new JSONObject();
		user.put("userName", "userName2");
		user.put("phoneNumber", "002");
		user.put("emailAddress", "userName2@tourguide.com");
		String jsonUser = user.toString();

		// Adding user
		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andDo(print());

		User userFound = userService.getUserByUserName("userName2").get();

		assertTrue(userFound.getVisitedLocations().isEmpty());

		// Getting his location
		mockMvc.perform(get("/getLocation")
						.param("userName", "userName2"))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andReturn();

		userFound = userService.getUserByUserName("userName2").get();

		assertFalse(userFound.getVisitedLocations().isEmpty());

	}

	@Test
	void getNearbyAttraction_Test() throws Exception {

		JSONObject user = new JSONObject();
		user.put("userName", "userName3");
		user.put("phoneNumber", "003");
		user.put("emailAddress", "userName3@tourguide.com");
		String jsonUser = user.toString();

		// Adding the user
		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andReturn();

		// Getting the nearby attractions
		MvcResult result = mockMvc.perform(get("/getNearbyAttractions")
						.param("userName", "userName3"))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andReturn();

		ObjectMapper mapper = new ObjectMapper();
		List<NearByAttractionDto> attractions = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, NearByAttractionDto.class));

		assertEquals(5, attractions.size());
	}

	@Test
	void getTripDeals_Test() throws Exception {

		JSONObject user = new JSONObject();
		user.put("userName", "userName5");
		user.put("phoneNumber", "005");
		user.put("emailAddress", "userName5@tourguide.com");
		String jsonUser = user.toString();

		// Adding the user
		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andReturn();

		// Getting the tripDeals
		JSONObject prefDeals = new JSONObject();
		prefDeals.put("userName", "userName5");
		prefDeals.put("numberOfAdults", "2");
		prefDeals.put("numberOfChildren", "1");
		prefDeals.put("tripDuration", "7");
		prefDeals.put("lowerPricePoint", "0");
		prefDeals.put("higherPricePoint", "10000000000");
		String jsonPrefDeals = prefDeals.toString();

		MvcResult result = mockMvc.perform(get("/getTripDeals")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonPrefDeals)
				)
				.andExpect(status().isAccepted())
				.andReturn();

		User userFound = userService.getUserByUserName("userName5").get();

		assertEquals(5, userFound.getTripDeals().size());

	}

	@Test
	void trackAllUsersLocation_Test() throws Exception {

		// Adding two users
		JSONObject user10 = new JSONObject();
		user10.put("userName", "userName10");
		user10.put("phoneNumber", "0010");
		user10.put("emailAddress", "userName10@tourguide.com");
		String jsonUser10 = user10.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser10)
				)
				.andReturn();

		JSONObject user11 = new JSONObject();
		user11.put("userName", "userName11");
		user11.put("phoneNumber", "0011");
		user11.put("emailAddress", "userName11@tourguide.com");
		String jsonUser11 = user11.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser11)
				)
				.andReturn();

		// Checking
		MvcResult result = mockMvc.perform(get("/trackAllUsersLocation"))
				.andReturn();

		//			TypeReference<HashMap<UUID, Location>> typeReference = new TypeReference<HashMap<UUID, Location>>() {};
		//			ObjectMapper mapper = new ObjectMapper();
		//			Map<UUID, Location> locations = mapper.readValue(result.getResponse().getContentAsString(), typeReference);

		// TODO : à améliorer

		String resultAsString = result.getResponse().getContentAsString();
		User user10Object = userService.getUserByUserName("userName10").get();
		User user11Object = userService.getUserByUserName("userName11").get();

		int numberOfTimes = StringUtils.countOccurrencesOf(resultAsString, "latitude");


		assertTrue(numberOfTimes >= 2);
		assertTrue(resultAsString.contains(user10Object.getUserId().toString()));
		assertTrue(resultAsString.contains(user11Object.getUserId().toString()));
	}


	@Test
	void getAllCurrentLocations_Test() throws Exception {

		// Adding three users
		JSONObject user12 = new JSONObject();
		user12.put("userName", "userName12");
		user12.put("phoneNumber", "0012");
		user12.put("emailAddress", "userName12@tourguide.com");
		String jsonUser12 = user12.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser12)
				)
				.andReturn();

		JSONObject user13 = new JSONObject();
		user13.put("userName", "userName13");
		user13.put("phoneNumber", "0011");
		user13.put("emailAddress", "userName11@tourguide.com");
		String jsonUser13 = user13.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser13)
				)
				.andReturn();

		JSONObject user14 = new JSONObject();
		user14.put("userName", "userName14");
		user14.put("phoneNumber", "0014");
		user14.put("emailAddress", "userName14@tourguide.com");
		String jsonUser14 = user14.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser14)
				)
				.andReturn();

		// Checking
		MvcResult result = mockMvc.perform(get("/getAllCurrentLocations"))
				.andReturn();

		System.out.println("*********** " + result.getResponse().getContentAsString());

		String resultAsString = result.getResponse().getContentAsString();

		int numberOfTimes = StringUtils.countOccurrencesOf(resultAsString, "latitude");
		User user12Object = userService.getUserByUserName("userName12").get();
		User user13Object = userService.getUserByUserName("userName13").get();
		User user14Object = userService.getUserByUserName("userName14").get();

		assertTrue(numberOfTimes >= 3);
		assertTrue(resultAsString.contains(user12Object.getUserId().toString()));
		assertTrue(resultAsString.contains(user13Object.getUserId().toString()));
		assertTrue(resultAsString.contains(user14Object.getUserId().toString()));
	}


}
