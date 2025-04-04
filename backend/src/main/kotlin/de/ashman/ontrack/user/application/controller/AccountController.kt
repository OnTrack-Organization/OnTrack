package de.ashman.ontrack.user.application.controller

import com.google.firebase.auth.FirebaseToken
import de.ashman.ontrack.user.infrastructure.UserRepository
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val userRepository: UserRepository
) {
    @GetMapping("/account")
    fun getAccountSettings(@AuthenticationPrincipal token: FirebaseToken): ResponseEntity<UserDto> {
        val user = userRepository.getReferenceById(token.uid)

        return ResponseEntity.ok(user.toDto())
    }

    @PostMapping("/account")
    @Transactional
    fun changeAccountSetting(
        @AuthenticationPrincipal token: FirebaseToken,
        @RequestBody @Valid accountSetting: AccountSettingDto
    ): ResponseEntity<String> {
        val user = userRepository.getReferenceById(token.uid)
        user.updateName(accountSetting.name)

        val usernameExists = userRepository.existsUserByUsername(accountSetting.username)
        if (usernameExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is taken")
        }

        user.updateUsername(accountSetting.username)

        return ResponseEntity.ok().build()
    }

    @PostMapping("/account/profile-picture")
    @Transactional
    fun changeProfilePicture(
        @AuthenticationPrincipal token: FirebaseToken,
        @RequestBody profilePictureDto: ProfilePictureDto
    ): ResponseEntity<Unit> {
        val user = userRepository.getReferenceById(token.uid)
        user.changeProfilePictureUrl(profilePictureDto.pictureUrl)

        return ResponseEntity.ok().build()
    }
}
