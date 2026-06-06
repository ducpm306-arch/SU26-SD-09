package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.LoaiPhong;
import su26sd09.su26sd09.service.PhongService;

@Controller
@RequestMapping("/admin/loai-phong")
public class AdminLoaiPhongController {

    @Autowired
    private PhongService phongService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        loadFormAndList(model, new LoaiPhong(), keyword, "Thêm loại phòng");
        return "admin/loai-phong-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        LoaiPhong loaiPhong = phongService.findLoaiPhongById(id);
        if (loaiPhong == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại phòng");
            return "redirect:/admin/loai-phong";
        }

        loadFormAndList(model, loaiPhong, keyword, "Cập nhật loại phòng");
        return "admin/loai-phong-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute LoaiPhong loaiPhong,
            RedirectAttributes redirectAttributes
    ) {
        phongService.saveLoaiPhong(loaiPhong);
        redirectAttributes.addFlashAttribute("success", "Lưu loại phòng thành công");
        return "redirect:/admin/loai-phong";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            phongService.deleteLoaiPhong(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại phòng thành công");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa loại phòng đang được sử dụng");
        }
        return "redirect:/admin/loai-phong";
    }

    private void loadFormAndList(Model model, LoaiPhong loaiPhong, String keyword, String title) {
        model.addAttribute("loaiPhong", loaiPhong);
        model.addAttribute("loaiPhongs", phongService.searchLoaiPhongAdmin(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
