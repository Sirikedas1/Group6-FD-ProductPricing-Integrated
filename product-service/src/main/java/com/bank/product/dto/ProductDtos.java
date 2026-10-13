package com.bank.product.dto;

import com.bank.product.model.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class ProductDtos {
 private ProductDtos(){}
 public record ProductRequest(
  @NotBlank @Pattern(regexp="[A-Z0-9]{2,20}") String productCode,@NotBlank String productId,@NotBlank String productName,@NotBlank String marketName,
  @NotBlank String productLine,@NotBlank String productType,String productSubtype,String productGroup,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency,
  @NotNull @Positive Integer minTermMonths,@NotNull @Positive Integer maxTermMonths,@NotNull @DecimalMin("0") BigDecimal minRate,@NotNull @DecimalMin("0") BigDecimal maxRate,
  @NotNull LocalDate effectiveDate,LocalDate expiryDate,ProductStatus status,String approvalStatus,
  @Valid List<RoleRequest> roles,@Valid List<BalanceTypeRequest> balanceTypes,@Valid List<TransactionTypeRequest> transactionTypes,
  @Valid List<FeeRateRequest> feeRates,@Valid List<TermRuleRequest> termRules,@Valid List<BusinessRuleRequest> businessRules,@Valid List<CommunicationRequest> communications){}
 public record RoleRequest(String roleId,@NotBlank String roleType,boolean mandatory){}
 public record BalanceTypeRequest(String balanceTypeId,@NotBlank String balanceCode,@NotBlank String classification){}
 public record TransactionTypeRequest(String transactionTypeId,@NotBlank String transactionCode,@NotBlank String transactionName,@Pattern(regexp="[DC]") String debitCreditIndicator){}
 public record FeeRateRequest(String feeRateId,@NotBlank String categoryCode,@NotBlank String feeType,@NotBlank String feeName,BigDecimal feeAmount,BigDecimal feePercent,String triggerEvent,BigDecimal additionalRatePercent,BigDecimal penaltyPercent,@NotNull LocalDate effectiveDate,LocalDate expiryDate){}
 public record TermRuleRequest(String termRuleId,@Pattern(regexp="DAYS|WEEKS|MONTHS") String durationUnit,@Positive int minValue,@Positive int maxValue,@Positive int defaultValue){}
 public record BusinessRuleRequest(String ruleId,@NotBlank String ruleCode,@NotBlank String ruleValue,boolean enabled,String description){}
 public record CommunicationRequest(String communicationId,@NotBlank String categoryCode,@NotBlank String communicationType,@NotBlank String channel,@NotBlank String frequency,@NotBlank String templateName,boolean active){}
 public record CategoryRequest(@NotBlank @Pattern(regexp="[A-Z0-9_]{2,20}") String categoryCode,@NotBlank String categoryName,String description){}
 public record ProductSummary(String productCode,String productName,String marketName,String productType,String currency,Integer minTermMonths,Integer maxTermMonths,BigDecimal minRate,BigDecimal maxRate,LocalDate effectiveDate,ProductStatus status){}
 public record ProductResponse(String productCode,String productId,String productName,String marketName,String productLine,String productType,String productSubtype,String productGroup,String currency,Integer minTermMonths,Integer maxTermMonths,BigDecimal minRate,BigDecimal maxRate,LocalDate effectiveDate,LocalDate expiryDate,ProductStatus status,String approvalStatus,List<RoleResponse> roles,List<BalanceTypeResponse> balanceTypes,List<TransactionTypeResponse> transactionTypes,List<FeeRateResponse> feeRates,List<TermRuleResponse> termRules,List<BusinessRuleResponse> businessRules,List<CommunicationResponse> communications){}
 public record RoleResponse(String roleId,String roleType,boolean mandatory){} public record BalanceTypeResponse(String balanceTypeId,String balanceCode,String classification){}
 public record TransactionTypeResponse(String transactionTypeId,String transactionCode,String transactionName,String debitCreditIndicator){}
 public record FeeRateResponse(String feeRateId,String categoryCode,String feeType,String feeName,BigDecimal feeAmount,BigDecimal feePercent,String triggerEvent,BigDecimal additionalRatePercent,BigDecimal penaltyPercent,LocalDate effectiveDate,LocalDate expiryDate){}
 public record TermRuleResponse(String termRuleId,String durationUnit,int minValue,int maxValue,int defaultValue){} public record BusinessRuleResponse(String ruleId,String ruleCode,String ruleValue,boolean enabled,String description){}
 public record CommunicationResponse(String communicationId,String categoryCode,String communicationType,String channel,String frequency,String templateName,boolean active){}
 public record PricingResponse(String productCode,String categoryCode,BigDecimal baseMinRate,BigDecimal baseMaxRate,BigDecimal additionalRatePercent,BigDecimal penaltyPercent,List<FeeRateResponse> entries){}
}
