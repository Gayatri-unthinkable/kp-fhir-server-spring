package com.hospital.kpfhirserver.fhir;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Interceptor used to customize Capability Statement
 * created automatically by HAPI-FHIR
 */
@Interceptor
//@PropertySource("classpath:application.properties")
@Component
public class CapabilityStatementCustomizer {

    @Value("${capabilityStatement.software.name}")
    String name;
    @Value("${capabilityStatement.software.version}")
    String version;
    @Value("${capabilityStatement.software.releaseDate}")
    String releaseDate;
    @Value("${capabilityStatement.publisher}")
    String publisher;
    @Value("${capabilityStatement.copyright}")
    String copyright;
    @Value("${capabilityStatement.patient.searchComponent.name.documentation}")
    String patientSearchComponentName;
    @Value("${capabilityStatement.patient.searchComponent.identifier.documentation}")
    String patientSearchComponentIdentifier;
    @Value("${capabilityStatement.observation.searchComponent.identifier.documentation}")
    String obsSearchComponentIdentifier;
    private Logger logger = LoggerFactory.getLogger(CapabilityStatementCustomizer.class);

    /**
     * Implement a hook method for the SERVER_CAPABILITY_STATEMENT_GENERATED pointcut
     */
    @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
    public void customize(IBaseConformance theCapabilityStatement) throws IOException {

    /*   Properties prop = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("application.properties");
        prop.load(input);

        // Loading properties from config.properties file
        String name = prop.getProperty("capabilityStatement.software.name");
        String version = prop.getProperty("capabilityStatement.software.version");
        String releaseDate = prop.getProperty("capabilityStatement.software.releaseDate");
        String publisher = prop.getProperty("capabilityStatement.publisher");
        String copyright = prop.getProperty("capabilityStatement.copyright");
        String patientSearchComponentName = prop.getProperty("capabilityStatement.patient.searchComponent.name.documentation");
        String patientSearchComponentIdentifier = prop.getProperty("capabilityStatement.patient.searchComponent.identifier.documentation");
        String obsSearchComponentIdentifier = prop.getProperty("capabilityStatement.observation.searchComponent.identifier.documentation");
*/
        // Cast to the appropriate version
        CapabilityStatement cs = (CapabilityStatement) theCapabilityStatement;
        // Customize the CapabilityStatement as desired
        cs.getSoftware()
                .setName(name)
                .setVersion(version)
                .setReleaseDateElement(new DateTimeType(releaseDate));
        cs.setPublisher(publisher);
        cs.setCopyright(copyright);
        CodeType fhirJsonCodeType = new CodeType();
        fhirJsonCodeType.setValue("application/fhir+json");
        CodeType jsonCodeType = new CodeType();
        jsonCodeType.setValue("application/fhir+xml");
        cs.getFormat().clear();
        cs.getFormat().add(fhirJsonCodeType);
        cs.getFormat().add(jsonCodeType);
        //logger.info("Name of the software --{}", name);
        for (CapabilityStatement.CapabilityStatementRestResourceComponent resource : cs.getRest().get(0).getResource()) {
            if (resource.getType().equalsIgnoreCase("Patient")) {
                for (CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent searchComponent : resource.getSearchParam()) {
                    if (searchComponent.getName().equalsIgnoreCase("name")) {
                        searchComponent.setDocumentation(patientSearchComponentName);
                        searchComponent.setDefinition(null);
                    }
                    if (searchComponent.getName().equalsIgnoreCase("identifier")) {
                        searchComponent.setDocumentation(patientSearchComponentIdentifier);
                        searchComponent.setType(Enumerations.SearchParamType.STRING);
                        searchComponent.setDefinition(null);
                    }
                }
            } else if (resource.getType().equalsIgnoreCase("Observation")) {
                for (CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent searchComponent : resource.getSearchParam()) {
                    if (searchComponent.getName().equalsIgnoreCase("identifier")) {
                        searchComponent.setDocumentation(obsSearchComponentIdentifier);
                        searchComponent.setType(Enumerations.SearchParamType.STRING);
                        searchComponent.setDefinition(null);
                    }
                }
            }
        }
    }
}
