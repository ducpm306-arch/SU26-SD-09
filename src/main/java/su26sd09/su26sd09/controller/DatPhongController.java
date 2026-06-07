package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.CartCheckoutService;
import su26sd09.su26sd09.service.PhongService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DatPhongController {

    @Autowired
    private CartCheckoutService cartCheckoutService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @PostMapping({"/dat-phong", "/dat-phong/quick"})
    public String quickBooking(
            @RequestParam(name = "maPhong", required = false) Integer maPhong,
            @RequestParam(name = "ngayNhan")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
            @RequestParam(name = "ngayTra")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
            @RequestParam(name = "nguoiLon", defaultValue = "1") int nguoiLon,
            @RequestParam(name = "treEm", defaultValue = "0") int treEm,
            @RequestParam(name = "maCccd", defaultValue = "") String maCccd,
            @RequestParam(name = "yeuCauThem", defaultValue = "") String yeuCauThem,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập trước khi đặt phòng");
            return "redirect:/Login";
        }

        if (maPhong == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn phòng cần đặt");
            return "redirect:/loai-phong";
        }

        if (!ngayTra.isAfter(ngayNhan)) {
            redirectAttributes.addFlashAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng");
            return "redirect:/phong/" + maPhong;
        }

        if (!isValidCccd(maCccd)) {
            redirectAttributes.addFlashAttribute("error", "Vui long nhap so CCCD gom 12 chu so");
            return "redirect:/phong/" + maPhong;
        }

        Phong phong = phongService.findById(maPhong);
        if (phong == null || !"Trong".equals(phong.getTrangThai())) {
            redirectAttributes.addFlashAttribute("error", "Phòng không khả dụng");
            return "redirect:/phong";
        }

        NguoiDung khach = nguoiDungRepository.findByEmail(authentication.getName());
        cartCheckoutService.checkout(khach, List.of(phong.getMaPhong()), ngayNhan, ngayTra, nguoiLon, treEm, maCccd, yeuCauThem);
        redirectAttributes.addFlashAttribute("success", "Đặt phòng thành công, vui lòng chờ nhân viên xác nhận");
        return "redirect:/profiles";
    }
    private boolean isValidCccd(String maCccd) {
        return maCccd != null && maCccd.matches("\\d{12}");
    }
}
