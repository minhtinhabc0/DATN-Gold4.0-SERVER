package Gold40.Controller;

import Gold40.DAO.ProductsDAO;
import Gold40.Entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductsController {
    @Autowired
    ProductsDAO productsDAO;
    @GetMapping("/products")
    public List<SanPham> getAll(Model model) {
        return productsDAO.findAllByTrangThaiTrue();
    }

    @GetMapping("/products/{id}")
    public SanPham getOne(@PathVariable("id") Integer id) {
        return productsDAO.findById(id).get();
    }

    @PostMapping("/products")
    public SanPham post(@RequestBody SanPham sanpham) {
        productsDAO.save(sanpham);
        return sanpham;
    }

    @PutMapping("/products/{id}")
    public SanPham put(@PathVariable("id") Integer id, @RequestBody SanPham sanpham) {
        productsDAO.save(sanpham);
        return sanpham;
    }
//@DeleteMapping("/products/{id}")
//public void delete(@PathVariable("id") String id) {
//	dao.deleteById(id);
//}
}
