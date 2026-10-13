package com.bank.product.service;
import com.bank.product.dto.ProductDtos.CategoryRequest; import com.bank.product.exception.ConflictException; import com.bank.product.model.CustomerCategory; import com.bank.product.repository.CustomerCategoryRepository; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @RequiredArgsConstructor public class CategoryService {private final CustomerCategoryRepository repo;
 @Transactional public CustomerCategory create(CategoryRequest r,String user){String code=r.categoryCode().toUpperCase();if(repo.existsById(code))throw new ConflictException("Category already exists");CustomerCategory c=new CustomerCategory();c.setCategoryCode(code);c.setCategoryName(r.categoryName());c.setDescription(r.description());c.setAuditUserId(user);return repo.save(c);}
 @Transactional(readOnly=true) public List<CustomerCategory> all(){return repo.findAll();}
}
