package design_patterns;

import java.util.ArrayList;
import java.util.List;

// Subject interface - defines contract for observable objects
interface Subject{
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

interface Observer{
    void update(String news);
}

class NewsAgency implements Subject{
    List<Observer> observers = new ArrayList<>();
    private String news;

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers){
            observer.update(news);
        }
    }

    public void setNews(String news){
        this.news = news;
        notifyObservers();
    }
}

class NewsChannel implements Observer{
    private final String name;

    public NewsChannel(String name){
        this.name = name;
    }

    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}

public class ObserverPattern {
    public static void main(String[] args) {
        System.out.println("Observer Pattern Demo");
        System.out.println("====================\n");

        // Create news agency
        NewsAgency agency = new NewsAgency();

        // Create news channels
        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");
        NewsChannel fox = new NewsChannel("FOX");

        // Subscribe channels
        System.out.println("1. Subscribing channels...");
        agency.attach(cnn);
        agency.attach(bbc);
        agency.attach(fox);

        // Publish news
        System.out.println("\n2. Publishing breaking news:");
        agency.setNews("Election results announced!");

        // Unsubscribe one channel
        System.out.println("\n3. BBC unsubscribes...");
        agency.detach(bbc);

        System.out.println("\n4. Publishing sports news:");
        agency.setNews("World Cup final starts!");

        // Add new channel
        System.out.println("\n5. NBC joins...");
        NewsChannel nbc = new NewsChannel("NBC");
        agency.attach(nbc);

        System.out.println("\n6. Publishing tech news:");
        agency.setNews("AI breakthrough announced!");

        System.out.println("\n✅ Observer Pattern working perfectly!");
    }
}