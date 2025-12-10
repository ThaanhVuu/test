package dhkthn.p2p.repository;

import dhkthn.p2p.config.DatabaseConnector;
import dhkthn.p2p.config.SqliteConnector;
import dhkthn.p2p.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("ALL")
public class UserRepo {
    private final Connection conn;

    public UserRepo() throws SQLException {
        DatabaseConnector connector = new SqliteConnector();
        conn = connector.getConnection();
    }

    public void save(User user){
        String sql = "insert into users(username, password) values(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean checkUserExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra user tồn tại");
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByUsername(String username){
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    return user;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error getting user by username");
            e.printStackTrace();
        }
        return null;
    }
}
