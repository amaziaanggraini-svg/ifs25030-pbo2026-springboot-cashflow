package org.delcom.ifs25030pbo2026springbootcashflow.domain.exception;

/** Dilempar saat validasi aturan bisnis / input domain gagal. */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}