package com.index325.dschallenge1.services;

import com.index325.dschallenge1.dto.ClientDTO;
import com.index325.dschallenge1.entities.Client;
import com.index325.dschallenge1.repositories.ClientRepository;
import com.index325.dschallenge1.services.exception.DatabaseException;
import com.index325.dschallenge1.services.exception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(PageRequest pageRequest) {
        Page<ClientDTO> clients = clientRepository.findAll(pageRequest).map(x -> new ClientDTO(x));

        return clients;
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> client = clientRepository.findById(id);

        client.orElseThrow(() -> new ResourceNotFoundException("Was not possible to find your resource"));

        return new ClientDTO(client.get());
    }

    @Transactional
    public ClientDTO insert(ClientDTO clientDTO) {
        Client client = new Client();

        BeanUtils.copyProperties(clientDTO, client);

        client = clientRepository.save(client);

        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO update(ClientDTO clientDTO, Long id) {
        try {
            Optional<Client> clientOptional = clientRepository.findById(id);

            Client client = new Client();
            BeanUtils.copyProperties(clientDTO, client);
            client.setId(clientOptional.get().getId());

            client = clientRepository.save(client);

            return new ClientDTO(client);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Such client was not found");
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            clientRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("This client that you`re trying to delete was not found");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation check your request and try again");
        }
    }
}
