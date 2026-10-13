package com.bank.product.service;
import com.bank.product.dto.ProductDtos.*; import com.bank.product.exception.BadRequestException; import com.bank.product.repository.*; import org.junit.jupiter.api.*; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*; import org.mockito.junit.jupiter.MockitoExtension; import java.math.BigDecimal; import java.time.LocalDate; import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class) class ProductServiceTest {
 @Mock ProductRepository products; @Mock CustomerCategoryRepository categories; ProductService service;
 @BeforeEach void setup(){service=new ProductService(products,categories,new ProductMapper());}
 @Test void rejectsInvertedTermRange(){ProductRequest r=request(60,6,new BigDecimal("5"),new BigDecimal("7"));assertThrows(BadRequestException.class,()->service.create(r,"admin"));}
 @Test void rejectsInvertedRateRange(){ProductRequest r=request(6,60,new BigDecimal("8"),new BigDecimal("7"));assertThrows(BadRequestException.class,()->service.create(r,"admin"));}
 private ProductRequest request(int minTerm,int maxTerm,BigDecimal minRate,BigDecimal maxRate){return new ProductRequest("FD001","1","FD","FD", "TERM_DEPOSIT","FD",null,"LIABILITY","INR",minTerm,maxTerm,minRate,maxRate,LocalDate.now(),null,null,null,null,null,null,null,null,null,null);}
}
