package com.bedetaxi.bedetaxi.Routing;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

/**
 * Generic class to get a route between a start and a destination point, 
 * going through a list of waypoints. 
 * @see MapQuestRoadManager
 * @see GoogleRoadManager
 * @see OSRMRoadManager
 *
 * @author M.Kergall
 */
public abstract class RoadManager {

  protected String mOptions;

	/**
	 * @param waypoints
	 * @return the road found.
	 * In case of error, road status is set to error, and the shape has just straight lines between waypoints.
	 */
	public abstract Road getRoad(ArrayList<GeoPoint> waypoints);

	/**
	 * @param waypoints
	 * @return the list of roads found.
	 * Road at index 0 is the shortest (in time).
	 * The array may contain more entries, for alternate routes - assuming the routing service used supports alternate routes.
	 * In case of error, return 1 road with its status set to error, and its shape with just straight lines between waypoints.
	 */
	public abstract Road[] getRoads(ArrayList<GeoPoint> waypoints);

	public RoadManager() {
		mOptions = "";
	}
	
	/**
	 * Add an option that will be used in the route request. 
	 * Note that some options are set in the request in all cases. 
	 * @param requestOption see provider documentation. 
	 * Just one example: "routeType=bicycle" for MapQuest; "mode=bicycling" for Google. 
	 */
	public void addRequestOption(String requestOption){
		mOptions += "&" + requestOption;
	}
	
	/**
	 * @return the GeoPoint as a string, properly formatted: lat,lon
	 */
	protected String geoPointAsString(GeoPoint p){
		StringBuilder result = new StringBuilder();
		double d = p.getLatitude();
		result.append(Double.toString(d));
		d = p.getLongitude();
		result.append(",");
		result.append(Double.toString(d));
		return result.toString();
	}
	
	/**
	 * Using the road high definition shape, builds and returns a Polyline. 
	 * @param road
	 * @param color Android Color. Setting some transparency is highly recommended.
	 * @param width in pixels.
	 */
	public static Polyline buildRoadOverlay(Road road, int color, float width){
		Polyline roadOverlay = new Polyline();
		roadOverlay.setColor(color);
		roadOverlay.setWidth(width);
		if (road != null) {
			ArrayList<GeoPoint> polyline = road.mRouteHigh;
			roadOverlay.setPoints(polyline);
		}
		return roadOverlay;
	}
	
	/**
	 * Builds an overlay for the road shape with a default (and nice!) style. 
	 * @return route shape overlay
	 */
	public static Polyline buildRoadOverlay(Road road){
		return buildRoadOverlay(road, 0x800000FF, 5.0f);
	}

}
