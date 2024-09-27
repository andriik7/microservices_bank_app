package com.microsevicesbank.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(
        name = "Response",
        description = "Response object for REST APIs"
)
@Data
@AllArgsConstructor
public class ResponseDTO {

    @Schema(
            description = "Status code of the response"
    )
    private String statusCode;

    @Schema(
            description = "Status message of the response"
    )
    private String statusMessage;
}
