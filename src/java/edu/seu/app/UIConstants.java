package edu.seu.app;

import javax.swing.*;
import java.awt.*;

public class UIConstants {
    /**
     * AppMainWindow大小
     */
    public static final int MAIN_WINDOW_X = 240;
    public static final int MAIN_WINDOW_Y = 100;
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 440;

    public static final String MAIN_WINDOW_TITLE = "密码学与安全协议期末作业_194686";

    public static final Image ICON_IMAGE = Toolkit.getDefaultToolkit()
            .getImage(AppMainWindow.class.getResource("/icon/dataEnc.png"));

    // 主窗口背景色
    public static final Color MAIN_WINDOW_BACK_COLOR = Color.darkGray;

    // 工具栏背景色
    public static final Color TOOL_BAR_BACK_COLOR = new Color(73, 71, 71);

    /**
     * 字体
     */
    // 标题字体
    public static final Font FONT_TITLE = new Font("黑体", 0, 27);
    // 普通字体
    public final static Font FONT_NORMAL = new Font("黑体", 0, 13);
    // radio字体
    public final static Font FONT_RADIO = new Font("黑体", 0, 20);

    /**
     * 工具栏图标
     */

    public static final ImageIcon ICON_DATA_ENC = new ImageIcon(AppMainWindow.class.getResource("/icon/procedures.png"));
    public static final ImageIcon ICON_DATA_AUTH = new ImageIcon(AppMainWindow.class.getResource("/icon/longtime.png"));


    /**
     * 样式布局相关
     */
    // 主面板水平间隔
    public final static int MAIN_H_GAP = 25;

}
