package com.mycompany.tiketundianbaru;

import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Pengaturan Spring untuk seluruh aplikasi. Isinya empat hal:
 * koneksi MySQL, Hibernate sebagai mesin JPA, pengelola transaksi, dan penerjemah galat database.
 * Spring membuat semua objek ini dan menyuntikkannya ke kelas yang membutuhkan.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.mycompany.tiketundianbaru")
public class AppConfig {

    // Ubah tiga nilai ini jika pengaturan MySQL Anda berbeda.
    private static final String URL =
            "jdbc:mysql://localhost:3306/db_tiketundian?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource sumber = new DriverManagerDataSource();
        sumber.setDriverClassName("com.mysql.cj.jdbc.Driver");
        sumber.setUrl(URL);
        sumber.setUsername(USER);
        sumber.setPassword(PASSWORD);
        return sumber;
    }

    /** Hibernate berjalan di sini. Kelas bertanda @Entity di package ini ditemukan otomatis. */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean pabrik = new LocalContainerEntityManagerFactoryBean();
        pabrik.setDataSource(dataSource);
        pabrik.setPackagesToScan("com.mycompany.tiketundianbaru");
        pabrik.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties sifat = new Properties();
        // Menampilkan perintah SQL buatan Hibernate di jendela Output NetBeans.
        sifat.setProperty("hibernate.show_sql", "true");
        pabrik.setJpaProperties(sifat);
        return pabrik;
    }

    /** Membuka dan menutup transaksi untuk method bertanda @Transactional. */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /** Mengubah galat Hibernate di kelas @Repository menjadi DataAccessException milik Spring. */
    @Bean
    public static PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
