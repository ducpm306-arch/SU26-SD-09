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
import su26sd09.su26sd09.service.UserService;

@Controller
@RequestMapping("/admin/nguoi-dung")
public class AdminNguoiDungController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("nguoiDungs", userService.search(keyword));
        model.addAttribute("vaiTros", userService.getAllVaiTro());
        model.addAttribute("keyword", keyword);
        return "admin/nguoi-dung-list";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable("id") int id,
            @RequestParam(name = "trangThai", defaultValue = "false") boolean trangThai,
            @RequestParam(name = "vaiTroId", required = false) Integer vaiTroId,
            RedirectAttributes redirectAttributes
    ) {
        userService.updateStatusAndRole(id, trangThai, vaiTroId);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật người dùng");
        return "redirect:/admin/nguoi-dung";
    }
}
