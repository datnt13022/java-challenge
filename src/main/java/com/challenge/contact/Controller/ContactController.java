package com.challenge.contact.Controller;

import com.challenge.contact.Entity.Contact;
import com.challenge.contact.Entity.Message;
import com.challenge.contact.Exception.NotFoundException;
import com.challenge.contact.Exception.SaveContactException;
import com.challenge.contact.Service.ContactService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Slf4j(topic = "CONTACT_CONTROLLER")
@RestController
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public List<Contact> getAllContacts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return contactService.getAllContacts(page, size);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        return contact.map(value -> ResponseEntity.ok().body(value))
                .orElseThrow(() -> new RuntimeException("No contact by ID: " + id));
    }
    @PostMapping("/add")
    public void addContact(@RequestBody @Valid Contact contact) {
        contactService.addContact(contact);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateContact(@PathVariable Long id, @Valid @RequestBody Contact contact) {
        Optional<Contact> existingContact = contactService.getContactById(id);
        if (existingContact.isPresent()) {
            contact.setId(id);
            contactService.updateContact(contact);
            throw new SaveContactException("Contact is updated");
        } else {
            throw new NotFoundException("Contact not found with id: " + id);
        }
    }

    @GetMapping("/search")
    public List<Contact> searchContacts(@RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName) {
        return contactService.searchContacts(firstName, lastName);
    }

}