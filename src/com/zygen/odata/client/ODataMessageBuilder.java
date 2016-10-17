package com.zygen.odata.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataDeltaFeed;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.zygen.linebot.model.message.AudioMessage;
import com.zygen.linebot.model.message.ImageMessage;
import com.zygen.linebot.model.message.LocationMessage;
import com.zygen.linebot.model.message.Message;
import com.zygen.linebot.model.message.StickerMessage;
import com.zygen.linebot.model.message.TextMessage;
import com.zygen.linebot.model.message.VideoMessage;

public class ODataMessageBuilder {
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_DELETE = "DELETE";

	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";

	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_ATOM_XML = "application/atom+xml";
	public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
	public static final String METADATA = "$metadata";
	public static final String INDEX = "/index.jsp";
	public static final String SEPARATOR = "/";
	public static final String DESTINATION = "sap-gateway-service";

	public static final boolean PRINT_RAW_CONTENT = true;
	public static String serviceUrl;
	public static String usedFormat;
	public static String entitySetName;
	public static String oid;
	public static String extend;
	public static int statusCode;
	public List<Message> message = new ArrayList<Message>();;

	public ODataMessageBuilder() {
		// TODO Auto-generated constructor stub
	}

	public ODataMessageBuilder(String url, String entityname, String id, String format) {
		serviceUrl = url;
		entitySetName = entityname;
		usedFormat = format;
		oid = id;
	}

	public ODataMessageBuilder(String entityname, String id) {
		// serviceUrl =
		// "http://zygenplay.com:8082/sap/opu/odata/ZGL01/ZGL01_SRV";
		// serviceUrl = getUrlFromDestination(DESTINATION);
		entitySetName = entityname;
		usedFormat = APPLICATION_JSON;
		oid = id;
	}

	public ODataMessageBuilder(String entityname, String id, String ext) {

		entitySetName = entityname;
		usedFormat = APPLICATION_JSON;
		oid = id;
		extend = ext;
	}

	private static String getUrlFromDestination(String destination) {
		Context ctx;
		String dest = null;
		try {
			ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");
			// get destination configuration for "myDestinationName"
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destination);
			dest = destConfiguration.getProperty("URL");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dest;

	}

	public static void main(String[] paras) throws Exception {
		String url = "http://zygenplay.com:8082/sap/opu/odata/ZGL01/ZGL02_SRV";
		ODataMessageBuilder app = new ODataMessageBuilder(url, "ZGFMLGW2HCollection",
				"IvChannel='1472660011',IvInput='fioriurl',IvUid='27ffa61f11828d1d5f03029348932e6251fb2470f3222877d453e05d3bc57902e7'",
				APPLICATION_JSON);

		// print("\n----- Read Edm ------------------------------");
		Edm edm = app.readEdm(serviceUrl);
		print("Read default EntityContainer: " + edm.getDefaultEntityContainer().getName());

		print("\n----- Read Entry ------------------------------");
		// ODataEntry entry = app.readEntry(edm, serviceUrl, usedFormat,
		// entitySetName, oid, "HeaderToDetailNav");
		InputStream is = app.readInputSteam(edm, serviceUrl, usedFormat, entitySetName, oid, "HeaderToDetailNav");
		// entry.getProperties().
		// Map<String, Object> values = new HashMap<String, Object>();
		ODataEntry entry = null;
		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		EdmEntitySet entitySet = entityContainer.getEntitySet(entitySetName);
		entry = EntityProvider.readEntry(usedFormat, entitySet, is, EntityProviderReadProperties.init().build());
		app.parseData(entry);
		

	}

