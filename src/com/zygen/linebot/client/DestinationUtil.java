package com.zygen.linebot.client;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

public class DestinationUtil {
	private Context context;
	private static ConnectivityConfiguration connectConfig;
	private static DestinationConfiguration destinationConfig;
	private String destination;
	public DestinationUtil(String destination) {
		this.setDestination(destination);
		this.context = this.initialContext();
		try {
			connectConfig = this.initialConnectivityConfigulation(context);
			destinationConfig = getDestinationConfiguration();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Context initialContext() {
		Context ctx = null;
		try {
			return ctx = new InitialContext();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctx;
	}

	private ConnectivityConfiguration initialConnectivityConfigulation(Context cont) throws NamingException {
		ConnectivityConfiguration configuration = (ConnectivityConfiguration) context
				.lookup("java:comp/env/connectivityConfiguration");
		// get destination configuration for "myDestinationName"

		return configuration;
	}

	private DestinationConfiguration getDestinationConfiguration() {

		return connectConfig.getConfiguration(this.destination);
	}

	public  String getUrl()  {


		return destinationConfig.getProperty("URL");

	}

	public  String getChannelAccessToken() {

		return destinationConfig.getProperty("ChannelAccessToken");

	}
	public String getChannelSecret(){
		return destinationConfig.getProperty("ChannelSecret");
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
