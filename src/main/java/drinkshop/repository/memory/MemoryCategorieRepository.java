package drinkshop.repository.memory;

import drinkshop.domain.CategorieBautura;
import drinkshop.repository.AbstractRepository;

public class MemoryCategorieRepository extends AbstractRepository<Integer, CategorieBautura> {

    public MemoryCategorieRepository() {
        for (CategorieBautura cb : CategorieBautura.values()) {
            super.entities.put(cb.ordinal(), cb);
        }
    }

    @Override
    protected Integer getId(CategorieBautura entity) {
        return entity.ordinal();
    }
}
