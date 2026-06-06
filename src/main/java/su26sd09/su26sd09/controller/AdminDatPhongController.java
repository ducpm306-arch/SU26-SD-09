package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import su26sd09.su26sd09.service.AdminDatPhongService;
import su26sd09.su26sd09.service.PhongService;
import su26sd09.su26sd09.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/dat-phong")
public class AdminDatPhongController {

    @Autowired
    private AdminDatPhongService adminDatPhongService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhongService phongService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        DatPhong datPhong = new DatPhong();
        datPhong.setSonguoiLon(2);
        datPhong.setSotreEm(0);
        datPhong.setTrangThai("Cho xac nhan");

        loadFormAndList(model, datPhong, keyword, null, null, null, "Thêm đơn đặt phòng");
        return "admin/dat-phong-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        DatPhong datPhong = adminDatPhongService.findById(id);
        if (datPhong == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn đặt phòng");
            return "redirect:/admin/dat-phong";
        }

        LocalDate ngayNhan = datPhong.getNgaydatPhong() == null ? null : datPhong.getNgaydatPhong().toLocalDate();
        LocalDate ngayTra = datPhong.getNgaytraPhong() == null ? null : datPhong.getNgaytraPhong().toLocalDate();
        Integer selectedPhongId = adminDatPhongService.findPhongIdByDatPhong(id);

        loadFormAndList(model, datPhong, keyword, selectedPhongId, ngayNhan, ngayTra, "Cập nhật đơn đặt phòng");
        return "admin/dat-phong-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute DatPhong datPhong,
            @RequestParam(name = "maKhach", required = false) Integer maKhach,
            @RequestParam(name = "maNhanVien", required = false) Integer maNhanVien,
            @RequestParam(name = "maPhong", required = false) Integer maPhong,
            @RequestParam(name = "ngayNhan", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
            @RequestParam(name = "ngayTra", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
            RedirectAttributes redirectAttributes
    ) {
        adminDatPhongService.save(datPhong, maKhach, maNhanVien, maPhong, ngayNhan, ngayTra);
        redirectAttributes.addFlashAttribute("success", "Lưu đơn đặt phòng thành công");
        return "redirect:/admin/dat-phong";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        adminDatPhongService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xóa đơn đặt phòng thành công");
        return "redirect:/admin/dat-phong";
    }

    private void loadFormAndList(
            Model model,
            DatPhong datPhong,
            String keyword,
            Integer selectedPhongId,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            String title
    ) {
        List<DatPhong> datPhongs = adminDatPhongService.search(keyword);

        model.addAttribute("datPhong", datPhong);
        model.addAttribute("datPhongs", datPhongs);
        model.addAttribute("phongTheoDon", adminDatPhongService.buildRoomLabelByDatPhong(datPhongs));
        model.addAttribute("nguoiDungs", userService.getAll());
        model.addAttribute("phongs", phongService.findAllPhong());
        model.addAttribute("selectedPhongId", selectedPhongId);
        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
