import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {
        int amount = Integer.parseInt(args[0]);
        BigDecimal price = BigDecimal.valueOf(Double.parseDouble(args[1]));
        Type type = Type.valueOf(args[2]);

        CurrencyRate inputCurrencyRate = CurrencyRate.DKK;
        CurrencyRate outputCurrencyRate = CurrencyRate.DKK;
        CountryCode countryCode = CountryCode.DK;

        // считывание доп. аргументов
        if (args.length > 3) {
            for (int i = 0; i < args.length - 3; i++) {
                String[] argsParts = args[3 + i].split("=");

                switch (argsParts[0]) {
                    case "--vat": {
                        countryCode = CountryCode.valueOf(argsParts[1]);
                        break;
                    }
                    case "--input-currency": {
                        inputCurrencyRate = CurrencyRate.valueOf(argsParts[1]);
                        break;
                    }
                    case "-–output-currency": {
                        outputCurrencyRate = CurrencyRate.valueOf(argsParts[1]);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        BigDecimal totalPrice = calculateTotalPrice(amount, price, type, countryCode, inputCurrencyRate, outputCurrencyRate);
        System.out.println(totalPrice + " " + outputCurrencyRate.name());
    }

    // метод для расчета полной цены
    private static BigDecimal calculateTotalPrice(
            int amount, BigDecimal price, Type type, CountryCode countryCode, CurrencyRate inputCurrencyRate, CurrencyRate outputCurrencyRate) {

        BigDecimal totalPrice = BigDecimal.valueOf(0); // используем BigDecimal, чтобы избежать неточностей при округлении

        totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(amount)));

        totalPrice = totalPrice.add(getFreight(type, amount));

        totalPrice = totalPrice.multiply(inputCurrencyRate.getRate());

        totalPrice = totalPrice.multiply(BigDecimal
                .valueOf(100)
                .add(getVatRate(countryCode, type))
                .divide(new BigDecimal(100), 2, RoundingMode.UNNECESSARY));

        totalPrice = totalPrice.divide(outputCurrencyRate.getRate(), 2, RoundingMode.CEILING);

        return totalPrice;
    }

    private static BigDecimal getFreight(Type type, int amount) {
        switch (type) {
            case online: {
                return BigDecimal.valueOf(0);
            }

            case book: {
                int freight;

                if (amount <= 10) {
                    freight = 50;
                } else {
                    freight = 50 + (int) Math.ceil((amount-10)/10.0) * 25;
                }

                return BigDecimal.valueOf(freight);
            }

            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    // метод для определения Налога на добавленную стоимость
    private static BigDecimal getVatRate(CountryCode countryCode, Type type) {
        switch (countryCode) {
            case DK:
            case NO:
            case SE: {
                return new BigDecimal("25");
            }
            case GB: {
                return new BigDecimal("20");
            }

            case DE: {
                switch (type) {
                    case online: {
                        return new BigDecimal("19");
                    }
                    case book: {
                        return new BigDecimal("12");
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
            }

            default: {
              throw new IllegalArgumentException();
            }
        }
    }
}