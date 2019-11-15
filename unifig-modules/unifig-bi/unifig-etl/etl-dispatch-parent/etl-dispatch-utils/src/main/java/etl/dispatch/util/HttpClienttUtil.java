package  etl.dispatch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;




public class HttpClienttUtil {


	
	
	public static String httpGet(String url) {
		
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse resp = client.execute(httpGet);
			
			HttpEntity entity = resp.getEntity();
	        String respContent = EntityUtils.toString(entity , "UTF-8").trim();
	        httpGet.abort();
	        

	        return respContent;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			client.getConnectionManager().shutdown();
		}
	}
	
	
	

	
	public static String httpPost(String url, Map<String, Object> params) throws ClientProtocolException, IOException {
		String respContent=null;
		HttpClient client = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(url);
			
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());
			for(Map.Entry<String, Object> entry : params.entrySet()){
				NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
				valuePairs.add(nameValuePair);
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
			httpPost.setEntity(formEntity);
			HttpResponse resp = client.execute(httpPost);
			
			HttpEntity entity = resp.getEntity();
			respContent = EntityUtils.toString(entity , "UTF-8").trim();
	        httpPost.abort();
		
		}finally{
			client.getConnectionManager().shutdown();
		}
		return respContent;
}
	
	public static void main(String args[]){
		
	
	}
	

}
