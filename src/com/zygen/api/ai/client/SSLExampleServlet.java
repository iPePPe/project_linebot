package com.zygen.api.ai.client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.cloud.crypto.keystore.api.KeyStoreService;


public class SSLExampleServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

     // get Keystore Service
     KeyStoreService keystoreService;
     try {
       Context context = new InitialContext();
       keystoreService = (KeyStoreService) context.lookup("java:comp/env/KeyStoreService");
     } catch (NamingException e) {
       response.getWriter().println("Error:<br><pre>");
       e.printStackTrace(response.getWriter());
       response.getWriter().println("</pre>");
       throw new ServletException(e);
     }

     String host = request.getParameter("host");
     if (host == null || (host = host.trim()).isEmpty()) {
       response.getWriter().println("Host is not specified");
       return;
     }
     String port = request.getParameter("port");
     if (port == null || (port = port.trim()).isEmpty()) {
       port = "443";
     }
     String path = request.getParameter("path");
     if (path == null || (path = path.trim()).isEmpty()) {
       path = "/";
     }
     String clientKeystoreName = "client";
    
     String clientKeystorePassword = request.getParameter("client.keystore.password");
     if (clientKeystorePassword == null || (clientKeystorePassword = clientKeystorePassword.trim()).isEmpty()) {
       response.getWriter().println("Password for client keystore is not specified");
       return;
     }
     String trustedCAKeystoreName = "cacerts";

    // get a named keystores with password for integrity check
     KeyStore clientKeystore;
     try {
       clientKeystore = keystoreService.getKeyStore(clientKeystoreName, clientKeystorePassword.toCharArray());
     } catch (Exception e) {
       response.getWriter().println("Client keystore is not available: " + e);
       return;
     } 

     // get a named keystore without integrity check
     KeyStore trustedCAKeystore;
     try {
       trustedCAKeystore = keystoreService.getKeyStore(trustedCAKeystoreName, null);
     } catch (Exception e) {
       response.getWriter().println("Trusted CAs keystore is not available" + e);
       return;
     }  
   
  callHTTPSServer(response, host, port, path, clientKeystorePassword, clientKeystore, trustedCAKeystore);
}

 private void callHTTPSServer(HttpServletResponse response, 
                              String host, 
                              String port, 
                              String path, 
                              String clientKeystorePassword, 
                              KeyStore clientKeystore, 
                              KeyStore trustedCAKeystore) throws IOException {
   try {
     KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
     kmf.init(clientKeystore, clientKeystorePassword.toCharArray());

     KeyManager[] keyManagers = kmf.getKeyManagers();
     TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
     tmf.init(trustedCAKeystore);

     TrustManager[] trustManagers = tmf.getTrustManagers();
     SSLContext sslContext = SSLContext.getInstance("TLS");
     sslContext.init(keyManagers, trustManagers, null);

     SSLSocketFactory factory = sslContext.getSocketFactory();

     SSLSocket socket = (SSLSocket)factory.createSocket(host, Integer.parseInt(port));
     socket.startHandshake();

     PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
     out.println("GET " + path + " HTTP/1.0");
     out.println();
     out.flush();

     if (out.checkError()) {
       response.getWriter().println("Error durring request sending");
     }

     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     String inputLine;
     while ((inputLine = in.readLine()) != null) {
       response.getWriter().println(inputLine);
     }

     in.close();
     out.close();
     socket.close();
   } catch (Exception e) {
     response.getWriter().println("Error:<br><pre>");
     e.printStackTrace(response.getWriter());
     response.getWriter().println("</pre>");
   } finally {
     response.getWriter();
   }
 } 
}