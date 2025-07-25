package cloud.xcan.angus.extension.sms.plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;

//如果JDK版本低于1.8,请使用三方库提供Base64类
//import org.apache.commons.codec.binary.Base64;

@Slf4j
public class HuaweiSmsSender {

  //无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
  private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";
  //无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
  private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

  public static String send(String url, String appKey, String appSecret, String sender,
      String templateId, String signature, String receiver, String templateParas) throws Exception {

  //    //必填,请参考"开发准备"获取如下数据,替换为实际值
  //    String url = "https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1"; //APP接入地址(在控制台"应用管理"页面获取)+接口访问URI
  //    String appKey = "c8RWg3ggEcyd4D3p94bf3Y7x1Ile"; //APP_Key
  //    String appSecret = "q4Ii87BhST9vcs8wvrzN80SfD7Al"; //APP_Secret
  //    String sender = "csms12345678"; //国内短信签名通道号或国际/港澳台短信通道号
  //    String templateId = "8ff55eac1d0b478ab3c06c3c6a492300"; //模板ID
  //
  //    //条件必填,国内短信关注,当templateId指定的模板类型为通用模板时生效且必填,必须是已审核通过的,与模板类型一致的签名名称
  //    //国际/港澳台短信不用关注该参数
  //    String signature = "华为云短信测试"; //签名名称
  //
  //    //必填,全局号码格式(包含国家码),示例:+8615123456789,多个号码之间用英文逗号分隔
  //    String receiver = "+86151****6789,+86152****7890"; //短信接收人号码

    //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
    String statusCallBack = "";

    /**
     * 选填,使用无变量模板时请赋空值 String templateParas = "";
     * 单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
     * 双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
     * 模板中的每个变量都必须赋值，且取值不能为空
     * 查看更多模板和变量规范:产品介绍>模板和变量规范
     */
    //String templateParas = "[\"369751\"]"; //模板变量，此处以单变量验证码短信为例，请客户自行生成6位验证码，并定义为字符串类型，以杜绝首位0丢失的问题（例如：002569变成了2569）。

    //请求Body,不携带签名名称时,signature请填null
    String body = buildRequestBody(sender, receiver, templateId, templateParas, statusCallBack,
        signature);
    if (null == body || body.isEmpty()) {
      throw new IllegalArgumentException("Body is null.");
    }

    return sendHttps(url, body, appKey, appSecret);
  }

  /**
   * 构造请求Body体
   *
   * @param signature | 签名名称,使用国内短信通用模板时填写
   */
  static String buildRequestBody(String sender, String receiver, String templateId,
      String templateParas,
      String statusCallBack, String signature) {
    if (null == sender || null == receiver || null == templateId || sender.isEmpty() || receiver
        .isEmpty() || templateId.isEmpty()) {
      return null;
    }
    Map<String, String> map = new HashMap<String, String>();

    map.put("from", sender);
    map.put("to", receiver);
    map.put("templateId", templateId);
    if (null != templateParas && !templateParas.isEmpty()) {
      map.put("templateParas", templateParas);
    }
    if (null != statusCallBack && !statusCallBack.isEmpty()) {
      map.put("statusCallback", statusCallBack);
    }
    if (null != signature && !signature.isEmpty()) {
      map.put("signature", signature);
    }

    StringBuilder sb = new StringBuilder();
    String temp = "";

    for (String s : map.keySet()) {
      try {
        temp = URLEncoder.encode(map.get(s), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      sb.append(s).append("=").append(temp).append("&");
    }

    return sb.deleteCharAt(sb.length() - 1).toString();
  }

  /**
   * 构造X-WSSE参数值
   */
  static String buildWsseHeader(String appKey, String appSecret) {
    if (null == appKey || null == appSecret || appKey.isEmpty() || appSecret.isEmpty()) {
      log.error("buildWsseHeader(): appKey or appSecret is null.");
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    String time = sdf.format(new Date()); //Created
    String nonce = UUID.randomUUID().toString().replace("-", ""); //Nonce

    MessageDigest md;
    byte[] passwordDigest = null;

    try {
      md = MessageDigest.getInstance("SHA-256");
      md.update((nonce + time + appSecret).getBytes());
      passwordDigest = md.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    //如果JDK版本是1.8,请加载原生Base64类,并使用如下代码
    String passwordDigestBase64Str = Base64.getEncoder()
        .encodeToString(passwordDigest); //PasswordDigest
    //如果JDK版本低于1.8,请加载三方库提供Base64类,并使用如下代码
    //String passwordDigestBase64Str = Base64.encodeBase64String(passwordDigest); //PasswordDigest
    //若passwordDigestBase64Str中包含换行符,请执行如下代码进行修正
    //passwordDigestBase64Str = passwordDigestBase64Str.replaceAll("[\\s*\t\n\r]", "");
    return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
  }

  /*** @throws Exception
   */
  static void trustAllHttpsCertificates() throws Exception {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] chain, String authType) {
            return;
          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain, String authType) {
            return;
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }
        }
    };
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  }

  static String sendHttps(String url, String body, String appKey, String appSecret)
      throws Exception {

    //请求Body为空
    if (null == body || body.isEmpty()) {
      throw new IllegalArgumentException("Body is null.");
    }
    //请求Headers中的X-WSSE参数值
    String wsseHeader = buildWsseHeader(appKey, appSecret);
    if (null == wsseHeader || wsseHeader.isEmpty()) {
      throw new IllegalArgumentException("Wsse header is null.");
    }

    StringBuffer result = new StringBuffer();

    HostnameVerifier hv = new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
    trustAllHttpsCertificates();

    URL realUrl = new URL(url);
    HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();

    try {
      connection.setHostnameVerifier(hv);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setUseCaches(true);
      //请求方法
      connection.setRequestMethod("POST");
      //请求Headers参数
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Authorization", AUTH_HEADER_VALUE);
      connection.setRequestProperty("X-WSSE", wsseHeader);

      connection.connect();
      
      // Use try-with-resources for proper resource management
      try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
        out.write(body); //发送请求Body参数
        out.flush();
      }

      int status = connection.getResponseCode();
      InputStream is = (200 == status) ? connection.getInputStream() : connection.getErrorStream();
      
      try (BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
        String line;
        while ((line = in.readLine()) != null) {
          result.append(line);
        }
      }
      
      return result.toString();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}
