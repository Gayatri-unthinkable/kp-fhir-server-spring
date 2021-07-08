package com.hospital.kpfhirserver.fhir.provider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import com.hospital.kpfhirserver.dto.RecordingDto;
import com.hospital.kpfhirserver.remote.KproClient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a Diagnostic Report Resource Provider.
 * Operations supported : Read
 */
@Component
public class DiagnosticReportProvider implements IResourceProvider {

    private Logger logger = LoggerFactory.getLogger(DiagnosticReportProvider.class);
    @Autowired
    private ObservationResourceProvider observationResourceProvider;
    @Autowired
    private KproClient kproClient;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return DiagnosticReport.class;
    }

    /**
     * End point(GET) which takes recordingId and gives Diagnostic Report Resource as response
     * after fetching PDF Report of recording from KPro
     *
     * @param recordingId
     * @return Diagnostic Report Resource
     * @throws IOException
     */
    @Read()
    public DiagnosticReport getPdfReport(@IdParam IdType recordingId) throws IOException {

        String id = recordingId.getIdPart();
        logger.info("The value of id for pdf --{}", id);
        // KproClient kproClient = new KproClient();
        byte[] base64Pdf = kproClient.getRecordingPdf(id);
        if (base64Pdf == null) {
            throw new InternalErrorException("Internal Error");
        }
        RecordingDto recordingDto = kproClient.getRecording(recordingId.getIdPart());
        Observation observation;
        if (recordingDto != null) {
            observation = observationResourceProvider.getObservationResource(recordingDto, recordingDto.getParticipantProfile());
        } else {
            throw new InternalErrorException("Internal Error");
        }
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        if (recordingDto.getMemberInterpretations() != null && !recordingDto.getMemberInterpretations().isEmpty()) {
            diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        } else {
            diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.REGISTERED);
        }
        List<Coding> categoryCodings = new ArrayList<>();
        categoryCodings.add(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0074").
                setCode("EC").setDisplay("EKG Report"));
        CodeableConcept categoryCodeableConcept = new CodeableConcept();
        categoryCodeableConcept.setCoding(categoryCodings);
        diagnosticReport.addCategory(categoryCodeableConcept);

        List<Coding> codings = new ArrayList<>();
        codings.add(new Coding().setSystem("http://loinc.org")
                .setCode("11524-6").setDisplay("EKG study"));
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setCoding(codings);
        String device;
        if (recordingDto.isIs6l()) {
            device = "6 Lead EKG";
        } else {
            device = "Single Lead EKG";
        }
        codeableConcept.setText("This is Ekg Report for " + device);
        diagnosticReport.setCode(codeableConcept);
        String patientRef = observation.getSubject().getReference();
        diagnosticReport.getSubject().setReference(patientRef);
        diagnosticReport.setEffective(new DateTimeType(recordingDto.getRecordedAt()));
        List<Attachment> attachment = new ArrayList<>();
        Attachment pdfAttachment = new Attachment();
        pdfAttachment.setContentType("application/pdf");
        pdfAttachment.setLanguage("en-US");
        pdfAttachment.setData(base64Pdf);
        pdfAttachment.setTitle("PDF Report");
        attachment.add(pdfAttachment);
        diagnosticReport.setPresentedForm(attachment);

        return diagnosticReport;
    }

    /**
     * End Point(GET) to search all Diagnostic Reports in a team
     * This operation is not supported by our server. Hence, it will throw exception when this
     * endpoint will be accessed
     *
     * @return Operation Outcome
     */
    @Search
    public OperationOutcome search() {
        OperationOutcome operationOutcome = new OperationOutcome();
        operationOutcome.setText(new Narrative().setStatus(Narrative.NarrativeStatus.GENERATED));
        String div = "<div xmlns=\"http://www.w3.org/1999/xhtml\"> <h1>Operation Outcome</h1>" +
                "<h2>ERROR</h2><p>Search Paramater mandatory. " +
                "Valid search parameters for this search is [_id]</p></div>";
        operationOutcome.getText().setDivAsString(div);
        throw new InvalidRequestException("Search Operation not supported for Diagnostic Report",
                operationOutcome);
    }
}
