import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ─────────────────────────────────────────────
//  ENUMS
// ─────────────────────────────────────────────

enum Genre {
    FICTION, NON_FICTION, SCIENCE, HISTORY, BIOGRAPHY,
    FANTASY, MYSTERY, ROMANCE, HORROR, SELF_HELP, CHILDREN;

    public static Genre fromIndex(int i) {
        Genre[] vals = values();
        if (i < 1 || i > vals.length) return null;
        return vals[i - 1];
    }

    public static void printAll() {
        Genre[] vals = values();
        for (int i = 0; i < vals.length; i++)
            System.out.printf("  %2d. %s%n", i + 1, vals[i]);
    }
}

enum OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

// ─────────────────────────────────────────────
//  BOOK
// ─────────────────────────────────────────────

class Book {
    private static int idCounter = 1;
    private final int id;
    private String title;
    private Author author;
    private double price;
    private int stock;
    private Genre genre;
    private String isbn;
    private int publishedYear;

    public Book(String title, Author author, double price, int stock,
                Genre genre, String isbn, int publishedYear) {
        this.id            = idCounter++;
        this.title         = title;
        this.author        = author;
        this.price         = price;
        this.stock         = stock;
        this.genre         = genre;
        this.isbn          = isbn;
        this.publishedYear = publishedYear;
    }

    public int    getId()            { return id; }
    public String getTitle()         { return title; }
    public Author getAuthor()        { return author; }
    public double getPrice()         { return price; }
    public int    getStock()         { return stock; }
    public Genre  getGenre()         { return genre; }
    public String getIsbn()          { return isbn; }
    public int    getPublishedYear() { return publishedYear; }
    public boolean isAvailable()     { return stock > 0; }

    public void setPrice(double p)   { this.price = p; }
    public void setStock(int s)      { this.stock = s; }
    public void setTitle(String t)   { this.title = t; }

    public boolean reduceStock(int qty) {
        if (qty > stock) return false;
        stock -= qty;
        return true;
    }
    public void restoreStock(int qty) { stock += qty; }

    @Override
    public String toString() {
        return String.format("[#%d] %-45s | %-20s | $%6.2f | Stock: %3d | %-10s | %s (%d)",
                id, "\"" + title + "\"", author.getFullName(),
                price, stock, genre, isbn, publishedYear);
    }
}

// ─────────────────────────────────────────────
//  AUTHOR
// ─────────────────────────────────────────────

class Author {
    private static int idCounter = 1;
    private final int id;
    private String firstName;
    private String lastName;
    private String nationality;
    private final List<Book> books = new ArrayList<>();

    public Author(String firstName, String lastName, String nationality) {
        this.id          = idCounter++;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.nationality = nationality;
    }

    public int    getId()          { return id; }
    public String getFullName()    { return firstName + " " + lastName; }
    public String getNationality() { return nationality; }
    public List<Book> getBooks()   { return Collections.unmodifiableList(books); }
    public void addBook(Book b)    { books.add(b); }

    @Override
    public String toString() {
        return String.format("[Author #%d] %-25s | %-15s | %d book(s)",
                id, getFullName(), nationality, books.size());
    }
}

// ─────────────────────────────────────────────
//  CUSTOMER
// ─────────────────────────────────────────────

class Customer {
    private static int idCounter = 1;
    private final int id;
    private String name;
    private String email;
    private String phone;
    private final List<Order> orderHistory = new ArrayList<>();

    public Customer(String name, String email, String phone) {
        this.id    = idCounter++;
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<Order> getOrderHistory() { return Collections.unmodifiableList(orderHistory); }
    void addOrder(Order o) { orderHistory.add(o); }

    @Override
    public String toString() {
        return String.format("[Customer #%d] %-20s | %-25s | %-15s | Orders: %d",
                id, name, email, phone, orderHistory.size());
    }
}

// ─────────────────────────────────────────────
//  ORDER ITEM
// ─────────────────────────────────────────────

class OrderItem {
    private final Book book;
    private int quantity;
    private final double unitPrice;

    public OrderItem(Book book, int quantity) {
        this.book      = book;
        this.quantity  = quantity;
        this.unitPrice = book.getPrice();
    }

