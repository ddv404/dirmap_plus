package burp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    // 服务器的api
    public static String target_url = "http://127.0.0.1:10011";
    // 认证的key
    public static String api_key = "ddv";
    // 项目名称 用于检索的唯一标识
    public static String project_name = "project";
    // 过滤地址的白名单
    public static String bai_ming_dans = "*";


    // 用于标识已经经过请求测试链接，可以开始连接了
    public static int open_connect = 0;

    // 用于表示是否开启发送状态
    public static int open_send_state = 0;

    // 是否开启cookie值
    public static int open_send_cookie_state = 0;

    // 是否设置固定cookie
    public static int fixe_cookie_state = 0;

    // 存储所有的url
    public static List<String> scan_urls = new ArrayList<String>();
}
