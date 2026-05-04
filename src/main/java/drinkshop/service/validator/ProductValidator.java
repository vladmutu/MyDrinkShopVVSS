package drinkshop.service.validator;

import drinkshop.domain.Product;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (product.getNume() == null || product.getNume().isBlank())
            errors += "Numele nu poate fi gol!\n";

        if (product.getPret() <= 0)
            errors += "Pret invalid!\n";

        if (product.getCategorie() == null)
            errors += "Categoria nu poate fi nula!\n";

        if (product.getTip() == null)
            errors += "Tipul nu poate fi nul!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
