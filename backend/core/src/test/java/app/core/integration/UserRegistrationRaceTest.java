package app.core.integration;

import org.springframework.test.context.ActiveProfiles;

//TODO: test for data race on simultaneous users creation
@ActiveProfiles("integration")
public class UserRegistrationRaceTest {
}
