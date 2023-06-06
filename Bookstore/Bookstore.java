


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;



class Customer{

    public static void buyBook(int ThreadId, int overall_thread_count) throws InterruptedException, IOException {
        if(Bookstore.customer_print_statements) {
            Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer " + ThreadId + " has entered the bookstore");
            Bookstore.buffer.newLine();
            System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer " + ThreadId + " has entered the bookstore"  );
        }
        // pick random genre to buy
        Random rand2 = new Random();
        String randomGenre = Bookstore.book_genres.get(rand2.nextInt(Bookstore.book_genres.size()));
        int wait_ticks = Bookstore.tick_count;
        // wait if no books available
        synchronized(Bookstore.my_monitor_customer_waiting.get(randomGenre)){
            while(Bookstore.book_shelves.get(randomGenre) == 0) {
                Bookstore.customer_waiting.put(randomGenre, Bookstore.customer_waiting.get(randomGenre) + 1);
                if(Bookstore.prioritize_stocking_statements){
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Genre " + randomGenre + " has a waiting list of " + Bookstore.customer_waiting.get(randomGenre));
                    Bookstore.buffer.newLine();
                    System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Genre " + randomGenre + " has a waiting list of " + Bookstore.customer_waiting.get(randomGenre));
                }
                Bookstore.my_monitor_customer_waiting.get(randomGenre).wait();
            }
        }
        // book is available and customer will buy the book
        if(Bookstore.customer_print_statements) {
            int tick_difference = Bookstore.tick_count - wait_ticks;
            if(tick_difference == 0) {
                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer" + ThreadId + " will buy book from genre " + randomGenre);
                Bookstore.buffer.newLine();
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer" + ThreadId + " will buy book from genre " + randomGenre);
            } else{
                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer" + ThreadId + " will buy book from genre " + randomGenre + "having waited " + tick_difference + " ticks");
                Bookstore.buffer.newLine();
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Customer" + ThreadId + " will buy book from genre " + randomGenre + "having waited " + tick_difference + " ticks");
            }
        }
        // increase bookstore money by book price if money system on
        if(Bookstore.money_system == true) {
            double price = Bookstore.genre_prices.get(randomGenre) + (Bookstore.genre_prices.get(randomGenre) * Bookstore.profit_percentage / 100);
            DecimalFormat df = new DecimalFormat("0.00");
            price = Double.valueOf(df.format(price));
            Bookstore.bookstore_money += Bookstore.genre_prices.get(randomGenre) + price;
            if(Bookstore.money_system_print_statements == true){
                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " customer " + ThreadId + " bought " + randomGenre + " costing " + price + " Bookstore now has " + Bookstore.bookstore_money + " money" );
                Bookstore.buffer.newLine();
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " customer " + ThreadId + " bought " + randomGenre + " costing " + price + " Bookstore now has " + Bookstore.bookstore_money + " money" );
            }
        }
        // take away from book shelve for that genre
        Bookstore.book_shelves.put(randomGenre, Bookstore.book_shelves.get(randomGenre) - 1);
    }
}

