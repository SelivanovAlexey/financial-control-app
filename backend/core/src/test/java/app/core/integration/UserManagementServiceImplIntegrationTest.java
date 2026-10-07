package app.core.integration;

import org.springframework.test.context.ActiveProfiles;

//TODO: test for data race on simultaneous users creation
//TODO: test for optimistic locking on simultaneous users update/delete
@ActiveProfiles("integration")
public class UserManagementServiceImplIntegrationTest {
}
