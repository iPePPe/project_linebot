package com.zygen.hcp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.zygen.linebot.callback.CallBackServlet;
import com.zygen.linebot.client.DestinationUtil;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.model.PushMessage;
import com.zygen.linebot.model.message.Message;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.response.BotApiResponse;
import com.zygen.odata.client.ODataMessageBuilder;
import com.zygen.odata.model.message.ZtextMessageV2;

import retrofit2.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import com.sap.security.auth.login.LoginContextFactory;

/**
 * Servlet implementation class ActivationServlet
 */
public class ActivationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Context context;
	private static final String lineapi = "line-api";
	private static final DestinationUtil dest = new DestinationUtil(lineapi);
	private InitialContext ctx;
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivationServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ActivationServlet() {
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
		String user = request.getRemoteUser();
		try {
			ctx = new InitialContext();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (user != null) {

			response.getWriter().println("Hello : " + user);
			doActivation(request, response);
		} else {
			LoginContext loginContext;
			try {
				loginContext = LoginContextFactory.createLoginContext("FORM");
				loginContext.login();
				response.getWriter().println("Hello, " + request.getRemoteUser());

			} catch (LoginException e) {
				e.printStackTrace();
			}
		}
		/*
		 * try { doActivation(request, response); } catch (Exception e) {
		 * response.getWriter().println("parser error: " + e.getMessage());
		 * LOGGER.error("Parser Error", e); }
		 */

	}

	private void doActivation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub

		String channelId = request.getParameter("IvChannel");
		String userId = request.getParameter("IvUid");
		String iVString = request.getParameter("IvInput");
		if (channelId == null || userId == null || iVString == null) {
			// response.getWriter().println("channelId = " + channelId);
			// response.getWriter().println("userId = " + userId);
			// response.getWriter().println("iVString = " + iVString);
			// LOGGER.error("Parameter Error");
			try {
				response.getWriter().println("Tenant ID : " + getCurrentTenantId());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ZtextMessageV2 ztext = new ZtextMessageV2(channelId, iVString, userId);
			ODataMessageBuilder zg = new ODataMessageBuilder("ZGFMLGW2HCollection", ztext.getId(), "HeaderToDetailNav");
			List<TextMessage> textMessage;
			try {
				textMessage = zg.getLineTextMessage();
				response.getWriter().print((textMessage.get(0)).getText());
				PushMessage pushMessage = new PushMessage(userId, (List<Message>) (Object) textMessage);

				Response<BotApiResponse> res = LineMessagingServiceBuilder.create(dest.getChannelAccessToken())
						.apiEndPoint(dest.getUrl()).build().pushMessage(pushMessage).execute();
				if (res.code() != 200) {
					String error = this.readStream(res.errorBody().byteStream());
					response.getWriter().println(error);
					LOGGER.debug("BODY: " + error);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				response.getWriter().println("Accivation error: " + e.getMessage());
				LOGGER.error("Parse error", e);

			}
		}
		// System.out.println(response.code() + " " + response.message());
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private String getCurrentTenantId() throws NamingException {
		String currentTenantId = "";
		// InitialContext ctx = new InitialContext();
		try {
			ctx = new InitialContext();
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			TenantContext tenantContext = (TenantContext) envCtx.lookup("TenantContext");

			currentTenantId = tenantContext.getTenant().getId();

		} catch (NamingException e) {
			return "NOT_CURRENTLY_RUNNING_MULTI_-_TENANT"; // 36 chars as per
															// real tenant id
		}
		return currentTenantId;
	}

}