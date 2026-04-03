package offeria.api_gateway.mapper;

import offeria.api_gateway.domain.dto.UserRegistrationDto;
import offeria.api_gateway.domain.dto.UserResponseDto;
import offeria.api_gateway.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for User entity and DTOs using MapStruct.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Handled in service
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserRegistrationDto dto);

    UserResponseDto toResponseDto(User user);
}
