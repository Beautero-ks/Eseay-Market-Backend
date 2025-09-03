package com.ninehub.dreamshops.response;

import lombok.AllArgsConstructor;
import lombok.Data;

//This class or package is to return our backend data to the frontend
@AllArgsConstructor
@Data
public class ApiResponse {
    private String message;
    private Object data;
}
