package com.challenge.contact;

import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Exception.DeleteContactException;
import com.challenge.contact.Exception.NotFoundException;
import com.challenge.contact.Repository.ContactRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContactControllerIntegrationTest {
    @Autowired
    private ContactRepository contactRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Before
    public void setUp() {
        Contact contact1 = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        Contact contact2 = new Contact(2L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        contactRepository.saveAll(Arrays.asList(contact1, contact2));
    }
    @Test
    public void testAddContact() throws Exception {
        Contact contact = new Contact();
        contact.setEmail("datnt3@gmail.com");
        contact.setFirstName("nguyen thanh");
        contact.setLastName("dat");
        contact.setPhoneNumber("123456789");
        contact.setPostalAddress("1 le loi");
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/contact/add",
                contact,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    @Test
    public void testGetAllContacts() throws Exception  {
        ResponseEntity<List<Contact>> response = restTemplate.exchange(
                "/contact",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Contact>>() {}
        );

        List<Contact> contacts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(contacts).isNotNull();
        assertThat(contacts.size()).isGreaterThan(0);
    }

    @Test
    public void testGetContactById() throws Exception {
        Long contactId = 1L;

        ResponseEntity<Contact> response = restTemplate.getForEntity(
                "/contact/" + contactId,
                Contact.class
        );

        Contact contact = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(contact).isNotNull();
        assertThat(contact.getId()).isEqualTo(contactId);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    @Test
    public void testDeleteContact() throws Exception {
        List<Contact> contacts = contactRepository.findAll();
        Long contactId = contacts.get(0).getId();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                createURLWithPort("/contact/" + contactId),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        ResponseEntity<Contact> getResponse = restTemplate.getForEntity(
                createURLWithPort("/contact/" + contactId),
                Contact.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateContact() throws Exception {

        List<Contact> contacts = contactRepository.findAll();
        Long contactId = contacts.get(0).getId();
        Contact contact = new Contact();
        contact.setEmail("datnt3@gmail.com");
        contact.setFirstName("nguyen thanh");
        contact.setLastName("dat");
        contact.setPhoneNumber("123456789");
        contact.setPostalAddress("1 le loi");
        contact.setId(contactId);
        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/contact/" + contactId),
                HttpMethod.PUT,
                new HttpEntity<>(contact),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testSearchContacts_firstName() throws Exception {
        String firstName = "nguyen";
        ResponseEntity<List<Contact>> response = restTemplate.exchange(
                "/contact/search?firstName=" + firstName ,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Contact>>() {}
        );

        List<Contact> contacts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(contacts).isNotNull();
        assertThat(contacts.size()).isGreaterThan(0);
    }
    @Test
    public void testSearchContacts_lastName() throws Exception {
        String lastName = "dat";
        ResponseEntity<List<Contact>> response = restTemplate.exchange(
                "/contact/search?"  + "&lastName=" + lastName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Contact>>() {}
        );

        List<Contact> contacts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(contacts).isNotNull();
        assertThat(contacts.size()).isGreaterThan(0);
    }
    @Test
    public void testSearchContacts_firstName_lastName()throws Exception  {
        String firstName = "nguyen";
        String lastName = "dat";

        ResponseEntity<List<Contact>> response = restTemplate.exchange(
                "/contact/search?firstName=" + firstName + "&lastName=" + lastName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Contact>>() {}
        );

        List<Contact> contacts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(contacts).isNotNull();
        assertThat(contacts.size()).isGreaterThan(0);
    }
}
