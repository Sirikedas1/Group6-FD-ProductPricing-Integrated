package com.bank.product.service;

import com.bank.product.dto.ProductDtos.*; import com.bank.product.model.*;
import org.springframework.stereotype.Component; import java.util.*;

@Component
public class ProductMapper {
 public ProductResponse response(Product p){
  return new ProductResponse(p.getProductCode(),p.getProductId(),p.getProductName(),p.getMarketName(),p.getProductLine(),p.getProductType(),p.getProductSubtype(),p.getProductGroup(),p.getCurrency(),p.getMinTermMonths(),p.getMaxTermMonths(),p.getMinRate(),p.getMaxRate(),p.getEffectiveDate(),p.getExpiryDate(),p.getStatus(),p.getApprovalStatus(),
   p.getRoles().stream().map(x->new RoleResponse(x.getRoleId(),x.getRoleType(),x.isMandatory())).toList(),
   p.getBalanceTypes().stream().map(x->new BalanceTypeResponse(x.getBalanceTypeId(),x.getBalanceCode(),x.getClassification())).toList(),
   p.getTransactionTypes().stream().map(x->new TransactionTypeResponse(x.getTransactionTypeId(),x.getTransactionCode(),x.getTransactionName(),x.getDebitCreditIndicator())).toList(),
   p.getFeeRates().stream().map(this::fee).toList(),p.getTermRules().stream().map(x->new TermRuleResponse(x.getTermRuleId(),x.getDurationUnit(),x.getMinValue(),x.getMaxValue(),x.getDefaultValue())).toList(),
   p.getBusinessRules().stream().map(x->new BusinessRuleResponse(x.getRuleId(),x.getRuleCode(),x.getRuleValue(),x.isEnabled(),x.getDescription())).toList(),
   p.getCommunications().stream().map(x->new CommunicationResponse(x.getCommunicationId(),x.getCategory().getCategoryCode(),x.getCommunicationType(),x.getChannel(),x.getFrequency(),x.getTemplateName(),x.isActive())).toList());
 }
 public FeeRateResponse fee(ProductFeeRate x){return new FeeRateResponse(x.getFeeRateId(),x.getCategory()==null?null:x.getCategory().getCategoryCode(),x.getFeeType(),x.getFeeName(),x.getFeeAmount(),x.getFeePercent(),x.getTriggerEvent(),x.getAdditionalRatePercent(),x.getPenaltyPercent(),x.getEffectiveDate(),x.getExpiryDate());}
 public ProductSummary summary(Product p){return new ProductSummary(p.getProductCode(),p.getProductName(),p.getMarketName(),p.getProductType(),p.getCurrency(),p.getMinTermMonths(),p.getMaxTermMonths(),p.getMinRate(),p.getMaxRate(),p.getEffectiveDate(),p.getStatus());}
}
