package cart.service.cart;

import cart.config.auth.AuthMember;
import cart.domain.cart.CartId;
import cart.domain.member.MemberId;
import cart.domain.product.ProductId;
import cart.repository.cart.CartJdbcRepository;
import cart.repository.member.MemberJdbcRepository;
import cart.repository.product.ProductJdbcRepository;
import cart.service.member.GeneralMemberService;
import cart.service.product.GeneralProductService;
import cart.service.request.MemberCreateRequest;
import cart.service.request.ProductCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class GeneralCartServiceTest {
    private static final MemberCreateRequest MEMBER_CREATE_REQUEST
            = new MemberCreateRequest("헤나", "test@test.com", "test");
    private static final ProductCreateRequest PRODUCT_CREATE_REQUEST
            = new ProductCreateRequest("치킨", 10000, "image-url");

    @Autowired
    JdbcTemplate jdbcTemplate;

    GeneralMemberService generalMemberService;
    GeneralCartService generalCartService;
    GeneralProductService generalProductService;

    @BeforeEach
    void setUp() {
        generalMemberService = new GeneralMemberService(new MemberJdbcRepository(jdbcTemplate));
        generalCartService = new GeneralCartService(new CartJdbcRepository(jdbcTemplate), new ProductJdbcRepository(jdbcTemplate));
        generalProductService = new GeneralProductService(new ProductJdbcRepository(jdbcTemplate));
    }

    @DisplayName("장바구니에 상품을 추가한다.")
    @Test
    void addProduct() {
        // given
        final MemberId memberId = generalMemberService.save(MEMBER_CREATE_REQUEST);
        final ProductId productId = generalProductService.save(PRODUCT_CREATE_REQUEST);
        final AuthMember authMember = new AuthMember(memberId, "test@test.com");

        // when
        final CartId saveCartId = generalCartService.addProduct(authMember, productId);

        // then
        assertThat(saveCartId).isNotNull();
    }

    @DisplayName("장바구니에 상품을 삭제한다.")
    @Test
    void removeProduct() {
        // given
        final MemberId memberId = generalMemberService.save(MEMBER_CREATE_REQUEST);
        final ProductId productId = generalProductService.save(PRODUCT_CREATE_REQUEST);
        final AuthMember authMember = new AuthMember(memberId, "test@test.com");
        final CartId saveCartId = generalCartService.addProduct(authMember, productId);

        // when
        final int deleteCount = generalCartService.removeProduct(authMember, productId);

        // then
        assertThat(deleteCount).isOne();
    }
}
