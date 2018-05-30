import com.google.gson.Gson;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import entity.LoginJson;
import jdk.nashorn.internal.parser.JSONParser;
import okhttp3.*;
import sun.plugin2.message.CookieReplyMessage;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSwuLogin {

    private static final String loginInfo = "ijKXahPeHcjAPKNHq6CI1XDXDKAy+huszQRdnfcJC/cSztG6ACiJQnxmFVSHafFYQrvA+tKrJt/LK2uEQgCU8hitys1HAOzHYIMxB96zhhhaJOEtkpNBpMKlZi7hOcYzqlph4CkwVqo3VnFTtgDoan4drg9S5aeGS2QZghqZUVo=,N1xagGBs7ADdVTViSZvtFFJ5F4Bc3+nnYb5dU4IDNI4A3dtCtkHmYUrSYC6YqmxoaXUdEyI2AD8e2HKpLI4d5/zqJ1qAK3s2C8guP6Cz/DBeRbudon4pturcRyc1u9pYsm3ylluYU6J3QrdTclqh4HMh06zEzcAX+CwdADCT0/w=,H7bFPcOIzUD+8xLvwRkw5pxCeBVTvX5ZsG21GnKVvZ0DQGqh/FKeeads+kHoeVfgCcknRp443Tw/MOF/ePhB+VHxPsubG2cWUNoJgh/+fYQbmrGgB7b7ErdR/N7WhvV9Hqr1P5vNB+og+b1Lt1XWmShhWrE+FNVm5Ny5ISxAQpk=,MmhwZ1LNZ5jh5IbW8lIwSbs229MC+/i91fmhx326G0LMd/Xq/tJca64bWltLS8vPczSj1Hu5oFAW1Im85bpakldbPhnO/rT2GiGANDoUsXPziwP8t0eG7pSrGOjHCXBm826qp/2ee20Ke8CWTntycBPj7R9oW09YmNyhod2cMXk=,K/7AT36POV8+OIVf2UuUAM9TtKBZdNIwhE7Zx9Gdspn9xquMG10ildnPwYXUsnIpQVTF0dI4Y/XlvPaBLT5ulbXxNyRxb0nlBr3j1b22FKeJ+566PsE82NfBWxSZsuTgNOxlTlKnSUi/ZvMjDxwOmyXTiZOwZA+Tmm/qErcvV+o=";
    private static String tgt;
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
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
    private static CookieJar cookieJar = new CookieJar() {
        HashMap<String, List<Cookie>> cookieMap = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            System.out.println(httpUrl.host());
            for (Cookie cookie : list) {
                System.out.println("------"+cookie.toString());
            }
            if (cookieMap.get(httpUrl.host()) != null) {
                List<Cookie> cookies = new ArrayList<>();
                cookies.addAll(list);
                cookies.addAll(cookieMap.get(httpUrl.host()));
                cookieMap.put(httpUrl.host(), cookies);

            } else cookieMap.put(httpUrl.host(), list);

        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            if (cookieMap.get(httpUrl.host()) == null) {
                return new ArrayList<>();
            } else {
                System.out.println(httpUrl.host()+"==="+ cookieMap.get(httpUrl.host()));
                return cookieMap.get(httpUrl.host());
            }
        }
    };

    public static void main(String[] args) {
        loginISWU();
        loginILib();
        loginMyLib();
    }

    private static void loginISWU() {
        OkHttpClient.Builder ocb = new OkHttpClient.Builder();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888));
        ocb.sslSocketFactory(trustAllSslSocketFactory);
        ocb.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        ocb.proxy(proxy);
        OkHttpClient okHttpClient = ocb.build();
        Request.Builder builder = new Request.Builder();
        FormBody.Builder fb = new FormBody.Builder();
        fb.add("serviceInfo", loginInfo);
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.header("X-Requested-With", "XMLHttpRequest");
        builder.url("http://i.swu.edu.cn/remote/service/process").post(fb.build());
        Request request = builder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            Gson gson = new Gson();
            LoginJson loginJson = gson.fromJson(response.body().string(), LoginJson.class);
            tgt = loginJson.getData().getGetUserInfoByUserNameResponse().getReturnX().getInfo().getAttributes().getTgt();
            String cookie = String.format("CASTGC=\"%s\"; rtx_rep=no", new String(Base64.decode(tgt)));
            String cookie1=response.header("Set-Cookie");

            List<Cookie> cookies = new ArrayList<>();
            cookies.add(Cookie.parse(HttpUrl.get(new URL("https://uaaap.swu.edu.cn")), cookie));
            cookies.add(Cookie.parse(HttpUrl.get(new URL("https://uaaap.swu.edu.cn")), cookie1));
            cookieJar.saveFromResponse(HttpUrl.get(new URL("https://uaaap.swu.edu.cn")), cookies);

            System.out.println(tgt + "---" + cookie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loginILib() {
        OkHttpClient.Builder ocb = new OkHttpClient.Builder();
        ocb.cookieJar(cookieJar);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888));
        ocb.sslSocketFactory(trustAllSslSocketFactory);
        ocb.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        ocb.proxy(proxy);
        OkHttpClient okHttpClient = ocb.build();
        Request.Builder builder = new Request.Builder();
        builder.header("Referer","http://202.202.121.3:8080/opac/search/simsearch?ticket=4c3400ade9174c83b3d83e3fe9093b03");
        builder.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
        builder.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.url("http://202.202.121.3:8080/opac/login?locale=zh_CN").get();
        Request request = builder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loginMyLib() {
        OkHttpClient.Builder ocb = new OkHttpClient.Builder();
        ocb.cookieJar(cookieJar);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888));
        ocb.sslSocketFactory(trustAllSslSocketFactory);
        ocb.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        ocb.proxy(proxy);
        OkHttpClient okHttpClient = ocb.build();
        Request.Builder builder = new Request.Builder();
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.header("X-Requested-With", "XMLHttpRequest");
        builder.url("http://202.202.121.3:8080/opac/mylibrary").get();
        Request request = builder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

