package burp.ScanPathUI;

import burp.Config;
import burp.DDVTool;
import burp.IBurpExtenderCallbacks;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jdk.nashorn.internal.parser.JSONParser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.Callable;

public class ScanPathUI {
    private JPanel rootPanel;

    private JButton CONNECT;
    private JButton clean;
    private JRadioButton OPENRadioButton;
    private JRadioButton USECOOKIERadioButton;
    private JRadioButton FIXECOOKIERadioButton;

    private JTextField white_list_text;
    private JTextField cookie_text;
    private JTextField api_text;
    private JTextField key_text;
    private JTextField project_text;
    private JLabel project;
    private JLabel key;
    private JLabel api;
    private JLabel white_list;
    private static JLabel info;
    private JList list1;

    private DefaultListModel listModel;
    private JTextArea textArea1;
    private JPanel scan_url_jpanel;
    private JPanel result_url_jpanel;


    public ScanPathUI(IBurpExtenderCallbacks callbacks) {

        // 清空当前缓存
        clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config.scan_urls.clear();
                listModel.clear();
            }
        });
        // 进行测试请求
        CONNECT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //listModel.addElement("sdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsd");
                // 将UI中的值设置到程序中
                Config.target_url = api_text.getText();
                Config.api_key = key_text.getText();
                Config.project_name = project_text.getText();
                Config.bai_ming_dans = white_list_text.getText();

                // 还有其他值

                // 进行连接测试
                JSONObject info = new JSONObject();

                info.element("url", "");
                info.element("cookie", "");
                info.element("project_name", Config.project_name);
                info.element("key", Config.api_key);

                String result = DDVTool.send_info_await(info,"add_scan_url"
                        , new Callable() {
                            @Override
                            public Object call() throws Exception {
                                Config.open_connect = 1;
                                return null;
                            }
                        },
                        new Callable() {
                            @Override
                            public Object call() throws Exception {
                                Config.open_connect = 0;
                                return null;
                            }
                        });
                show_info(result);
                callbacks.printOutput(result);
//                show_info(Config.open_connect + "");

            }
        });
        // 设置是否开启发送cookie
        USECOOKIERadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 1表示选中，2表示取消选中
                if (e.getStateChange() == 1){
                    Config.open_send_cookie_state = 1;
                }else{
                    Config.open_send_cookie_state = 0;
                }
//                show_info(Config.open_send_cookie_state + "");
            }
        });
        // 设置是否设置固定cookie值
        FIXECOOKIERadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 1表示选中，2表示取消选中
                // 在开启发送cookie的情况下，设置固定cookie才有效
                if (e.getStateChange() == 1 && Config.open_send_cookie_state == 1){
                    Config.fixe_cookie_state = 1;
                }else{
                    Config.fixe_cookie_state = 0;
                }
