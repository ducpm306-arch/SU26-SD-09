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
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.service.NhanVienService;
import su26sd09.su26sd09.service.UserService;

@Controller
@RequestMapping("/admin/nhan-vien")
public class AdminNhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        loadFormAndList(model, new Nhanvien(), keyword, "Thêm nhân viên");
        return "admin/nhan-vien-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Nhanvien nhanVien = nhanVienService.findById(id);
        if (nhanVien == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
            return "redirect:/admin/nhan-vien";
        }

        loadFormAndList(model, nhanVien, keyword, "Cập nhật nhân viên");
        return "admin/nhan-vien-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Nhanvien nhanVien,
            @RequestParam(name = "maNguoiDung", required = false) Integer maNguoiDung,
            RedirectAttributes redirectAttributes
    ) {
        NguoiDung nguoiDung = maNguoiDung == null ? null : userService.Getbyid(maNguoiDung);
        nhanVien.setN(nguoiDung);
        nhanVienService.save(nhanVien);
        redirectAttributes.addFlashAttribute("success", "Lưu nhân viên thành công");
        return "redirect:/admin/nhan-vien";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        nhanVienService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công");
        return "redirect:/admin/nhan-vien";
    }

    private void loadFormAndList(Model model, Nhanvien nhanVien, String keyword, String title) {
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("nhanViens", nhanVienService.search(keyword));
        model.addAttribute("nguoiDungs", userService.getNhanVienUsers());
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
