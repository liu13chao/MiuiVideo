/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  HttpClient.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.xiaomi.mitv.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * @author tianli
 *
 */
public class HttpClient {
	
	private static final String TAG = HttpClient.class.getName();
	private static final int TIMEOUT = 10000; // 10s
	private static final int MAX_TOTAL_CONNECTIONS = 30;

	private static DefaultHttpClient sHttpClient;

	private HashMap<String, String> headers = new HashMap<String, String>();

	private String proxyHost;
	private int proxyPort;

	public HttpClient() {
		createHttpClient();
	}
	
	public static void sendHttpRequest(final String url, int timeout){
		try{
			HttpClient httpClient = new HttpClient();
			httpClient.setTimoutMs(timeout);
			HttpRequest request = new HttpRequest();
			request.setUrl(url);
			httpClient.doGetRequest(request);
		}catch(Exception e){
		}
	}

	public HttpResponse doGetRequest(HttpRequest request)
			throws TimeoutException, NetworkErrorException,
			ServerErrorException {
		return doRequest(request, true);
	}

	public HttpResponse doPostRequest(HttpRequest request)
			throws TimeoutException, NetworkErrorException,
			ServerErrorException {
		return doRequest(request, false);
	}

	public HttpResponse doRequest(HttpRequest request, boolean isGet)
			throws TimeoutException, NetworkErrorException,
			ServerErrorException {
		if (proxyHost != null && proxyHost.length() > 0) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
			sHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		} else {
			sHttpClient.getParams().removeParameter(
					ConnRoutePNames.DEFAULT_PROXY);
		}
		HttpRequestBase httpRequest;
		if (isGet) {
			httpRequest = createHttpGet(request);
		} else {
			httpRequest = createHttpPost(request);
		}
		// add HTTP headers
		Iterator<Entry<String, String>> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) it.next();
			httpRequest.addHeader((String) entry.getKey(),
					(String) entry.getValue());
		}
		int statusCode;
		org.apache.http.HttpResponse response;
		try {
			response = sHttpClient.execute(httpRequest);
			statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
			case 200:
				InputStream is = null;
				is = response.getEntity().getContent();
				Header encoding = response.getEntity().getContentEncoding();
				if (encoding != null && encoding.getValue() != null
						&& encoding.getValue().contains("gzip")) {
					is = new GZIPInputStream(is);
				}
				response.getEntity().getContentLength();
				return new HttpResponse(
						response.getEntity().getContentLength(), is);
			default:
				response.getEntity().consumeContent();
				throw new ServerErrorException(statusCode,
						"network error with error code " + statusCode);
			}
		} catch (ConnectTimeoutException e) {
			throw new TimeoutException(e.getMessage(), e);
		} catch (SocketTimeoutException e) {
			throw new TimeoutException(e.getMessage(), e);
		} catch (ServerErrorException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetworkErrorException("unkown network error!");
		}
	}
	
	public void setTimoutMs(int timoutMs){
		final HttpParams params = sHttpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, timoutMs);
		HttpConnectionParams.setSoTimeout(params, timoutMs);
		ConnManagerParams.setTimeout(params, timoutMs);
	}

	private HttpGet createHttpGet(HttpRequest request) {
		String query = URLEncodedUtils.format(request.getParams(), HTTP.UTF_8);
		String fullUrl = request.getUrl();
		if (!fullUrl.contains("?")) {
			fullUrl += "?";
		}
		fullUrl += query;
		HttpGet httpGet = new HttpGet(fullUrl);
		return httpGet;
	}

	private HttpPost createHttpPost(HttpRequest request) {
		HttpPost httpPost = new HttpPost(request.getUrl());
		try {
			if(request.getBody() != null){
				httpPost.setEntity(new ByteArrayEntity(request.getBody()));
			}else{
				httpPost.setEntity(new UrlEncodedFormEntity(request.getParams(), HTTP.UTF_8));
			}
		} catch (Exception e) {
		}
		return httpPost;
	}

	public static List<NameValuePair> buildParams(
			Hashtable<String, String> params) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (params != null) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				String value = params.get(key);
				if (value != null) {
					NameValuePair pair = new BasicNameValuePair(key, value);
					list.add(pair);
				}
			}
		}
		return list;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	private static final synchronized void createHttpClient() {
		if (sHttpClient == null) {
			final SchemeRegistry supportedSchemes = new SchemeRegistry();
			SSLSocketFactory socketFactory = null;
			try {
				KeyStore trustStore;
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				socketFactory = new MySSLSocketFactory(trustStore);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			if (socketFactory == null) {
				socketFactory = SSLSocketFactory.getSocketFactory();
			}
			X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			socketFactory
					.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			supportedSchemes.register(new Scheme("https", socketFactory, 443));
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			final SocketFactory sf = PlainSocketFactory.getSocketFactory();
			supportedSchemes.register(new Scheme("http", sf, 80));
			// Set some client http client parameter defaults.
			final HttpParams httpParams = createHttpParams();
			HttpClientParams.setRedirecting(httpParams, false);
			final ClientConnectionManager connManager = new ThreadSafeClientConnManager(
					httpParams, supportedSchemes);
			sHttpClient = new DefaultHttpClient(connManager, httpParams);
		}
	}

	private final static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		// HttpClientParams.setRedirecting(params, true);
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
		ConnManagerParams.setTimeout(params, TIMEOUT);
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
		ConnPerRouteBean connRoute = new ConnPerRouteBean(MAX_TOTAL_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connRoute);
		return params;
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new MyX509TrustManager();
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}

		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			return true;
		}
	}

	private static class MyX509TrustManager implements X509TrustManager {
		public void checkClientTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public void checkServerTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
