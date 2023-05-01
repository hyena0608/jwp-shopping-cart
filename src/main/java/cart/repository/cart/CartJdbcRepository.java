package cart.repository.cart;

import cart.domain.cart.Cart;
import cart.domain.cart.CartId;
import cart.domain.member.MemberId;
import cart.domain.product.ProductId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static cart.repository.cart.CartJdbcRepository.Table.*;

@Repository
public class CartJdbcRepository implements CartRepository {
    enum Table {
        TABLE("carts"),
        ID("id"),
        MEMBER_ID("member_id"),
        PRODUCT_ID("product_id");

        private final String name;

        Table(final String name) {
            this.name = name;
        }
    }

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public CartJdbcRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE.name)
                .usingGeneratedKeyColumns(ID.name);
    }

    @Override
    public CartId saveByMemberId(final MemberId memberId, final ProductId productId) {
        final long cartId = simpleJdbcInsert.executeAndReturnKey(cartProductParamSource(productId, memberId)).longValue();
        return CartId.from(cartId);
    }

    private SqlParameterSource cartProductParamSource(final ProductId productId, final MemberId memberId) {
        return new MapSqlParameterSource()
                .addValue(MEMBER_ID.name, memberId.getId())
                .addValue(PRODUCT_ID.name, productId.getId());
    }

    @Override
    public Optional<Cart> findByCartId(final CartId cartId) {
        final String sql = "SELECT * FROM carts WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, cartRowMapper, cartId.getId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private final RowMapper<Cart> cartRowMapper = (rs, rowNum) -> {
        return new Cart(
                CartId.from(rs.getLong(ID.name)),
                MemberId.from(rs.getLong(MEMBER_ID.name)),
                ProductId.from(rs.getLong(PRODUCT_ID.name))
        );
    };

    @Override
    public List<Cart> findAllByMemberId(final MemberId memberId) {
        final String sql = "SELECT * FROM carts WHERE member_id = ?";
        return jdbcTemplate.query(sql, rowMapperJoinProducts, memberId.getId());
    }

    private final RowMapper<Cart> rowMapperJoinProducts = (rs, rowNum) -> {
        final CartId cartId = CartId.from(rs.getLong(ID.name));
        final MemberId memberId = MemberId.from(rs.getLong(MEMBER_ID.name));
        final ProductId productId = ProductId.from(rs.getLong(PRODUCT_ID.name));

        return new Cart(cartId, memberId, productId);
    };

    @Override
    public int deleteByMemberId(final MemberId memberId, final ProductId productId) {
        final String sql = "DELETE FROM carts WHERE member_id = ? AND product_id = ?";
        return jdbcTemplate.update(sql, memberId.getId(), productId.getId());
    }
}
