package dao;

import Model.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BookDao {
    // 数据库连接地址
    private static final String URL = "jdbc:sqlite:D:/a3575/SQLite/Data/user.db";
    // 2. 提取SQL为常量，提高可维护性
    private static final String CHECK_SQL = "SELECT totalBooks, availableBooks FROM Book WHERE name = ? AND publisher = ? AND author = ?";
    private static final String UPDATE_SQL = "UPDATE Book SET totalBooks = totalBooks + ?, availableBooks = availableBooks + ? WHERE name = ? AND publisher = ? AND author = ?";
    private static final String INSERT_SQL = "INSERT INTO Book (name, publisher, author, status, borrowerId, borrowerName, availableBooks, totalBooks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public BookDao() {}
    // 增加图书（优化：入参校验、资源管理、逻辑一致性）
    public int addBook(Book book, int num) {
        // 1. 入参校验：避免非法数量或空对象导致异常
        if (book == null) {
            throw new IllegalArgumentException("图书对象不能为null");
        }
        if (num <= 0) {
            throw new IllegalArgumentException("新增图书数量必须大于0");
        }

        // 初始化返回值：最终更新后的总库存
        int finalTotal = 0;

        // 2. 复用一个Connection，所有资源通过try-with-resources自动关闭
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL);
             PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);
             PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL)) {

            // 3. 检查图书是否已存在
            checkStmt.setString(1, book.getBookName());
            checkStmt.setString(2, book.getBookPublisher());
            checkStmt.setString(3, book.getBookAuthor());

            // ResultSet也用try-with-resources，确保自动关闭
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    // 4. 图书已存在：更新库存数量
                    int existingTotal = checkRs.getInt("totalBooks");
                    int existingAvailable = checkRs.getInt("availableBooks");
                    // 计算新库存并更新到图书对象
                    finalTotal = existingTotal + num;
                    int newAvailable = existingAvailable + num;
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(newAvailable);
                    book.setBookAvailable(true); // 库存增加后，状态为可借

                    // 执行更新SQL
                    updateStmt.setInt(1, num); // 增加的总数量
                    updateStmt.setInt(2, num); // 增加的可借数量
                    updateStmt.setString(3, book.getBookName());
                    updateStmt.setString(4, book.getBookPublisher());
                    updateStmt.setString(5, book.getBookAuthor());
                    updateStmt.executeUpdate();

                } else {
                    // 5. 图书不存在：插入新记录
                    finalTotal = num; // 新图书总库存为传入的数量
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(num);
                    book.setBookAvailable(true); // 新图书状态为可借

                    // 执行插入SQL（字段顺序与INSERT_SQL对应）
                    insertStmt.setString(1, book.getBookName());    // 书名
                    insertStmt.setString(2, book.getBookPublisher());// 出版社
                    insertStmt.setString(3, book.getBookAuthor());   // 作者
                    insertStmt.setString(4, "在馆");                 // 状态
                    insertStmt.setInt(5, 0);                        // 借阅者ID（无则为0）
                    insertStmt.setString(6, null);                  // 借阅者姓名（无则为null）
                    insertStmt.setInt(7, num);                      // 可借数量
                    insertStmt.setInt(8, num);                      // 总库存数量
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            // 优化：添加错误信息，便于排查问题
            throw new RuntimeException("添加图书失败：" + e.getMessage(), e);
        }

        // 返回最终的总库存
        return finalTotal;
    }

    public int deleteBook(Book book, int delNum) {
        if (book == null) {
            return -1;
        }
        if(delNum <= 0) {
            return -1;
        }
        int finalTotal = 0;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL);
             PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);) {
            //检查是否存在删除图书
            checkStmt.setString(1, book.getBookName());
            checkStmt.setString(2, book.getBookPublisher());
            checkStmt.setString(3, book.getBookAuthor());
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if(checkRs.next()) {
                    //存在该图书，执行删除逻辑
                    int existingTotal = checkRs.getInt("totalBooks");
                    int existingAvailable = checkRs.getInt("availableBooks");
                    finalTotal = existingTotal - delNum;
                    int newAvailable = existingAvailable - delNum;
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(newAvailable);
                    boolean newStatus = newAvailable >= 0;
                    book.setBookAvailable(newStatus); // 更新状态

                    //执行更新语句
                    updateStmt.setInt(1, -delNum); // 删除的总数量
                    updateStmt.setInt(2, -delNum); // 减少的可借数量
                    updateStmt.setString(3, book.getBookName());
                    updateStmt.setString(4, book.getBookPublisher());
                    updateStmt.setString(5, book.getBookAuthor());
                    updateStmt.executeUpdate();
                } else {
                    //不存在要删除的书
                    return -1;
                }
            }
        } catch (SQLException e) {

        }
        return finalTotal;
    }
}