class CustomerThread extends Thread{
    @Override
    public void run(){
        // run function to buy book for customer.
        Bookstore.buy_thread_id += 1;
        Bookstore.overall_thread_count += 1;
        try {
            // customer will buy book.
            Customer.buyBook(Bookstore.buy_thread_id, Bookstore.overall_thread_count);
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
}

class Assistant{

    private static final Map<String, Integer> put_books_back = new ConcurrentHashMap<>();

    private static Map<String, Integer> take_books = new ConcurrentHashMap<>();

    private static Map<String, Integer> tmp_book_box = new ConcurrentHashMap<>();

    // shared resource
    static Map<Integer,Map<String, Integer>> assistants_take_books = new HashMap<>();

    private static ThreadLocal<HashMap<String, Integer>> put_books_backTL = new ThreadLocal<HashMap<String, Integer>>() {
        @Override
        protected HashMap<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    private static ThreadLocal<HashMap<String, Integer>> take_booksTL = new ThreadLocal<HashMap<String, Integer>>() {
        @Override
        protected HashMap<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    private static ThreadLocal<HashMap<String, Integer>> tmp_book_boxTL = new ThreadLocal<HashMap<String, Integer>>() {
        @Override
        protected HashMap<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    private static ThreadLocal<HashMap<String, Integer>> book_box_tmpTL = new  ThreadLocal<HashMap<String, Integer>>(){
        @Override
        protected HashMap<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    static ReentrantLock lock = new ReentrantLock();




    public static void stockShelves(int ThreadId, int overall_thread_id) throws InterruptedException, IOException {
        int iy = 0;
        while(Bookstore.assistant_can_stock) {



            // reset hashmaps
            take_books = new HashMap<>();

            tmp_book_box = new HashMap<>();


            int boxCount = 0;
            // check if book box is empty + wait until non empty
            for (Integer value : Bookstore.book_box.values()) {
                if (value <= 0) {
                    boxCount += 1;
                }
            }

            // book box is empty assistant will wait
            if (boxCount == Bookstore.book_box.size()) {
                // stop looking for values
                synchronized(Bookstore.monitor_assistant_box_empty.get(ThreadId)){
                    Bookstore.monitor_assistant_box_empty.get(ThreadId).wait();
                }
            }
            // make sure only one assistant taking from box
            synchronized (Bookstore.my_monitor_assistants.get(ThreadId)) {
                if(!Bookstore.AssistantsbookThreadIndex.contains(ThreadId)){
                    Bookstore.AssistantsbookThreadIndex.add(ThreadId);
                }
                if(Bookstore.AssistantsbookThreadIndex.size() == Bookstore.number_of_assistants - Bookstore.assistant_break_count){
                    // prioritize books where high customer waiting list
                    tmp_book_boxTL.get().putAll(Bookstore.customer_waiting);
                    int numberOfSections = 0;
                    ArrayList<String> bookSectionsIndexed = new ArrayList<>();
                    for(String key : Bookstore.book_box.keySet()){
                        if(Bookstore.book_box.get(key) == 0 && tmp_book_boxTL.get().containsKey(key)){
                            tmp_book_boxTL.get().remove(key);
                        } else if(Bookstore.book_box.get(key) > 0 && tmp_book_boxTL.get().containsKey(key)){
                            tmp_book_boxTL.get().put(key, Bookstore.book_box.get(key));
                            numberOfSections += 1;
                            bookSectionsIndexed.add(key);
                        }
                    }
                    int assistantIndex = 0;
                    int bookSections = 0;
                    Map<String, Integer> assistants_take_books_tmp = new HashMap<>();
                    Map<String, Integer> book_box_tmp = new HashMap<>();
                    // create temp book box.
                    book_box_tmp.putAll(tmp_book_boxTL.get());
                    assistants_take_books.clear();
                    // keep looping until book box is empty
                    while (!book_box_tmp.isEmpty()) {
                        assistants_take_books_tmp = assistants_take_books.get(Bookstore.AssistantsbookThreadIndex.get(assistantIndex));
                        if (assistants_take_books_tmp == null) {
                            assistants_take_books_tmp = new HashMap<>();
                        }
                        // ensure books are available for that genre.
                        boolean nobooksavailable = false;
                        if (assistants_take_books_tmp.containsKey(bookSectionsIndexed.get(bookSections))) {
                            if (book_box_tmp.containsKey(bookSectionsIndexed.get(bookSections)) && book_box_tmp.get(bookSectionsIndexed.get(bookSections)) > 0) {
                                // put books for that assistant, and take away from tmp box.
                                assistants_take_books_tmp.put(bookSectionsIndexed.get(bookSections), assistants_take_books_tmp.get(bookSectionsIndexed.get(bookSections)) + 1);
                                book_box_tmp.put(bookSectionsIndexed.get(bookSections), book_box_tmp.get(bookSectionsIndexed.get(bookSections)) - 1);
                                Bookstore.book_box.put(bookSectionsIndexed.get(bookSections), Bookstore.book_box.get(bookSectionsIndexed.get(bookSections)) - 1);
                            } else {
                                nobooksavailable = true;
                            }
                        } else {
                            if (book_box_tmp.containsKey(bookSectionsIndexed.get(bookSections)) && book_box_tmp.get(bookSectionsIndexed.get(bookSections)) > 0) {
                                // put books for that assistant, and take away from tmp box.
                                assistants_take_books_tmp.put(bookSectionsIndexed.get(bookSections), 1);
                                Bookstore.book_box.put(bookSectionsIndexed.get(bookSections), Bookstore.book_box.get(bookSectionsIndexed.get(bookSections)) - 1);
                                book_box_tmp.put(bookSectionsIndexed.get(bookSections), book_box_tmp.get(bookSectionsIndexed.get(bookSections)) - 1);
                            } else {
                                nobooksavailable = true;
                            }
                        }
                        // remove from temp box if available.
                        if (book_box_tmp.containsKey(bookSectionsIndexed.get(bookSections)) && book_box_tmp.get(bookSectionsIndexed.get(bookSections)) == 0) {
                            book_box_tmp.remove(bookSectionsIndexed.get(bookSections));
                        }
                        // if no books avaialble check next book section, keep assistant index as current
                        if (nobooksavailable == false) {
                            assistants_take_books.put(Bookstore.AssistantsbookThreadIndex.get(assistantIndex), assistants_take_books_tmp);
                            if (assistantIndex + 1 >= (Bookstore.number_of_assistants - Bookstore.assistant_break_count)) {
                                assistantIndex = 0;
                            } else {
                                assistantIndex += 1;
                            }
                        }
                        bookSections += 1;
                        if (bookSections >= numberOfSections) {
                            bookSections = 0;
                        }
                    }
                    int iur = 1;
                    // assistant on break tmp books
                    Map<String, Integer> assistants_take_books_tmp3 = new HashMap<>();
                    assistants_take_books_tmp3.putAll(assistants_take_books_tmp);
                    // for books where assistant is on break divide them to other assistants.
                    while (iur <= Bookstore.number_of_assistants){
                        if(!assistants_take_books.containsKey(iur)) {
                            Set<String> setTmp = assistants_take_books_tmp.keySet();
                            for (String x : setTmp) {
                                assistants_take_books_tmp.put(x, 0);
                            }
                            assistants_take_books.put(iur, assistants_take_books_tmp);
                            // condition for assistants on break
                            if (Bookstore.assistant_is_break == true) {
                                int uri = 1;
                                // check if assistant has already taken similar genres
                                Map<String, Integer> assistants_take_books_tmp2 = new HashMap<>();
                                boolean added = false;
                                while (uri < assistants_take_books.size()) {
                                    if (assistants_take_books.get(uri).keySet().equals(assistants_take_books_tmp3.keySet()) && iur != uri) {
                                        assistants_take_books_tmp2.putAll(assistants_take_books.get(uri));
                                        Set<String> setTmp2 = assistants_take_books_tmp2.keySet();
                                        for (String y : setTmp2) {
                                            assistants_take_books_tmp2.put(y, assistants_take_books_tmp2.get(y) + assistants_take_books_tmp3.get(y));
                                        }
                                        assistants_take_books.put(uri, assistants_take_books_tmp2);
                                        added = true;
                                        uri = assistants_take_books.size();
                                    }
                                    uri += 1;
                                }
                                if(added == false){
                                    int uri2 = 0;
                                    while (uri2 < assistants_take_books.size()){
                                        if(iur != uri){
                                            assistants_take_books_tmp2.putAll(assistants_take_books.get(uri));
                                            Set<String> setTmp2 = assistants_take_books_tmp2.keySet();
                                            for (String y : setTmp2) {
                                                assistants_take_books_tmp2.put(y, assistants_take_books_tmp2.get(y) + assistants_take_books_tmp3.get(y));
                                            }
                                            assistants_take_books.put(uri, assistants_take_books_tmp2);
                                            added = true;
                                            uri = assistants_take_books.size();
                                        }
                                        uri2 += 1;
                                    }
                                }
                            }
                        }
                        iur += 1;
                    }
                    Bookstore.bookstore_assistant_notify = 1;
                    // if assistant has taken a break if statement wont notify anybody else
                    int yr = 1;
                    Bookstore.AssistantsbookThreadIndex.clear();
                    for (int iw = 0; iw < Bookstore.number_of_assistants; iw++) {
                        Bookstore.my_monitor_assistants.put(iw + 1 , "random");
                    }
                } else {
                    Bookstore.my_monitor_assistants.get(ThreadId).wait();
                }
            }

            take_booksTL.get().clear();
            put_books_backTL.get().clear();




            // get highest customer waiting list
            boolean maxGenre = false;
            Map<String,Integer> customerWaitingTmp = new HashMap<>();
            // loop through hashmap
            customerWaitingTmp.putAll(Bookstore.customer_waiting);
            String maxGenres = "";
            while (maxGenre == false){
                // if customers are waiting prioritize them
                if(customerWaitingTmp.size() > 0) {
                    // select genres first with most customers waiting.
                    maxGenres = customerWaitingTmp.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                    if(Bookstore.prioritize_stocking_statements){
                        Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + "Assistant " + ThreadId + " began prioritizing stocking " + maxGenres + " section as they have the highest waiting list.");
                        Bookstore.buffer.newLine();
                        System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + "Assistant " + ThreadId + " began prioritizing stocking " + maxGenres + " section as they have the highest waiting list.");
                    }
                }
                while (!assistants_take_books.get(ThreadId).containsKey(maxGenres)) {
                    // if no customers waiting genre select genre from genres divided.
                    Set<String> setTmp = assistants_take_books.get(ThreadId).keySet();
                    for (String x : setTmp) {
                        maxGenres = x;
                    }
                }
                for (int ii = 0; ii < assistants_take_books.size(); ii++) {
                    if (!assistants_take_books.get(ThreadId).isEmpty() && assistants_take_books.get(ThreadId).containsKey(maxGenres)) {
                        take_booksTL.get().clear();
                        // put books into threadlocal hashmap for indexed thread of books divided between assistants.
                        take_booksTL.get().putAll(assistants_take_books.get(ThreadId));
                        maxGenre = true;
                    }
                }
                customerWaitingTmp.remove(maxGenres);
            }
            // notify next assistant so only one assistant taking book
            if(Bookstore.my_monitor_assistants.get(Bookstore.bookstore_assistant_notify) != null) {
                synchronized (Bookstore.my_monitor_assistants.get(Bookstore.bookstore_assistant_notify)) {
                    if (Bookstore.my_monitor_assistants.containsKey(Bookstore.bookstore_assistant_notify)) {
                        Bookstore.my_monitor_assistants.get(Bookstore.bookstore_assistant_notify).notify();
                        Bookstore.bookstore_assistant_notify += 1;
                    }
                }
            }
            // if capacity is reached
            int booksBackCount = 0;
            for (String book_key : take_booksTL.get().keySet()) {
                // walks to section
                if(take_booksTL.get().get(book_key) > 0) {
                    int ogBookstoreticks = Bookstore.tick_count;
                    while(Bookstore.tick_count < ogBookstoreticks + (10 + take_booksTL.get().get(book_key))){
                        Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                    }
                    if (Bookstore.assistant_print_statements_general) {
                        Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant " + ThreadId + " began stocking" + book_key + " section with " + take_booksTL.get().get(book_key) + " books");
                        Bookstore.buffer.newLine();
                        System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " began stocking" + book_key + " section with " + take_booksTL.get().get(book_key) + " books");
                    }
                    // stock books into shelves
                    ogBookstoreticks = Bookstore.tick_count;
                    while(Bookstore.tick_count < ogBookstoreticks + take_booksTL.get().get(book_key)){
                        Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                    }
                    synchronized ("synch") {
                        if (take_booksTL.get().get(book_key) + Bookstore.book_shelves.get(book_key) > Bookstore.book_shelves_capacity.get(book_key)) {
                            int booksToShelve = Bookstore.book_shelves_capacity.get(book_key) - Bookstore.book_shelves.get(book_key);
                            int booksPutBack = (take_booksTL.get().get(book_key) + Bookstore.book_shelves.get(book_key)) - Bookstore.book_shelves_capacity.get(book_key) ;
                            put_books_back.put(book_key, booksPutBack);
                            put_books_backTL.get().put(book_key, booksPutBack);
                            booksBackCount += booksPutBack;
                            if (Bookstore.assistant_bookshelve_capacity_statements) {
                                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id  + " shelve capacity for " + book_key + " has reached capacity of " + Bookstore.book_shelves_capacity.get(book_key) + " it had " + Bookstore.book_shelves.get(book_key) + " books in its shelve. It will store " + booksToShelve + " books.");
                                Bookstore.buffer.newLine();
                                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " shelve capacity for " + book_key + " has reached capacity of " + Bookstore.book_shelves_capacity.get(book_key) + " it had " + Bookstore.book_shelves.get(book_key) + " books in its shelve. It will store " + booksToShelve + " books.");
                            }
                            Bookstore.book_shelves.put(book_key, booksToShelve);
                        } else {
                            // add to bookshelve
                            Bookstore.book_shelves.put(book_key, Bookstore.book_shelves.get(book_key) + take_booksTL.get().get(book_key));
                        }
                    }
                    if (Bookstore.assistant_print_statements_general) {
                        if (put_books_backTL.get().size() > 0 && put_books_backTL.get().containsKey(book_key) ) {
                            Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " finished stocking " + book_key + " section with " + (take_booksTL.get().get(book_key) - put_books_backTL.get().get(book_key)) + " books");
                            Bookstore.buffer.newLine();
                            System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " finished stocking " + book_key + " section with " + (take_booksTL.get().get(book_key) - put_books_backTL.get().get(book_key)) + " books");
                        } else {
                            Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " finished stocking " + book_key + " section with " + (take_booksTL.get().get(book_key)) + " books");
                            Bookstore.buffer.newLine();
                            System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " finished stocking " + book_key + " section with " + (take_booksTL.get().get(book_key)) + " books");
                        }
                    }
                }
            }
            int ogBookstoreTick2 = Bookstore.tick_count;
            if (Bookstore.assistant_print_statements_general) {
                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " is returning to the book box.");
                Bookstore.buffer.newLine();
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " Assistant" + ThreadId + " is returning to the book box.");
            }
            // finished stocking shelves, goes back to box
            while(Bookstore.tick_count < ogBookstoreTick2 + booksBackCount + 10){
                Thread.sleep(1 * Bookstore.miliseconds_per_tick);
            }

            String synchObject = "synch";
            boolean isBreaked = false;
            synchronized (synchObject) {
                // every 150 ticks one assistant per group takes break.
                if (Bookstore.tick_count - Bookstore.assistant_break_ticks_last > Bookstore.assistant_break_ticks) {
                    // check if assistant is on break in any group
                    if (Bookstore.assistant_is_break == false) {
                        // reset list if every one has taken a break
                        if (Bookstore.rotate_assistant_break.size() == Bookstore.number_of_assistants) {
                            if (Bookstore.assistant_print_break_statements) {
                                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " all assistants have taken a break, reset rotation");
                                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " all assistants have taken a break, reset rotation");
                                Bookstore.buffer.newLine();
                            }
                            Bookstore.rotate_assistant_break.clear();
                        }
                        // check if its there turn for a break.
                        if (!Bookstore.rotate_assistant_break.contains(ThreadId) ) {
                            if (Bookstore.assistant_print_break_statements) {
                                Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant " + ThreadId + " is taking a break.");
                                Bookstore.buffer.newLine();
                                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant" + ThreadId + " is taking a break.");
                            }
                            Bookstore.assistant_break_count += 1;
                            Bookstore.assistant_is_break = true;
                            Bookstore.rotate_assistant_break.add(ThreadId);
                            Bookstore.assistant_break_ticks_last = Bookstore.assistant_break_ticks_last + Bookstore.assistant_break_ticks;
                            synchObject.notify();
                            // no assistant in group is on break, takes 150 tick break
                            isBreaked = true;
                        }
                    }
                }
            }
            // put any remaining books back
            for (String remaining_book : put_books_backTL.get().keySet()){
                if(Bookstore.assistant_bookshelve_capacity_statements == true){
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant " + ThreadId + " is putting back " + put_books_backTL.get().get(remaining_book) + " books of " + remaining_book  + " section as its bookshelve has reached capacity.");
                    Bookstore.buffer.newLine();
                    System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant " + ThreadId + " is putting back " + put_books_backTL.get().get(remaining_book) + " books of " + remaining_book  + " section as its bookshelve has reached capacity.");
                }
                Bookstore.book_box.put(remaining_book, Bookstore.book_box.get(remaining_book) + put_books_backTL.get().get(remaining_book));
            }
            int ogBookstoretick1 = Bookstore.tick_count;
            // current thread's/assistants turn to go on break
            if(isBreaked == true){
                // go on break
                int ogBookTickCount = Bookstore.tick_count;
                while(Bookstore.tick_count < ogBookTickCount + Bookstore.assistant_break_ticks){
                    Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                }
                if (Bookstore.assistant_print_break_statements) {
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant " + ThreadId + " has finished taking a break.");
                    Bookstore.buffer.newLine();
                    System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id + " assistant " + ThreadId + " has finished taking a break.");
                }
                Bookstore.assistant_break_count -= 1;
                Bookstore.assistant_is_break = false;
            }
            iy +=  1;
        }
    }
}


