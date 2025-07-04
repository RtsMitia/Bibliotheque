package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.JourFerie;
import com.rtsmitia.bibliotheque.repositories.JourFerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JourFerieService {

    private final JourFerieRepository jourFerieRepository;

    @Autowired
    public JourFerieService(JourFerieRepository jourFerieRepository) {
        this.jourFerieRepository = jourFerieRepository;
    }

    /**
     * Check if a specific date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return jourFerieRepository.isDateHoliday(date);
    }

    /**
     * Find the next available date (not a holiday)
     * If the given date is not a holiday, returns the same date
     * If it is a holiday, finds the next available date after the holiday period
     */
    public LocalDate getNextAvailableDate(LocalDate date) {
        LocalDate currentDate = date;
        
        // Keep checking until we find a non-holiday date
        while (isHoliday(currentDate)) {
            List<JourFerie> holidays = jourFerieRepository.findHolidaysContainingDate(currentDate);
            if (!holidays.isEmpty()) {
                // Find the latest end date among overlapping holidays
                LocalDate latestEndDate = holidays.stream()
                    .map(holiday -> holiday.getDateFin() != null ? holiday.getDateFin() : holiday.getDateDebut())
                    .max(LocalDate::compareTo)
                    .orElse(currentDate);
                
                // Move to the day after the holiday period
                currentDate = latestEndDate.plusDays(1);
            } else {
                // Should not happen, but safety fallback
                currentDate = currentDate.plusDays(1);
            }
        }
        
        return currentDate;
    }

    /**
     * Adjust loan end date to avoid holidays
     * If the end date falls on a holiday, it will be moved to the next available date
     */
    public LocalDate adjustLoanEndDate(LocalDate proposedEndDate) {
        return getNextAvailableDate(proposedEndDate);
    }

    /**
     * Get all holidays
     */
    public List<JourFerie> getAllHolidays() {
        return jourFerieRepository.findAll();
    }

    /**
     * Get holiday by ID
     */
    public Optional<JourFerie> getHolidayById(Long id) {
        return jourFerieRepository.findById(id);
    }

    /**
     * Create a new holiday
     */
    public JourFerie createHoliday(LocalDate dateDebut, LocalDate dateFin) {
        JourFerie holiday = new JourFerie(dateDebut, dateFin);
        return jourFerieRepository.save(holiday);
    }

    /**
     * Create a single day holiday
     */
    public JourFerie createSingleDayHoliday(LocalDate date) {
        JourFerie holiday = new JourFerie(date);
        return jourFerieRepository.save(holiday);
    }

    /**
     * Update a holiday
     */
    public JourFerie updateHoliday(JourFerie holiday) {
        return jourFerieRepository.save(holiday);
    }

    /**
     * Delete a holiday
     */
    public void deleteHoliday(Long id) {
        jourFerieRepository.deleteById(id);
    }

    /**
     * Find holidays within a date range
     */
    public List<JourFerie> getHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        return jourFerieRepository.findHolidaysInRange(startDate, endDate);
    }

    /**
     * Find holidays starting from a specific date
     */
    public List<JourFerie> getHolidaysFromDate(LocalDate fromDate) {
        return jourFerieRepository.findHolidaysFromDate(fromDate);
    }

    /**
     * Validate holiday dates (no overlaps)
     */
    public boolean isValidHolidayPeriod(LocalDate dateDebut, LocalDate dateFin, Long excludeId) {
        LocalDate actualDateFin = (dateFin != null) ? dateFin : dateDebut;
        List<JourFerie> overlapping = jourFerieRepository.findOverlappingHolidays(
            dateDebut, actualDateFin, excludeId != null ? excludeId : 0L);
        return overlapping.isEmpty();
    }
}
