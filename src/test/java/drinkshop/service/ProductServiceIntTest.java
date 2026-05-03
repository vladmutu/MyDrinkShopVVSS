package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//Step 2: ProductServiceIntTest (S + V real, R mock)

@ExtendWith(MockitoExtension.class)
class ProductServiceIntTest {

    @Mock
    private Repository<Integer, Product> productRepo;

    @Test
    void addProduct_valid_usesRealValidator_andSaves() {
        // Arrange
        ProductService service = new ProductService(productRepo, new ProductValidator());
        Product product = new Product(1, "Latte", 10.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act
        service.addProduct(product);

        // Assert
        verify(productRepo, times(1)).save(product);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void addProduct_invalid_throws_andDoesNotSave() {
        // Arrange
        ProductService service = new ProductService(productRepo, new ProductValidator());
        Product invalid = new Product(0, "Latte", 0.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act + Assert
        assertThrows(ValidationException.class, () -> service.addProduct(invalid));
        verify(productRepo, never()).save(any());
        verifyNoMoreInteractions(productRepo);
    }
}