package com.swp.DataModel;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


/**
 * Klasse, die Booleans in String Charakters umwandelt, damit bspw. TrueFalseCard verwendet werden kann.
 * @Author: Thorben Janssen: https://thorben-janssen.com/hibernate-tips-how-to-map-a-boolean-to-y-n/
 */
@Converter
public class BooleanConverter implements AttributeConverter<Boolean,String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute != null) {
            if (attribute) {
                return "Y";
            } else {
                return "N";
            }

        }
        return null;
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            return dbData.equals("Y");
        }
        return null;
    }
}
