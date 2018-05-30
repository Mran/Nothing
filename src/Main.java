

import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class Main {
    private static final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static final SSLContext trustAllSslContext;
    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();
    public static void main(String[] args) {

        Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",8888));
        OkHttpClient client = new OkHttpClient().newBuilder().sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]).proxy(proxy).build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("","").add("text", "你好").add("from", "zh-CN").add("to", "en");
        RequestBody requestBody = builder.build();

Headers headers=new Headers.Builder().add("Accept-Language: zh-CN,zh;q=0.9,ja;q=0.8")
        .add("Accept-Encoding: gzip, deflate, br")
        .add("Referer: https://www.bing.com/")
        .add("Accept-Language: zh-CN,zh;q=0.9,ja;q=0.8")
        .add("DNT: 1")
        .add("Accept: */*")
        .add("Content-type: application/x-www-form-urlencoded")
        .add("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.162 Safari/537.36")
        .add("Origin: https://www.bing.com")
        .add("Connection: keep-alive").build();
        Request.Builder Requestbuilder = new Request.Builder();
        Request request = Requestbuilder.url("https://www.bing.com/ttranslationlookup?&IG=AFD6772E01B24FD39B0E336CAB357227&IID=translator.5035.2")

                .headers(headers)
                .post(requestBody).build();
        Response responseBody = null;
        try {
            responseBody = client.newCall(request).execute();
            ResponseBody responseBody1 = responseBody.body();
            System.out.println(responseBody1.string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
