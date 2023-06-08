package com.challenge.contact;

import com.challenge.contact.Controller.ContactController;
import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Exception.CustomExceptionHandler;
import com.challenge.contact.Exception.DeleteContactException;
import com.challenge.contact.Exception.NotFoundException;
import com.challenge.contact.Exception.SaveContactException;
import com.challenge.contact.Repository.ContactRepository;
import com.challenge.contact.Service.ContactService;
import com.challenge.contact.Service.ContactServiceImpl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContactControllerTest {
    @Mock
    private ContactServiceImpl contactService;
    @Mock
    private ContactRepository contactRepository;


    @InjectMocks
    private ContactController contactController;

//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(contactController)
//                .setControllerAdvice(new CustomExceptionHandler())
//                .build();
//    }
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetAllContacts() throws Exception {
        List<Contact> contacts = Arrays.asList(
                new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi"),
                new Contact(2L, "nguyen thanh", "dat", "datnt2@gmail.com", "123456789", "2 le loi")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> contactPage = new PageImpl<>(contacts, pageable, contacts.size());
        when(contactService.getAllContacts(0, 10)).thenReturn(contactPage.getContent());
        List<Contact> contact = contactController.getAllContacts(0, 10);
        Assert.assertEquals(contacts.size(), contact.size());
        Assert.assertEquals(contacts.get(0), contact.get(0));
        Assert.assertEquals(contacts.get(1), contact.get(1));
        verify(contactService).getAllContacts(0, 10);
    }
    @Test
    public void testGetContactById() throws NotFoundException {
        Contact contact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        when(contactService.getContactById(1L)).thenReturn(Optional.of(contact));
        ResponseEntity<Contact> response = contactController.getContactById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contact, response.getBody());
        verify(contactService, times(1)).getContactById(1L);
    }
    @Test
    public void testUpdateContact() throws SaveContactException{
        Contact contact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        Optional<Contact> existingContact = Optional.of(contact);
        when(contactService.getContactById(eq(1L)))
                .thenReturn(existingContact);
        assertThatThrownBy(() -> contactController.updateContact(1L, contact))
                .isInstanceOf(SaveContactException.class)
                .hasMessage("Contact is updated");
        verify(contactService, times(1)).updateContact(contact);
    }
    @Test
    public void testDeleteContact() throws DeleteContactException {
        DeleteContactException deleteContactException = new DeleteContactException("delete contact with id: " + 1L);
        Mockito.doThrow(deleteContactException).when(contactService).deleteContact(1L);
        assertThrows(DeleteContactException.class, () -> contactController.deleteContact(1L));
        verify(contactService, times(1)).deleteContact(1L);
    }
    @Test
    public void testSearchContacts() {
        String firstName = "nguyen thanh";
        String lastName = "dat";
        List<Contact> expectedContacts = Arrays.asList(new Contact(), new Contact());

        when(contactService.searchContacts(eq(firstName), eq(lastName))).thenReturn(expectedContacts);

        List<Contact> contacts = contactController.searchContacts(firstName, lastName);
        assertEquals(expectedContacts, contacts);

        verify(contactService, times(1)).searchContacts(eq(firstName), eq(lastName));
    }
    @Test
    public void testAddContact() {
        Contact contact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        contactController.addContact(contact);
        verify(contactService, times(1)).addContact(contact);
    }
}