package com.ninehub.dreamshops.execptions;

public class ProductNotFoundExecption extends RuntimeException {
    public ProductNotFoundExecption (String message) {
        super(message);
    }
}
