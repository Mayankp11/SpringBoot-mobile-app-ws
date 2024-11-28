package com.techsorcerer.mobile_app_ws.ui.response;

public enum ErrorMessages {
    MISSING_REQUIRED_FIELDS("Missing required fields. Please check the documentation."),
    RECORD_ALREADY_EXISTS("Record already exists."),
    INTERNAL_SERVER_ERROR("Internal server error. Please try again later."),
    NO_RECORD_FOUND("No record found with the provided details."),
    EMAIL_ID_IS_MISSING("Email id is missing"),
    COULD_NOT_UPDATE_RECORD("Could nit update record"),
    COULD_NOT_DELETE_RECORD("Could nit delete record"),
    AUTHENTICATION_FAILED("Authentication failed. Invalid credentials."),
    ACCESS_DENIED("Access denied. You do not have the required permissions."),
    INVALID_INPUT("Invalid input. Please verify the entered data."),
    OPERATION_NOT_ALLOWED("This operation is not allowed.");

    // Private field to hold the error message
    private final String errorMessage;

    // Constructor to initialize the field
    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

	public String getErrorMessage() {
		return errorMessage;
	}

    
}
