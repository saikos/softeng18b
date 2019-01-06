package gr.ntua.ece.softeng18b.data;

import gr.ntua.ece.softeng18b.data.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractInterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

public class DataAccess {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private static final int MAX_TOTAL_CONNECTIONS = 16;
    private static final int MAX_IDLE_CONNECTIONS = 8;
    
    private JdbcTemplate jdbcTemplate;
    private DataSourceTransactionManager tm;

    public void setup(String driverClass, String url, String user, String pass) throws SQLException {

        //initialize the data source
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(driverClass);
        bds.setUrl(url);
        bds.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        bds.setMaxIdle(MAX_IDLE_CONNECTIONS);
        bds.setUsername(user);
        bds.setPassword(pass);
        bds.setValidationQuery("SELECT 1");
        bds.setTestOnBorrow(true);
        bds.setDefaultAutoCommit(true);

        //check that everything works OK
        bds.getConnection().close();

        //initialize the jdbc template utilitiy
        jdbcTemplate = new JdbcTemplate(bds);
        
        tm = new DataSourceTransactionManager(bds);
    }

    public List<Product> getProducts(Limits limits) {
        //TODO: Support limits
        List<Product> products = jdbcTemplate.query("select * from product order by id", EMPTY_ARGS, new ProductRowMapper());
        for (Product p: products) {
            fetchTagsOfProduct(p);
        }
        return products;
    }

    public Product addProduct(String name, String description, String category, boolean withdrawn, String[] tags ) {
                
        TransactionTemplate transactionTemplate = new TransactionTemplate(tm);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);                
        
        long id = transactionTemplate.execute((TransactionStatus status) -> {
            
            //Create the new product record using a prepared statement                        
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            int rowCount = jdbcTemplate.update((Connection con) -> {
                PreparedStatement ps = con.prepareStatement(
                    "insert into product(name, description, category, withdrawn) values(?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setString(3, category);
                ps.setBoolean(4, withdrawn);                
                return ps;
            }, keyHolder);
            
            if (rowCount != 1) {
                throw new RuntimeException("New product not inserted");
            }
            
            long newId = keyHolder.getKey().longValue();
            
            if (tags != null && tags.length > 0) {
                jdbcTemplate.batchUpdate("insert into product_tags values(?, ?)", new AbstractInterruptibleBatchPreparedStatementSetter(){
                    @Override
                    protected boolean setValuesIfAvailable(PreparedStatement ps, int i) throws SQLException {
                        if (i < tags.length) {
                            ps.setLong(1, newId);
                            ps.setString(2, tags[i]);
                            return true;
                        }
                        else {
                            return false;
                        }
                    }

                });
            }                        
            
            return newId;
        });
        
        //New row has been added
        Product product = new Product(
            id,
            name,
            description,
            category,
            withdrawn                
        );  
        if (tags != null && tags.length > 0) {
            product.setTags(Arrays.asList(tags));
        }
        
        return product;        
    }

    public Optional<Product> getProduct(long id) {
        Long[] params = new Long[]{id};
        List<Product> products = jdbcTemplate.query("select * from product where id = ?", params, new ProductRowMapper());
        if (products.size() == 1)  {
            Product p = products.get(0);            
            fetchTagsOfProduct(p);
            return Optional.of(p);
        }
        else {
            return Optional.empty();
        }
    }
    
    protected void fetchTagsOfProduct(Product p) {
        Long[] params = new Long[]{p.getId()};
        List<String> tags = jdbcTemplate.query("select tag from product_tags where pid = ?", params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("tag");
            }            
        });  
        p.setTags(tags);
    }


}
