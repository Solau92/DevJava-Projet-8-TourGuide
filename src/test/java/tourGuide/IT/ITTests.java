package tourGuide.IT;

import gpsUtil.location.Location;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ITTests {

	@Autowired
	private MockMvc mockMvc;

	private UserServiceImpl userService;

	@Test
	void index() throws Exception {

		MvcResult result = mockMvc.perform(get("/"))
				.andDo(print())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Greetings from TourGuide!"));
	}

	@Disabled
	@Test
	void addUser() throws Exception {

		mockMvc.perform(post("/addUser")

//				.param("userName", "userName1")
//				.param("phoneNumber", "001")
//				.param("emailAddress", "userName1@tourguide.com")
		)
				.andDo(print())
				.andExpect(status().isCreated());

		User userFound = userService.getUserByUserName("userName1").get();

		assertEquals("001", userFound.getPhoneNumber());
	}




	//	@Test
//	void getLocation() throws Exception {
//
//		MvcResult result = mockMvc.perform(get("/getLocation")
//				.param("userName", "internalUser1")				)
//				.andDo(print())
//				.andExpect(status().isAccepted())
//				.andReturn();
//
//		assertFalse(user1.getVisitedLocations().isEmpty());
//
//	}


//	@RequestMapping("/getLocation")
//	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
//		logger.info("/getLocation for user " + userName);
//		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getUserLocation(userName));
//	}


}