    public Book   getBook()      { return book; }
    public int    getQuantity()  { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal()  { return unitPrice * quantity; }
    public void   setQuantity(int q) { this.quantity = q; }

    @Override
    public String toString() {
        return String.format("  %-45s x%-3d  $%6.2f  =  $%7.2f",
                "\"" + book.getTitle() + "\"", quantity, unitPrice, getSubtotal());
    }
}

// ─────────────────────────────────────────────
//  RETURN RECORD
// ─────────────────────────────────────────────

class ReturnRecord {
    private static int idCounter = 1;
    private final int id;
    private final Order originalOrder;
    private final Book book;
    private final int qty;
    private final double refundAmount;
    private final LocalDate returnDate;
    private final String reason;

    public ReturnRecord(Order order, Book book, int qty, String reason) {
        this.id            = idCounter++;
        this.originalOrder = order;
        this.book          = book;
        this.qty           = qty;
        this.refundAmount  = book.getPrice() * qty;
        this.returnDate    = LocalDate.now();
        this.reason        = reason;
    }

    public double getRefundAmount() { return refundAmount; }
    public Book   getBook()         { return book; }
    public int    getQty()          { return qty; }

    @Override
    public String toString() {
        return String.format("[Return #%d] Order #%d | \"%s\" x%d | Refund: $%.2f | Reason: %s | Date: %s",
                id, originalOrder.getId(), book.getTitle(), qty, refundAmount,
                reason, returnDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }
}

// ─────────────────────────────────────────────
//  ORDER
// ─────────────────────────────────────────────

class Order {
    private static int idCounter = 1;
    private final int id;
    private final Customer customer;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private final LocalDate orderDate;
    private boolean hasReturn = false;

    public Order(Customer customer) {
        this.id        = idCounter++;
        this.customer  = customer;
        this.status    = OrderStatus.PENDING;
        this.orderDate = LocalDate.now();
    }

    public int         getId()        { return id; }
    public Customer    getCustomer()  { return customer; }
    public OrderStatus getStatus()    { return status; }
    public LocalDate   getOrderDate() { return orderDate; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean     hasReturn()    { return hasReturn; }

    public void setStatus(OrderStatus s) { this.status = s; }
    public void markReturned()           { this.hasReturn = true; }
    public void addItem(OrderItem i)     { items.add(i); }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public void print() {
        String div = "  " + "-".repeat(65);
        System.out.println(div);
        System.out.printf("  Order #%-5d  Customer: %-20s  Date: %s%n",
                id, customer.getName(),
                orderDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.printf("  Status: %s%s%n", status, hasReturn ? "  [HAS RETURN]" : "");
        System.out.println(div);
        items.forEach(System.out::println);
        System.out.println(div);
        System.out.printf("  TOTAL: $%.2f%n", getTotal());
        System.out.println(div);
    }
}

// ─────────────────────────────────────────────
//  SHOPPING CART
// ─────────────────────────────────────────────

class ShoppingCart {
    private final Customer customer;
    private final Map<Integer, OrderItem> items = new LinkedHashMap<>();

    public ShoppingCart(Customer customer) { this.customer = customer; }

    public Customer getCustomer() { return customer; }
    public boolean  isEmpty()     { return items.isEmpty(); }
    public Collection<OrderItem> getItems() { return items.values(); }

    public void addBook(Book book, int qty) {
        if (!book.isAvailable()) {
            System.out.println("  [!] \"" + book.getTitle() + "\" is out of stock.");
            return;
        }
        if (qty > book.getStock()) {
            System.out.printf("  [!] Only %d in stock.%n", book.getStock());
            return;
        }
        if (items.containsKey(book.getId()))
            items.get(book.getId()).setQuantity(items.get(book.getId()).getQuantity() + qty);
        else
            items.put(book.getId(), new OrderItem(book, qty));
        System.out.printf("  [+] Added %dx \"%s\" to cart.%n", qty, book.getTitle());
    }

    public boolean removeBook(int bookId) { return items.remove(bookId) != null; }

    public double getTotal() {
        return items.values().stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public void clear() { items.clear(); }

    public void display() {
        if (isEmpty()) { System.out.println("  Cart is empty."); return; }
        String div = "  " + "-".repeat(65);
        System.out.println(div);
        items.values().forEach(System.out::println);
        System.out.println(div);
        System.out.printf("  Cart Total: $%.2f%n", getTotal());
        System.out.println(div);
    }
}

// ─────────────────────────────────────────────
//  INVENTORY
// ─────────────────────────────────────────────

class Inventory {
    private final Map<Integer, Book> books = new LinkedHashMap<>();

    public void addBook(Book b)           { books.put(b.getId(), b); }
    public boolean removeBook(int id)     { return books.remove(id) != null; }
    public Optional<Book> findById(int id){ return Optional.ofNullable(books.get(id)); }
    public Collection<Book> getAll()      { return books.values(); }
    public int size()                     { return books.size(); }

    public List<Book> search(String keyword) {
        String kw = keyword.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books.values())
            if (b.getTitle().toLowerCase().contains(kw)
                    || b.getAuthor().getFullName().toLowerCase().contains(kw)
                    || b.getIsbn().contains(kw))
                res.add(b);
        return res;
    }

    public List<Book> findByGenre(Genre g) {
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) if (b.getGenre() == g) res.add(b);
        return res;
    }

    public List<Book> getLowStock(int threshold) {
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) if (b.getStock() <= threshold) res.add(b);
        return res;
    }

