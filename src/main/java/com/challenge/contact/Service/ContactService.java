package com.challenge.contact.Service;

import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Entity.Message;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    List<Contact> getAllContacts(int page, int size);
    Optional<Contact> getContactById(Long id);
    void deleteContact(Long id);
    Contact updateContact(Contact contact);
    List<Contact> searchContacts(String firstName, String lastName);

    void addContact(Contact contact);
}