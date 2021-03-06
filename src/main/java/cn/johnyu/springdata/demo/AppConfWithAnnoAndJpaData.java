package cn.johnyu.springdata.demo;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.johnyu.springdata.demo.pojo.Customer;
import cn.johnyu.springdata.demo.repository.CustomerRepository;
import cn.johnyu.springdata.demo.service.CustomerManagerImpl;

/**
 * 使用Annotation，DATA-JPA,MySQL完成测试
 * @author john
 *
 */
@Configuration
// @EnableJpaRepositories
// 可选以下配置
@EnableJpaRepositories(basePackages="cn.johnyu.springdata.demo"
,entityManagerFactoryRef="entityManagerFactory",transactionManagerRef="transactionManager")
@ComponentScan(basePackages = { "cn.johnyu.springdata.demo.service" })
@EnableTransactionManagement(proxyTargetClass = false)
public class AppConfWithAnnoAndJpaData {
	@Bean
	public DataSource getDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:mysql://localhost:3306/test", "root", "123");
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		return ds;
	}

	@Bean
	public JpaVendorAdapter getHibernateJpaVendorAdapter() {
		HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
		vendor.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
		return vendor;
	}

	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManageFactory() {
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
		emfb.setDataSource(getDataSource());
		emfb.setJpaVendorAdapter(getHibernateJpaVendorAdapter());
		emfb.setPackagesToScan("cn.johnyu.springdata.demo.pojo");
		emfb.setPersistenceUnitName("entityManager");

		
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
		jpaProperties.put("hibernate.show_sql", "true");
		jpaProperties.put("hibernate.hbm2ddl.auto", "create");
		emfb.setJpaProperties(jpaProperties);
		return emfb;
	}

	@Bean(name="transactionManager")
	public JpaTransactionManager getTransactionManager() {
		return new JpaTransactionManager((EntityManagerFactory) entityManageFactory().getObject());
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfWithAnnoAndJpaData.class);
		
		CustomerRepository cr=context.getBean(CustomerRepository.class);
		Customer c = new Customer();
		c.setCname("john");
		cr.save(c);

	}
}
