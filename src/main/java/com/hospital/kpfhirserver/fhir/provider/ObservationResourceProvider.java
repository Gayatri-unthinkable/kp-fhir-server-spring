package com.hospital.kpfhirserver.fhir.provider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hospital.kpfhirserver.dto.EkgDto;
import com.hospital.kpfhirserver.dto.ParticipantsDto;
import com.hospital.kpfhirserver.dto.PatientProfileDto;
import com.hospital.kpfhirserver.dto.RecordingDto;
import com.hospital.kpfhirserver.remote.KproClient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is an Observation Resource Provider.
 * Operations supported : Read, Search w/o param, Search by MRN
 */
@Component
public class ObservationResourceProvider implements IResourceProvider {

    private Logger logger = LoggerFactory.getLogger(ObservationResourceProvider.class);
    @Autowired
    private KproClient kproClient;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    /**
     * End point(GET) which takes recordingId and gives Observation Resource as response
     * after fetching Recording from KPro
     *
     * @param recordingId
     * @return Observation Resource
     * @throws ParseException
     */
    @Read()
    public Observation getObservation(@IdParam IdType recordingId) throws IOException {

        RecordingDto recordingDto = kproClient.getRecording(recordingId.getIdPart());
        if (recordingDto != null) {
            return getObservationResource(recordingDto, recordingDto.getParticipantProfile());
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * End Point(GET) to search Recordings of a given patient using in a team in KPro
     * by providing MRN
     *
     * @param mrn
     * @return Bundle of Observation Resources
     * @throws ParseException
     */
    @Search
    public List<Observation> searchRecordingByMrn(@RequiredParam(name = Observation.SP_IDENTIFIER) TokenParam mrn) throws ParseException, IOException {
        List<Observation> observationList = new ArrayList<>();
        String mrnValue = mrn.getValue();
        List<ParticipantsDto> patientDtoList = kproClient.getPatientByIdentifer(mrnValue);
        String patientId = null;

        if (!patientDtoList.isEmpty() && patientDtoList.get(0).getPatientProfile() != null
                && !patientDtoList.get(0).getPatientProfile().isEmpty()
                && patientDtoList.get(0).getPatientProfile().get(0).getPatientId() != null) {

            patientId = patientDtoList.get(0).getPatientProfile().get(0).getPatientId();
            EkgDto recording = kproClient.getRecordingByPatientId(patientId);
            if (recording != null) {
                for (RecordingDto recordingDto : recording.getRecordingDtoList()) {
                    observationList.add(getObservationResource(recordingDto,
                            patientDtoList.get(0).getPatientProfile().get(0)));
                }
            }
        }
        Collections.sort(observationList, new Comparator<Observation>() {
            @Override
            public int compare(Observation o1, Observation o2) {
                return o2.getEffectiveDateTimeType().getValue().compareTo(o1.getEffectiveDateTimeType().getValue());
            }
        });
        return observationList;
    }

    /**
     * End Point(GET) to search all the recordings in a team in KPro
     *
     * @return Bundle of Observation Resources
     * @throws ParseException
     */
    @Search
    public List<Observation> search() throws IOException {

        String patientData = kproClient.getAllPatient();
        Type patientProfileType = new TypeToken<List<PatientProfileDto>>() {
        }.getType();
        List<PatientProfileDto> patientDtoList = new Gson()
                .fromJson(patientData, patientProfileType);
        List<Observation> observationList = new ArrayList<>();

        if (patientDtoList != null && !patientDtoList.isEmpty()) {
            for (PatientProfileDto patientProfileDto : patientDtoList) {
                if (patientProfileDto != null && patientProfileDto.getPatientId() != null
                        && !patientProfileDto.getPatientId().equalsIgnoreCase("")) {

                    EkgDto ekgDto = kproClient.getRecordingByPatientId(patientProfileDto.getPatientId());
                    if (ekgDto != null && ekgDto.getRecordingDtoList() != null
                            && !ekgDto.getRecordingDtoList().isEmpty()) {
                        for (RecordingDto recordingDto : ekgDto.getRecordingDtoList()) {
                            if (recordingDto != null) {
                                observationList.add(getObservationResource(recordingDto, patientProfileDto));
                            }
                        }
                    }
                } else {
                    throw new InternalErrorException("Internal Error");
                }
            }
            Collections.sort(observationList, new Comparator<Observation>() {
                @Override
                public int compare(Observation o1, Observation o2) {
                    return o2.getEffectiveDateTimeType().getValue().compareTo(o1.getEffectiveDateTimeType().getValue());
                }
            });
            return observationList;
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * Common method which accepts RecordingDto and PatientProfileDto
     * and converts it to Observation Resource
     *
     * @param recordingDto, patientProfileDto
     * @return Observation Resource
     * @throws ParseException
     */
    public static Observation getObservationResource(RecordingDto recordingDto, PatientProfileDto patientProfileDto) {

        Observation observation = new Observation();
        observation.setId(recordingDto.getId());
        observation.setSubject(new Reference("Patient/" + patientProfileDto.getPatientId())
                .setDisplay(patientProfileDto.getLastName() + " "
                        + patientProfileDto.getFirstName()));

        List<Coding> categoryCodings = new ArrayList<>();
        categoryCodings.add(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("procedure").setDisplay("Procedure"));
        CodeableConcept categoryCodeableConcept = new CodeableConcept();
        categoryCodeableConcept.setCoding(categoryCodings);
        observation.addCategory(categoryCodeableConcept);

        List<Coding> codings = new ArrayList<>();
        codings.add(new Coding().setSystem("http://loinc.org")
                .setCode("11524-6").setDisplay("EKG study"));
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setCoding(codings);
        observation.setCode(codeableConcept);
        observation.setEffective(new DateTimeType(recordingDto.getRecordedAt()));

        if (recordingDto.getMemberInterpretations() != null && !recordingDto.getMemberInterpretations().isEmpty()) {
            observation.setStatus(Observation.ObservationStatus.FINAL);
        } else {
            observation.setStatus(Observation.ObservationStatus.REGISTERED);
        }

        List<Coding> componentCodings = new ArrayList<>();
        componentCodings.add(new Coding().setDisplay("leadI"));
        CodeableConcept componentCodeableConcept = new CodeableConcept().setCoding(componentCodings);
        List<Double> dataList = recordingDto.getPreviewData().getSamples().getLeadI();
        String data = dataList.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", " ");
        observation.addComponent().setCode(componentCodeableConcept).setValue(new SampledData().setData(data)
                .setPeriod(10).setLowerLimit(Collections.min(dataList))
                .setUpperLimit(Collections.max(dataList)).setDimensions(1)
                .setOrigin(new Quantity(recordingDto.getPreviewStartSample())));

        return observation;
    }
}


