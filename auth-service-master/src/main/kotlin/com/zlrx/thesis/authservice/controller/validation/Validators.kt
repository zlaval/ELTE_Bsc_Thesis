package com.zlrx.thesis.authservice.controller.validation

import com.zlrx.thesis.authservice.controller.model.PasswordChangeRequest
import com.zlrx.thesis.authservice.controller.model.RegisterRequest
import com.zlrx.thesis.authservice.controller.model.UserRequest
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.passay.LengthRule
import org.passay.PasswordData
import org.passay.PasswordValidator
import kotlin.reflect.KClass

@Constraint(validatedBy = [PasswordConstraintValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPassword(
    val message: String = "{validation.invalid.password}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Constraint(
    validatedBy = [
        PasswordMatchValidator::class,
        PasswordChangeMatchValidator::class,
        UserPasswordChangeMatchValidator::class
    ]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PasswordMatch(
    val message: String = "{validation.password.not.match}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Constraint(validatedBy = [PasswordOrNullConstraintValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidOrNullPassword(
    val message: String = "{validation.invalid.password}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class PasswordOrNullConstraintValidator : ConstraintValidator<ValidOrNullPassword, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        val validator = PasswordValidator(
            listOf(
                LengthRule(5, 50),
            )
        )
        val result = validator.validate(PasswordData(value))
        return result.isValid
    }
}

class PasswordConstraintValidator : ConstraintValidator<ValidPassword, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        val validator = PasswordValidator(
            listOf(
                LengthRule(8, 50),
            )
        )
        val result = validator.validate(PasswordData(value))
        return result.isValid
    }
}

class PasswordMatchValidator : ConstraintValidator<PasswordMatch, RegisterRequest> {
    override fun isValid(value: RegisterRequest?, context: ConstraintValidatorContext?): Boolean {
        return value != null && value.password == value.confirmPassword
    }
}

class PasswordChangeMatchValidator : ConstraintValidator<PasswordMatch, UserRequest> {
    override fun isValid(value: UserRequest?, context: ConstraintValidatorContext?): Boolean {
        return value != null && value.password == value.confirmPassword
    }
}

class UserPasswordChangeMatchValidator : ConstraintValidator<PasswordMatch, PasswordChangeRequest> {
    override fun isValid(value: PasswordChangeRequest?, context: ConstraintValidatorContext?): Boolean {
        return value != null && value.password == value.confirmPassword
    }
}
