import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApiError,
  CategoryListResponse,
  FdCalculationRequest,
  FdCalculationResponse,
  FdReportResponse,
  ProductInfo,
} from './fd.models';

/**
 * All HTTP calls from the Angular app to calculator-service.
 * Addresses start with /api/... ; the dev proxy (proxy.conf.json) forwards
 * them to http://localhost:8082.
 */
@Injectable({ providedIn: 'root' })
export class FdCalculatorService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/fd';

  getCategories(productCode?: string | null): Observable<CategoryListResponse> {
    let params = new HttpParams();
    if (productCode) {
      params = params.set('productCode', productCode);
    }
    return this.http.get<CategoryListResponse>(`${this.baseUrl}/categories`, { params });
  }

  getProducts(): Observable<ProductInfo[]> {
    return this.http.get<ProductInfo[]>(`${this.baseUrl}/products`);
  }

  getMyCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/my-categories`);
  }

  calculate(request: FdCalculationRequest): Observable<FdCalculationResponse> {
    return this.http.post<FdCalculationResponse>(`${this.baseUrl}/calculate`, request);
  }

  addToReport(request: FdCalculationRequest): Observable<FdReportResponse> {
    return this.http.post<FdReportResponse>(`${this.baseUrl}/report`, request);
  }

  downloadReport(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/report/download`, { responseType: 'blob' });
  }
}

/** Turns any HTTP error into a list of friendly messages for the page. */
export function describeHttpError(err: unknown): string[] {
  if (err instanceof HttpErrorResponse) {
    const body = err.error as Partial<ApiError> | null;
    if (body && typeof body === 'object' && typeof body.message === 'string') {
      const details = Array.isArray(body.details) ? body.details.filter((d) => !!d) : [];
      return details.length > 0 ? details : [body.message];
    }
    if (err.status === 0 || err.status === 500 || err.status === 502 || err.status === 504) {
      return ['Cannot reach the FD Calculator service. Is the backend running on port 8082?'];
    }
    if (err.status === 401) {
      return ['Please log in first.'];
    }
    if (err.status === 403) {
      return ['Access denied. The FD Calculator is only for customers.'];
    }
    return [`Unexpected error (HTTP ${err.status}).`];
  }
  return ['Unexpected error. Please try again.'];
}
