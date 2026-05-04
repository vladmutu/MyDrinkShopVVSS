package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


//mvn -Dtest=ProductServiceUnitTest test
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ProductServiceUnitTest {

    @Mock
    private Repository<Integer, Product> productRepo;

    @Mock
    private Validator<Product> productValidator;

    @Test
    void addProduct_valid_callsValidateAndSave() {
        // Arrange
        ProductService service = new ProductService(productRepo, productValidator);
        Product product = new Product(1, "Latte", 10.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act
        service.addProduct(product);

        // Assert
        verify(productValidator, times(1)).validate(product);
        verify(productRepo, times(1)).save(product);
        verifyNoMoreInteractions(productValidator, productRepo);
    }

    @Test
    void addProduct_invalid_throwsAndDoesNotSave() {
        // Arrange
        ProductService service = new ProductService(productRepo, productValidator);
        Product product = new Product(0, "Latte", 0.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        doThrow(new ValidationException("invalid")).when(productValidator).validate(product);

        // Act + Assert
        assertThrows(ValidationException.class, () -> service.addProduct(product));

        verify(productValidator, times(1)).validate(product);
        verify(productRepo, never()).save(any());
        verifyNoMoreInteractions(productValidator, productRepo);
    }
}