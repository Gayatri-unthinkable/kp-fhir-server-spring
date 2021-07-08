package com.hospital.kpfhirserver.remote;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hospital.kpfhirserver.dto.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Contains method which make Http Calls to KPro's internal APIs
 * and return responses given by those APIs
 */
@Component
public class KproClient {

    Logger logger = LoggerFactory.getLogger(KproClient.class);

    @Value("${kpro.socketTimeOut}")
    Integer socketTimeOut;
    @Value("${kpro.connectionTimeOut}")
    Integer connectionTimeOut;
    @Value("${kpro.url}")
    String url;
    @Value("${kpro.teamId}")
    String teamId;
    @Value("${kpro.username}")
    String username;
    @Value("${kpro.password}")
    String password;

    Type participantType = new TypeToken<ParticipantsDto>() {
    }.getType();

    Type orderType = new TypeToken<ServiceRequestDto>() {
    }.getType();


    /**
     * Common method to make HTTP GET calls to KPro Internal APIs
     *
     * @param uri
     * @return response in String
     */
    public String getData(String uri) {

        String data = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectionTimeOut)
                    .build();
            HttpGet get = new HttpGet(url + uri);
            get.setConfig(requestConfig);
            get.setHeader("Accept-Charset", "utf-8");
            get.setHeader("Content-Type", "application/json;charset=UTF-8");
            String encoding = Base64.getEncoder()
                    .encodeToString((username + ":" + password)
                            .getBytes("UTF-8"));
            get.setHeader("Authorization", "Basic " + encoding);

