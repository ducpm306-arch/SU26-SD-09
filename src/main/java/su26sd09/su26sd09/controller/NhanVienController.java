package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.service.AdminDatPhongService;
import su26sd09.su26sd09.service.DanhGiaService;
import su26sd09.su26sd09.service.KhuyenMaiService;
import su26sd09.su26sd09.service.PhongService;
import su26sd09.su26sd09.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {

    @Autowired
    private AdminDatPhongService adminDatPhongService;

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String index() {
        return "redirect:/nhan-vien/dat-phong";
    }

    @GetMapping("/dat-phong")
    public String datPhongIndex(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            Principal principal
    ) {
        DatPhong datPhong = new DatPhong();
        datPhong.setSonguoiLon(2);
        datPhong.setSotreEm(0);
        datPhong.setTrangThai("Cho xac nhan");

        NguoiDung nhanVienDangNhap = resolveNhanVienDangNhap(principal);
        datPhong.setNv(nhanVienDangNhap);

        loadDatPhongFormAndList(model, datPhong, keyword, null, null, null, nhanVienDangNhap, "Them don dat phong");
        return "nhan-vien/dat-phong-list";
    }

    @GetMapping("/dat-phong/edit/{id}")
    public String datPhongEdit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        DatPhong datPhong = adminDatPhongService.findById(id);
        if (datPhong == null) {
            redirectAttributes.addFlashAttribute("error", "Khong tim thay don dat phong");
            return "redirect:/nhan-vien/dat-phong";
        }

        LocalDate ngayNhan = datPhong.getNgaydatPhong() == null ? null : datPhong.getNgaydatPhong().toLocalDate();
        LocalDate ngayTra = datPhong.getNgaytraPhong() == null ? null : datPhong.getNgaytraPhong().toLocalDate();
        Integer selectedPhongId = adminDatPhongService.findPhongIdByDatPhong(id);
        NguoiDung nhanVienDangNhap = resolveNhanVienDangNhap(principal);
        if (nhanVienDangNhap != null) {
            datPhong.setNv(nhanVienDangNhap);
        }

        loadDatPhongFormAndList(model, datPhong, keyword, selectedPhongId, ngayNhan, ngayTra, nhanVienDangNhap, "Cap nhat don dat phong");
        return "nhan-vien/dat-phong-list";
    }

    @PostMapping("/dat-phong/save")
    public String datPhongSave(
            @ModelAttribute DatPhong datPhong,
            @RequestParam(name = "maKhach", required = false) Integer maKhach,
            @RequestParam(name = "maPhong", required = false) Integer maPhong,
            @RequestParam(name = "ngayNhan", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
            @RequestParam(name = "ngayTra", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        NguoiDung nhanVienDangNhap = resolveNhanVienDangNhap(principal);
        Integer maNhanVien = nhanVienDangNhap == null ? null : nhanVienDangNhap.getMaNguoiDung();

        adminDatPhongService.save(datPhong, maKhach, maNhanVien, maPhong, ngayNhan, ngayTra);
        redirectAttributes.addFlashAttribute("success", "Luu don dat phong thanh cong");
        return "redirect:/nhan-vien/dat-phong";
    }

    @PostMapping("/dat-phong/delete/{id}")
    public String datPhongDelete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        adminDatPhongService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xoa don dat phong thanh cong");
        return "redirect:/nhan-vien/dat-phong";
    }

    @GetMapping("/danh-gia")
    public String danhGiaIndex(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("danhGias", danhGiaService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "nhan-vien/danh-gia-list";
    }

    @PostMapping("/danh-gia/approve/{id}")
    public String danhGiaApprove(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.updateDuyet(id, true);
        redirectAttributes.addFlashAttribute("success", "Da duyet danh gia");
        return "redirect:/nhan-vien/danh-gia";
    }

    @PostMapping("/danh-gia/hide/{id}")
    public String danhGiaHide(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.updateDuyet(id, false);
        redirectAttributes.addFlashAttribute("success", "Da an danh gia");
        return "redirect:/nhan-vien/danh-gia";
    }

    @PostMapping("/danh-gia/reply/{id}")
    public String danhGiaReply(
            @PathVariable("id") int id,
            @RequestParam(name = "phanHoi", defaultValue = "") String phanHoi,
            RedirectAttributes redirectAttributes
    ) {
        danhGiaService.updatePhanHoi(id, phanHoi);
        redirectAttributes.addFlashAttribute("success", "Da luu phan hoi danh gia");
        return "redirect:/nhan-vien/danh-gia";
    }

    @PostMapping("/danh-gia/delete/{id}")
    public String danhGiaDelete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        danhGiaService.removeById(id);
        redirectAttributes.addFlashAttribute("success", "Da xoa danh gia");
        return "redirect:/nhan-vien/danh-gia";
    }

    @GetMapping("/khach-hang")
    public String khachHangIndex(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("nguoiDungs", userService.searchKhachHang(keyword));
        model.addAttribute("keyword", keyword);
        return "nhan-vien/khach-hang-list";
    }

    @PostMapping("/khach-hang/update/{id}")
    public String khachHangUpdate(
            @PathVariable("id") int id,
            @RequestParam(name = "trangThai", defaultValue = "false") boolean trangThai,
            RedirectAttributes redirectAttributes
    ) {
        userService.updateStatusAndRole(id, trangThai, null);
        redirectAttributes.addFlashAttribute("success", "Da cap nhat khach hang");
        return "redirect:/nhan-vien/khach-hang";
    }

    @GetMapping("/phong")
    public String phongIndex(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        Phong phong = new Phong();
        phong.setHoatDong(true);
        phong.setTrangThai("Trong");

        loadPhongFormAndList(model, phong, List.of(), keyword, "Them phong");
        return "nhan-vien/phong-list";
    }

    @GetMapping("/phong/edit/{id}")
    public String phongEdit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Phong phong = phongService.findById(id);
        if (phong == null) {
            redirectAttributes.addFlashAttribute("error", "Khong tim thay phong");
            return "redirect:/nhan-vien/phong";
        }

        loadPhongFormAndList(model, phong, phongService.findTienNghiIdsByPhong(id), keyword, "Cap nhat phong");
        return "nhan-vien/phong-list";
    }

    @PostMapping("/phong/save")
    public String phongSave(
            @ModelAttribute Phong phong,
            @RequestParam(name = "loaiPhongId") int loaiPhongId,
            @RequestParam(name = "tienNghiIds", required = false) List<Integer> tienNghiIds,
            RedirectAttributes redirectAttributes
    ) {
        phongService.save(phong, loaiPhongId, tienNghiIds);
        redirectAttributes.addFlashAttribute("success", "Luu phong thanh cong");
        return "redirect:/nhan-vien/phong";
    }

    @PostMapping("/phong/delete/{id}")
    public String phongDelete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        phongService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xoa phong thanh cong");
        return "redirect:/nhan-vien/phong";
    }

    @GetMapping("/khuyen-mai")
    public String khuyenMaiIndex(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            Principal principal
    ) {
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setHoatDong(true);
        NguoiDung nhanVienDangNhap = resolveNhanVienDangNhap(principal);
        khuyenMai.setN(nhanVienDangNhap);

        loadKhuyenMaiFormAndList(model, khuyenMai, keyword, nhanVienDangNhap, "Them khuyen mai");
        return "nhan-vien/khuyen-mai-list";
    }

    @GetMapping("/khuyen-mai/edit/{id}")
    public String khuyenMaiEdit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        KhuyenMai khuyenMai = khuyenMaiService.findById(id);
        if (khuyenMai == null) {
            redirectAttributes.addFlashAttribute("error", "Khong tim thay khuyen mai");
            return "redirect:/nhan-vien/khuyen-mai";
        }

        NguoiDung nhanVienDangNhap = resolveNhanVienDangNhap(principal);
        if (nhanVienDangNhap != null) {
            khuyenMai.setN(nhanVienDangNhap);
        }

        loadKhuyenMaiFormAndList(model, khuyenMai, keyword, nhanVienDangNhap, "Cap nhat khuyen mai");
        return "nhan-vien/khuyen-mai-list";
    }

    @PostMapping("/khuyen-mai/save")
    public String khuyenMaiSave(
            @ModelAttribute KhuyenMai khuyenMai,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        khuyenMai.setN(resolveNhanVienDangNhap(principal));
        khuyenMaiService.save(khuyenMai);
        redirectAttributes.addFlashAttribute("success", "Luu khuyen mai thanh cong");
        return "redirect:/nhan-vien/khuyen-mai";
    }

    @PostMapping("/khuyen-mai/delete/{id}")
    public String khuyenMaiDelete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        khuyenMaiService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Xoa khuyen mai thanh cong");
        return "redirect:/nhan-vien/khuyen-mai";
    }

    private void loadDatPhongFormAndList(
            Model model,
            DatPhong datPhong,
            String keyword,
            Integer selectedPhongId,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            NguoiDung nhanVienDangNhap,
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
        model.addAttribute("nhanVienDangNhap", nhanVienDangNhap);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }

    private void loadPhongFormAndList(
            Model model,
            Phong phong,
            List<Integer> selectedTienNghiIds,
            String keyword,
            String title
    ) {
        List<Phong> phongs = phongService.search(keyword);

        Map<Integer, List<String>> tienNghiTheoPhong = new HashMap<>();
        for (Phong item : phongs) {
            tienNghiTheoPhong.put(item.getMaPhong(), phongService.findTenTienNghiByPhong(item.getMaPhong()));
        }

        model.addAttribute("phong", phong);
        model.addAttribute("phongs", phongs);
        model.addAttribute("loaiPhongs", phongService.findAllLoaiPhong());
        model.addAttribute("tienNghis", phongService.findAllTienNghi());
        model.addAttribute("selectedTienNghiIds", selectedTienNghiIds);
        model.addAttribute("tienNghiTheoPhong", tienNghiTheoPhong);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }

    private void loadKhuyenMaiFormAndList(
            Model model,
            KhuyenMai khuyenMai,
            String keyword,
            NguoiDung nhanVienDangNhap,
            String title
    ) {
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("khuyenMais", khuyenMaiService.search(keyword));
        model.addAttribute("nhanVienDangNhap", nhanVienDangNhap);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }

    private NguoiDung resolveNhanVienDangNhap(Principal principal) {
        String email = principal == null ? null : principal.getName();

        if (email == null || email.isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                email = authentication.getName();
            }
        }

        if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
            return null;
        }
        return userService.findByEmail(email);
    }
}
