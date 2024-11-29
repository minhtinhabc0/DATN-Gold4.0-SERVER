package Gold40.Controller;

import Gold40.Entity.BaoCaoNPP;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/baocaonpp")
public class BaoCaoNPPController {

    @Autowired
    private Gold40.DAO.BaoCaoNPPDAO baoCaoNPPDAO;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    // API lấy báo cáo nhà phân phối
    @GetMapping
    public ResponseEntity<?> getBaoCaoByDistributor(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null && taiKhoan.getVaitro() == 2) { // Vai trò nhà phân phối
                    String maNhaPhanPhoi = taiKhoan.getManhaphanphoi();
                    List<BaoCaoNPP> baoCaoList;

                    // Kiểm tra nếu có tham số năm và tháng
                    if (year != null && month != null) {
                        // Lọc theo năm và tháng
                        baoCaoList = baoCaoNPPDAO.findBaoCaoByMaNhaPhanPhoiAndMonth(maNhaPhanPhoi, String.valueOf(year), String.format("%02d", month));
                    } else if (year != null) {
                        // Lọc theo năm
                        baoCaoList = baoCaoNPPDAO.findBaoCaoByMaNhaPhanPhoiAndYear(maNhaPhanPhoi, String.valueOf(year));
                        System.out.println("Dữ liệu báo cáo theo năm: " + baoCaoList);
                    } else {
                        // Nếu không có năm và tháng thì lấy tất cả
                        baoCaoList = baoCaoNPPDAO.findBaoCaoByMaNhaPhanPhoi(maNhaPhanPhoi);
                    }



                    // Tính tổng doanh thu và lợi nhuận cho từng sản phẩm
                    Map<String, Float> revenueMap = new HashMap<>();
                    Map<String, Float> profitMap = new HashMap<>();
                    for (BaoCaoNPP baoCao : baoCaoList) {
                        String productName = baoCao.getMasanPham().getTenSanPham();
                        revenueMap.put(productName, revenueMap.getOrDefault(productName, 0f) + baoCao.getDoanhThu());
                        profitMap.put(productName, profitMap.getOrDefault(productName, 0f) + baoCao.getLoiNhuan());
                    }

                    // Tạo đối tượng trả về cho client
                    Map<String, Object> response = new HashMap<>();
                    response.put("totalRevenue", revenueMap);
                    response.put("totalProfit", profitMap);
                    response.put("products", baoCaoList);

                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.status(403).body("Tài khoản không phải nhà phân phối.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }
    @GetMapping("/baocaonpp")
    public List<BaoCaoNPP> getBaoCao(
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "month", required = false) String month) {

        // Kiểm tra giá trị của year và month
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);

        if (year != null && !year.isEmpty()) {
            if (month != null && !month.isEmpty()) {
                return baoCaoNPPDAO.findBaoCaoByYearAndMonth(year, month);
            } else {
                return baoCaoNPPDAO.findBaoCaoByYear(year); // Trả về tất cả dữ liệu của năm nếu không có tháng
            }
        } else {
            return baoCaoNPPDAO.findAll(); // Trả về tất cả dữ liệu nếu không có năm
        }
    }



}
