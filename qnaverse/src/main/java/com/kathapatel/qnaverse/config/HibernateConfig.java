package com.kathapatel.qnaverse.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableTransactionManagement  // to manage transaction declaratively by spring 
public class HibernateConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");  //MySQL JDBC driver class 
        ds.setUrl("jdbc:mysql://localhost:3306/qnaverse?createDatabaseIfNotExist=true");  // connection url
        ds.setUsername("root");
        ds.setPassword(""); 
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);  // wire data source to session factory 
        factoryBean.setPackagesToScan("com.kathapatel.qnaverse.model"); 
        factoryBean.setHibernateProperties(hibernateProperties()); //load hibernate configuration properties 
        return factoryBean;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update"); //automatically updates schema based on entity changed  
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.current_session_context_class", "thread");
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory); // transaction manager link to the session factory 
        return txManager;
    }
}
