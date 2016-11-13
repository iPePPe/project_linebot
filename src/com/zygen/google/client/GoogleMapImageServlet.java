package com.zygen.google.client;

import static net.sf.sprockets.google.Places.Response.STATUS_OK;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.PlacesParams;
import net.sf.sprockets.google.Place.Photo;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;

/**
 * Servlet implementation class GoogleMapImageServlet
 */
public class GoogleMapImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GoogleMapImageServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String placeId = request.getParameter("placeId");
		if (placeId != null) {
			
			PlacesParams params = Params.create();
			//params.placeId(placeId);
			//Response<List<Place>> search = Places.radarSearch(params);
			Response<Place> search = Places.details(params.placeId(placeId));
			
			String status = search.getStatus();
			response.setContentType("image/jpg");
			if (STATUS_OK.equals(status)) {

				
				Place place = search.getResult();
					List<Photo> photos = place.getPhotos();
					if (!photos.isEmpty()) {
						Photo photo = photos.get(0);
						params.clear();
						Response<InputStream> resp = Places
								.photo(params.reference(photo.getReference()).maxWidth(800).maxHeight(600));
						InputStream in = resp.getResult();
						BufferedImage bi = ImageIO.read(in);
						OutputStream out = response.getOutputStream();
						ImageIO.write(bi, "jpg", out);
						out.close();
						in.close();
					}else{
						InputStream is = null;
						URL iconUrl = new URL(place.getIcon());
						is = iconUrl.openStream();
						BufferedImage bi = ImageIO.read(is);
				        OutputStream out = response.getOutputStream();
						ImageIO.write(bi, "jpg", out);
						out.close();
						is.close();
					}
				//}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
