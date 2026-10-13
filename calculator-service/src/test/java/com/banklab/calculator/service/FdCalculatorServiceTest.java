package com.banklab.calculator.service;

import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.Term;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.rates.LocalRateProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Lab L11 "FD Calculator Testing": every expected value below was worked out by hand
 * (see the Sample Calculations table in the guide). If the formulas ever change by
 * even one paisa, these tests fail.
 */
class FdCalculatorServiceTest {

    private final FdCalculatorService service =
            new FdCalculatorService(new LocalRateProvider(new BigDecimal("2.00")));

    private static FdCalculationRequest request(String principal, int termValue, String termUnit,
                                                String baseRate, String interestType,
                                                String frequency, String... categories) {
        FdCalculationRequest r = new FdCalculationRequest();
        r.setPrincipal(principal == null ? null : new BigDecimal(principal));
        r.setTerm(new Term(termValue, termUnit));
        r.setBaseRate(baseRate == null ? null : new BigDecimal(baseRate));
        r.setInterestType(interestType);
        r.setCompoundingFrequency(frequency);
        r.setCategories(Arrays.asList(categories));
        return r;
    }

    // ---------- The 7 sample calculations from the guide ----------

    @Test
    void sample1_simple_seniorPlusEmployee_matchesManualL11CsvRow() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "SENIOR", "EMPLOYEE"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.50");
        assertThat(r.interestEarned()).isEqualByComparingTo("8500.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
        assertThat(r.capApplied()).isFalse();
    }

    @Test
    void sample2_simple_employeePlusPremium_isCappedAtTwoPercent() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "EMPLOYEE", "PREMIUM_USER"));
        assertThat(r.requestedAddOn()).isEqualByComparingTo("3.00");
        assertThat(r.appliedAddOn()).isEqualByComparingTo("2.00");
        assertThat(r.capApplied()).isTrue();
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.50");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
    }

    @Test
    void sample3_compoundQuarterly_noCategory() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "COMPOUND", "QUARTERLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("6660.16");
        assertThat(r.maturityAmount()).isEqualByComparingTo("106660.16");
        assertThat(r.apy()).isEqualByComparingTo("6.66");
    }

    @Test
    void sample4_compoundMonthly_senior() {
        FdCalculationResponse r = service.calculate(
                request("50000", 24, "MONTHS", "7.0", "COMPOUND", "MONTHLY", "SENIOR"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.00");
        assertThat(r.interestEarned()).isEqualByComparingTo("8644.40");
        assertThat(r.maturityAmount()).isEqualByComparingTo("58644.40");
    }

    @Test
    void sample5_oneMonthMonthly_matchesManualL14Example() {
        FdCalculationResponse r = service.calculate(
                request("100000", 1, "MONTHS", "6.6", "COMPOUND", "MONTHLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("550.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("100550.00");
    }

    @Test
    void sample6_simple_eighteenMonths() {
        FdCalculationResponse r = service.calculate(
                request("25000", 18, "MONTHS", "7.25", "SIMPLE", null));
        assertThat(r.interestEarned()).isEqualByComparingTo("2718.75");
        assertThat(r.maturityAmount()).isEqualByComparingTo("27718.75");
    }

    @Test
    void sample7_compoundQuarterly_leftoverPartPeriod() {
        FdCalculationResponse r = service.calculate(
                request("100000", 5, "MONTHS", "7.0", "COMPOUND", "QUARTERLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("2937.08");
        assertThat(r.maturityAmount()).isEqualByComparingTo("102937.08");
    }

    // ---------- Lab L11 scenario table ----------

    @ParameterizedTest(name = "L11: {0} + {1} on base {2}% -> {4}% (capped={5})")
    @CsvSource({
            "EMPLOYEE, SENIOR,       6.5, 2.0, 8.5, false",
            "EMPLOYEE, PREMIUM_USER, 6.5, 3.0, 8.5, true"
    })
    void labL11ScenarioTable(String cat1, String cat2, String base, String addOnBeforeCap,
                             String finalRate, boolean capped) {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", base, "SIMPLE", null, cat1, cat2));
        assertThat(r.requestedAddOn()).isEqualByComparingTo(addOnBeforeCap);
        assertThat(r.effectiveRate()).isEqualByComparingTo(finalRate);
        assertThat(r.capApplied()).isEqualTo(capped);
    }

    // ---------- Category names written the Lab L6 way ----------

    @Test
    void categoryNamesFromLabL6PayloadAreUnderstood() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "months", "6.5", null, null, "Senior Citizen", "Employee"));
        assertThat(r.categories()).containsExactly("SENIOR", "EMPLOYEE");
        assertThat(r.interestType()).isEqualTo("SIMPLE");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
    }

    // ---------- Categories coming from the Customer module (Step 16) ----------

    @Test
    void superSeniorCategoryIsSupported() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "SUPER_SENIOR"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108000.00");
    }

    @ParameterizedTest(name = "customer-module classification {0} maps to {1}")
    @CsvSource({
            "REGULAR,        GENERAL",
            "PREMIUM,        PREMIUM_USER",
            "SENIOR_CITIZEN, SENIOR",
            "SUPER_SENIOR,   SUPER_SENIOR"
    })
    void classificationValuesFromTheCustomerModuleAreUnderstood(String incoming, String expectedCode) {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, incoming));
        assertThat(r.categories()).containsExactly(expectedCode);
    }

    // ---------- Invalid input must be rejected ----------

    @Test
    void rejectsTooSmallAmount() {
        assertThatThrownBy(() -> service.calculate(request("500", 12, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("Minimum deposit")));
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> service.calculate(request("-5000", 12, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("greater than 0")));
    }

    @Test
    void rejectsMissingBaseRate() {
        assertThatThrownBy(() -> service.calculate(request("100000", 12, "MONTHS", null, "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .contains("Base rate is required."));
    }

    @Test
    void rejectsTermOutOfRange() {
        assertThatThrownBy(() -> service.calculate(request("100000", 200, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("between 1 and 120")));
    }

    @Test
    void rejectsUnknownCategory() {
        assertThatThrownBy(() -> service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "ALIEN")))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("Unknown customer category: ALIEN")));
    }

    @Test
    void reportsSeveralProblemsAtOnce() {
        FdCalculationRequest r = request(null, 0, "WEEKS", "40", "FANCY", null);
        assertThatThrownBy(() -> service.calculate(r))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails()).hasSizeGreaterThanOrEqualTo(4));
    }

    @Test
    void emptyCategoryListMeansNoAddOn() {
        FdCalculationResponse r = service.calculate(
                request("100000", 1, "YEARS", "6.5", "SIMPLE", null));
        assertThat(r.appliedAddOn()).isEqualByComparingTo("0.00");
        assertThat(r.categories()).isEqualTo(List.of());
        assertThat(r.maturityAmount()).isEqualByComparingTo("106500.00");
    }
}
