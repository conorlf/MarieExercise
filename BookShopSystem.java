import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ─────────────────────────────────────────────
//  ENUMS
// ─────────────────────────────────────────────

enum Genre {
    FICTION, NON_FICTION, SCIENCE, HISTORY, BIOGRAPHY,
    FANTASY, MYSTERY, ROMANCE, HORROR, SELF_HELP, CHILDREN
}

enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

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

    public Book(String title, Author author, double price, int stock, Genre genre, String isbn, int publishedYear) {
        this.id            = idCounter++;
        this.title         = title;
        this.author        = author;
        this.price         = price;
        this.stock         = stock;
        this.genre         = genre;
        this.isbn          = isbn;
        this.publishedYear = publishedYear;
    }

    // Getters & setters
    public int    getId()           { return id; }
    public String getTitle()        { return title; }
    public Author getAuthor()       { return author; }
    public double getPrice()        { return price; }
    public int    getStock()        { return stock; }
    public Genre  getGenre()        { return genre; }
    public String getIsbn()         { return isbn; }
    public int    getPublishedYear(){ return publishedYear; }

    public void setTitle(String title)         { this.title  = title;  }
    public void setPrice(double price)         { this.price  = price;  }
    public void setStock(int stock)            { this.stock  = stock;  }
    public void setGenre(Genre genre)          { this.genre  = genre;  }

    public boolean isAvailable()               { return stock > 0; }

    public boolean reduceStock(int qty) {
        if (qty > stock) return false;
        stock -= qty;
        return true;
    }

    public void restoreStock(int qty)          { stock += qty; }

    @Override
    public String toString() {
        return String.format("[Book #%d] \"%s\" by %s | €%.2f | Stock: %d | Genre: %s | ISBN: %s (%d)",
                id, title, author.getFullName(), price, stock, genre, isbn, publishedYear);
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
    private List<Book> books;

    public Author(String firstName, String lastName, String nationality) {
        this.id          = idCounter++;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.nationality = nationality;
        this.books       = new ArrayList<>();
    }

    public int    getId()          { return id; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getFullName()    { return firstName + " " + lastName; }
    public String getNationality() { return nationality; }
    public List<Book> getBooks()   { return Collections.unmodifiableList(books); }

    public void addBook(Book book) { books.add(book); }

    @Override
    public String toString() {
        return String.format("[Author #%d] %s (%s) — %d book(s)", id, getFullName(), nationality, books.size());
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
    private List<Order> orderHistory;

    public Customer(String name, String email, String phone) {
        this.id           = idCounter++;
        this.name         = name;
        this.email        = email;
        this.phone        = phone;
        this.orderHistory = new ArrayList<>();
    }

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<Order> getOrderHistory() { return Collections.unmodifiableList(orderHistory); }

    public void setName(String name)   { this.name  = name;  }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    void addOrder(Order order) { orderHistory.add(order); }

    @Override
    public String toString() {
        return String.format("[Customer #%d] %s | %s | %s | Orders: %d",
                id, name, email, phone, orderHistory.size());
    }
}

// ─────────────────────────────────────────────
//  ORDER ITEM
// ─────────────────────────────────────────────

class OrderItem {
    private final Book book;
    private int quantity;
    private final double unitPrice; // price locked at time of order

    public OrderItem(Book book, int quantity) {
        this.book      = book;
        this.quantity  = quantity;
        this.unitPrice = book.getPrice();
    }

    public Book   getBook()      { return book; }
    public int    getQuantity()  { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal()  { return unitPrice * quantity; }

    public void setQuantity(int qty) { this.quantity = qty; }

    @Override
    public String toString() {
        return String.format("  %-40s x%d  @ €%.2f = €%.2f",
                "\"" + book.getTitle() + "\"", quantity, unitPrice, getSubtotal());
    }
}

// ─────────────────────────────────────────────
//  ORDER
// ─────────────────────────────────────────────

class Order {
    private static int idCounter = 1;

    private final int id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDate orderDate;

    public Order(Customer customer) {
        this.id        = idCounter++;
        this.customer  = customer;
        this.items     = new ArrayList<>();
        this.status    = OrderStatus.PENDING;
        this.orderDate = LocalDate.now();
    }

    public int          getId()       { return id; }
    public Customer     getCustomer() { return customer; }
    public OrderStatus  getStatus()   { return status; }
    public LocalDate    getOrderDate(){ return orderDate; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void setStatus(OrderStatus status) { this.status = status; }

    public void addItem(OrderItem item) { items.add(item); }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╔══ Order #%d ══════════════════════════════════════╗%n", id));
        sb.append(String.format("  Customer : %s%n", customer.getName()));
        sb.append(String.format("  Date     : %s%n", orderDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))));
        sb.append(String.format("  Status   : %s%n", status));
        sb.append("  Items:%n");
        items.forEach(i -> sb.append(i).append("\n"));
        sb.append(String.format("  ─────────────────────────────────────────────────%n"));
        sb.append(String.format("  TOTAL    : €%.2f%n", getTotal()));
        sb.append("╚══════════════════════════════════════════════════╝");
        return sb.toString();
    }
}

// ─────────────────────────────────────────────
//  SHOPPING CART
// ─────────────────────────────────────────────

class ShoppingCart {
    private final Customer customer;
    private final Map<Integer, OrderItem> items; // bookId -> OrderItem

    public ShoppingCart(Customer customer) {
        this.customer = customer;
        this.items    = new LinkedHashMap<>();
    }

    public Customer getCustomer() { return customer; }

    public void addBook(Book book, int qty) {
        if (!book.isAvailable()) {
            System.out.println("  ✗ \"" + book.getTitle() + "\" is out of stock.");
            return;
        }
        if (items.containsKey(book.getId())) {
            items.get(book.getId()).setQuantity(items.get(book.getId()).getQuantity() + qty);
        } else {
            items.put(book.getId(), new OrderItem(book, qty));
        }
        System.out.println("  ✓ Added " + qty + "x \"" + book.getTitle() + "\" to cart.");
    }

    public void removeBook(int bookId) {
        if (items.remove(bookId) != null)
            System.out.println("  ✓ Removed book #" + bookId + " from cart.");
        else
            System.out.println("  ✗ Book #" + bookId + " not found in cart.");
    }

    public void clear() { items.clear(); }

    public double getTotal() {
        return items.values().stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public Collection<OrderItem> getItems() { return items.values(); }

    public void display() {
        if (isEmpty()) { System.out.println("  Cart is empty."); return; }
        System.out.println("  ── Cart for " + customer.getName() + " ──────────────────────");
        items.values().forEach(System.out::println);
        System.out.printf("  Total: €%.2f%n", getTotal());
    }
}

// ─────────────────────────────────────────────
//  INVENTORY
// ─────────────────────────────────────────────

class Inventory {
    private final Map<Integer, Book> books = new LinkedHashMap<>();

    public void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public Optional<Book> findById(int id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> findByTitle(String keyword) {
        String kw = keyword.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : books.values())
            if (b.getTitle().toLowerCase().contains(kw)) result.add(b);
        return result;
    }

    public List<Book> findByAuthor(String name) {
        String kw = name.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : books.values())
            if (b.getAuthor().getFullName().toLowerCase().contains(kw)) result.add(b);
        return result;
    }

    public List<Book> findByGenre(Genre genre) {
        List<Book> result = new ArrayList<>();
        for (Book b : books.values())
            if (b.getGenre() == genre) result.add(b);
        return result;
    }

    public List<Book> getAvailable() {
        List<Book> result = new ArrayList<>();
        for (Book b : books.values())
            if (b.isAvailable()) result.add(b);
        return result;
    }

    public void displayAll() {
        if (books.isEmpty()) { System.out.println("  No books in inventory."); return; }
        books.values().forEach(System.out::println);
    }

    public boolean removeBook(int id) { return books.remove(id) != null; }

    public int size() { return books.size(); }
}

// ─────────────────────────────────────────────
//  BOOKSHOP  (main service / facade)
// ─────────────────────────────────────────────

class BookShop {
    private final String name;
    private final Inventory inventory;
    private final Map<Integer, Customer>  customers = new LinkedHashMap<>();
    private final Map<Integer, Author>    authors   = new LinkedHashMap<>();
    private final Map<Integer, Order>     orders    = new LinkedHashMap<>();

    public BookShop(String name) {
        this.name      = name;
        this.inventory = new Inventory();
    }

    // ── Author management ──────────────────────

    public Author registerAuthor(String firstName, String lastName, String nationality) {
        Author a = new Author(firstName, lastName, nationality);
        authors.put(a.getId(), a);
        return a;
    }

    // ── Book management ────────────────────────

    public Book addBook(String title, Author author, double price, int stock,
                        Genre genre, String isbn, int year) {
        Book b = new Book(title, author, price, stock, genre, isbn, year);
        author.addBook(b);
        inventory.addBook(b);
        return b;
    }

    public boolean restockBook(int bookId, int qty) {
        return inventory.findById(bookId).map(b -> { b.restoreStock(qty); return true; }).orElse(false);
    }

    // ── Customer management ────────────────────

    public Customer registerCustomer(String name, String email, String phone) {
        Customer c = new Customer(name, email, phone);
        customers.put(c.getId(), c);
        return c;
    }

    public Optional<Customer> findCustomer(int id) {
        return Optional.ofNullable(customers.get(id));
    }

    // ── Cart & checkout ────────────────────────

    public ShoppingCart createCart(Customer customer) {
        return new ShoppingCart(customer);
    }

    public Order checkout(ShoppingCart cart) {
        if (cart.isEmpty()) {
            System.out.println("  ✗ Cannot checkout — cart is empty.");
            return null;
        }

        Order order = new Order(cart.getCustomer());

        for (OrderItem item : cart.getItems()) {
            if (!item.getBook().reduceStock(item.getQuantity())) {
                System.out.printf("  ✗ Insufficient stock for \"%s\" (requested %d, available %d).%n",
                        item.getBook().getTitle(), item.getQuantity(), item.getBook().getStock());
                // Restore any already-reduced stock for this order
                order.getItems().forEach(i -> i.getBook().restoreStock(i.getQuantity()));
                return null;
            }
            order.addItem(item);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orders.put(order.getId(), order);
        cart.getCustomer().addOrder(order);
        cart.clear();

        System.out.println("  ✓ Order #" + order.getId() + " confirmed for " + order.getCustomer().getName());
        return order;
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order o = orders.get(orderId);
        if (o == null) return false;
        o.setStatus(newStatus);
        return true;
    }

    // ── Reporting ──────────────────────────────

    public void printInventory()  { System.out.println("\n═══ INVENTORY ═══════════════════════════════"); inventory.displayAll(); }

    public void printOrders() {
        System.out.println("\n═══ ALL ORDERS ══════════════════════════════");
        if (orders.isEmpty()) { System.out.println("  No orders yet."); return; }
        orders.values().forEach(o -> System.out.println(o + "\n"));
    }

    public void printCustomers() {
        System.out.println("\n═══ CUSTOMERS ═══════════════════════════════");
        if (customers.isEmpty()) { System.out.println("  No customers registered."); return; }
        customers.values().forEach(System.out::println);
    }

    public Inventory getInventory() { return inventory; }
    public String getName()         { return name; }
}

// ─────────────────────────────────────────────
//  MAIN — demo / smoke test
// ─────────────────────────────────────────────

public class BookShopSystem {

    public static void main(String[] args) {

        BookShop shop = new BookShop("Clover Books");
        System.out.println("Welcome to " + shop.getName() + "!\n");

        // ── Authors ──────────────────────────────
        Author tolkien  = shop.registerAuthor("J.R.R.", "Tolkien",  "British");
        Author orwell   = shop.registerAuthor("George", "Orwell",   "British");
        Author rowling  = shop.registerAuthor("J.K.",   "Rowling",  "British");
        Author hawking  = shop.registerAuthor("Stephen","Hawking",  "British");

        // ── Books ────────────────────────────────
        Book lotr    = shop.addBook("The Lord of the Rings",   tolkien,  24.99, 10, Genre.FANTASY,     "978-0261103252", 1954);
        Book hobbit  = shop.addBook("The Hobbit",              tolkien,  12.99, 15, Genre.FANTASY,     "978-0261102217", 1937);
        Book farm    = shop.addBook("Animal Farm",             orwell,    8.99, 20, Genre.FICTION,     "978-0451526342", 1945);
        Book g1984   = shop.addBook("Nineteen Eighty-Four",   orwell,    9.99,  5, Genre.FICTION,     "978-0451524935", 1949);
        Book hp1     = shop.addBook("Harry Potter and the Philosopher's Stone", rowling, 14.99, 30, Genre.FANTASY, "978-0747532699", 1997);
        Book brief   = shop.addBook("A Brief History of Time", hawking,  11.99,  3, Genre.SCIENCE,    "978-0553380163", 1988);

        // ── Customers ────────────────────────────
        Customer alice = shop.registerCustomer("Alice Murphy",  "alice@example.com",  "+353-87-111-2222");
        Customer bob   = shop.registerCustomer("Bob O'Brien",   "bob@example.com",    "+353-86-333-4444");

        // ── Print initial inventory ──────────────
        shop.printInventory();

        // ── Alice fills her cart & checks out ────
        System.out.println("\n─── Alice's shopping session ────────────────");
        ShoppingCart aliceCart = shop.createCart(alice);
        aliceCart.addBook(lotr,   1);
        aliceCart.addBook(hp1,    2);
        aliceCart.addBook(brief,  1);
        aliceCart.display();
        Order order1 = shop.checkout(aliceCart);

        // ── Bob fills his cart & checks out ──────
        System.out.println("\n─── Bob's shopping session ──────────────────");
        ShoppingCart bobCart = shop.createCart(bob);
        bobCart.addBook(farm,   3);
        bobCart.addBook(g1984,  1);
        bobCart.addBook(hobbit, 2);
        bobCart.display();
        Order order2 = shop.checkout(bobCart);

        // ── Advance order statuses ────────────────
        if (order1 != null) shop.updateOrderStatus(order1.getId(), OrderStatus.SHIPPED);
        if (order2 != null) shop.updateOrderStatus(order2.getId(), OrderStatus.DELIVERED);

        // ── Search demo ──────────────────────────
        System.out.println("\n─── Search: genre FANTASY ────────────────────");
        shop.getInventory().findByGenre(Genre.FANTASY).forEach(System.out::println);

        System.out.println("\n─── Search: author 'Orwell' ──────────────────");
        shop.getInventory().findByAuthor("Orwell").forEach(System.out::println);

        // ── Reports ───────────────────────────────
        shop.printOrders();
        shop.printCustomers();
        shop.printInventory();

        // ── Customer order history ─────────────────
        System.out.println("\n─── Alice's order history ────────────────────");
        alice.getOrderHistory().forEach(System.out::println);
    }
}