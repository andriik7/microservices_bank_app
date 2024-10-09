package com.microservicesbank.loans.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(
        name = "ErrorResponse",
        description = "Schema to store error response information"
)
@Data @AllArgsConstructor
public class ErrorResponseDTO {

    @Schema(
            description = "API path invoked by the client"
    )
    private String apiPath;

    @Schema(
            description = "Error code representing the error happened",
            example = "XXX"
    )
    private HttpStatus errorCode;

    @Schema(
            description = "Error message representing the error happened"
    )
    private String errorMessage;

    @Schema(
            description = "Time representing the error happened",
            example = "2024-01-01T00:00:00.000"
    )
    private LocalDateTime errorTime;
}
