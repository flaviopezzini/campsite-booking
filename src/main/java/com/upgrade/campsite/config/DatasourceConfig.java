package com.upgrade.campsite.config;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatasourceConfig {

  private String driverClassName;
  private String jdbcUrl;
  private int connectionTimeoutInSeconds;

  public DatasourceConfig(@Value("${spring.datasource.driverClassName}") String driverClassName,
      @Value("${spring.datasource.url}") String jdbcUrl,
      @Value("${spring.datasource.connection-timeout}") int connectionTimeout) {
    super();
    this.driverClassName = driverClassName;
    this.jdbcUrl = jdbcUrl;
    this.connectionTimeoutInSeconds = connectionTimeout;
  }

  @Bean
  @Primary
  public DataSource dataSource() throws SQLException {
    DataSource dataSource = DataSourceBuilder.create().url(jdbcUrl).driverClassName(driverClassName).build();
    dataSource.setLoginTimeout(connectionTimeoutInSeconds);
    return dataSource;
  }

}
