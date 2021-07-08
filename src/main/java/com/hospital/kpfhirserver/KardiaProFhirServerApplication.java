package com.hospital.kpfhirserver;

import com.hospital.kpfhirserver.fhir.SimpleRestfulServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
public class KardiaProFhirServerApplication {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(KardiaProFhirServerApplication.class, args);
    }

    /**
     * Servlet Registeration for HAPI FHIR
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean ServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new SimpleRestfulServer(context), "/fhir/*");
        registration.setName("FhirServlet");
        return registration;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
