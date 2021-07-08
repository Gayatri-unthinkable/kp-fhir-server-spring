package com.hospital.kpfhirserver.fhir.provider;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hospital.kpfhirserver.dto.*;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is a Patient Resource Provider.
 * Operations supported : Read, Create, Update, Patch, Search w/o param, Search by name,
 * Search by MRN.
 */
@Component
public class PatientResourceProvider implements IResourceProvider {

    @Autowired
    private KproClient kproClient;
    private Logger logger = LoggerFactory.getLogger(PatientResourceProvider.class);
    Type patientProfileType = new TypeToken<List<PatientProfileDto>>() {
    }.getType();

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }


    /**
     * End point(GET) which takes patientId and gives Patient Resource as response
     * after fetching Patient from KPro
     *
     * @param patientId
     * @return Patient Resource
     * @throws ParseException
     */
    @Read
    public Patient getPatient(@IdParam IdType patientId) throws ParseException, IOException {

        // KproClient kproClient = new KproClient();
        PatientProfileDto patientProfileDto = kproClient.getPatientProfile(patientId.getIdPart());

        if (patientProfileDto != null) {
            return getPatientResource(patientProfileDto);
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * End Point(POST) which accepts Patient Resource as Request Body and create Patient on KPro
     *
     * @param patient
     * @return
     */
    @Create
    public MethodOutcome createPatient(@ResourceParam Patient patient) throws IOException {

        String mrn = patient.getIdentifier().get(0).getValue();
        Date dob = patient.getBirthDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dobString = sdf.format(dob);
        String lastName = patient.getName().get(0).getFamily();
        String firstName = patient.getName().get(0).getGiven().get(0).toString();
        String email = "";
        String phone = "";

        for (ContactPoint contactPoint : patient.getTelecom()) {
            if (contactPoint.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                phone = contactPoint.getValue();
            } else if (contactPoint.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                email = contactPoint.getValue();
            }
        }

        String gender;
        if (patient.getGender() == Enumerations.AdministrativeGender.MALE) {
            gender = "male";
        } else if (patient.getGender() == Enumerations.AdministrativeGender.FEMALE) {
            gender = "female";
        } else {
            gender = "not_known";
        }

        logger.info("MRN, firstName, lastName, dob, email, phone, gender ----> {}, {}, {}, {}, {}, {}, {}",
                mrn, firstName, lastName, dobString, email, phone, gender);

        // KproClient kproClient = new KproClient();
        PatientProfileDto patientProfileDto = kproClient.createPatient(mrn, email, dobString, firstName, lastName,
                gender, phone);
        if (patientProfileDto != null) {
            patient.setId(patientProfileDto.getPatientId());
            return new MethodOutcome().setResource(patient);
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * End Point(PUT) which accepts Patient Resource as Request Body and update Patient on KPro
     *
     * @param patientId, patient
     * @return Patient Resource
     */
    @Update
    public MethodOutcome updatePatient(@IdParam IdType patientId, @ResourceParam Patient patient) throws IOException {
        String mrn = patient.getIdentifier().get(0).getValue();
        Date dob = patient.getBirthDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dobString = sdf.format(dob);
        String lastName = patient.getName().get(0).getFamily();
        String firstName = patient.getName().get(0).getGiven().get(0).toString();
        String email = "";
        String phone = "";

        for (ContactPoint contactPoint : patient.getTelecom()) {
            if (contactPoint.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                phone = contactPoint.getValue();
            } else if (contactPoint.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                email = contactPoint.getValue();
            }
        }

        String gender;
        if (patient.getGender() == Enumerations.AdministrativeGender.MALE) {
            gender = "male";
        } else if (patient.getGender() == Enumerations.AdministrativeGender.FEMALE) {
            gender = "female";
        } else {
            gender = "not_known";
        }

        logger.info("firstName, lastName, dob, email, phone, gender ----> {}, {}, {}, {}, {}, {}, {}",
                firstName, lastName, dobString, email, phone, gender);

        // KproClient kproClient = new KproClient();
        PatientProfileDto patientProfileDto = kproClient.updatePatient(patientId.getIdPart(), mrn, email,
                dobString, firstName, lastName, gender, phone);
        if (patientProfileDto != null) {
            patient.setId(patientProfileDto.getPatientId());
            return new MethodOutcome().setResource(patient);
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * End Point(GET) to search all the patients who are part of a team in KPro
     *
     * @return Bundle of Patient Resources
     * @throws ParseException
     */
    @Search
    public List<Patient> search() throws ParseException, IOException {
        // KproClient kproClient = new KproClient();
        List<Patient> patientList = new ArrayList<>();
        String patientData = kproClient.getAllPatient();
        List<PatientProfileDto> patientDtoList = new Gson()
                .fromJson(patientData, patientProfileType);

        logger.info("Patient List after client call---{}", patientDtoList.size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (patientDtoList != null && !patientDtoList.isEmpty()) {
            for (PatientProfileDto patientDto : patientDtoList) {
                patientList.add(getPatientResource(patientDto));
            }
            return patientList;
        } else {
            throw new InternalErrorException("Internal Error");
        }
    }

    /**
     * End Point(GET) to search all the patients of a given name in a team in KPro
     *
     * @param name
     * @return Bundle of Patient Resources
     * @throws ParseException
     */
    @Search
    public List<Patient> searchByName(@RequiredParam(name = Patient.SP_NAME) StringParam name) throws ParseException, IOException {
        //   KproClient client = new KproClient();
        String patientName = name.getValue();
        List<ParticipantsDto> participantsDtoList = kproClient.getPatientByIdentifer(patientName);
        logger.info("Patient Data from client --{}", participantsDtoList.size());
        List<Patient> patientList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (participantsDtoList != null) {
            for (ParticipantsDto participantsDto : participantsDtoList) {
                for (PatientProfileDto patientDto : participantsDto.getPatientProfile()) {
                    Patient patient = new Patient();
                    patient.setId(patientDto.getPatientId());
                    List<Coding> codings = new ArrayList<>();
                    codings.add(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                            .setCode("MR"));
                    CodeableConcept codeableConcept = new CodeableConcept();
                    codeableConcept.setCoding(codings);
                    patient.addIdentifier().setValue(patientDto.getMrn()).setUse(Identifier.IdentifierUse.USUAL)
                            .setAssigner(new Reference().setDisplay("Kardia Pro")).setType(codeableConcept);
                    patient.addName().addGiven(patientDto.getFirstName()).setFamily(patientDto.getLastName());
                    patient.addTelecom().setUse(ContactPoint.ContactPointUse.HOME).
                            setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(patientDto.getEmail());
                    patient.addTelecom().setUse(ContactPoint.ContactPointUse.HOME).
                            setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(patientDto.getPhone());
                    if (patientDto.getDob() != null) {
                        patient.setBirthDate(sdf.parse(patientDto.getDob()));
                    }
                    if (patientDto.getSex().equalsIgnoreCase("Male")) {
                        patient.setGender(Enumerations.AdministrativeGender.MALE);
                    } else if (patientDto.getSex().equalsIgnoreCase("Female")) {
                        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
                    } else {
                        patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
                    }
                    patientList.add(patient);
                }
            }
        } else {
            throw new InternalErrorException("Internal Error");
        }
        return patientList;

    }

    /**
     * End Point(GET) to search patient of a given MRN in a team in KPro
     *
     * @param mrn
     * @return Bundle of Patient Resources
     * @throws ParseException
     */
    @Search
    public List<Patient> searchByMrn(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam mrn) throws ParseException, IOException {
        //KproClient client = new KproClient();
        String identifier = mrn.getValue();
        List<ParticipantsDto> participantsDtoList = kproClient.getPatientByIdentifer(identifier);
        logger.info("Patient Data from client --{}", participantsDtoList);
        List<Patient> patientList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (participantsDtoList != null && !participantsDtoList.isEmpty()) {
            for (ParticipantsDto participantsDto : participantsDtoList) {
                for (PatientProfileDto patientDto : participantsDto.getPatientProfile()) {
                    patientList.add(getPatientResource(patientDto));
                }
            }
        } else {
            throw new InternalErrorException("Internal Error");
        }
        return patientList;
    }

    /**
     * End Point(PATCH) which accepts a JSON Patch as Request Body and update Patient on KPro
     *
     * @param patientId, patient, patchType
     * @return Patient Resource
     */
    @Patch
    public MethodOutcome patchPatient(@IdParam IdType patientId, @ResourceParam String body,
                                      PatchTypeEnum patchType) throws ParseException, IOException {

        if (patchType == PatchTypeEnum.JSON_PATCH) {
            Type type = new TypeToken<List<PatchOperationDto>>() {
            }.getType();
            Gson gson = new Gson();
            List<PatchOperationDto> patchOperations = gson.fromJson(body, type);
            // KproClient kproClient = new KproClient();
            PatientProfileDto patientProfileDto = kproClient.getPatientProfile(patientId.getIdPart());

            if (patientProfileDto != null) {
                for (PatchOperationDto patchOperationDto : patchOperations) {
                    validatePatchRequest(patchOperationDto);
                    if (patchOperationDto.getPath().equals("/birthDate")) {
                        patientProfileDto.setDob(patchOperationDto.getValue().toString());

                    } else if (patchOperationDto.getPath().equals("/gender")) {
                        if (patchOperationDto.getValue().toString().equalsIgnoreCase("male")
                                || patchOperationDto.getValue().toString().equalsIgnoreCase("female")) {
                            patientProfileDto.setSex(patchOperationDto.getValue().toString());
                        } else {
                            patchOperationDto.setValue("not_known");
                        }
                    } else if (patchOperationDto.getPath().equals("/name/0")) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> stringObjectMap = objectMapper.convertValue(
                                patchOperationDto.getValue(), Map.class);
                        NameDto nameDto = gson.fromJson(stringObjectMap.toString(), NameDto.class);

                        if (nameDto != null) {
                            patientProfileDto.setLastName(nameDto.getFamily());
                            patientProfileDto.setFirstName(nameDto.getGiven().get(0));
                        } else {
                            throw new InvalidRequestException("Bad request for name");
                        }
                    } else if (patchOperationDto.getPath().equals("/telecom/0")) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> stringObjectMap = objectMapper.convertValue(
                                patchOperationDto.getValue(), Map.class);
                        SystemValueDto systemValueDto = gson.fromJson(stringObjectMap.toString(), SystemValueDto.class);

                        if (systemValueDto != null && systemValueDto.getSystem().equalsIgnoreCase("phone")) {
                            patientProfileDto.setPhone(systemValueDto.getValue().toString());
                        } else {
                            throw new InvalidRequestException("Bad Request for Phone");
                        }
                    } else if (patchOperationDto.getPath().equals("/telecom/1")) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> stringObjectMap = objectMapper.convertValue(
                                patchOperationDto.getValue(), Map.class);
                        SystemValueDto systemValueDto = gson.fromJson(stringObjectMap.toString(), SystemValueDto.class);

                        if (systemValueDto != null && systemValueDto.getSystem().equalsIgnoreCase("email")) {
                            patientProfileDto.setEmail(systemValueDto.getValue().toString());
                        } else {
                            throw new InvalidRequestException("Bad Request for Email");
                        }
                    } else {
                        throw new InvalidRequestException("Bad Request");
                    }
                }
                PatientProfileDto patientProfileResponse = kproClient.updatePatient(patientId.getIdPart(), patientProfileDto.getMrn(), patientProfileDto.getEmail(),
                        patientProfileDto.getDob(), patientProfileDto.getFirstName(), patientProfileDto.getLastName(),
                        patientProfileDto.getSex(), patientProfileDto.getPhone());

                if (patientProfileResponse != null) {
                    String sex = patientProfileDto.getSex();
                    Enumerations.AdministrativeGender administrativeGender;

                    if (sex.equalsIgnoreCase("male")) {
                        administrativeGender = Enumerations.AdministrativeGender.MALE;
                    } else if (sex.equalsIgnoreCase("female")) {
                        administrativeGender = Enumerations.AdministrativeGender.FEMALE;
                    } else {
                        administrativeGender = Enumerations.AdministrativeGender.NULL;
                    }
                    Patient patient = new Patient();
                    patient.setId(patientId.getIdPart());
                    patient.addIdentifier().setSystem("MRN").setValue(patientProfileResponse.getMrn());
                    patient.addName().setFamily(patientProfileResponse.getLastName()).addGiven(patientProfileResponse.getFirstName())
                            .setUse(HumanName.NameUse.OFFICIAL);
                    patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(patientProfileResponse.getPhone());
                    patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(patientProfileResponse.getEmail());
                    patient.setGender(administrativeGender);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    patient.setBirthDate(sdf.parse(patientProfileDto.getDob()));
                    return new MethodOutcome().setResource(patient);
                } else {
                    throw new InternalErrorException("Internal Error");
                }
            } else {
                throw new InternalErrorException("Internal Error");
            }
        } else {
            throw new InvalidRequestException("Patch Type should be json");
        }
    }

    /**
     * Method for Validating Request Body of Patch Operation End Point
     *
     * @param patchOperationDto
     */
    private void validatePatchRequest(PatchOperationDto patchOperationDto) {

        if (!patchOperationDto.getOp().equalsIgnoreCase("replace")) {
            throw new InvalidRequestException("Bad Request : Only Replace Operation is supported");
        }
    }

    /**
     * Common method which accepts PatientProfileDto and converts it to Patient Resource
     *
     * @param patientProfileDto
     * @return Patient Resource
     * @throws ParseException
     */
    private Patient getPatientResource(PatientProfileDto patientProfileDto) throws ParseException {

        String sex = patientProfileDto.getSex();
        Enumerations.AdministrativeGender administrativeGender;
        if (sex.equalsIgnoreCase("male")) {
            administrativeGender = Enumerations.AdministrativeGender.MALE;
        } else if (sex.equalsIgnoreCase("female")) {
            administrativeGender = Enumerations.AdministrativeGender.FEMALE;
        } else {
            administrativeGender = Enumerations.AdministrativeGender.UNKNOWN;
        }
        Patient patient = new Patient();
        patient.setId(patientProfileDto.getPatientId());
        List<Coding> codings = new ArrayList<>();
        codings.add(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                .setCode("MR"));
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setCoding(codings);
        patient.addIdentifier().setValue(patientProfileDto.getMrn()).setUse(Identifier.IdentifierUse.USUAL)
                .setAssigner(new Reference().setDisplay("Kardia Pro")).setType(codeableConcept);
        patient.addName().setFamily(patientProfileDto.getLastName()).addGiven(patientProfileDto.getFirstName())
                .setUse(HumanName.NameUse.OFFICIAL);

        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(patientProfileDto.getPhone());
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(patientProfileDto.getEmail());
        patient.setGender(administrativeGender);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (patientProfileDto.getDob() != null) {
            patient.setBirthDate(sdf.parse(patientProfileDto.getDob()));
        }
        return patient;
    }
}
