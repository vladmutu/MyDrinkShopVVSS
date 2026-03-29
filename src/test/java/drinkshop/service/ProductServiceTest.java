package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BBT ECP+BVA - ProductService")
@Tag("bbt")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ProductServiceTest {

    // In-memory repository used to isolate service behavior from file persistence.

    private Repository<Integer, Product> newInMemoryRepo() {
        return new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
    }

    private Product validProduct(int id, double price) {
        // Dummy valid values for non-investigated parameters (name/category/type).
        return new Product(id, "Latte", price, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
    }

    @Nested
    @DisplayName("addProduct")
    class AddProductTests {

        // ECP over two constrained parameters: id (>0) and price (>0).
        @ParameterizedTest(name = "ECP add: id={0}, price={1}, valid={2}")
        @CsvSource({
                "1, 10.0, true",
                "0, 10.0, false",
                "1, 0.0, false",
                "0, 0.0, false"
        })
        void addProduct_ECP(int id, double price, boolean valid) {
            // Arrange
            Repository<Integer, Product> repo = newInMemoryRepo();
            ProductService service = new ProductService(repo);
            Product p = validProduct(id, price);

            // Act
            // Assert
            if (valid) {
                assertDoesNotThrow(() -> service.addProduct(p));
                assertNotNull(service.findById(id));
            } else {
                assertThrows(ValidationException.class, () -> service.addProduct(p));
            }
        }

        // BVA around id boundary at 0: invalid {-1,0}, valid {1,2}.
        @ParameterizedTest(name = "BVA add id boundary: id={0}, valid={1}")
        @CsvSource({
                "-1, false",
                "0, false",
                "1, true",
                "2, true"
        })
        void addProduct_BVA_IdBoundary(int id, boolean valid) {
            // Arrange
            Repository<Integer, Product> repo = newInMemoryRepo();
            ProductService service = new ProductService(repo);
            Product p = validProduct(id, 10.0);

            // Act
            // Assert
            if (valid) {
                assertDoesNotThrow(() -> service.addProduct(p));
                assertNotNull(service.findById(id));
            } else {
                assertThrows(ValidationException.class, () -> service.addProduct(p));
            }
        }

        // BVA around price boundary at 0: invalid {-0.01,0}, valid {0.01,1.0}.
        @ParameterizedTest(name = "BVA add price boundary: price={0}, valid={1}")
        @CsvSource({
                "-0.01, false",
                "0.0, false",
                "0.01, true",
                "1.0, true"
        })
        void addProduct_BVA_PriceBoundary(double price, boolean valid) {
            // Arrange
            Repository<Integer, Product> repo = newInMemoryRepo();
            ProductService service = new ProductService(repo);
            Product p = validProduct(1, price);

            // Act
            // Assert
            if (valid) {
                assertDoesNotThrow(() -> service.addProduct(p));
                assertNotNull(service.findById(1));
            } else {
                assertThrows(ValidationException.class, () -> service.addProduct(p));
            }
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProductTests {

        // ECP for update using same constrained parameters: id and price.
        @ParameterizedTest(name = "ECP update: id={0}, price={1}, valid={2}")
        @CsvSource({
                "1, 12.0, true",
                "0, 12.0, false",
                "1, 0.0, false",
                "0, 0.0, false"
        })
        void updateProduct_ECP(int id, double price, boolean valid) {
            // Arrange
            Repository<Integer, Product> repo = newInMemoryRepo();
            ProductService service = new ProductService(repo);
            service.addProduct(validProduct(1, 10.0)); // dummy valid existing product

            // Act
            // Assert
            if (valid) {
                assertDoesNotThrow(() ->
                        service.updateProduct(id, "Latte Update", price, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
                );
                Product updated = service.findById(id);
                assertNotNull(updated);
                assertEquals("Latte Update", updated.getNume());
                assertEquals(price, updated.getPret(), 0.0001);
            } else {
                assertThrows(ValidationException.class, () ->
                        service.updateProduct(id, "Latte Update", price, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
                );
            }
        }
    }
}