package ru.abondarenko.dev.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.abondarenko.dev.spring.controllers.config.WithCustomUser;
import ru.abondarenko.dev.spring.model.SecretRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class SecuredControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @WithAnonymousUser
    void getUnsecuredInfo_anonymousUser_shouldReturnUnsecuredString() throws Exception {
        // Config
        val expected = "not-secured-string";

        // Call and verify
        mockMvc.perform(get("/secured"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    @WithMockUser(roles = {"READ_SECURED_INFO"})
    void getSecuredByRoleInfo_correctRole_shouldReturnSecuredString() throws Exception {
        // Config
        val expected = "secret-string-for-role";

        // Call and verify
        mockMvc.perform(get("/secured/has_role"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    @WithMockUser(roles = {"WRONG_ROLE"})
    void getSecuredByRoleInfo_incorrectRole_shouldReturnReturnForbidden() throws Exception {
        // Call and verify
        mockMvc.perform(get("/secured/has_role"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomUser(
            id = "1",
            email = "mock@user.com",
            authorities = {"READ_SECURED_INFO"}
    )
    void getSecuredByAuthenticationInfo_authenticatedUser_shouldReturnSecuredString() throws Exception {
        // Config
        val email = "mock@user.com";
        val expected = "secret-string-for-user-" + email;

        // Call and verify
        mockMvc.perform(get("/secured/authenticated"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    @WithAnonymousUser
    void getSecuredByAuthenticationInfo_notAuthenticatedUser_shouldReturnForbidden() throws Exception {
        // Call and verify
        mockMvc.perform(get("/secured/authenticated"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomUser(
            id = "1",
            email = "mock@user.com",
            authorities = {"READ_SECURED_INFO"}
    )
    void getSecuredByEmailInfo_correctEmail_shouldReturnSecuredString() throws Exception {
        // Config
        val correctEmail = "mock@user.com";
        val query = "very-secret-query";
        val expected = "secret-string-for-request-" + query;
        val request = mapper.writeValueAsString(new SecretRequest(correctEmail, query));

        // Call and verify
        mockMvc.perform(post("/secured/has_email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    @WithCustomUser(
            id = "1",
            email = "mock@user.com",
            authorities = {"READ_SECURED_INFO"}
    )
    void getSecuredByEmailInfo_incorrectEmail_shouldReturnForbidden() throws Exception {
        // Config
        val incorrectEmail = "wrong@email.com";
        val query = "very-secret-query";
        val request = mapper.writeValueAsString(new SecretRequest(incorrectEmail, query));

        // Call and verify
        mockMvc.perform(post("/secured/has_email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(status().isForbidden());
    }
}