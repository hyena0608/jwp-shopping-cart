package cart.controller.view;

import cart.config.auth.BasicAuth;
import cart.domain.member.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/cart")
@Controller
public class CartViewController {

    @GetMapping
    public String getCart(@BasicAuth Member member) {
        return "cart";
    }
}
