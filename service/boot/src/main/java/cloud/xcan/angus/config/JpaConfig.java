package cloud.xcan.angus.config;

import cloud.xcan.angus.core.jpa.repository.BaseRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class JpaConfig {

  @EnableJpaAuditing
  @EnableTransactionManagement
  @EnableJpaRepositories(
      repositoryBaseClass = BaseRepositoryImpl.class,
      entityManagerFactoryRef = "entityManagerFactory",
      transactionManagerRef = "transactionManager",
      basePackages = {"cloud.xcan.angus.idgen.dao",
          "cloud.xcan.angus.core.gm.infra.persistence.mysql.**",
          "cloud.xcan.angus.core.storage.infra.persistence.mysql.**" /* For Private Edition */
      })
  @ConditionalOnProperty(name = "xcan.datasource.extra.dbType", havingValue = "MYSQL")
  protected static class JpaEnableMysqlConfiguration {

  }

  @EnableJpaAuditing
  @EnableTransactionManagement
  @EnableJpaRepositories(
      repositoryBaseClass = BaseRepositoryImpl.class,
      entityManagerFactoryRef = "entityManagerFactory",
      transactionManagerRef = "transactionManager",
      basePackages = {"cloud.xcan.angus.idgen.dao",
          "cloud.xcan.angus.core.gm.infra.persistence.postgres.**",
          "cloud.xcan.angus.core.storage.infra.persistence.postgres.**" /* For Private Edition */
      })
  @ConditionalOnProperty(name = "xcan.datasource.extra.dbType", havingValue = "POSTGRES")
  protected static class JpaEnablePostgresConfiguration {

  }

}
