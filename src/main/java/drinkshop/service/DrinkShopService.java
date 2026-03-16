package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.export.CsvExporter;
import drinkshop.receipt.ReceiptGenerator;
import drinkshop.reports.DailyReportService;
import drinkshop.repository.Repository;

import java.util.List;

public class DrinkShopService {

    private final ProductService productService;
    private final OrderService orderService;
    private final RetetaService retetaService;
    private final StocService stocService;
    private final DailyReportService report;

    public DrinkShopService(
            Repository<Integer, Product> productRepo,
            Repository<Integer, Order> orderRepo,
            Repository<Integer, Reteta> retetaRepo,
            Repository<Integer, Stoc> stocService
    ) {
        this.productService = new ProductService(productRepo);
        this.orderService = new OrderService(orderRepo, productRepo);
        this.retetaService = new RetetaService(retetaRepo);
        this.stocService = new StocService(stocService);
        this.report = new DailyReportService(orderRepo);
    }

    // ---------- PRODUCT ----------
    public void addProduct(Product p) {
        productService.addProduct(p);
    }

    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        productService.updateProduct(id, name, price, categorie, tip);
    }

    public void deleteProduct(int id) {
        productService.deleteProduct(id);
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public List<Product> filtreazaDupaCategorie(CategorieBautura categorie) {
        return productService.filterByCategorie(categorie);
    }

    public List<Product> filtreazaDupaTip(TipBautura tip) {
        return productService.filterByTip(tip);
    }

    // ---------- ORDER ----------
    // Modificăm addOrder pentru a integra logica de consum a stocului folosind metoda comandaProdus:
    public void addOrder(Order o) {
        // 1. Validăm și consumăm stocul pentru fiecare produs din coș
        for (OrderItem item : o.getItems()) {
            // Apelăm comandaProdus de atâtea ori cât este cantitatea din acel produs
            for(int i = 0; i < item.getQuantity(); i++) {
                comandaProdus(item.getProduct());
            }
        }

        // 2. Doar dacă nu a crăpat stocul la pasul 1, salvăm comanda finală
        orderService.addOrder(o);
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public double computeTotal(Order o) {
        return orderService.computeTotal(o);
    }

    public String generateReceipt(Order o) {
        return ReceiptGenerator.generate(o, productService.getAllProducts());
    }

    public double getDailyRevenue() {
        return report.getTotalRevenue();
    }

    public void exportCsv(String path) {
        CsvExporter.exportOrders(productService.getAllProducts(), orderService.getAllOrders(), path);
    }

    // ---------- STOCK + RECIPE ----------
    public void comandaProdus(Product produs) {
        Reteta reteta = retetaService.findById(produs.getId());

        if (!stocService.areSuficient(reteta)) {
            throw new IllegalStateException("Stoc insuficient pentru produsul: " + produs.getNume());
        }
        stocService.consuma(reteta);
    }

    public List<Reteta> getAllRetete() {
        return retetaService.getAll();
    }

    public void addReteta(Reteta r) {
        retetaService.addReteta(r);
    }

    public void updateReteta(Reteta r) {
        retetaService.updateReteta(r);
    }

    public void deleteReteta(int id) {
        retetaService.deleteReteta(id);
    }
}