package ch.zhaw.freelancer4u.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


@Service
public class RoleService {

    public boolean userHasRole(String role) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> userRoles = jwt.getClaimAsStringList("user_roles");
        if (userRoles.contains(role)) {
            return true;
        }
        return false;
    }
}
