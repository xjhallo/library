package ui;
import Model.Book;
import dao.BookDao;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMainFrame extends JFrame {
    // 将文本框定义为类成员，方便在事件监听器中访问
    private JTextField nameField;
    private JTextField publisherField;
    private JTextField authorField;
    private JTextField numberField;
    private JPanel mainPanel; // 主面板，用于切换显示内容
    private JPanel mainContentPanel;
    private ButtonGroup buttonGroup;
    public AdminMainFrame() {
        setTitle("管理员界面");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainContentPanel = new JPanel();
        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 初始化按钮
        JRadioButton addBookButton = new JRadioButton("增加图书");
        JRadioButton deleteBookButton = new JRadioButton("删除图书");
        JRadioButton searchBookButton = new JRadioButton("查询图书");
        JRadioButton changeBookButton = new JRadioButton("更改图书");
        // 添加按钮到面板
        buttonPanel.add(addBookButton);
        buttonPanel.add(deleteBookButton);
        buttonPanel.add(searchBookButton);
        buttonPanel.add(changeBookButton);
        // 初始化按钮组
        buttonGroup = new ButtonGroup();
        buttonGroup.add(addBookButton);
        buttonGroup.add(deleteBookButton);
        buttonGroup.add(searchBookButton);
        buttonGroup.add(changeBookButton);

        mainContentPanel.add(buttonPanel, BorderLayout.NORTH);
        // 将按钮面板添加到主面板

        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        // 添加图书按钮事件
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBookPanel(); // 先显示输入面板
            }
        });
        //删除图书按钮事件
        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDelBookPanel();
            }
        });
        // 添加主面板到窗口
        add(mainPanel);

        setLocationRelativeTo(null); // 窗口居中显示
        setVisible(true);
    }

    // 显示添加图书的输入面板
    private void showAddBookPanel() {
        // 创建信息输入面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 初始化输入组件
        JLabel nameLabel = new JLabel("书名：");
        nameField = new JTextField();
        JLabel publisherLabel = new JLabel("出版社：");
        publisherField = new JTextField();
        JLabel authorLabel = new JLabel("作者：");
        authorField = new JTextField();
        JLabel numberLabel = new JLabel("数量：");
        numberField = new JTextField();
        JButton confirmButton = new JButton("确认添加");
        JButton backButton = new JButton("返回");

        // 添加组件到面板
        infoPanel.add(nameLabel);
        infoPanel.add(nameField);
        infoPanel.add(authorLabel);
        infoPanel.add(authorField);
        infoPanel.add(publisherLabel);
        infoPanel.add(publisherField);
        infoPanel.add(numberLabel);
        infoPanel.add(numberField);
        infoPanel.add(backButton);
        infoPanel.add(confirmButton);

        // 确认添加按钮事件
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddBook(); // 处理添加图书逻辑
            }
        });

        // 返回按钮事件
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换回主面板
                mainPanel.removeAll();
                mainPanel.add(mainContentPanel, BorderLayout.CENTER);
                // 清除单选按钮的选中状态
                buttonGroup.clearSelection();
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        // 切换显示输入面板
        mainPanel.removeAll();
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    //显示删除图书的面板
    private void showDelBookPanel() {
        //创造信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //初始化输入组件
        JLabel nameLabel = new JLabel("书名：");
        nameField = new JTextField();
        JLabel publisherLabel = new JLabel("出版社：");
        publisherField = new JTextField();
        JLabel authorLabel = new JLabel("作者：");
        authorField = new JTextField();
        JLabel numberLabel = new JLabel("数量：");
        numberField = new JTextField();
        JButton confirmButton = new JButton("确认删除");
        JButton backButton = new JButton("返回");

        //将组件加入到面板
        infoPanel.add(nameLabel);
        infoPanel.add(nameField);
        infoPanel.add(authorLabel);
        infoPanel.add(authorField);
        infoPanel.add(publisherLabel);
        infoPanel.add(publisherField);
        infoPanel.add(numberLabel);
        infoPanel.add(numberField);
        infoPanel.add(backButton);
        infoPanel.add(confirmButton);
        //确认删除按钮事件
        confirmButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               handleDelBook();//处理删除图书逻辑
           }
        });
        // 返回按钮事件
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换回主面板
                mainPanel.removeAll();
                mainPanel.add(mainContentPanel, BorderLayout.CENTER);
                // 清除单选按钮的选中状态
                buttonGroup.clearSelection();
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        // 切换显示输入面板
        mainPanel.removeAll();
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    // 处理添加图书的方法
    private void handleAddBook() {
        try {
            // 从文本框获取输入值
            String bookName = nameField.getText().trim();
            String bookAuthor = authorField.getText().trim();
            String bookPublisher = publisherField.getText().trim();
            String numberText = numberField.getText().trim();

            // 简单验证输入
            if (bookName.isEmpty() || bookAuthor.isEmpty() ||
                    bookPublisher.isEmpty() || numberText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写所有字段", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 转换数量为整数
            int addNumber = Integer.parseInt(numberText);
            if (addNumber <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须为正数", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建图书对象
            Book book = new Book(bookName, bookAuthor, bookPublisher, "这是一本书", "文学类");

            // 调用DAO添加图书
            BookDao manager = new BookDao();
            int success = manager.addBook(book, addNumber);

            // 显示操作结果
            if (success > 0) {
                JOptionPane.showMessageDialog(this, "图书添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 清空输入框并返回主面板
                clearInputFields();
                showAddBookPanel(); // 可以选择返回主面板或保持在当前面板
            } else {
                JOptionPane.showMessageDialog(this, "图书添加失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "数量必须是数字", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "添加图书时发生错误: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    //处理删除图书的逻辑
    private void handleDelBook() {
        try {
            String bookName = nameField.getText().trim();//书名
            String bookAuthor = authorField.getText().trim();//作者
            String bookPublisher = publisherField.getText().trim();//出版社
            String bookNumber = numberField.getText().trim();
            // 简单验证输入
            if (bookName.isEmpty() || bookAuthor.isEmpty() ||
                    bookPublisher.isEmpty() || bookNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写所有字段", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int delNumber = Integer.parseInt(numberField.getText());
            if (delNumber <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须为正数", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Book book = new Book(bookName, bookAuthor, bookPublisher);
            BookDao manager = new BookDao();
            int success = manager.deleteBook(book, delNumber);
            if(success >= 0) {
                JOptionPane.showMessageDialog(this, "图书删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 清空输入框并返回主面板
                clearInputFields();
                showAddBookPanel(); // 可以选择返回主面板或保持在当前面板
            } else {
                JOptionPane.showMessageDialog(this, "图书删除失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "数量必须是数字", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "删除图书时发生错误: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    // 清空输入框
    private void clearInputFields() {
        if (nameField != null) nameField.setText("");
        if (authorField != null) authorField.setText("");
        if (publisherField != null) publisherField.setText("");
        if (numberField != null) numberField.setText("");
    }

}
