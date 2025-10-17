import java.util.*;

interface TravelStrategy {
    double calculate(double distance, int passengers, String travelClass, boolean discount);
}

class Plane implements TravelStrategy {
    public double calculate(double distance, int passengers, String travelClass, boolean discount) {
        double cost = distance * 0.5;
        if(travelClass.equalsIgnoreCase("business")) cost *= 2;
        if(discount) cost *= 0.8;
        return cost * passengers;
    }
}

class Train implements TravelStrategy {
    public double calculate(double distance, int passengers, String travelClass, boolean discount) {
        double cost = distance * 0.2;
        if(travelClass.equalsIgnoreCase("business")) cost *= 1.5;
        if(discount) cost *= 0.9;
        return cost * passengers;
    }
}

class Bus implements TravelStrategy {
    public double calculate(double distance, int passengers, String travelClass, boolean discount) {
        double cost = distance * 0.1;
        if(travelClass.equalsIgnoreCase("business")) cost *= 1.2;
        if(discount) cost *= 0.95;
        return cost * passengers;
    }
}

class TravelContext {
    private TravelStrategy strategy;
    public void setStrategy(TravelStrategy s) { strategy = s; }
    public double calculate(double distance, int passengers, String travelClass, boolean discount) {
        return strategy.calculate(distance, passengers, travelClass, discount);
    }
}

interface StockObserver {
    void update(String stock, double price);
}

interface StockSubject {
    void register(String stock, StockObserver o);
    void remove(String stock, StockObserver o);
    void notify(String stock);
}

class StockExchange implements StockSubject {
    private Map<String, Double> prices = new HashMap<>();
    private Map<String, List<StockObserver>> observers = new HashMap<>();

    public void setPrice(String stock, double price) {
        prices.put(stock, price);
        notify(stock);
    }

    public void register(String stock, StockObserver o) {
        observers.computeIfAbsent(stock, k -> new ArrayList<>()).add(o);
    }

    public void remove(String stock, StockObserver o) {
        if(observers.containsKey(stock)) observers.get(stock).remove(o);
    }

    public void notify(String stock) {
        if(observers.containsKey(stock)) {
            for(StockObserver o : observers.get(stock)) o.update(stock, prices.get(stock));
        }
    }
}

class Trader implements StockObserver {
    private String name;
    public Trader(String n) { name = n; }
    public void update(String stock, double price) {
        System.out.println(name + " получил: " + stock + " = " + price);
    }
}

class AutoTrader implements StockObserver {
    private String name;
    private double threshold;
    public AutoTrader(String n, double t) { name = n; threshold = t; }
    public void update(String stock, double price) {
        System.out.println(name + " проверяет " + stock + ": " + price);
        if(price > threshold) System.out.println(name + " продает " + stock);
        else System.out.println(name + " покупает " + stock);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TravelContext travel = new TravelContext();
        System.out.println("Выберите транспорт: 1-П Plane, 2-Т Train, 3-B Bus");
        String c = sc.nextLine();
        if(c.equals("1")) travel.setStrategy(new Plane());
        else if(c.equals("2")) travel.setStrategy(new Train());
        else travel.setStrategy(new Bus());

        System.out.println("Расстояние:");
        double distance = sc.nextDouble();
        System.out.println("Пассажиры:");
        int passengers = sc.nextInt();
        sc.nextLine();
        System.out.println("Класс (economy/business):");
        String tClass = sc.nextLine();
        System.out.println("Скидка (true/false):");
        boolean discount = sc.nextBoolean();

        double cost = travel.calculate(distance, passengers, tClass, discount);
        System.out.println("Стоимость поездки: " + cost);

        StockExchange exchange = new StockExchange();
        Trader t1 = new Trader("Иван");
        AutoTrader t2 = new AutoTrader("Робот", 100);

        exchange.register("AAPL", t1);
        exchange.register("AAPL", t2);
        exchange.register("GOOG", t2);

        exchange.setPrice("AAPL", 120);
        exchange.setPrice("GOOG", 90);
        exchange.setPrice("AAPL", 80);
    }
}
