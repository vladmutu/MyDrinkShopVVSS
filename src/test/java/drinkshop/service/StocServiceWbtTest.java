package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WBT - StocService.consuma (CFG/CC/Paths/Coverage)")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class StocServiceWbtTest {

    private Repository<Integer, Stoc> newInMemoryRepo() {
        return new AbstractRepository<>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
    }

    private Reteta reteta(int id, IngredientReteta... ingrediente) {
        return new Reteta(id, List.of(ingrediente));
    }

    @Test
    @DisplayName("TC1/P1: stoc insuficient -> ValidationException")
    void consuma_insufficientStock_throwsValidationException() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "lapte", 2, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(1, new IngredientReteta("lapte", 3));

        assertThrows(ValidationException.class, () -> service.consuma(r));
    }

    @Test
    @DisplayName("TC2/P2: reteta fara ingrediente -> fara modificari")
    void consuma_emptyRecipe_noChanges() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "zahar", 10, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(2);

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(10.0, repo.findOne(1).getCantitate(), 0.0001);
    }

    @Test
    @DisplayName("TC3/P3: necesar=0, ingredient inexistent in stoc -> inner loop 0 iteratii")
    void consuma_zeroNeeded_noStockRows_noChanges() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "lapte", 5, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(3, new IngredientReteta("scortisoara", 0));

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(5.0, repo.findOne(1).getCantitate(), 0.0001);
    }

    @Test
    @DisplayName("TC4/P4: necesar=0 cu stoc existent -> ramas<=0 true (break)")
    void consuma_breakBranch_ramasZero_noUpdate() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "zahar", 10, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(4, new IngredientReteta("zahar", 0));

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(10.0, repo.findOne(1).getCantitate(), 0.0001);
    }

    @Test
    @DisplayName("TC5/P5: consum normal -> update stoc")
    void consuma_consumesAndUpdatesStock() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "zahar", 10, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(5, new IngredientReteta("zahar", 5));

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(5.0, repo.findOne(1).getCantitate(), 0.0001);
    }

    @Test
    @DisplayName("TC6/LC: inner loop cu iteratii multiple")
    void consuma_multipleInnerIterations_totalReducedCorrectly() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        repo.save(new Stoc(1, "lapte", 3, 0));
        repo.save(new Stoc(2, "lapte", 4, 0));
        StocService service = new StocService(repo);

        Reteta r = reteta(6, new IngredientReteta("lapte", 5));

        assertDoesNotThrow(() -> service.consuma(r));

        double totalRamas = repo.findAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("lapte"))
                .mapToDouble(Stoc::getCantitate)
                .sum();

        assertEquals(2.0, totalRamas, 0.0001);
    }

    @Test
    @DisplayName("TC7/non-valid: reteta null -> NullPointerException")
    void consuma_nullReteta_throwsNpe() {
        Repository<Integer, Stoc> repo = newInMemoryRepo();
        StocService service = new StocService(repo);

        assertThrows(NullPointerException.class, () -> service.consuma(null));
    }
}
