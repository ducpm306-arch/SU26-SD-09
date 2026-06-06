package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.HoaDon;
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.service.AdminDatPhongService;
import su26sd09.su26sd09.service.HoaDonService;
import su26sd09.su26sd09.service.KhuyenMaiService;
import su26sd09.su26sd09.service.UserService;

@Controller
@RequestMapping("/admin/hoa-don")
public class AdminHoaDonController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private AdminDatPhongService adminDatPhongService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        loadFormAndList(model, new HoaDon(), keyword, "Thêm hóa đơn");
        return "admin/hoa-don-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        HoaDon hoaDon = hoaDonService.findById(id);
        if (hoaDon == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn");
            return "redirect:/admin/hoa-don";
        }

        loadFormAndList(model, hoaDon, keyword, "Cập nhật hóa đơn");
        return "admin/hoa-don-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute HoaDon hoaDon,
            @RequestParam(name = "maDatPhong", required = false) Integer maDatPhong,
            @RequestParam(name = "maKhuyenMai", required = false) Integer maKhuyenMai,
            @RequestParam(name = "maNhanVienXuat", required = false) Integer maNhanVienXuat,
            RedirectAttributes redirectAttributes
    ) {
        DatPhong datPhong = maDatPhong == null ? null : adminDatPhongService.findById(maDatPhong);
        KhuyenMai khuyenMai = maKhuyenMai == null ? null : khuyenMaiService.findById(maKhuyenMai);
        NguoiDung nhanVien = maNhanVienXuat == null ? null : userService.Getbyid(maNhanVienXuat);

        hoaDon.setD(datPhong);
        hoaDon.setK(khuyenMai);
        hoaDon.setN(nhanVien);
        hoaDonService.save(hoaDon);
        redirectAttributes.addFlashAttribute("success", "Lưu hóa đơn thành công");
        return "redirect:/admin/hoa-don";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        hoaDonService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xóa hóa đơn thành công");
        return "redirect:/admin/hoa-don";
    }

    private void loadFormAndList(Model model, HoaDon hoaDon, String keyword, String title) {
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hoaDons", hoaDonService.search(keyword));
        model.addAttribute("datPhongs", adminDatPhongService.search(""));
        model.addAttribute("khuyenMais", khuyenMaiService.findAll());
        model.addAttribute("nguoiDungs", userService.getAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
