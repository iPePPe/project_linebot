package com.zygen.linebot.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.ByteStreams;
import com.sap.cloud.account.TenantContext;
import com.zygen.hcp.jpa.JPAEntityFactoryManager;
import com.zygen.linebot.client.DestinationUtil;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.client.LineSignatureValidator;
import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.event.CallbackRequest;
import com.zygen.linebot.model.event.Event;
import com.zygen.linebot.model.event.MessageEvent;
import com.zygen.linebot.model.event.message.TextMessageContent;
import com.zygen.linebot.model.event.source.UserSource;
import com.zygen.linebot.model.message.Message;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.response.BotApiResponse;
import com.zygen.odata.client.ODataMessageBuilder;
import com.zygen.odata.model.message.ZtextMessageV2;

import com.zygen.ai.api.AIConfiguration;
import com.zygen.ai.api.AIDataService;
import com.zygen.ai.api.model.AIRequest;
import com.zygen.ai.api.model.AIResponse;
import retrofit2.Response;

/**
 * Servlet implementation class CallbackAiServlet
 */
public class CallbackAiServlet extends HttpServlet {
	private static final long serialVersionUID = 102831973239L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CallBackServlet.class);
	private final LineSignatureValidator lineSignatureValidator;
	private final ObjectMapper objectMapper;
	public static final String DATA_SOURCE_NAME = "java:comp/env/jdbc/DefaultDB";
	public static final String PERSISTENCE_UNIT_NAME = "persistence-linebot";
	private static final String lineapi = "line-api";
	private static final DestinationUtil dest = new DestinationUtil(lineapi);
	// private InitialContext ctx;

	private EntityManagerFactory emf;

	public CallbackAiServlet() {

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
				final List<Event> result = callbackRequest.getEvents();
				final MessageEvent messageEvent = (MessageEvent) result.get(0);

				replyToken = messageEvent.getReplyToken();
				addMessageEventToDB(messageEvent, channelId);

				final UserSource source = (UserSource) messageEvent.getSource();
				final String userId = source.getUserId();
				final TextMessageContent text = (TextMessageContent) messageEvent.getMessage();
				String linetext = text.getText();
				linetext = getCommand(linetext);
				ZtextMessageV2 ztext = new ZtextMessageV2(channelId, linetext, userId);
				ODataMessageBuilder odata = new ODataMessageBuilder("ZGFMLGW2HCollection", ztext.getId(),
						"HeaderToDetailNav");
				List<Message> msg = odata.getMessage();
				if (msg.size() > 0) {
					replyMessageLine(msg, messageEvent.getReplyToken());
				} else {
					replyLine(new TextMessage("I don't know"), messageEvent.getReplyToken());
				}

			}
		} catch (Exception e) {
			replyLine(new TextMessage("Service Unavailable"), replyToken);
			response.getWriter().println("Parser error: " + e.getMessage());
			LOGGER.error("Parse error", e);

		}
	}
	public String getCommand(String text) {
		/*if (args.length < 1) {
			showHelp("Please specify API key", ERROR_EXIT_CODE);
		}*/
		
		AIConfiguration configuration = new AIConfiguration("7655a22931034bc08c0e324ff594aee7");
		
		AIDataService dataService = new AIDataService(configuration);
		
		String command = text;
		
		try {
			//System.out.println("line="+line);
			AIRequest request = new AIRequest(text);

			AIResponse response = dataService.request(request);

			if (response.getStatus().getCode() == 200) {
				//System.out.println(response.getResult().getFulfillment().getSpeech());
				if (response.getResult().getAction().equals("input.unknown")){
					//System.out.println("idontknow");
					command = text;
				}else{
					//System.out.println(response.getResult().getFulfillment().getSpeech());
					//response.getResult().getContexts()
					command = response.getResult().getFulfillment().getSpeech();
				}
			} else {
				//System.err.println(response.getStatus().getErrorDetails());
				command = text;
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
			LOGGER.debug(ex.getMessage());
		}
		
		return command;
		//System.out.println("See ya!");
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

	public void addMessageEventToDB(MessageEvent messageEvent, String ch) throws Exception {

		emf = this.getEntityManagerFactory();

		EntityManager em = emf.createEntityManager();
		try {

			com.zygen.hcp.jpa.MessageEvent me = new com.zygen.hcp.jpa.MessageEvent();
			// UserProfile user = new UserProfile();
			me.setReplyToken(messageEvent.getReplyToken());
			me.setText(((TextMessageContent) messageEvent.getMessage()).getText());
			me.setTimestamp(Date.from(messageEvent.getTimestamp()));
			me.setType(((TextMessageContent) messageEvent.getMessage()).getType());
			me.setUserId(messageEvent.getSource().getUserId());
			me.setChannel(ch);
			/*
			 * Query query = em
			 * .createQuery("SELECT s FROM UserProfile s WHERE s.userId =" +
			 * messageEvent.getSource().getUserId());
			 * user.setUserId(messageEvent.getSource().getUserId());
			 */
			em.getTransaction().begin();
			em.setProperty("me-tenant.id", this.getCurrentTenantId());
			em.persist(me);
			em.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("DB Error " + e.getMessage());
		} finally {
			em.close();
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
