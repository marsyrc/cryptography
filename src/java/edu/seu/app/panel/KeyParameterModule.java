package edu.seu.app.panel;

import edu.seu.app.AppMainWindow;
import edu.seu.app.AlgUtil;
import edu.seu.app.BytesUtils;
import edu.seu.app.UIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static edu.seu.app.AppMainWindow.algUtil;

public class KeyParameterModule extends JPanel {
    private static JButton buttonSave;
    //private static MyIconButton buttonUser1;
    private static JButton buttonUser1;
    private static JButton buttonUser2;
    private static JComboBox<String> symEncAlgBox;
    private static JComboBox<String> symKeyBox;
    private static JComboBox<String> hashAlgBox;
    private static JTextField symKeySeedField;
    private static JTextField rsaModule1Field;
    private static JTextField rsaModule2Field;
    private static JTextArea user1Parameter;
    private static JTextArea user2Parameter;
    public static JDialog user1ParameterDialog;
    public static JDialog user2ParameterDialog;
    private static String symEncAlg;
    private static String symKeySeed;
    private static String hashAlg;
    private static String rsaKeySize1;
    private static String rsaKeySize2;
    public boolean isSave = false;

    static {
        symEncAlg = "DES";
        symKeySeed = null;
        hashAlg = "MD5";
        rsaKeySize1 = "2048";
        rsaKeySize2 = "2048";
    }

    public KeyParameterModule() {
        initialize();
        addComponent();
        addListener();
    }

    private void initialize() {
        this.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        this.setLayout(new BorderLayout());
        AppMainWindow.algUtil = new AlgUtil(symEncAlg,
                symKeySeed, hashAlg, rsaKeySize1, rsaKeySize2);
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

        JLabel labelTitle = new JLabel("设置加密方案");
        labelTitle.setFont(UIConstants.FONT_TITLE);
        labelTitle.setForeground(Color.white);
        panelUp.add(labelTitle);

        return panelUp;
    }

    /**
     * 中部面板
     */
    private JPanel getCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        centerPanel.setLayout(new GridLayout(1, 1));

        JPanel panelParameter = new JPanel();
        panelParameter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelParameter.setBounds(25, 20, 800, 600);
        panelParameter.setLayout(null);

        // 对称加密算法选择
        JLabel symEncLabel = new JLabel("对称加密算法");
        symEncLabel.setForeground(Color.white);
        symEncLabel.setFont(UIConstants.FONT_RADIO);
        symEncLabel.setBounds(25, 60, 150, 25);

        symEncAlgBox = new JComboBox<>();
        symEncAlgBox.addItem("DES");
        symEncAlgBox.addItem("AES");
        symEncAlgBox.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        symEncAlgBox.setFont(UIConstants.FONT_RADIO);
        symEncAlgBox.setBounds(240, 60, 120, 27);
        symEncAlgBox.setSelectedIndex(0);

        // 对称密钥生成方案
        JLabel symKeyLabel = new JLabel("对称密钥生成");
        symKeyLabel.setFont(UIConstants.FONT_RADIO);
        symKeyLabel.setForeground(Color.white);
        symKeyLabel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        symKeyLabel.setBounds(25, 100, 150, 25);
        symKeyBox = new JComboBox<>();
        symKeyBox.addItem("随机生成");
        symKeyBox.addItem("种子生成");
        symKeyBox.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        symKeyBox.setFont(UIConstants.FONT_RADIO);
        symKeyBox.setBounds(240, 100, 120, 27);
        symKeyBox.setSelectedIndex(0);

        JLabel symKeySeedLabel = new JLabel("密钥生成种子");
        symKeySeedLabel.setFont(UIConstants.FONT_RADIO);
        symKeySeedLabel.setForeground(Color.white);
        symKeySeedLabel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        symKeySeedLabel.setBounds(25, 140, 160, 25);

        symKeySeedField = new JTextField();
        symKeySeedField.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        symKeySeedField.setFont(UIConstants.FONT_RADIO);
        symKeySeedField.setBounds(240, 140, 120, 27);
        symKeySeedField.setEnabled(false);

        JLabel hashLabel = new JLabel("Hash函数");
        hashLabel.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        hashLabel.setForeground(Color.white);
        hashLabel.setFont(UIConstants.FONT_RADIO);
        hashLabel.setBounds(420, 60, 150, 25);

        hashAlgBox = new JComboBox<>();
        hashAlgBox.addItem("MD5");
        hashAlgBox.addItem("SHA224");
        hashAlgBox.addItem("SHA256");
        hashAlgBox.addItem("SHA384");
        hashAlgBox.addItem("SHA512");
        hashAlgBox.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        hashAlgBox.setFont(UIConstants.FONT_RADIO);
        hashAlgBox.setBounds(650, 60, 120, 27);
        hashAlgBox.setSelectedIndex(0);


