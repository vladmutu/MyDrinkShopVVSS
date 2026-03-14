package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.export.CsvExporter;
import drinkshop.receipt.ReceiptGenerator;
import drinkshop.reports.DailyReportService;
import drinkshop.repository.Repository;

import java.util.List;

/**
 * Service class that acts as a facade for the Drink Shop application.
 * It provides a unified interface to manage products, orders, stock, and
 * recipes by leveraging other specialized services.
 */
public class DrinkShopService {

    private final ProductService productService;
    private final OrderService orderService;
    private final RetetaService retetaService;
    private final StocService stocService;
    private final DailyReportService report;
    private final Repository<Integer, CategorieBautura> catRepo;

    /**
     * Constructs a {@code DrinkShopService} with the specified repositories.
     * Initializes the underlying specialized services.
     *
     * @param productRepo The repository for managing {@link Product} entities.
     * @param orderRepo   The repository for managing {@link Order} entities.
     * @param retetaRepo  The repository for managing {@link Reteta} (recipe)
     *                    entities.
     * @param stocRepo   The repository for managing {@link Stoc} (stock) entities.
     * @param catRepo    The repository for managing {@link CategorieBautura} (category) entities.
     */
    public DrinkShopService(
            Repository<Integer, Product> productRepo,
            Repository<Integer, Order> orderRepo,
            Repository<Integer, Reteta> retetaRepo,
            Repository<Integer, Stoc> stocRepo,
            Repository<Integer, CategorieBautura> catRepo) {
        this.productService = new ProductService(productRepo);
        this.orderService = new OrderService(orderRepo, productRepo);
        this.retetaService = new RetetaService(retetaRepo);
        this.stocService = new StocService(stocRepo);
        this.report = new DailyReportService(orderRepo);
        this.catRepo = catRepo;
    }

    // ---------- PRODUCT ----------

    /**
     * Adds a new product to the system.
     *
     * @param p The {@code Product} to be added.
     */
    public void addProduct(Product p) {
        productService.addProduct(p);
    }

    /**
     * Updates an existing product with new details.
     *
     * @param id        The ID of the product to update.
     * @param name      The new name of the product.
     * @param price     The new price of the product.
     * @param categorie The new category ({@link CategorieBautura}) of the product.
     * @param tip       The new type ({@link TipBautura}) of the product.
     */
    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        productService.updateProduct(id, name, price, categorie, tip);
    }

    /**
     * Deletes a product from the system by its ID.
     *
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(int id) {
        productService.deleteProduct(id);
    }

    /**
     * Retrieves a list of all available drink categories.
     *
     * @return A list containing all {@code CategorieBautura} entries.
     */
    public List<CategorieBautura> getAllCategories() {
        return catRepo.findAll();
    }

    /**
     * Retrieves a list of all products in the system.
     *
     * @return A list containing all {@code Product} entities.
     */
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Filters products based on their category.
     *
     * @param categorie The {@link CategorieBautura} to filter by.
     * @return A list of products that belong to the specified category.
     */
    public List<Product> filtreazaDupaCategorie(CategorieBautura categorie) {
        return productService.filterByCategorie(categorie);
    }

    /**
     * Filters products based on their type.
     *
     * @param tip The {@link TipBautura} to filter by.
     * @return A list of products that match the specified type.
     */
    public List<Product> filtreazaDupaTip(TipBautura tip) {
        return productService.filterByTip(tip);
    }

    // ---------- ORDER ----------

    /**
     * Adds a new order to the system.
     *
     * @param o The {@code Order} to be added.
     */
    public void addOrder(Order o) {
        orderService.addOrder(o);
    }

    /**
     * Finalizes an order by checking stock availability, consuming the required
     * stock,
     * and saving the order in the system.
     *
     * @param o The {@code Order} to be finalized.
     * @throws IllegalStateException If there is insufficient stock to fulfill any
     *                               of the products' recipe requirements.
     */
    public void finalizeazaComanda(Order o) {
        // 1. Verifica stocul pentru fiecare produs din comanda
        for (OrderItem item : o.getItems()) {
            Reteta r = retetaService.findById(item.getProduct().getId());
            for (int i = 0; i < item.getQuantity(); i++) {
                if (!stocService.areSuficient(r)) {
                    throw new IllegalStateException("Stoc insuficient pentru: " + item.getProduct().getNume());
                }
            }
        }

        // 2. Consuma ingredientele
        for (OrderItem item : o.getItems()) {
            Reteta r = retetaService.findById(item.getProduct().getId());
            for (int i = 0; i < item.getQuantity(); i++) {
                stocService.consuma(r);
            }
        }

        // 3. Salveaza comanda
        orderService.addOrder(o);
    }

    /**
     * Retrieves a list of all orders placed in the system.
     *
     * @return A list containing all {@code Order} entities.
     */
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Computes the total price of a given order.
     *
     * @param o The {@code Order} for which to compute the total.
     * @return The total price of the order as a {@code double}.
     */
    public double computeTotal(Order o) {
        return orderService.computeTotal(o);
    }

    /**
     * Generates a text receipt for a given order.
     *
     * @param o The {@code Order} to generate the receipt for.
     * @return A string representation of the generated receipt.
     */
    public String generateReceipt(Order o) {
        return ReceiptGenerator.generate(o, productService.getAllProducts());
    }

    /**
     * Retrieves the total revenue generated from all daily orders.
     *
     * @return The total daily revenue as a {@code double}.
     */
    public double getDailyRevenue() {
        return report.getTotalRevenue();
    }

    /**
     * Exports all orders and associated products to a CSV file at the given path.
     *
     * @param path The file path where the CSV should be exported.
     */
    public void exportCsv(String path) {
        CsvExporter.exportOrders(productService.getAllProducts(), orderService.getAllOrders(), path);
    }

    // ---------- STOCK + RECIPE ----------

    /**
     * Processes an order for a specific product by checking and consuming the
     * necessary stock based on its recipe.
     *
     * @param produs The {@code Product} to be ordered and consumed from stock.
     * @throws IllegalStateException If there is insufficient stock to fulfill the
     *                               product's recipe requirements.
     */
    public void comandaProdus(Product produs) {
        Reteta reteta = retetaService.findById(produs.getId());

        if (!stocService.areSuficient(reteta)) {
            throw new IllegalStateException("Stoc insuficient pentru produsul: " + produs.getNume());
        }
        stocService.consuma(reteta);
    }

    /**
     * Retrieves a list of all available recipes.
     *
     * @return A list containing all {@code Reteta} (recipe) entities.
     */
    public List<Reteta> getAllRetete() {
        return retetaService.getAll();
    }

    /**
     * Adds a new recipe to the system.
     *
     * @param r The {@code Reteta} to be added.
     */
    public void addReteta(Reteta r) {
        retetaService.addReteta(r);
    }

    /**
     * Updates an existing recipe.
     *
     * @param r The {@code Reteta} containing the updated information.
     */
    public void updateReteta(Reteta r) {
        retetaService.updateReteta(r);
    }

    /**
     * Deletes a recipe from the system by its ID.
     *
     * @param id The ID of the recipe to delete.
     */
    public void deleteReteta(int id) {
        retetaService.deleteReteta(id);
    }
}