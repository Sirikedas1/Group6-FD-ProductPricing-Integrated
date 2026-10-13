package com.bank.product.dto;
import java.time.OffsetDateTime;
public record ApiResponse<T>(String status,T data,String message,OffsetDateTime timestamp){
 public static <T> ApiResponse<T> success(T data,String message){return new ApiResponse<>("success",data,message,OffsetDateTime.now());}
 public static ApiResponse<Void> error(String message){return new ApiResponse<>("error",null,message,OffsetDateTime.now());}
}
