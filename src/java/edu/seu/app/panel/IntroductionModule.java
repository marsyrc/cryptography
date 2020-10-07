package edu.seu.app.panel;

import edu.seu.app.AppMainWindow;
import edu.seu.app.BytesUtils;
import edu.seu.app.UIConstants;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntroductionModule extends JPanel {

    private static JButton buttonUser1;
    private static JButton buttonUser2;



    public IntroductionModule() {
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

    public void showdir(int i) {
        if (AppMainWindow.keyParameterModule.isSave) {
            if (i == 1) {
                KeyParameterModule.user1ParameterDialog.setVisible(true);
            } else {
                KeyParameterModule.user2ParameterDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(getParent(), "error 0:加密方案未保存", "Tip", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * 上部面板
     */
    private JPanel getUpPanel() {
        JPanel panelUp = new JPanel();
        panelUp.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelUp.setLayout(new FlowLayout(FlowLayout.LEFT, UIConstants.MAIN_H_GAP, 5));

        JLabel labelTitle = new JLabel("通信流程");
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
        // 中间面板
        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelCenter.setLayout(new GridLayout(1, 1));

        // 图标、版本Grid
        JPanel panelGridIcon = new JPanel();
        panelGridIcon.setBackground(UIConstants.MAIN_WINDOW_BACK_COLOR);
        panelGridIcon.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.MAIN_H_GAP, 0));

        // 初始化组件
        JLabel proce = new JLabel(UIConstants.ICON_DATA_ENC);
        proce.setBounds(0,50,686,250);

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
        panelDown.setBounds(0,260,686,150);
        panelDown.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.MAIN_H_GAP, 25));

        buttonUser1 = new JButton("发送方参数");
        buttonUser1.setFont(UIConstants.FONT_RADIO);
        buttonUser1.setBackground(Color.white);
        buttonUser1.setBorderPainted(false);
        buttonUser1.setContentAreaFilled(false);
        buttonUser1.setForeground(Color.white);
        buttonUser1.setPreferredSize(new Dimension(160,50));

        buttonUser2 = new JButton("接收方参数");
        buttonUser2.setFont(UIConstants.FONT_RADIO);
        buttonUser2.setContentAreaFilled(false);
        //  buttonUser2.setBackground(Color.white);
        buttonUser2.setBorderPainted(false);
        buttonUser2.setForeground(Color.white);
        buttonUser2.setPreferredSize(new Dimension(160,50));



        panelDown.add(buttonUser1);
        panelDown.add(buttonUser2);

        return panelDown;
    }

    private void addListener() {
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

    }
}