    public void displayAll() {
        if (books.isEmpty()) { System.out.println("  No books in inventory."); return; }
        books.values().forEach(System.out::println);
    }
}

// ─────────────────────────────────────────────
//  SALES STATS
// ─────────────────────────────────────────────

class SalesStats {
    private final List<Order>        orders;
    private final List<ReturnRecord> returns;

    public SalesStats(List<Order> orders, List<ReturnRecord> returns) {
        this.orders  = orders;
        this.returns = returns;
    }

    public void print() {
        String div = "  " + "=".repeat(65);
        System.out.println(div);
        System.out.println("  SALES STATISTICS & REPORTS");
        System.out.println(div);

        double totalRevenue = orders.stream().mapToDouble(Order::getTotal).sum();
        double totalRefunds = returns.stream().mapToDouble(ReturnRecord::getRefundAmount).sum();
        double netRevenue   = totalRevenue - totalRefunds;

        System.out.printf("  Total Orders    : %d%n",    orders.size());
        System.out.printf("  Total Revenue   : $%.2f%n", totalRevenue);
        System.out.printf("  Total Refunds   : $%.2f%n", totalRefunds);
        System.out.printf("  Net Revenue     : $%.2f%n", netRevenue);
        System.out.printf("  Total Returns   : %d%n",    returns.size());

        // Units sold & revenue per book
        Map<Integer, int[]>  sold   = new LinkedHashMap<>();
        Map<Integer, String> titles = new LinkedHashMap<>();
        for (Order o : orders)
            for (OrderItem i : o.getItems()) {
                sold.computeIfAbsent(i.getBook().getId(), k -> new int[]{0, 0});
                sold.get(i.getBook().getId())[0] += i.getQuantity();
                sold.get(i.getBook().getId())[1] += (int)(i.getSubtotal() * 100);
                titles.put(i.getBook().getId(), i.getBook().getTitle());
            }

        if (!sold.isEmpty()) {
            System.out.println();
            System.out.println("  Top Selling Books (by units):");
            System.out.printf("  %-45s  %6s  %10s%n", "Title", "Units", "Revenue");
            System.out.println("  " + "-".repeat(65));
            sold.entrySet().stream()
                .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
                .limit(10)
                .forEach(e -> System.out.printf("  %-45s  %6d  $%8.2f%n",
                        "\"" + titles.get(e.getKey()) + "\"",
                        e.getValue()[0], e.getValue()[1] / 100.0));
        }

        // Order status breakdown
        System.out.println();
        System.out.println("  Orders by Status:");
        Map<OrderStatus, Long> byStatus = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) byStatus.put(s, 0L);
        for (Order o : orders) byStatus.merge(o.getStatus(), 1L, Long::sum);
        byStatus.forEach((s, c) -> System.out.printf("  %-12s : %d%n", s, c));

        System.out.println(div);
    }
}

// ─────────────────────────────────────────────
//  BOOKSHOP SYSTEM  (main facade + menu)
// ─────────────────────────────────────────────

public class BookShopSystem {

    private static final Scanner sc = new Scanner(System.in);

    private static final Inventory              inventory = new Inventory();
    private static final Map<Integer, Author>   authors   = new LinkedHashMap<>();
    private static final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private static final Map<Integer, Order>    orders    = new LinkedHashMap<>();
    private static final List<ReturnRecord>     returns   = new ArrayList<>();

    private static ShoppingCart activeCart = null;

    // ─── Entry point ──────────────────────────

