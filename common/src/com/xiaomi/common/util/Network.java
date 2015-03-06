package com.xiaomi.common.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Network {
    private static final String LogTag = "com.xiaomi.common.Network";

    /**
     * user agent for chrome browser on PC
     */
    public static final String UserAgent_PC_Chrome_6_0_464_0 = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.464.0 Safari/534.3";

    public static final String UserAgent_PC_Chrome = UserAgent_PC_Chrome_6_0_464_0;

    public static final String CMWAP_GATEWAY = "10.0.0.172";

    public static final int CMWAP_PORT = 80;

    private static final String CMWAP_HEADER_HOST_KEY = "X-Online-Host";

    public static InputStream downloadXmlAsStream(Context context, URL url) throws IOException {
        return downloadXmlAsStream(context, url, true, null, null, null, null);
    }

    public static InputStream downloadXmlAsStream(Context context, URL url, boolean noEncryptUrl,
            String userAgent, String cookie) throws IOException {
        return downloadXmlAsStream(context, url, noEncryptUrl, userAgent, cookie, null, null);
    }

    /**
     * 包装 HTTP request/response 的辅助函数
     *
     * @param context 应用程序上下文
     * @param url HTTP地址
     * @param noEncryptUrl 是否加密
     * @param userAgent
     * @param cookie
     * @param requestHdrs 用于传入除userAgent和cookie之外的其他header info
     * @param responseHdrs 返回的HTTP response headers;
     * @return
     * @throws IOException
     */
    public static InputStream downloadXmlAsStream(
            /* in */Context context,
            /* in */URL url, boolean noEncryptUrl, String userAgent, String cookie,
            Map<String, String> requestHdrs,
            /* out */HttpHeaderInfo responseHdrs) throws IOException {
        if (null == url)
            throw new IllegalArgumentException("url");

        URL newUrl = url;
        if (!noEncryptUrl)
            newUrl = new URL(encryptURL(url.toString()));

        InputStream responseStream = null;
        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection conn = getHttpUrlConnection(context, newUrl);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(2*60*1000);
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-agent", userAgent);
        }
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        if (null != requestHdrs) {
            for (String key : requestHdrs.keySet()) {
                conn.setRequestProperty(key, requestHdrs.get(key));
            }
        }

        if ((responseHdrs != null)
                && (url.getProtocol().equals("http") || url.getProtocol().equals("https"))) {
            responseHdrs.ResponseCode = conn.getResponseCode();
            if (responseHdrs.AllHeaders == null)
                responseHdrs.AllHeaders = new HashMap<String, String>();
            for (int i = 0;; i++) {
                String name = conn.getHeaderFieldKey(i);
                String value = conn.getHeaderField(i);

                if (name == null && value == null)
                    break;
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value))
                    continue;
                if (name != null) {
                    responseHdrs.AllHeaders.put(name.toLowerCase(), value);
                }
            }
        }

        responseStream = conn.getInputStream();
        return responseStream;
    }

    public static InputStream downloadXmlAsStreamWithoutRedirect(URL url, String userAgent,
            String cookie) throws IOException {
        InputStream responseStream = null;
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(2*60*1000);
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-agent", userAgent);
        }
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }

        int resCode = conn.getResponseCode();
        if (resCode < 300 || resCode >= 400) {
            responseStream = conn.getInputStream();
        }
        return responseStream;
    }

    public static String downloadXml(Context context, URL url) throws IOException {
        return downloadXml(context, url, false, null, "UTF-8", null);
    }

    public static String downloadXml(Context context, URL url, boolean noEncryptUrl,
            String userAgent, String encoding, String cookie) throws IOException {
        InputStream responseStream = null;
        StringBuilder sbReponse;
        try {
            responseStream = downloadXmlAsStream(context, url, noEncryptUrl, userAgent, cookie);
            sbReponse = new StringBuilder(1024);
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream,
                    encoding), 1024);
            String line;
            while (null != (line = reader.readLine())) {
                sbReponse.append(line);
                sbReponse.append("\r\n");
            }
        } finally {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    Log.e(LogTag, "Failed to close responseStream" + e.toString());
                }
            }
        }

        String responseXml = sbReponse.toString();
        return responseXml;
    }

    /**
     * Based on the doc at
     * "http://diveintomark.org/archives/2004/02/13/xml-media-types" RFC 3023
     * (XML Media Types) defines the interaction between XML and HTTP as it
     * relates to character encoding. HTTP uses MIME to define a method of
     * specifying the character encoding, as part of the Content-Type HTTP
     * header, which looks like this: Content-Type: text/html; charset="utf-8"
     * If no charset is specified, HTTP defaults to iso-8859-1, but only for
     * text/* media types. (Thanks, Ian.) For other media types, the default
     * encoding is undefined, which is where RFC 3023 comes in. In XML, the
     * character encoding is optional and can be given in the XML declaration in
     * the first line of the document, like this: <xml version="1.0"
     * encoding="iso-8859-1"?> If no encoding is given and no Byte Order Mark is
     * present (don’t ask), XML defaults to utf-8. (For those of you smart
     * enough to realize that this is a Catch-22, that an XML processor can’t
     * possibly read the XML declaration to determine the document’s character
     * encoding without already knowing the document’s character encoding,
     * please read Section F of the XML specification and bow in awe at the
     * intricate care with which this issue was thought out.) According to RFC
     * 3023, if the media type given in the Content-Type HTTP header is
     * application/xml, application/xml-dtd,
     * application/xml-external-parsed-entity, or any one of the subtypes of
     * application/xml such as application/atom+xml or application/rss+xml or
     * even application/rdf+xml, then the encoding is: 1. the encoding given in
     * the charset parameter of the Content-Type HTTP header, 2. or the encoding
     * given in the encoding attribute of the XML declaration within the
     * document, 3. or utf-8. On the other hand, if the media type given in the
     * Content-Type HTTP header is text/xml, text/xml-external-parsed-entity, or
     * a subtype like text/AnythingAtAll+xml, then the encoding attribute of the
     * XML declaration within the document is ignored completely, and the
     * encoding is 1. the encoding given in the charset parameter of the
     * Content-Type HTTP header, 2. or us-ascii.
     *
     * @param url
     * @param userAgent
     * @return
     * @throws IOException
     */
    public static String tryDetectCharsetEncoding(URL url, String userAgent) throws IOException {
        if (null == url)
            throw new IllegalArgumentException("url");

        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-agent", userAgent);
        }

        String ret = null;

        // 1. the encoding given in the charset parameter of the Content-Type
        // HTTP header,
        String contentType = conn.getContentType();
        if (!TextUtils.isEmpty(contentType)) {
            Matcher matcher = ContentTypePattern_Charset.matcher(contentType);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                String charset = matcher.group(2);
                if (!TextUtils.isEmpty(charset)) {
                    ret = charset;
                    Log.v(LogTag, "HTTP charset detected is: " + ret);
                }
            }

            // 2. or the encoding given in the encoding attribute of the XML
            // declaration within the document,
            if (TextUtils.isEmpty(ret)) {
                matcher = ContentTypePattern_MimeType.matcher(contentType);
                if (matcher.matches() && matcher.groupCount() >= 2) {
                    String mimetype = matcher.group(1);
                    if (!TextUtils.isEmpty(mimetype)) {
                        mimetype = mimetype.toLowerCase();
                        if (mimetype.startsWith("application/")
                                && (mimetype.startsWith("application/xml") || mimetype
                                        .endsWith("+xml"))) {
                            InputStream responseStream = null;
                            try {
                                responseStream = conn.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        responseStream));
                                String aLine;
                                while ((aLine = reader.readLine()) != null) {
                                    aLine = aLine.trim();
                                    if (aLine.length() == 0)
                                        continue;

                                    matcher = ContentTypePattern_XmlEncoding.matcher(aLine);
                                    if (matcher.matches() && matcher.groupCount() >= 3) {
                                        String charset = matcher.group(2);
                                        if (!TextUtils.isEmpty(charset)) {
                                            ret = charset;
                                            Log.v(LogTag, "XML charset detected is: " + ret);
                                        }
                                    }
                                    break;
                                }
                            } finally {
                                if (responseStream != null)
                                    responseStream.close();
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    public static final Pattern ContentTypePattern_MimeType = Pattern.compile("([^\\s;]+)(.*)");

    public static final Pattern ContentTypePattern_Charset = Pattern.compile(
            "(.*?charset\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)", Pattern.CASE_INSENSITIVE);

    public static final Pattern ContentTypePattern_XmlEncoding = Pattern.compile(
            "(\\<\\?xml\\s+.*?encoding\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)",
            Pattern.CASE_INSENSITIVE);

    public static InputStream getHttpPostAsStream(URL url, String data,
            Map<String, String> headers, String userAgent, String cookie) throws IOException {
        if (null == url)
            throw new IllegalArgumentException("url");

        URL newUrl = url;

        InputStream responseStream = null;
        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-agent", userAgent);
        }

        if (!TextUtils.isEmpty(cookie)) {
            conn.setRequestProperty("Cookie", cookie);
        }

        conn.getOutputStream().write(data.getBytes());
        conn.getOutputStream().flush();
        conn.getOutputStream().close();

        String responseCode = conn.getResponseCode() + "";
        headers.put("ResponseCode", responseCode);

        for (int i = 0;; i++) {
            String name = conn.getHeaderFieldKey(i);
            String value = conn.getHeaderField(i);
            if (name == null && value == null) {
                break;
            }
            headers.put(name, value);

        }
        responseStream = conn.getInputStream();
        return responseStream;
    }

    public static HttpHeaderInfo getHttpHeaderInfo(String urlString, String userAgent, String cookie) {
        try {
            URL url = new URL(urlString);
            if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
                // this is not a http protocol, return
                return null;
            }
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (urlString.indexOf("wap") == -1) {
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
            } else {
                // this is suspected as a wap site,
                // let's wait for the result a little longer
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
            }
            if (!TextUtils.isEmpty(userAgent)) {
                conn.setRequestProperty("User-agent", userAgent);
            }

            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            }

            HttpHeaderInfo ret = new HttpHeaderInfo();
            ret.ResponseCode = conn.getResponseCode();

            ret.UserAgent = userAgent;
            for (int i = 0;; i++) {
                String name = conn.getHeaderFieldKey(i);
                String value = conn.getHeaderField(i);
                if (name == null && value == null) {
                    break;
                }
                if (name != null && name.equals("content-type")) {
                    ret.ContentType = value;
                }

                if (name != null && name.equals("location")) {
                    URI uri = new URI(value);
                    if (!uri.isAbsolute()) {
                        URI baseUri = new URI(urlString);
                        uri = baseUri.resolve(uri);
                    }
                    ret.realUrl = uri.toString();
                }
            }
            return ret;
        } catch (MalformedURLException e) {
            Log.e(LogTag, "Failed to transform URL", e);
        } catch (IOException e) {
            Log.e(LogTag, "Failed to get mime type", e);
        } catch (URISyntaxException e) {
            Log.e(LogTag, "Failed to parse URI", e);
        }
        return null;
    }

    public static class HttpHeaderInfo {
        public int ResponseCode;

        public String ContentType;

        public String UserAgent;

        public String realUrl;

        public Map<String, String> AllHeaders;
    }

    /**
     * 向服务端提交HttpPost请求 设置为5秒钟连接超时，发送数据不超时；
     *
     * @param url: HTTP post的URL地址
     * @param nameValuePairs: HTTP post参数
     * @return: 如果post
     *          response代码不是2xx，表示发生了错误，返回null。否则返回服务器返回的数据（如果服务器没有返回任何数据，返回""）；
     * @throws IOException: 调用过程中可能抛出到exception
     */
    public static String doHttpPost(Context context, String url, List<NameValuePair> nameValuePairs)
    throws IOException {
        return doHttpPost(context, url, nameValuePairs, null, null, null);
    }

    public static String doHttpPost(Context context, String url, List<NameValuePair> nameValuePairs,
            Map<String, String> headers, String userAgent, String cookie) throws IOException {
        if (TextUtils.isEmpty(url))
            throw new IllegalArgumentException("url");

        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        if (!TextUtils.isEmpty(userAgent)) {
            httpParameters.setParameter("User-agent", userAgent);
        }

        if (!TextUtils.isEmpty(cookie)) {
            httpParameters.setParameter("Cookie", cookie);
        }
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost;
        if (isCmwap(context)) {
            URL _url = new URL(url);
            String cmwapUrl = getCMWapUrl(_url);
            String host = _url.getHost();
            httppost = new HttpPost(cmwapUrl);
            httppost.addHeader(CMWAP_HEADER_HOST_KEY, host);
        } else {
            httppost = new HttpPost(url);
        }

        if (null != nameValuePairs && nameValuePairs.size() != 0)
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);
        int statusCode = response.getStatusLine().getStatusCode();
        Log.d(LogTag, "Http POST Response Code: " + statusCode);
        if (statusCode >= 200 && statusCode < 300) { // Http: 2xx（成功）

            // put http headers into the map
            if (headers != null) {
                Header _headers[] = response.getAllHeaders();
                for (int i = 0; i < _headers.length; i++) {
                    headers.put(_headers[i].getName(), _headers[i].getValue());
                }
            }
            HttpEntity body = response.getEntity();
            if (null != body) {
                String result = EntityUtils.toString(body);

                if (null != result)
                    return result;
            }

            return "";
        }

        return null;
    }

    /**
     * 向服务端提交HttpGet请求 设置为8秒钟连接超时，发送数据超时15秒；
     *
     * @param url: HTTP get的URL地址
     * @return: 如果post
     *          response代码不是2xx，表示发生了错误，返回null。否则返回服务器返回的数据；
     * @throws URISyntaxException, ClientProtocolException, IOException: 调用过程中可能抛出到exception
     */
    public static InputStream doHttpGet(final String strUrl)
    throws URISyntaxException, ClientProtocolException, IOException {
        return doHttpGet(strUrl, 8000, 15000);
    }

    public static InputStream doHttpGet(final String strUrl, int connTimeOut, int soTimeOut)
    throws URISyntaxException, ClientProtocolException, IOException {
        if (!(URLUtil.isHttpUrl(strUrl) || URLUtil.isHttpsUrl(strUrl))) {
            return null;
        }

        InputStream instream = null;

        HttpGet httpRequest = null;
        URL lrcUrl = new URL(strUrl);
        httpRequest = new HttpGet(lrcUrl.toURI());
        httpRequest.setHeader("User-agent", UserAgent_PC_Chrome);

        HttpParams httpParameters = new BasicHttpParams();
        if (connTimeOut > 0) {
            HttpConnectionParams.setConnectionTimeout(httpParameters, connTimeOut);
        }
        if (soTimeOut > 0) {
            HttpConnectionParams.setSoTimeout(httpParameters, soTimeOut);
        }
        HttpClient httpclient = new DefaultHttpClient(httpParameters);

        HttpResponse response = httpclient.execute(httpRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                instream = bufHttpEntity.getContent();
            }
        }

        return instream;
    }

    public static String concatAsUrl(String url, Map<String, String> nameValues) {
        if (nameValues == null || nameValues.isEmpty()) {
            return url;
        }

        final StringBuilder sb = new StringBuilder(url);
        final Iterator<Entry<String, String>> iter = nameValues.entrySet().iterator();
        while(iter.hasNext()) {
            final Entry<String, String> entry = iter.next();
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(Network.encode(entry.getValue()));
            sb.append("&");
        }

        final int len = sb.length();
        if (len > 0) {
            sb.deleteCharAt(len - 1);
        }

        return sb.toString();
    }

    /**
     * @param strUrl 要加密的URL string
     * @return 获取加密后的URL string
     */
    public static String encryptURL(String strUrl) {
        if (!TextUtils.isEmpty(strUrl)) {
            new String();
            String strTemp = String.format("%sbe988a6134bc8254465424e5a70ef037", strUrl);
            return String.format("%s&key=%s", strUrl, MD5.MD5_32(strTemp));
        }
        return null;
    }

    public interface PostDownloadHandler {
        void OnPostDownload(boolean sucess);
    }

    /**
     * 开始下载远程文件到指定输出流
     *
     * @param url 远程文件地址
     * @param output 输出流
     * @param handler 下载成功或者失败的处理
     */
    public static void beginDownloadFile(String url, OutputStream output,
            PostDownloadHandler handler) {
        DownloadTask task = new DownloadTask(url, output, handler);
        task.execute();
    }

    /**
     * 下载远程文件到指定输出流
     *
     * @param url 远程文件地址
     * @param output 输出流
     * @return 成功与否
     */
    public static boolean downloadFile(String urlStr, OutputStream output) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.connect();
            InputStream input = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int count;

            while ((count = input.read(buffer)) > 0) {
                output.write(buffer, 0, count);
            }

            input.close();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String uploadFile(String url, File file, String fileKey) throws IOException {

        if (!file.exists()) {
            return null;
        }
        String filename = file.getName();

        HttpURLConnection conn = null;

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";

        FileInputStream fileInputStream = null;
        DataOutputStream dos = null;
        BufferedReader rd = null;

        try {
            URL _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(5000);

            // Allow Inputs
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            final int EXTRA_LEN = 77; // 除去文件名和文件内容之外，所有内容的length
            int len = EXTRA_LEN + filename.length() + (int) file.length() + fileKey.length();
            conn.setFixedLengthStreamingMode(len);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + fileKey +"\";filename=\""
                    + file.getName() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // read file and write it into form...
            fileInputStream = new FileInputStream(file);
            int bytesRead = -1;
            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
                dos.flush();
            }
            // send multi-part form data necessary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens);
            dos.writeBytes(boundary);
            dos.writeBytes(twoHyphens);
            dos.writeBytes(lineEnd);

            // flush streams
            dos.flush();
            StringBuffer sb = new StringBuffer();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (rd != null) {
                    rd.close();
                }
            } catch (IOException e) {
                Log.e(LogTag, "error while closing strean", e);
            }
        }
    }

    private static class DownloadTask extends AsyncTask<Void, Void, Boolean> {
        private String url;

        private OutputStream output;

        private PostDownloadHandler handler;

        public DownloadTask(String urlstr, OutputStream outputstr, PostDownloadHandler handlerstr) {
            this.url = urlstr;
            this.output = outputstr;
            this.handler = handlerstr;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return Network.downloadFile(this.url, this.output);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            this.handler.OnPostDownload(result);
        }
    }
    
	private static NetworkInfo getActiveNetworkInfo(Context context) {
		if (context == null) {
			return null;
		}
		ConnectivityManager connectiveManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectiveManager == null) {
			return null;
		}
		return connectiveManager.getActiveNetworkInfo();
	}
	
	public static boolean isNetworkConncected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnected();
	}
	
	public static boolean isWifiConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

    public static int getActiveNetworkType(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info == null) {
            return -1;
        }
        return info.getType();
    }

    public static boolean isActive(Context context) {
        return getActiveNetworkType(context) >= 0;
    }

    public static String getActiveNetworkName(final Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info == null) {
            return "null";
        }
        if (TextUtils.isEmpty(info.getSubtypeName())) {
            return info.getTypeName();
        }
        return String.format("%s-%s", info.getTypeName(), info.getSubtypeName());
    }

    public static boolean isWifi(Context context) {
        return (getActiveNetworkType(context) == ConnectivityManager.TYPE_WIFI);
    }
   
    public static boolean isActiveNetworkMetered(Context context) {
//       ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//       return cm.isActiveNetworkMetered();
        return false;
        // by tfling
    }

    public static boolean isCmwap(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info == null) {
            return false;
        }
        String extraInfo = info.getExtraInfo();
        if (TextUtils.isEmpty(extraInfo) || (extraInfo.length() < 3)) {
            return false;
        }
        return extraInfo.regionMatches(true, extraInfo.length() - 3, "wap", 0, 3);
    }

    private static HttpURLConnection getHttpUrlConnection(Context context, URL url) throws IOException {
        if (!isCmwap(context)) {
            return (HttpURLConnection) url.openConnection();
        }
        String host = url.getHost();
        String cmwapUrl = getCMWapUrl(url);
        URL gatewayUrl = new URL(cmwapUrl);
        HttpURLConnection conn = (HttpURLConnection) gatewayUrl.openConnection();
        conn.addRequestProperty(CMWAP_HEADER_HOST_KEY, host);
        return conn;
    }

    private static String getCMWapUrl(URL oriUrl) {
        StringBuilder gatewayBuilder = new StringBuilder();
        gatewayBuilder.append(oriUrl.getProtocol())
        .append("://")
        .append(CMWAP_GATEWAY)
        .append(oriUrl.getPath());
        if (!TextUtils.isEmpty(oriUrl.getQuery())) {
            gatewayBuilder.append("?").append(oriUrl.getQuery());
        }
        return gatewayBuilder.toString();
    }

    public static String encode(String src) {
        if (src != null) {
            try {
                return URLEncoder.encode(src, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }

        return "";
    }
}
