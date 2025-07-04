package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.QuotaPret;
import com.rtsmitia.bibliotheque.models.TypeAdherent;
import com.rtsmitia.bibliotheque.repositories.QuotaPretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuotaPretService {

    private final QuotaPretRepository quotaPretRepository;

    @Autowired
    public QuotaPretService(QuotaPretRepository quotaPretRepository) {
        this.quotaPretRepository = quotaPretRepository;
    }

    /**
     * Get all quota prêts
     */
    public List<QuotaPret> getAllQuotaPrets() {
        return quotaPretRepository.findAll();
    }

    /**
     * Get quota prêt by ID
     */
    public Optional<QuotaPret> getQuotaPretById(Long id) {
        return quotaPretRepository.findById(id);
    }

    /**
     * Get current quota prêt for a specific adherent type
     */
    public QuotaPret getCurrentQuotaForType(TypeAdherent typeAdherent) {
        Optional<QuotaPret> quota = quotaPretRepository.findCurrentByTypeAdherent(typeAdherent);
        return quota.orElse(null);
    }

    /**
     * Get loan duration for a specific adherent type
     */
    public Integer getLoanDurationForType(TypeAdherent typeAdherent) {
        QuotaPret quota = getCurrentQuotaForType(typeAdherent);
        return quota != null ? quota.getNombreJourPret() : 14; // Default 14 days
    }

    /**
     * Get maximum books allowed for a specific adherent type
     */
    public Integer getMaxBooksForType(TypeAdherent typeAdherent) {
        QuotaPret quota = getCurrentQuotaForType(typeAdherent);
        return quota != null ? quota.getNombreLivre() : 3; // Default 3 books
    }

    /**
     * Create a new quota prêt
     */
    public QuotaPret createQuotaPret(QuotaPret quotaPret) {
        return quotaPretRepository.save(quotaPret);
    }

    /**
     * Update quota prêt
     */
    public QuotaPret updateQuotaPret(QuotaPret quotaPret) {
        return quotaPretRepository.save(quotaPret);
    }

    /**
     * Delete quota prêt
     */
    public void deleteQuotaPret(Long id) {
        quotaPretRepository.deleteById(id);
    }
}
