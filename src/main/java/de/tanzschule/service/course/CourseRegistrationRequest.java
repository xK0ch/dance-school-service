package de.tanzschule.service.course;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRegistrationRequest(
        @NotBlank String salutation,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String birthDate,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String phone,
        String mobile,
        @NotBlank @Email String email,
        String remark,
        @NotBlank String tariffName,
        @NotNull Boolean withPartner,
        String partnerSalutation,
        String partnerFirstName,
        String partnerLastName,
        String partnerBirthDate,
        String partnerStreet,
        String partnerCity,
        String partnerPhone,
        String partnerMobile,
        @Email String partnerEmail,
        @NotNull Boolean directDebit,
        String accountHolder,
        String iban,
        String bic,
        Boolean samePaymentDetails,
        String partnerAccountHolder,
        String partnerIban,
        String partnerBic
) {
}
