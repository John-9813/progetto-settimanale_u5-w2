package johnoliveira.progetto_settimanale_u5_w2.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import johnoliveira.progetto_settimanale_u5_w2.entities.Dipendente;
import johnoliveira.progetto_settimanale_u5_w2.exceptions.BadRequestException;
import johnoliveira.progetto_settimanale_u5_w2.exceptions.ResourceNotFoundException;
import johnoliveira.progetto_settimanale_u5_w2.payloads.NewDipendenteDTO;
import johnoliveira.progetto_settimanale_u5_w2.repositories.DipendenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DipendenteService {

    @Autowired
    private DipendenteRepository dipendenteRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    // salvo il dipendente con  verifica email
    public Dipendente save(NewDipendenteDTO dipendente) {
        // 1. Verifico che l'email non sia già in uso
        this.dipendenteRepository.findByEmail(dipendente.getEmail()).ifPresent(
                Dipendente -> {
                    throw new BadRequestException("Email " + dipendente.getEmail() + " già in uso!");
                }
        );

        // 2. Salvo il nuovo dipendente
        return this.dipendenteRepository.save(dipendente);
    }

    // trovo tutti i dipendenti e gestisco elementi per paginazione
    public List<Dipendente> findAll(int page, int size, String sortBy) {
        // Limitiamo il numero massimo di elementi per pagina a 10
        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return dipendenteRepository.findAll(pageable).getContent();
    }

    // trovo uno specifico dipendente
    public Dipendente findById(Long dipendenteId) {
        return dipendenteRepository.findById(dipendenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Dipendente non trovato con ID: " + dipendenteId));
    }

    // aggiorno un dipendente e verifica email
    public Dipendente findByIdAndUpdate(Long dipendenteId, NewDipendenteDTO nuoviDati) {
        // 1. Trova il dipendente nel database
        Dipendente found = this.findById(dipendenteId);

        // 2. Verifica se l'email è già in uso da un altro dipendente
        if (!found.getEmail().equals(nuoviDati.getEmail())) {
            this.dipendenteRepository.findByEmail(nuoviDati.getEmail()).ifPresent(
                    Dipendente -> {
                        throw new BadRequestException("Email " + nuoviDati.getEmail() + " già in uso!");
                    }
            );
        }

        // 3. Aggiorna i campi del dipendente
        found.setUsername(nuoviDati.getUsername());
        found.setNome(nuoviDati.getNome());
        found.setCognome(nuoviDati.getCognome());
        found.setEmail(nuoviDati.getEmail());

        // 4. Salva le modifiche
        return this.dipendenteRepository.save(found);
    }

    // trova un dipendente e elimina
    public void findByIdAndDelete(Long dipendenteId) {
        Dipendente found = this.findById(dipendenteId);
        this.dipendenteRepository.delete(found);
    }

    // upload immagine dipendente
    public String uploadAvatar(Long dipendenteId, MultipartFile file) {
        String url = null;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException e) {
            throw new BadRequestException("Ci sono stati problemi con l'upload del file!");
        }
        return url;
    }
}


