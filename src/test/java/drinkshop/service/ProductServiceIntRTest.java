package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

//Step 3: ProductServiceIntRTest (S + V + R real)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ProductServiceIntRTest {

    private FileProductRepository newTempRepo() throws Exception {
        Path tempFile = Files.createTempFile("products", ".txt");
        tempFile.toFile().deleteOnExit();
        return new FileProductRepository(tempFile.toString());
    }

    @Test
    void addProduct_valid_savesAndCanBeFound() throws Exception {
        // Arrange
        FileProductRepository repo = newTempRepo();
        ProductService service = new ProductService(repo, new ProductValidator());
        Product product = new Product(1, "Latte", 10.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act
        service.addProduct(product);

        // Assert
        Product found = service.findById(1);
        assertNotNull(found);
        assertEquals("Latte", found.getNume());
    }

    @Test
    void addProduct_invalid_throwsAndNotSaved() throws Exception {
        // Arrange
        FileProductRepository repo = newTempRepo();
        ProductService service = new ProductService(repo, new ProductValidator());
        Product invalid = new Product(0, "Latte", 0.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act + Assert
        assertThrows(ValidationException.class, () -> service.addProduct(invalid));
        assertNull(service.findById(0));
    }
}