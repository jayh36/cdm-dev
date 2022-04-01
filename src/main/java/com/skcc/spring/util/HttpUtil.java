package com.skcc.spring.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpUtil {

	private final String method = "POST"; // POST만 가능. 그외의 Method는 에러 발생.
	private final String contentType = "application/json"; // application/json만 가능.
	private final String charset = "UTF-8"; // UTF-8 이외는 글자가 비정상.
	private final int connectionTimeout = 5000;
	private final int readTimeout = 10000;

	private String url;
	private String param;

	public HttpUtil(String url, String jsonStringParam) {
		this.url = url;
		this.param = jsonStringParam;
	}

	public String httpConnection() throws Exception {

		URL url = new URL(this.url);
		HttpURLConnection conn = null;
		if ("https".equalsIgnoreCase(url.getProtocol())) {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new SecureRandom());
			connect.setSSLSocketFactory(context.getSocketFactory());
			conn = connect;
		} else {
			conn = (HttpURLConnection) url.openConnection();

		}
		conn.setRequestProperty("Content-Type", this.contentType);
		conn.setRequestProperty("Accept-Charset", this.charset);
		conn.setRequestMethod(this.method);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);
		conn.setConnectTimeout(this.connectionTimeout);
		conn.setReadTimeout(this.readTimeout);

		OutputStream os = conn.getOutputStream();
		os.write(this.param.getBytes(this.charset));
		os.flush();
		os.close();

		String inputLine;
		StringBuffer response = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), this.charset));
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		conn.disconnect();

		return response.toString();
	}

}
