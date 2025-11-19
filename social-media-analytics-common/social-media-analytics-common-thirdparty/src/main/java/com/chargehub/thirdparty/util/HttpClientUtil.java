package com.chargehub.thirdparty.util;

import com.chargehub.common.core.utils.HtjsApiSdkClient;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:38
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.util
 * @Filename：HttpClientUtil
 */
@Component
public class HttpClientUtil implements ApplicationContextAware {

    private static HtjsApiSdkClient hsc;


    public static String doGet(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param, Map<String, String> headerParam) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            //如果header内参数不为空  则设置header内参数
            if (headerParam != null) {
                for (String key : headerParam.keySet()) {
                    httpPost.setHeader(key, headerParam.get(key));
                }
            }

            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null, null);
    }

    public static String doPost(String url, Map<String, String> param) {
        return doPost(url, param, null);
    }

    public static String doPostJson(String url, String json, Map<String, String> headerParam) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            //如果header内参数不为空  则设置header内参数
            if (headerParam != null) {
                for (String key : headerParam.keySet()) {
                    httpPost.setHeader(key, headerParam.get(key));
                }
            }

            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }


    public static String doPostJson(String url, String json) {
        return doPostJson(url, json, null);
    }


    /**
     * 上传文件
     *
     * @param file
     * @param keyName
     * @param url
     * @return
     * @throws Exception
     */
    public static String uploading(MultipartFile file, String keyName, String url) throws Exception {
        //CloseableHttpClient意思是：可关闭的
        CloseableHttpClient httpClient = HttpClients.createDefault();//1、创建实例
        HttpPost uploadFile = new HttpPost(url);//2、创建请求

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.addTextBody("text", "111", ContentType.TEXT_PLAIN);//传参
        builder.setCharset(Charset.forName("utf8"));//设置请求的编码格式
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式

        // 把文件加到HTTP的post请求中
        builder.addBinaryBody(
                keyName,
                new ByteArrayInputStream(file.getBytes()),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);//对于HttpPost对象而言，可调用setEntity(HttpEntity entity)方法来设置请求参数。
        CloseableHttpResponse response = httpClient.execute(uploadFile);//3、执行
        HttpEntity responseEntity = response.getEntity();//4、获取实体

        Header header = responseEntity.getContentType();
        //打印内容
        String sResponse = EntityUtils.toString(responseEntity, "UTF-8");//5、获取网页内容，并且指定编码
        System.out.println("Post 返回结果" + sResponse);
        httpClient.close();
        response.close();
        return sResponse;
    }


    /**
     * x-www-form-urlencoded传参
     *
     * @param url
     * @param map
     * @return
     */
    public static String doPostUrlEncoder(String url, Map<String, String> map) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpResponse response = null;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpClient httpClient = HttpClientBuilder.create().build();
            response = httpClient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 发送POST请求
     *
     * @param url 请求地址
     * @return 响应结果
     * @throws IOException
     */
    public static String sendPostRequest(String url, String jsonBody, Map<String, String> headers) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // 设置连接主机超时（30s）
            connection.setConnectTimeout(30 * 1000);
            // 设置从主机读取数据超时（3min）
            connection.setReadTimeout(180 * 1000);

            // 设置自定义请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonBody.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                throw new IOException("请求失败，错误码：" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 发送GET请求
     *
     * @param url 请求地址
     * @return 响应结果
     * @throws IOException
     */
    public static String sendGetRequest(String url, Map<String, String> headers) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            // 设置header参数
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                throw new Exception("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 解析短连接
     *
     * @param shortLink
     * @return
     */
    public static Map<String, String> resolveShortLink(String shortLink) {
        Map<String, String> params = new HashMap<>();

        try {
            URL url = new URL(shortLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                String longLink = connection.getHeaderField("Location");
                URL longLinkUrl = new URL(longLink);
                String query = longLinkUrl.getQuery();
                String[] queryParts = query.split("&");

                for (String part : queryParts) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        params.put(key, value);
                    }
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }


    /**
     * 发送get请求 并且传json对象
     *
     * @param url
     * @param jsonBody
     * @return
     * @throws IOException
     */
    public static String sendGet(String url, String jsonBody) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(jsonBody.getBytes());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IOException("GET request with JSON body failed with response code: " + responseCode);
        }
    }

    public static String httpPostRequest(String url, String params, String ContentType) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(params, "utf-8");
            httpPost.setEntity(entity);
            if ("json".equals(ContentType)) {
                httpPost.setHeader("Content-type", "application/json");
            } else {
                httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            }
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //网关密钥

            Map header = hsc.get_signed_headers("POST", url);
            System.out.println("4、header数据：");

            Set keyset = header.keySet();
            Iterator it = keyset.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                httpPost.setHeader(key, (String) header.get(key));
                System.out.println("key值：" + key + "对应值：" + (String) header.get(key));
            }
            response = httpClient.execute(httpPost);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String result = "";
//        if (response.getStatusLine().getStatusCode() == 200) {
        try {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//        }

        try {
            httpClient.close();
            response.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HtjsApiSdkClient bean = applicationContext.getBean(HtjsApiSdkClient.class);
        HttpClientUtil.hsc = bean;
    }
}
