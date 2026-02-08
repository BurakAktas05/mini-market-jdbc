package model;

import java.math.BigDecimal;

public class Product {

    private Long id;
    private String name;
    private String barcode;
    private int stock;
    private BigDecimal price;
    public Product(){

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setName(String name) {
        if (name==null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");

        }
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stok negatif olamaz");
        }
        this.stock = stock;
    }
    public Product(Long id, String name, String barcode, BigDecimal price, int stock) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.stock = stock;
    }


}