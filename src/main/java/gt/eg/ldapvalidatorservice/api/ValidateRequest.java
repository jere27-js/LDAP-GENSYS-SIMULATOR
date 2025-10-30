package gt.eg.ldapvalidatorservice.api;

import jakarta.validation.constraints.NotBlank;

public record ValidateRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