//                show_info(Config.fixe_cookie_state + "");
            }
        });
        OPENRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 1表示选中，2表示取消选中
                // 只有在通过测试连接的情况下 才能开启发送状态
                if (e.getStateChange() == 1 && Config.open_connect == 1){
                    Config.open_send_state = 1;
                    // 设置白名单信息
                    Config.bai_ming_dans = white_list_text.getText();
                    show_info("open send ok");
                }else{
                    Config.open_send_state = 0;
                    show_info("close send");
                    if (Config.open_connect == 0){
                        show_info("frist test connect");

                    }
                }

            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        rootPanel.add(panel1, BorderLayout.NORTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.SOUTH);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        white_list = new JLabel();
        white_list.setText("White host list");
        panel4.add(white_list, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        white_list_text = new JTextField();
        panel4.add(white_list_text, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel5.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
//        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setLayout(new BorderLayout(0, 0));
        panel6.add(panel7, BorderLayout.WEST);
        CONNECT = new JButton();
        CONNECT.setText("CONNECT");
//        panel7.add(CONNECT, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel7.add(CONNECT, BorderLayout.CENTER);

        clean = new JButton();
        clean.setText("CLEAN");
        panel7.add(clean, BorderLayout.WEST);


        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, BorderLayout.CENTER);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        OPENRadioButton = new JRadioButton();
        OPENRadioButton.setText("OPEN  ");
        panel9.add(OPENRadioButton, BorderLayout.WEST);
        info = new JLabel();
        info.setText("INFO");
        panel9.add(info, BorderLayout.CENTER);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        panel5.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, BorderLayout.WEST);
        USECOOKIERadioButton = new JRadioButton();
        USECOOKIERadioButton.setText("USE COOKIE");
        panel11.add(USECOOKIERadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel12, BorderLayout.CENTER);
        FIXECOOKIERadioButton = new JRadioButton();
        FIXECOOKIERadioButton.setText("FIXE COOKIE");
        panel12.add(FIXECOOKIERadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cookie_text = new JTextField();
        panel12.add(cookie_text, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow"));
        panel1.add(panel13, BorderLayout.CENTER);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        CellConstraints cc = new CellConstraints();
        panel13.add(panel14, cc.xy(1, 1));
        api = new JLabel();
        api.setText("API");
        panel14.add(api, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        api_text = new JTextField();
        panel14.add(api_text, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel13.add(panel15, cc.xy(3, 1));
        key = new JLabel();
        key.setText("KEY");
        panel15.add(key, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        key_text = new JTextField();
        panel15.add(key_text, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel13.add(panel16, cc.xy(5, 1));
        project = new JLabel();
        project.setText("PROJECT");
        panel16.add(project, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        project_text = new JTextField();
        panel16.add(project_text, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new BorderLayout(0, 0));
        rootPanel.add(panel17, BorderLayout.CENTER);
        scan_url_jpanel = new JPanel();
        scan_url_jpanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(scan_url_jpanel, BorderLayout.WEST);
        final JScrollPane scrollPane1 = new JScrollPane();
        scan_url_jpanel.setPreferredSize(new Dimension(500,0));
        scan_url_jpanel.setMaximumSize(new Dimension(500,0));
        scan_url_jpanel.setMinimumSize(new Dimension(500,0));
        scan_url_jpanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listModel = new DefaultListModel();
        list1 = new JList(listModel);
        list1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //listModel.addElement("sdsdsdsds1231231232dsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsdsd");
                // 目录项被点击
                // 向后台发送请求，获取扫描结果
                if (e.getValueIsAdjusting()){


                    String url = list1.getSelectedValue().toString();
//                    listModel.addElement(url);
                    // 从服务器夹在获取扫描结果
                    JSONObject info = new JSONObject();
                    info.element("url", url);
//                    info.element("cookie", "");
                    info.element("project_name", Config.project_name);
                    info.element("key", Config.api_key);

                    String result = DDVTool.send_info_await(info,"select_scan_result"
                            , new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    show_info("get "+url+" scan result ok");
                                    return null;
                                }
                            },
                            new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    show_info("get "+url+" scan result error");
                                    return null;
                                }
                            });
                    textArea1.setText("");
                    try{
                        JSONArray ok_result_urls = JSONArray.fromObject(result);
                        for (Object ok_result_url : ok_result_urls) {
                            textArea1.append(ok_result_url.toString() + "\n");

                        }
                    }catch (Exception exception) {
                        textArea1.setText(result);
                    }




                }
            }
        });
        scrollPane1.setViewportView(list1);
        result_url_jpanel = new JPanel();
        result_url_jpanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(result_url_jpanel, BorderLayout.CENTER);
        textArea1 = new JTextArea();
        JScrollPane js=new JScrollPane(textArea1);
        //分别设置水平和垂直滚动条自动出现
        js.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //分别设置水平和垂直滚动条总是出现
//        js.setHorizontalScrollBarPolicy(
//                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        js.setVerticalScrollBarPolicy(
//                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //分别设置水平和垂直滚动条总是隐藏
//        js.setHorizontalScrollBarPolicy(
//                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        js.setVerticalScrollBarPolicy(
//                JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        result_url_jpanel.add(js, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        set_init();
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    // 对页面中的ui控件内容进行设置
    public void set_init(){
        api_text.setText(Config.target_url);
        key_text.setText(Config.api_key);
        project_text.setText(Config.project_name);
        white_list_text.setText(Config.bai_ming_dans);

    }


    // 显示提示内容
    public static void show_info(String message){
        info.setText(message);
    }

    // 获取ui中的cookie值
    public String get_ui_cookie(){
        return cookie_text.getText();
    }

    // 向url列表中添加值
    public void add_list_item(String url){
        listModel.addElement(url);
    }

}
