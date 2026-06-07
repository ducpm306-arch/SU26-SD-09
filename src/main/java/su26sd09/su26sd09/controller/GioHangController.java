package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.CartCheckoutService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GioHangController {

    @Autowired
    private CartCheckoutService cartCheckoutService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/gio-hang")
    public String index() {
        return "gio-hang";
    }

    @PostMapping("/gio-hang/checkout")
    public String checkout(
            @RequestParam(name = "roomIds") List<Integer> roomIds,
            @RequestParam(name = "ngayNhan")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
            @RequestParam(name = "ngayTra")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
            @RequestParam(name = "nguoiLon", defaultValue = "1") int nguoiLon,
            @RequestParam(name = "treEm", defaultValue = "0") int treEm,
            @RequestParam(name = "maCccd", defaultValue = "") String maCccd,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập trước khi thanh toán giỏ hàng");
            return "redirect:/Login";
        }

        if (!ngayTra.isAfter(ngayNhan)) {
            redirectAttributes.addFlashAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng");
            return "redirect:/gio-hang";
        }

        if (!isValidCccd(maCccd)) {
            redirectAttributes.addFlashAttribute("error", "Vui long nhap so CCCD gom 12 chu so");
            return "redirect:/gio-hang";
        }

        NguoiDung khach = nguoiDungRepository.findByEmail(authentication.getName());
        try {
            cartCheckoutService.checkout(khach, roomIds, ngayNhan, ngayTra, nguoiLon, treEm, maCccd, "Dat tu gio hang");
            redirectAttributes.addFlashAttribute("success", "Đã tạo đơn đặt phòng và hóa đơn tạm tính từ giỏ hàng");
            return "redirect:/profiles";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/gio-hang";
        }
    }
    private boolean isValidCccd(String maCccd) {
        return maCccd != null && maCccd.matches("\\d{12}");
    }
}
