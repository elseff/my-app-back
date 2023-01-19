package com.elseff.project.web.api.modules.user.controller;

import com.elseff.project.web.api.modules.user.dto.UserAllFieldsCanBeNullDto;
import com.elseff.project.web.api.modules.user.dto.UserAllFieldsDto;
import com.elseff.project.web.api.modules.user.dto.UserDto;
import com.elseff.project.web.api.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User controller", description = "User management")
@CrossOrigin(origins = {"http://192.168.100.4:4200", "http://localhost:4200"})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserAllFieldsDto.class))
                    ),
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get specific user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserAllFieldsDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAllFieldsDto getSpecific(@Parameter(description = "User id")
                                        @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Delete user",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No content. User has been successfully deleted",
                            content = {@Content()}
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Parameter(description = "User id")
                           @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Update user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User has been successfully updated",
                            content = @Content(schema = @Schema(implementation = UserAllFieldsCanBeNullDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "User not valid", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAllFieldsDto updateUser(@Parameter(description = "Updated user")
                                       @RequestBody
                                       @Valid
                                               UserAllFieldsCanBeNullDto userAllFieldsCanBeNullDto,
                                       @Parameter(description = "User id")
                                       @PathVariable
                                               Long id) {
        return userService.updateUser(id, userAllFieldsCanBeNullDto);
    }
}