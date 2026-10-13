import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FdCalculatorService, describeHttpError } from './fd-calculator.service';
import {
  CategoryInfo,
  CompoundingFrequency,
  FdCalculationRequest,
  FdCalculationResponse,
  InterestType,
  ProductInfo,
  TermUnit,
} from './fd.models';

/** Values typed into the form. */
interface FormModel {
  principal: number | null;
  termValue: number | null;
  termUnit: TermUnit;
  baseRate: number | null;
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency;
  productCode: string;
}

const TERM_LIMITS: Record<TermUnit, { min: number; max: number }> = {
  DAYS: { min: 7, max: 3650 },
  MONTHS: { min: 1, max: 120 },
  YEARS: { min: 1, max: 10 },
};

function defaultModel(): FormModel {
  return {
    principal: null,
    termValue: 12,
    termUnit: 'MONTHS',
    baseRate: 6.5,
    interestType: 'COMPOUND',
    compoundingFrequency: 'QUARTERLY',
    productCode: '',
  };
}

/**
 * The FD Calculator page (Lab L6 section 4 "UI Integration"):
 *  - category dropdown fetched from the backend's rate table
 *  - shows the additional % of every selected category
 *  - shows the cap being applied BEFORE the form is submitted
 */
@Component({
  selector: 'calc-fd-calculator',
  imports: [FormsModule, DatePipe],
  templateUrl: './fd-calculator.html',
  styleUrl: './fd-calculator.css',
})
export class FdCalculator implements OnInit {
  private readonly api = inject(FdCalculatorService);

  model: FormModel = defaultModel();

  // Data loaded from the backend
  readonly categories = signal<CategoryInfo[]>([]);
  readonly products = signal<ProductInfo[]>([]);
  readonly myCategories = signal<string[]>([]);
  readonly addOnCap = signal(2);
  readonly rateSource = signal('LOCAL');
  readonly categorySource = signal('REQUEST');

  // Page state
  readonly selected = signal<string[]>([]);
  readonly loadingSetup = signal(true);
  readonly setupErrors = signal<string[]>([]);
  readonly calculating = signal(false);
  readonly errors = signal<string[]>([]);
  readonly result = signal<FdCalculationResponse | null>(null);
  readonly reportBusy = signal(false);
  readonly reportMessage = signal<string | null>(null);
  readonly reportError = signal<string | null>(null);

  /** Categories that will be used: ticked ones, or the customer's own in PROFILE mode. */
  readonly activeCategories = computed(() =>
    this.categorySource() === 'PROFILE' ? this.myCategories() : this.selected(),
  );

  /** Sum of the add-ons of the active categories, before the cap. */
  readonly requestedAddOn = computed(() =>
    this.activeCategories().reduce((sum, code) => sum + (this.addOnOf(code) ?? 0), 0),
  );

  readonly capExceeded = computed(() => this.requestedAddOn() > this.addOnCap() + 1e-9);

  readonly appliedAddOn = computed(() => Math.min(this.requestedAddOn(), this.addOnCap()));

  ngOnInit(): void {
    this.loadSetup();
  }

  /** Loads categories (and products / my categories when those modes are switched on). */
  loadSetup(): void {
    this.loadingSetup.set(true);
    this.setupErrors.set([]);
    this.api.getCategories().subscribe({
      next: (res) => {
        this.categories.set(res.categories);
        this.addOnCap.set(Number(res.addOnCap));
        this.rateSource.set(res.rateSource);
        this.categorySource.set(res.categorySource);
        if (res.rateSource === 'PRODUCT') {
          this.loadProducts();
        }
        if (res.categorySource === 'PROFILE') {
          this.loadMyCategories();
        }
        this.loadingSetup.set(false);
      },
      error: (err) => {
        this.setupErrors.set(describeHttpError(err));
        this.loadingSetup.set(false);
      },
    });
  }

