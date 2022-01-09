package burp;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class DDVTool {
    private static ExecutorService executor;
    public DDVTool(){
        executor = Executors.newFixedThreadPool(200);
    }
    public void send_info(PrintWriter stdout, JSONObject data){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    JSONObject respone_json = request_service(data.toString(),"add_scan_url");
                    if(respone_json.containsKey("status") && respone_json.get("status").equals("success") && respone_json.containsKey("message")){

                        if (data.getString("url").length() == 0){
                        }

                    }else{
                        stdout.println(data.toString());
                        stdout.println(respone_json.toString());
                    }
                } catch (Exception e) {
                    stdout.println(data.toString());
                    e.printStackTrace();
                }
                return "done task!";
            }
        }, this.executor);
    }

    public static String send_info_await(JSONObject data,String path, Callable okFun, Callable errFun){

                try {
                    JSONObject respone_json = request_service(data.toString(),path);
                    if(respone_json.containsKey("status") && respone_json.get("status").equals("success") && respone_json.containsKey("message")){
                        okFun.call();
                        return respone_json.getString("message");

                    }else{
                        errFun.call();
                        return respone_json.toString();
                    }
                } catch (Exception e) {
                    try {
                        errFun.call();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return e.toString();
                }

    }

    private static void request_err_write(String path, String message) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(path,true));
        bw.write(message);
        bw.newLine();
        bw.close();
    }
    public static JSONObject request_service(String message,String path) throws IOException {
        String turl = "";
        if(Config.target_url.substring(Config.target_url.length() - 1).equals("/")){
            turl = Config.target_url+ path;
        }else{
            turl = Config.target_url+ "/"+path;
        }
        URL url = new URL(turl);
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("encoding", "UTF-8");
        connection.addRequestProperty("content-type","application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(3000);
        ((HttpURLConnection) connection).setRequestMethod("POST");

        OutputStream os = connection.getOutputStream();
        OutputStreamWriter osr = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osr);

        bw.write(message);
        bw.flush();

        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is,"utf-8");
        BufferedReader br = new BufferedReader(isr);

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line+"\n");
        }

        bw.close();
        osr.close();
        os.close();

        br.close();
        isr.close();
        is.close();


        return JSONObject.fromObject(builder.toString());
//        return builder.toString();

    }
    public static String bp_content_type(byte index){

        switch (index){
            case IRequestInfo.CONTENT_TYPE_AMF:
                return "AMF";
            case IRequestInfo.CONTENT_TYPE_JSON:
                return "JSON";
            case IRequestInfo.CONTENT_TYPE_MULTIPART:
                return "MULTIPART";
            case IRequestInfo.CONTENT_TYPE_NONE:
                return "NONE";
            case IRequestInfo.CONTENT_TYPE_UNKNOWN:
                return "UNKNOWN";
            case IRequestInfo.CONTENT_TYPE_URL_ENCODED:
                return "ENCODED";
            case IRequestInfo.CONTENT_TYPE_XML:
                return "XML";
        }
        return "UNKNOWN";
    }
    public static String bp_tool_name(int toolFlag){

        switch (toolFlag){
            case IBurpExtenderCallbacks.TOOL_COMPARER:
                return "COMPARER";
            case IBurpExtenderCallbacks.TOOL_DECODER:
                return "DECODER";
            case IBurpExtenderCallbacks.TOOL_EXTENDER:
                return "EXTENDER";
            case IBurpExtenderCallbacks.TOOL_INTRUDER:
                return "INTRUDER";
            case IBurpExtenderCallbacks.TOOL_PROXY:
                return "PROXY";
            case IBurpExtenderCallbacks.TOOL_REPEATER:
                return "REPEATER";
            case IBurpExtenderCallbacks.TOOL_SCANNER:
                return "SCANNER";
            case IBurpExtenderCallbacks.TOOL_SEQUENCER:
                return "SEQUENCER";
            case IBurpExtenderCallbacks.TOOL_SPIDER:
                return "SPIDER";
            case IBurpExtenderCallbacks.TOOL_SUITE:
                return "SUITE";
            case IBurpExtenderCallbacks.TOOL_TARGET:
                return "TARGET";
        }
        return "UNKOWN";
    }
    public String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
//            System.exit(-1);
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }
}
