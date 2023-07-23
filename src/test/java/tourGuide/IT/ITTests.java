package tourGuide.IT;

import gpsUtil.location.Location;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ITTests {

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
//
//
//	@Test
//	void addUser_Test() throws Exception {
//
//		JSONObject user = new JSONObject();
//		user.put("userName", "userName1");
//		user.put("phoneNumber", "001");
//		user.put("emailAddress", "userName1@tourguide.com");
//		String jsonUser = user.toString();
//
//		mockMvc.perform(post("/addUser")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonUser)
//				)
//				.andDo(print())
//		/*.andExpect(status().isCreated())*/;
//
//		User userFound = userService.getUserByUserName("userName1").get();
//
//		assertEquals("001", userFound.getPhoneNumber());
//	}
//
//
//	@Test
//	void getLocation_Test() throws Exception {
//
//		JSONObject user = new JSONObject();
//		user.put("userName", "userName2");
//		user.put("phoneNumber", "002");
//		user.put("emailAddress", "userName2@tourguide.com");
//		String jsonUser = user.toString();
//
//		mockMvc.perform(post("/addUser")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonUser)
//				)
//				.andDo(print());
//
//		mockMvc.perform(get("/getLocation")
//						.param("userName", "userName2"))
//				.andDo(print())
//				.andExpect(status().isAccepted())
//				.andReturn();
//
//		User userFound = userService.getUserByUserName("userName2").get();
//
//		assertFalse(userFound.getVisitedLocations().isEmpty());
//
//	}
//
	@Test
	void getNearbyAtttraction_Test() throws Exception {

		JSONObject user = new JSONObject();
		user.put("userName", "userName3");
		user.put("phoneNumber", "003");
		user.put("emailAddress", "userName3@tourguide.com");
		String jsonUser = user.toString();

		mockMvc.perform(post("/addUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUser)
				)
				.andDo(print());

		mockMvc.perform(get("/getLocation")
						.param("userName", "userName3"))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andReturn();

		MvcResult result = mockMvc.perform(get("/getNearbyAttractions")
						.param("userName", "userName3"))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andExpect(content().json("{'name:toto'}"))
				.andReturn();

		//result.getResponse().getContentAsString(). //retransformer avec ObjectMapper en objet

		System.out.println(result.getResponse().getContentAsString());
	}
//
//	@Test
//	void getTripDeals_Test() throws Exception {
//
//		JSONObject user = new JSONObject();
//		user.put("userName", "userName5");
//		user.put("phoneNumber", "005");
//		user.put("emailAddress", "userName5@tourguide.com");
//		String jsonUser = user.toString();
//
//		mockMvc.perform(post("/addUser")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonUser)
//				)
//				.andReturn();
////				.andDo(print());
//
//		JSONObject prefDeals = new JSONObject();
//		prefDeals.put("userName", "userName5");
//		prefDeals.put("numberOfAdults", "2");
//		prefDeals.put("numberOfChildren", "1");
//		prefDeals.put("tripDuration", "7");
//		String jsonPrefDeals = prefDeals.toString();
//
//		MvcResult result = mockMvc.perform(get("/getTripDeals")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonPrefDeals)
//				)
////				.andDo(print())
//				/*.andExpect(status().isAccepted())*/
//				.andReturn();
//
//				System.out.println(result.toString());
//
//		User userFound = userService.getUserByUserName("userName5").get();
//
//		assertEquals(5, userFound.getTripDeals().size());
//
//	}
//
//	@Test
//	void trackAllUsersLocation_Test() throws Exception {
//
//		JSONObject user10 = new JSONObject();
//		user10.put("userName", "userName10");
//		user10.put("phoneNumber", "0010");
//		user10.put("emailAddress", "userName10@tourguide.com");
//		String jsonUser10 = user10.toString();
//
//		mockMvc.perform(post("/addUser")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonUser10)
//				)
//				.andReturn();
//
//		JSONObject user11 = new JSONObject();
//		user10.put("userName", "userName11");
//		user10.put("phoneNumber", "0011");
//		user10.put("emailAddress", "userName11@tourguide.com");
//		String jsonUser11 = user10.toString();
//
//		mockMvc.perform(post("/addUser")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonUser11)
//				)
//				.andReturn();
//
//		MvcResult result = mockMvc.perform(get("/trackAllUsersLocation"))
//				.andReturn();
//
//
//	}
//
//	@Test
//	void getAllCurrentLocations_Test(){
//
//
//	}


}
