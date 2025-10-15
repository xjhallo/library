package ui;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
public class LoginFrame extends JFrame {

    private static final String URL = "jdbc:sqlite:D:/a3575/SQLite/Data/user.db";

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

        // 给密码框绑定回车键事件
        passField.addActionListener(e -> {
            // 当在密码框按下Enter时，触发登录逻辑
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            String role = getRole(user, pass);
            roleCheck(role);

        });

// 给用户名框绑定回车键事件（按回车自动聚焦到密码框）
        userField.addActionListener(e -> {
            passField.requestFocus(); // 光标跳到密码框
        });

        // 登录按钮事件
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            String role = getRole(user, pass);
            roleCheck(role);
        });

        setVisible(true);
    }
    public static String getRole(String id, String pwd) {
        String sql = "select * from userdata where ID = ? and password = ?";
        String role = null;

        try (
                Connection conn = DriverManager.getConnection(URL);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // 给SQL中的第一个占位符设置id值（text类型）
            ps.setString(1, id);
            // 给SQL中的第二个占位符设置密码值
            ps.setString(2, pwd);

            // 执行查询，获取结果集
            ResultSet rs = ps.executeQuery();

            // 判断结果集中是否有数据
            if (rs.next()) {
                // 从结果集中获取role字段的值
                role = rs.getString("role");
            }

        } catch (SQLException e) {
            System.out.println("登录验证出错: " + e.getMessage());
        }

        // 返回角色
        return role;
    }
    public void roleCheck(String role) {
        boolean isReader = false;
        boolean isAdmin = false;
        if(role != null) {
            isReader = role.equals("user");
            isAdmin = role.equals("admin");
        }
        if (isReader) {
            JOptionPane.showMessageDialog(this, "用户登录成功！");
            dispose();
            new ReaderMainFrame(); // 打开读者主界面
        } else if(isAdmin) {
            JOptionPane.showMessageDialog(this, "管理员登录成功！");
            dispose();
            new AdminMainFrame(); //打开管理员主界面
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！");
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
