package final_project.server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import final_project.client.shared.MessService;

public class MessServiceImpl extends UnicastRemoteObject implements MessService {
    
    private List<String> feedbackList = new ArrayList<>();
    
    public MessServiceImpl() throws RemoteException {
        super();
        // --- CLEANED: NO DUMMY DATA ---
        // The list starts empty now.
    }

    @Override
    public String getDailyMenu() throws RemoteException {
        LocalDate today = LocalDate.now();
        DayOfWeek day = today.getDayOfWeek();
        String dayName = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        
        return "=== 🍛 MENU FOR " + dayName + " 🍛 ===\n" +
               "(Date: " + today + ")\n\n" +
               getMenuForDay(day);
    }

    private String getMenuForDay(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return "🌞 Breakfast: Ghee Pongal, Vada, Chutney\n" +
                       "🌞 Lunch:     Sambar Rice, Potato Fry, Curd\n" +
                       "🌚 Dinner:    Chapati, Dal Tadka, Milk";
            case TUESDAY:
                return "🌞 Breakfast: Poori, Aloo Masala\n" +
                       "🌞 Lunch:     Lemon Rice, Egg/Paneer Curry\n" +
                       "🌚 Dinner:    Dosa, Sambar, Coconut Chutney";
            case WEDNESDAY:
                return "🌞 Breakfast: Idli, Sambar, Kara Chutney\n" +
                       "🌞 Lunch:     Veg Biryani, Onion Raitha\n" +
                       "🌚 Dinner:    Parotta, Veg Kurma";
            case THURSDAY:
                return "🌞 Breakfast: Rava Upma, Kesari (Sweet)\n" +
                       "🌞 Lunch:     Rice, Rasam, Cabbage Poriyal\n" +
                       "🌚 Dinner:    Phulka, Mixed Veg Curry";
            case FRIDAY:
                return "🌞 Breakfast: Dosa, Tomato Chutney\n" +
                       "🌞 Lunch:     Full Meals (Rice, Kootu, Payasam)\n" +
                       "🌚 Dinner:    Fried Rice, Gobi Manchurian";
            case SATURDAY:
                return "🌞 Breakfast: Bread Omelette / Bread Jam\n" +
                       "🌞 Lunch:     Curd Rice, Pickle, Chips\n" +
                       "🌚 Dinner:    Noodles, Tomato Sauce";
            case SUNDAY:
                return "🌞 Breakfast: Masala Dosa\n" +
                       "🌞 Lunch:     FEAST: Chicken Biryani / Paneer Butter Masala\n" +
                       "🌚 Dinner:    Light Upma / Fruits";
            default:
                return "Kitchen is closed.";
        }
    }

    @Override
    public String submitFeedback(String name, String rating, String comment) throws RemoteException {
        String entry = "[" + name + "] " + rating + ": " + comment;
        feedbackList.add(entry);
        System.out.println("[RMI] New Feedback: " + entry);
        return "Feedback Received!";
    }

    @Override
    public List<String> getAllFeedback() throws RemoteException {
        return feedbackList;
    }
}