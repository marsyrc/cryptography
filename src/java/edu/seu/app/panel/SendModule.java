package edu.seu.app.panel;

import edu.seu.app.AppMainWindow;
import edu.seu.app.UIConstants;
import edu.seu.app.BytesUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SendModule extends JPanel {
    private JTextArea messageArea;
    private JButton sendButton;
    private JButton Openfile;
    private JComboBox<String> SendModeEnsure;
    public static String messagesource = "message";
    public File file;
    public static String filepath;
    public static byte[] sendByte = "".getBytes();


    public SendModule() {
        initialize();
        addComponent();
        addListener();
    }

    private void initialize() {
        this.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        this.setLayout(new BorderLayout());
    }

    private void addComponent() {
        this.add(getUpPanel(), BorderLayout.NORTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);
        this.add(getDownPanel(), BorderLayout.SOUTH);
    }

    /**
     * 上部面板
     */
    private JPanel getUpPanel() {
        JPanel panelUp = new JPanel();
        panelUp.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelUp.setLayout(new FlowLayout(FlowLayout.LEFT, UIConstants.MAIN_H_GAP, 5));

        JLabel labelTitle = new JLabel("消息发送");
        labelTitle.setFont(UIConstants.FONT_TITLE);
        labelTitle.setForeground(Color.white);
        panelUp.add(labelTitle);

        return panelUp;
    }


    /**
     * 明文输入
     */
    private JPanel getCenterPanel() {
        JPanel panelCenter = new JPanel();
        panelCenter.setBounds(25, 20, 800, 600);
        panelCenter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelCenter.setLayout(null);

        JLabel SMELabel = new JLabel("选取明文来源：");
        SMELabel.setForeground(Color.white);
        SMELabel.setFont(UIConstants.FONT_RADIO);
        SMELabel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        SMELabel.setBounds(25, 70, 180, 30);
        SendModeEnsure = new JComboBox<>();
        SendModeEnsure.addItem("从字符串");
        SendModeEnsure.addItem("从文件");
        SendModeEnsure.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        SendModeEnsure.setFont(UIConstants.FONT_NORMAL);
        SendModeEnsure.setBounds(25, 105, 120, 30);
        SendModeEnsure.setSelectedIndex(0);

        JLabel messageLabel = new JLabel("手动输入明文：");
        messageLabel.setForeground(Color.white);
        messageLabel.setFont(UIConstants.FONT_RADIO);
        messageLabel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        messageLabel.setBounds(300, 25, 320, 25);

        Border border = BorderFactory.createEtchedBorder();
        messageArea = new JTextArea(40, 40);
        messageArea.setLineWrap(true);
        messageArea.setFont(UIConstants.FONT_NORMAL);
        messageArea.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        messageArea.setBounds(300, 53, 355, 185);
        messageArea.setBorder(border);

        Openfile = new JButton("打开...");
        Openfile.setFont(UIConstants.FONT_RADIO);
        Openfile.setBackground(Color.white);
        Openfile.setContentAreaFilled(false);
        //   buttonSave.setBorderPainted(false);
        Openfile.setForeground(Color.white);
        Openfile.setBounds(25, 145, 120, 30);

        panelCenter.add(SendModeEnsure);
        panelCenter.add(SMELabel);
        panelCenter.add(messageLabel);
        panelCenter.add(messageArea);
        panelCenter.add(Openfile);
        return panelCenter;
    }

    /**
     * 底部面板
     */
    private JPanel getDownPanel() {
        JPanel panelDown = new JPanel();
        panelDown.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelDown.setLayout(new FlowLayout(FlowLayout.RIGHT, UIConstants.MAIN_H_GAP, 15));

        sendButton = new JButton("发送");
        sendButton.setBorderPainted(false);
        sendButton.setFont(UIConstants.FONT_RADIO);
        sendButton.setContentAreaFilled(false);
        //  buttonUser2.setBackground(Color.white);
        sendButton.setBorderPainted(false);
        sendButton.setForeground(Color.white);
        sendButton.setPreferredSize(new Dimension(120, 50));

        panelDown.add(sendButton);

        return panelDown;
    }


    private void addListener() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println(messagesource);
                if (messagesource == "message") {
                    String msg = messageArea.getText();
                    //sendByte = AppMainWindow.securityUtil.sendProcess(msg.getBytes());   //senderMessage
                    System.out.println(new String(msg.getBytes()));
                    sendByte = AppMainWindow.algUtil.sendProcess(msg.getBytes());
                    JOptionPane.showMessageDialog(getParent(), "Text Message Sended", "success", JOptionPane.INFORMATION_MESSAGE);
                } else if (messagesource == "file") {
                    //filepath = file.toString();
                    filepath = messageArea.getText();
                    byte[] bfile = BytesUtils.getBytes(filepath);
                    if (bfile == null) {
                        JOptionPane.showMessageDialog(getParent(), "error 5:文件读取错误", "Tip", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //  存入文件
                        filepath = filepath.trim();
                        String filename = filepath.substring(filepath.lastIndexOf("\\") + 1);
                        String filePath = filepath.substring(0, filepath.lastIndexOf("\\"));
                        filename = filename.concat(".rsa");
                        //AppMainWindow.securityUtil.sendProcess(msg1);
                        sendByte = AppMainWindow.algUtil.sendProcess(bfile);
                        BytesUtils.saveFile(sendByte, filePath, filename);
                        JOptionPane.showMessageDialog(getParent(), "File Message Sended", "success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }
        });
        SendModeEnsure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SendModeEnsure.getSelectedIndex() == 0) {
                    messagesource = "message";
                    messageArea.setEditable(true);
                } else {
                    messagesource = "file";
                    messageArea.setText("");
                    messageArea.setEditable(false);
                }
            }
        });
        Openfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messagesource == "file") {
                    JFileChooser dlg = new JFileChooser();
                    dlg.setDialogTitle("打开明文文件");
                    int result = dlg.showOpenDialog(null);  // 打开"打开文件"对话框
                    // int result = dlg.showSaveDialog(this);  // 打"开保存文件"对话框
                    if (result == JFileChooser.APPROVE_OPTION) {
                        file = dlg.getSelectedFile();
                        messageArea.setEditable(true);
                        messageArea.setText(file.toString());
                        messageArea.setEditable(false);
                    }
                }
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
                frame.add(new SendModule());
                frame.setVisible(true);
            }
        });
    }
}
