import dao.ProductDao;
import db.DbHelper;
import model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductDao dao = new ProductDao();

        while (true) {
            System.out.println("""
                1 - Ürün ekle
                2 - Ürün ara & listele
                3 - Ürün sil
                4 - Stok düş
                0 - Çıkış
                """);

            System.out.print("Seçim: ");
            int choice = sc.nextInt();
            sc.nextLine(); // buffer temizle

            try {
                switch (choice) {
                    case 1 -> addProduct(sc, dao);
                    case 2 -> searchProduct(sc, dao);
                    case 3 -> deleteProduct(sc, dao);
                    case 4 -> decreaseStock(sc, dao);
                    case 0 -> {
                        System.out.println("Çıkılıyor...");
                        return;
                    }
                    default -> System.out.println("Geçersiz seçim");
                }
            } catch (Exception e) {
                DbHelper.showErrorMessage(e);
            }
        }
    }

    static void addProduct(Scanner sc, ProductDao dao) throws SQLException {
        Product p = new Product();

        System.out.print("Ad: ");
        p.setName(sc.nextLine());

        System.out.print("Fiyat: ");
        p.setPrice(new BigDecimal(sc.nextLine()));

        System.out.print("Stok: ");
        p.setStock(sc.nextInt());
        sc.nextLine();

        long id = dao.create(p);
        System.out.println("Ürün eklendi. ID=" + id);
    }

    static void searchProduct(Scanner sc, ProductDao dao) throws SQLException {
        System.out.print("Ürün adı ara: ");
        String name = sc.nextLine();

        List<Product> list = dao.findByName(name);
        if (list.isEmpty()) {
            System.out.println("Ürün bulunamadı.");
            return;
        }

        for (Product p : list) {
            System.out.println(
                    "ID: " + p.getId() +
                            " | " + p.getName() +
                            " | " + p.getPrice() +
                            " | Stok: " + p.getStock()
            );
        }
    }

    static void deleteProduct(Scanner sc, ProductDao dao) throws SQLException {
        System.out.print("Silinecek ürün ID: ");
        long id = sc.nextLong();
        sc.nextLine();

        boolean ok = dao.delete(id);
        System.out.println(ok ? "Silindi" : "ID bulunamadı");
    }

    static void decreaseStock(Scanner sc, ProductDao dao) throws SQLException {
        System.out.print("Ürün ID: ");
        long id = sc.nextLong();

        System.out.print("Kaç adet düşülsün: ");
        int qty = sc.nextInt();
        sc.nextLine();

        boolean ok = dao.decreaseStockTx(id, qty);
        System.out.println(ok ? "Stok düşüldü" : "Ürün yok veya stok yetersiz");
    }
}
