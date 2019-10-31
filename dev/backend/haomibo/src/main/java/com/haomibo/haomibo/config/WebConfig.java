package com.haomibo.haomibo.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;


/**
 * Web configurations.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
}
