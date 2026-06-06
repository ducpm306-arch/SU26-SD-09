package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.service.DanhGiaService;

@Controller
@RequestMapping("/admin/danh-gia")
public class AdminDanhGiaController {

    @Autowired
    private DanhGiaService danhGiaService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("danhGias", danhGiaService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/danh-gia-list";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.updateDuyet(id, true);
        redirectAttributes.addFlashAttribute("success", "Đã duyệt đánh giá");
        return "redirect:/admin/danh-gia";
    }

    @PostMapping("/hide/{id}")
    public String hide(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.updateDuyet(id, false);
        redirectAttributes.addFlashAttribute("success", "Đã ẩn đánh giá");
        return "redirect:/admin/danh-gia";
    }

    @PostMapping("/reply/{id}")
    public String reply(
            @PathVariable("id") int id,
            @RequestParam(name = "phanHoi", defaultValue = "") String phanHoi,
            RedirectAttributes redirectAttributes
    ) {
        danhGiaService.updatePhanHoi(id, phanHoi);
        redirectAttributes.addFlashAttribute("success", "Đã lưu phản hồi đánh giá");
        return "redirect:/admin/danh-gia";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.removeById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa đánh giá");
        return "redirect:/admin/danh-gia";
    }
}