	public List<Message> getMessage() {
		Edm edm;
		try {
			edm = this.readEdm(getUrlFromDestination(DESTINATION));

			ODataEntry entry = readEntry(edm, getUrlFromDestination(DESTINATION), usedFormat, entitySetName, oid, extend);
			parseData(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ODataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;

	}

	private void parseData(ODataEntry entry) {
		Set<Entry<String, Object>> entries = entry.getProperties().entrySet();
		for (Entry<String, Object> entry1 : entries) {
			Object value = entry1.getValue();
			// String key = entry1.getKey();
			// System.out.println("Key=" + key);
			if (value instanceof String) {

				// String text = value == null ? "null" : value.toString();
				// System.out.println("Value=" + text);
			} else if (value instanceof ODataDeltaFeed) {
				// System.out.println("ODT");
				ODataDeltaFeed feed = (ODataDeltaFeed) value;
				List<ODataEntry> inlineEntries = feed.getEntries();
				// System.out.println(inlineEntries.size());
				for (int i = 0; i < inlineEntries.size(); i++) {
					buildMessage(inlineEntries.get(i).getProperties());

				}

			} else if (entry1.getValue() instanceof Calendar) {
				Calendar cal = (Calendar) entry1.getValue();
				value = SimpleDateFormat.getInstance().format(cal.getTime());
			} else {
				handleEntry((Map<String, Object>) value);
			}
		}
	}

	private static void handleEntry(Map<String, Object> properties) {
		Set<Entry<String, Object>> entries = properties.entrySet();
		for (Entry<String, Object> entry : entries) {
			Object value = entry.getValue();
			String key = entry.getKey();
			System.out.println("Key:" + key);
			if (value instanceof Map) {
				System.out.println("Map");
				handleEntry((Map<String, Object>) value);
			} else if (value instanceof Calendar) {
				System.out.println("Cal");
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());

			} else if (value instanceof ODataDeltaFeed) {
				System.out.println("ODF");
				ODataDeltaFeed feed = (ODataDeltaFeed) value;
				List<ODataEntry> inlineEntries = feed.getEntries();

				for (ODataEntry oDataEntry : inlineEntries) {
					handleEntry((Map<String, Object>) oDataEntry.getProperties());
				}
			} else {
				System.out.println("Else");
				String text = entry.getValue() == null ? "null" : entry.getValue().toString();
				System.out.println("Value=" + text);
				if (key.equals("EvString")) {

				} else if (key.equals("HeaderToDetailNav")) {

				} else if (key.equals("results")) {

				}
			}
		}
	}

	private void buildMessage(Map<String, Object> properties) {

		//List<Message> msg = new ArrayList<Message>();
		String type = (String) properties.get("Type");
		if (type.equals("text")) {
			TextMessage textMessage = new TextMessage((String) properties.get("F1"));
			message.add(textMessage);

		} else if (type.equals("sticker")) {
			StickerMessage sticker = new StickerMessage((String) properties.get("F1"), (String) properties.get("F2"));
			message.add((Message) sticker);

		}else if (type.equals("video")) {
			VideoMessage video = new VideoMessage((String) properties.get("F1"), (String) properties.get("F2"));
			message.add((Message)video);
		}else if (type.equals("audio")) {
			AudioMessage video = new AudioMessage((String) properties.get("F1"), 
												  Integer.getInteger((String)properties.get("F2")) );
			message.add((Message)video);
		}else if (type.equals("imagemap")) {
			TextMessage text = new TextMessage("Not yet support");
			message.add((Message)text);
		}else if (type.equals("image")) {
			ImageMessage image = new ImageMessage((String) properties.get("F1"), 
												  (String) properties.get("F2"));
			message.add((Message)image);
		}else if (type.equals("template")) {
			TextMessage text = new TextMessage("Not yet support");
			message.add((Message)text);
		}else if (type.equals("location")) {
			LocationMessage location = new LocationMessage((String) properties.get("F1"), 
														   (String) properties.get("F2"), 
														   Double.parseDouble((String)properties.get("F3")), 
														   Double.parseDouble((String)properties.get("F4"))   );
			message.add((Message)location);
		}



	}

	public List<TextMessage> getLineTextMessage() throws Exception {
		List<TextMessage> text = new ArrayList<TextMessage>();
		List<String> rets = new ArrayList<String>();
		Edm edm = this.readEdm(getUrlFromDestination(DESTINATION));
		ODataEntry entry = readEntry(edm, getUrlFromDestination(DESTINATION), usedFormat, entitySetName, oid, extend);

		Map<String, Object> properties = entry.getProperties();
		Set<Entry<String, Object>> entries = properties.entrySet();
		for (Entry<String, Object> ent : entries) {
			if (ent.getKey().equals("EvString")) {
				rets = splitEqually((String) ent.getValue(), 1000);
				for (int i = 0; i < rets.size(); i++) {
					// System.out.println(arrJavaTechnologies.get(i));
					text.add(new TextMessage(rets.get(i)));
				}
			}
		}
		return text;
	}

	public static List<String> splitEqually(String text, int size) {
		// Give the list the right capacity to start with. You could use an
		// array
		// instead if you wanted.
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}

	private static void print(String content) {
		System.out.println(content);
	}

	private static String prettyPrint(ODataEntry createdEntry) {
		return prettyPrint(createdEntry.getProperties(), 0);
	}

	private static String prettyPrint(Map<String, Object> properties, int level) {
		StringBuilder b = new StringBuilder();
		Set<Entry<String, Object>> entries = properties.entrySet();

		for (Entry<String, Object> entry : entries) {
			intend(b, level);
			b.append(entry.getKey()).append(": ");
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = prettyPrint((Map<String, Object>) value, level + 1);
				b.append(value).append("\n");
			} else if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());
				b.append(value).append("\n");
			} else if (value instanceof ODataDeltaFeed) {
				ODataDeltaFeed feed = (ODataDeltaFeed) value;
				List<ODataEntry> inlineEntries = feed.getEntries();
				b.append("{");
				for (ODataEntry oDataEntry : inlineEntries) {
					value = prettyPrint((Map<String, Object>) oDataEntry.getProperties(), level + 1);
					b.append("\n[\n").append(value).append("\n],");
				}
				b.deleteCharAt(b.length() - 1);
				intend(b, level);
				b.append("}\n");
			} else {
				b.append(value).append("\n");
			}
		}

		// remove last line break
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	private static String toJson(ODataEntry createdEntry) {
		return toJson(createdEntry.getProperties(), 0);
	}

	private static String toJson(Map<String, Object> properties, int level) {
		StringBuilder b = new StringBuilder();
		Set<Entry<String, Object>> entries = properties.entrySet();
		b.append("{\n");
		intend(b, level);
		level++;
		b.append("\"message\": {").append("\n");
		for (Entry<String, Object> entry : entries) {
			intend(b, level);
			b.append("\"").append(entry.getKey()).append("\" : ");
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = toJson((Map<String, Object>) value, level + 1);
				b.append(value).append("\"\n");
			} else if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());
				b.append(value).append("\"\n");
			} else if (value instanceof ODataDeltaFeed) {
				ODataDeltaFeed feed = (ODataDeltaFeed) value;
				List<ODataEntry> inlineEntries = feed.getEntries();
				b.append("{");
				for (ODataEntry oDataEntry : inlineEntries) {
					value = toJson((Map<String, Object>) oDataEntry.getProperties(), level + 1);
					b.append("\n[\n").append(value).append("\n],");
				}
				b.deleteCharAt(b.length() - 1);
				intend(b, level);
				b.append("}\n");
			} else {
				b.append("\"").append(value).append("\"\n");
			}
		}
		b.append("}").append("\n");
		b.append("}").append("\n");
		// remove last line break
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	private static void intend(StringBuilder builder, int intendLevel) {
		for (int i = 0; i < intendLevel; i++) {
			builder.append("  ");
		}
	}

	public void generateSampleData(String serviceUrl) throws MalformedURLException, IOException {
		String url = serviceUrl.substring(0, serviceUrl.lastIndexOf(SEPARATOR));
		HttpURLConnection connection = initializeConnection(url + INDEX, APPLICATION_FORM, HTTP_METHOD_POST);
		String content = "genSampleData=true";
		connection.getOutputStream().write(content.getBytes());
		print("Generate response: " + checkStatus(connection));
		connection.disconnect();
	}

	public Edm readEdm(String serviceUrl) throws IOException, ODataException {
		InputStream content = execute(serviceUrl + SEPARATOR + METADATA, APPLICATION_XML, HTTP_METHOD_GET);
		return EntityProvider.readMetadata(content, false);
	}

	public ODataFeed readFeed(Edm edm, String serviceUri, String contentType, String entitySetName)
			throws IOException, ODataException {
		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		String absolutUri = createUri(serviceUri, entitySetName, null);

		InputStream content = execute(absolutUri, contentType, HTTP_METHOD_GET);
		return EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
				EntityProviderReadProperties.init().build());
	}

	public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String keyValue)
			throws IOException, ODataException {
		return readEntry(edm, serviceUri, contentType, entitySetName, keyValue, null);
	}

	public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String keyValue,
			String expandRelationName) throws IOException, ODataException {
		// working with the default entity container
		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		// create absolute uri based on service uri, entity set name with its
		// key property value and optional expanded relation name
		String absolutUri = createUri(serviceUri, entitySetName, keyValue, expandRelationName);

		InputStream content = execute(absolutUri, contentType, HTTP_METHOD_GET);
		// this.logRawContent("/n", content, "/n");
		return EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content,
				EntityProviderReadProperties.init().build());
	}

	public InputStream readInputSteam(Edm edm, String serviceUri, String contentType, String entitySetName,
			String keyValue, String expandRelationName) throws IOException, ODataException {
		// working with the default entity container

		// create absolute uri based on service uri, entity set name with its
		// key property value and optional expanded relation name
		String absolutUri = createUri(serviceUri, entitySetName, keyValue, expandRelationName);

		InputStream content = execute(absolutUri, contentType, HTTP_METHOD_GET);
		// this.logRawContent("/n", content, "/n");
		return content;
	}

	private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		if (PRINT_RAW_CONTENT) {
			byte[] buffer = streamToArray(content);
			print(prefix + new String(buffer) + postfix);
			return new ByteArrayInputStream(buffer);
		}
		return content;
	}

	private byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while (readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		stream.close();
		return result;
	}

	public ODataEntry createEntry(Edm edm, String serviceUri, String contentType, String entitySetName,
			Map<String, Object> data) throws Exception {
		String absolutUri = createUri(serviceUri, entitySetName, null);
		return writeEntity(edm, absolutUri, entitySetName, data, contentType, HTTP_METHOD_POST);
	}

	public void updateEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String id,
			Map<String, Object> data) throws Exception {
		String absolutUri = createUri(serviceUri, entitySetName, id);
		writeEntity(edm, absolutUri, entitySetName, data, contentType, HTTP_METHOD_PUT);
	}

	public HttpStatusCodes deleteEntry(String serviceUri, String entityName, String id) throws IOException {
		String absolutUri = createUri(serviceUri, entityName, id);
		HttpURLConnection connection = connect(absolutUri, APPLICATION_XML, HTTP_METHOD_DELETE);
		return HttpStatusCodes.fromStatusCode(connection.getResponseCode());
	}

	private ODataEntry writeEntity(Edm edm, String absolutUri, String entitySetName, Map<String, Object> data,
			String contentType, String httpMethod)
			throws EdmException, MalformedURLException, IOException, EntityProviderException, URISyntaxException {

		HttpURLConnection connection = initializeConnection(absolutUri, contentType, httpMethod);

		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		EdmEntitySet entitySet = entityContainer.getEntitySet(entitySetName);
		URI rootUri = new URI(entitySetName);

		EntityProviderWriteProperties properties = EntityProviderWriteProperties.serviceRoot(rootUri).build();
		// serialize data into ODataResponse object
		ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, data, properties);
		// get (http) entity which is for default Olingo implementation an
		// InputStream
		Object entity = response.getEntity();
		if (entity instanceof InputStream) {
			byte[] buffer = streamToArray((InputStream) entity);
			// just for logging
			String content = new String(buffer);
			print(httpMethod + " request on uri '" + absolutUri + "' with content:\n  " + content + "\n");
			//
			connection.getOutputStream().write(buffer);
		}

		// if a entity is created (via POST request) the response body contains
		// the new created entity
		ODataEntry entry = null;
		HttpStatusCodes statusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (statusCode == HttpStatusCodes.CREATED) {
			// get the content as InputStream and de-serialize it into an
			// ODataEntry object
			InputStream content = connection.getInputStream();
			// content = logRawContent(httpMethod + " request on uri '" +
			// absolutUri + "' with content:\n ", content, "\n");
			entry = EntityProvider.readEntry(contentType, entitySet, content,
					EntityProviderReadProperties.init().build());
		}

		//
		connection.disconnect();

		return entry;
	}

	private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());

		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {

		}
		return httpStatusCode;
	}

	private static String readStream(InputStream stream) throws Exception {
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

	private String createUri(String serviceUri, String entitySetName, String id) {
		return createUri(serviceUri, entitySetName, id, null);
	}

	private String createUri(String serviceUri, String entitySetName, String id, String expand) {
		final StringBuilder absolutUri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
		if (id != null) {
			absolutUri.append("(").append(id).append(")");
		}
		if (expand != null) {
			absolutUri.append("/?$expand=").append(expand);
		}
		return absolutUri.toString();
	}

	private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);
		InputStream content = null;
		connection.connect();
		HttpStatusCodes code = checkStatus(connection);
		statusCode = code.getStatusCode();
		if (code.getStatusCode() == 200) {
			content = connection.getInputStream();
		} else {
			content = connection.getErrorStream();
		}
		// content = logRawContent(httpMethod + " request on uri '" +
		// relativeUri + "' with content:\n ", content, "\n");
		return content;
	}

	private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();

		try {
			checkStatus(connection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

	private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
			throws MalformedURLException, IOException {
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
		if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		return connection;
	}

	private HttpURLConnection initializeConnectionFromDestination(String destination, String contentType,
			String httpMethod) throws MalformedURLException, IOException, NamingException {

		try {
			// look up the connectivity configuration API
			// "connectivityConfiguration"
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");
			// get destination configuration for "myDestinationName"
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destination);

			// get the configured keystore
			// KeyStore keyStore = destConfiguration.getKeyStore();

			// get the configured truststore
			// KeyStore trustStore = destConfiguration.getTrustStore();

			// create sslcontext
			// TrustManagerFactory tmf =
			// TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// tmf.init(trustStore);

			// KeyManagerFactory keyManagerFactory =
			// KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			// String keyStorePassword = "myPassword";
			// keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

			// SSLContext sslcontext = SSLContext.getInstance("TLSv1");
			// sslcontext.init(keyManagerFactory.getKeyManagers(),
			// tmf.getTrustManagers(), null);
			// SSLSocketFactory sslSocketFactory =
			// sslcontext.getSocketFactory();

			URL url = new URL(destConfiguration.getProperty("URL"));

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod(httpMethod);
			connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
			if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
				connection.setDoOutput(true);
				connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
			}
			return connection;
		} catch (NamingException e) {
			throw new NamingException();
		}

	}

}
