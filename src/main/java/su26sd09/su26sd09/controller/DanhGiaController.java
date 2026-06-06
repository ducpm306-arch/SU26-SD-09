package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.AdminDatPhongService;
import su26sd09.su26sd09.service.DanhGiaService;

@Controller
public class DanhGiaController {

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private AdminDatPhongService adminDatPhongService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @PostMapping("/phong/{id}/danh-gia")
    public String saveReview(
            @PathVariable("id") int maPhong,
            @RequestParam(name = "diemDanhGia") int diemDanhGia,
            @RequestParam(name = "noiDung", defaultValue = "") String noiDung,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập trước khi đánh giá phòng");
            return "redirect:/Login";
        }

        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(authentication.getName());
        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản đăng nhập");
            return "redirect:/phong/" + maPhong;
        }

        DatPhong datPhong = adminDatPhongService.findLatestBookingByUserAndRoom(nguoiDung.getMaNguoiDung(), maPhong);
        if (datPhong == null) {
            redirectAttributes.addFlashAttribute("error", "Chỉ khách đã đặt phòng này mới có thể đánh giá");
            return "redirect:/phong/" + maPhong;
        }

        danhGiaService.createReview(nguoiDung, datPhong, diemDanhGia, noiDung);
        redirectAttributes.addFlashAttribute("success", "Đã gửi đánh giá, vui lòng chờ duyệt");
        return "redirect:/phong/" + maPhong;
    }
}
