package Gold40.Entity;

public class PaymentRequest {
    private String productName;
    private int price;
    private int quantity;

    // Getters and setters
    public int getQuantity() {return quantity;}
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