        JLabel rsaModule1Label = new JLabel("发送方公钥模长:");
        rsaModule1Label.setFont(UIConstants.FONT_RADIO);
        rsaModule1Label.setForeground(Color.white);
        rsaModule1Label.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        rsaModule1Label.setBounds(420, 100, 200, 25);

        rsaModule1Field = new JTextField();
        rsaModule1Field.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        rsaModule1Field.setFont(UIConstants.FONT_RADIO);
        rsaModule1Field.setBounds(650, 100, 120, 27);
        rsaModule1Field.setEnabled(true);
        JLabel rsaModule2Label = new JLabel("接收方公钥模长:");
        rsaModule2Label.setFont(UIConstants.FONT_RADIO);
        rsaModule2Label.setForeground(Color.white);
        rsaModule2Label.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        rsaModule2Label.setBounds(420, 140, 200, 25);

        rsaModule2Field = new JTextField();
        rsaModule2Field.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        rsaModule2Field.setFont(UIConstants.FONT_RADIO);
        rsaModule2Field.setBounds(650, 140, 120, 27);
        rsaModule2Field.setEnabled(true);

        user1Parameter = new JTextArea();
        user1Parameter.setFont(UIConstants.FONT_NORMAL);
        user1Parameter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        user1Parameter.setBounds(0, 0, 300, 100);
        user1Parameter.setEnabled(false);
        user1Parameter.setLineWrap(true);
        Border border1 = BorderFactory.createEtchedBorder();
        user1Parameter.setBorder(border1);
//        user1Parameter.setText("用户A默认参数如下：\n");
//        user1Parameter.append(AppMainWindow.securityUtil.getParameterInfo(1));
        JScrollPane scroll1 = new JScrollPane(user1Parameter);
        scroll1.setBounds(0, 0, 400, 400);
        scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scroll1.setViewportView(user1Parameter);

        user2Parameter = new JTextArea();
        user2Parameter.setFont(UIConstants.FONT_NORMAL);
        user2Parameter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        user2Parameter.setBounds(0, 0, 300, 100);
        user2Parameter.setEnabled(false);
        user2Parameter.setLineWrap(true);
        Border border2 = BorderFactory.createEtchedBorder();
        user2Parameter.setBorder(border2);
//        user2Parameter.setText("用户B默认参数如下：\n");
//        user2Parameter.append(AppMainWindow.securityUtil.getParameterInfo(2));
        JScrollPane scroll2 = new JScrollPane();
        scroll2.setBounds(0, 0, 400, 400);
        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll2.setViewportView(user2Parameter);

        user1ParameterDialog = new JDialog();
        user1ParameterDialog.setBounds(400, 200, 300, 300);
        user1ParameterDialog.setLayout(new BorderLayout());
        user1ParameterDialog.setTitle("发送方参数");

        user2ParameterDialog = new JDialog();
        user2ParameterDialog.setBounds(400, 200, 300, 300);
        user2ParameterDialog.setLayout(new BorderLayout());
        user2ParameterDialog.setTitle("接收方参数");


        panelParameter.add(symEncLabel);
        panelParameter.add(symKeyLabel);
        panelParameter.add(symKeyBox);
        panelParameter.add(symKeySeedLabel);
        panelParameter.add(symKeySeedField);
        panelParameter.add(hashLabel);
        panelParameter.add(rsaModule1Label);
        panelParameter.add(rsaModule1Field);
        panelParameter.add(rsaModule2Label);
        panelParameter.add(rsaModule2Field);
        panelParameter.add(symEncAlgBox);
        panelParameter.add(hashAlgBox);
        centerPanel.add(panelParameter);
        user1ParameterDialog.add(scroll1);
        user2ParameterDialog.add(scroll2);
//        user1ParameterDialog.add(user1Ok);
//        user2ParameterDialog.add(user2Parameter);
//        user2ParameterDialog.add(user2Ok);

        return centerPanel;
    }

    /**
     * 底部面板
     */
    private JPanel getDownPanel() {
        JPanel panelDown = new JPanel();
        panelDown.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelDown.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.MAIN_H_GAP, 25));

