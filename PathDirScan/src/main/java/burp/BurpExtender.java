package burp;

import burp.ScanPathUI.ScanPathUI;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import java.awt.*;
import java.io.Console;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BurpExtender implements IBurpExtender,ITab,IHttpListener {

    public PrintWriter stdout;
    private ScanPathUI scanPathUI;
    public IExtensionHelpers helpers;
    private IBurpExtenderCallbacks pub_callbacks;
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        callbacks.setExtensionName("PathDirScan");
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
//        实例UI
        scanPathUI = new ScanPathUI(callbacks);
        callbacks.addSuiteTab(this);
        callbacks.printOutput("Load ddv tool ok...");
        stdout.println("ok");
        callbacks.registerHttpListener(this);

        this.pub_callbacks = callbacks;



    }

    @Override
    public String getTabCaption() {
        return "PathDirScan";
    }

    @Override
    public Component getUiComponent() {

//        return dataCollectGUI.$$$getRootComponent$$$();
        return scanPathUI.$$$getRootComponent$$$();
    }
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
//        if(toolFlag == IBurpExtenderCallbacks.TOOL_PROXY) return;

        // 打开开启状态后，进行测试
        // 打开发送状态
        if(Config.open_connect == 0 || Config.open_send_state == 0){
            return;
        }

        if(messageIsRequest){

            IHttpService ihs = messageInfo.getHttpService();
            String now_host = ihs.getHost();

            IRequestInfo requestInfo = helpers.analyzeRequest(messageInfo.getRequest());

            // 对白名单的host进行校验
            //  如果是 * 则表示需要所有的host，否则只是需要符合的host

            stdout.println(11);
            if (Config.bai_ming_dans.trim().length() == 0){
                return;
            }
            stdout.println(22);
            if (!Config.bai_ming_dans.equals("*")){

                String[] baimingdans = Config.bai_ming_dans.split(",");
                if (Arrays.stream(baimingdans).filter(baimingdan -> now_host.indexOf(baimingdan) != -1).count() == 0){
                    return;
                }
            }
            stdout.println(33);

            // 获取当前cookie
            String cookie = "";
            // 开启发送cookie，根据每个请求 动态发送cookie
            if (Config.open_send_cookie_state == 1 && Config.fixe_cookie_state == 0){
                String[] cookies  = (String[]) requestInfo.getHeaders().stream().filter(header -> header.toUpperCase().indexOf("COOKIE") != -1).toArray();
                if (cookies.length > 0){
                    cookie = cookies[0];
                }
                // 开发发送cookie，并设置固定的cookie值
            }else if (Config.open_send_cookie_state == 1&& Config.fixe_cookie_state == 1){
                cookie = scanPathUI.get_ui_cookie();
            }// 其他情况为不发送cookie值



            // 获取当前的url
            String pro = ihs.getProtocol();
            String[] paths =requestInfo.getHeaders().get(0).split(" ")[1].split("/");
            if (paths.length == 0){
                paths = new String[1];
                paths[0] = "";
            }
            stdout.println("paths");
            stdout.println(paths.length);
            String url = pro + "://" + now_host;
            for (int i = 0; i < paths.length ; i++) {
                String path = paths[i];
                stdout.println("path");
                stdout.println(path);
                if ( url.substring(url.length() - 1).equals("/")){

                    url = url + path;
                }else{

                    url = url + "/" + path;
                }

                // 判断当前url是否已经存在缓存数据中，如果不存在则进行添加到缓存中去。
                // 同时将数据发送到服务端 待进行扫描
                if (!Config.scan_urls.contains(url)){
                    Config.scan_urls.add(url);

                    // 使用异步的方式 发送数据
                    JSONObject info = new JSONObject();
                    info.element("url",url);
                    info.element("cookie",cookie);
                    info.element("project_name",Config.project_name);
                    info.element("key",Config.api_key);
                    new DDVTool().send_info(stdout,info);

                    stdout.println(info.toString());
                    // 将值添加到list中
                    scanPathUI.add_list_item(url);
                }
            }






        }

    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
