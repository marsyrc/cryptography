package edu.seu.app.panel;

import edu.seu.app.AlgUtil;
import edu.seu.app.AppMainWindow;
import edu.seu.app.BytesUtils;
import edu.seu.app.UIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ReceiveModule extends JPanel {
    private JTextArea receiveArea;
    private JTextArea plaintextArea;
    private JButton decryptButton;
    private JButton authButton;
    private JButton buttonSavePara;
    private boolean saveSignatureEnable;
    private byte[] localSignature = new byte[0];

    final Base64.Encoder encoder = Base64.getMimeEncoder();
    final Base64.Decoder decoder = Base64.getMimeDecoder();

    public ReceiveModule() {
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

    public JTextArea getReceiveArea() {
        return receiveArea;
    }

    /**
     * 上部面板
     */
    private JPanel getUpPanel() {
        JPanel panelUp = new JPanel();
        panelUp.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelUp.setLayout(new FlowLayout(FlowLayout.LEFT, UIConstants.MAIN_H_GAP, 5));

        JLabel labelTitle = new JLabel("解密认证");
        labelTitle.setFont(UIConstants.FONT_TITLE);
        labelTitle.setForeground(Color.white);
        panelUp.add(labelTitle);

        return panelUp;
    }

    /**
     * 中部面板
     */
    private JPanel getCenterPanel() {
        JPanel panelCenter = new JPanel();
        panelCenter.setBounds(25, 20, 800, 600);
        panelCenter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelCenter.setLayout(null);

        JLabel ciphertextLabel = new JLabel("接收的密文如下：");
        ciphertextLabel.setFont(UIConstants.FONT_RADIO);
        ciphertextLabel.setForeground(Color.white);
        ciphertextLabel.setBackground(Color.white);
        ciphertextLabel.setBounds(25, 15, 300, 20);

        Border border1 = BorderFactory.createEtchedBorder();
        receiveArea = new JTextArea(40, 40);
        receiveArea.setLineWrap(true);
        receiveArea.setFont(UIConstants.FONT_NORMAL);
        receiveArea.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        receiveArea.setBounds(25, 38, 355, 180);
        receiveArea.setLineWrap(true);
        receiveArea.setBorder(border1);
        JScrollPane scroll1 = new JScrollPane();
        scroll1.setBounds(25, 38, 355, 180);
        scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll1.setViewportView(receiveArea);

        JLabel plaintextLabel = new JLabel("解密后明文如下：");
        plaintextLabel.setFont(UIConstants.FONT_RADIO);
        plaintextLabel.setForeground(Color.white);
        plaintextLabel.setBounds(400, 15, 300, 20);

        Border border2 = BorderFactory.createEtchedBorder();
        plaintextArea = new JTextArea(40, 40);
        plaintextArea.setLineWrap(true);
        plaintextArea.setFont(UIConstants.FONT_NORMAL);
        plaintextArea.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        plaintextArea.setBounds(400, 38, 355, 180);
        plaintextArea.setLineWrap(true);
        plaintextArea.setBorder(border2);
        JScrollPane scroll2 = new JScrollPane();
        scroll2.setBounds(400, 38, 355, 180);
        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll2.setViewportView(plaintextArea);

        panelCenter.add(ciphertextLabel);
        panelCenter.add(scroll1);
        panelCenter.add(plaintextLabel);
        panelCenter.add(scroll2);

        return panelCenter;
    }

    /**
     * 底部面板
     */
    private JPanel getDownPanel() {
        JPanel panelDown = new JPanel();
        panelDown.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelDown.setLayout(new FlowLayout(FlowLayout.RIGHT, UIConstants.MAIN_H_GAP, 25));

//        decryptButton = new MyIconButton(UIConstants.ICON_DECRYPT_BUTTON, UIConstants.ICON_DECRYPT_BUTTON_ENABLE,
//                UIConstants.ICON_DECRYPT_BUTTON_DISABLE, "");
//        authButton = new MyIconButton(UIConstants.ICON_AUTH_BUTTON, UIConstants.ICON_AUTH_BUTTON_ENABLE,
//                UIConstants.ICON_AUTH_BUTTON_DISABLE, "");
        decryptButton = new JButton("解密");
        decryptButton.setFont(UIConstants.FONT_RADIO);
        decryptButton.setBackground(Color.white);
        decryptButton.setBorderPainted(false);
        decryptButton.setContentAreaFilled(false);
        decryptButton.setForeground(Color.white);
        decryptButton.setPreferredSize(new Dimension(120, 50));

        authButton = new JButton("认证");
        authButton.setFont(UIConstants.FONT_RADIO);
        authButton.setBackground(Color.white);
        authButton.setBorderPainted(false);
        authButton.setContentAreaFilled(false);
        authButton.setForeground(Color.white);
        authButton.setPreferredSize(new Dimension(120, 50));

        buttonSavePara = new JButton("保存落地验证参数");
        buttonSavePara.setFont(UIConstants.FONT_RADIO);
        buttonSavePara.setContentAreaFilled(false);
        //  buttonUser2.setBackground(Color.white);
        buttonSavePara.setBorderPainted(false);
        buttonSavePara.setForeground(Color.white);
        buttonSavePara.setPreferredSize(new Dimension(240,50));

        panelDown.add(decryptButton);
        panelDown.add(authButton);
        panelDown.add(buttonSavePara);
        return panelDown;
    }

    private void addListener() {
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = receiveArea.getText();
                //byte[] bfile = BytesUtils.getBytes(SendModule.filepath+"");
                //System.out.println(msg.equals(""));
                if(!msg.equals("")){
                    if(SendModule.messagesource == "file"){
                        byte[] bfile = BytesUtils.getBytes(msg);
                        byte[] resDecode = AppMainWindow.algUtil.receiverDecrypt(bfile);
                        String result = AppMainWindow.algUtil.ByteTOString(resDecode);
                        String[] split = result.split("解密得消息内容：");
                        //String result = new String(resDecode);
                        if(!split[0].equals("消息遭到篡改，无法解密!\n")){

                            try{
                                localSignature = AppMainWindow.algUtil.getSignatureSend(resDecode);        //全局变量
                                saveSignatureEnable = true; //使能保存签名到本地
                            }catch (Exception e2){
                            }

                            plaintextArea.setText(split[0]+"明文已保存到:"+SendModule.filepath.substring(0,SendModule.filepath.lastIndexOf("\\"))+" 目录下");
                            System.out.println(SendModule.filepath);
                            int messageL = resDecode.length- AlgUtil.sessionkeyL - AlgUtil.signatureL - AlgUtil.digestL;
                            byte[] messageByte = new byte[messageL];
                            System.arraycopy(resDecode,0,messageByte,0,messageL);
                            BytesUtils.saveFile(messageByte, SendModule.filepath.substring(0,SendModule.filepath.lastIndexOf("\\")),"decode_"+ SendModule.filepath.substring(SendModule.filepath.lastIndexOf("\\")+1));
                        }else{
                            plaintextArea.setText(split[0]);
                        }
                    }else{
                        //byte[] resByte = AppMainWindow.securityUtil.receiverDecrypt(SendModule.sendByte);
                        byte[] messByte = new byte[0];
                        try{
                            messByte = decoder.decode(msg);
                        }catch (Exception ent){
                            plaintextArea.setText("消息遭到篡改，无法解密!\n");
                            throw ent;
                        }
                        byte[] resByte = AppMainWindow.algUtil.receiverDecrypt(messByte);     //还要改toolbar文件那decoder.decode(msg)
                        //byte[] resByte = AppMainWindow.securityUtil.receiverDecrypt(msg.getBytes());
                        System.out.println("resByte:"+resByte.length);
                        System.out.println("resByte:"+resByte.length);
                        String result = AppMainWindow.algUtil.ByteTOString(resByte);
                        plaintextArea.setText(result);
                    }
                }else{
                    plaintextArea.setText("没有收到任何消息！\n");
                }
            }

        });

        authButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = receiveArea.getText();
                String success = "认证通过，消息没有遭到篡改！\n";
                //byte[] bfile = BytesUtils.getBytes(SendModule.filepath+"");
                //System.out.println(msg.equals(""));
                if(!msg.equals("")){
                    if(SendModule.messagesource == "file"){
                        byte[] bfile = BytesUtils.getBytes(msg);
                        byte[] resByte = AppMainWindow.algUtil.receiverVerify(bfile);
                        //System.out.println("sss getbytes string: "+new String("132".getBytes())); //这是可以的
                        String result = new String(resByte);
                        if (success.equals(result)){
                            JOptionPane.showMessageDialog(getParent(),result,"Tip",JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(getParent(),result,"Tip",JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        byte[] messByte = new byte[0];
                        try{
                            messByte = decoder.decode(msg);
                        }catch (Exception ent){
                            JOptionPane.showMessageDialog(getParent(), "认证失败，消息遭到篡改！\n","Tip",JOptionPane.ERROR_MESSAGE);
                            throw ent;
                        }
                        byte[] resByte = AppMainWindow.algUtil.receiverVerify(messByte);
                        //System.out.println("sss getbytes string: "+new String("132".getBytes())); //这是可以的
                        String result = new String(resByte);
                        if (success.equals(result)){
                            JOptionPane.showMessageDialog(getParent(),result,"Tip",JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(getParent(),result,"Tip",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(getParent(),"没有收到任何消息","Tip",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonSavePara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((saveSignatureEnable == true) && SendModule.messagesource == "file" ){
                    BytesUtils.saveFile(localSignature,System.getProperty("user.dir"),"signature.sn");
                    JOptionPane.showMessageDialog(getParent(),"消息签名已保存到本地","Tip",JOptionPane.INFORMATION_MESSAGE);
                }else if (SendModule.messagesource == "message"){
                    JOptionPane.showMessageDialog(getParent(),"落地后完整性验证只针对文件，签名保存失败！","Tip",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(getParent(),"请先解密消息得到签名","Tip",JOptionPane.ERROR_MESSAGE);
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
                frame.add(new ReceiveModule());
                frame.setVisible(true);
            }
        });
    }
}