  private loadProducts(): void {
    this.api.getProducts().subscribe({
      next: (list) => this.products.set(list),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  private loadMyCategories(): void {
    this.api.getMyCategories().subscribe({
      next: (list) => this.myCategories.set(list),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  /** When a product is chosen, reload categories so the add-ons of that product are shown. */
  onProductChange(): void {
    this.result.set(null);
    this.api.getCategories(this.model.productCode || null).subscribe({
      next: (res) => this.categories.set(res.categories),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  // ---------- Category helpers ----------

  addOnOf(code: string): number | null {
    const info = this.categories().find((c) => c.code === code);
    return info && info.additionalRatePercent !== null ? Number(info.additionalRatePercent) : null;
  }

  nameOf(code: string): string {
    return this.categories().find((c) => c.code === code)?.name ?? code;
  }

  isSelected(code: string): boolean {
    return this.selected().includes(code);
  }

  toggleCategory(code: string): void {
    this.selected.update((list) =>
      list.includes(code) ? list.filter((c) => c !== code) : [...list, code],
    );
  }

  selectionSummary(): string {
    const list = this.selected();
    if (list.length === 0) {
      return 'No category selected (General customer)';
    }
    return list.map((c) => this.nameOf(c)).join(', ');
  }

  addOnLabel(value: number | null): string {
    return value === null ? 'depends on product' : '+' + Number(value).toFixed(2) + '%';
  }

  // ---------- Preview (before submitting) ----------

  previewRate(): number | null {
    if (this.rateSource() !== 'LOCAL' || this.model.baseRate === null) {
      return null;
    }
    return Number(this.model.baseRate) + this.appliedAddOn();
  }

  termHint(): string {
    const limits = TERM_LIMITS[this.model.termUnit];
    return `Allowed: ${limits.min} to ${limits.max} ${this.model.termUnit.toLowerCase()}.`;
  }

  // ---------- Validation (same rules as the backend) ----------

  private validate(): string[] {
    const e: string[] = [];
    const m = this.model;

    if (m.principal === null || m.principal === undefined) {
      e.push('Please enter the deposit amount.');
    } else if (m.principal <= 0) {
      e.push('Deposit amount must be greater than 0.');
    } else if (m.principal < 1000) {
      e.push('Minimum deposit amount is ₹1,000.');
    } else if (m.principal > 100000000) {
      e.push('Maximum deposit amount is ₹10,00,00,000 (10 crore).');
    } else if (!/^\d+(\.\d{1,2})?$/.test(String(m.principal))) {
      e.push('Deposit amount can have at most 2 decimal places (paise).');
    }

    const limits = TERM_LIMITS[m.termUnit];
    if (m.termValue === null || m.termValue === undefined) {
      e.push('Please enter the term.');
    } else if (!Number.isInteger(m.termValue)) {
      e.push('Term must be a whole number.');
    } else if (m.termValue < limits.min || m.termValue > limits.max) {
      e.push(`For ${m.termUnit}, the term must be between ${limits.min} and ${limits.max}.`);
    }

    if (this.rateSource() === 'LOCAL') {
      if (m.baseRate === null || m.baseRate === undefined) {
        e.push('Please enter the base rate.');
      } else if (m.baseRate <= 0) {
        e.push('Base rate must be greater than 0.');
      } else if (m.baseRate > 15) {
        e.push('Base rate cannot be more than 15% per year.');
      } else if (!/^\d+(\.\d{1,2})?$/.test(String(m.baseRate))) {
        e.push('Base rate can have at most 2 decimal places.');
      }
    } else if (!m.productCode) {
      e.push('Please choose an FD product.');
    }
    return e;
  }

  private buildRequest(): FdCalculationRequest {
    const m = this.model;
    return {
      principal: Number(m.principal),
      term: { value: Number(m.termValue), unit: m.termUnit },
      baseRate: this.rateSource() === 'LOCAL' ? Number(m.baseRate) : null,
      categories: this.categorySource() === 'PROFILE' ? [] : this.selected(),
      interestType: m.interestType,
      compoundingFrequency: m.interestType === 'COMPOUND' ? m.compoundingFrequency : null,
      productCode: this.rateSource() === 'PRODUCT' ? m.productCode : null,
    };
  }

  // ---------- Buttons ----------

  calculate(): void {
    this.reportMessage.set(null);
    this.reportError.set(null);
    const problems = this.validate();
    this.errors.set(problems);
    if (problems.length > 0) {
      this.result.set(null);
      return;
    }
    this.calculating.set(true);
    this.api.calculate(this.buildRequest()).subscribe({
      next: (res) => {
        this.result.set(res);
        this.calculating.set(false);
      },
      error: (err) => {
        this.result.set(null);
        this.errors.set(describeHttpError(err));
        this.calculating.set(false);
      },
    });
  }

  addToReport(): void {
    this.reportMessage.set(null);
    this.reportError.set(null);
    this.reportBusy.set(true);
    this.api.addToReport(this.buildRequest()).subscribe({
      next: (res) => {
        this.reportMessage.set(
          `Row ${res.report.rowsInFile} added to ${res.report.fileName}: ${res.report.csvRow}`,
        );
        this.reportBusy.set(false);
      },
      error: (err) => {
        this.reportError.set(describeHttpError(err).join(' '));
        this.reportBusy.set(false);
      },
    });
  }

  downloadReport(): void {
    this.reportError.set(null);
    this.api.downloadReport().subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'fd_report.csv';
        link.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        if (err instanceof HttpErrorResponse && err.status === 404) {
          this.reportError.set("No report yet. Calculate first, then click 'Add to report'.");
        } else {
          this.reportError.set(describeHttpError(err).join(' '));
        }
      },
    });
  }

  reset(): void {
    this.model = defaultModel();
    this.selected.set([]);
    this.errors.set([]);
    this.result.set(null);
    this.reportMessage.set(null);
    this.reportError.set(null);
  }

  // ---------- Display helpers ----------

  inr(value: number | null | undefined): string {
    if (value === null || value === undefined) {
      return '-';
    }
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(Number(value));
  }

  pct(value: number | null | undefined): string {
    return value === null || value === undefined ? '-' : Number(value).toFixed(2) + '%';
  }

  termText(r: FdCalculationResponse): string {
    return `${r.termValue} ${r.termUnit.toLowerCase()}`;
  }
}
