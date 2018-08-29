package cn.muye.version.util;

import cn.mrobot.bean.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by chay on 2018/8/25.
 */
@Slf4j
public class PingUtil {

    /**
     * 判断网络是不是通
     *
     * @param hostIp
     * @param logName
     * @return
     */
    public static boolean isConnect(String hostIp, String logName) throws Exception {
        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            log.info("[{}]开始ping:{}", logName, hostIp);
            String osName = System.getProperty("os.name");//获取操作系统类型
            String pingCommand = "";
            //超时时间ms
            int timeOut = 500;
            int count = 2;
            if (osName.toLowerCase().contains("linux")) {
                pingCommand = "ping -c " + count + " " + hostIp;
            } else {
                pingCommand = "ping " + hostIp + " -n " + count + " -w " + timeOut;
            }
            process = runtime.exec(pingCommand);
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("[{}]返回值为:{}", logName, sb);

            if (null != sb && !sb.toString().equals("")) {
                if (sb.toString().toLowerCase().indexOf(Constant.NET_TTL) > 0) {
                    // 网络畅通
                    log.info("[{}]ping:{}网络畅通", logName, hostIp);
                    connect = true;
                } else {
                    // 网络不畅通
                    log.info("[{}]ping:{}网络异常", logName, hostIp);
                    connect = false;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
            }
            return connect;
        }
    }

    /**
     * 发送请求前，先测试网络连接，使用get方法请求
     *
     * @param restTemplate
     * @param serverUrl
     * @param requestUrl
     * @param responseType
     * @param uriVariables
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T connectAndCheckFirstUseGet(RestTemplate restTemplate,
                                                   String serverUrl,
                                                   String logName,
                                                   String requestUrl, Class<T> responseType, Object... uriVariables) throws Exception {
        if (restTemplate == null) {
            log.info("restTemplate is Null");
            return null;
        }
        if (!RosConnectUtil.isHostAvailablePingFirst(new URI(serverUrl), logName)) {
            return null;
        }
        return restTemplate.getForObject(requestUrl, responseType, uriVariables);
    }

    /**
     * 发送请求前，先测试网络连接，使用post方法请求
     *
     * @param restTemplate
     * @param serverUrl
     * @param requestUrl
     * @param responseType
     * @param uriVariables
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T connectAndCheckFirstUsePost(RestTemplate restTemplate,
                                                    String serverUrl,
                                                    String logName,
                                                    String requestUrl, Object request, Class<T> responseType, Object... uriVariables) throws Exception {
        if (restTemplate == null) {
            log.info("restTemplate is Null");
            return null;
        }
        if (!RosConnectUtil.isHostAvailablePingFirst(new URI(serverUrl), logName)) {
            return null;
        }
        return restTemplate.postForObject(requestUrl, request, responseType, uriVariables);
    }

    /**
     * 使用jar包自带的ping方法
     * @param ipAddress
     * @param logName
     * @return
     * @throws Exception
     */
    public static boolean ping(String ipAddress, String logName) throws Exception {
        int timeOut = 2000;  //超时应该在3钞以上
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
        log.info("[{}]ping:{} {}",logName, ipAddress, status);
        return status;
    }

    public static void ping02(String ipAddress) throws Exception {
        String line = null;
        try {
            Process pro = Runtime.getRuntime().exec("ping " + ipAddress);
            BufferedReader buf = new BufferedReader(new InputStreamReader(
                    pro.getInputStream()));
            while ((line = buf.readLine()) != null)
                System.out.println(line);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes + " -w " + timeOut;
        try {   // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return connectedCount == pingTimes;
        } catch (Exception ex) {
            ex.printStackTrace();   // 出现异常则返回假
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);
        Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        try {
            PingUtil.isConnect("172.16.1.81", Constant.NET_CLOUD_SERVER);
            RosConnectUtil.isHostAvailablePingFirst(new URI("http://172.16.1.81:8063"), Constant.NET_CLOUD_SERVER);
            PingUtil.ping("172.16.1.51", "test");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
