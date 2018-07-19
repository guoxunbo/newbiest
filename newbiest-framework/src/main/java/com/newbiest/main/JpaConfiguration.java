package com.newbiest.main;

import com.newbiest.base.repository.custom.RepositoryFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 定义数据访问事务
 * Created by guoxunbo on 2017/9/14.
 */

@Configuration
@EnableTransactionManagement(proxyTargetClass = true) // 启动事务管理以及CGLIB代理
@EnableJpaRepositories(basePackages = "com.newbiest", repositoryFactoryBeanClass = RepositoryFactoryBean.class)
@EntityScan(basePackages = "com.newbiest.**.model")
public class JpaConfiguration {

    /**
     * 使用spring的异常体系
     * @return
     */
    @Bean
    PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
