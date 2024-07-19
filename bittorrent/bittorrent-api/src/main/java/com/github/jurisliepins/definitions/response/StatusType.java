package com.github.jurisliepins.definitions.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusType {
    @JsonProperty("Success")
    SUCCESS,

    @JsonProperty("Failure")
    FAILURE
}
