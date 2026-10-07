package app.core.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


//TODO: make not to implement UserDetails. Move it to separate class.
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;
    @Column(name = "display_name", length = 128)
    private String displayName;
    @Column(name = "username", length = 128)
    private String username;
    @Column(name = "password_hash", length = 128)
    private String password;
    @Column(name = "email", length = 128)
    private String email;
    @Version
    private Integer version;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
