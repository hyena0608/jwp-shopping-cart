package cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cart.domain.Product;
import cart.dto.ProductDto;
import cart.controller.request.ProductCreateRequest;
import cart.repository.ProductRepository;

@Transactional(readOnly = true)
@Service
public class GeneralProductService implements ProductService {
	private final ProductRepository productRepository;

	public GeneralProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<ProductDto> findAll() {
		return productRepository.findAll()
			.stream()
			.map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getImage()))
			.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public long save(ProductCreateRequest request) {
		return productRepository.save(request);
	}

	@Override
	public long deleteByProductId(long productId) {
		final boolean isDelete = productRepository.deleteByProductId(productId) == productId;

		if (!isDelete) {
			throw new IllegalStateException("상품 삭제에 실패했습니다.");
		}

		return productId;
	}
}
