package com.appsdev.app.ws.appdevws.exceptions;


import com.appsdev.app.ws.appdevws.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;

//it will be used to handle exceptions
@ControllerAdvice
public class AppExceptionsHandler {

    @ExceptionHandler(value = {UserServiceException.class}) //if I want this class to handle more than Exceptions
    // I can add this in tha annotation and in the method e.g. NullPointerException.class
    public ResponseEntity<Object> handleUserServiceException(UserServiceException userServiceException,
                                                             WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(), userServiceException.getMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        //userServiceException.getMessage() send back only message string
    }

    @ExceptionHandler(value = {Exception.class}) //in this way i can handle diff exceptions i can add as many methods I want
    public ResponseEntity<Object> handleOtherException(Exception exception,
                                                             WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        //userServiceException.getMessage() send back only message string
    }

}