    public static void main(String[] args) {
        seedData();
        System.out.println();
        System.out.println("  +------------------------------------------+");
        System.out.println("  |       Welcome to CLOVER BOOKS POS         |");
        System.out.println("  +------------------------------------------+");

        boolean running = true;
        while (running) running = showMainMenu();

        System.out.println("\n  Goodbye! Have a great day.\n");
    }

    // ─── MAIN MENU ────────────────────────────

    private static boolean showMainMenu() {
        System.out.println();
        System.out.println("  +------------------------------------------+");
        System.out.println("  |                MAIN MENU                  |");
        System.out.println("  +------------------------------------------+");
        System.out.println("  |  1. Sell a Book  (Point of Sale)          |");
        System.out.println("  |  2. Process a Return                      |");
        System.out.println("  |  3. Inventory Management                  |");
        System.out.println("  |  4. Customer Management                   |");
        System.out.println("  |  5. Order Management                      |");
        System.out.println("  |  6. Sales Statistics & Reports            |");
        System.out.println("  |  0. Exit                                  |");
        System.out.println("  +------------------------------------------+");
        System.out.print("  Choose: ");

        switch (readInt()) {
            case 1: menuSell();       break;
            case 2: menuReturn();     break;
            case 3: menuInventory();  break;
            case 4: menuCustomers();  break;
            case 5: menuOrders();     break;
            case 6: menuStats();      break;
            case 0: return false;
            default: System.out.println("  [!] Invalid option.");
        }
        return true;
    }

    // ─── 1. SELL ──────────────────────────────