            logger.info("final url from get data: {}", get.getURI());
            CloseableHttpResponse response = client.execute(get);
            logger.info("Response status is {}", response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                data = EntityUtils.toString(entity);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                data = "Bad Request to Kpro Server";
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                data = "Not Found from Kpro Server";
            }
            return data;
        } catch (SocketTimeoutException ex) {
            logger.warn("Remote server could not respond in {} milliseconds, Error Message:{}", socketTimeOut, ex.getMessage());
        } catch (Throwable ex) {
            logger.error("Could not connect to kardia pro server : {}", ex.getMessage(), ex);
        }
        return data;
    }

    /**
     * Makes Http Call Kpro's Get Patient Profile Internal API
     *
     * @param patientId
     * @return PatientProfileDto
     */
    public PatientProfileDto getPatientProfile(String patientId) {

        Type type = new TypeToken<PatientProfileDto>() {
        }.getType();
        Gson gson = new Gson();
        String uri = "participants/" + patientId + "/profile";
        String patientProfileData = getData(uri);
        logger.info("PatientProfileDetails received from kardia");

        if (patientProfileData != null && patientProfileData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (patientProfileData != null && patientProfileData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }

        if (patientProfileData != null && !patientProfileData.equals("")) {
            PatientProfileDto patientProfileDto = gson.fromJson(patientProfileData, type);
            if (patientProfileDto != null) {
                logger.info("patientProfileDto : {}", patientProfileDto.getPatientId());
                return patientProfileDto;
            }
            logger.info("Can't convert response to Patient Object");
            return null;
        } else {
            logger.info("Patient with id --> {} not found in", patientId);
            return null;
        }
    }

    /**
     * Common method to make HTTP POST calls to KPro Internal APIs
     *
     * @param uri, requestJson
     * @return response in String
     */
    public String postData(String uri, String requestJson) {

        String data = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectionTimeOut)
                    .build();

            HttpPost httpPost = new HttpPost(url + uri);
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Accept-Charset", "utf-8");
            httpPost.setHeader("Accept", "application/json;charset=UTF-8");
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            String encoding = Base64.getEncoder()
                    .encodeToString((username + ":" + password)
                            .getBytes("UTF-8"));
            httpPost.setHeader("Authorization", "Basic " + encoding);
            httpPost.setEntity(new StringEntity(requestJson));

            logger.info("final url from post data: {}", httpPost.getURI());
            logger.info("Request from post date : {}", httpPost.toString());

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            logger.info("Response --> {}", response.toString());
            logger.info("Status Line --> {}, Status Code --> {}", response.getStatusLine(), response.getStatusLine().
                    getStatusCode());

            if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK && response.getStatusLine().getStatusCode()
                    < HttpStatus.SC_MULTIPLE_CHOICES) {
                data = EntityUtils.toString(entity);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                data = "Bad Request to Kpro Server";
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                data = "Not Found from Kpro Server";
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
                data = "Conflict";
            }
            logger.info("data----->{}", data);
            return data;
        } catch (SocketTimeoutException ex) {
            logger.warn("Remote server could not respond in {} milliseconds, Error Message:{}", socketTimeOut, ex.getMessage());
        } catch (Throwable ex) {
            logger.error("Could not connect to kardia pro server : {}", ex.getMessage(), ex);
        }
        return data;
    }

    /**
     * Makes Http call to Kpro's Create Patient Internal API
     *
     * @param mrn
     * @param email
     * @param dob
     * @param firstName
     * @param lastName
     * @param sex
     * @param phone
     * @return PatientProfileDto
     */
    public PatientProfileDto createPatient(String mrn, String email, String dob, String firstName, String lastName,
                                           String sex, String phone) {
        logger.info("Inside create kardia patient client --->{}, {}, {}, {}, {}, {}, {}", mrn, email, dob, firstName,
                lastName, sex, phone);
        String uri = "teams/" + teamId + "/participants";
        Type type = new TypeToken<PatientProfileDto>() {
        }.getType();

        PatientProfileDto patientProfileDto = new PatientProfileDto();
        patientProfileDto.setMrn(mrn);
        patientProfileDto.setFirstName(firstName);
        patientProfileDto.setLastName(lastName);
        patientProfileDto.setEmail(email);
        patientProfileDto.setPhone(phone);
        patientProfileDto.setSex(sex);
        patientProfileDto.setDob(dob);

        Gson gson = new Gson();
        String requestJson = gson.toJson(patientProfileDto);

        String response = postData(uri, requestJson);
        if (response != null && response.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (response != null && response.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        } else if (response != null && response.equalsIgnoreCase("Conflict")) {
            throw new InvalidRequestException("Already Exists");
        }
        logger.info("Inside create kardia patient client response --->{}", response);

        if (response != null && !response.equalsIgnoreCase("")) {
            PatientProfileDto patientProfileDtoResponse = gson.fromJson(response, type);
            if (patientProfileDtoResponse != null) {
                logger.info("patientDto : {}", patientProfileDtoResponse);
                return patientProfileDtoResponse;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Makes Http Call to Kpro's Get Recording Internal API
     *
     * @param recordingId
     * @return RecordingDto
     */
    public RecordingDto getRecording(String recordingId) {

        Type type = new TypeToken<RecordingDto>() {
        }.getType();
        Gson gson = new Gson();
        String uri = "recordings/" + recordingId;
        String recordingData = getData(uri);

        if (recordingData != null && recordingData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (recordingData != null && recordingData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }

        if (recordingData != null && !recordingData.equals("")) {
            RecordingDto recordingDto = gson.fromJson(recordingData, type);
            if (recordingDto != null) {
                return recordingDto;
            }
            logger.info("Can't convert response to Recording Object");
            return null;
        } else {
            logger.info("Recording with id --> {} not found", recordingId);
            return null;
        }
    }

    /**
     * Common method to make HTTP PUT calls to Kpro's Internal APIs
     *
     * @param uri
     * @param requestJson
     * @return response in string
     */
    public String putData(String uri, String requestJson) {
        String data = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectionTimeOut)
                    .build();

            HttpPut httpPut = new HttpPut(url + uri);
            httpPut.setConfig(requestConfig);
            httpPut.setHeader("Accept-Charset", "utf-8");
            httpPut.setHeader("Accept", "application/json;charset=UTF-8");
            httpPut.setHeader("Content-Type", "application/json;charset=UTF-8");
            String encoding = Base64.getEncoder()
                    .encodeToString((username + ":" + password)
                            .getBytes("UTF-8"));
            httpPut.setHeader("Authorization", "Basic " + encoding);
            httpPut.setEntity(new StringEntity(requestJson));

            logger.info("final url from put data: {}", httpPut.getURI());

            CloseableHttpResponse response = client.execute(httpPut);
            HttpEntity entity = response.getEntity();

            logger.info("Response --> {}", response.toString());
            logger.info("Status Line --> {}, Status Code --> {}", response.getStatusLine(), response.getStatusLine().
                    getStatusCode());

            if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK && response.getStatusLine().getStatusCode()
                    < HttpStatus.SC_MULTIPLE_CHOICES) {
                data = EntityUtils.toString(entity);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                data = "Bad Request to Kpro Server";
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                data = "Not Found from Kpro Server";
            }
            logger.info("data----->{}", data);
            return data;
        } catch (SocketTimeoutException ex) {
            logger.warn("Remote server could not respond in {} milliseconds, Error Message:{}", socketTimeOut, ex.getMessage());
        } catch (Throwable ex) {
            logger.error("Could not connect to kardia pro server : {}", ex.getMessage(), ex);
        }
        return data;
    }

    /**
     * Makes HTTP call to Kpro's Update Patient Internal API
     *
     * @param patientId
     * @param mrn
     * @param email
     * @param dob
     * @param firstName
     * @param lastName
     * @param sex
     * @param phone
     * @return PatientProfileDto
     */
    public PatientProfileDto updatePatient(String patientId, String mrn, String email, String dob, String
            firstName, String lastName, String sex, String phone) {
        logger.info("Inside Update kardia patient client --->{}, {}, {}, {}, {}, {}, {}", mrn, email, dob, firstName,
                lastName, sex, phone);
        String uri = "participants/" + patientId + "/profile";
        Type type = new TypeToken<PatientProfileDto>() {
        }.getType();

        PatientProfileDto patientProfileDto = new PatientProfileDto();
        patientProfileDto.setMrn(mrn);
        patientProfileDto.setFirstName(firstName);
        patientProfileDto.setLastName(lastName);
        patientProfileDto.setEmail(email);
        patientProfileDto.setPhone(phone);
        patientProfileDto.setSex(sex);
        patientProfileDto.setDob(dob);

        Gson gson = new Gson();
        String requestJson = gson.toJson(patientProfileDto);

        String response = putData(uri, requestJson);
        if (response != null && response.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (response != null && response.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        logger.info("Inside create kardia patient client response --->{}", response);

        if (response != null && !response.equalsIgnoreCase("")) {
            PatientProfileDto patientProfileDtoResponse = gson.fromJson(response, type);
            if (patientProfileDtoResponse != null) {
                logger.info("patientDto : {}", patientProfileDtoResponse);
                return patientProfileDtoResponse;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Makes HTTP call to KPro's Get Recordings of a patient internal API
     *
     * @param patientId
     * @return EkgDto
     */
    public EkgDto getRecordingByPatientId(String patientId) {
        Type type = new TypeToken<EkgDto>() {
        }.getType();
        Gson gson = new Gson();
        String uri = "participants/" + patientId;
        String recordingData = getData(uri);
        if (recordingData != null && recordingData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (recordingData != null && recordingData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        logger.info("Recordings data received from client");
        if (recordingData != null && !recordingData.equals("")) {
            EkgDto recordingDto = gson.fromJson(recordingData, type);
            if (recordingDto != null) {
                logger.info("recordingDto : {}", recordingDto.getRecordingDtoList().size());
                return recordingDto;
            }
            logger.info("Can't convert response to Recording Object");
            return null;
        } else {
            logger.info("Recording with id --> {} not found", teamId);
            return null;
        }
    }

    /**
     * Makes HTTP call to Kpro's get all patients internal API
     *
     * @return response in string
     */
    public String getAllPatient() {
        String uri = "teams/" + teamId + "/participants";
        String patientData = getData(uri);
        if (patientData != null && patientData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (patientData != null && patientData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        logger.info("Patient received from client");
        if (patientData != null) {
            return patientData;
        } else {
            logger.warn("Not found", teamId);
            return null;
        }
    }

    /**
     * Makes Http call to Kpro's Search Patient with param Internal API
     *
     * @param theParam
     * @return List<ParticipantsDto>
     */
    public List<ParticipantsDto> getPatientByIdentifer(String theParam) {
        String uri = "teams/" + teamId + "/participants/search/participant?identifier=" + theParam;
        logger.info("TeamId ---{}", teamId);
        String patientData = getData(uri);
        if (patientData != null && patientData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (patientData != null && patientData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        List<ParticipantsDto> participantsDtoList = new ArrayList<>();
        ParticipantsDto patientDtoList = new Gson()
                .fromJson(patientData, participantType);
        participantsDtoList.add(patientDtoList);

        int page = 2;
        while (patientDtoList.isHasNextPage()) {
            String url = "teams/" + teamId + "/participants/search/participant?identifier=" + theParam + "&page=" + page;
            patientData = getData(url);
            ParticipantsDto patientDto = new Gson()
                    .fromJson(patientData, participantType);
            participantsDtoList.add(patientDto);
            if (!patientDto.isHasNextPage())
                break;
            page++;
        }
        logger.info("Patient Data in search by Identifier  --{}", participantsDtoList.size());

        if (!participantsDtoList.isEmpty()) {
            return participantsDtoList;
        } else {
            logger.warn("patientId not found ....{}", theParam);
        }
        return null;
    }

    /**
     * Makes Http call to Kpro's get Recording PDF internal API
     *
     * @param id
     * @return byte[]
     * @throws IOException
     */
    public byte[] getRecordingPdf(String id) throws IOException {
        String uri = "recordings/" + id + ".pdf";
        byte[] pdfData = getPdfData(uri);
        //  byte[] encodedPdf = Base64.getEncoder().encode(pdfData);
        if (pdfData != null) {
            return pdfData;
        } else {
            logger.warn("Pdf file not found for the given Id");
            return null;
        }
    }

    /**
     * Common method to make HTTP GET calls to Kpro's Internal APIs
     * which return PDF as a response
     *
     * @param uri
     * @return byte[]
     */
    public byte[] getPdfData(String uri) {
        byte[] pdf = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectionTimeOut)
                    .build();
            HttpGet get = new HttpGet(url + uri);
            get.setConfig(requestConfig);
            get.setHeader("Accept-Charset", "utf-8");
            get.setHeader("Content-Type", "application/json;charset=UTF-8");
            get.setHeader("Accept", "application/pdf");
            String encoding = Base64.getEncoder()
                    .encodeToString((username + ":" + password)
                            .getBytes("UTF-8"));

            get.setHeader("Authorization", "Basic " + encoding);

            logger.info("final url from get data: {}", get.getURI());

            CloseableHttpResponse response = client.execute(get);
            logger.info("Response status is {}", response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = entity.getContent();
                byte[] buffer = new byte[80000];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                pdf = output.toByteArray();
            }
            return pdf;
        } catch (SocketTimeoutException ex) {
            logger.warn("Remote server could not respond in {} milliseconds, Error Message:{}", socketTimeOut, ex.getMessage());
        } catch (Throwable ex) {
            logger.error("Could not connect to kardia pro server : {}", ex.getMessage(), ex);
        }
        return pdf;
    }

    /**
     * Makes Http call to Kpro's Get Patient Profile Internal API to get the details related to order
     *
     * @param patientId
     * @return
     */
    public OrderDto getOrderDetail(String patientId) {

        Type type = new TypeToken<OrderDto>() {
        }.getType();
        Gson gson = new Gson();
        String uri = "participants/" + patientId + "/profile";
        String patientProfileData = getData(uri);

        if (patientProfileData != null && patientProfileData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (patientProfileData != null && patientProfileData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }

        if (patientProfileData != null && !patientProfileData.equals("")) {
            OrderDto patientProfileDto = gson.fromJson(patientProfileData, type);
            if (patientProfileDto != null) {
                logger.info("patientProfileDto : {}", patientProfileDto.getPatientId());
                return patientProfileDto;
            }
            logger.info("Can't convert response to Patient Object");
            return null;
        } else {
            logger.info("Patient with id --> {} not found in", patientId);
            return null;
        }
    }

    /**
     * Makes Http call to Kpro's Get Patient using search parameter to fetch order details of patient
     *
     * @param theParam
     * @return
     */
    public List<ServiceRequestDto> getorderByIdentifer(String theParam) {
        String uri = "teams/" + teamId + "/participants/search/participant?identifier=" + theParam;
        String orderData = getData(uri);
        if (orderData != null && orderData.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (orderData != null && orderData.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        List<ServiceRequestDto> serviceRequestDtoList = new ArrayList<>();
        ServiceRequestDto serviceRequestDto = new Gson()
                .fromJson(orderData, orderType);
        serviceRequestDtoList.add(serviceRequestDto);

        int page = 2;
        while (serviceRequestDto.isHasNextPage()) {
            String url = "teams/" + teamId + "/participants/search/participant?identifier=" + theParam + "&page=" + page;
            orderData = getData(url);
            ServiceRequestDto orderDto = new Gson()
                    .fromJson(orderData, orderType);
            serviceRequestDtoList.add(orderDto);
            if (!orderDto.isHasNextPage())
                break;
            page++;
        }
        logger.info("Patient Data in search by Identifier  --{}", serviceRequestDtoList.size());

        if (!serviceRequestDtoList.isEmpty()) {
            return serviceRequestDtoList;
        } else {
            logger.warn("patientId not found ....{}", theParam);
        }
        return null;
    }

    /**
     * Makes Http call to Kpro's Update  Patient Internal API
     *
     * @param orderRequest
     * @param patientId
     * @return
     */

    public OrderDto updateOrder(OrderRequest orderRequest, String patientId) {
        logger.info("Inside Update order --->{}", orderRequest);
        String uri = "participants/" + patientId + "/profile";
        Type type = new TypeToken<OrderDto>() {
        }.getType();


        Gson gson = new Gson();
        String requestJson = gson.toJson(orderRequest);

        String response = putData(uri, requestJson);
        if (response != null && response.equalsIgnoreCase("Not Found from Kpro Server")) {
            throw new ResourceNotFoundException("Not Found from Kpro Server");
        } else if (response != null && response.equalsIgnoreCase("Bad Request to Kpro Server")) {
            throw new InvalidRequestException("Bad Request to Kpro Server");
        }
        logger.info("Inside create kardia patient client response --->{}", response);

        if (response != null && !response.equalsIgnoreCase("")) {
            OrderDto orderDtoDtoResponse = gson.fromJson(response, type);
            if (orderDtoDtoResponse != null) {
                logger.info("patientDto : {}", orderDtoDtoResponse);
                return orderDtoDtoResponse;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

