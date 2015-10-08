package com.senao.utility;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;

import com.senao.utility.StringUtility;

public class HttpsClient
{
	public HttpsClient()
	{

	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
	}

	private SSLSocketFactory buildSSLSocketFactory() throws Exception
	{
		TrustStrategy ts = new TrustStrategy()
		{
			// @Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
			{
				return true; // heck yea!
			}
		};

		SSLSocketFactory sf = null;
		/* build socket factory with hostname verification turned off. */
		sf = new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return sf;
	}

	private void configureSSLHandling(HttpClient hc) throws Exception
	{
		Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		SSLSocketFactory sf = buildSSLSocketFactory();
		Scheme https = new Scheme("https", 443, (SchemeSocketFactory) sf);
		SchemeRegistry sr = hc.getConnectionManager().getSchemeRegistry();
		sr.register(http);
		sr.register(https);
	}

	private HttpClient buildHttpClient() throws Exception
	{
		HttpClient hc = new DefaultHttpClient();
		configureSSLHandling(hc);
		return hc;
	}

	public String sendPost(String url, Map<String, String> parm) throws Exception
	{

		String result = "";
		HttpClient client = null;
		client = buildHttpClient();
		HttpPost post = new HttpPost(url);

		if (parm.size() > 0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Entry<String, String> item : parm.entrySet())
			{
				params.add(new BasicNameValuePair(item.getKey(), item.getValue()));
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
			post.setEntity(ent);
		}

		HttpResponse responsePOST = client.execute(post);
		HttpEntity resEntity = responsePOST.getEntity();

		if (resEntity != null)
		{
			result = EntityUtils.toString(resEntity);
		}

		client.getConnectionManager().shutdown();

		if(StringUtility.isValid(result))
		{
			result.trim();
		}
		else
		{
			result = "";
		}
		return result;
	}

	public String sendGet(String url) throws Exception
	{
		String result = "";
		HttpClient client = null;
		try
		{
			client = buildHttpClient();

			HttpGet get = new HttpGet(url);

			HttpResponse response = client.execute(get);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null)
			{
				result = EntityUtils.toString(resEntity);
			}

		}
		catch (Exception e)
		{
			throw new Exception(e.toString());
		}
		finally
		{
			client.getConnectionManager().shutdown();
		}

		if(StringUtility.isValid(result))
		{
			result.trim();
		}
		else
		{
			result = "";
		}
		return result;
	}

	public static String UrlEncode(final String strText)
	{
		try
		{
			return URLEncoder.encode(StringUtility.convertNull(strText), StandardCharsets.UTF_8.toString());
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
