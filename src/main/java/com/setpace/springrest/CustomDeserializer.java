package com.setpace.springrest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.setpace.validation.OnlyForValidation;

public class CustomDeserializer extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember annotatedMember) {
        return annotatedMember.hasAnnotation(OnlyForValidation.class) || annotatedMember.hasAnnotation(JsonIgnore.class);
    }
}
