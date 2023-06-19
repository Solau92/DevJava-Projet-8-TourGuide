//package tourGuide.service;
//
//import gpsUtil.GpsUtil;
//import gpsUtil.location.VisitedLocation;
//import org.springframework.stereotype.Service;
//import tourGuide.tracker.Tracker;
//import tourGuide.user.User;
//
//@Service
//public class UserService {
//
//	private final GpsUtil gpsUtil;
//
////	public final Tracker tracker;
//
//	public UserService(GpsUtil gpsUtil) {
//		this.gpsUtil = gpsUtil;
////		this.tracker = new Tracker();
//	}
//
//	public VisitedLocation getUserLocation(User user) {
//		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
//				user.getLastVisitedLocation() :
//				trackUserLocation(user);
//		return visitedLocation;
//	}
//}
