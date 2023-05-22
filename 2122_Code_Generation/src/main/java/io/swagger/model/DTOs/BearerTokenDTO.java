package io.swagger.model.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BearerTokenDTO {
    @JsonProperty("bearerToken")
    private String bearerToken = null;

    public BearerTokenDTO bearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
        return this;
    }

    /**
     * Get bearerToken
     * @return bearerToken
     **/
    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BearerTokenDTO bearerTokenDto = (BearerTokenDTO) o;
        return Objects.equals(this.bearerToken, bearerTokenDto.bearerToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bearerToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BearerTokenDto {\n");

        sb.append("    bearerToken: ").append(toIndentedString(bearerToken)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