    private static void menuSell() {
        System.out.println("\n  -- POINT OF SALE --------------------------------");
        Customer customer = selectOrCreateCustomer();
        if (customer == null) return;

        activeCart = new ShoppingCart(customer);
        System.out.printf("  Cart opened for: %s%n", customer.getName());

        boolean shopping = true;
        while (shopping) {
            System.out.println("\n  Cart Options:");
            System.out.println("    1. Add a book");
            System.out.println("    2. Remove a book");
            System.out.println("    3. View cart");
            System.out.println("    4. Checkout");
            System.out.println("    0. Cancel sale");
            System.out.print("  Choose: ");

            switch (readInt()) {
                case 1: cartAddBook();    break;
                case 2: cartRemoveBook(); break;
                case 3: System.out.println(); activeCart.display(); break;
                case 4:
                    if (checkout()) shopping = false;
                    break;
                case 0:
                    System.out.println("  Sale cancelled.");
                    activeCart = null;
                    shopping = false;
                    break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    private static void cartAddBook() {
        System.out.print("  Search book (title / author / ISBN): ");
        List<Book> results = inventory.search(readLine());
        if (results.isEmpty()) { System.out.println("  No books found."); return; }
        System.out.println();
        for (int i = 0; i < results.size(); i++)
            System.out.printf("  %2d. %s%n", i + 1, results.get(i));
        System.out.print("  Select number (0 to cancel): ");
        int idx = readInt();
        if (idx < 1 || idx > results.size()) return;
        Book book = results.get(idx - 1);
        System.out.print("  Quantity: ");
        int qty = readInt();
        if (qty < 1) { System.out.println("  [!] Invalid quantity."); return; }
        activeCart.addBook(book, qty);
    }

    private static void cartRemoveBook() {
        if (activeCart.isEmpty()) { System.out.println("  Cart is empty."); return; }
        activeCart.display();
        System.out.print("  Enter Book ID to remove: ");
        int id = readInt();
        if (activeCart.removeBook(id)) System.out.println("  [✓] Removed from cart.");
        else System.out.println("  [!] Book not found in cart.");
    }

    private static boolean checkout() {
        if (activeCart.isEmpty()) { System.out.println("  [!] Cart is empty."); return false; }
        System.out.println("\n  -- CHECKOUT ----------------------------------------");
        activeCart.display();
        System.out.print("  Confirm purchase? (y/n): ");
        if (!readLine().equalsIgnoreCase("y")) { System.out.println("  Checkout cancelled."); return false; }

        Order order = new Order(activeCart.getCustomer());
        for (OrderItem item : activeCart.getItems()) {
            if (!item.getBook().reduceStock(item.getQuantity())) {
                System.out.printf("  [!] Insufficient stock for \"%s\".%n", item.getBook().getTitle());
                order.getItems().forEach(i -> i.getBook().restoreStock(i.getQuantity()));
                return false;
            }
            order.addItem(item);
        }
        order.setStatus(OrderStatus.CONFIRMED);
        orders.put(order.getId(), order);
        activeCart.getCustomer().addOrder(order);
        activeCart.clear();

        System.out.println("\n  ===== RECEIPT =================================");
        order.print();
        System.out.printf("  [✓] Order #%d confirmed. Thank you, %s!%n",
                order.getId(), order.getCustomer().getName());
        return true;
    }

    // ─── 2. RETURN ────────────────────────────

    private static void menuReturn() {
        System.out.println("\n  -- PROCESS RETURN --------------------------------");
        System.out.print("  Enter Order ID: ");
        Order order = orders.get(readInt());
        if (order == null) { System.out.println("  [!] Order not found."); return; }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.out.println("  [!] Cannot return a cancelled order."); return;
        }

        order.print();
        List<OrderItem> items = order.getItems();
        System.out.println("  Items eligible for return:");
        for (int i = 0; i < items.size(); i++)
            System.out.printf("  %2d. %s%n", i + 1, items.get(i));

        System.out.print("  Select item number (0 to cancel): ");
        int idx = readInt();
        if (idx < 1 || idx > items.size()) return;
        OrderItem item = items.get(idx - 1);

        System.out.printf("  Return how many? (max %d): ", item.getQuantity());
        int qty = readInt();
        if (qty < 1 || qty > item.getQuantity()) { System.out.println("  [!] Invalid quantity."); return; }

        System.out.print("  Reason for return: ");
        String reason = readLine();

        item.getBook().restoreStock(qty);
        order.markReturned();
        ReturnRecord rec = new ReturnRecord(order, item.getBook(), qty, reason);
        returns.add(rec);

        System.out.println("\n  [✓] Return processed:");
        System.out.println("  " + rec);
        System.out.printf("  Refund issued: $%.2f%n", rec.getRefundAmount());
    }

    // ─── 3. INVENTORY ─────────────────────────

    private static void menuInventory() {
        boolean stay = true;
        while (stay) {
            System.out.println("\n  -- INVENTORY MANAGEMENT ---------------------");
            System.out.println("    1. View all books");
            System.out.println("    2. Search books");
            System.out.println("    3. Browse by genre");
            System.out.println("    4. Low stock alert");
            System.out.println("    5. Add new book");
            System.out.println("    6. Edit book");
            System.out.println("    7. Remove book");
            System.out.println("    8. Restock a book");
            System.out.println("    9. Manage authors");
            System.out.println("    0. Back");
            System.out.print("  Choose: ");

            switch (readInt()) {
                case 1: System.out.println(); inventory.displayAll(); break;
                case 2: invSearch();     break;
                case 3: invByGenre();    break;
                case 4: invLowStock();   break;
                case 5: invAddBook();    break;
                case 6: invEditBook();   break;
                case 7: invRemoveBook(); break;
                case 8: invRestock();    break;
                case 9: menuAuthors();   break;
                case 0: stay = false;    break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    private static void invSearch() {
        System.out.print("  Search (title / author / ISBN): ");
        List<Book> res = inventory.search(readLine());
        if (res.isEmpty()) System.out.println("  No results found.");
        else res.forEach(System.out::println);
    }

    private static void invByGenre() {
        System.out.println("  Select genre:"); Genre.printAll();
        System.out.print("  Choice: ");
        Genre g = Genre.fromIndex(readInt());
        if (g == null) { System.out.println("  [!] Invalid genre."); return; }
        List<Book> res = inventory.findByGenre(g);
        if (res.isEmpty()) System.out.println("  No books in that genre.");
        else res.forEach(System.out::println);
    }

    private static void invLowStock() {
        System.out.print("  Low-stock threshold: ");
        int t = readInt();
        List<Book> res = inventory.getLowStock(t);
        if (res.isEmpty()) System.out.println("  All books are above threshold.");
        else { System.out.printf("  %d book(s) at or below %d units:%n", res.size(), t); res.forEach(System.out::println); }
    }

    private static void invAddBook() {
        System.out.println("  -- Add New Book --");
        System.out.print("  Title: "); String title = readLine();

        System.out.println("  Select author (or enter 0 to add a new one):");
        authors.values().forEach(System.out::println);
        System.out.print("  Author ID (0 = new): ");
        int aid = readInt();
        Author author;
        if (aid == 0) author = createAuthor();
        else {
            author = authors.get(aid);
            if (author == null) { System.out.println("  [!] Author not found."); return; }
        }
        System.out.print("  Price: $");         double price = readDouble();
        System.out.print("  Stock: ");           int stock    = readInt();
        System.out.println("  Genre:"); Genre.printAll();
        System.out.print("  Genre #: ");         Genre genre  = Genre.fromIndex(readInt());
        if (genre == null) { System.out.println("  [!] Invalid genre."); return; }
        System.out.print("  ISBN: ");            String isbn  = readLine();
        System.out.print("  Published year: ");  int year     = readInt();

        Book b = new Book(title, author, price, stock, genre, isbn, year);
        author.addBook(b);
        inventory.addBook(b);
        System.out.println("  [✓] Book added: " + b);
    }

    private static void invEditBook() {
        System.out.print("  Book ID to edit: ");
        Optional<Book> ob = inventory.findById(readInt());
        if (!ob.isPresent()) { System.out.println("  [!] Book not found."); return; }
        Book b = ob.get();
        System.out.println("  Current: " + b);
        System.out.println("    1. Change price");
        System.out.println("    2. Change stock");
        System.out.println("    3. Change title");
        System.out.print("  Choose: ");
        switch (readInt()) {
            case 1: System.out.print("  New price: $"); b.setPrice(readDouble()); System.out.println("  [✓] Price updated."); break;
            case 2: System.out.print("  New stock: ");  b.setStock(readInt());   System.out.println("  [✓] Stock updated."); break;
            case 3: System.out.print("  New title: ");  b.setTitle(readLine());  System.out.println("  [✓] Title updated."); break;
            default: System.out.println("  [!] Invalid option.");
        }
    }

    private static void invRemoveBook() {
        System.out.print("  Book ID to remove: ");
        if (inventory.removeBook(readInt())) System.out.println("  [✓] Book removed.");
        else System.out.println("  [!] Book not found.");
    }

    private static void invRestock() {
        System.out.print("  Book ID to restock: ");
        Optional<Book> ob = inventory.findById(readInt());
        if (!ob.isPresent()) { System.out.println("  [!] Book not found."); return; }
        System.out.printf("  Current stock: %d%n", ob.get().getStock());
        System.out.print("  Units to add: ");
        int qty = readInt();
        ob.get().restoreStock(qty);
        System.out.printf("  [✓] Restocked. New stock: %d%n", ob.get().getStock());
    }

    private static void menuAuthors() {
        System.out.println("\n  -- AUTHORS ------------------------------------");
        System.out.println("    1. List all authors");
        System.out.println("    2. Add new author");
        System.out.println("    0. Back");
        System.out.print("  Choose: ");
        switch (readInt()) {
            case 1: authors.values().forEach(System.out::println); break;
            case 2: createAuthor(); break;
        }
    }

    private static Author createAuthor() {
        System.out.print("  First name: ");  String fn  = readLine();
        System.out.print("  Last name: ");   String ln  = readLine();
        System.out.print("  Nationality: "); String nat = readLine();
        Author a = new Author(fn, ln, nat);
        authors.put(a.getId(), a);
        System.out.println("  [✓] Author added: " + a);
        return a;
    }

    // ─── 4. CUSTOMERS ─────────────────────────

    private static void menuCustomers() {
        boolean stay = true;
        while (stay) {
            System.out.println("\n  -- CUSTOMER MANAGEMENT ----------------------");
            System.out.println("    1. List all customers");
            System.out.println("    2. View customer order history");
            System.out.println("    3. Register new customer");
            System.out.println("    0. Back");
            System.out.print("  Choose: ");
            switch (readInt()) {
                case 1: customers.values().forEach(System.out::println); break;
                case 2: custOrderHistory(); break;
                case 3: registerCustomer(); break;
                case 0: stay = false; break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    private static void custOrderHistory() {
        System.out.print("  Customer ID: ");
        Customer c = customers.get(readInt());
        if (c == null) { System.out.println("  [!] Customer not found."); return; }
        System.out.println("  Orders for " + c.getName() + ":");
        if (c.getOrderHistory().isEmpty()) System.out.println("  No orders yet.");
        else c.getOrderHistory().forEach(Order::print);
    }

    private static Customer registerCustomer() {
        System.out.print("  Name: ");  String name  = readLine();
        System.out.print("  Email: "); String email = readLine();
        System.out.print("  Phone: "); String phone = readLine();
        Customer c = new Customer(name, email, phone);
        customers.put(c.getId(), c);
        System.out.println("  [✓] Customer registered: " + c);
        return c;
    }

    private static Customer selectOrCreateCustomer() {
        System.out.println("  Select customer:");
        System.out.println("    1. Existing customer");
        System.out.println("    2. Register new customer");
        System.out.println("    0. Cancel");
        System.out.print("  Choose: ");
        switch (readInt()) {
            case 1:
                customers.values().forEach(c -> System.out.printf("  [%d] %s%n", c.getId(), c.getName()));
                System.out.print("  Customer ID: ");
                Customer c = customers.get(readInt());
                if (c == null) { System.out.println("  [!] Customer not found."); return null; }
                return c;
            case 2: return registerCustomer();
            default: return null;
        }
    }

    // ─── 5. ORDERS ────────────────────────────

    private static void menuOrders() {
        boolean stay = true;
        while (stay) {
            System.out.println("\n  -- ORDER MANAGEMENT -------------------------");
            System.out.println("    1. View all orders");
            System.out.println("    2. View order details");
            System.out.println("    3. Update order status");
            System.out.println("    4. View all returns");
            System.out.println("    0. Back");
            System.out.print("  Choose: ");
            switch (readInt()) {
                case 1:
                    if (orders.isEmpty()) System.out.println("  No orders yet.");
                    else orders.values().forEach(Order::print);
                    break;
                case 2:
                    System.out.print("  Order ID: ");
                    Order o = orders.get(readInt());
                    if (o == null) System.out.println("  [!] Order not found.");
                    else o.print();
                    break;
                case 3: updateOrderStatus(); break;
                case 4:
                    if (returns.isEmpty()) System.out.println("  No returns on record.");
                    else returns.forEach(System.out::println);
                    break;
                case 0: stay = false; break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    private static void updateOrderStatus() {
        System.out.print("  Order ID: ");
        Order o = orders.get(readInt());
        if (o == null) { System.out.println("  [!] Order not found."); return; }
        System.out.println("  Current status: " + o.getStatus());
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++)
            System.out.printf("  %d. %s%n", i + 1, statuses[i]);
        System.out.print("  New status: ");
        int idx = readInt();
        if (idx < 1 || idx > statuses.length) { System.out.println("  [!] Invalid."); return; }
        o.setStatus(statuses[idx - 1]);
        System.out.println("  [✓] Status updated to " + o.getStatus());
    }

    // ─── 6. STATS ─────────────────────────────

    private static void menuStats() {
        new SalesStats(new ArrayList<>(orders.values()), returns).print();
    }

    // ─── SEED DATA ────────────────────────────

    private static void seedData() {
        Author tolkien = new Author("J.R.R.", "Tolkien",  "British");
        Author orwell  = new Author("George", "Orwell",   "British");
        Author rowling = new Author("J.K.",   "Rowling",  "British");
        Author hawking = new Author("Stephen","Hawking",  "British");
        authors.put(tolkien.getId(), tolkien);
        authors.put(orwell.getId(),  orwell);
        authors.put(rowling.getId(), rowling);
        authors.put(hawking.getId(), hawking);

        Book[] books = {
            new Book("The Lord of the Rings",                    tolkien, 24.99, 10, Genre.FANTASY, "978-0261103252", 1954),
            new Book("The Hobbit",                               tolkien, 12.99, 15, Genre.FANTASY, "978-0261102217", 1937),
            new Book("Animal Farm",                              orwell,   8.99, 20, Genre.FICTION, "978-0451526342", 1945),
            new Book("Nineteen Eighty-Four",                     orwell,   9.99,  5, Genre.FICTION, "978-0451524935", 1949),
            new Book("Harry Potter and the Philosopher's Stone", rowling, 14.99, 30, Genre.FANTASY, "978-0747532699", 1997),
            new Book("A Brief History of Time",                  hawking, 11.99,  3, Genre.SCIENCE, "978-0553380163", 1988)
        };
        for (Book b : books) { b.getAuthor().addBook(b); inventory.addBook(b); }

        Customer alice = new Customer("Alice Murphy", "alice@example.com", "+353-87-111-2222");
        Customer bob   = new Customer("Bob O'Brien",  "bob@example.com",   "+353-86-333-4444");
        customers.put(alice.getId(), alice);
        customers.put(bob.getId(),   bob);
    }

    // ─── INPUT HELPERS ────────────────────────

    private static int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double readDouble() {
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static String readLine() { return sc.nextLine().trim(); }
}