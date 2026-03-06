package cat.udl.eps.softarch.fll.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ManageFloaterStepDefs {

    private final StepDefs stepDefs;
    private String currentFloaterUrl;

    public ManageFloaterStepDefs(StepDefs stepDefs) {
        this.stepDefs = stepDefs;
    }

    @When("I request to create a floater with name {string} and student code {string}")
    public void i_request_to_create_a_floater(String name, String studentCode) throws Exception {
        String payload = String.format(
            "{\"name\": \"%s\", \"studentCode\": \"%s\", \"emailAddress\": \"test@fll.com\", \"phoneNumber\": \"123456789\"}",
            name, studentCode
        );

        stepDefs.result = stepDefs.mockMvc.perform(post("/floaters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user("admin").roles("ADMIN")));
        
        if (stepDefs.result.andReturn().getResponse().getStatus() == 201) {
            currentFloaterUrl = stepDefs.result.andReturn().getResponse().getHeader("Location");
        }
    }

    @Given("a floater exists with name {string} and student code {string}")
    public void a_floater_exists(String name, String studentCode) throws Exception {
        i_request_to_create_a_floater(name, studentCode);
    }

    @When("I request to retrieve that floater")
    public void i_request_to_retrieve_that_floater() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(get(currentFloaterUrl)
                .with(user("admin").roles("ADMIN")));
    }

    @When("I request to update the floater name to {string}")
    public void i_request_to_update_the_floater_name(String newName) throws Exception {
        String payload = String.format("{\"name\": \"%s\"}", newName);
        
        stepDefs.result = stepDefs.mockMvc.perform(patch(currentFloaterUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user("admin").roles("ADMIN")));
    }

    @When("I request to delete that floater")
    public void i_request_to_delete_that_floater() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(delete(currentFloaterUrl)
                .with(user("admin").roles("ADMIN")));
    }

    @Then("the floater API response status should be {int}")
    public void the_floater_api_response_status_should_be(int expectedStatus) throws Exception {
        stepDefs.result.andExpect(status().is(expectedStatus));
    }
}