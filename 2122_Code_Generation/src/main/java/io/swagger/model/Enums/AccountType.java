package io.swagger.model.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    SAVINGS("savings"),

    CURRENT("current");

    private String value;

    AccountType(String value)
    {
        this.value = value;
    }

    @JsonCreator
    public static AccountType fromValue(String text)
    {
        for (AccountType b : AccountType.values())
        {
            if (String.valueOf(b.value).equals(text))
            {
                return b;
            }
        }
        return null;
    }

    public String getValue(){
        return this.value;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return String.valueOf(value);
    }
}
