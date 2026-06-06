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
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.service.KhuyenMaiService;
import su26sd09.su26sd09.service.UserService;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class AdminKhuyenMaiController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setHoatDong(true);
        loadFormAndList(model, khuyenMai, keyword, "Thêm khuyến mãi");
        return "admin/khuyen-mai-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        KhuyenMai khuyenMai = khuyenMaiService.findById(id);
        if (khuyenMai == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi");
            return "redirect:/admin/khuyen-mai";
        }

        loadFormAndList(model, khuyenMai, keyword, "Cập nhật khuyến mãi");
        return "admin/khuyen-mai-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute KhuyenMai khuyenMai,
            @RequestParam(name = "maNguoiTao", required = false) Integer maNguoiTao,
            RedirectAttributes redirectAttributes
    ) {
        NguoiDung nguoiTao = maNguoiTao == null ? null : userService.Getbyid(maNguoiTao);
        khuyenMai.setN(nguoiTao);
        khuyenMaiService.save(khuyenMai);
        redirectAttributes.addFlashAttribute("success", "Lưu khuyến mãi thành công");
        return "redirect:/admin/khuyen-mai";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        khuyenMaiService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công");
        return "redirect:/admin/khuyen-mai";
    }

    private void loadFormAndList(Model model, KhuyenMai khuyenMai, String keyword, String title) {
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("khuyenMais", khuyenMaiService.search(keyword));
        model.addAttribute("nguoiDungs", userService.getAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
