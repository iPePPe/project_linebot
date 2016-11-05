package com.zygen.linebot.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
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

//import javax.naming.InitialContext;
//import javax.naming.NamingException;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
import com.zygen.linebot.model.message.Message;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.response.BotApiResponse;
import com.zygen.odata.client.ODataMessageBuilder;
import com.zygen.odata.model.message.ZtextMessageV2;
import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.event.BeaconEvent;
import com.zygen.linebot.model.event.CallbackRequest;
import com.zygen.hcp.jpa.JPAEntityFactoryManager;
import com.zygen.hcp.model.UserProfileModel;
import com.zygen.linebot.client.DestinationUtil;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.client.LineSignatureValidator;

import retrofit2.Response;

import java.sql.Date;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	// private InitialContext ctx;

	private EntityManagerFactory emf;

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

			response.getWriter().print("PePPe OData2..." + this.getCurrentTenantId());
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
			doParser(request, response);
		} catch (Exception e) {
			response.getWriter().println("parser error: " + e.getMessage());
			LOGGER.error("Parser Error", e);
		}
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
		            	//replyLine(new TextMessage("Error"), messageEvent.getReplyToken());
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
					
					ZtextMessageV2 ztext = new ZtextMessageV2(ch, textContent.getText(), messageEvent.getSource().getUserId());
					ODataMessageBuilder odata = new ODataMessageBuilder("ZGFMLGW2HCollection", ztext.getId(),
							"HeaderToDetailNav");
					List<Message> msg = odata.getMessage();
					if (msg.size() > 0) {
						replyMessageLine(msg, messageEvent.getReplyToken());
					} else {
						replyLine(new TextMessage("I don't know"), messageEvent.getReplyToken());
					}
				} else if (message instanceof StickerMessageContent) {
					StickerMessageContent stkContent = (StickerMessageContent) message;

					me.setMessageId(stkContent.getId());
					me.setType(stkContent.getType());
					me.setPackageId(stkContent.getPackageId());
					me.setStickerId(stkContent.getStickerId());

				} else if (message instanceof LocationMessageContent) {
					LocationMessageContent locationMessage = (LocationMessageContent) message;
					me.setMessageId(locationMessage.getId());
					me.setLatitude(locationMessage.getLatitude());
					me.setLongitude(locationMessage.getLongitude());
					me.setTitle(locationMessage.getTitle());
					me.setAddress(locationMessage.getAddress());
					me.setType(locationMessage.getType());

				} else if (message instanceof AudioMessageContent) {
					AudioMessageContent auContent = (AudioMessageContent) message;
					me.setMessageId(auContent.getId());
					me.setType(auContent.getType());
					me.setUrl(auContent.getUrl());

				} else if (message instanceof ImageMessageContent) {
					ImageMessageContent imageContent = (ImageMessageContent) message;
					me.setMessageId(imageContent.getId());
					me.setType(imageContent.getType());
					me.setUrl(imageContent.getUrl());
				}
			} else if (event instanceof UnfollowEvent) {
				
			} else if (event instanceof FollowEvent) {
				
			} else if (event instanceof JoinEvent) {
				
			} else if (event instanceof LeaveEvent) {
				
			} else if (event instanceof BeaconEvent) {
				
			} else if (event instanceof PostbackEvent) {
				
			} else if (event instanceof UnknownEvent) {
				
			}

			em.getTransaction().begin();
			em.setProperty("me-tenant.id", this.getCurrentTenantId());

			UserProfileModel upm = new UserProfileModel();
			em.persist(upm.checkCreateUserProfile(em, event.getSource().getUserId(), this.getCurrentTenantId(),
					dest.getChannelAccessToken(), me));
			em.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("DB Error " + e.getMessage());
		} finally {
			em.close();
		}
	}

	private Class<?> getPropertyType(Class<?> clazz, String property) {
		try {
			LinkedList<String> properties = new LinkedList<String>();
			properties.addAll(Arrays.asList(property.split("\\.")));
			Field field = null;
			while (!properties.isEmpty()) {
				field = clazz.getDeclaredField(properties.removeFirst());
				clazz = field.getType();
			}
			return field.getType();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
