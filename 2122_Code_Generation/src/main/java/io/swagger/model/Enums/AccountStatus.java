package io.swagger.model.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountStatus {

    ACTIVE("active"), //when coping this, capitalize value inside parenthesis

    CLOSED("closed"),

    INACTIVE("inactive"),

    BLOCKED("blocked");

    private String value;

    AccountStatus(String value)
    {
        this.value = value;
    }

    @JsonCreator
    public static AccountStatus fromValue(String text)
    {
        for (AccountStatus b : AccountStatus.values())
        {
            if (String.valueOf(b.value).equals(text))
            {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return String.valueOf(value);
    }
}
