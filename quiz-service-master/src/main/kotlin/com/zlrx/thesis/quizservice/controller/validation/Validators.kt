package com.zlrx.thesis.quizservice.controller.validation

import com.zlrx.thesis.quizservice.controller.model.EditQuestion
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [HasRightAnswerValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidAnswer(
    val message: String = "{validation.mandatory.answer}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class HasRightAnswerValidator : ConstraintValidator<ValidAnswer, EditQuestion> {
    override fun isValid(value: EditQuestion?, context: ConstraintValidatorContext?): Boolean {
        if (value != null) {
            return value.answers.count { it.accepted } == 1
        }
        return true
    }
}
