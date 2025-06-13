package org.voting.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CpfValidationResponse {
    private String status;
}