class AssistantThread extends Thread{
    @Override
    public void run(){
        try {
            // stock shelves function.
            Bookstore.overall_thread_count += 1;
            Bookstore.assistant_thread_id += 1;
            Assistant.stockShelves(Bookstore.assistant_thread_id, Bookstore.overall_thread_count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class Deliver{
    static int deliver_thread_id = 0;
    private static Random generator = new Random();

    public static void deliverBooks(int overall_thread_count) throws InterruptedException, IOException {
        deliver_thread_id += 1;
        // deliver books
        // Printing the generated random numbers
        int i = 0;
        // randomly assign the books
        while(i < 10){
            int min = 0; // Minimum value of range
            int max = 10 - i; // Maximum value of range
            // get random int for genre
            int randomInt = (int)Math.floor(Math.random() * (max - min + 1) + min);
            // get random genre
            Random rand = new Random();
            String randomGenre = Bookstore.book_genres.get(rand.nextInt(Bookstore.book_genres.size()));
            // notify new books to any customers waiting
            synchronized(Bookstore.my_monitor_customer_waiting.get(randomGenre)){
                Bookstore.my_monitor_customer_waiting.get(randomGenre).notify();
            }
            // put books into box
            int ii = 0;
            while (ii < randomInt){
                // check that there is enough money
                if((Bookstore.bookstore_money > Bookstore.genre_prices.get(randomGenre) && Bookstore.money_system == true) || Bookstore.money_system == false) {
                    Bookstore.book_box.put(randomGenre, Bookstore.book_box.get(randomGenre) + 1);
                    if(Bookstore.money_system == true){
                        Bookstore.bookstore_money -= Bookstore.genre_prices.get(randomGenre);
                        if(Bookstore.money_system_print_statements == true){
                            Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Bookstore bought " + randomGenre + " costing " + Bookstore.genre_prices.get(randomGenre) );
                            Bookstore.buffer.newLine();
                            System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Bookstore bought " + randomGenre + " costing " + Bookstore.genre_prices.get(randomGenre));
                        }
                    }
                }
                ii += 1;
            }
            i += randomInt;
        }
        if(Bookstore.delivery_print_statements) {
            Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Deposited a box of books" );
            Bookstore.buffer.newLine();
            System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_count + " Deposited a box of books");
        }
        for (int y = 0; y < Bookstore.number_of_assistants; y++) {
            synchronized(Bookstore.monitor_assistant_box_empty.get(y + 1)){
                Bookstore.monitor_assistant_box_empty.get(y + 1).notify();
            }
        }
    }
}

class Inflation extends Thread{
    @Override
    public void run(){
        Bookstore.overall_thread_count += 1;
        int overall_thread_id_tmp = Bookstore.overall_thread_count;
        while (true) {
            // sleep defined by inflation tick count in gui.
            try {
                int ogBookTickCount = Bookstore.tick_count;
                int ogBookTickCount2 = Bookstore.inflation_tick_count - (Bookstore.tick_count % Bookstore.inflation_tick_count);
                while(Bookstore.tick_count - ogBookTickCount < ogBookTickCount2){
                    Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // random inflation increase.
            Random ringed = new Random();
            // make sure more than 1 seconds to stop looping
            int random_tick = ringed.nextInt(Bookstore.inflation_tick_count - 1) + 1;
            try {
                Thread.sleep(random_tick * Bookstore.miliseconds_per_tick);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Random randm = new Random();
            int randomInflation = randm.nextInt(4 - 1) + 1;
            if(Bookstore.money_system_print_statements == true){
                try {
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Inflation has increased by " + randomInflation + " %");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Bookstore.buffer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Inflation has increased by " + randomInflation + " %");
            }
            // increase prices in relation to inflation.
            // also increase profit percentage in relation to inflation.
            for (String genre : Bookstore.genre_prices.keySet()){
                double newPrice = Bookstore.genre_prices.get(genre) + (Bookstore.genre_prices.get(genre) * ((double)randomInflation / 100));
                Bookstore.genre_prices.put(genre,newPrice);
                // set profit percentage to align with inflation
                double getPercentage = ( ((double) Bookstore.profit_percentage / 100) * ((double) randomInflation / 100)) * 100;
                Bookstore.profit_percentage = Bookstore.profit_percentage + getPercentage;
            }
        }
    }
}

class CloseBookstoreInterval extends Thread{
    @Override
    public void run(){
        int ogBookTickCount = 0;
        int ogBookTickCount2 = 0;
        Bookstore.overall_thread_count += 1;
        int overall_thread_id_tmp = Bookstore.overall_thread_count;
        while (true){
            Bookstore.customer_can_enter = true;
            Bookstore.can_deliver_books = true;
            Bookstore.assistant_can_stock = true;
            if(Bookstore.close_store_print_statements == true){
                try {
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Bookstore has opened");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Bookstore.buffer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Bookstore has opened");
            }
            try {
                ogBookTickCount = Bookstore.tick_count;
                ogBookTickCount2 = Bookstore.close_time_tick_count - (Bookstore.tick_count % Bookstore.close_time_tick_count);
                while(Bookstore.tick_count - ogBookTickCount < Bookstore.close_time_tick_count){
                    Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bookstore.customer_can_enter = false;
            Bookstore.can_deliver_books = false;
            Bookstore.assistant_can_stock = false;
            if(Bookstore.close_store_print_statements == true){
                try {
                    Bookstore.buffer.write("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Bookstore has closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Bookstore.buffer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Tick count " + Bookstore.tick_count + " Thread ID " + overall_thread_id_tmp + " Bookstore has closed");
            }
            try {
                ogBookTickCount = Bookstore.tick_count;
                ogBookTickCount2 = Bookstore.total_close_tick_count - (Bookstore.tick_count % Bookstore.total_close_tick_count);
                while(Bookstore.tick_count - ogBookTickCount < ogBookTickCount2){
                    Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DeliverBooks extends Thread{
    @Override
    public void run(){
        while (Bookstore.can_deliver_books) {
            if(Bookstore.delivery_started == true) {
                try {
                    int ogBookTickCount = Bookstore.tick_count;
                    int ogBookTickCount2 = 100 - (Bookstore.tick_count % 100);
                    while(Bookstore.tick_count - ogBookTickCount < ogBookTickCount2){
                        Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(Bookstore.delivery_started == false){
                Bookstore.delivery_started = true;
            }
            // open bookshop to customers
            // customer randomly buys every 1-10 ticks
            Random randgen = new Random();
            // get random delivery tick
            int random_tick = randgen.nextInt(100 - 1) + 1;
            // get random genre
            Bookstore.overall_thread_count += 1;
            try {
                Thread.sleep(random_tick * Bookstore.miliseconds_per_tick);
                Deliver.deliverBooks(Bookstore.overall_thread_count);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class TimerThread extends Thread{
    @Override
    public void run(){
        Bookstore.overall_thread_count += 1;
        // increase tick based on miliseconds per tick
        Timer timer = new Timer();
        final int[] days = {0};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Bookstore.tick_count += 1;
                if(Bookstore.tick_count % 1000 == 0){
                    days[0] += 1;
                }
                if(days[0] == Bookstore.number_of_days){
                    try {
                        Bookstore.buffer.close();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, Bookstore.miliseconds_per_tick);
    }
}

class CustomerEntry{
    public void CustomerEntry() throws InterruptedException {
        while (Bookstore.customer_can_enter) {
            try {
                int ogBookTickCount = Bookstore.tick_count;
                int ogBookTickCount2 = 10 - (Bookstore.tick_count % 10);
                while(Bookstore.tick_count - ogBookTickCount < ogBookTickCount2){
                    Thread.sleep(1 * Bookstore.miliseconds_per_tick);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // open bookshop to customers
            // customer randomly buys every 1-10 ticks
            Random randgen = new Random();
            // get random delivery tick
            int random_tick = randgen.nextInt(10 - 1) + 1;
            try {
                Thread.sleep(random_tick * Bookstore.miliseconds_per_tick );
                CustomerThread customer = new CustomerThread();
                customer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Bookstore{

    static List<String> book_genres_all = Arrays.asList("fiction","horror","romance", "fantasy", "poetry", "history","classic","philosophy","religion","comedy","young-adult","politics");

    static List<String> book_genres = new ArrayList<>();

    static Map<String, Integer> book_shelves = new HashMap<>();

    static Map<String, Integer> book_shelves_capacity = new HashMap<>();

    static int overall_thread_count = 0;

    // 1 tick = 100 miliseconds
    static int tick_count = 0;

    static Map<String, Integer> book_box = new HashMap<>();

    static Map<String, Integer> customer_waiting = new HashMap<>();

    static Map<String, String> my_monitor_customer_waiting = new HashMap<>();

    static Map<Integer, String> my_monitor_assistants = new HashMap<>();

    static List<Integer> AssistantsbookThreadIndex = new ArrayList<>();

    static Map<Integer, String> monitor_assistant_box_empty = new HashMap<>();

    static int buy_thread_id = 0;

    static int bookstore_assistant_notify = 1;

    static int assistant_thread_id = 0;

    static int assistant_break_ticks = 150;

    static int number_of_assistants = 6;

    static int miliseconds_per_tick = 100;

    static int number_of_days = 5;

    static int assistant_break_count = 0;

    static int number_of_genres = 0;

    static List<Integer> book_shelving_capacity = new ArrayList<>();

    static boolean customer_print_statements = false;

    static boolean assistant_print_statements_general = false;

    static boolean delivery_started = false;

    static boolean assistant_print_break_statements = false;

    static boolean assistant_bookshelve_capacity_statements = false;

    static boolean prioritize_stocking_statements = false;

    static boolean delivery_print_statements = false;

    static boolean assistant_is_break = false;

    static boolean customer_can_enter = true;

    static boolean assistant_can_stock = true;

    static boolean can_deliver_books = true;

    static ArrayList<Integer> rotate_assistant_break = new ArrayList<>();

    static int assistant_break_ticks_last = 0;

    static bookStoreWindow bkStW;

    static int bookstore_money = 100;

    static Map<String, Double> genre_prices = new HashMap<>();

    static double profit_percentage = 25;

    static boolean money_system = false;

    static int inflation_tick_count = 1000;

    static BufferedWriter buffer;

    static AssistantThread[] assistant_threads;

    static Inflation inflation;

    static DeliverBooks deliver_books;

    static boolean close_interval_on = false;

    static int close_time_tick_count = 800;

    static int total_close_tick_count = 200;

    static boolean money_system_print_statements = false;

    static boolean close_store_print_statements = false;

    public static void main(String args[] ){
        bkStW = new bookStoreWindow();
        JframeThread jframeT = new JframeThread();
        jframeT.start();
    }

    public static void startBookstore() throws Exception{


        // create output log.
        FileWriter writer = new FileWriter("output.dat");
        buffer = new BufferedWriter(writer);

        int y = 0;
        // get genres per genre count.
        while (y < number_of_genres){
            Random randG = new Random();
            String randomGenre = null;
            boolean book_genre_new = false;
            while (book_genre_new == false){
                randomGenre = Bookstore.book_genres_all.get(randG.nextInt(Bookstore.book_genres_all.size()));
                if(!book_genres.contains(randomGenre)){
                    book_genre_new = true;
                }
            }
            book_genres.add(randomGenre);
            y += 1;
        }


        // initialize hashmaps based on book genres size.
        for (int i = 0; i < book_genres.size(); i++) {
            Random randgen2 = new Random();
            // get random delivery tick
            int random_price = randgen2.nextInt(10 - 1) + 1;
            genre_prices.put(book_genres.get(i), (double) random_price);
            book_box.put(book_genres.get(i),0);
            book_shelves.put(book_genres.get(i),0);
            book_shelves_capacity.put(book_genres.get(i),book_shelving_capacity.get(i));
            customer_waiting.put(book_genres.get(i),0);
            my_monitor_customer_waiting.put(book_genres.get(i),"0");
        }

        // monitors for assistants.
        for (int i = 0; i < number_of_assistants; i++) {
            my_monitor_assistants.put(i + 1 , "random");
            monitor_assistant_box_empty.put(i + 1 , "random");
        }

        TimerThread timerThread = new TimerThread();
        timerThread.start();
        // start inflation thread.

        if(money_system == true){
            inflation = new Inflation();
            inflation.start();
        }

        // start deliverbooks thread.
        deliver_books = new DeliverBooks();
        deliver_books.start();
        if(close_interval_on == true) {
            CloseBookstoreInterval closeBookstoreInterval = new CloseBookstoreInterval();
            closeBookstoreInterval.start();
        }

        // start assistant thread based on the amount of assistants.
        assistant_threads = new AssistantThread[number_of_assistants];
        int i = 0;
        while (i < assistant_threads.length){
            assistant_threads[i] = new AssistantThread();
            assistant_threads[i].start();
            i += 1;
        }




        // create customer thread inside this class.
        CustomerEntry customerEntry = new CustomerEntry();
        customerEntry.CustomerEntry();

    }

}

// thread for jframe gui.
class JframeThread extends Thread{
    @Override
    public void run(){
        bookStoreWindow.mainStatement();
    }
}



class bookStoreWindow extends JPanel implements ActionListener
{

    static JFrame window_frame = new JFrame("Text Example");



    public static void main(String args[] ){
    }

    public static void setMiscSystem(){
        // gui for moneysystem
        window_frame.getContentPane().removeAll();
        window_frame.repaint();
        JCheckBox checkBoxMoneySystem = new JCheckBox("Money System on", true);
        checkBoxMoneySystem.setBounds(10,20, 220,50);
        window_frame.add(checkBoxMoneySystem);
        JLabel MiscLabels;
        MiscLabels=new JLabel("Set Inflation TickCount : ");
        MiscLabels.setBounds(10,60, 220,30);
        window_frame.add(MiscLabels);
        JTextField inflationTickCountTextField = new JTextField("1000");
        inflationTickCountTextField.setBounds(165,60, 200,30);
        window_frame.add(inflationTickCountTextField);
        MiscLabels=new JLabel("Set Profit Percentage: ");
        MiscLabels.setBounds(10,90, 220,30);
        window_frame.add(MiscLabels);
        JTextField profitPercentageTextField = new JTextField("25");
        profitPercentageTextField.setBounds(150,90, 200,30);
        window_frame.add(profitPercentageTextField);
        MiscLabels=new JLabel("Bookstore Money : ");
        MiscLabels.setBounds(10,120, 220,30);
        window_frame.add(MiscLabels);
        JTextField bookStoreMoneyTextField = new JTextField("500");
        bookStoreMoneyTextField.setBounds(125,120, 200,30);
        window_frame.add(bookStoreMoneyTextField);
        JCheckBox closeStoreCheckBox = new JCheckBox("Close store system", true);
        closeStoreCheckBox.setBounds(10,150, 220,50);
        window_frame.add(closeStoreCheckBox);
        MiscLabels=new JLabel("Close store tick count : ");
        MiscLabels.setBounds(10, 190,200,30);
        window_frame.add(MiscLabels);
        JTextField closeStoreTextField = new JTextField("900");
        closeStoreTextField.setBounds(165,190, 200,30);
        window_frame.add(closeStoreTextField);
        MiscLabels =new JLabel("total ticks store closed : ");
        MiscLabels.setBounds(10,220, 220,30);
        window_frame.add(MiscLabels);
        JTextField totalTicksStoreClosed = new JTextField("100");
        totalTicksStoreClosed.setBounds(165,220, 200,30);
        window_frame.add(totalTicksStoreClosed);
        JLabel labelError=new JLabel("");
        labelError.setBounds(10,540, 300,30);
        labelError.setForeground(Color.RED);
        window_frame.add(labelError);
        JCheckBox checkBoxCloseStorePrintCheckBox = new JCheckBox("Close store print statements", true);
        checkBoxCloseStorePrintCheckBox.setBounds(10,250, 250,50);
        window_frame.add(checkBoxCloseStorePrintCheckBox);
        JCheckBox moneySystemPrintCheckBox = new JCheckBox("Money system print statements", true);
        moneySystemPrintCheckBox.setBounds(10,280, 250,50);
        window_frame.add(moneySystemPrintCheckBox);
        JButton buttonMisc =new JButton("Set misc. systems");
        buttonMisc.setBounds(25,320, 200,35);
        buttonMisc.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(Integer.valueOf(bookStoreMoneyTextField.getText()) > 0 && Integer.valueOf(profitPercentageTextField.getText()) > 0 && Integer.valueOf(inflationTickCountTextField.getText()) > 0 && Integer.valueOf(closeStoreTextField.getText()) > 0 && Integer.valueOf(totalTicksStoreClosed.getText()) > 0 ){
                    Bookstore.money_system = checkBoxMoneySystem.isSelected();
                    Bookstore.bookstore_money = Integer.valueOf(bookStoreMoneyTextField.getText());
                    Bookstore.profit_percentage = Integer.valueOf(profitPercentageTextField.getText());
                    Bookstore.inflation_tick_count = Integer.valueOf(inflationTickCountTextField.getText());
                    Bookstore.close_interval_on = closeStoreCheckBox.isSelected();
                    Bookstore.close_time_tick_count = Integer.valueOf(closeStoreTextField.getText());
                    Bookstore.total_close_tick_count = Integer.valueOf(totalTicksStoreClosed.getText());
                    Bookstore.close_store_print_statements = checkBoxCloseStorePrintCheckBox.isSelected();
                    Bookstore.money_system_print_statements = moneySystemPrintCheckBox.isSelected();
                    labelError.setText("Successfully applied!");
                } else{
                    labelError.setText("Cant choose a zero value!");
                }
            }
        });
        window_frame.add(buttonMisc);
        JButton goBack =new JButton("Go back");
        goBack.setBounds(225,320,100,35);
        goBack.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                mainStatement();
            }
        });
        window_frame.add(goBack);
    }

    public static void setPrintStatements(){
        //gui for print statements.
        window_frame.getContentPane().removeAll();
        window_frame.repaint();
        JCheckBox checkBoxCustomerPrint = new JCheckBox("Customer Print Statements:", true);
        checkBoxCustomerPrint.setBounds(10,20, 220,50);
        window_frame.add(checkBoxCustomerPrint);
        JCheckBox checkBoxAssistantGeneral = new JCheckBox("Assistant General Print Statements:", true);
        checkBoxAssistantGeneral.setBounds(10,60, 280,50);
        window_frame.add(checkBoxAssistantGeneral);
        JCheckBox checkBoxDelivery = new JCheckBox("Delivery Print Statements:", true);
        checkBoxDelivery.setBounds(10,100, 280,50);
        window_frame.add(checkBoxDelivery);
        JCheckBox checkBoxPrioritizeStocking = new JCheckBox("Prioritize Stocking Print Statements:", false);
        checkBoxPrioritizeStocking.setBounds(10,140, 280,50);
        window_frame.add(checkBoxPrioritizeStocking);
        JCheckBox checkBoxBookshelveCapacity = new JCheckBox("Bookshelve Capacity Print Statements:", false);
        checkBoxBookshelveCapacity.setBounds(10,180, 280,50);
        window_frame.add(checkBoxBookshelveCapacity);
        JCheckBox checkBoxAssistantBreak = new JCheckBox("Assistant Break Print Statements:", false);
        checkBoxAssistantBreak.setBounds(10,220, 280,50);
        window_frame.add(checkBoxAssistantBreak);
        JButton JButtonPrintStatements=new JButton("Set Print Statements");
        JButtonPrintStatements.setBounds(10,270,150,35);
        window_frame.add(JButtonPrintStatements);
        JButtonPrintStatements.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // assign bookstore values.
                Bookstore.assistant_print_statements_general = checkBoxAssistantGeneral.isSelected();
                Bookstore.assistant_print_break_statements = checkBoxAssistantBreak.isSelected();
                Bookstore.customer_print_statements = checkBoxCustomerPrint.isSelected();
                Bookstore.delivery_print_statements = checkBoxDelivery.isSelected();
                Bookstore.prioritize_stocking_statements = checkBoxPrioritizeStocking.isSelected();
                Bookstore.assistant_bookshelve_capacity_statements = checkBoxBookshelveCapacity.isSelected();
                JLabel labelError = new JLabel("Successfully applied!");
                labelError.setBounds(10,330, 300,30);
                labelError.setForeground(Color.RED);
                window_frame.add(labelError);
                Graphics paper = window_frame.getGraphics();
                window_frame.update(paper);
            }
        });
        JButton buttonGoBack =new JButton("Go back");
        buttonGoBack.setBounds(170,270,100,35);
        buttonGoBack.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                mainStatement();
            }
        });
        window_frame.add(buttonGoBack);
        Graphics paper = window_frame.getGraphics();
        window_frame.update(paper);
    }

    public static void mainStatement()
    {
        // gui for bookstore.
        window_frame.getContentPane().removeAll();
        window_frame.repaint();
        JLabel labelError;
        JLabel mainLabels;
        JTextField[] shelveLimit = new JTextField[13];
        mainLabels=new JLabel("Number of Assistants : ");
        mainLabels.setBounds(10,20, 150,30);
        window_frame.add(mainLabels);
        JTextField assistantsTextField=new JTextField("2");
        assistantsTextField.setBounds(160,20, 200,30);
        window_frame.add(assistantsTextField);
        mainLabels=new JLabel("Number of book sections(1-11) : ");
        mainLabels.setBounds(10,50, 220,30);
        window_frame.add(mainLabels);
        JTextField bookSectionsTextField=new JTextField("6");
        bookSectionsTextField.setBounds(220,50, 200,30);
        window_frame.add(bookSectionsTextField);
        mainLabels=new JLabel("Miliseconds per tick: ");
        mainLabels.setBounds(10,80, 220,30);
        window_frame.add(mainLabels);
        JTextField milisecondsPerTickTextField = new JTextField("1000");
        milisecondsPerTickTextField.setBounds(145,80, 200,30);
        window_frame.add(milisecondsPerTickTextField);
        mainLabels=new JLabel("Assistant break per tick : ");
        mainLabels.setBounds(10,110, 220,30);
        window_frame.add(mainLabels);
        JTextField assistantBreaksTextField = new JTextField("150");
        assistantBreaksTextField.setBounds(170,110, 200,30);
        window_frame.add(assistantBreaksTextField);
        mainLabels=new JLabel("Number of days program is run : ");
        mainLabels.setBounds(10,140, 220,30);
        window_frame.add(mainLabels);
        JTextField daysProgramTextField = new JTextField("2");
        daysProgramTextField.setBounds(220,140, 200,30);
        window_frame.add(daysProgramTextField);
        int i = 0;
        int ycord = 170;
        while(i < 11){
            JLabel limitLabels = new JLabel("Book Section " + String.valueOf(i + 1) + " shelve limit : ");
            limitLabels.setBounds(10,ycord, 220,30);
            window_frame.add(limitLabels);
            shelveLimit[i] = new JTextField("20");
            if(i < 9){
                shelveLimit[i].setBounds(190,ycord, 200,30);
            } else{
                shelveLimit[i].setBounds(197,ycord, 200,30);
            }
            window_frame.add(shelveLimit[i]);
            ycord += 30;
            i += 1;
        }
        labelError=new JLabel("");
        labelError.setBounds(10,540, 300,30);
        labelError.setForeground(Color.RED);
        window_frame.add(labelError);
        JButton startBookstore=new JButton("Start bookstore");
        startBookstore.setBounds(15,505,150,35);
        JTextField finalL = assistantsTextField;
        JTextField finalL2 = assistantBreaksTextField;
        JTextField finalL3 = milisecondsPerTickTextField;
        JTextField finalL4 = bookSectionsTextField;
        JLabel finalErrr = labelError;
        startBookstore.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // assign bookstore values.
                Bookstore newBookstore = new Bookstore();
                int y = 0;
                boolean shelvelimitzero = false;
                while(y < 11){
                    Bookstore.book_shelving_capacity.add(Integer.valueOf(shelveLimit[y].getText()));
                    if(Integer.valueOf(shelveLimit[y].getText()) == 0){
                        shelvelimitzero = true;
                    }
                    y += 1;
                }
                newBookstore.number_of_assistants = Integer.valueOf(finalL.getText());
                Bookstore.assistant_break_ticks = Integer.valueOf(finalL2.getText());
                Bookstore.miliseconds_per_tick = Integer.valueOf(finalL3.getText());
                Bookstore.number_of_genres = Integer.valueOf(finalL4.getText());
                Bookstore.number_of_days = Integer.valueOf(daysProgramTextField.getText());
                try {
                    if(shelvelimitzero == false && Integer.valueOf(finalL4.getText()) <= 12 && Integer.valueOf(finalL.getText()) > 0 && Integer.valueOf(finalL2.getText()) > 0 && Integer.valueOf(finalL3.getText()) > 0 && Integer.valueOf(finalL4.getText()) > 0 && Integer.valueOf(daysProgramTextField.getText()) > 0 ){
                        Graphics paper = window_frame.getGraphics();
                        paper.clearRect(0, 0, (int)window_frame.getSize().getWidth(), (int)window_frame.getSize().getHeight());
                        window_frame.getContentPane().removeAll();
                        window_frame.repaint();
                        //f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                        newBookstore.startBookstore();
                        finalErrr.setText("");
                    } else{
                        if(Integer.valueOf(finalL4.getText()) <= 12){
                            finalErrr.setText("Only allowed 1-12 book sections!");
                        }
                        if(shelvelimitzero == true || Integer.valueOf(finalL.getText()) == 0 || Integer.valueOf(finalL2.getText()) == 0 || Integer.valueOf(finalL3.getText()) == 0 || Integer.valueOf(finalL4.getText()) == 0 || Integer.valueOf(daysProgramTextField.getText()) == 0){
                            finalErrr.setText("Cant choose a zero value!");
                        }
                    }
                }
                catch (Exception ef) {
                }
            }
        });
        window_frame.add(startBookstore);
        JButton printStatementsButton=new JButton("Set print statements");
        printStatementsButton.setBounds(185,505,180,35);
        printStatementsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setPrintStatements();
            }
        });
        window_frame.add(printStatementsButton);

        JButton miscButtons =new JButton("Set misc. systems");
        miscButtons.setBounds(390,505,180,35);
        miscButtons.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setMiscSystem();
            }
        });
        window_frame.add(miscButtons);
        window_frame.setSize(600,600);
        window_frame.setLayout(null);
        window_frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {

    }
}

