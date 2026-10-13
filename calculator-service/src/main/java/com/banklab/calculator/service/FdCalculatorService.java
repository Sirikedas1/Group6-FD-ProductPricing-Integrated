package com.banklab.calculator.service;

import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.Term;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.banklab.calculator.model.CompoundingFrequency;
import com.banklab.calculator.model.InterestType;
import com.banklab.calculator.model.TermUnit;
import com.banklab.calculator.rates.RateInputs;
import com.banklab.calculator.rates.RateProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The heart of the module: validates the input, works out the effective rate
 * (base + category add-ons, capped) and applies the Simple or Compound formula.
 *
 * All money maths uses BigDecimal (exact decimal numbers), never double,
 * so results are correct to the paisa.
 */
@Service
public class FdCalculatorService {

    /** Smallest deposit accepted (usual Indian bank minimum). */
    public static final BigDecimal MIN_PRINCIPAL = new BigDecimal("1000.00");
    /** Largest deposit accepted: 10 crore rupees. */
    public static final BigDecimal MAX_PRINCIPAL = new BigDecimal("100000000.00");
    /** Largest base rate accepted, in percent. */
    public static final BigDecimal MAX_BASE_RATE = new BigDecimal("15.00");

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    /** 34 significant digits for all intermediate steps. */
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final ZoneId INDIA = ZoneId.of("Asia/Kolkata");
    private static final String RUPEE = "₹";

    private final RateProvider rateProvider;

