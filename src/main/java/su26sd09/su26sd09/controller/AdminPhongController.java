package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.service.PhongService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/phong")
public class AdminPhongController {

    @Autowired
    private PhongService phongService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Phong phong = new Phong();
        phong.setHoatDong(true);
        phong.setTrangThai("Trong");

        loadPage(model, phong, List.of(), keyword, page, size, "Them phong");
        return "admin/phong-list";
    }

    @GetMapping("/create")
    public String create() {
        return "redirect:/admin/phong";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable int id,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Phong phong = phongService.findById(id);

        if (phong == null) {
            redirectAttributes.addFlashAttribute("error", "Khong tim thay phong");
            return "redirect:/admin/phong";
        }

        loadPage(model, phong, phongService.findTienNghiIdsByPhong(id), keyword, page, size, "Cap nhat phong");
        return "admin/phong-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Phong phong,
            @RequestParam int loaiPhongId,
            @RequestParam(required = false) List<Integer> tienNghiIds,
            RedirectAttributes redirectAttributes
    ) {
        phongService.save(phong, loaiPhongId, tienNghiIds);
        redirectAttributes.addFlashAttribute("success", "Luu phong thanh cong");
        return "redirect:/admin/phong";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        phongService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xoa phong thanh cong");
        return "redirect:/admin/phong";
    }

    private void loadPage(
            Model model,
            Phong phong,
            List<Integer> selectedTienNghiIds,
            String keyword,
            int page,
            int size,
            String title
    ) {
        Page<Phong> phongPage = phongService.search(
                keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "maPhong"))
        );

        Map<Integer, List<String>> tienNghiTheoPhong = new HashMap<>();
        for (Phong item : phongPage.getContent()) {
            tienNghiTheoPhong.put(item.getMaPhong(), phongService.findTenTienNghiByPhong(item.getMaPhong()));
        }

        model.addAttribute("phong", phong);
        model.addAttribute("phongPage", phongPage);
        model.addAttribute("loaiPhongs", phongService.findAllLoaiPhong());
        model.addAttribute("tienNghis", phongService.findAllTienNghi());
        model.addAttribute("selectedTienNghiIds", selectedTienNghiIds);
        model.addAttribute("tienNghiTheoPhong", tienNghiTheoPhong);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("title", title);
    }
}
