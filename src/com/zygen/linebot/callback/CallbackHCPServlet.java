package com.zygen.linebot.callback;

import static net.sf.sprockets.google.Places.Response.STATUS_OK;
import static net.sf.sprockets.google.Places.Response.STATUS_ZERO_RESULTS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;

//import javax.naming.InitialContext;
//import javax.naming.NamingException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.ByteStreams;

import com.zygen.linebot.model.event.message.AudioMessageContent;
import com.zygen.linebot.model.event.message.ImageMessageContent;
import com.zygen.linebot.model.event.message.LocationMessageContent;
import com.zygen.linebot.model.event.message.StickerMessageContent;
import com.zygen.linebot.model.event.message.MessageContent;
import com.sap.cloud.account.TenantContext;
import com.zygen.linebot.model.event.Event;
import com.zygen.linebot.model.event.FollowEvent;
import com.zygen.linebot.model.event.JoinEvent;
import com.zygen.linebot.model.event.LeaveEvent;
import com.zygen.linebot.model.event.MessageEvent;
import com.zygen.linebot.model.event.PostbackEvent;
import com.zygen.linebot.model.event.UnfollowEvent;
import com.zygen.linebot.model.event.UnknownEvent;
import com.zygen.linebot.model.event.message.TextMessageContent;
import com.zygen.linebot.model.event.message.VideoMessageContent;
import com.zygen.linebot.model.event.postback.PostbackContent;
import com.zygen.linebot.model.message.Message;
import com.zygen.linebot.model.message.TemplateMessage;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.message.template.ButtonsTemplate;
import com.zygen.linebot.model.message.template.Template;
import com.zygen.linebot.model.response.BotApiResponse;
import com.zygen.odata.client.ODataMessageBuilder;
import com.zygen.odata.model.message.ZtextMessageV2;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;

import com.zygen.linebot.model.PushMessage;
import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.action.Action;
import com.zygen.linebot.model.action.MessageAction;
import com.zygen.linebot.model.action.PostbackAction;
import com.zygen.linebot.model.action.URIAction;
import com.zygen.linebot.model.event.BeaconEvent;
import com.zygen.linebot.model.event.CallbackRequest;
import com.zygen.hcp.jpa.JPAEntityFactoryManager;
import com.zygen.hcp.jpa.NicePlace;
import com.zygen.hcp.jpa.UserProfile;
import com.zygen.hcp.model.UserProfileModel;
import com.zygen.linebot.client.DestinationUtil;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.client.LineSignatureValidator;

import retrofit2.Response;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Servlet implementation class CallBackServletd
 */