    public FdCalculatorService(RateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    public FdCalculationResponse calculate(FdCalculationRequest request) {
        if (request == null) {
            throw CalculationException.badRequest("VALIDATION_ERROR", "Request body is required.");
        }
        List<String> errors = new ArrayList<>();

        // ---------- 1. Deposit amount ----------
        BigDecimal principal = request.getPrincipal();
        if (principal == null) {
            errors.add("Deposit amount (principal) is required.");
        } else if (principal.signum() <= 0) {
            errors.add("Deposit amount must be greater than 0.");
        } else if (principal.compareTo(MIN_PRINCIPAL) < 0) {
            errors.add("Minimum deposit amount is " + RUPEE + "1,000.");
        } else if (principal.compareTo(MAX_PRINCIPAL) > 0) {
            errors.add("Maximum deposit amount is " + RUPEE + "10,00,00,000 (10 crore).");
        } else if (principal.stripTrailingZeros().scale() > 2) {
            errors.add("Deposit amount can have at most 2 decimal places (paise).");
        }

        // ---------- 2. Term ----------
        Term term = request.getTerm();
        TermUnit unit = null;
        int termValue = 0;
        if (term == null || term.getValue() == null) {
            errors.add("Term value is required.");
        } else {
            termValue = term.getValue();
            unit = TermUnit.parse(term.getUnit());
            if (unit == null) {
                errors.add("Term unit must be DAYS, MONTHS or YEARS.");
            } else if (termValue < unit.getMin() || termValue > unit.getMax()) {
                errors.add("For " + unit.name() + ", the term must be between "
                        + unit.getMin() + " and " + unit.getMax() + ".");
            }
        }

        // ---------- 3. Interest type and compounding ----------
        InterestType interestType = InterestType.parse(request.getInterestType());
        if (interestType == null) {
            errors.add("Interest type must be SIMPLE or COMPOUND.");
        }
        CompoundingFrequency frequency = null;
        if (interestType == InterestType.COMPOUND) {
            frequency = CompoundingFrequency.parse(request.getCompoundingFrequency());
            if (frequency == null) {
                errors.add("Compounding frequency must be MONTHLY, QUARTERLY, HALF_YEARLY or ANNUALLY.");
            }
        }

        // ---------- 4. Base rate (only when the user types it) ----------
        if (rateProvider.usesRequestBaseRate()) {
            checkBaseRate(request.getBaseRate(), errors);
        }

        if (!errors.isEmpty()) {
            throw CalculationException.validation(errors);
        }

        // ---------- 5. Rates: base + category add-ons ----------
        List<String> categories = CategoryCodes.normalizeAll(request.getCategories());
        RateInputs rates = rateProvider.getRateInputs(
                request.getProductCode(), request.getBaseRate(), categories, principal,
                unit.toWholeMonths(termValue));

        BigDecimal baseRate = rates.baseRate();
        List<String> rateErrors = new ArrayList<>();
        checkBaseRate(baseRate, rateErrors);
        if (!rateErrors.isEmpty()) {
            throw CalculationException.validation(rateErrors);
        }

        // Sum ALL selected add-ons, then cap ONCE (Lab L6 / L11)
        BigDecimal requestedAddOn = BigDecimal.ZERO;
        for (BigDecimal addOn : rates.addOns().values()) {
            requestedAddOn = requestedAddOn.add(addOn);
        }
        BigDecimal cap = rates.addOnCap();
        boolean capApplied = requestedAddOn.compareTo(cap) > 0;
        BigDecimal appliedAddOn = capApplied ? cap : requestedAddOn;
        BigDecimal effectiveRate = baseRate.add(appliedAddOn);

        // Product-defined maximum rate (Lab L11), only when a product supplies one
        boolean maxRateCapApplied = false;
        if (rates.maxEffectiveRate() != null && effectiveRate.compareTo(rates.maxEffectiveRate()) > 0) {
            effectiveRate = rates.maxEffectiveRate();
            maxRateCapApplied = true;
        }

        // ---------- 6. The formula ----------
        BigDecimal maturity;
        BigDecimal apy = null;
        String method;
        if (interestType == InterestType.SIMPLE) {
            maturity = simpleMaturity(principal, effectiveRate, termValue, unit);
            method = "SIMPLE";
        } else {
            maturity = compoundMaturity(principal, effectiveRate, termValue, unit, frequency);
            apy = annualYield(effectiveRate, frequency);
            method = "COMPOUND_" + frequency.name();
        }
        BigDecimal principal2 = principal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal maturity2 = maturity.setScale(2, RoundingMode.HALF_UP);
        BigDecimal interest2 = maturity2.subtract(principal2);

        // ---------- 7. Dates ----------
        LocalDate startDate = LocalDate.now(INDIA);
        LocalDate maturityDate = switch (unit) {
            case DAYS -> startDate.plusDays(termValue);
            case MONTHS -> startDate.plusMonths(termValue);
            case YEARS -> startDate.plusYears(termValue);
        };

        Map<String, BigDecimal> addOns2 = new LinkedHashMap<>();
        rates.addOns().forEach((code, value) -> addOns2.put(code, scale2(value)));

        return new FdCalculationResponse(
                principal2,
                termValue,
                unit.name(),
                interestType.name(),
                frequency == null ? null : frequency.name(),
                method,
                request.getProductCode(),
                scale2(baseRate),
                new ArrayList<>(addOns2.keySet()),
                addOns2,
                scale2(requestedAddOn),
                scale2(appliedAddOn),
                scale2(cap),
                capApplied,
                rates.maxEffectiveRate() == null ? null : scale2(rates.maxEffectiveRate()),
                maxRateCapApplied,
                scale2(effectiveRate),
                interest2,
                maturity2,
                apy,
                startDate,
                maturityDate,
                "INR",
                rates.source());
    }

    private static void checkBaseRate(BigDecimal baseRate, List<String> errors) {
        if (baseRate == null) {
            errors.add("Base rate is required.");
        } else if (baseRate.signum() <= 0) {
            errors.add("Base rate must be greater than 0.");
        } else if (baseRate.compareTo(MAX_BASE_RATE) > 0) {
            errors.add("Base rate cannot be more than 15% per year.");
        } else if (baseRate.stripTrailingZeros().scale() > 2) {
            errors.add("Base rate can have at most 2 decimal places.");
        }
    }

    /**
     * SIMPLE INTEREST (Lab L6): M = P + (P x R x t / 100)
     * t in years = termValue / unitsPerYear  (e.g. 18 months = 18/12 = 1.5 years)
     */
    public static BigDecimal simpleMaturity(BigDecimal principal, BigDecimal ratePercent,
                                            int termValue, TermUnit unit) {
        BigDecimal interest = principal
                .multiply(ratePercent, MC)
                .multiply(BigDecimal.valueOf(termValue), MC)
                .divide(HUNDRED.multiply(BigDecimal.valueOf(unit.getUnitsPerYear())), MC);
        return principal.add(interest, MC);
    }

    /**
     * COMPOUND INTEREST (Lab L6): M = P x (1 + r/n)^(n x t), with r = rate / 100.
     *
     * Total periods = n x t. If that is not a whole number (e.g. 5 months with
     * quarterly compounding = 1 full quarter + 2/3 of a quarter), the full
     * periods are compounded and the leftover part-period earns simple interest
     * on the grown amount (usual Indian bank practice).
     */
    public static BigDecimal compoundMaturity(BigDecimal principal, BigDecimal ratePercent,
                                              int termValue, TermUnit unit,
                                              CompoundingFrequency frequency) {
        int n = frequency.getPeriodsPerYear();
        BigDecimal periodRate = ratePercent.divide(HUNDRED.multiply(BigDecimal.valueOf(n)), MC);

        long numerator = (long) termValue * n;       // periods = termValue x n / unitsPerYear
        int denominator = unit.getUnitsPerYear();
        int wholePeriods = (int) (numerator / denominator);
        BigDecimal leftoverFraction = BigDecimal.valueOf(numerator % denominator)
                .divide(BigDecimal.valueOf(denominator), MC);

        BigDecimal growth = BigDecimal.ONE.add(periodRate).pow(wholePeriods, MC);
        BigDecimal leftoverGrowth = BigDecimal.ONE.add(periodRate.multiply(leftoverFraction, MC));
        return principal.multiply(growth, MC).multiply(leftoverGrowth, MC);
    }

    /**
     * Annual Percentage Yield (your WBS task B4.1): APY = (1 + r/n)^n - 1, shown in percent.
     * It tells the customer what the compound rate is "really worth" per year.
     */
    public static BigDecimal annualYield(BigDecimal ratePercent, CompoundingFrequency frequency) {
        int n = frequency.getPeriodsPerYear();
        BigDecimal periodRate = ratePercent.divide(HUNDRED.multiply(BigDecimal.valueOf(n)), MC);
        return BigDecimal.ONE.add(periodRate).pow(n, MC)
                .subtract(BigDecimal.ONE)
                .multiply(HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal scale2(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
