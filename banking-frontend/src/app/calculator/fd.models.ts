/**
 * TypeScript "shapes" of the data exchanged with calculator-service.
 * They mirror the Java classes in calculator-service/src/main/java/com/banklab/calculator/dto.
 */
export type TermUnit = 'DAYS' | 'MONTHS' | 'YEARS';
export type InterestType = 'SIMPLE' | 'COMPOUND';
export type CompoundingFrequency = 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'ANNUALLY';

export interface Term {
  value: number;
  unit: TermUnit;
}

/** Sent to POST /api/fd/calculate (Lab L6 payload + optional extras). */
export interface FdCalculationRequest {
  principal: number;
  term: Term;
  baseRate: number | null;
  categories: string[];
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency | null;
  productCode: string | null;
}

/** Received from POST /api/fd/calculate. */
export interface FdCalculationResponse {
  principal: number;
  termValue: number;
  termUnit: TermUnit;
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency | null;
  calculationMethod: string;
  productCode: string | null;
  baseRate: number;
  categories: string[];
  categoryAddOns: Record<string, number>;
  requestedAddOn: number;
  appliedAddOn: number;
  addOnCap: number;
  capApplied: boolean;
  maxEffectiveRate: number | null;
  maxRateCapApplied: boolean;
  effectiveRate: number;
  interestEarned: number;
  maturityAmount: number;
  apy: number | null;
  startDate: string;
  maturityDate: string;
  currencyCode: string;
  rateSource: string;
}

export interface CategoryInfo {
  code: string;
  name: string;
  additionalRatePercent: number | null;
}

/** Received from GET /api/fd/categories. */
export interface CategoryListResponse {
  rateSource: string;
  categorySource: string;
  addOnCap: number;
  categories: CategoryInfo[];
}

export interface ProductInfo {
  code: string;
  name: string;
  minRate: number | null;
  maxRate: number | null;
  minAmount: number | null;
  maxAmount: number | null;
  defaultCompoundingFrequency: string | null;
}

export interface ReportResult {
  fileName: string;
  csvRow: string;
  rowsInFile: number;
}

export interface FdReportResponse {
  calculation: FdCalculationResponse;
  report: ReportResult;
}

/** Every error from the backend has this shape. */
export interface ApiError {
  code: string;
  message: string;
  details: string[];
}
