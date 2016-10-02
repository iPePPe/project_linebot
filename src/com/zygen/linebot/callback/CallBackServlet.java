package com.zygen.linebot.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.naming.InitialContext;
//import javax.naming.NamingException;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.ByteStreams;

import com.zygen.linebot.model.event.Event;
import com.zygen.linebot.model.event.MessageEvent;
import com.zygen.linebot.model.event.message.TextMessageContent;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.response.BotApiResponse;
import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.event.CallbackRequest;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
//import com.zygen.linebot.client.LineMessagingService;
//import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.client.LineSignatureValidator;

import lombok.NonNull;

import retrofit2.Response;


//import com.zygen.linebot.callback.LineMessageDAO;
//import com.zygen.linebot.model.message.TextMessage;

import java.sql.SQLException;
import java.util.List;


/**
 * Servlet implementation class CallBackServletd
 */
public class CallBackServlet extends HttpServlet {
	private static final long serialVersionUID = 102831973239L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CallBackServlet.class);
	private final LineSignatureValidator lineSignatureValidator;
	private final ObjectMapper objectMapper;
	//private LineMessageDAO lineDAO;
	//private LineMessagingService lineMessagingService;
	private static final String channelSecret = "f423494d7fc1881b5171ab65cb15b1d1";
	private static final String chanelAccessToken = "enCA8ygHaHvpPDRoGjxSlJh7/k84nPgTvFXJWIzTJsh6emQ/7pO4FYTUKdjK7Feislf31SieH2vZ+Pw4UvjQuWB6R7fHbmmaArv+bymUuJoOjf1skErDOZAuOcSU5PcWXxfjH3htlXo7LrOvQvmkgQdB04t89/1O/w1cDnyilFU=";
    public CallBackServlet() {
        this.lineSignatureValidator = new LineSignatureValidator(channelSecret.getBytes(StandardCharsets.UTF_8));
        this.objectMapper = buildObjectMapper();
    }
	/** {@inheritDoc} */
	@Override
	public void init() throws ServletException {
	/*	try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			lineDAO = new LineMessageDAO(ds);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (NamingException e) {
			throw new ServletException(e);
		}*/
	}




	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException  {
		try{
			doParser(request,response);
		}catch (Exception e) {
			response.getWriter().println("parser error: " + e.getMessage());
			LOGGER.error("Parser Error", e);
		}
	}
	public void doParser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException , CallBackServletException {
		// TODO Auto-generated method stub
		try {
			//doAdd(request);
			
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
	        }else{
	        	final List<Event> result = callbackRequest.getEvents();
	        	final MessageEvent messageEvent = (MessageEvent) result.get(0);
	        	final TextMessageContent text = (TextMessageContent) messageEvent.getMessage();
	        	String linetext = text.getText();
	        	TextMessage textMessage = new TextMessage("return text" + linetext);
	        	//List<TextMessage> textMessages = Collections.singletonList(textMessage);
	        	//List<TextMessage> textMessages = Arrays.asList(textMessage);
	        	//ReplyMessage reply = new ReplyMessage(messageEvent.getReplyToken(),(Message)textMessage);
	        	//response.getWriter().print(reply.toJSON(true));
	        	//BotApiResponse apiResponse = lineMessagingService.replyMessage(reply).execute().body();
	        	//response.getWriter().print(apiResponse);
	        	
	        	//TextMessage textMessage = new TextMessage("hello");
	        	ReplyMessage replyMessage = new ReplyMessage(
	        			messageEvent.getReplyToken(),
	        	        textMessage
	        	);
	        	//response.getWriter().print(replyMessage.toJSON(true));
	        	//doAdd(linetext,replyMessage.toJSON(true));
	        	Response<BotApiResponse> res =
	        	        LineMessagingServiceBuilder
	        	                .create(chanelAccessToken)
	        	                .build()
	        	                .replyMessage(replyMessage)
	        	                .execute();
	        	//System.out.println(response.code() + " " + response.message());
	        }
		} catch (Exception e) {
			response.getWriter().println("Parser error: " + e.getMessage());
			LOGGER.error("Parse error", e);
		}
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
	        // do NOT close the reader here, or you won't be able to get the post data twice
	    } catch(IOException e) {
	        LOGGER.error("getPostData couldn't.. get the post data", e);  // This has happened if the request's reader is closed    
	    }

	    return sb.toString();
	}
    private static ObjectMapper buildObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Register JSR-310(java.time.temporal.*) module and read number as millsec.
        objectMapper.registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }

}
