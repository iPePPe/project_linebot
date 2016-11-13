package com.zygen.google.client;


import static net.sf.sprockets.google.Places.Response.STATUS_OK;
import static net.sf.sprockets.google.Places.Response.STATUS_ZERO_RESULTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Place.Photo;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import net.sf.sprockets.google.PlacesParams;

import org.junit.Test;

public class PlacesTest {
	public static void main(String[] args) throws IOException {
        System.out.println("Hello World!"); // Display the string.
        Response<List<Place>> resp = Places.nearbySearch(
				Params.create().latitude(13.909487).longitude(100.588296).keyword("ตำจังโก้"));
        
        String status = resp.getStatus();
        List<Place> places = resp.getResult();
        
        if (STATUS_OK.equals(status)) {
            for (Place place : places) {
            	Response<Place> details = Places.details(Params.create().placeId(place.getPlaceId().getId()));
                System.out.println(place.getName() + " @ " + place.getVicinity());
                System.out.println(details.getResult().getUrl());
            }
        } else if (STATUS_ZERO_RESULTS.equals(status)) {
            System.out.println("no results");
        } else {
            System.out.println("error: " + status);
        }
    }
	@Test
	public void testNearbySearch() throws IOException {
		testSearch(Places.nearbySearch(
				Params.create().latitude(40.758897).longitude(-73.985126).keyword("pizza")));
	}

	@Test
	public void testTextSearch() throws IOException {
		testSearch(Places.textSearch(Params.create().query("pizza near times square")));
	}

	@Test
	public void testRadarSearch() throws IOException {
		testSearch(Places.radarSearch(
				Params.create().latitude(40.758897).longitude(-73.985126).keyword("pizza")));
	}

	@Test
	public void testAutocomplete() throws IOException {
		testAutocomplete(Places.autocomplete(
				Params.create().latitude(40.758897).longitude(-73.985126).query("john's piz")));
	}

	@Test
	public void testQueryAutocomplete() throws IOException {
		testAutocomplete(Places.queryAutocomplete(
				Params.create().latitude(40.758897).longitude(-73.985126).query("pizza near tim")));
	}

	private void testSearch(Response<List<Place>> resp) {
		testResponse(resp);
		assertTrue(!resp.getResult().get(0).getPlaceId().getId().isEmpty());
	}

	private void testAutocomplete(Response<List<Prediction>> resp) {
		testResponse(resp);
		assertTrue(!resp.getResult().get(0).getDescription().isEmpty());
	}

	private void testResponse(Response<? extends List<?>> resp) {
		assertEquals(STATUS_OK, resp.getStatus());
		assertTrue(!resp.getResult().isEmpty());
	}

	@Test
	public void testDetails() throws IOException {
		PlacesParams params = Params.create();
		Response<List<Place>> search = Places.textSearch(params.query("pizza near times square"));
		testSearch(search);
		Place place = search.getResult().get(0);
		params.clear();
		Response<Place> details = Places.details(params.placeId(place.getPlaceId().getId()));
		assertEquals(STATUS_OK, details.getStatus());
		place = details.getResult();
		assertNotNull(place);
		assertTrue(!place.getPlaceId().getId().isEmpty());
	}

	@Test
	public void testPhoto() throws IOException {
		PlacesParams params = Params.create();
		Response<List<Place>> search = Places.textSearch(params.query("pizza near times square"));
		testSearch(search);
		boolean tested = false;
		for (Place place : search.getResult()) {
			List<Photo> photos = place.getPhotos();
			if (!photos.isEmpty()) {
				Photo photo = photos.get(0);
				assertTrue(!photo.getReference().isEmpty());
				params.clear();
				Response<InputStream> resp = Places.photo(
						params.reference(photo.getReference()).maxWidth(100).maxHeight(75));
				assertEquals(STATUS_OK, resp.getStatus());
				InputStream in = resp.getResult();
				assertNotNull(in);
				byte[] b = new byte[8192];
				while (in.read(b) != -1) {
				}
				in.close();
				tested = true;
				break;
			}
		}
		assertTrue(tested);
	}
}