package com.setpace.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used if you have additional data you need to set in ARequest in the class ResponseValidator
 * For example if you need a Boolean (which is not part of the json body) to build up the expected response in the
 * method hasExpectedDataBasedOn() in the class ResponseValidator.
 *
 * If added on a field, that field will be ignored by Jackson (serialization and deserialization) and it will also
 * be ignored in the validation class AssertJExtension
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyForValidation {
}
