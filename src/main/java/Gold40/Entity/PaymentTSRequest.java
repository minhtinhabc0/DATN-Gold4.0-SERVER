package Gold40.Entity;

import java.util.List;

public class PaymentTSRequest {
    private double totalAmount; // Tổng số tiền của giỏ hàng
    private List<Item> items;  // Danh sách sản phẩm trong giỏ hàng

    // Getters and setters

    public static class Item {
        private String maSanPham; // Mã sản phẩm
        private String tenSanPham; // Tên sản phẩm
        private double gia; // Giá sản phẩm
        private int soLuong; // Số lượng
        private String kichThuoc;
        // Getters and setters

        public String getMaSanPham() {
            return maSanPham;
        }

        public void setMaSanPham(String maSanPham) {
            this.maSanPham = maSanPham;
        }

        public String getTenSanPham() {
            return tenSanPham;
        }

        public void setTenSanPham(String tenSanPham) {
            this.tenSanPham = tenSanPham;
        }

        public double getGia() {
            return gia;
        }

        public void setGia(double gia) {
            this.gia = gia;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(int soLuong) {
            this.soLuong = soLuong;
        }

        public String getKichThuoc() {
            return kichThuoc;
        }

        public void setKichThuoc(String kichThuoc) {
            this.kichThuoc = kichThuoc;
        }
    }
}
