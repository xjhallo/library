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

    // 1. 拆分SQL常量：为删除单独定义“减少库存”的SQL，语义更清晰
    private static final String CHECK_SQL = "SELECT totalBooks, availableBooks FROM Book WHERE name = ? AND publisher = ? AND author = ?";
    private static final String ADD_STOCK_SQL = "UPDATE Book SET totalBooks = totalBooks + ?, availableBooks = availableBooks + ? WHERE name = ? AND publisher = ? AND author = ?";
    private static final String DELETE_STOCK_SQL = "UPDATE Book SET totalBooks = totalBooks - ?, availableBooks = availableBooks - ? WHERE name = ? AND publisher = ? AND author = ?";
    private static final String INSERT_SQL = "INSERT INTO Book (name, publisher, author, status, borrowerId, borrowerName, availableBooks, totalBooks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public BookDao() {}

    // 增加图书（原逻辑保留，仅优化SQL常量名）
    public int addBook(Book book, int num) {
        if (book == null) {
            throw new IllegalArgumentException("图书对象不能为null");
        }
        if (num <= 0) {
            throw new IllegalArgumentException("新增图书数量必须大于0");
        }

        int finalTotal = 0;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL);
             PreparedStatement updateStmt = conn.prepareStatement(ADD_STOCK_SQL); // 改用“增加库存”SQL
             PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL)) {

            checkStmt.setString(1, book.getBookName());
            checkStmt.setString(2, book.getBookPublisher());
            checkStmt.setString(3, book.getBookAuthor());

            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    int existingTotal = checkRs.getInt("totalBooks");
                    int existingAvailable = checkRs.getInt("availableBooks");
                    finalTotal = existingTotal + num;
                    int newAvailable = existingAvailable + num;
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(newAvailable);

                    updateStmt.setInt(1, num);
                    updateStmt.setInt(2, num);
                    updateStmt.setString(3, book.getBookName());
                    updateStmt.setString(4, book.getBookPublisher());
                    updateStmt.setString(5, book.getBookAuthor());
                    updateStmt.executeUpdate();

                } else {
                    finalTotal = num;
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(num);

                    insertStmt.setString(1, book.getBookName());
                    insertStmt.setString(2, book.getBookPublisher());
                    insertStmt.setString(3, book.getBookAuthor());
                    insertStmt.setString(4, "在馆");
                    insertStmt.setInt(5, 0);
                    insertStmt.setString(6, null);
                    insertStmt.setInt(7, num);
                    insertStmt.setInt(8, num);
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("添加图书失败：" + e.getMessage(), e);
        }

        return finalTotal;
    }

    // 删除图书（修复3个问题：SQL语句、错误处理、状态判断）
    public int deleteBook(Book book, int delNum) {
        if (book == null) {
            return -1;
        }
        if (delNum <= 0) {
            return -1;
        }

        int finalTotal = 0;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL);
             // 2. 改用“减少库存”的SQL，而非复用增加的SQL
             PreparedStatement updateStmt = conn.prepareStatement(DELETE_STOCK_SQL)) {

            checkStmt.setString(1, book.getBookName());
            checkStmt.setString(2, book.getBookPublisher());
            checkStmt.setString(3, book.getBookAuthor());

            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    int existingTotal = checkRs.getInt("totalBooks");
                    int existingAvailable = checkRs.getInt("availableBooks");

                    // 3. 增加“总库存不足”的判断（原逻辑只判断了可借数量）
                    if (existingTotal < delNum || existingAvailable < delNum) {
                        return -1; // 总库存或可借数量不足，删除失败
                    }

                    finalTotal = existingTotal - delNum;
                    int newAvailable = existingAvailable - delNum;
                    book.setTotalBooks(finalTotal);
                    book.setAvailableBooks(newAvailable);
                    // 4. 状态逻辑优化：可借数量>0才为可借，=0为不可借

                    // 5. 传入正数delNum（SQL已用“-”，无需传负数）
                    updateStmt.setInt(1, delNum);
                    updateStmt.setInt(2, delNum);
                    updateStmt.setString(3, book.getBookName());
                    updateStmt.setString(4, book.getBookPublisher());
                    updateStmt.setString(5, book.getBookAuthor());
                    updateStmt.executeUpdate();

                } else {
                    return -1; // 图书不存在
                }
            }

        } catch (SQLException e) {
            // 6. 修复“吞异常”问题：打印错误信息并抛出，便于调试
            e.printStackTrace();
            throw new RuntimeException("删除图书失败：" + e.getMessage(), e);
        }

        return finalTotal;
    }

    // 查询图书（修复空指针异常，补充错误处理）
    public Book searchBook(Book book) {
        if (book == null) {
            return null;
        }

        Book res = null; // 初始为null

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL)) {

            checkStmt.setString(1, book.getBookName());
            checkStmt.setString(2, book.getBookPublisher());
            checkStmt.setString(3, book.getBookAuthor());

            try (ResultSet checkRs = checkStmt.executeQuery()) {
                // 7. 修复空指针：查询到结果后，先创建Book实例再赋值
                if (checkRs.next()) {
                    res = new Book(); // 关键：初始化res对象
                    res.setBookName(book.getBookName());
                    res.setBookPublisher(book.getBookPublisher());
                    res.setBookAuthor(book.getBookAuthor());
                    // 8. 补充：从数据库获取库存数量（原逻辑用了传入book的数量，错误）
                    res.setTotalBooks(checkRs.getInt("totalBooks"));
                    res.setAvailableBooks(checkRs.getInt("availableBooks"));
                }
            }
        } catch (SQLException e) {
            // 10. 补充错误处理
            e.printStackTrace();
            throw new RuntimeException("查询图书失败：" + e.getMessage(), e);
        }
        return res;
    }

}