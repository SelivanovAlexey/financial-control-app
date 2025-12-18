package app.core.mappers;

import app.core.model.UserEntity;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password",expression = "java(passwordEncoder.encode(request.password()))")
    @Mapping(target = "displayName", expression = "java(request.displayName() == null || request.displayName().isBlank() ? request.username() : request.displayName())")
    @Mapping(target = "authorities", ignore = true)
    UserEntity createUserFromRequest(CreateUserRequestDto request, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", expression = "java(request.password() == null ? user.getPassword() : passwordEncoder.encode(request.password()))")
    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UpdateUserRequestDto request, @MappingTarget UserEntity user, @Context PasswordEncoder passwordEncoder);

    UserResponseDto toResponse(UserEntity user);
}
