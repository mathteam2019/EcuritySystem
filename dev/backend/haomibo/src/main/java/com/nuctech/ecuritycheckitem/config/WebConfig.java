/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Web configurations）
 * 文件名：	WebConfig.java
 * 描述：	Web configurations
 * 作者名：	Sandy
 * 日期：	2019/10/14
 *
 */

package com.nuctech.ecuritycheckitem.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import org.apache.catalina.filters.RemoteIpFilter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableJpaRepositories("com.nuctech.ecuritycheckitem.repositories")
@EnableElasticsearchRepositories(basePackages = "com.nuctech.ecuritycheckitem.es.repositories")
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setThreadNamePrefix("EcurityCheck-");
        executor.initialize();
        return executor;
    }

    /**
     * Allow CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }

    /**
     * Resource handlers for serving files.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // We can use System.getProperty("user.dir") too.
        String baseAbsolutePath = Paths.get("").toAbsolutePath().toString() + File.separator;

        registry
                .addResourceHandler(Constants.PORTRAIT_FILE_SERVING_BASE_URL + "**")
                .addResourceLocations("file:///" + baseAbsolutePath + Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY + File.separator);

    }

    /**
     * Used for filtering object for json conversion
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(getConfiguredHibernateModule());

        subscribeFiltersInMapper(mapper);

        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

        converter.setObjectMapper(mapper);
        converters.add(converter);
    }

    /**
     * Used for filtering object for json conversion
     */
    private Hibernate5Module getConfiguredHibernateModule() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Hibernate5Module module = new Hibernate5Module(sessionFactory);
        module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);

        return module;

    }

    /**
     * Used for filtering object for json conversion
     */
    private void subscribeFiltersInMapper(ObjectMapper mapper) {

        mapper.setFilterProvider(ModelJsonFilters.getDefaultFilters());

    }

    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }
}
