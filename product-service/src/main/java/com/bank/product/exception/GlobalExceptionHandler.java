package com.bank.product.exception;
import com.bank.product.dto.ApiResponse; import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.stream.Collectors;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ApiResponse<Void>> notFound(RuntimeException e){return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(BadRequestException.class) ResponseEntity<ApiResponse<Void>> bad(RuntimeException e){return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(ConflictException.class) ResponseEntity<ApiResponse<Void>> conflict(RuntimeException e){return ResponseEntity.status(409).body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining(", "));return ResponseEntity.badRequest().body(ApiResponse.error(m));}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<ApiResponse<Void>> forbidden(AccessDeniedException e){return ResponseEntity.status(403).body(ApiResponse.error("Insufficient role"));}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiResponse<Void>> generic(Exception e){return ResponseEntity.status(500).body(ApiResponse.error("Unexpected server error"));}
}
