package org.voting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.voting.client.dto.CpfValidationResponse;

@FeignClient(name = "cpfValidationClient", url = "https://user-info.herokuapp.com")
public interface CpfValidationClient {

    @GetMapping("/users/{cpf}")
    CpfValidationResponse validateCpf(@PathVariable("cpf") String cpf);
}

