package cart.member.ui;

import cart.member.service.MemberService;
import cart.member.service.response.MemberResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MemberViewController {
    private final MemberService memberService;

    public MemberViewController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/settings")
    public String getMembers(Model model) {
        final List<MemberResponse> findMembers = memberService.findAll();
        model.addAttribute("members", findMembers);
        return "settings";
    }
}
