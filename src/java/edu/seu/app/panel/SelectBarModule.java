package edu.seu.app.panel;

import edu.seu.app.AppMainWindow;
import edu.seu.app.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

public class SelectBarModule extends JPanel {
    private static JButton buttonKeyParameter;
    private static JButton buttonSend;
    private static JButton buttonReceive;
    private static JButton buttonSetting;
    private static JButton buttonCAuth;
    private byte[] send_old = "".getBytes();

    final Base64.Encoder encoder = Base64.getMimeEncoder();
    final Base64.Decoder decoder = Base64.getDecoder();

    public SelectBarModule() {
        initialize();
        addButton();
        addListener();
    }

    private void initialize() {
        Dimension preferredSize = new Dimension(700, 48);
        this.setPreferredSize(preferredSize);
        this.setMaximumSize(preferredSize);
        this.setMinimumSize(preferredSize);
        this.setBackground(UIConstants.TOOL_BAR_BACK_COLOR);
        this.setLayout(new GridLayout(1, 2));
    }

    /**
     * 添加工具按钮
     */
    private void addButton() {
        JPanel panelUp = new JPanel();
        panelUp.setBackground(UIConstants.TOOL_BAR_BACK_COLOR);
        panelUp.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        buttonKeyParameter = new JButton("方案设定");
        buttonKeyParameter.setFont(UIConstants.FONT_RADIO);
        buttonKeyParameter.setBackground(Color.white);
        buttonKeyParameter.setBorderPainted(false);
        buttonKeyParameter.setContentAreaFilled(false);
        buttonKeyParameter.setForeground(Color.white);
        buttonKeyParameter.setPreferredSize(new Dimension(120, 50));

        buttonSend = new JButton("消息发送");
        buttonSend.setFont(UIConstants.FONT_RADIO);
        buttonSend.setBackground(Color.white);
        buttonSend.setBorderPainted(false);
        buttonSend.setContentAreaFilled(false);
        buttonSend.setForeground(Color.white);
        buttonSend.setPreferredSize(new Dimension(120, 50));

        buttonReceive = new JButton("解密认证");
        buttonReceive.setFont(UIConstants.FONT_RADIO);
        buttonReceive.setBackground(Color.white);
        buttonReceive.setBorderPainted(false);
        buttonReceive.setContentAreaFilled(false);
        buttonReceive.setForeground(Color.white);
        buttonReceive.setPreferredSize(new Dimension(120, 50));

        buttonSetting = new JButton("通信流程");
        buttonSetting.setFont(UIConstants.FONT_RADIO);
        buttonSetting.setBackground(Color.white);
        buttonSetting.setBorderPainted(false);
        buttonSetting.setContentAreaFilled(false);
        buttonSetting.setForeground(Color.white);
        buttonSetting.setPreferredSize(new Dimension(120, 50));

        buttonCAuth = new JButton("落地验证");
        buttonCAuth.setFont(UIConstants.FONT_RADIO);
        buttonCAuth.setBackground(Color.white);
        buttonCAuth.setBorderPainted(false);
        buttonCAuth.setContentAreaFilled(false);
        buttonCAuth.setForeground(Color.white);
        buttonCAuth.setPreferredSize(new Dimension(115, 50));

        panelUp.add(buttonKeyParameter);
        panelUp.add(buttonSend);
        panelUp.add(buttonReceive);
        panelUp.add(buttonSetting);
        panelUp.add(buttonCAuth);

        this.add(panelUp);
    }

    /**
     * 为各按钮添加事件动作监听
     */
    private void addListener() {
        buttonKeyParameter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppMainWindow.mainPanelCenter.removeAll();
                AppMainWindow.mainPanelCenter.add(AppMainWindow.keyParameterModule, BorderLayout.CENTER);

                AppMainWindow.mainPanelCenter.updateUI();
            }
        });

        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppMainWindow.mainPanelCenter.removeAll();
                AppMainWindow.mainPanelCenter.add(AppMainWindow.sendModule, BorderLayout.CENTER);

                AppMainWindow.mainPanelCenter.updateUI();
            }
        });

        buttonReceive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AppMainWindow.mainPanelCenter.removeAll();
                AppMainWindow.mainPanelCenter.add(AppMainWindow.receiveModule, BorderLayout.CENTER);
                AppMainWindow.mainPanelCenter.updateUI();

                AppMainWindow.receiveModule.getReceiveArea().setEditable(true);
                if (SendModule.sendByte != "".getBytes() && send_old != SendModule.sendByte) {
                    if (SendModule.messagesource == "file") {
                        AppMainWindow.receiveModule.getReceiveArea().
                                setText(SendModule.filepath + ".rsa");
                        send_old = SendModule.sendByte;
                    } else {
                        AppMainWindow.receiveModule.getReceiveArea().
                                setText(encoder.encodeToString(SendModule.sendByte));
                        send_old = SendModule.sendByte;
                    }
                } else {
                    AppMainWindow.receiveModule.getReceiveArea().
                            setText("");
                    AppMainWindow.receiveModule.getReceiveArea().setEditable(false);
                }
            }
        });

        buttonSetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AppMainWindow.mainPanelCenter.removeAll();
                AppMainWindow.mainPanelCenter.add(AppMainWindow.introductionModule, BorderLayout.CENTER);
                AppMainWindow.mainPanelCenter.updateUI();

            }
        });

        buttonCAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AppMainWindow.mainPanelCenter.removeAll();
                AppMainWindow.mainPanelCenter.add(AppMainWindow.consistentAuth, BorderLayout.CENTER);
                AppMainWindow.mainPanelCenter.updateUI();

            }
        });

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setBounds(UIConstants.MAIN_WINDOW_X, UIConstants.MAIN_WINDOW_Y,
                        UIConstants.MAIN_WINDOW_WIDTH, UIConstants.MAIN_WINDOW_HEIGHT);
                frame.setLayout(new BorderLayout());
                frame.add(new SelectBarModule(), BorderLayout.WEST);
                frame.setVisible(true);
            }
        });
    }
}
