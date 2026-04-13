package com.mukuru.utils;

import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Generates unique synthetic customer data for each test run.
 * Names are produced by datafaker; MSISDNs, passport numbers,
 * and dates of birth are generated to match Mukuru USSD validation rules.
 */
public class TestDataGenerator {

    private static final Random RNG   = new Random();
    private static final Faker  FAKER = new Faker();

    // South-African network prefixes (international format, no leading +)
    private static final String[] SA_PREFIXES = {
        "27831", "27832", "27833", "27721", "27722",
        "27763", "27764", "27765", "27611", "27612"
    };

    // Lesotho mobile prefixes (2-digit, paired with 00266 + 6 more digits = 13 total)
    private static final String[] LS_PREFIXES = {
        "57", "58", "59", "62", "63", "67", "68"
    };

    private final String msisdn;
    private final String passportNumber;
    private final String firstName;
    private final String lastName;
    private final String gender;         // "1" = Male, "2" = Female
    private final String dateOfBirth;   // DD.MM.YYYY — format expected by the USSD menu
    private final String city;
    private final String suburb;
    private final String streetAddress;
    private final String postalCode;

    // Order / recipient fields
    private final String recipientMsisdn;
    private final String recipientFirstName;
    private final String recipientLastName;
    private final String sendAmount;     // ZAR integer string, e.g. "200"

    public TestDataGenerator() {
        this.msisdn             = generateMsisdn();
        this.passportNumber     = generatePassport();
        this.firstName          = FAKER.name().firstName().replaceAll("[^A-Za-z]", "");
        this.lastName           = FAKER.name().lastName().replaceAll("[^A-Za-z]", "");
        this.gender             = RNG.nextBoolean() ? "1" : "2";
        this.dateOfBirth        = generateDob();
        // Fixed address that auto-resolves cleanly in the USSD emulator
        this.city               = "Johannesburg";
        this.suburb             = "Yeoville";
        this.streetAddress      = "1 Baker St";
        this.postalCode         = "2198";
        // Recipient
        this.recipientMsisdn    = generateRecipientMsisdn();
        this.recipientFirstName = FAKER.name().firstName().replaceAll("[^A-Za-z]", "");
        this.recipientLastName  = FAKER.name().lastName().replaceAll("[^A-Za-z]", "");
        this.sendAmount         = String.valueOf(100 + RNG.nextInt(401)); // R100–R500
    }

    // -------------------------------------------------------------------------
    // Accessors — customer
    // -------------------------------------------------------------------------

    public String getMsisdn()           { return msisdn; }
    public String getPassportNumber()   { return passportNumber; }
    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getGender()           { return gender; }
    public String getDateOfBirth()      { return dateOfBirth; }
    public String getCity()             { return city; }
    public String getSuburb()           { return suburb; }
    public String getStreetAddress()    { return streetAddress; }
    public String getPostalCode()       { return postalCode; }
    public String getFullName()         { return firstName + " " + lastName; }

    // Accessors — order / recipient
    public String getRecipientMsisdn()      { return recipientMsisdn; }
    public String getRecipientFirstName()   { return recipientFirstName; }
    public String getRecipientLastName()    { return recipientLastName; }
    public String getRecipientFullName()    { return recipientFirstName + " " + recipientLastName; }
    public String getSendAmount()           { return sendAmount; }

    @Override
    public String toString() {
        return String.format(
            "Customer{msisdn='%s', passport='%s', name='%s', dob='%s'}",
            msisdn, passportNumber, getFullName(), dateOfBirth
        );
    }

    // -------------------------------------------------------------------------
    // Generators
    // -------------------------------------------------------------------------

    private static String generateMsisdn() {
        String prefix = SA_PREFIXES[RNG.nextInt(SA_PREFIXES.length)];
        String suffix;
        do {
            suffix = String.format("%06d", RNG.nextInt(1_000_000));
        } while (suffix.startsWith("3378") || suffix.equals("000000"));
        return "+" + prefix + suffix;
    }

    /**
     * Generates a Lesotho mobile number in the format 0026667XXXXXX (13 digits).
     * e.g. 0026667941381 → 00266 + 67 + 941381
     */
    private static String generateRecipientMsisdn() {
        String prefix = LS_PREFIXES[RNG.nextInt(LS_PREFIXES.length)];
        return "00266" + prefix + String.format("%06d", RNG.nextInt(1_000_000));
    }

    /**
     * DRC-style passport: 9 all-numeric digits.
     * Mukuru USSD rejects alphanumeric formats for DRC passports.
     */
    private static String generatePassport() {
        long seed = Math.abs(UUID.randomUUID().getMostSignificantBits());
        long num  = 100_000_000L + (seed % 900_000_000L);
        return Long.toString(num);
    }

    /**
     * DOB in DD.MM.YYYY format. Age range: 18–65.
     */
    private static String generateDob() {
        LocalDate dob = LocalDate.now()
                .minusYears(18 + RNG.nextInt(48))
                .minusMonths(RNG.nextInt(12))
                .minusDays(RNG.nextInt(28));
        return dob.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
