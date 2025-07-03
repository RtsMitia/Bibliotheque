package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatutPretEnumConverter implements AttributeConverter<StatutPret.StatutPretEnum, String> {

    @Override
    public String convertToDatabaseColumn(StatutPret.StatutPretEnum statut) {
        if (statut == null) {
            return null;
        }
        return statut.toString(); // This returns the value from enum constructor
    }

    @Override
    public StatutPret.StatutPretEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        
        // Convert database string back to enum
        for (StatutPret.StatutPretEnum statut : StatutPret.StatutPretEnum.values()) {
            if (statut.toString().equals(dbData)) {
                return statut;
            }
        }
        
        throw new IllegalArgumentException("Unknown statut value: " + dbData);
    }
}
