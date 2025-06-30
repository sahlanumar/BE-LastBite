package Group3.CourseApp.config;


import Group3.CourseApp.constant.UserRole;
import Group3.CourseApp.entity.Role;
import Group3.CourseApp.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        for (UserRole role : UserRole.values()) {
            if (!roleRepository.findByName(role).isPresent()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }
    }
}
