package net.osmand.router;

import net.osmand.GPXUtilities;
import net.osmand.GPXUtilities.GPXExtensionsWriter;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.GPXUtilities.RouteSegment;
import net.osmand.GPXUtilities.RouteType;
import net.osmand.GPXUtilities.Track;
import net.osmand.GPXUtilities.TrkSegment;
import net.osmand.GPXUtilities.WptPt;
import net.osmand.Location;
import net.osmand.binary.BinaryMapRouteReaderAdapter.RouteTypeRule;
import net.osmand.binary.RouteDataBundle;
import net.osmand.binary.StringBundle;
import net.osmand.binary.StringBundleWriter;
import net.osmand.binary.StringBundleXmlWriter;
import net.osmand.util.Algorithms;

import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteExporter {

	public static final String OSMAND_ROUTER_V2 = "OsmAndRouterV2";

	private String name;
	private List<RouteSegmentResult> route;
	private List<Location> locations;
	private List<WptPt> points;

	public RouteExporter(String name, List<RouteSegmentResult> route, List<Location> locations, List<WptPt> points) {
		this.name = name;
		this.route = route;
		this.locations = locations;
		this.points = points;
	}

	public GPXFile exportRoute() {
		RouteDataResources resources = new RouteDataResources(locations);
		List<StringBundle> routeItems = new ArrayList<>();
		if (!Algorithms.isEmpty(route)) {
			for (RouteSegmentResult sr : route) {
				sr.collectTypes(resources);
			}
			for (RouteSegmentResult sr : route) {
				sr.collectNames(resources);
			}

			for (RouteSegmentResult sr : route) {
				RouteDataBundle itemBundle = new RouteDataBundle(resources);
				sr.writeToBundle(itemBundle);
				routeItems.add(itemBundle);
			}
		}
		List<StringBundle> typeList = new ArrayList<>();
		Map<RouteTypeRule, Integer> rules = resources.getRules();
		for (RouteTypeRule rule : rules.keySet()) {
			RouteDataBundle typeBundle = new RouteDataBundle(resources);
			rule.writeToBundle(typeBundle);
			typeList.add(typeBundle);
		}

		GPXFile gpx = new GPXFile(OSMAND_ROUTER_V2);
		Track track = new Track();
		track.name = name;
		gpx.tracks.add(track);
		TrkSegment trkSegment = new TrkSegment();
		track.segments.add(trkSegment);

		if (locations == null || locations.isEmpty()) {
			return gpx;
		}
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			WptPt pt = new WptPt();
			pt.lat = loc.getLatitude();
			pt.lon = loc.getLongitude();
			if (loc.hasSpeed()) {
				pt.speed = loc.getSpeed();
			}
			if (loc.hasAltitude()) {
				pt.ele = loc.getAltitude();
			}
			if (loc.hasAccuracy()) {
				pt.hdop = loc.getAccuracy();
			}
			trkSegment.points.add(pt);
		}
		if (points != null) {
			for (WptPt pt : points) {
				gpx.addPoint(pt);
			}
		}

		List<RouteSegment> routeSegments = new ArrayList<>();
		for (StringBundle item : routeItems) {
			routeSegments.add(RouteSegment.fromStringBundle(item));
		}
		gpx.routeSegments = routeSegments;
		List<RouteType> routeTypes = new ArrayList<>();
		for (StringBundle item : typeList) {
			routeTypes.add(RouteType.fromStringBundle(item));
		}
		gpx.routeTypes = routeTypes;

		return gpx;
	}
}
