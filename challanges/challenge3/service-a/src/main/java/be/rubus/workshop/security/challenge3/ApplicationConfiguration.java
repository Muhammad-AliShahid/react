package be.rubus.workshop.security.challenge3;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@FormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login.xhtml",
                errorPage = "/login-error.xhtml"))
@DatabaseIdentityStoreDefinition(
        callerQuery = "select password from users where name = ?",
        groupsQuery = "select group_name from user_groups where user_name = ?"
)

@ApplicationScoped
public class ApplicationConfiguration {
}