public class CallbackHCPServlet extends HttpServlet {
	private static final long serialVersionUID = 102831973239L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackHCPServlet.class);
	private final LineSignatureValidator lineSignatureValidator;
	private final ObjectMapper objectMapper;
	public static final String DATA_SOURCE_NAME = "java:comp/env/jdbc/DefaultDB";
	public static final String PERSISTENCE_UNIT_NAME = "persistence-linebot";
	private static final String lineapi = "line-api";
	private static final DestinationUtil dest = new DestinationUtil(lineapi);
	private static String hcpUrl = "";
	// private InitialContext ctx;

	private EntityManagerFactory emf;
	private ArrayList<NicePlace> nices = new ArrayList<NicePlace>();

	public CallbackHCPServlet() {

		this.lineSignatureValidator = new LineSignatureValidator(
				dest.getChannelSecret().getBytes(StandardCharsets.UTF_8));
		this.objectMapper = buildObjectMapper();

	}

	/** {@inheritDoc} */
	@Override
	public void init() throws ServletException {

		try {

			emf = this.getEntityManagerFactory();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private EntityManagerFactory getEntityManagerFactory() throws Exception {
		try {

			emf = JPAEntityFactoryManager.getEntityManagerFactory();

		} catch (Exception e) {
			throw new Exception(e);
		}
		return emf;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {

			response.getWriter().print("PePPe Nice Place6..." + this.getCurrentTenantId());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// response.getWriter().println("parser");
			hcpUrl = getBaseUrl(request);
			doParser(request, response);
		} catch (Exception e) {
			response.getWriter().println("parser error: " + e.getMessage());
			LOGGER.error("Parser Error", e);
		}
	}

	public static String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme() + "://";
		String serverName = request.getServerName();
		String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		String contextPath = request.getContextPath();
		return scheme + serverName + serverPort + contextPath;
	}

	public void doParser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, CallBackServletException {
		// TODO Auto-generated method stub
		String replyToken = null;
		try {

			String channelId = request.getParameter("ch");
			String signature = request.getHeader("X-Line-Signature");
			if (signature == null || signature.length() == 0) {
				throw new CallBackServletException("Missing 'X-Line-Signature' header");
			}

			final byte[] json = ByteStreams.toByteArray(request.getInputStream());

			if (!lineSignatureValidator.validateSignature(json, signature)) {
				throw new CallBackServletException("Invalid API signature");
			}

			final CallbackRequest callbackRequest = objectMapper.readValue(json, CallbackRequest.class);
			if (callbackRequest == null || callbackRequest.getEvents() == null) {
				throw new CallBackServletException("Invalid content");
			} else {
				final List<Event> events = callbackRequest.getEvents();

				for (Event event : events) {
					try {
						handleEvent(event, channelId);

					} catch (IOException e) {
						// replyLine(new TextMessage("Error"),
						// messageEvent.getReplyToken());
					}
				}

			}
		} catch (Exception e) {
			replyLine(new TextMessage("Service Unavailable"), replyToken);
			response.getWriter().println("Parser error: " + e.getMessage());
			LOGGER.error("Parse error", e);

		}
	}

	public String getCurrentTenantId() throws NamingException {
		String currentTenantId = "";

		try {
			InitialContext ctx = new InitialContext();
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			TenantContext tenantContext = (TenantContext) envCtx.lookup("TenantContext");

			currentTenantId = tenantContext.getTenant().getId();

		} catch (NamingException e) {
			return "NOT_CURRENTLY_RUNNING_MULTI_-_TENANT"; // 36 chars as per
															// real tenant id
		}
		return currentTenantId;
	}

	public void handleEvent(Event event, String ch) throws Exception {

		emf = this.getEntityManagerFactory();

		EntityManager em = emf.createEntityManager();
		UserProfileModel upm = new UserProfileModel();
		UserProfile user = new UserProfile();

		em.getTransaction().begin();
		em.setProperty("me-tenant.id", this.getCurrentTenantId());
		try {

			com.zygen.hcp.jpa.MessageEvent me = new com.zygen.hcp.jpa.MessageEvent();
			// UserProfile user = new UserProfile();

			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				me.setReplyToken(messageEvent.getReplyToken());
				me.setTimestamp(Date.from(messageEvent.getTimestamp()));
				me.setUserId(messageEvent.getSource().getUserId());
				me.setChannel(ch);

				MessageContent message = messageEvent.getMessage();
				if (message instanceof TextMessageContent) {
					TextMessageContent textContent = (TextMessageContent) message;
					me.setMessageId(textContent.getId());
					me.setType(textContent.getType());
					me.setText(textContent.getText());
					/*
					 * if (textContent.getText().equals("Welcome")) {
					 * TemplateMessage welcome = createWelcome("Welcome");
					 * //push((Message)welcome,messageEvent.getSource().
					 * getUserId());
					 * reply((Message)welcome,messageEvent.getReplyToken());
					 * //replyLine(new TextMessage("Wellll"),
					 * messageEvent.getReplyToken()); } else { ZtextMessageV2
					 * ztext = new ZtextMessageV2(ch, textContent.getText(),
					 * messageEvent.getSource().getUserId());
					 * ODataMessageBuilder odata = new
					 * ODataMessageBuilder("ZGFMLGW2HCollection", ztext.getId(),
					 * "HeaderToDetailNav"); List<Message> msg =
					 * odata.getMessage(); if (msg.size() > 0) {
					 * replyMessageLine(msg, messageEvent.getReplyToken()); }
					 * else { replyLine(new TextMessage("I don't know"),
					 * messageEvent.getReplyToken()); } }
					 */
					user = upm.checkCreateUserProfile(em, this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);

					if (user.getLatitude() == 0) {
						TemplateMessage locationTemplate = createLocation("Check-In");
						reply((Message) locationTemplate, messageEvent.getReplyToken());
					} else if (user.getRadius() == 0) {
						TemplateMessage rediusTemplate = createRadius("Raduis");
						reply((Message) rediusTemplate, messageEvent.getReplyToken());
					} else {
						if (textContent.getText().substring(0, 2).equals("#r")
								|| textContent.getText().substring(0, 2).equals("#R")) {
							TemplateMessage rediusTemplate = createRadius("Raduis");
							reply((Message) rediusTemplate, messageEvent.getReplyToken());
						} else {

							reply(nearbySearch(textContent.getText(), user, em), messageEvent.getReplyToken());
						}
					}

					// reply(nearbySearch(textContent.getText(), user),
					// messageEvent.getReplyToken());

				} else if (message instanceof StickerMessageContent) {
					StickerMessageContent stkContent = (StickerMessageContent) message;

					me.setMessageId(stkContent.getId());
					me.setType(stkContent.getType());
					me.setPackageId(stkContent.getPackageId());
					me.setStickerId(stkContent.getStickerId());
					user = upm.checkCreateUserProfile(em, this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);

				} else if (message instanceof LocationMessageContent) {
					LocationMessageContent locationMessage = (LocationMessageContent) message;
					me.setMessageId(locationMessage.getId());
					me.setLatitude(locationMessage.getLatitude());
					me.setLongitude(locationMessage.getLongitude());
					me.setTitle(locationMessage.getTitle());
					me.setAddress(locationMessage.getAddress());
					me.setType(locationMessage.getType());
					user = upm.checkUserProfileLocation(em, event.getSource().getUserId(), this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);
					if (!(user.getLocationTitle() == null)) {
						replyLine(new TextMessage(user.getDisplayName() + "@" + user.getLocationTitle()),
								messageEvent.getReplyToken());
					} else {
						replyLine(new TextMessage("Share location again"), me.getReplyToken());
					}

				} else if (message instanceof AudioMessageContent) {
					AudioMessageContent auContent = (AudioMessageContent) message;
					me.setMessageId(auContent.getId());
					me.setType(auContent.getType());
					me.setUrl(auContent.getUrl());
					user = upm.checkCreateUserProfile(em, this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);

				} else if (message instanceof ImageMessageContent) {
					ImageMessageContent imageContent = (ImageMessageContent) message;
					me.setMessageId(imageContent.getId());
					me.setType(imageContent.getType());
					me.setUrl(imageContent.getUrl());
					user = upm.checkCreateUserProfile(em, this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);

				} else if (message instanceof VideoMessageContent) {
					VideoMessageContent vContent = (VideoMessageContent) message;
					me.setMessageId(vContent.getId());
					me.setType(vContent.getType());
					me.setUrl(vContent.getUrl());
					user = upm.checkCreateUserProfile(em,  this.getCurrentTenantId(),
							dest.getChannelAccessToken(), me);

				}
			} else if (event instanceof UnfollowEvent) {

			} else if (event instanceof FollowEvent) {

			} else if (event instanceof JoinEvent) {
				JoinEvent je = (JoinEvent)event;
				com.zygen.hcp.jpa.MessageEvent jpe = new com.zygen.hcp.jpa.MessageEvent();
				jpe.setReplyToken(je.getReplyToken());
				jpe.setTimestamp(Date.from(je.getTimestamp()));
				jpe.setType("join");
				jpe.setUserId(je.getSource().getUserId());
				user = upm.checkCreateUserProfile(em,  this.getCurrentTenantId(),
						dest.getChannelAccessToken(), jpe);
			} else if (event instanceof LeaveEvent) {

			} else if (event instanceof BeaconEvent) {

			} else if (event instanceof PostbackEvent) {
				PostbackEvent pe = (PostbackEvent) event;
				com.zygen.hcp.jpa.MessageEvent jpe = new com.zygen.hcp.jpa.MessageEvent();

				PostbackContent postback = pe.getPostbackContent();
				String data = postback.getData();
				String[] parts = data.split("-");
				String part1 = parts[0]; // 004
				String part2 = parts[1]; // 034556
				jpe.setReplyToken(pe.getReplyToken());
				jpe.setTimestamp(Date.from(pe.getTimestamp()));
				jpe.setType("postback");
				jpe.setPostbackData(postback.getData());
				jpe.setUserId(pe.getSource().getUserId());
				if (part1.equals("#r")) {

					user = upm.checkCreateUserProfileRaduis(em, pe.getSource().getUserId(), this.getCurrentTenantId(),
							dest.getChannelAccessToken(), Integer.parseInt(part2), jpe);
					replyLine(new TextMessage("Radius = " + part2 + "m"), pe.getReplyToken());
				}
			} else if (event instanceof UnknownEvent) {

			}
			em.persist(user);
			for (NicePlace nice : nices) {
				em.persist(nice);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("DB Error " + e.getMessage());
		} finally {
			em.close();
		}
	}

	private String nearbySearch(String keyword, UserProfile user, int from, int to) {
		// TODO Auto-generated method stub
		net.sf.sprockets.google.Places.Response<List<Place>> response;
		String result = "";
		try {
			response = Places.nearbySearch(Params.create().latitude(user.getLatitude()).longitude(user.getLongitude())
					.radius(user.getRadius()).keyword(keyword));

			String status = response.getStatus();
			List<Place> places = response.getResult();

			if (STATUS_OK.equals(status)) {
				for (int i = from; i <= to; i++) {

				}
				for (Place place : places) {
					// System.out.println(place.getName() + " @ " +
					// place.getVicinity()+"\n");
					result += place.getName() + " @ " + place.getVicinity() + "\n";
				}
			} else if (STATUS_ZERO_RESULTS.equals(status)) {
				// System.out.println("no results");
				result = "no results";
			} else {
				// System.out.println("error: " + status);
				result = "no results";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private List<Message> nearbySearch(String keyword, UserProfile user, EntityManager em) {
		// TODO Auto-generated method stub
		net.sf.sprockets.google.Places.Response<List<Place>> response;
		String rankBy = user.getRankby();
		nices.clear();
		if (rankBy == null) {
			rankBy = Params.RANK_BY_PROMINENCE;
		}
		List<Message> messages = new ArrayList<Message>();
		try {

			if (user.getLatitude() > 0 && user.getRadius() > 0) {
				response = Places.nearbySearch(Params.create().latitude(user.getLatitude())
						.longitude(user.getLongitude()).radius(user.getRadius()).keyword(keyword).rankBy(rankBy));
			} else if (user.getLatitude() > 0 && user.getRadius() == 0) {
				response = Places.nearbySearch(Params.create().latitude(user.getLatitude())
						.longitude(user.getLongitude()).radius(2000).keyword(keyword).rankBy(rankBy));
			} else {
				response = Places.nearbySearch(Params.create().radius(2000).keyword(keyword).rankBy(rankBy));
			}
			String status = response.getStatus();
			List<Place> places = response.getResult();

			if (STATUS_OK.equals(status)) {
				int y = places.size();
				if (y >= 4) {
					y = 4;
				} else {
					y = places.size();
				}
				int i = 0;
				for (Place place : places) {
					net.sf.sprockets.google.Places.Response<Place> details = Places
							.details(Params.create().placeId(place.getPlaceId().getId()));
					
					if(i<=y) {

						ArrayList<Action> actions = new ArrayList<Action>();

						if (details.getResult().getUrl() != null) {
							URIAction mapAction = new URIAction("Detail", details.getResult().getUrl());
							actions.add(mapAction);

						}
						if (details.getResult().getWebsite() != null) {
							URIAction urlAction = new URIAction("Website", details.getResult().getWebsite());
							actions.add(urlAction);
						}

						String name = details.getResult().getName();
						if (name.length() > 40) {
							name = details.getResult().getName().substring(0, 40);
						}
						String imageUrl = hcpUrl + "/PlaceImageServlet?placeId=" + details.getResult().getPlaceId().getId();
						ButtonsTemplate button = createButtion(imageUrl, name,
								details.getResult().getTypes().get(0) + " Rating :" + details.getResult().getRating(), actions);

						Message message = (Message) new TemplateMessage(keyword, (Template) button);
						if (message != null) {
							messages.add(message);
						}
						i++;
					} 
					
					
					
					NicePlace nice = (NicePlace) em.find(NicePlace.class, details.getResult().getPlaceId().getId());
					if (nice == null) {
						nice = new NicePlace(details.getResult());
						nice.setCounter(1);
						nice.setCreateDate(new java.util.Date());
						nices.add(nice);
					} else {
						nice.setCounter(nice.getCounter()+1);
						nice.setAddress(details.getResult().getFormattedAddress());
						nice.setInternational_phone_number(details.getResult().getFormattedPhoneNumber());
						nice.setLatitude(details.getResult().getLatitude());
						nice.setLongitude(details.getResult().getLongitude());
						nice.setName(details.getResult().getName());
						nice.setPhoneNumber(details.getResult().getFormattedPhoneNumber());
						nice.setRating(details.getResult().getRating());
						nice.setTypes(details.getResult().getTypes());
						nice.setVicinity(details.getResult().getVicinity());
						nice.setWebsite(details.getResult().getWebsite());
					}
				}
				
				

			} else if (STATUS_ZERO_RESULTS.equals(status)) {
				// System.out.println("no results");

				messages.add((Message) new TextMessage("No data"));
			} else {
				// System.out.println("error: " + status);
				// message = (Message) new TextMessage(result);
				messages.add((Message) new TextMessage("No data"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
	}

	private String generateSecret() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();
		return output;
	}

	private TemplateMessage createRadius(String altText) {
		// TODO Auto-generated method stub
		// Template template = new Template();

		PostbackAction r0500 = new PostbackAction("500 m", "#r-500");
		PostbackAction r1000 = new PostbackAction("1 km", "#r-1000");
		PostbackAction r2000 = new PostbackAction("2 km", "#r-2000");
		PostbackAction r5000 = new PostbackAction("5 km", "#r-5000");
		ButtonsTemplate button = createButtion("https://dl.dropboxusercontent.com/u/57992228/nice/radius.png",
				"Set Radius", "Please select your prefer radius or type #r-xxxxx",
				Arrays.asList(r0500, r1000, r2000, r5000));
		return new TemplateMessage(altText, (Template) button);
	}

	private TemplateMessage createLocation(String altText) {
		// TODO Auto-generated method stub
		// Template template = new Template();
		PostbackAction ok = new PostbackAction("OK", "OK");
		ButtonsTemplate button = createButtion("https://dl.dropboxusercontent.com/u/57992228/nice/sharelocation.png",
				"Check-In", "Please check-in your locatin", Arrays.asList(ok));
		return new TemplateMessage(altText, (Template) button);
	}

	private TemplateMessage createWelcome(String altText) {
		// TODO Auto-generated method stub
		// Template template = new Template();

		PostbackAction help = new PostbackAction("Help1", "help2", "Text Help");
		MessageAction mHelp = new MessageAction("help label", "help");
		ButtonsTemplate button = createButtion("https://dl.dropboxusercontent.com/u/57992228/nice/management.png",
				"Hi..Title", "b text", Arrays.asList(help, mHelp));
		return new TemplateMessage(altText, (Template) button);
	}

	private ButtonsTemplate createButtion(String url, String title, String text, List<Action> action) {
		// TODO Auto-generated method stub
		ButtonsTemplate button = new ButtonsTemplate(url, title, text, action);

		return button;
	}

	private void replyLine(List<TextMessage> textMessage, String replyToken) {

		replyLine(textMessage.get(0), replyToken);
	}

	private void replyLine(TextMessage textMessage, String replyToken) {

		ReplyMessage replyMessage = new ReplyMessage(replyToken, (Message) textMessage);
		Response<BotApiResponse> res;
		try {
			res = LineMessagingServiceBuilder.create(dest.getChannelAccessToken()).build().replyMessage(replyMessage)
					.execute();
			if (res.code() != 200) {
				String error = this.readStream(res.errorBody().byteStream());
				LOGGER.debug("BODY: " + error);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void push(Message message, String userId) {

		Response<BotApiResponse> res;
		PushMessage pushMessage;
		try {

			pushMessage = new PushMessage(userId, Arrays.asList(message));

			res = LineMessagingServiceBuilder.create(dest.getChannelAccessToken()).apiEndPoint(dest.getUrl()).build()
					.pushMessage(pushMessage).execute();

			// res =
			// LineMessagingServiceBuilder.create(dest.getChannelAccessToken()).build().replyMessage(replyMessage)
			// .execute();
			if (res.code() != 200) {
				String error = this.readStream(res.errorBody().byteStream());
				LOGGER.debug("Push:" + pushMessage.toJSON(false));
				LOGGER.debug("BODY: " + error);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reply(Message message, String tokenReply) {
		reply(Arrays.asList(message), tokenReply);
	}

	private void reply(List<Message> message, String tokenReply) {

		Response<BotApiResponse> res;
		ReplyMessage replyMessage;
		try {

			replyMessage = new ReplyMessage(tokenReply, message);
			res = LineMessagingServiceBuilder.create(dest.getChannelAccessToken()).build().replyMessage(replyMessage)
					.execute();
			if (res.code() != 200) {
				String error = this.readStream(res.errorBody().byteStream());
				LOGGER.debug("Reply:" + replyMessage.toJSON(false));
				LOGGER.debug("BODY: " + error);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void replyMessageLine(List<Message> message, String replyToken) {

		ReplyMessage replyMessage = new ReplyMessage(replyToken, message);

		Response<BotApiResponse> res;
		try {
			res = LineMessagingServiceBuilder.create(dest.getChannelAccessToken()).build().replyMessage(replyMessage)
					.execute();
			if (res.code() != 200) {
				String error = this.readStream(res.errorBody().byteStream());
				LOGGER.error("BODY: " + error);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String readStream(InputStream stream) throws Exception {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
			String line;
			while ((line = in.readLine()) != null) {
				builder.append(line); // + "\r\n"(no need, json has no line
										// breaks!)
			}
			in.close();
		}
		// System.out.println("JSON: " + builder.toString());
		return builder.toString();
	}
	/*
	 * public void doFilter(HttpServletRequest request, HttpServletResponse
	 * response, FilterChain chain) throws IOException, ServletException { //
	 * TODO Auto-generated method stub HttpServletRequest httpRequest =
	 * (HttpServletRequest) request; Enumeration<String> headerNames =
	 * httpRequest.getHeaderNames();
	 * 
	 * if (headerNames != null) { while (headerNames.hasMoreElements()) {
	 * System.out.println("Header: " +
	 * httpRequest.getHeader(headerNames.nextElement())); } }
	 * 
	 * //doFilter chain.doFilter(httpRequest, response); }
	 */

	public static String getPostData(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = req.getReader();
			reader.mark(10000);

			String line;
			do {
				line = reader.readLine();
				sb.append(line).append("\n");
			} while (line != null);
			reader.reset();
			// do NOT close the reader here, or you won't be able to get the
			// post data twice
		} catch (IOException e) {
			LOGGER.error("getPostData couldn't.. get the post data", e); // This
																			// has
																			// happened
																			// if
																			// the
																			// request's
																			// reader
																			// is
																			// closed
		}

		return sb.toString();
	}

	private static ObjectMapper buildObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// Register JSR-310(java.time.temporal.*) module and read number as
		// millsec.
		objectMapper.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		return objectMapper;
	}

}