//        buttonSave = new MyIconButton(UIConstants.ICON_SAVE_BUTTON, UIConstants.ICON_SAVE_BUTTON_ENABLE,
//                UIConstants.ICON_SAVE_BUTTON_DISABLE, "选定参数");
        buttonUser1 = new JButton("发送方");
        buttonUser1.setFont(UIConstants.FONT_RADIO);
        buttonUser1.setBackground(Color.white);
        buttonUser1.setBorderPainted(false);
        buttonUser1.setContentAreaFilled(false);
        buttonUser1.setForeground(Color.white);
        buttonUser1.setPreferredSize(new Dimension(120, 50));

        buttonUser2 = new JButton("接收方");
        buttonUser2.setFont(UIConstants.FONT_RADIO);
        buttonUser2.setContentAreaFilled(false);
        //  buttonUser2.setBackground(Color.white);
        buttonUser2.setBorderPainted(false);
        buttonUser2.setForeground(Color.white);
        buttonUser2.setPreferredSize(new Dimension(120, 50));

        buttonSave = new JButton("保存方案");
        buttonSave.setFont(UIConstants.FONT_RADIO);
        buttonSave.setBackground(Color.white);
        buttonSave.setContentAreaFilled(false);

        buttonSave.setForeground(Color.white);
        buttonSave.setPreferredSize(new Dimension(120, 50));

        panelDown.add(buttonSave);
//        panelDown.add(buttonUser1);
//        panelDown.add(buttonUser2);

        return panelDown;
    }

    /**
     * 为各组件添加事件监听
     */
    private void addListener() {
        symEncAlgBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symEncAlgBox.getSelectedIndex() == 0) {
                    symEncAlg = "DES";
                } else {
                    symEncAlg = "AES";
                }
            }
        });
        symKeyBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symKeyBox.getSelectedIndex() == 0) {
                    symKeySeedField.setEnabled(false);
                } else {
                    symKeySeedField.setEnabled(true);
                }
            }
        });
        hashAlgBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (hashAlgBox.getSelectedIndex()) {
                    case 0: {
                        hashAlg = "MD5";
                    }
                    break;
                    case 1: {
                        hashAlg = "SHA224";
                    }
                    break;
                    case 2: {
                        hashAlg = "SHA256";
                    }
                    break;
                    case 3: {
                        hashAlg = "SHA384";
                    }
                    break;
                    case 4: {
                        hashAlg = "SHA512";
                    }
                    break;
                }
            }
        });
        buttonUser1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showdir(1);
            }
        });
        buttonUser2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showdir(2);
            }
        });
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symKeyBox.getSelectedIndex() == 0) {
                    symKeySeed = null;
                } else {
                    symKeySeed = symKeySeedField.getText().trim();
                    if (symKeySeed.equals("")) {
                        JOptionPane.showMessageDialog(getParent(), "error 1:对称密钥生成种子未设置", "Tip", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                rsaKeySize1 = rsaModule1Field.getText().trim();
                rsaKeySize2 = rsaModule2Field.getText().trim();

                if (rsaKeySize1.equals("") || (Integer.parseInt(rsaKeySize1) < 1024) || (Integer.parseInt(rsaKeySize1) >2048)) {
                    JOptionPane.showMessageDialog(getParent(), "error 2:发送方公钥模数允许的范围:1024-2048", "Tip", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (rsaKeySize2.equals("") || (Integer.parseInt(rsaKeySize2) < 1024) || (Integer.parseInt(rsaKeySize1) >2048)) {
                    JOptionPane.showMessageDialog(getParent(), "error 3:接收方公钥模数允许的范围:1024-2048", "Tip", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                isSave = true;
                algUtil = new AlgUtil(symEncAlg, symKeySeed, hashAlg, rsaKeySize1, rsaKeySize2);

                user1Parameter.setText("发送方参数如下：\n");
                user1Parameter.append(algUtil.getParameterInfo(1));

                user2Parameter.setText("接收方参数如下：\n");
                user2Parameter.append(algUtil.getParameterInfo(2));

                if (user1Parameter.getText() != "") {
                    BytesUtils.saveFile(algUtil.getSendPublicKey().getBytes(), System.getProperty("user.dir"), "SenderPublicKey.pu");
                    String path = System.getProperty("user.dir");
                    JOptionPane.showMessageDialog(getParent(), "发送方公钥已保存到本地工程目录下", "Tip", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(getParent(), "error 4:请先保存参数设置", "Tip", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(getParent(), "ok:加密方案已设定,请在通信流程查看双方密钥", "Tip", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * byte array 转 16进制字符串
     *
     * @param bytes
     */
    private String byteArray2hexStr(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b & 0xff));
        }
        return hex.toString();
    }

    public void showdir(int i) {
        if (isSave) {
            if (i == 1) {
                user1ParameterDialog.setVisible(true);
            } else {
                user2ParameterDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(getParent(), "error 0:加密方案未保存！", "Tip", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setBounds(UIConstants.MAIN_WINDOW_X, UIConstants.MAIN_WINDOW_Y,
                        UIConstants.MAIN_WINDOW_WIDTH, UIConstants.MAIN_WINDOW_HEIGHT);
                frame.add(new KeyParameterModule());
                frame.setVisible(true);
            }
        });
    }
}
