package dao;

import db.DbHelper;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public long create(Product p) throws SQLException {
        String sql = "INSERT INTO products (name,barcode,price,stock) VALUES (?,?,?,?) RETURNING id";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBarcode());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        throw new SQLException("Insert failed, id not returned");
    }
    public List<Product> findByName(String name) throws SQLException {
        String sql = """
        SELECT id, name, barcode, price, stock
        FROM products
        WHERE name ILIKE ?
        ORDER BY id
        """;

        List<Product> list = new ArrayList<>();

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("barcode"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock")
                    );
                    list.add(p);
                }
            }
        }
        return list;
    }
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        }
    }
    public boolean decreaseStockTx(long productId, int qty) throws SQLException {
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be > 0");
        }

        String selectSql = "SELECT stock FROM products WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        Connection conn = DbHelper.getConnection();
        try {
            conn.setAutoCommit(false); // TRANSACTION BAŞLAT

            int currentStock;

            // 1) satırı kilitle + stok oku
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, productId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // ürün yok
                    }
                    currentStock = rs.getInt("stock");
                }
            }

            // 2) stok yeter mi?
            if (currentStock < qty) {
                conn.rollback();
                return false; // stok yetersiz
            }

            // 3) stok düş
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, qty);
                ps.setLong(2, productId);

                int affected = ps.executeUpdate();
                if (affected != 1) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // HER ŞEY OK -> KALICI YAP
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            try {
                conn.setAutoCommit(true); // bağlantıyı normale döndür
            } catch (SQLException ignored) {}
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }



}
