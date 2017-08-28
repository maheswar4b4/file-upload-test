package com.test.fileUpload.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.fileUpload.exceptions.FileUploadException;
import com.test.fileUpload.repository.PersonRepository;
import com.test.fileUpload.entity.Person;
import com.test.fileUpload.reps.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MultipartResolver multipartResolver;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private Environment environment;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private UploadResponse createUploadResponseRep(HttpServletRequest request) {
        UploadResponse rep = new UploadResponse();
        rep.setRequestType(request.getClass().getName());
        rep.setMultipartResolverType(multipartResolver == null ? null : multipartResolver.getClass().getName());
        rep.setFileSizeThreshold(environment.getProperty("spring.http.multipart.file-size-threshold"));
        return rep;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/multipartFile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public UploadResponse getViaMultipartFile(@RequestParam(required = false) MultipartFile file, HttpServletRequest request) throws Exception {
        if (file == null)
            throw new FileUploadException("MultipartFile was null (likely no parts on request).  Make sure Spring's multipart support is enabled for this upload method to work");
        String string = new String(file.getBytes(), "UTF-8");
        Person person = objectMapper.readValue(string, Person.class);
        personRepository.save(person);
        UploadResponse rep = createUploadResponseRep(request);
        try (InputStream in = file.getInputStream()) {
            rep.setInputStreamType(in.getClass().getName());
        }
        return rep;
    }

    @RequestMapping(value = "/persons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Person> getPersons() throws Exception {
        Iterable<Person> personIterable = personRepository.findAll();
        List<Person> list = new ArrayList<>();
        for(Person person: personIterable)
            list.add(person);
        return list;
    }

}