package com.ninehub.dreamshops.data;

import com.ninehub.dreamshops.model.Role;
import com.ninehub.dreamshops.model.User;
import com.ninehub.dreamshops.repositry.RoleRepository;
import com.ninehub.dreamshops.repositry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        this.createDefaultRoleIfNotExist(defaultRoles);
        this.createDefaultUserIfNotExist();
        this.createDefaultAdminIfNotExist();
    }

    private void createDefaultUserIfNotExist() {
        Optional<Role> optionalUserRole = roleRepository.findByRoleName("ROLE_USER");

        if (optionalUserRole.isEmpty()) {
            throw new IllegalStateException("ROLE_USER not found. Please create it first.");
        }

        Role userRole = optionalUserRole.get();

        for (int i=1; i <= 5 ; i++){
            String defaultEmail = "user"+i+"@gmail.com";

            if (userRepository.existsByEmail(defaultEmail)) continue;

            User user = new User();
            user.setFirstName("Beautero");
            user.setLastName("Ken"+i);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setEmail(defaultEmail);
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Default user "+i+" created successfully.");
        }
    }

    private void createDefaultAdminIfNotExist() {
        Optional<Role> optionalAdminRole = roleRepository.findByRoleName("ROLE_ADMIN");

        if (optionalAdminRole.isEmpty()) {
            throw new IllegalStateException("ROLE_ADMIN not found. Please create it first.");
        }

        Role adminRole = optionalAdminRole.get();

        for (int i=1; i <= 2 ; i++){
            String defaultEmail = "admin"+i+"@gmail.com";

            if (userRepository.existsByEmail(defaultEmail)) continue;

            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Ken"+i);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setEmail(defaultEmail);
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default admin user "+i+" created successfully.");
        }
    }

    private void createDefaultRoleIfNotExist(Set<String> roles) {
        roles.stream()
                .filter(role -> roleRepository.findByRoleName(role).isEmpty())
                .map(Role::new)
                .forEach(roleRepository::save);
    }

}
