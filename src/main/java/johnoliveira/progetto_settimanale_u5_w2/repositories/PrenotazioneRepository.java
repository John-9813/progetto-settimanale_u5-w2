package johnoliveira.progetto_settimanale_u5_w2.repositories;


import johnoliveira.progetto_settimanale_u5_w2.entities.Dipendente;
import johnoliveira.progetto_settimanale_u5_w2.entities.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    // Metodo per verificare se un dipendente ha già una prenotazione per un certo giorno
    boolean existsByDipendenteAndData(Dipendente dipendente, LocalDate data);

    boolean existsByDipendenteAndDataAndIdNot(Dipendente dipendente, LocalDate data, Long id);
}

