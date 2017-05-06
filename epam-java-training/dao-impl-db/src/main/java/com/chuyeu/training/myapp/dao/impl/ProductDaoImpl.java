package com.chuyeu.training.myapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.chuyeu.training.myapp.dao.api.IProductDao;
import com.chuyeu.training.myapp.dao.api.filters.CommonFilter;
import com.chuyeu.training.myapp.datamodel.Product;

@Repository
public class ProductDaoImpl  extends AbstractDaoImpl<Product>  implements IProductDao {

	@Inject
	private JdbcTemplate jdbcTemplate;
	
	protected ProductDaoImpl() {
        super(Product.class);
    }

	@Override
	public List<Product> getAll(CommonFilter commonFilter) {
		String sql = createSql(commonFilter);
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Product>(Product.class));
	}

	@Override
	public Integer getProductQuantity() {
		return jdbcTemplate.queryForObject("select count (*) from product", Integer.class);
	}

	@Override
	public Integer add(Product product) {

		final String INSERT_SQL = "insert into product (name, description, base_price, active) values(?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] { "id" });
				ps.setString(1, product.getName());
				ps.setString(2, product.getDescription());
				ps.setDouble(3, product.getBasePrice());
				ps.setBoolean(4, product.getActive());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Override
	public void update(Product product) {
		jdbcTemplate.update(
				"update product set name = ?, description = ?, base_price = ?, active = ?" + " where id = ?",
				product.getName(), product.getDescription(), product.getBasePrice(), product.getActive(),
				product.getId());
	}
	
	private String createSql(CommonFilter commonFilter) {

		Integer offset = commonFilter.getLimit() * (commonFilter.getPageNumber() - 1);
		StringBuilder stringBuilder = new StringBuilder("select * from product ");

		if (commonFilter.getSort() != null && commonFilter.getSort().getColumn() != null) {
			stringBuilder.append("order by ").append(commonFilter.getSort().getColumn());

			if ("desc".equals(commonFilter.getSort().getDirection())) {
				stringBuilder.append(" desc");
			}
		}

		stringBuilder.append(" limit ");
		stringBuilder.append(commonFilter.getLimit());
		stringBuilder.append(" offset ");
		stringBuilder.append(offset);
		return stringBuilder.toString();
	}

}
