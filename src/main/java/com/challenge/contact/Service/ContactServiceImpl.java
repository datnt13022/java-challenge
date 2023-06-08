package com.challenge.contact.Service;
import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Entity.Message;
import com.challenge.contact.Exception.ConflictEmailException;
import com.challenge.contact.Exception.DeleteContactException;
import com.challenge.contact.Exception.NotFoundException;
import com.challenge.contact.Exception.SaveContactException;
import com.challenge.contact.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }


    @Override
    public List<Contact> getAllContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contactRepository.findAll(pageable).getContent();
    }

    @Override
    public Optional<Contact> getContactById(Long id) {
        return Optional.ofNullable(contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact not found with id: " + id)));
    }

    @Override
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new NotFoundException("Contact not found with id: " + id);
        }
        contactRepository.deleteById(id);
        throw new DeleteContactException("delete contact with id: " + id);

    }

    @Override
    public Contact updateContact(Contact contact) {
        if (!contactRepository.existsById(contact.getId())) {
            throw new NotFoundException("Contact not found with id: " + contact.getId());
        }
        return contactRepository.save(contact);

    }

    @Override
    public List<Contact> searchContacts(String firstName, String lastName) {
        return contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstName, lastName);
    }
    @Override
    public void addContact(Contact contact) {
        if (!contactRepository.existsByEmail(contact.getEmail())) {
            contactRepository.save(contact);
            throw new SaveContactException("Save contact with email: " + contact.getEmail());
        }
        throw new ConflictEmailException("Your email is exist");

    }
}
