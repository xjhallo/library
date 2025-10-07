package ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("图书管理系统 - 登录");
        //setSize(350, 200);
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // 标签和输入框
        JLabel userLabel = new JLabel("用户名:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("密码:");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("登录");

        // 添加组件
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel()); // 占位
        panel.add(loginButton);

        add(panel);

        // 登录按钮事件
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("123456")) {
                JOptionPane.showMessageDialog(this, "登录成功！");
                dispose();
                new AdminMainFrame(); // 打开主界面
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
