package com.challenge.contact;

import com.challenge.contact.Controller.ContactController;
import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Exception.ConflictEmailException;
import com.challenge.contact.Exception.DeleteContactException;
import com.challenge.contact.Exception.NotFoundException;
import com.challenge.contact.Exception.SaveContactException;
import com.challenge.contact.Repository.ContactRepository;
import com.challenge.contact.Service.ContactService;
import com.challenge.contact.Service.ContactServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

class ContactServiceTest {
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private ContactServiceImpl contactService;

    @Captor
    private ArgumentCaptor<Contact> contactCaptor;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testAddContact() {
        Contact contact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        SaveContactException save = new SaveContactException("Save contact with email: " + contact.getEmail());
        Mockito.doThrow(save).when(contactRepository).save(Mockito.any(Contact.class));
        assertThrows(SaveContactException.class, () -> contactService.addContact(contact));
        verify(contactRepository, times(1)).save(contactCaptor.capture());
        Contact capturedContact = contactCaptor.getValue();
        Assert.assertEquals("datnt@gmail.com", capturedContact.getEmail());
    }
    @Test
    void testGetAllContacts() {
        List<Contact> contacts = Arrays.asList(
                new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi"),
                new Contact(2L, "nguyen thanh", "dat", "datnt2@gmail.com", "123456789", "2 le loi")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> contactPage = new PageImpl<>(contacts, pageable, contacts.size());
        when(contactRepository.findAll(pageable)).thenReturn(contactPage);
        List<Contact> result = contactService.getAllContacts(0, 10);
        Assert.assertEquals(contacts.size(), result.size());
        Assert.assertEquals(contacts.get(0), result.get(0));
        Assert.assertEquals(contacts.get(1), result.get(1));
        verify(contactRepository).findAll(pageable);
    }

    @Test
    void testGetContactById() {
        Contact contact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        Optional<Contact> result = contactService.getContactById(1L);
        assertNotNull(result.get());
        Assert.assertEquals(contact, result.get());
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    void testGetContactById_ContactNotFound() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> contactService.getContactById(1L));
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteContact() {
        when(contactRepository.existsById(1L)).thenReturn(true);
        DeleteContactException delete = new DeleteContactException("delete contact with id: " + 1L);
        Mockito.doThrow(delete).when(contactRepository).save(Mockito.any(Contact.class));
        assertThrows(DeleteContactException.class, () -> contactService.deleteContact(1L));
        verify(contactRepository, times(1)).existsById(1L);
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteContact_ContactNotFound() {
        when(contactRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> contactService.deleteContact(1L));
        verify(contactRepository, times(1)).existsById(1L);
        verify(contactRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateContact() {
        Contact updatedContact = new Contact(1L, "nguyen thanh", "dat", "datnt@gmail.com", "123456789", "1 le loi");
        when(contactRepository.existsById(1L)).thenReturn(true);
        when(contactRepository.save(updatedContact)).thenReturn(updatedContact);
        Contact result = contactService.updateContact(updatedContact);
        assertNotNull(result);
        assertEquals(updatedContact, result);
        verify(contactRepository, times(1)).save(updatedContact);
    }
    @Test
    public void testSearchContacts() {
        String firstName = "John";
        String lastName = "Doe";
        List<Contact> expectedContacts = Arrays.asList(new Contact(), new Contact());

        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstName, lastName)).thenReturn(expectedContacts);

        List<Contact> contacts = contactService.searchContacts(firstName, lastName);
        assertEquals(expectedContacts, contacts);

        verify(contactRepository, times(1)).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstName, lastName);
    }
}