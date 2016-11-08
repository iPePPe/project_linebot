package com.zygen.google.client;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sprockets.google.StreetView;
import net.sf.sprockets.google.StreetView.Params;
import net.sf.sprockets.google.StreetView.Response;

import org.junit.Test;

public class StreetViewTest {
	@Test
	public void testImage() throws IOException {
		Response<InputStream> image = StreetView.image(
				Params.create().location("18 Rue Cujas, Paris, France"));
		assertEquals(HTTP_OK, image.getStatus());
		
		InputStream in = image.getResult();
		assertNotNull(in);
		byte[] b = new byte[8192];
		while (in.read(b) != -1) {
		}
		in.close();
	}
}