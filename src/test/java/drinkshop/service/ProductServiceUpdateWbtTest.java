package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================
 * WBT - ProductService.updateProduct() - White Box Testing
 * ============================================================
 *
 * Tine: updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip)
 *
 * Validari implementate in ProductValidator:
 * 1. id <= 0 -> ValidationException
 * 2. nume == null || nume.isBlank() -> ValidationException
 * 3. pret <= 0 -> ValidationException
 * 4. categoria == null -> ValidationException (implicit, domain validation)
 * 5. tip == null -> ValidationException (implicit, domain validation)
 *
 * Cazurile WBT acoperite:
 * - Path VALID: toti parametrii sunt validi
 * - Path INVALID: id <= 0
 * - Path INVALID: nume == null
 * - Path INVALID: nume este blank/empty
 * - Path INVALID: pret <= 0
 * - Path INVALID: pret == 0 (boundary)
 * - Path INVALID: categoria == null
 * - Path INVALID: tip == null
 * - Path INVALID: combinatii de parametri invalizi
 * - Boundary values: id = -1, 0, 1
 * - Boundary values: pret = -0.01, 0.0, 0.01
 *
 * Respecta structura AAA: Arrange -> Act -> Assert
 */
@DisplayName("WBT - ProductService.updateProduct()")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ProductServiceUpdateWbtTest {

    // Repository in-memory pentru izolarea testelor
    private Repository<Integer, Product> newInMemoryRepo() {
        return new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
    }

    // ==================== TESTE VALIDE ====================

    /**
     * TC1-VALID: updateProduct cu parametrii complet validi
     * Preconditions: id > 0, nume != null/empty, pret > 0, categorie selectata, tip selectat
     * Expected: fara exceptii, produsul este actualizat
     */
    @Test
    @DisplayName("TC1-VALID: updateProduct cu toti parametrii validi")
    void updateProduct_valid_all_parameters() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Latte Original", 8.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Latte Premium", 10.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
        );

        Product updated = repo.findOne(1);
        assertNotNull(updated);
        assertEquals("Latte Premium", updated.getNume());
        assertEquals(10.0, updated.getPret(), 0.0001);
    }

    /**
     * TC2-VALID: updateProduct cu id = 1 (boundary valid)
     * Preconditions: id = 1, nume valid, pret valid
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC2-VALID: updateProduct cu id = 1 (boundary)")
    void updateProduct_valid_id_boundary_one() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Updated", 7.5, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC3-VALID: updateProduct cu id mare (100)
     * Preconditions: id = 100, toti ceilalti parametrii validi
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC3-VALID: updateProduct cu id = 100 (mare)")
    void updateProduct_valid_id_large() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(100, "Coffee", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(100, "Coffee Updated", 16.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC)
        );
    }

    /**
     * TC4-VALID: updateProduct cu pret = 0.01 (boundary valid)
     * Preconditions: pret = 0.01, ceilalti parametrii validi
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC4-VALID: updateProduct cu pret = 0.01 (boundary)")
    void updateProduct_valid_price_boundary_min() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Cheap", 1.0, CategorieBautura.JUICE, TipBautura.PLANT_BASED));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Very Cheap", 0.01, CategorieBautura.JUICE, TipBautura.PLANT_BASED)
        );
    }

    /**
     * TC5-VALID: updateProduct cu pret mare (500.99)
     * Preconditions: pret = 500.99, ceilalti parametrii validi
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC5-VALID: updateProduct cu pret = 500.99 (mare)")
    void updateProduct_valid_price_large() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Expensive", 100.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Very Expensive", 500.99, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
        );
    }

    /**
     * TC6-VALID: updateProduct cu nume scurt "A"
     * Preconditions: nume = "A", ceilalti parametrii validi
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC6-VALID: updateProduct cu nume = \"A\" (scurt)")
    void updateProduct_valid_name_single_character() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "A", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC7-VALID: updateProduct cu nume lung cu spatii
     * Preconditions: nume = "Premium Espresso With Extra Shots", ceilalti parametrii validi
     * Expected: fara exceptii
     */
    @Test
    @DisplayName("TC7-VALID: updateProduct cu nume lung cu spatii")
    void updateProduct_valid_name_long_with_spaces() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Coffee", 8.0, CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Premium Espresso With Extra Shots", 12.5, CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY)
        );
    }

    /**
     * TC8-VALID: updateProduct cu toate categoriile suportate
     * Expected: fara exceptii pentru fiecare categorie
     */
    @Test
    @DisplayName("TC8-VALID: updateProduct cu MILK_COFFEE")
    void updateProduct_valid_category_milk_coffee() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Latte", 8.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Latte Updated", 9.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
        );
    }

    /**
     * TC9-VALID: updateProduct cu categorie TEA
     */
    @Test
    @DisplayName("TC9-VALID: updateProduct cu categorie TEA")
    void updateProduct_valid_category_tea() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Tea", 5.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Green Tea", 6.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC10-VALID: updateProduct cu categorie JUICE
     */
    @Test
    @DisplayName("TC10-VALID: updateProduct cu categorie JUICE")
    void updateProduct_valid_category_juice() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Juice", 6.0, CategorieBautura.TEA, TipBautura.PLANT_BASED));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Orange Juice", 7.0, CategorieBautura.JUICE, TipBautura.PLANT_BASED)
        );
    }

    /**
     * TC11-VALID: updateProduct cu tip DAIRY
     */
    @Test
    @DisplayName("TC11-VALID: updateProduct cu tip DAIRY")
    void updateProduct_valid_type_dairy() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Milk Coffee", 8.0, CategorieBautura.MILK_COFFEE, TipBautura.PLANT_BASED));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Milk Coffee With Cream", 9.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY)
        );
    }

    /**
     * TC12-VALID: updateProduct cu tip NON_DAIRY
     */
    @Test
    @DisplayName("TC12-VALID: updateProduct cu tip NON_DAIRY")
    void updateProduct_valid_type_non_dairy() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Almond Latte", 7.5, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(1, "Almond Latte", 8.0, CategorieBautura.MILK_COFFEE, TipBautura.PLANT_BASED)
        );
    }

    // ==================== TESTE INVALIDE - ID ====================

    /**
     * TC13-INVALID: updateProduct cu id = -1
     * Preconditions: id = -1, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC13-INVALID: updateProduct cu id = -1")
    void updateProduct_invalid_id_negative_one() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(-1, "Updated", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC14-INVALID: updateProduct cu id = -100 (boundary negativ mic)
     * Preconditions: id = -100, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC14-INVALID: updateProduct cu id = -100")
    void updateProduct_invalid_id_negative_large() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(-100, "Updated", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC15-INVALID: updateProduct cu id = 0 (boundary invalid)
     * Preconditions: id = 0, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC15-INVALID: updateProduct cu id = 0 (boundary)")
    void updateProduct_invalid_id_zero() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(0, "Updated", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    // ==================== TESTE INVALIDE - NUME ====================

    /**
     * TC16-INVALID: updateProduct cu nume = null
     * Preconditions: nume = null, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC16-INVALID: updateProduct cu nume = null")
    void updateProduct_invalid_name_null() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, null, 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC17-INVALID: updateProduct cu nume = "" (empty string)
     * Preconditions: nume = "", ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC17-INVALID: updateProduct cu nume = \"\" (empty)")
    void updateProduct_invalid_name_empty() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC18-INVALID: updateProduct cu nume = "   " (blank - doar spatii)
     * Preconditions: nume = "   ", ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC18-INVALID: updateProduct cu nume = \"   \" (blank)")
    void updateProduct_invalid_name_blank() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "   ", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC19-INVALID: updateProduct cu nume = "\n" (doar newline)
     * Preconditions: nume = "\n", ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC19-INVALID: updateProduct cu nume = \"\\n\" (newline)")
    void updateProduct_invalid_name_newline() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "\n", 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    // ==================== TESTE INVALIDE - PRET ====================

    /**
     * TC20-INVALID: updateProduct cu pret = 0.0 (boundary invalid)
     * Preconditions: pret = 0.0, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC20-INVALID: updateProduct cu pret = 0.0 (boundary)")
    void updateProduct_invalid_price_zero() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "Updated", 0.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC21-INVALID: updateProduct cu pret = -0.01 (boundary negativ mic)
     * Preconditions: pret = -0.01, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC21-INVALID: updateProduct cu pret = -0.01")
    void updateProduct_invalid_price_negative_small() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "Updated", -0.01, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC22-INVALID: updateProduct cu pret = -100.0 (negativ mare)
     * Preconditions: pret = -100.0, ceilalti parametrii validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC22-INVALID: updateProduct cu pret = -100.0")
    void updateProduct_invalid_price_negative_large() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "Updated", -100.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    // ==================== TESTE INVALIDE - COMBINATII PARAMETRI ====================

    /**
     * TC23-INVALID: updateProduct cu id = -1 si pret = 0.0
     * Preconditions: id = -1, pret = 0.0, nume valid, categoria si tip selectate
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC23-INVALID: updateProduct cu id = -1 si pret = 0.0")
    void updateProduct_invalid_id_negative_and_price_zero() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(-1, "Updated", 0.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC24-INVALID: updateProduct cu id = 0 si nume = null
     * Preconditions: id = 0, nume = null, pret > 0, categoria si tip selectate
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC24-INVALID: updateProduct cu id = 0 si nume = null")
    void updateProduct_invalid_id_zero_and_name_null() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(0, null, 5.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC25-INVALID: updateProduct cu id = -1, nume = "", pret = -50
     * Preconditions: toti parametrii invalizi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC25-INVALID: updateProduct cu toti parametrii invalidi")
    void updateProduct_invalid_all_parameters() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(-1, "", -50.0, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    /**
     * TC26-INVALID: updateProduct cu pret = -0.01 si nume = "   "
     * Preconditions: pret negativ, nume blank, ceilalti validi
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC26-INVALID: updateProduct cu pret negativ si nume blank")
    void updateProduct_invalid_price_negative_and_name_blank() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                service.updateProduct(1, "   ", -0.01, CategorieBautura.TEA, TipBautura.DAIRY)
        );
    }

    // ==================== TESTE EDGE CASES ====================

    /**
     * TC27-VALID: updateProduct cu id si pret la limitele maxime (Integer.MAX_VALUE si Double.MAX_VALUE)
     * Expected: fara exceptii (deși id nu va exista in repo)
     */
    @Test
    @DisplayName("TC27-VALID: updateProduct cu id si pret maxime")
    void updateProduct_valid_max_values() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(Integer.MAX_VALUE, "Product", 1.0, CategorieBautura.TEA, TipBautura.BASIC));

        // Act & Assert
        assertDoesNotThrow(() ->
                service.updateProduct(Integer.MAX_VALUE, "Updated", Double.MAX_VALUE, CategorieBautura.TEA, TipBautura.BASIC)
        );
    }

    /**
     * TC28-INVALID: updateProduct cu categoria = null
     * Expected: NullPointerException din domeniu (nu din validator)
     */
    @Test
    @DisplayName("TC28-INVALID: updateProduct cu categoria = null")
    void updateProduct_invalid_category_null() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(Exception.class, () ->
                service.updateProduct(1, "Updated", 5.0, null, TipBautura.DAIRY)
        );
    }

    /**
     * TC29-INVALID: updateProduct cu tip = null
     * Expected: NullPointerException din domeniu (nu din validator)
     */
    @Test
    @DisplayName("TC29-INVALID: updateProduct cu tip = null")
    void updateProduct_invalid_type_null() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Product", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act & Assert
        assertThrows(Exception.class, () ->
                service.updateProduct(1, "Updated", 5.0, CategorieBautura.TEA, null)
        );
    }

    /**
     * TC30-VALID: updateProduct verifica ca valorile anterioare sunt inlocuite complet
     * Expected: noul nume si pret inlocuiesc cele vechi
     */
    @Test
    @DisplayName("TC30-VALID: updateProduct inlocuieste complet valorile anterioare")
    void updateProduct_valid_values_completely_replaced() {
        // Arrange
        Repository<Integer, Product> repo = newInMemoryRepo();
        ProductService service = new ProductService(repo);
        repo.save(new Product(1, "Original Name", 5.0, CategorieBautura.TEA, TipBautura.DAIRY));

        // Act
        assertDoesNotThrow(() ->
                service.updateProduct(1, "New Name", 15.99, CategorieBautura.MILK_COFFEE, TipBautura.PLANT_BASED)
        );

        // Assert
        Product updated = repo.findOne(1);
        assertEquals("New Name", updated.getNume());
        assertEquals(15.99, updated.getPret(), 0.0001);
        assertEquals(CategorieBautura.MILK_COFFEE, updated.getCategorie());
        assertEquals(TipBautura.PLANT_BASED, updated.getTip());
    }
}

