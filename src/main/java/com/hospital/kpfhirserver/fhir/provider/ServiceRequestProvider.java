package com.hospital.kpfhirserver.fhir.provider;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hospital.kpfhirserver.dto.*;
import com.hospital.kpfhirserver.remote.KproClient;
import javassist.NotFoundException;
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
import java.util.List;
import java.util.Random;

/**
 * This is a ServiceRequest Resource Provider.
 * Operations supported : Create, Search w/o param, Search by patientId,MRN,
 * Search by MRN.
 */
@Component
public class ServiceRequestProvider implements IResourceProvider {
    private Logger logger = LoggerFactory.getLogger(ServiceRequestProvider.class);
    Type orderType = new TypeToken<List<OrderDto>>() {
    }.getType();

    @Autowired
    private PatientResourceProvider patientResourceProvider;

    @Autowired
    private KproClient kproClient;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ServiceRequest.class;
    }

    /**
     * GET ServiceRequest Endpoint which returns a specific order for a patient based upon the Id and code
     *
     * @param patientId
     * @param code
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @Search
    public List<ServiceRequest> getServiceRequest(@RequiredParam(name = ServiceRequest.SP_IDENTIFIER) String patientId
            , @RequiredParam(name = ServiceRequest.SP_CODE) String code) throws IOException, ParseException {
        logger.info("Get ServiceRequest method triggered ");
        OrderDto order = kproClient.getOrderDetail(patientId);
        logger.info("Order Detail from client--{}", order);
        List<ServiceRequest> serviceRequestList = new ArrayList<>();
        ServiceRequest serviceRequest = new ServiceRequest();
        List<EnrollmentDto> enrollmentDtoList;
        ServiceRequest.ServiceRequestStatus orderStatus = null;
        if (order != null) {
            if (!order.getEnrollmentDto().isEmpty()) {
                enrollmentDtoList = order.getEnrollmentDto();
                for (EnrollmentDto enrollmentDto : enrollmentDtoList) {
                    String codeId = enrollmentDto.getCode();
                    logger.info("Code --{}", code);
                    if (codeId.equalsIgnoreCase(code)) {
                        String planStatus = enrollmentDto.getConnectionStatus();
                        if (planStatus.equalsIgnoreCase("connected")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.ACTIVE;
                        } else if (planStatus.equalsIgnoreCase("disconnected")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.ONHOLD;
                        } else if (planStatus.equalsIgnoreCase("pending")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.DRAFT;
                        } else if (planStatus.equalsIgnoreCase("expired")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.COMPLETED;
                        } else {
                            orderStatus = null;
                        }

                        serviceRequest.setId(order.getPatientId());
                        serviceRequest.getRequisition().setValue(enrollmentDto.getOrderNumber()).setUse(Identifier.IdentifierUse.OFFICIAL);
                        serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
                        serviceRequest.setPriority(ServiceRequest.ServiceRequestPriority.ROUTINE);
                        serviceRequest.setStatus(orderStatus);

                        List<Coding> codings = new ArrayList<>();
                        codings.add(new Coding().setSystem("http://snomed.info/sct")
                                .setCode("46825001").setDisplay("Electrocardiographic monitoring (procedure)"));
                        codings.add(new Coding().setCode("6Yncruk2LjvXMneTNKuucbgbvweygwmu").setDisplay("Kardia Mobile + 1 year connection ($119)"));
                        CodeableConcept codeableConcept = new CodeableConcept();
                        codeableConcept.setCoding(codings);
                        serviceRequest.setCode(codeableConcept);
                        serviceRequest.setSubject(new Reference("Patient/" + order.getPatientId()));
                        serviceRequestList.add(serviceRequest);
                    } else {
                        throw new ResourceNotFoundException("Order with given code does not exist for patient ");
                    }
                }
            }
        }
        return serviceRequestList;
    }

    /**
     * Get Endpoint - It displays the list of orders available for  patient based upon patientId
     *
     * @param patientId
     * @return ServiceRequest resource
     * @throws IOException
     */
    @Search
    public List<ServiceRequest> getServiceRequestByPatientId(@RequiredParam(name = ServiceRequest.SP_IDENTIFIER) String patientId) throws IOException {
        logger.info("Patient id ---{}", patientId);
        OrderDto order = kproClient.getOrderDetail(patientId);
        logger.info("Order Detail from client--{}", order);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ServiceRequest serviceRequest = new ServiceRequest();
        List<ServiceRequest> serviceRequestList = new ArrayList<>();
        List<EnrollmentDto> enrollmentDtoList;
        if (order != null) {
            if (!order.getEnrollmentDto().isEmpty()) {
                enrollmentDtoList = order.getEnrollmentDto();
                for (EnrollmentDto enrollmentDto : enrollmentDtoList) {
                    ServiceRequest.ServiceRequestStatus orderStatus = null;
                    String planStatus = enrollmentDto.getConnectionStatus();
                    if (planStatus.equalsIgnoreCase("connected")) {
                        orderStatus = ServiceRequest.ServiceRequestStatus.ACTIVE;
                    } else if (planStatus.equalsIgnoreCase("disconnected")) {
                        orderStatus = ServiceRequest.ServiceRequestStatus.ONHOLD;
                    } else if (planStatus.equalsIgnoreCase("pending")) {
                        orderStatus = ServiceRequest.ServiceRequestStatus.DRAFT;
                    } else if (planStatus.equalsIgnoreCase("expired")) {
                        orderStatus = ServiceRequest.ServiceRequestStatus.COMPLETED;
                    } else {
                        orderStatus = null;
                    }
                    serviceRequest.setId(order.getPatientId());
                    serviceRequest.getRequisition().setValue(enrollmentDto.getOrderNumber()).setUse(Identifier.IdentifierUse.OFFICIAL);
                    serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
                    serviceRequest.setPriority(ServiceRequest.ServiceRequestPriority.ROUTINE);
                    serviceRequest.setStatus(orderStatus);
                    List<Coding> codings = new ArrayList<>();
                    codings.add(new Coding().setSystem("http://snomed.info/sct")
                            .setCode("46825001").setDisplay("Electrocardiographic monitoring (procedure)"));
                    codings.add(new Coding().setCode("6Yncruk2LjvXMneTNKuucbgbvweygwmu").setDisplay("Kardia Mobile + 1 year connection ($119)"));
                    CodeableConcept codeableConcept = new CodeableConcept();
                    codeableConcept.setCoding(codings);
                    serviceRequest.setCode(codeableConcept);
                    serviceRequest.setSubject(new Reference("Patient/" + order.getPatientId()));
                    serviceRequestList.add(serviceRequest);
                }
            } else {
                throw new InternalError("There is no orders available for given patient");
            }
            return serviceRequestList;
        } else {
            throw new InternalError("Internal Error");
        }
    }

    /**
     * Get Endpoint  which returns all the orders available in team .
     *
     * @return ServiceRequest resource
     * @throws IOException
     */

    @Search
    public List<ServiceRequest> getAllServiceRequest() throws IOException {
        List<ServiceRequest> serviceRequestList = new ArrayList<>();
        String orderData = kproClient.getAllPatient();
        List<OrderDto> orderDtoList = new Gson()
                .fromJson(orderData, orderType);
        List<EnrollmentDto> enrollmentDtoList;
        ServiceRequest.ServiceRequestStatus orderStatus = null;
        if (orderDtoList != null && !orderData.isEmpty()) {
            for (OrderDto orderDto : orderDtoList) {
                enrollmentDtoList = orderDto.getEnrollmentDto();
                if (enrollmentDtoList != null && !enrollmentDtoList.isEmpty()) {
                    for (EnrollmentDto enrollmentDto : enrollmentDtoList) {
                        String planStatus = enrollmentDto.getConnectionStatus();
                        if (planStatus.equalsIgnoreCase("connected")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.ACTIVE;
                        } else if (planStatus.equalsIgnoreCase("disconnected")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.ONHOLD;
                        } else if (planStatus.equalsIgnoreCase("pending")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.DRAFT;
                        } else if (planStatus.equalsIgnoreCase("expired")) {
                            orderStatus = ServiceRequest.ServiceRequestStatus.COMPLETED;
                        } else {
                            orderStatus = null;
                        }

                        ServiceRequest serviceRequest = new ServiceRequest();
                        serviceRequest.setStatus(orderStatus);
                        serviceRequest.setId(orderDto.getPatientId());
                        serviceRequest.getRequisition().setValue(enrollmentDto.getOrderNumber()).setUse(Identifier.IdentifierUse.OFFICIAL);
                        serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
                        serviceRequest.setPriority(ServiceRequest.ServiceRequestPriority.ROUTINE);
                        List<Coding> codings = new ArrayList<>();
                        codings.add(new Coding().setSystem("http://snomed.info/sct")
                                .setCode("46825001").setDisplay("Electrocardiographic monitoring (procedure)"));
                        codings.add(new Coding().setCode("6Yncruk2LjvXMneTNKuucbgbvweygwmu").setDisplay("Kardia Mobile + 1 year connection ($119)"));
                        CodeableConcept codeableConcept = new CodeableConcept();
                        codeableConcept.setCoding(codings);
                        serviceRequest.setCode(codeableConcept);
                        serviceRequest.setSubject(new Reference("Patient/" + orderDto.getPatientId()));
                        serviceRequestList.add(serviceRequest);
                    }
                }
            }
            return serviceRequestList;

        } else {
            throw new InternalError("Internal Error");
        }
    }

    /**
     * Get Endpoint - returns list of all ServiceRequest available for a patient based upon MRN
     *
     * @param mrn
     * @return ServiceRequest resource
     * @throws IOException
     * @throws ParseException
     * @throws NotFoundException
     */
    @Search
    public List<ServiceRequest> getServiceRequestByMrn(@RequiredParam(name = ServiceRequest.SP_PATIENT) String mrn) throws IOException, ParseException, NotFoundException {
        List<ServiceRequestDto> serviceRequestDtoList = kproClient.getorderByIdentifer(mrn);
        logger.info("Order Detail from client--{}", serviceRequestDtoList);
        if (serviceRequestDtoList != null) {
            List<ServiceRequest> serviceRequestList = new ArrayList<>();
            ServiceRequest serviceRequest = new ServiceRequest();
            List<EnrollmentDto> enrollmentDtoList;
            ServiceRequest.ServiceRequestStatus orderStatus = null;
            if (serviceRequestDtoList != null && !serviceRequestDtoList.isEmpty()) {
                for (ServiceRequestDto serviceRequestDto : serviceRequestDtoList) {
                    if (!serviceRequestDto.getParticipantOrder().isEmpty() && serviceRequestDto.getParticipantOrder() != null) {
                        enrollmentDtoList = serviceRequestDto.getParticipantOrder().get(0).getEnrollmentDto();
                        if (!enrollmentDtoList.isEmpty() && enrollmentDtoList != null) {
                            for (EnrollmentDto enrollmentDto : enrollmentDtoList) {
                                String planStatus = enrollmentDto.getConnectionStatus();
                                if (planStatus.equalsIgnoreCase("connected")) {
                                    orderStatus = ServiceRequest.ServiceRequestStatus.ACTIVE;
                                } else if (planStatus.equalsIgnoreCase("disconnected")) {
                                    orderStatus = ServiceRequest.ServiceRequestStatus.ONHOLD;
                                } else if (planStatus.equalsIgnoreCase("pending")) {
                                    orderStatus = ServiceRequest.ServiceRequestStatus.DRAFT;
                                } else if (planStatus.equalsIgnoreCase("expired")) {
                                    orderStatus = ServiceRequest.ServiceRequestStatus.COMPLETED;
                                } else {
                                    orderStatus = ServiceRequest.ServiceRequestStatus.NULL;
                                }
                                String patientId = serviceRequestDto.getParticipantOrder().get(0).getPatientId();
                                serviceRequest.setId(patientId);
                                serviceRequest.getRequisition().setValue(enrollmentDto.getOrderNumber()).setUse(Identifier.IdentifierUse.OFFICIAL);
                                serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
                                serviceRequest.setPriority(ServiceRequest.ServiceRequestPriority.ROUTINE);
                                serviceRequest.setStatus(orderStatus);

                                List<Coding> codings = new ArrayList<>();
                                codings.add(new Coding().setSystem("http://snomed.info/sct")
                                        .setCode("46825001").setDisplay("Electrocardiographic monitoring (procedure)"));
                                codings.add(new Coding().setCode("6Yncruk2LjvXMneTNKuucbgbvweygwmu").
                                        setDisplay("Kardia Mobile + 1 year connection ($119)"));

                                CodeableConcept codeableConcept = new CodeableConcept();
                                codeableConcept.setCoding(codings);
                                serviceRequest.setCode(codeableConcept);
                                serviceRequest.setSubject(new Reference("Patient/" + patientId));
                                serviceRequestList.add(serviceRequest);
                            }
                        }
                    } else {
                        throw new ResourceNotFoundException("Participant with given MRN not found");
                    }
                }
            }
            return serviceRequestList;
        } else {
            throw new InternalError("Internal Error");
        }
    }

    /**
     * Post Endpoint - Create Order for  patient
     *
     * @param serviceRequest
     * @throws IOException
     */
    @Create
    public MethodOutcome createServiceRequest(@ResourceParam ServiceRequest serviceRequest) throws IOException {
        String id = serviceRequest.getSubject().getReference();
        String[] splitArray = id.split("/", 2);
        String patientId = null;
        patientId = splitArray[1];
        logger.info("PatientId in create order --{}", patientId);
        PatientProfileDto patientProfileDto = kproClient.getPatientProfile(patientId);
        OrderRequest orderRequest = new OrderRequest();
        List<Coding> code = new ArrayList<>();
        code = serviceRequest.getCode().getCoding();
        String connectionTemplateId = code.get(1).getCode();
        logger.info("ConnectionTemplate ---{}", connectionTemplateId);
        if (connectionTemplateId.equals("6Yncruk2LjvXMneTNKuucbgbvweygwmu")) {
            if (patientProfileDto != null) {
                orderRequest.setAssignedMemberID(null);
                BillingCodesDto billingCodes = new BillingCodesDto();
                billingCodes.setBillable(true);
                List<BillingCodesDto> billingCodesDtoList = new ArrayList<>();
                billingCodes.setDescription("Reports generated per 30-day period");
                billingCodes.setId("1");
                billingCodes.setName("CPT 99091");
                billingCodesDtoList.add(billingCodes);
                orderRequest.setBillingCodes(billingCodesDtoList);
                orderRequest.setConnectionTemplateID("6Yncruk2LjvXMneTNKuucbgbvweygwmu");
                orderRequest.setCustomParticipantID(patientProfileDto.getMrn());
                orderRequest.setDob(patientProfileDto.getDob());
                orderRequest.setEmail(patientProfileDto.getEmail());
                orderRequest.setFirstName(patientProfileDto.getFirstName());
                List<IcdCodeRequest> icdCodesDtoList = new ArrayList<>();
                IcdCodeRequest icdCodesDto = new IcdCodeRequest();
                icdCodesDto.setBillable(true);
                icdCodesDto.setDescription("Pain in throat and chest");
                icdCodesDto.setId("R07.9");
                icdCodesDto.setName("Chest pain, unspecified");
                icdCodesDtoList.add(icdCodesDto);
                orderRequest.setIcd10Codes(icdCodesDtoList);
                orderRequest.setImplantedDevice(null);
                orderRequest.setLastName(patientProfileDto.getLastName());
                orderRequest.setNotes(null);
                orderRequest.setPhone(patientProfileDto.getPhone());
                orderRequest.setPrescribed(true);
                orderRequest.setSex(patientProfileDto.getSex());
                Random rnd = new Random();
                int number = rnd.nextInt(999999999) + 10;
                String orderNumber = String.format("%010d", number);
                orderRequest.setOrderNumber(serviceRequest.getRequisition().getValue());
            }
        } else {
            throw new ResourceNotFoundException("Provide valid code for creating order");
        }
        OrderDto orderDto = kproClient.updateOrder(orderRequest, patientId);
        if (orderDto != null) {
            ServiceRequest.ServiceRequestStatus orderStatus = null;
            List<EnrollmentDto> enrollmentDtoList = new ArrayList<>();
            enrollmentDtoList = orderDto.getEnrollmentDto();
            for (EnrollmentDto enrollmentDto : enrollmentDtoList) {
                String planStatus = enrollmentDto.getConnectionStatus();
                logger.info("Plan Status ---{}", planStatus);
                if (planStatus.equalsIgnoreCase("connected")) {
                    orderStatus = ServiceRequest.ServiceRequestStatus.ACTIVE;
                } else if (planStatus.equalsIgnoreCase("disconnected")) {
                    orderStatus = ServiceRequest.ServiceRequestStatus.ONHOLD;
                } else if (planStatus.equalsIgnoreCase("pending")) {
                    orderStatus = ServiceRequest.ServiceRequestStatus.DRAFT;
                } else if (planStatus.equalsIgnoreCase("expired")) {
                    orderStatus = ServiceRequest.ServiceRequestStatus.COMPLETED;
                } else {
                    orderStatus = ServiceRequest.ServiceRequestStatus.NULL;
                }
                serviceRequest.getRequisition().setValue(enrollmentDto.getOrderNumber()).setUse(Identifier.IdentifierUse.OFFICIAL);

            }

            serviceRequest.setStatus(orderStatus);
            serviceRequest.setId(patientId);
            return new MethodOutcome().setResource(serviceRequest);
        } else {
            throw new InternalError("Internal Error");
        }
    }
}

