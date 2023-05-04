package cart.service.cart;

import cart.common.auth.AuthMember;
import cart.domain.cart.Cart;
import cart.domain.cart.CartId;
import cart.domain.product.ProductId;
import cart.repository.cart.CartRepository;
import cart.repository.product.ProductRepository;
import cart.service.response.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class GeneralCartService implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public GeneralCartService(final CartRepository cartRepository, final ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public CartId addProduct(final AuthMember authMember, final ProductId productId) {
        Optional<Cart> cart = cartRepository.findByMemberIdAndProductId(authMember.getId(), productId);

        if (cart.isPresent()) {
            throw new IllegalArgumentException("이미 장바구니에 등록되어 있는 상품입니다.");
        }

        return cartRepository.saveByMemberId(authMember.getId(), productId);
    }

    @Override
    public List<ProductResponse> findAllByMember(final AuthMember authMember) {
        return cartRepository.findAllByMemberId(authMember.getId())
                .stream()
                .map(Cart::getProductId)
                .map(productRepository::findByProductId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(product -> new ProductResponse(
                        product.getId().getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getImage()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public int removeProduct(final AuthMember authMember, final ProductId productId) {
        return cartRepository.deleteByMemberId(authMember.getId(), productId);
    }
}
