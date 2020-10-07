package edu.seu.app.panel;


import edu.seu.app.AppMainWindow;
import edu.seu.app.UIConstants;
import edu.seu.app.BytesUtils;
import edu.seu.algs.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import java.math.BigInteger;


public class ConsistentAuth extends JPanel {
    private BigInteger n;
    private BigInteger e;
    private byte[] msg = "123".getBytes();
    private byte[] signature;
    private String HashALG = "MD5";
    boolean LongVerift = AppMainWindow.algUtil.LongTermValidation(AppMainWindow.algUtil.PublicKeyGen(n, e), msg, signature, HashALG);

    private JButton authButton;
    private JButton OpenPublicKeyButton;
    private JButton OpenMessageButton;
    private JButton OpenSignatureButton;

    private String publicPath = "";
    private String signPath = "";
    private String mesPath = "";
    private File file1;
    private File file2;
    private File file3;

    public ConsistentAuth() {
        initialize();
        addComponet();
        addListener();
    }

    private void initialize() {
        this.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        this.setLayout(new BorderLayout());
    }

    private void addComponet() {
        this.add(getUpPanel(), BorderLayout.NORTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);
        this.add(getDownPanel(), BorderLayout.SOUTH);
    }

    /**
     * 顶部面板
     *
     * @return
     */
    private JPanel getUpPanel() {
        JPanel panelUp = new JPanel();
        panelUp.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelUp.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.MAIN_H_GAP, 10));

        JLabel labelTitle = new JLabel("文件落地后完整性验证");
        labelTitle.setFont(UIConstants.FONT_TITLE);
        labelTitle.setForeground(Color.white);
        panelUp.add(labelTitle);
        return panelUp;
    }

    /**
     * 中部面板
     *
     * @return
     */
    private JPanel getCenterPanel() {
//        JPanel panelCenter = new JPanel();
//        panelCenter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
//        panelCenter.setLayout(null);

        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelCenter.setLayout(new GridLayout(1, 1));

        JPanel panelGridIcon = new JPanel();
        panelGridIcon.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelGridIcon.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.MAIN_H_GAP, 0));

        // 初始化组件
        JLabel proce = new JLabel(UIConstants.ICON_DATA_AUTH);
        proce.setBounds(250,50,686,250);

        panelGridIcon.add(proce);
        panelCenter.add(panelGridIcon);

        return panelCenter;
    }

    /**
     * 底部面板
     *
     * @return
     */
    private JPanel getDownPanel() {
        JPanel panelDown = new JPanel();
        panelDown.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelDown.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 25));

        OpenPublicKeyButton = new JButton("发送方公钥");
        OpenPublicKeyButton.setFont(UIConstants.FONT_RADIO);
        OpenPublicKeyButton.setBackground(Color.white);
        OpenPublicKeyButton.setBorderPainted(false);
        OpenPublicKeyButton.setContentAreaFilled(false);
        OpenPublicKeyButton.setForeground(Color.white);
        OpenPublicKeyButton.setPreferredSize(new Dimension(150, 50));

        OpenMessageButton = new JButton("待验证明文");
        OpenMessageButton.setFont(UIConstants.FONT_RADIO);
        OpenMessageButton.setBackground(Color.white);
        OpenMessageButton.setBorderPainted(false);
        OpenMessageButton.setContentAreaFilled(false);
        OpenMessageButton.setForeground(Color.white);
        OpenMessageButton.setPreferredSize(new Dimension(150, 50));

        OpenSignatureButton = new JButton("签名");
        OpenSignatureButton.setFont(UIConstants.FONT_RADIO);
        OpenSignatureButton.setBackground(Color.white);
        OpenSignatureButton.setBorderPainted(false);
        OpenSignatureButton.setContentAreaFilled(false);
        OpenSignatureButton.setForeground(Color.white);
        OpenSignatureButton.setPreferredSize(new Dimension(80, 50));

        panelDown.add(OpenPublicKeyButton);
        panelDown.add(OpenMessageButton);
        panelDown.add(OpenSignatureButton);

        authButton = new JButton("认证");
        authButton.setFont(UIConstants.FONT_RADIO);
        authButton.setBackground(Color.white);
        authButton.setBorderPainted(false);
        authButton.setContentAreaFilled(false);
        authButton.setForeground(Color.white);
        authButton.setPreferredSize(new Dimension(160, 50));
        panelDown.add(authButton);

        return panelDown;
    }

    private void addListener() {
        //添加读取公钥按钮监听
        OpenPublicKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser dlg = new JFileChooser();
                dlg.setDialogTitle("选择发送方公钥文件");
                int result = dlg.showOpenDialog(null);  // 打开"打开文件"对话框
                // int result = dlg.showSaveDialog(this);  // 打"开保存文件"对话框
                if (result == JFileChooser.APPROVE_OPTION) {
                    file1 = dlg.getSelectedFile();
                    publicPath = file1.getPath();
                }
            }
        });


        //添加读取签名按钮监听
        OpenSignatureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser dlg = new JFileChooser();
                dlg.setDialogTitle("选择签名文件");
                int result = dlg.showOpenDialog(null);  // 打开"打开文件"对话框
                // int result = dlg.showSaveDialog(this);  // 打"开保存文件"对话框
                if (result == JFileChooser.APPROVE_OPTION) {
                    file2 = dlg.getSelectedFile();
                    signPath = file2.getPath();
                }
            }
        });


        //添加读取明文按钮监听
        OpenMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser dlg = new JFileChooser();
                dlg.setDialogTitle("选取待验证明文文件");
                int result = dlg.showOpenDialog(null);  // 打开"打开文件"对话框
                // int result = dlg.showSaveDialog(this);  // 打"开保存文件"对话框
                if (result == JFileChooser.APPROVE_OPTION) {
                    file3 = dlg.getSelectedFile();
                    mesPath = file3.getPath();

                }
            }
        });

        authButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (publicPath.equals("")) {
                    JOptionPane.showMessageDialog(getParent(), "请加载公钥和Hash算法", "提示", JOptionPane.ERROR_MESSAGE);
                }
                if (signPath.equals("")) {
                    JOptionPane.showMessageDialog(getParent(), "请加载签名信息", "提示", JOptionPane.ERROR_MESSAGE);
                }
                if (mesPath.equals("")) {
                    JOptionPane.showMessageDialog(getParent(), "请加载待验证明文文件", "提示", JOptionPane.ERROR_MESSAGE);
                }
                if (!publicPath.equals("") && !signPath.equals("") && !mesPath.equals("")) {
                    byte[] pufile = BytesUtils.getBytes(publicPath);
                    byte[] snfile = BytesUtils.getBytes(signPath);
                    byte[] mesfile = BytesUtils.getBytes(mesPath);
                    try {
                        String publicHash = new String(pufile);
                        System.out.println(publicHash);
                        String[] S = publicHash.split("\n");
                        String HashALG = S[0];
                        BigInteger n = new BigInteger(S[1]);
                        BigInteger e = new BigInteger(S[2]);
                        RSAKey.PublicKey PublicKey = AppMainWindow.algUtil.PublicKeyGen(n, e);
                        boolean Verified = AppMainWindow.algUtil.LongTermValidation(PublicKey, mesfile, snfile, HashALG);
                        if (Verified == true) {
                            JOptionPane.showMessageDialog(getParent(), "验证成功，明文完整！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(getParent(), "验证失败！明文遭到篡改！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(getParent(), "验证失败！未知错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        throw e;
                    }
                }

            }
        });


    }


}

